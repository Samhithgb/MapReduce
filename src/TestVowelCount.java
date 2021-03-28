import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class TestVowelCount {

    static String configLocation = "./input_data_paths.txt";

    public static String[] getInputDataList() {
        List<String> list=new ArrayList<String>();

        try
        {
            FileInputStream fis=new FileInputStream(configLocation);
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

        configLocation = args[0];

        String[] input_files = getInputDataList();
        System.out.println("You entered: " + Arrays.toString(input_files));
        VowelCountMapFunction o = new VowelCountMapFunction();
        int res = MapReduce.initialize(input_files, MapReduceFunction.makeSerializable(o), null);
        if (res == 0) {
            verifyVowelCount();
        } else {
            System.out.println("Failed with exit code: " + res + ". Try running in terminal/cmd ");
        }
    }

    private static void verifyVowelCount() throws FileNotFoundException {
        //Based on the input, the word counts outputted from the workers have to be as follows.

        String[] filenames = new String[]{"filename1.txt","filename3.txt"};
        HashMap<String, Integer> actualCounts = new HashMap<>();

        //construct expected HashMap with counts from input files.
        HashMap<String,Integer> expectedCounts = new HashMap<>();
        expectedCounts.put("data",2);
        expectedCounts.put("for",1);
        expectedCounts.put("count",2);
        expectedCounts.put("test",1);
        expectedCounts.put("word",1);

        for(String i : filenames) {
            FileInputStream fis = new FileInputStream(i);
            Scanner sc = new Scanner(fis);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if(line!=null && !line.isEmpty()) {
                    String[] counts = line.split("=");
                    System.out.println("Count " + counts[0] + " " + counts[1]);
                    if (actualCounts.containsKey(counts[0].trim())) {
                        actualCounts.put(counts[0].trim(), actualCounts.getOrDefault(counts[0].trim(), 1) + Integer.parseInt(counts[1].trim()));
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

        System.out.println("VERIFICATION SUCCESS");
    }
}
