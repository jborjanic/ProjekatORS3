package memory;

import java.util.ArrayList;
import kernel.Process;

public class PartitionMemory {
    private static final int PARTITION_SIZE = 16; // Fiksna veličina particije
    private static final int TOTAL_PARTITIONS = 8; // Ukupan broj particija

    private int[] data;
    private int positionInMemory; // Početna adresa particije u memoriji
    private Process process;
    private boolean occupied;
    private PartitionMemory next;
    private static ArrayList<PartitionMemory> allPartitions = new ArrayList<>();

    // Inicijalizacija memorijskih particija
    public static void initialize() {
        allPartitions.clear();
        for (int i = 0; i < TOTAL_PARTITIONS; i++) {
            allPartitions.add(new PartitionMemory(i * PARTITION_SIZE));
        }
    }

    // Privatni konstruktor za inicijalizaciju praznih particija
    private PartitionMemory(int position) {
        this.positionInMemory = position;
        this.data = new int[PARTITION_SIZE];
        this.occupied = false;
    }

    // Dodeljuje proces particiji ako ima dovoljno mesta
    public static PartitionMemory allocatePartition(Process process) {
        if (process.getSize() <= PARTITION_SIZE) {
            for (PartitionMemory partition : allPartitions) {
                if (!partition.isOccupied()) {
                    partition.setProcess(process);
                    partition.loadProcessData(process);
                    partition.setOccupied(true);
                    return partition;
                }
            }
        }
        return null;
    }

    // Oslobađa particiju
    public void freePartition() {
        this.process = null;
        this.occupied = false;
        this.data = new int[PARTITION_SIZE];
    }

    // Učitava instrukcije procesa u particiju
    private void loadProcessData(Process process) {
        for (int i = 0; i < Math.min(process.getInstructions().size(), PARTITION_SIZE); i++) {
            this.data[i] = Integer.parseInt(process.getInstructions().get(i), 2);
        }
    }

    // **ZAŠTITA ADRESNOG PROSTORA** - Proverava da li je adresa u okviru particije
    public boolean isValidMemoryAccess(int address) {
        return address >= getStartAddress() && address < getEndAddress();
    }

    // **GETTERI ZA GRANICE MEMORIJE**
    public int getStartAddress() {
        return positionInMemory;
    }

    public int getEndAddress() {
        return positionInMemory + PARTITION_SIZE;
    }

    // Getteri i pomoćne metode
    public static ArrayList<PartitionMemory> getAllPartitions() {
        return allPartitions;
    }

    public PartitionMemory getNext() {
        return next;
    }

    public void setNext(PartitionMemory next) {
        this.next = next;
    }

    public static PartitionMemory getPartitionByProcess(Process process) {
        for (PartitionMemory partition : allPartitions) {
            if (partition.process != null && partition.process.equals(process))
                return partition;
        }
        return null;
    }

    public int getSize() {
        return PARTITION_SIZE;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public int[] getData() {
        return data;
    }

    public void setData(int[] data) {
        this.data = data;
    }
}
