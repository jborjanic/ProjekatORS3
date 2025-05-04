package memory;

import java.util.Arrays;

public class FileInMemory {
    private String name;
    private int size; // Velicina fajla u bajtovima
    private int startBlock = -1; // Početni blok unutar particije (inicijalno nepostavljen)
    private int length; // Broj zauzetih blokova
    private byte[] contentFile; // Sadržaj fajla
    private int partitionId; // ID particije u kojoj je fajl smešten

    public FileInMemory(String name, byte[] content, int partitionSize) {
        this.name = name;
        this.size = content.length;
        this.contentFile = Arrays.copyOf(content, content.length);
        this.length = (int) Math.ceil((double) size / Block.getSize()); // Koliko blokova fajl zauzima
        
        // Provera da li fajl može stati u jednu particiju
        if (this.length > partitionSize) {
            throw new IllegalArgumentException("File too large to fit in a single partition.");
        }
    }


    // Metoda za podelu fajla na blokove unutar particije
    public byte[] part(int index) {
        int blockSize = Block.getSize();
        byte[] part = new byte[blockSize];
        int startIndex = index * blockSize;

        // Zaštita adresnog prostora - provera opsega
        if (index < 0 || startIndex >= contentFile.length) {
            throw new IndexOutOfBoundsException("Attempted to access memory out of bounds.");
        }

        int lengthToCopy = Math.min(blockSize, contentFile.length - startIndex);
        System.arraycopy(contentFile, startIndex, part, 0, lengthToCopy);

        if (lengthToCopy < blockSize) {
            Arrays.fill(part, lengthToCopy, blockSize, (byte) ' ');
        }

        return part;
    }

    public int getStartBlock() {
        return startBlock;
    }

    public void setStartBlock(int startBlock) {
        if (startBlock < 0) {
            throw new IllegalArgumentException("Invalid memory address: Start block cannot be negative.");
        }
        this.startBlock = startBlock;
    }

    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("Invalid length: Must be non-negative.");
        }
        this.length = length;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public byte[] getContentFile() {
        return Arrays.copyOf(contentFile, contentFile.length);
    }
    
    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        if (partitionId < 0) {
            throw new IllegalArgumentException("Invalid partition ID: Must be non-negative.");
        }
        this.partitionId = partitionId;
    }
}
