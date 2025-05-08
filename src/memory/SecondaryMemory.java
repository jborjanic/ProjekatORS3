package memory;

import java.util.ArrayList;

public class SecondaryMemory {
    private static int size;
    private static Block[] blocks;
    private static int numberOfBlocks;
    public static ArrayList<FileInMemory> files;
    private Block freeListHead;

    public SecondaryMemory() {
        size = 2048;
        numberOfBlocks = size / Block.getSize();
        blocks = new Block[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new Block(i);
        }
        files = new ArrayList<>();
        linkFreeBlocks();
    }

    // Metoda za broj blokova
    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }

    // Metoda za ispis slobodnih blokova
    public void printFreeList() {
        StringBuilder freeList = new StringBuilder();
        Block current = freeListHead;
        while (current != null) {
            freeList.append("Block ").append(current.getAddress()).append("\n");
            current = current.getNextFree();
        }
        if (freeList.length() == 0) {
            System.out.println("No free blocks available.");
        } else {
            System.out.println("Free Blocks List:");
            System.out.println(freeList);
        }
    }

    private void linkFreeBlocks() {
        Block previous = null;
        freeListHead = null;
        for (Block block : blocks) {
            if (!block.isOccupied()) {
                if (previous != null) {
                    previous.setNextFree(block);
                } else {
                    freeListHead = block;
                }
                previous = block;
            }
        }
        if (previous != null) {
            previous.setNextFree(null);
        }
    }

    private int findContiguousFreeBlocks(int requiredBlocks) {
        for (int i = 0; i <= blocks.length - requiredBlocks; i++) {
            boolean canAllocate = true;
            for (int j = i; j < i + requiredBlocks; j++) {
                if (blocks[j].isOccupied()) {
                    canAllocate = false;
                    break;
                }
            }
            if (canAllocate) return i;
        }
        return -1;
    }

    public void save(FileInMemory file) {
        int requiredBlocks = file.getLength();
        int startIndex = findContiguousFreeBlocks(requiredBlocks);
        if (startIndex == -1) {
            System.out.println("Not enough contiguous space to save the file.");
            return;
        }

        for (int i = startIndex, blockIndex = 0; blockIndex < requiredBlocks; i++, blockIndex++) {
            blocks[i].setOccupied(true);
            blocks[i].writeContent(file.part(blockIndex));
            blocks[i].setNextFree(null);
        }

        file.setStartBlock(startIndex);
        file.setLength(requiredBlocks);
        files.add(file);
        linkFreeBlocks();
    }

    public void deleteFile(FileInMemory file) {
        if (!files.contains(file)) {
            System.out.println("File not found in memory.");
            return;
        }

        int startBlock = file.getStartBlock();
        int length = file.getLength();

        for (int i = startBlock; i < startBlock + length; i++) {
            blocks[i].clear();
        }

        files.remove(file);
        linkFreeBlocks();
    }

    public String readFile(FileInMemory file) {
        if (!files.contains(file))
            return "File not found.";

        StringBuilder read = new StringBuilder();
        int startBlock = file.getStartBlock();
        int length = file.getLength();

        for (int i = startBlock, blockIndex = 0; blockIndex < length; i++, blockIndex++) {
            if (!isValidBlockAccess(file, blockIndex)) {
                System.out.println("Memory access violation! Access denied.");
                return null;
            }
            byte[] content = blocks[i].getContent();
            for (byte b : content) {
                if (b != 0) read.append((char) b);
            }
        }

        return read.toString();
    }

    private boolean isValidBlockAccess(FileInMemory file, int blockIndex) {
        int start = file.getStartBlock();
        int end = start + file.getLength();
        int blockAddress = start + blockIndex;
        return blockAddress >= start && blockAddress < end;
    }

    public FileInMemory getFile(String name) {
        for (FileInMemory file : files) {
            if (file.getName().equals(name)) {
                return file;
            }
        }
        return null;
    }
    
    public ArrayList<FileInMemory> getAllFiles() {
        return new ArrayList<>(files);
    }

    public static void printMemoryAllocationTable() {
        String line = "--------------------------------------------------";
        System.out.println("Memory Allocation Table:");
        System.out.println(line);
        System.out.println("File Name\tStart Block\tLength");
        System.out.println(line);
        for (FileInMemory file : files) {
            System.out.println(file.getName() + "\t" + file.getStartBlock() + "\t\t" + file.getLength());
        }
    }
}
