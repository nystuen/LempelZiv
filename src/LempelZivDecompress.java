import java.io.*;
import java.util.ArrayList;

public class LempelZivDecompress {
    private static String s = "[]{}abcdefghijklmnopqrstuvwxyzæøå,.-;:_!#$%&/()=?´";

    //static byte[] bytes = {(byte) 5, (byte) 'b', (byte) 'm', (byte) 'o', (byte) 'b', (byte) 'a', (byte) -4, (byte) 4, (byte) 3, (byte) 'o', (byte) 'k', (byte) 'k', (byte) -10, (byte) 4, (byte) 1, (byte) 0};
    static byte[] bytes = {(byte) 12, (byte) 'P', (byte) 'r', (byte) 'o', (byte) 'b', (byte) 'l', (byte) 'e', (byte) 'm', (byte) 'e', (byte) 'r', (byte) ',', (byte) ' ', (byte) 'p', (byte) -11, (byte) 8, (byte) 10, (byte) '.', (byte) ' ', (byte) 'A', (byte) 'l', (byte) 'l', (byte) 't', (byte) 'i', (byte) 'd', (byte) ' ', (byte) 'p', (byte) -23, (byte) 8};


    public static void writeBytesToFile(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream("src/" + filename);
            fos.write(bytes, 0, bytes.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            System.out.print("error: " + e);
        }
    }


    public static void writeResultToFile(String compressedFile, String decompressedFile) {
        try {
            ArrayList<Byte> decompressedBytes = decompress(compressedFile);

            PrintWriter pw = new PrintWriter(new FileOutputStream("src/ " + decompressedFile));
            for (byte b : decompressedBytes) {
                pw.print((char) b);
            }
            pw.close();

        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }

    public static ArrayList<Byte> decompress(String filename) {

        ArrayList<Byte> result = new ArrayList<>();
        byte[] characters;
        characters = toByteArray("src/" + filename);

        int currentPosition = 0;
        int refNumber = -1;
        int readNumber = -1;

        while (currentPosition < characters.length) {

            readNumber = (int) characters[currentPosition];

            for (int i = currentPosition + 1; i <= currentPosition + readNumber; i++) {
                result.add(characters[i]);
                System.out.println("Adding " + (char) characters[i] + " to result");
            }

            currentPosition += readNumber + 1;
            if (currentPosition < characters.length) {
                refNumber = (int) characters[currentPosition];

                if (refNumber < 0) {
                    readNumber = (int) characters[currentPosition + 1];
                    currentPosition += 2;
                }

                int startPos = currentPosition - 2 + (int) characters[currentPosition - 2];
                for (int i = startPos; i <= readNumber + 1; i++) {
                    result.add(characters[i]);
                    System.out.println("Adding " + (char) characters[i] + " to result");
                }
            }
        }
        return result;
    }

    public static byte[] toByteArray(String filename) {
        try {
            File file = new File(filename);
            byte[] bytesArray = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();
            return bytesArray;

        } catch (Exception e) {
            System.out.println("ops:" + e);
            byte[] bytes = {(byte) 1};
            return bytes;
        }
    }

    public static void main(String[] args) {
        writeBytesToFile("tekster/compressedFile.txt");
        writeResultToFile("tekster/compressedFile.txt", "decompressedFile.txt");
    }

}
