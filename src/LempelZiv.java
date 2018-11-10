import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;


public class LempelZiv {
    private final int MIN_WORDLENGTH = 4;
    private final int MAX_DISTANCE_BACK = 127; //Kommer fra hvordan du vil komprimere, bestemmes av hvor mange byte du vil bruke.
    private String fileRead = "";
    private String fileWrite = "";
    private int totalLength = -1;
    int bytesRemaining = -1;
    private byte[] bytesFromFile = new byte[0];
    private byte[] compressedBuffer = new byte[10000];

    public LempelZiv(String fileRead, String fileWrite) {
        this.fileRead = fileRead;
        this.fileWrite = fileWrite;
        readFile();
    }

    public void readFile(){
        try{
            Path readFilePath = Paths.get(fileRead);
            bytesFromFile = Files.readAllBytes(readFilePath);
            totalLength = bytesFromFile.length;
            bytesRemaining = bytesFromFile.length;
            System.out.println(bytesRemaining);
        } catch (IOException e){
            System.out.print(e);
        }
    }

    public void compressFile() {
        System.out.println("Hei og start komprimering");
        
        int bufferIndex = 0;

        while (bytesRemaining != 0) { //Går helt til alle bytene i readFila er lest og gjennomgått av komprimeringen.

            getNewBlock(bytesRemaining);
            boolean foundCompress = false;
            int doneBytes = 0;//Bytes som er ferdig komprimert slik at det er mulig å gå videre med teksten og være på riktig plass slik at neste komprimering skjer fra riktig sted.
            int compressIndex = -1;
            for(int i = 0; i < totalLength; i++){ //Loop som går gjennom hele teksten tegn for tegn.
                ArrayList<Byte> currentBytes = new ArrayList<>(); //Arrayliste som brukes
                foundCompress = false; //Må resettes hver gang.
                //Hjelpevariabler for komprimeringen:
                int compressLength = -1;
                int startCompressIndex = -1;

                for(int j = i; j < totalLength; j++){//For hvert tegn i teksten, går i loop fremover for å lage ulike ord. F.eks tekst ABCDEF: i = 0 --> ABCDEF, i = 1 --> BCDEF osv..
                    currentBytes.add(bytesFromFile[j]);//Legger til en byte av teksten for hver gjennomgang.
                    if((currentBytes.size() >= MIN_WORDLENGTH) && (bytesFromFile.length - i >= MIN_WORDLENGTH)){//Sjekker om noen av bytene som vi har lagt i arraylisten kan matche bytene i teksten. Må ha lengre ord enn minimumslengden, og det må være flere tegn igjen enn minimumsordlengde.
                        int compressPlace = findCompressionPlace(currentBytes, i); //Finner plass det er mulig å komprimere.
                        if(compressPlace >= 0){
                            foundCompress = true;
                            compressIndex = i;
                            startCompressIndex = compressPlace;
                            compressLength = currentBytes.size();
                        } else{
                            break;
                        }
                    }
                }
                if(foundCompress){
                    int unCompressed = compressIndex - doneBytes;
                    compressedBuffer[bufferIndex] = (byte)-unCompressed;
                    bufferIndex++;

                    for (int b = doneBytes; b < compressIndex; b++, bufferIndex++){
                        compressedBuffer[bufferIndex] = bytesFromFile[b];
                    }
                    int howManyBack = compressIndex - startCompressIndex;
                    compressedBuffer[bufferIndex] = (byte)howManyBack;
                    bufferIndex++;
                    compressedBuffer[bufferIndex] = (byte)compressLength;
                    bufferIndex++;

                    doneBytes = compressIndex + compressLength;
                    i += compressLength;
                }
            }
            int unCompressed = bytesFromFile.length - doneBytes;
            compressedBuffer[bufferIndex] = (byte) -unCompressed;
            bufferIndex++;

            for(int b = doneBytes; b < bytesFromFile.length; b++, bufferIndex++){
                compressedBuffer[bufferIndex] = bytesFromFile[b];
            }
        }
        byte[] buffer = compressedBuffer; //Hjelpe buffer.
        fixEmptyBufferBytes(buffer, bufferIndex); //Sender inn hjelpe buffer og lengde på komprimert tabell.
        writeToFile();
    }

    public void writeToFile(){
        try{
            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileWrite)));
            dataOutputStream.write(compressedBuffer);
            dataOutputStream.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void fixEmptyBufferBytes(byte[] buffer, int bufferLength){
        compressedBuffer = new byte[bufferLength];
        for(int i = 0; i < bufferLength; i++){
            compressedBuffer[i] = buffer[i];
            System.out.println(buffer[i]);
        }
    }

    public int findCompressionPlace(ArrayList<Byte> bytesToMatch, int startIndex){
        int startLongestBackIndex = startIndex - MAX_DISTANCE_BACK; //Variabel for hvor langt tilbake det skal startes å lete etter match for komprimering.
        int distanceBack = MAX_DISTANCE_BACK; //Variabel for hvor langt tilbake det kan letes.
        if(startLongestBackIndex < 0){
            startLongestBackIndex = 0;
            distanceBack = startIndex;//Hvis det ikke er mulig å lete lengre tilbake enn en blokkstørrelse settes variabelen til startindeksen fordi det er hvor langt vi har kommet frem og derfor ikke er mulig å lete lengre tilbake.
        }

        for (int i = startLongestBackIndex; i <= (distanceBack - MIN_WORDLENGTH); i++){ //Sjekker for hver bokstav fra -127 tilbake eller så langt som mulig fra startIndex.
            boolean isFound = true;
            //Loopen går helt til ett tegn ikke matcher, da returner metoden
            for(int j = i, k = 0; k < bytesToMatch.size(); j++, k++){ //Sjekker for hvert ord som er laget i arraylisten.
                if(tempBlock[j] != bytesToMatch.get(k)){ //Sjekker hvert tegn i det ordet.
                    isFound = false;
                    break;
                }
                if(isFound){
                    return i;
                }
            }
        }
        return -1;
    }

    private byte[] tempBlock; //Midlertidig blokk, størrelsen kommer fra hvilken bytestørrelse vi velger på komprimeringen.
    private int newBlockCounter = 0; //Holder tellingen på hvor vi er i teksten vår slik at om teksten overgår maks størrelse, kan vi fortsette der vi slapp.

    public void getNewBlock(int bytesRemaining) {
        if (bytesRemaining < MAX_DISTANCE_BACK) { //Hvis bytesRemaining ikke overgår en blokkstørrelse.
            tempBlock = new byte[bytesRemaining];
            for (int i = 0; i < bytesRemaining; i++, newBlockCounter++) {
                tempBlock[i] = bytesFromFile[newBlockCounter];
            }
            this.bytesRemaining = 0;
        } else { //Hvis bytesRemaining overgår blokkstørrelsen.
            tempBlock = new byte[MAX_DISTANCE_BACK];
            for (int i = 0; i < MAX_DISTANCE_BACK; i++, newBlockCounter++) {
                tempBlock[i] = bytesFromFile[newBlockCounter];
            }
            this.bytesRemaining -= MAX_DISTANCE_BACK; //bytesRemaining blir da en blokkstørrelse mindre.
        }
    }

    public static void main(String[] args) {
        LempelZiv LempelZinCompression = new LempelZiv("C:\\Users\\knut-\\OneDrive\\Dokumenter\\Skole\\Programmering\\Prosjekter\\LempelZiv\\tekster\\decompressedFile.txt", "C:\\Users\\knut-\\OneDrive\\Dokumenter\\Skole\\Programmering\\Prosjekter\\LempelZiv\\tekster\\compressed.txt"); //Sende inn fil som skal komprimeres og hvor ny fil skal skrives.
        LempelZinCompression.compressFile();
    }
}
