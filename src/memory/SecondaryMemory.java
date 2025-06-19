package memory;

import java.util.ArrayList;

public class SecondaryMemory {
    private static int size;
    private static Block[] blocks;
    private static int numberOfBlocks;
    public static ArrayList<FileInMemory> files;
    private Block freeListHead;  // glava ulancanog slobodnog prostora

    public SecondaryMemory() {
        size = 2048;
        numberOfBlocks = size / Block.getSize();
        blocks = new Block[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new Block(i);
        }
      
        for (int i = 0; i < numberOfBlocks - 1; i++) { // ulancavanje slobodnog prostora
            blocks[i].setNextFree(blocks[i + 1]);
        }
        blocks[numberOfBlocks - 1].setNextFree(null);
        freeListHead = blocks[0];

        files = new ArrayList<>();
    }

    public void save(FileInMemory file) {
        int requiredBlocks = file.getLength();

        Block prev = null;
        Block current = freeListHead;

        while (current != null) {
            int count = 0;
            Block temp = current;

            while (temp != null && !temp.isOccupied() &&     // neprekidni niz slobodnih blokova; da ne predjemo kraj liste
                   temp.getAddress() == current.getAddress() + count &&
                   count < requiredBlocks) {
                count++;
                temp = temp.getNextFree();
            }

            if (count == requiredBlocks) {
                int startIndex = current.getAddress();

                for (int i = startIndex, blockIndex = 0; blockIndex < requiredBlocks; i++, blockIndex++) {
                    blocks[i].writeContent(file.part(blockIndex));  // pise sadrzaj fajla u blokove
                    blocks[i].setOccupied(true);
                    blocks[i].setNextFree(null); 
                }

                if (prev == null) {
                    freeListHead = temp;
                } else {
                    prev.setNextFree(temp);
                }

                file.setStartBlock(startIndex);
                file.setLength(requiredBlocks);
                files.add(file);
                return;
            }

            prev = current;
            current = current.getNextFree();
        }

        System.out.println("Not enough continuous space to save the file.");
    }

    public void deleteFile(FileInMemory file) {  // vracanje blokova u lanac slobodnih blokova
        if (!files.contains(file)) {
            System.out.println("File not found in memory.");
            return;
        }

        int startBlock = file.getStartBlock();
        int length = file.getLength();

        for (int i = startBlock + length - 1; i >= startBlock; i--) {
            blocks[i].clear();
            blocks[i].setNextFree(freeListHead);
            freeListHead = blocks[i];
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
            byte[] content = blocks[i].getContent();
            for (byte b : content) {
                read.append((char) b);
            }
        }
        return read.toString();
    }

    public static void printMemoryAllocationTable() {
        String line = "--------------------------------------------------";
        System.out.println("Memory Allocation Table:");
        System.out.println(line);
        System.out.printf("%-20s %-15s %-10s%n", "File Name", "Start Block", "Length");
        System.out.println(line);
        for (FileInMemory file : files) {
            System.out.printf("%-20s %-15d %-10d%n", file.getName(), file.getStartBlock(), file.getLength());
        }
    }


    public boolean contains(String fileName) {
        for (FileInMemory f : files) {
            if (f.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    public FileInMemory getFile(String fileName) {
        for (FileInMemory f : files) {
            if (f.getName().equals(fileName)) {
                return f;
            }
        }
        return null;
    }

    public int getNumberOfBlocks() {
        return numberOfBlocks;
    }
}
