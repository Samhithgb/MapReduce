public class add3 implements SerFunc<String, String> {
    @Override
    public String apply(String s) {
        int i = Integer.parseInt(String.valueOf(s.charAt(s.length()-1)));
        return (s.toUpperCase()+"_"+(i*3)+"_"+3);
    }
}

