package memory;

import java.util.Arrays;

public class FileInMemory {
    private String name;
    private int size; // Velicina fajla u bajtovima
    private int startBlock; // Početni blok u memoriji
    private int length; // Broj zauzetih blokova
    private byte[] contentFile; // Sadržaj fajla

    public FileInMemory(String name, byte[] content) {
        this.name = name;
        this.size = content.length;
        this.contentFile = Arrays.copyOf(content, content.length);
        this.length = (int) Math.ceil((double) size / Block.getSize()); // Koliko blokova fajl zauzima
    }

    // Vraća podatke u delovima (po blokovima)
    public byte[] part(int index) {
        int blockSize = Block.getSize();
        byte[] part = new byte[blockSize];
        int startIndex = index * blockSize;

        // Ako tražimo deo koji je van opsega, vraćamo prazan blok
        if (startIndex >= contentFile.length) {
            Arrays.fill(part, (byte) ' '); // Popunjavanje praznim bajtovima
            return part;
        }

        // Kopiramo podatke u blok
        int lengthToCopy = Math.min(blockSize, contentFile.length - startIndex);
        System.arraycopy(contentFile, startIndex, part, 0, lengthToCopy);

        // Popunimo ostatak praznim bajtovima ako je potrebno
        if (lengthToCopy < blockSize) {
            Arrays.fill(part, lengthToCopy, blockSize, (byte) ' '); // Popunjavanje ostatka bloka
        }

        return part;
    }

    // Getteri i setteri
    public int getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(int startBlock) {
        this.startBlock = startBlock;
    }

    public int getLength() {
        return length;
    } // Vrati broj blokova

    public void setLength(int length) {
        this.length = length;
    }

    public int getSize() {
        return size;
    } // Vrati broj bajtova

    public String getName() {
        return name;
    }

    public byte[] getContentFile() {
        return Arrays.copyOf(contentFile, contentFile.length);
    }
}