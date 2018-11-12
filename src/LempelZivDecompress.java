import java.io.*;
import java.lang.reflect.Array;
import java.rmi.server.ExportException;
import java.util.ArrayList;

public class LempelZivDecompress {
    private static String s = "[]{}abcdefghijklmnopqrstuvwxyzæøå,.-;:_!#$%&/()=?´";

    //sftatic byte[] bytes = {(byte) 5, (byte) 'b', (byte) 'm', (byte) 'o', (byte) 'b', (byte) 'a', (byte) -4, (byte) 4, (byte) 3, (byte) 'o', (byte) 'k', (byte) 'k', (byte) -10, (byte) 4, (byte) 1, (byte) 0};
      static byte[] bytes = {(byte) 12, (byte) 'P', (byte) 'r', (byte) 'o', (byte) 'b', (byte) 'l', (byte) 'e', (byte) 'm', (byte) 'e', (byte) 'r', (byte) ',', (byte) ' ', (byte) 'p', (byte) -11, (byte) 8, (byte) 10, (byte) '.', (byte) ' ', (byte) 'A', (byte) 'l', (byte) 'l', (byte) 't', (byte) 'i', (byte) 'd', (byte) ' ', (byte) 'p', (byte) -24, (byte) 8};
    //static byte[] bytes = {(byte) 10, (byte) 'H', (byte) 'e', (byte) 'i', (byte) ' ', (byte) 's', (byte) 'v', (byte) 'e', (byte) 'i', (byte) 's', (byte) ',', (byte) -7, (byte) 6};
    // Hei sveis, sveis

    public static void writeBytesToFile(String filename) {
        try {
            FileOutputStream fos = new FileOutputStream("tekster/" + filename);
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

            PrintWriter pw = new PrintWriter(new FileOutputStream("tekster/" + decompressedFile));
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
        characters = toByteArray("tekster/" + filename);

        int currentPosition = 0;
        int refNumber = -1;
        int readNumber = -1;
        boolean firstIteration = true;

        while (currentPosition < characters.length) {
            // System.out.println("startPos: " + currentPosition);

            readNumber = (int) characters[currentPosition];
            /**
             * characeters[currenpos] er negativt (btyr hvor mange tilbake vi skal), følgende tall er positivt.
             * Følgende tallet betyr hvor mange som skal leses.
             */
            if (readNumber < 0) { // readnumber er negativt, egentlig har vi funnet et refnumber
                refNumber = (int) characters[currentPosition];
                readNumber = (int) characters[currentPosition + 1];

                int tempCurrentPosition = currentPosition;
                if(!firstIteration) {
                    currentPosition += 2;
                } else {
                    firstIteration = false;
                }

                int startPos = tempCurrentPosition + refNumber ;
                System.out.println("Startpos: " + startPos + "\nReadnumber: " + readNumber + "\nRefnumber: " + refNumber +"\nTempCurrentpos: " + tempCurrentPosition);
                for (int i = 0; i < readNumber; startPos++, i++) {
                    result.add(characters[startPos]);
                    System.out.print(" " + (char) characters[startPos]);
                }
            } else {
                /**
                 * characters[currentposition] er positivt, følgende karakterer skal være ukomprimert.
                 */
                currentPosition++;
                for (int i = 0; i < readNumber; currentPosition++, i++) {
                    System.out.print(" " + (char) characters[currentPosition]);
                    result.add(characters[currentPosition]);
                }
            }

        }
            return result;
    }
/*

            for (int i = currentPosition + 1; i <= currentPosition + readNumber; i++) {
                //System.out.println("i :" + i);
                result.add(characters[i]);
                // System.out.println("Adding " + (char) characters[i] + " to result");
            }

            currentPosition += readNumber + 1;

            if (currentPosition < characters.length) {
                refNumber = (int) characters[currentPosition];

                if (refNumber < 0) {
                    readNumber = (int) characters[currentPosition + 1];
                    currentPosition += 2;
                }

                int startPos = currentPosition - 2 + (int) characters[currentPosition - 2];
                System.out.println("Startpos: " + startPos + "\nReadnumber: " + readNumber + "\nRefnumber: " + refNumber);
                for (int i = 0; i < readNumber; startPos++, i++) {

                    result.add(characters[startPos]);
                    System.out.println("Adding " + (char) characters[startPos] + " to result");
                }
                //  System.out.println();
            }
        }
        return result;

    }

*/
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
        // writeBytesToFile("compressedFile.txt");
        // writeBytesToFile("compressed.txt");
        writeResultToFile("compressed.txt", "decompressedFile.txt");
    }

}

/**
 * Hei sveis,�
 */
