import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;

public class TestMapFunction implements MapReduceFunction<String, String> {
    @Override
    public String apply(String s) {
        System.out.println(" inside the TestMapFunction");

        HashMap<String, String> haspmap = new HashMap<String, String>();
        haspmap.put("england", "1");
        haspmap.put("nepal", "2");
        haspmap.put("yoo", "3");

        try {
            return serialize(haspmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
