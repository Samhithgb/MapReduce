import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestVowelCount {

    static String configLocation = "./input_data_paths.txt";

    public static String[] getInputDataList(String location) {
        List<String> list=new ArrayList<String>();

        try
        {
            FileInputStream fis=new FileInputStream(location);
            Scanner sc=new Scanner(fis);
            while(sc.hasNextLine())
            {
                list.add(sc.nextLine());
            }
            sc.close();
            String[] itemsArray = new String[list.size()];
            itemsArray = list.toArray(itemsArray);
            return itemsArray;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        String[] dummyArray = new String[0];
        return dummyArray;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        int number = 5;
        System.out.println("[TESTVOWELCOUNT] : RUNNING VOWEL COUNT VERIFICATION");
        configLocation = args[0];


        Map<String,String> configMap = new HashMap<String,String>();
        configMap.put("configLocation", configLocation);
        String content = new String ( Files.readAllBytes( Paths.get(configLocation) ) );
        String[] splitted = content.split("\n");
        for(String aa: splitted){
            String temp[] = aa.split("=");
            configMap.put(temp[0], temp[1]);
        }
        System.out.println(configMap);


        String[] input_files = getInputDataList(configMap.get("input_data_locations"));
        System.out.println("[TESTVOWELCOUNT] : You entered: " + Arrays.toString(input_files));
        VowelCountMapFunction o = new VowelCountMapFunction();
        VowelCountReduceFunction r = new VowelCountReduceFunction();

        int res = MapReduce.initialize(input_files, MapReduceFunction.makeSerializable(o), MapReduceFunction.makeSerializable(r), configMap);
        if (res == 0) {
            verifyVowelCount();
        } else {
            System.out.println("[TESTVOWELCOUNT] : Failed with exit code: " + res + ". Try running in terminal/cmd ");
        }
    }

    private static void verifyVowelCount() throws FileNotFoundException {
        //Based on the input, the word counts outputted from the workers have to be as follows.
        HashMap<String, Integer> actualCounts = new HashMap<>();

        File dir = new File(".");
        File[] foundFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains("output");
            }
        });

        //construct expected HashMap with counts from input files.
        HashMap<String,Integer> expectedCounts = new HashMap<>();
        expectedCounts.put("a",9);
        expectedCounts.put("e",6);
        expectedCounts.put("o",7);
        expectedCounts.put("u",2);

        for(File i : foundFiles) {
            FileInputStream fis = new FileInputStream(i);
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line!=null && !line.isEmpty()) {
                    String[] counts = line.split("=");
                    if (actualCounts.containsKey(counts[0].trim())) {
                        //ignore.
                    } else {
                        actualCounts.put(counts[0].trim(), Integer.parseInt(counts[1].trim()));
                    }
                }
            }
            sc.close();
        }

        for(String i : expectedCounts.keySet()) {
            if(!actualCounts.get(i).equals(expectedCounts.get(i))){
                throw new AssertionError("Counts don't match for " + i + ". Actual :" + actualCounts.get(i) + " Expected :" + expectedCounts.get(i));
            }
        }

        //no AssertionError thrown. Verification success.
        System.out.println("[TESTVOWELCOUNT] : VERIFICATION SUCCESS");
    }
}
