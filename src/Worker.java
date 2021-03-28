import workerstate.WorkerState;

import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;

public class Worker {

    static String workerId;

    private static String STATUS_UPDATE_FORMAT = "STATUS %s : %s";

    // driver code
   public static void main(String[] args) throws InterruptedException {
        // establish a connection by providing host and port
        // number
        Thread.sleep(1000);
        try (Socket socket = new Socket("localhost", 1235)) {
            // writing to server

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            // reading from server, optional - could be used to receive commands from master
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

                        workerId = args[0];
            sendState(WorkerState.RUNNING,out);


            String file_path = args[1];
            MapReduceFunction<String, String> func;
            func = functionFromString(args[2]);
            String res = func.apply(file_path);
            // String res = Worker.apply(file_path);

            @SuppressWarnings("unchecked")
            HashMap<String, String> resultFromMapper =  (HashMap<String, String>) deserialize(res);


            out.println(" :running... hashmap output=" + resultFromMapper);
            String filePaths = AssignIntermediateFilesAndReturnFileLocations(resultFromMapper);
            out.println("filePaths: "+ filePaths);
            out.flush();
            sendState(WorkerState.DONE,out);
          
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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

    private static String AssignIntermediateFilesAndReturnFileLocations(HashMap<String, String> resultFromMapper) throws IOException {
        int num_of_reducers = 3;
        File myObj1 = new File("filename1.txt");
        File myObj2 = new File("filename2.txt");
        File myObj3 = new File("filename3.txt");

        FileWriter myWriter1 = new FileWriter("filename1.txt");
        FileWriter myWriter2 = new FileWriter("filename2.txt");
        FileWriter myWriter3 = new FileWriter("filename3.txt");

        for (String i : resultFromMapper.keySet()) {
            System.out.println("key: " + i + " value: " + resultFromMapper.get(i));
            if(i.charAt(0) < 'g') {
                myWriter1.write(i + " = " +  resultFromMapper.get(i));
            } else if (i.charAt(0) < 'p') {
                myWriter2.write(i + " = " +  resultFromMapper.get(i));
            } else {
                myWriter3.write(i + " = " +  resultFromMapper.get(i));
            }
        }

        myWriter1.close();
        myWriter2.close();
        myWriter3.close();

        return "filename1.txt , filename2.txt , filename3.txt";
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
            o = null;
        }
        ois.close();
        return o;
    }






    public static String apply(String s) { // for testing purposes only
        System.out.println(" inside the TestMapFunction");

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