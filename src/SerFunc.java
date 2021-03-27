import java.io.Serializable;
import java.util.function.Function;

public interface SerFunc<T, U> extends Function<T, U>, Serializable {
    static <T, U> SerFunc<T, U> makeSerializable(SerFunc<T, U> function) {
        return function;
    }
}