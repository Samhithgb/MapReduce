import java.util.Arrays;
import java.util.Scanner;

public class caller {
    public static void main(String[] args) {

//        Scanner reader = new Scanner(System.in);
//        System.out.print("Enter a number: ");
//        int number = reader.nextInt();
        int number = 5;
        System.out.println("You entered: " + number);

        int[] nums = {number+1, number+2, number+3, number+4};
        System.out.println("You entered: " + Arrays.toString(nums));
        int[] res = Mapper.map(nums, new add3());
        System.out.println("You applied: " + Arrays.toString(res));

    }
}
