import workerstate.WorkerInfo;
import workerstate.WorkerState;
import workerstate.WorkerType;

import java.io.*;
import java.net.*;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Server class
class Master {

    private static final Map<Integer,WorkerInfo> sWorkers = Collections.synchronizedMap(new HashMap<Integer, WorkerInfo>());
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static long sWorkerThreshold = 10;
    private static String[] argumentsList = null;
    private static HashMap<String, String> sConfigMap = new HashMap<>();
    private static int sRelaunchTimes = 1;
    private static ServerSocket mapperSocket = null;
    private static ServerSocket reducerSocket = null;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        argumentsList = args;
        sConfigMap = (HashMap<String, String>) deserialize(args[2]);
        sRelaunchTimes = Integer.parseInt(sConfigMap.get("relaunch_times").trim());
        launchMappers(-1);
    }

    private static void launchMappers(int mapperId) throws IOException, ClassNotFoundException {

        String[] args = argumentsList;
        ServerSocket server = null;
        String[] inputs = args[0].split(",");
        String mapfunc = args[1];
        String reduceFunction = args[3];

        try {
            if(mapperSocket!=null){
                mapperSocket.close();
            }

            // server is listening on port 1234
            mapperSocket = new ServerSocket(0);
            mapperSocket.setReuseAddress(true);


            // client request
            int counter = 1;
            int num_of_workers = Integer.parseInt(sConfigMap.get("num_of_workers").trim());
            sWorkerThreshold = Integer.parseInt(sConfigMap.get("worker_threshold").trim());
            System.out.println("[MASTER] : Number of workers according to config file = "+num_of_workers);

            if(mapperId != -1){
                System.out.println("[MASTER] : Relaunching mapper : " + mapperId);

                if(mapperId > (inputs.length)){
                    System.out.println("[MASTER] : No work for the mapper. Not relaunching");
                    return;
                }

                try {
                    inputs = new String[]{inputs[mapperId - 1]};
                    counter = mapperId;
                } catch (Exception e){
                    e.printStackTrace();
                }
            }



            for (String inp : inputs) {

                String[] startOptions = new String[]{"java", "-cp", System.getProperty("user.dir") + File.separator + "out" + File.separator + "production" + File.separator+ "project_folder", "Worker", String.valueOf(counter), inp, mapfunc, toString((Serializable) sConfigMap), "M", String.valueOf(mapperSocket.getLocalPort())};
                // inheritIO redirects all child process streams to this process
                ProcessBuilder pb = new ProcessBuilder(startOptions).inheritIO();
                Process p = pb.start();
                System.out.println("[MASTER] : This is a multi-process env and process info is "+ p);
                WorkerInfo info = new WorkerInfoBuilder().setWorkerProcess(p)
                        .setWorkerState(WorkerState.RUNNING)
                        .setWorkerType(WorkerType.MAPPER)
                        .setWorkerId(counter)
                        .setWorkerStartTime(LocalTime.now())
                        .build();


                sWorkers.put(info.getWorkerId(),info);

                System.out.println("[MASTER] : Number of processes : " + sWorkers.size());
                // socket object to receive incoming client
                // requests

                Socket client = mapperSocket.accept();
                // Displaying that new client is connected
                // to server
                System.out.println("[MASTER] : New client connected "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client, info.getWorkerId());

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
                counter++;
            }
            //Implement periodic checks for worker states.

            scheduler.scheduleAtFixedRate(new PeriodicTask(() -> {
                launchReducers(sConfigMap, reduceFunction, -1);
            }),10, 1000, TimeUnit.MILLISECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mapperSocket != null) {
                try {
                    mapperSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean areReducersLaunched(){
        if(!sWorkers.isEmpty()){
            WorkerInfo info = sWorkers.get(1);
            return info.getType() == WorkerType.REDUCER;
        }
        return false;
    }

    private static void launchReducers(HashMap<String, String> configMap, String reduceFunction, int reducerId){
        if(areReducersLaunched() && reducerId == -1) {
            System.out.println("[MASTER] : Reducers already launched. Skipping step.");
            return;
        }

        System.out.println("[MASTER] : ------------------------------Launching reducer/s now -------------------------------");
        sWorkers.clear();

        //Get all intermediate files
        File dir = new File("./");
        File[] foundFiles = dir.listFiles((dir1, name) -> name.contains("worker_id"));

        int number_of_reducers = Integer.parseInt(configMap.get("num_of_reducers").trim());
        System.out.println("[MASTER] : Number of reducers : " + number_of_reducers);

        int startingId = 1;

        if(reducerId != -1) {
          System.out.println("[MASTER] : Relaunching reducer : " + reducerId);
          startingId = reducerId;
          number_of_reducers = startingId + 1;
        }

        try {

            if(reducerSocket!=null){
                reducerSocket.close();
            }

            reducerSocket = new ServerSocket(0);
            reducerSocket.setReuseAddress(true);

            for (int i = startingId; i < number_of_reducers + 1; i++) {
                String[] startOptions = new String[0];
                StringBuilder input_file_pattern = new StringBuilder();

                for (File file : foundFiles) {
                    String filename = "_filename_%d";
                    if (file.getName().contains(String.format(filename, i))) {
                        input_file_pattern.append("./").append(file.getName()).append(",");
                    }
                }
                try {
                    startOptions = new String[]{"java", "-cp", System.getProperty("user.dir") + File.separator + "out" + File.separator + "production" + File.separator + "project_folder", "Worker", String.valueOf(i), input_file_pattern.toString(), reduceFunction, toString((Serializable) configMap), "R", String.valueOf(reducerSocket.getLocalPort())};
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // inheritIO redirects all child process streams to this process
                ProcessBuilder pb = new ProcessBuilder(startOptions).inheritIO();
                Process p;
                try {

                    p = pb.start();
                    WorkerInfo info = new WorkerInfoBuilder().setWorkerProcess(p)
                            .setWorkerState(WorkerState.RUNNING)
                            .setWorkerType(WorkerType.REDUCER)
                            .setWorkerId(i)
                            .setWorkerStartTime(LocalTime.now())
                            .build();

                    sWorkers.put(info.getWorkerId(),info);
                    System.out.println("[MASTER] : Number of reducer processes : " + sWorkers.size());

                    // socket object to receive incoming client
                    // requests
                    Socket client = reducerSocket.accept();

                    // Displaying that new client is connected
                    // to server
                    System.out.println("[MASTER] : New client connected "
                            + client.getInetAddress()
                            .getHostAddress());

                    // create a new thread object
                    ClientHandler clientSock
                            = new ClientHandler(client, info.getWorkerId());

                    // This thread will handle the client
                    // separately
                    new Thread(clientSock).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object deserialize(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private static class PeriodicTask implements Runnable {
        PeriodicClassCallback callback;
        public PeriodicTask(PeriodicClassCallback callback){
            this.callback = callback;
        }

        @Override
        public void run() {
            boolean isDone = true;
            boolean isError = true;

            synchronized (sWorkers) {
                for(WorkerInfo i : sWorkers.values()) {
                    if(i.getState() != WorkerState.DONE){
                        isDone = false;
                        System.out.println("[MASTER] : Number of workers " + sWorkers.size());
                        long secondsRunning = ((LocalTime.now().toNanoOfDay() - i.getStartTime().toNanoOfDay())/1000000000);
                        System.out.println("[MASTER] : HEARTBEAT : " +i.getType().toString() + " " + i.getWorkerId() +  " has been running for " + secondsRunning+ " seconds");
                        if(secondsRunning > sWorkerThreshold) {
                            relaunchWorker(i);
                        }
                    }
                    if(i.getState() != WorkerState.ERROR){
                        isError = false;
                    }
                }
            }

            if(isDone){
                if(areReducersLaunched()){
                    //We are done.
                    System.out.println("[MASTER] : All done. Shutting down master");
                    scheduler.shutdown();
                    return;
                }
                callback.onDone();
            }
            else if(isError){
                // exit status 1 if error occurs in worker
                System.out.println("[MASTER] : Error while running UDF");
                System.exit(1);
            }

        }
    }

    private static void relaunchWorker(WorkerInfo i) {
        System.out.println("Relaunching : " + i.getType() + " with id " + i.getWorkerId());
        Process p = i.getWorkerProcess();
        p.destroyForcibly();

        if(sRelaunchTimes == 0){
            System.out.println("[MASTER] : Reached limit for number of re-launches. Moving on after the kill");
            System.exit(1);
        }
        sRelaunchTimes--;

        try {
            p.waitFor();
            if(i.getType() == WorkerType.MAPPER){
                try {
                    // This file tells whether fault tolerance is being tested
                    String fileName = "run_second.txt";
                    File outPutFile = new File(fileName);
                    try (FileWriter writer = new FileWriter(outPutFile)) {
                        writer.write("1");
                        System.out.println("[MASTER] : (Disabling fault tolerance forced failure for restarted worker)");

                    }
//                    for (String s : argumentsList){
//                        System.out.println("MAP--->" + s);
//                    }
                    launchMappers(i.getWorkerId());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                for (String s : argumentsList){
                    System.out.println("[MASTER] : ----->" + s);
                }
                launchReducers(sConfigMap,argumentsList[3],i.getWorkerId());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private int workerId;
        // Constructor
        public ClientHandler(Socket socket, int workerId) {
            this.clientSocket = socket;
            this.workerId = workerId;
        }

        public void run() {
            System.out.println("[MASTER] : Starting client handler for worker id : " + workerId);

            PrintWriter out = null;
            BufferedReader in = null;
            try {
                // get the outputstream of client
                if(clientSocket.isClosed()){
                    System.out.println("[MASTER] : Socket closed");
                    return;
                }

                out = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                // get the inputstream of client
                in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                String line;

                if((mapperSocket!=null && mapperSocket.isClosed()) && (reducerSocket!=null &&reducerSocket.isClosed())){
                    System.out.println("[MASTER] : Closing the socket");
                    clientSocket.close();
                }

                while (!clientSocket.isClosed() && (line = in.readLine()) != null) {
                        if(line.contains("STATUS")) {
                            //Status update received. Process.
                            String[] two = line.split(":");
                            WorkerState status = WorkerState.valueOf(two[1].trim());
                            int id = Integer.parseInt(two[0].split(" ")[1]);
                            WorkerInfo info = sWorkers.get(id);
                            info.setState(status);
                            System.out.println("[MASTER] : Status update received for Worker : " + id + " " + status.toString());
                        }
                }
            } catch (IOException e) {
                System.out.println("[MASTER] : Exception");
  /*              ClientHandler clientSock
                        = new ClientHandler(clientSocket, workerId);
                new Thread(clientSock).start();*/
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
