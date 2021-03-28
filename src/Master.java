import workerstate.WorkerInfo;
import workerstate.WorkerState;
import workerstate.WorkerType;

import java.io.*;
import java.net.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Server class
class Master {

    private static List<WorkerInfo> sWorkers = Collections.synchronizedList(new ArrayList<>());

    private static boolean isError = false;

    public static void main(String[] args) {
        ServerSocket server = null;
        String[] inputs = args[0].split(",");
        String func = args[1];
        System.out.println("Master running");
        try {

            // server is listening on port 1234
            server = new ServerSocket(1235);
            server.setReuseAddress(true);

            // client request
            int counter = 1;
            for (String inp : inputs) {
                String[] startOptions = new String[]{"java", "-cp", ".", "Worker", String.valueOf(counter++), inp, func};

                ProcessBuilder pb = new ProcessBuilder(startOptions);
                pb.redirectErrorStream(true);
                Process p = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;

                WorkerInfo info = new WorkerInfoBuilder().setWorkerProcess(p)
                        .setWorkerState(WorkerState.RUNNING)
                        .setWorkerType(WorkerType.MAPPER)
                        .setWorkerId(counter)
                        .build();

                sWorkers.add(info);
                System.out.println("Number of processes : " + sWorkers.size());

                while ((line = reader.readLine()) != null) {
                    //TODO : Error has happened. For Milestone 1, ignoring the error and simply updating a state. Fault tolerance to be implemented for upcoming milestones.
                    if(line.contains("filename")) {
                        System.out.println("Master received the following file path from worker: ");
                    }
                    System.out.println(line);
                    isError = true;
                }
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
            ScheduledExecutorService scheduler
                    = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(new PeriodicTask(),10, 1000, TimeUnit.MILLISECONDS);


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

    private static class PeriodicTask implements Runnable {
        public PeriodicTask(){}
        @Override
        public void run() {
            boolean isDone = true;
            synchronized (sWorkers) {
                for(WorkerInfo i : sWorkers) {
                    if(i.getState() != WorkerState.DONE){
                        isDone = false;
                        break;
                    }
                }
            }
            if(isDone){
                //TODO : Once reducers are implemented, take care of launching them from here
                System.exit(0);
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
                            WorkerInfo info = sWorkers.get(id-1);
                            info.setState(status);

                            System.out.println("Status update received for Worker : " + id + " " + status.toString());
                        }
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
