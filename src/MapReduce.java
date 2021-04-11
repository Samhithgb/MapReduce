import java.io.*;
import java.util.Base64;
import java.util.Map;

public class MapReduce implements Serializable {
    static int initialize(String[] source, MapReduceFunction<?, ?> mapFunction, MapReduceFunction<?, ?> reduceFunction, Map<String,String> configMap) throws IOException, InterruptedException {
        System.out.println("MapReduce.map running");

        // Launch master with necessary arguments
        String master_input = String.join(",", source);

        System.out.println("Input : " + master_input);

        String[] startOptions = new String[]{"java", "-cp", System.getProperty("user.dir") + File.separator + "out" + File.separator + "production" + File.separator+ "project_folder", "Master", master_input, toString(mapFunction), toString((Serializable) configMap), toString(reduceFunction)};
        ProcessBuilder pb = new ProcessBuilder(startOptions).inheritIO();
        Process p = pb.start();
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