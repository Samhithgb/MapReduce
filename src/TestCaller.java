import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestCaller {

    public static String[] getInputDataList() {
        List<String> list=new ArrayList<String>();

        try
        {
            FileInputStream fis=new FileInputStream("C:\\Users\\gbsam\\IdeaProjects\\p1_mapreduce-team-88\\src\\input_data_paths.txt");
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

        String[] input_files = getInputDataList();
        System.out.println("You entered: " + Arrays.toString(input_files));
        TestMapFunction o = new TestMapFunction();
        int res = MapReduce.initialize(input_files, MapReduceFunction.makeSerializable(o), null);
        if (res == 0) {
            System.out.println("SUCCESS");
        } else {
            System.out.println("Failed with exit code: " + res + ". Try running in terminal/cmd ");
        }
    }
}
