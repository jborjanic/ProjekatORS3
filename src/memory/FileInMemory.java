package memory;

import java.util.Arrays;

public class FileInMemory {
    private String name;
    private int size; 
    private int startBlock; 
    private int length; 
    private byte[] contentFile; 
    private int partitionId; 

    public FileInMemory(String name, byte[] content, int partitionSize) {
        this.name = name;
        this.size = content.length;
        this.contentFile = Arrays.copyOf(content, content.length);
        this.length = (int) Math.ceil((double) size / Block.getSize()); 
    
        if (this.length > partitionSize) {
            throw new IllegalArgumentException("File too large to fit in a single partition.");
        }
    }

    public byte[] part(int index) {
        int blockSize = Block.getSize();
        byte[] part = new byte[blockSize];
        int startIndex = index * blockSize;

        if (startIndex >= contentFile.length) {
            Arrays.fill(part, (byte) ' ');
            return part;
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
        this.startBlock = startBlock;
    }

    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
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
        this.partitionId = partitionId;
    }
}
