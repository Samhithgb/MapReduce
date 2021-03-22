import java.util.function.Function;

public class add3 implements Function<Integer, Integer> {
    @Override
    public Integer apply(Integer integer) {
        return integer + 3;
    }
}

