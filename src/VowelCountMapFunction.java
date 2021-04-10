import java.io.*;
import java.util.*;

public class VowelCountMapFunction implements MapReduceFunction<String, String> {
    @Override
    public String apply(String s) {
        HashMap<String,String> map = new HashMap<>();
        List<Character> vowels = new ArrayList<>();
        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        // intentional error
        int a = 3/0;

        try {
            File myObj = new File(s);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] array = data.split(" ");

                for(String i : array){
                    if(map.containsKey(i)) {
                        continue;
                    } else {
                        for(char j : i.toCharArray()) {
                            if(vowels.contains(j)) {
                                map.put(i, String.valueOf(Integer.parseInt(map.getOrDefault(i,String.valueOf(0)))+1));
                            }
                        }
                    }
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try {
            return serialize(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
