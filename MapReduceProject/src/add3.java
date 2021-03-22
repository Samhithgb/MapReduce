import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Function;

public class add3 extends Mapper implements Function<Integer, Integer> {
    @Override
    public Integer apply(Integer integer) {
        return integer + 3;
    }
}

