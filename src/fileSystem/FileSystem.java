package fileSystem;

import memory.FileInMemory;
import memory.SecondaryMemory;

import java.util.List;

public class FileSystem {
    private SecondaryMemory memory;
    private final int partitionSize;

    public FileSystem(SecondaryMemory memory) {
        this.memory = memory;
        this.partitionSize = memory.getNumberOfBlocks(); // koristi celu memoriju kao jednu particiju
    }

    // Kreiranje i upis fajla
    public void createFile(String name, String content) {
        byte[] contentBytes = content.getBytes();

        FileInMemory file = new FileInMemory(name, contentBytes, partitionSize);
        memory.save(file);
    }

    // Brisanje fajla po imenu
    public void deleteFile(String name) {
        FileInMemory file = memory.getFile(name);
        if (file != null) {
            memory.deleteFile(file);
            System.out.println("File \"" + name + "\" deleted.");
        } else {
            System.out.println("File \"" + name + "\" not found.");
        }
    }

    // Čitanje sadržaja fajla
    public String readFile(String name) {
        FileInMemory file = memory.getFile(name);
        if (file != null) {
            return memory.readFile(file);
        } else {
            return "File not found.";
        }
    }

    // Ispis tabele alokacije fajlova
    public void printAllocationTable() {
        SecondaryMemory.printMemoryAllocationTable();
    }

    // Ispis ulančanog slobodnog prostora
    public void printFreeBlocks() {
        memory.printFreeList(); // trebaš implementirati u SecondaryMemory
    }

    // Ispis svih fajlova u memoriji
    public void listFiles() {
        List<FileInMemory> files = memory.getAllFiles(); // trebaš dodati ovu metodu u SecondaryMemory
        if (files.isEmpty()) {
            System.out.println("No files in memory.");
        } else {
            System.out.println("Files in memory:");
            for (FileInMemory file : files) {
                System.out.println("- " + file.getName());
            }
        }
    }

    // Promena "direktorijuma" — ako želiš da dodaš podršku za to, trebaš podršku za foldere
    public void changeDirectory(String folderName) {
        System.out.println("Simulated folders not supported in current implementation.");
        // Možeš kasnije implementirati logiku hijerarhije ako ti bude potrebno
    }
}
