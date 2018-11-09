import java.sql.SQLOutput;
import java.util.Arrays;

public class LempelZiv {
    static String test = "HeiaHeia";

    public static void compressFile(String filelocation){
        System.out.println("Hei og start");
        int startIndex = 4;
        int newStartIndex = startIndex;
        int minCompress = 4;
        boolean compressed = false;
        byte[] newBytes = {0};
        byte[] bytes = convertToByte(filelocation);
        int loopLength = bytes.length;


        System.out.println(bytes.length);
        for(int i = startIndex; i < loopLength; i++){
            if (compressed){
                bytes = newBytes;
                compressed = false;
            }
            int[] compressable = findCompressable(bytes, i, minCompress);
            if(compressable[0] != -1){
                newBytes = compressBytes(compressable, bytes);
                loopLength = newBytes.length;
                compressed = true;
            }
        }

        System.out.println(bytes.length + " lengde på komprimert");
    }

    public static byte[] compressBytes(int[] values, byte[] bytes){
        int compressFrom = values[0];
        int compressTo = values[1];
        System.out.println(compressFrom + "fra, til: " + compressTo);
        int startIndex = values[3];
        byte[] tempStart = Arrays.copyOfRange(bytes, 0, compressFrom - 1);
        byte[] end = Arrays.copyOfRange(bytes, compressTo + 1, bytes.length);
        byte[] start = new byte[tempStart.length + 2];
        start[start.length - 2] = (byte)(compressFrom - startIndex);
        start[start.length - 1] = (byte)(compressTo - compressFrom);
        byte[] newBytes = new byte[start.length + end.length];
        for(int i = 0; i < start.length; i ++){
            newBytes[i] = start[i];
        }
        for(int i = 0; i < end.length; i++){
            newBytes[start.length + i] = end[i];
        }
        return newBytes;
    }

    public static int[] findCompressable(byte[] bytes, int startIndex, int minCompress){

        int compressFrom = -1;
        int compressTo;
        int newStartIndex;
        boolean firstMatch = true;
        int countMatch = 0;
        int[] noMatch = {-1};

        for(int i = 0; i < startIndex; i++){
            byte compare = bytes[startIndex];
            System.out.println(compare);
            if(compare.){
                compressFrom = startIndex - i;
                firstMatch = false;
                countMatch ++;
                //System.out.print(bytes[i]);
            }
            if(compare == bytes[i] && !firstMatch){
                countMatch ++;
                //System.out.print(bytes[i]);
            }
            if(compare != bytes[i]){
                //System.out.print(bytes[i]);
                if(countMatch >= minCompress){
                    compressTo = startIndex - i - 1;
                    newStartIndex = startIndex + i;
                    int[] values = {compressFrom, compressTo, newStartIndex, startIndex};
                    return values;
                } else {
                    firstMatch = true;
                    countMatch = 0;
                }
            }

        }
        return noMatch;
    }

    public static byte[] convertToByte(String text){
        byte[] bytes = new byte[text.length()];
        for (int i = 0; i < bytes.length; i++){
            bytes[i] = (byte)text.charAt(i);
        }
        return bytes;
    }

    public static void main(String[] args) {
        compressFile(test);
    }
}
