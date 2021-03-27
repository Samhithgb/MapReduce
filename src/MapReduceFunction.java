import java.io.Serializable;
import java.util.function.Function;

public interface MapReduceFunction<T, U> extends Function<T, U>, Serializable {
    static <T, U> MapReduceFunction<T, U> makeSerializable(MapReduceFunction<T, U> function) {
        return function;
    }
}