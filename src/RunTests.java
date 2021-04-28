import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RunTests {
    static String configFile = "";
    public static void main(String[] args) throws IOException, InterruptedException {
        configFile = args[0];

        String [] testFileList = {"TestCharacterCount", "TestWordCount", "TestVowelCount"};
        for(String i : testFileList) {
            System.out.println("[RUNTESTS]:Working Directory = " + System.getProperty("user.dir"));
            String[] command = new String[]{"java" , "-cp", System.getProperty("user.dir") + File.separator + "out" + File.separator + "production" + File.separator+ "project_folder" ,"TestCharacterCount", configFile};
            System.out.println("-------------------------------STARTED  " + i +"  -----------------------------------");
            ProcessBuilder pb = new ProcessBuilder(command).inheritIO();
            Process p = pb.start();
            p.waitFor();
            if(p.exitValue()!=0){
                throw new AssertionError("Error while verificaiton");
            }
            System.out.println("-------------------------------DONE-----------------------------------");
        }

        System.out.println("[RUNTESTS]:Done with running all the tests. Exiting..");
    }

}
