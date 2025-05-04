package memory;

import java.util.ArrayList;

public class SecondaryMemory {
    private static int size;
    private static Block[] blocks;
    private static int numberOfBlocks;
    public static ArrayList<FileInMemory> files;

    public SecondaryMemory() {
        size = 2048; 
        numberOfBlocks = size / Block.getSize(); 
        blocks = new Block[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new Block(i);
        }
        files = new ArrayList<>();
    }

    public void save(FileInMemory file) {
        int requiredBlocks = file.getLength(); 

        // Proverava da li fajl može da stane
        int freeBlocksCount = 0;
        int firstFreeBlock = -1;
        for (int i = 0; i < blocks.length; i++) {
            if (!blocks[i].isOccupied()) {
                freeBlocksCount++;
                if (firstFreeBlock == -1) {
                    firstFreeBlock = i; 
                }
                if (freeBlocksCount == requiredBlocks) {

                    break;

                }
            }
        }

        if (freeBlocksCount < requiredBlocks) {
            System.out.println("Not enough space to save the file.");
            return;
        }

        // Dodeljujemo blokove fajlu
        for (int i = firstFreeBlock, blockIndex = 0; blockIndex < requiredBlocks; i++, blockIndex++) {
            blocks[i].setOccupied(true);
            blocks[i].writeContent(file.part(blockIndex));
        }

        file.setStartBlock(firstFreeBlock);
        file.setLength(requiredBlocks);
        files.add(file);
    }

    public void deleteFile(FileInMemory file) {
        if (!files.contains(file)) {
            System.out.println("File not found in memory.");
            return;
        }

        int startBlock = file.getStartBlock();
        int length = file.getLength();

       
        for (int i = startBlock; i < startBlock + length; i++) {
            blocks[i].setOccupied(false);
            blocks[i].writeContent(null);
        }

        files.remove(file);
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
                read.append((char) b);
            }
        }

        return read.toString();
    }

    // **ZAŠTITA ADRESNOG PROSTORA**
    private boolean isValidBlockAccess(FileInMemory file, int blockIndex) {
        int start = file.getStartBlock();
        int end = start + file.getLength();
        int blockAddress = start + blockIndex;
        return blockAddress >= start && blockAddress < end;
    }

    private static int numberOfFreeBlocks() {
        int counter = 0;
        for (Block block : blocks) {
            if (!block.isOccupied()) {
                counter++;
            }
        }
        return counter;
    }

    public static void printMemoryAllocationTable() {
        String line = "--------------------------------------------------";
        System.out.println("Memory Allocation Table:");
        System.out.println(line);
        System.out.println("File Name\tStart Block\tLength");
        System.out.println(line);
        for (FileInMemory file : files) {
            System.out.println(file.getName() + "\t" + file.getStartBlock() + "\t" + file.getLength());
        }
    }
}
