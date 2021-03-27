import java.io.*;
import java.util.Base64;

public class MapReduce implements Serializable {
    static int initialize(String[] source, MapReduceFunction<?, ?> function) throws IOException, InterruptedException {
        System.out.println("MapReduce.map running");
        // Launch master with necessary arguments
        String master_input = String.join(",", source);
        String[] startOptions = new String[]{"java", "-cp", ".", "Master", master_input, toString(function)};
        ProcessBuilder pb = new ProcessBuilder(startOptions);
        Process p = pb.start();

        // Get output of process
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        // Wait for process to end
        p.waitFor();
        return p.exitValue();
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
}