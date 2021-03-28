import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TestMapFunction implements MapReduceFunction<String, String> {
    @Override
    public String apply(String filePath) {
        HashMap<String, String> map = new HashMap<String, String>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
            String input = String.join(" ", lines).toLowerCase();

            if (input != null) {
                String[] separatedWords = input.split(" ");
                for (String str: separatedWords) {
                    if (map.containsKey(str)) {
                        int count = Integer.parseInt(map.get(str));
                        map.put(str, String.valueOf(count + 1));
                    } else {
                        map.put(str, "1");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            return serialize(map);
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
