package memory;

import java.util.ArrayList;

public class SecondaryMemory {
    private static int size;
    private static Block[] blocks;
    private static int numberOfBlocks;
    public static ArrayList<FileInMemory> files;

    public SecondaryMemory() {
        size = 2048;  // Ukupna memorija
        numberOfBlocks = size / Block.getSize();  // Broj fiksnih blokova
        blocks = new Block[numberOfBlocks];
        for (int i = 0; i < numberOfBlocks; i++) {
            blocks[i] = new Block(i);
        }
        files = new ArrayList<>();
    }

    public void save(FileInMemory file) {
        int fileSize = file.getSize();
        int blockSize = Block.getSize();
        int requiredBlocks = file.getLength(); // Koliko blokova je potrebno za fajl

        // Proverava da li fajl može da stane u slobodne blokove
        int freeBlocksCount = 0;
        int firstFreeBlock = -1;
        for (int i = 0; i < blocks.length; i++) {
            if (!blocks[i].isOccupied()) {
                freeBlocksCount++;
                if (firstFreeBlock == -1) {
                    firstFreeBlock = i; // Prvi slobodan blok
                }
                if (freeBlocksCount == requiredBlocks) {
                    break; // Našli smo dovoljno slobodnih blokova
                }
            }
        }

        // Ako nije dovoljno slobodnih blokova, vraćamo poruku
        if (freeBlocksCount < requiredBlocks) {
            System.out.println("Not enough space to save the file.");
            return;
        }

        // Dodeljujemo blokove za fajl
        for (int i = firstFreeBlock, blockIndex = 0; blockIndex < requiredBlocks; i++, blockIndex++) {
            blocks[i].setOccupied(true);
            byte[] fileData = file.part(blockIndex); // Uzimamo deo fajla
            blocks[i].writeContent(fileData);  // Upisivanje sadržaja fajla u blok
        }

        file.setStartBlock(firstFreeBlock);  // Početni blok je sada prvi zauzeti blok
        file.setLength(requiredBlocks);  // Dodeljujemo broj zauzetih blokova
        files.add(file); // Dodajemo fajl u listu
    }

    public void deleteFile(FileInMemory file) {
        if (!files.contains(file)) {
            System.out.println("File not found in memory.");
            return;
        }

        int startBlock = file.getStartBlock();  // Početni blok fajla
        int length = file.getLength();  // Koliko blokova je zauzelo fajl

        // Brišemo blokove
        for (int i = startBlock; i < startBlock + length; i++) {
            blocks[i].setOccupied(false);
            blocks[i].writeContent(null);  // Brisanje sadržaja fajla
        }

        files.remove(file); // Uklanjamo fajl iz memorije
    }

    public String readFile(FileInMemory file) {
        if (!files.contains(file)) 
            return "File not found.";

        StringBuilder read = new StringBuilder();
        int startBlock = file.getStartBlock();  // Početni blok fajla
        int length = file.getLength();  // Koliko blokova sadrži fajl

        // Čitamo fajl sa svih blokova
        for (int i = startBlock, blockIndex = 0; blockIndex < length; i++, blockIndex++) {
            byte[] content = blocks[i].getContent();
            for (byte b : content) {
                read.append((char) b);
            }
        }

        return read.toString();
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
