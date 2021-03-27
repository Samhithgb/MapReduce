import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class Worker {

    static String workerId;

    private static String STATUS_UPDATE_FORMAT = "%s : %s";

    // driver code
    public static void main(String[] args) throws InterruptedException {
        // establish a connection by providing host and port
        // number
        Thread.sleep(1000);
        try (Socket socket = new Socket("localhost", 1235)) {
            // writing to server

            PrintWriter out = new PrintWriter(
                    socket.getOutputStream(), true);
            sendState(WorkerState.RUNNING,out);
            // reading from server, optional - could be used to receive commands from master
            BufferedReader in
                    = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));

            workerId = args[0];

            String file_path = args[1];
            MapReduceFunction<String, String> func;
            func = functionFromString(args[2]);

            String res = func.apply(file_path);

            sendState(WorkerState.DONE,out);

            out.flush();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void sendState(WorkerState state, PrintWriter out){
        out.println(String.format(STATUS_UPDATE_FORMAT,workerId,state.toString()));
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

}
