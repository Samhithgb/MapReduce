import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class Worker {

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
            String id = args[0];
            String file_path = args[1];
            MapReduceFunction<String, String> func;
            func = functionFromString(args[2]);
            out.println(id + " :starting... input=" + file_path);
            out.flush();
            Thread.sleep(1000);

            String res = func.apply(file_path);
            out.println(id + " :running... output=" + res);
            out.flush();
            Thread.sleep(5000);

            out.println(args[0] + " :done...");
            out.flush();
        } catch (IOException | InterruptedException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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
