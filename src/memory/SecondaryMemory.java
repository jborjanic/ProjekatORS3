package memory;

import java.util.ArrayList;

public class SecondaryMemory {
    private static int size;
    private static Block[] blocks;
    private static int numberOfBlocks;
    public static ArrayList<FileInMemory> files;
    private Block freeListHead;  // glava ulančanog slobodnog prostora

    public SecondaryMemory() {
        size = 2048;
        numberOfBlocks = size / Block.getSize();
        blocks = new Block[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new Block(i);
        }
        // Ulančavanje slobodnih blokova
        for (int i = 0; i < numberOfBlocks - 1; i++) {
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

            // Proveravamo kontinualni niz slobodnih blokova
            while (temp != null && !temp.isOccupied() &&
                   temp.getAddress() == current.getAddress() + count &&
                   count < requiredBlocks) {
                count++;
                temp = temp.getNextFree();
            }

            if (count == requiredBlocks) {
                int startIndex = current.getAddress();

                for (int i = startIndex, blockIndex = 0; blockIndex < requiredBlocks; i++, blockIndex++) {
                    blocks[i].writeContent(file.part(blockIndex));
                    blocks[i].setOccupied(true);
                    blocks[i].setNextFree(null); // više nije u lancu slobodnih
                }

                // Ažuriranje slobodnog lanca
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

    public void deleteFile(FileInMemory file) {
        if (!files.contains(file)) {
            System.out.println("File not found in memory.");
            return;
        }

        int startBlock = file.getStartBlock();
        int length = file.getLength();

        // Oslobađanje blokova i vraćanje u lanac slobodnog prostora
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
        System.out.println("File Name\tStart Block\tLength");
        System.out.println(line);
        for (FileInMemory file : files) {
            System.out.println(file.getName() + "\t" + file.getStartBlock() + "\t" + file.getLength());
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
