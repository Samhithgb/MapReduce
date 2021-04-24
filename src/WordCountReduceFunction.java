import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public class WordCountReduceFunction implements MapReduceFunction<String, String>{

    @Override
    public String apply(String s) {
        // s is a comma separated string of file paths
        String[] file_path_arr = s.split(",");

        // create hashmap
        HashMap<String, String> map = new HashMap<>();
        try{
            // iterate over file and populate hashmap
            for(String filename: file_path_arr){
                if(filename.length()<3){
                    break;
                }
                File myObj = new File(filename);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    String[] array = data.split("=");
                    if(map.containsKey(array[0])){
                        map.put(array[0], Integer.toString(Integer.parseInt(map.get(array[0])) + Integer.parseInt(array[1])));
                    }
                    else{
                        map.put(array[0], array[1]);
                    }
                }
                myReader.close();
            }

            // return hashmap (to save hashmap into single file)
            return serialize(map);
        }
        catch (Exception e){
            try {
                return serialize(map);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        return null;
    }

    private static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
