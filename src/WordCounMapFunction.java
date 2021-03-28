public class WordCounMapFunction implements MapReduceFunction<String, String> {
    @Override
    public String apply(String s) {
        //int i = Integer.parseInt(String.valueOf(s.charAt(s.length() - 1)));
        // (s.toUpperCase() + "_" + (i * 3) + "_" + 3);
        return s.toUpperCase();
    }
}

