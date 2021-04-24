import workerstate.WorkerInfo;
import workerstate.WorkerState;
import workerstate.WorkerType;

import java.io.*;
import java.net.*;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Server class
class Master {

    private static final List<WorkerInfo> sWorkers = Collections.synchronizedList(new ArrayList<>());
    private static final ScheduledExecutorService scheduler
            = Executors.newScheduledThreadPool(1);

    private static boolean isError = false;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket server = null;
        String[] inputs = args[0].split(",");
        String mapfunc = args[1];
        @SuppressWarnings("unchecked")
        HashMap<String, String> configMap = (HashMap<String, String>) deserialize(args[2]);
        String reduceFunction = args[3];

        try {

            // server is listening on port 1234
            server = new ServerSocket(1235);
            server.setReuseAddress(true);

            // client request
            int counter = 1;
            int num_of_workers = Integer.parseInt(configMap.get("num_of_workers").trim());
            System.out.println("Number of workers according to config file = "+num_of_workers);

            for (String inp : inputs) {
                String[] startOptions = new String[]{"java", "-cp", System.getProperty("user.dir") + File.separator + "out" + File.separator + "production" + File.separator+ "project_folder", "Worker", String.valueOf(counter++), inp, mapfunc, toString((Serializable) configMap), "M"};
                // inheritIO redirects all child process streams to this process
                ProcessBuilder pb = new ProcessBuilder(startOptions).inheritIO();
                Process p = pb.start();
                System.out.println("This is a multi-process env and process info is "+ p);
                WorkerInfo info = new WorkerInfoBuilder().setWorkerProcess(p)
                        .setWorkerState(WorkerState.RUNNING)
                        .setWorkerType(WorkerType.MAPPER)
                        .setWorkerId(counter)
                        .build();

                sWorkers.add(info);
                System.out.println("Number of processes : " + sWorkers.size());

                isError = true;

                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server
                System.out.println("New client connected "
                        + client.getInetAddress()
                        .getHostAddress());

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client);

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
            //Implement periodic checks for worker states.

            scheduler.scheduleAtFixedRate(new PeriodicTask(() -> {
                launchReducers(configMap, reduceFunction);
            }),10, 1000, TimeUnit.MILLISECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean areReducersLaunched(){
        if(!sWorkers.isEmpty()){
            WorkerInfo info = sWorkers.get(0);
            return info.getType() == WorkerType.REDUCER;
        }
        return false;
    }

    private static void launchReducers(HashMap<String, String> configMap, String reduceFunction){
        if(areReducersLaunched()) {
            System.out.println("Reducers already launched. Skipping step.");
            return;
        }

        System.out.println("------------------------------Launching reducers now -------------------------------");
        sWorkers.clear();
        int counter2 = 1;
        ServerSocket server2;

        //Get all intermediate files
        File dir = new File("./");
        File[] foundFiles = dir.listFiles((dir1, name) -> name.contains("worker_id"));

        int number_of_reducers = Integer.parseInt(configMap.get("num_of_reducers").trim());
        System.out.println("Number of reducers : " + number_of_reducers);
        try {
            server2 = new ServerSocket(1235);
            server2.setReuseAddress(true);

            for (int i = 1; i < number_of_reducers + 1; i++) {
                String[] startOptions = new String[0];
                StringBuilder input_file_pattern = new StringBuilder();


                for (File file : foundFiles) {
                    String filename = "_filename_%d";
                    if (file.getName().contains(String.format(filename, i))) {
                        input_file_pattern.append("./").append(file.getName()).append(",");
                    }
                }
                try {
                    startOptions = new String[]{"java", "-cp", System.getProperty("user.dir") + File.separator + "out" + File.separator + "production" + File.separator + "project_folder", "Worker", String.valueOf(counter2++), input_file_pattern.toString(), reduceFunction, toString((Serializable) configMap), "R"};
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
                            .setWorkerId(counter2)
                            .build();

                    sWorkers.add(info);
                    System.out.println("Number of reducer processes : " + sWorkers.size());
                    isError = true;

                    // socket object to receive incoming client
                    // requests
                    Socket client = server2.accept();

                    // Displaying that new client is connected
                    // to server
                    System.out.println("New client connected "
                            + client.getInetAddress()
                            .getHostAddress());

                    // create a new thread object
                    ClientHandler clientSock
                            = new ClientHandler(client);

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
                for(WorkerInfo i : sWorkers) {
                    if(i.getState() != WorkerState.DONE){
                        isDone = false;
                    }
                    if(i.getState() != WorkerState.ERROR){
                        isError = false;
                    }
                }
            }

            if(isDone){
                if(areReducersLaunched()){
                    //We are done.
                    System.out.println("All done. Shutting down master");
                    scheduler.shutdown();
                }
                callback.onDone();
            }
            else if(isError){
                // exit status 1 if error occurs in worker
                System.out.println("Error while running UDF");
                System.exit(1);
            }

        }
    }


    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try {

                // get the outputstream of client
                out = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                // get the inputstream of client
                in = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                        if(line.contains("STATUS")) {
                            //Status update received. Process.
                            String[] two = line.split(":");
                            WorkerState status = WorkerState.valueOf(two[1].trim());
                            int id = Integer.parseInt(two[0].split(" ")[1]);
                            WorkerInfo info = sWorkers.get(id - 1);
                            info.setState(status);
                            System.out.println("Status update received for Worker : " + id + " " + status.toString());

                        }
                        isError = true;
                }
            } catch (IOException e) {
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
