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
            // reading from server, optional - could be used to receive commands from master
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

                        workerId = args[0];
            sendState(WorkerState.RUNNING,out);

            try {
                String file_path = args[1];
//                Thread.sleep(5000); // uncomment to test if processes run in parallel
                MapReduceFunction<String, String> func;

                func = functionFromString(args[2]);

                // System.out.println(" ss1: worker culprit filepath: " + file_path);
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
//                sendState(WorkerState.ERROR, e, out);
                sendState(WorkerState.ERROR, out);
            }
          
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


        static void generateOutPutFile(HashMap<String, String> resultFromReducer) throws IOException {
            String fileName = "reducer_id_"+ workerId + "_output_.txt";
            File outPutFile = new File(fileName);
            FileWriter writer = null;

            if(resultFromReducer.isEmpty()){
                System.out.println("No output generated. Will result in an empty file");
            }

            try {
                writer = new FileWriter(outPutFile);
                for (String i : resultFromReducer.keySet()) {
                    writer.write(i + "=" + resultFromReducer.get(i) + '\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(writer!=null) writer.close();
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
//            System.out.println(" ss1: i "+i);
//            System.out.println("ss1: i.hashcode "+i.hashCode());
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
    private static MapReduceFunction<String, String> functionFromString(String s) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        MapReduceFunction<String, String> o;
        try {
            o = (MapReduceFunction<String, String>) ois.readObject();
        } catch (ClassCastException e) {
            e.printStackTrace();
            o = null;
        }
        ois.close();
        return o;
    }

    public static String apply(String s) { // for testing purposes only

        HashMap<String, String> haspmap = new HashMap<String, String>();
        haspmap.put("aa", "1");
        haspmap.put("nn", "2");
        haspmap.put("yy", "3");

        try {
            return serialize(haspmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String serialize(Serializable o) throws IOException { // for testing purposes only
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}