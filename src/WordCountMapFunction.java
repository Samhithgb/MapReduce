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

            // for(String x: temp){
            //     System.out.println(" ss1: X " + x);
            // }
            if(temp.length>1){
                try{
                    for(String filename: temp){
                        if(filename.length()<3){
                            break;
                        }
                        System.out.println("***************** SS1: File reading started:: " + filename + "  ****************");

                        // System.out.println("26");
                        File myObj = new File(filename);
                        Scanner myReader = new Scanner(myObj);
                        // System.out.println("29");
                        while (myReader.hasNextLine()) {
                            String data = myReader.nextLine();
                            String[] array = data.split("=");
                            // System.out.println(" ss1 array[0] " + array[0]);
//                            System.out.println("33");

                            int defVal = Integer.parseInt(map.getOrDefault(array[0], "0"));
                            map.put(array[0], String.valueOf(defVal+Integer.parseInt(array[1])));

//                            System.out.println("41");
                        }
                        myReader.close();


                    }
                    System.out.println(" ss1: map "+ map);
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
