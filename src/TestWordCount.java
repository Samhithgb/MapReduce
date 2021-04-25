import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestWordCount {

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
//        int number = 5;

        configLocation = args[0];

        Map<String,String> configMap = new HashMap<String,String>();
        configMap.put("configLocation", configLocation);
        String content = new String ( Files.readAllBytes( Paths.get(configLocation) ) );
        String[] splitted = content.split("\n");
        for(String aa: splitted){
            String temp[] = aa.split("=");
            configMap.put(temp[0], temp[1]);
        }


        System.out.println("RUNNING WORD COUNT VERIFICATION");
        String[] input_files = getInputDataList(configMap.get("input_data_locations"));
        System.out.println("You entered: " + Arrays.toString(input_files));
        WordCountMapFunction o = new WordCountMapFunction();
        WordCountReduceFunction r = new WordCountReduceFunction();




        int res = MapReduce.initialize(input_files, MapReduceFunction.makeSerializable(o), MapReduceFunction.makeSerializable(r), configMap);
//        int res =0;
        if (res == 0) {

            verifyWordCount();

        } else {
            System.out.println("Failed with exit code: " + res + ". Try running in terminal/cmd ");
        }
    }

    private static void verifyWordCount() throws FileNotFoundException {

        File dir = new File(".");
        File[] foundFilesWorker = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains("filename");
            }
        });

        File[] foundFilesReducer = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.contains("output");
            }
        });

        //construct expected HashMap with counts from input files.
        HashMap<String,Integer> expectedCounts = new HashMap<>();
        expectedCounts.put("data",4);
        expectedCounts.put("for",2);
        expectedCounts.put("count",2);
        expectedCounts.put("test",4);
        expectedCounts.put("word",2);
        expectedCounts.put("set",1);
        expectedCounts.put("another",1);

        //Based on the input, the word counts outputted from the workers have to be as follows.
        HashMap<String, Integer> actualCountsWorkers = returnActualCounts(foundFilesWorker);
        HashMap<String, Integer> actualCountsReducers = returnActualCounts(foundFilesReducer);

        for(String i : expectedCounts.keySet()) {
            if(!actualCountsWorkers.get(i).equals(expectedCounts.get(i))){
                throw new AssertionError("Error in Worker: Counts don't match for '" + i + "'. Actual :" + actualCountsWorkers.get(i) + " Expected :" + expectedCounts.get(i));
            }
        }

        for(String i : expectedCounts.keySet()) {
            if(!actualCountsReducers.get(i).equals(expectedCounts.get(i))){
                throw new AssertionError("Error in Reducers: Counts don't match for '" + i + "'. Actual :" + actualCountsReducers.get(i) + " Expected :" + expectedCounts.get(i));
            }
        }
        //no AssertionError thrown. Verification success.
        System.out.println("VERIFICATION SUCCESS");
    }

    private static HashMap<String ,Integer> returnActualCounts(File[] foundFiles) throws FileNotFoundException {
        HashMap<String, Integer> actualCounts = new HashMap<>();

        for(File i : foundFiles) {
            FileInputStream fis = new FileInputStream(i);
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line!=null && !line.isEmpty()) {
                    String[] counts = line.split("=");
                    if (actualCounts.containsKey(counts[0].trim())) {
                        actualCounts.put(counts[0].trim(), actualCounts.getOrDefault(counts[0].trim(), 1) + Integer.parseInt(counts[1].trim()));
                    } else {
                        actualCounts.put(counts[0].trim(), Integer.parseInt(counts[1].trim()));
                    }
                }
            }
            sc.close();
        }
        return actualCounts;
    }

}
