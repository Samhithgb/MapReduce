public class Mapper {
    static int[] map(int[] source, add3 function){
        int[] res = new int[source.length];
        for (int i = 0; i < source.length; i++){
            res[i] = function.apply(source[i]);
        }
        return res;
    }
}