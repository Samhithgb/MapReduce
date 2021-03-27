import java.io.IOException;
import java.util.Arrays;

public class TestCaller {
    public static void main(String[] args) throws IOException, InterruptedException {
        int number = 5;

        String[] input_files = {"./file" + (number + 1), "./file" + (number + 2), "./file" + (number + 3), "./file" + (number + 4)};
        System.out.println("You entered: " + Arrays.toString(input_files));
        add3 o = new add3();
        int res = MapReduce.initialize(input_files, SerFunc.makeSerializable(o));
        if (res == 0) {
            System.out.println("SUCCESS");
        } else {
            System.out.println("Failed with exit code: " + res + ". Try running in terminal/cmd ");
        }
    }
}
