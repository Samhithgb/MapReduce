import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunTests {
    static String configFile = "";
    public static void main(String[] args) throws IOException, InterruptedException {
        configFile = args[0];

        String [] testFileList = {"TestCharacterCount", "TestWordCount", "TestVowelCount"};

        for(String i : testFileList) {
            String[] command = new String[]{"java" , i , configFile};

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            p.waitFor();
            if(p.exitValue()!=0){
                throw new AssertionError("Error while verificaiton");
            }
        }

        System.out.println("Done with running all the tests. Exiting..");
    }

}
