import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public class WordCountMapFunction implements MapReduceFunction<String, String> {
    @Override
    public String apply(String s) {
        HashMap<String,String> map = new HashMap<>();
        try {


            String[] temp =  s.split(",");

            if(temp.length>1){
                try{
                    for(String filename: temp){
                        if(filename.length()<3){
                            break;
                        }
                        File myObj = new File(filename);
                        Scanner myReader = new Scanner(myObj);
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            String[] array = data.split("=");

                            int defVal = Integer.parseInt(map.getOrDefault(array[0], "0"));
                            map.put(array[0], String.valueOf(defVal+Integer.parseInt(array[1])));

                        }
                        myReader.close();


                    }
                    return serialize(map);
                }
                catch (Exception e){
                    System.out.println("catch m " + e);
                    try {
                        return serialize(map);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }



            File myObj = new File(s);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] array = data.split(" ");

                for(String i : array){
                    if(map.containsKey(i)) {
                        map.put(i, String.valueOf(Integer.parseInt(map.getOrDefault(i,String.valueOf(1)))+1));
                    } else {
                        map.put(i,String.valueOf(1));
                    }
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
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
