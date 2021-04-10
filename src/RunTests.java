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

            ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
            Process p = pb.start();
            p.waitFor();
            if(p.exitValue()!=0){
                throw new AssertionError("Error while verificaiton");
            }
        }

        System.out.println("Done with running all the tests. Exiting..");
    }

}
