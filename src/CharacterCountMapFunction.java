import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;

public class CharacterCountMapFunction implements MapReduceFunction<String, String> {
    @Override
    public String apply(String s) {
        System.out.println("/////////////////////////////////////////////////////////// ");

        String[] temp =  s.split(",");


        if(temp.length>1){
            HashMap<String, String> map = new HashMap<>();
            try{
                for(String filename: temp){
                    if(filename.length()<3){
                        break;
                    }
                    System.out.println("File reading started " + filename);
                    File myObj = new File(filename);
                    Scanner myReader = new Scanner(myObj);
                    System.out.println("File reading end " + filename);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        System.out.println(data);
                        String[] array = data.split("=");
                        if(map.containsKey(array[0])){

                        }
                        else{
                            map.put(array[0], array[1]);
                        }
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

        HashMap<String, String> map = new HashMap<>();
        try {
            System.out.println(s);
            System.out.println(" the above is filename in apply ");
            File myObj = new File(s);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] array = data.split(" ");

                for (String i : array) {
                    if (map.containsKey(i)) {
                        //done. ignore.
                    } else {
                        map.put(i, String.valueOf(i.toCharArray().length));
                    }
                }
            }
            myReader.close();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred." + e);
            e.printStackTrace();
        }
        try {
            System.out.println(map);
            System.out.println(" apply try ");
            return serialize(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" error aagya in apply ");
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
