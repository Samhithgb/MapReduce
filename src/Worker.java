import workerstate.WorkerState;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;

public class Worker {

    static String workerId;

    private static String STATUS_UPDATE_FORMAT = "STATUS %s : %s ";
    // driver code
   public static void main(String[] args) throws InterruptedException {
        // establish a connection by providing host and port
        // number
        Thread.sleep(1000);  // removing this might break things
        try (Socket socket = new Socket("localhost", Integer.parseInt(args[5]))) {
            // writing to server

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);

            workerId = args[0];
            sendState(WorkerState.RUNNING,out);

            if(Integer.parseInt(workerId) == 1){
                while (true){}
            }

            try {
                String file_path = args[1];
//                Thread.sleep(5000); // uncomment to test if processes run in parallel
                MapReduceFunction<String, String> func;

                func = functionFromString(args[2]);
                String res = func.apply(file_path);
                // String res = Worker.apply(file_path);

                @SuppressWarnings("unchecked")
                HashMap<String, String> configMap = (HashMap<String, String>) deserialize(args[3]);


                @SuppressWarnings("unchecked")
                HashMap<String, String> resultFromMapper = (HashMap<String, String>) deserialize(res);

                if(resultFromMapper!=null || resultFromMapper.isEmpty()){
                    System.out.println("Empty result received");
                }
                boolean isMapper = args[4].equalsIgnoreCase("M");

                if(isMapper) {
                    System.out.println("Mapper. Generating intermediate files");
                    String filePaths = AssignIntermediateFilesAndReturnFileLocations(resultFromMapper, configMap);
                } else {
                    System.out.println("Reducer. Outputting");
                    generateOutPutFile(resultFromMapper);
                }
                // out.println("filePaths returned from worker: "+ filePaths);
                out.flush();
                sendState(WorkerState.DONE, out);
            } catch (Exception e) {
                  e.printStackTrace();
                sendState(WorkerState.ERROR, out);
            }
          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        static void generateOutPutFile(HashMap<String, String> resultFromReducer) throws IOException {
            String fileName = "reducer_id_"+ workerId + "_output_.txt";
            File outPutFile = new File(fileName);
            if(resultFromReducer.isEmpty()){
                System.out.println("No output generated. Will result in an empty file");
            }
            try (FileWriter writer = new FileWriter(outPutFile)) {
                for (String i : resultFromReducer.keySet()) {
                    writer.write(i + "=" + resultFromReducer.get(i) + '\n');
                }
            }
        }

        static void sendState(WorkerState state, PrintWriter out){
            out.println(String.format(STATUS_UPDATE_FORMAT,workerId,state.toString()));
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

    private static String AssignIntermediateFilesAndReturnFileLocations(HashMap<String, String> resultFromMapper, HashMap<String, String> configMap) throws IOException {
        int num_of_reducers = Integer.parseInt(configMap.get("num_of_reducers").trim());

        File[] fileObjArray = new File[num_of_reducers];
        FileWriter[] myWriterArray = new FileWriter[num_of_reducers];
        String[] fileNames = new String[num_of_reducers];

        for(int i=0;i<num_of_reducers;i++) {
            fileNames[i] = "worker_id_"+ workerId + "_filename_" + (i+1) + ".txt";
            fileObjArray[i] = new File(fileNames[i]);
        }

        for(int i=0;i<num_of_reducers;i++) {
            myWriterArray[i] = new FileWriter(fileNames[i]);
        }

        for (String i : resultFromMapper.keySet()) {
            int index = (i.hashCode() % num_of_reducers);
            if(index<0){
                index = index + num_of_reducers;
            }
            myWriterArray[index].write(i + "=" +  resultFromMapper.get(i) + '\n');
        }

        for(int i=0;i<num_of_reducers;i++) {
            myWriterArray[i].close();
        }

        return fileNames.toString();
    }

    /**
     * Read the object from Base64 string.
     */
    @SuppressWarnings("unchecked")
    private static MapReduceFunction<String, String> functionFromString(String s) throws IOException, ClassNotFoundException, ClassCastException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        MapReduceFunction<String, String> o;
        o = (MapReduceFunction<String, String>) ois.readObject();
        ois.close();
        return o;
    }
}