package memory;


import kernel.Process;   

public class Ram { 
    private static final int CAPACITY = 128;
    private static final int PARTITION_SIZE = 16; 
    private static final int TOTAL_PARTITIONS = CAPACITY / PARTITION_SIZE;

    private static int[] ram = new int[CAPACITY]; 
    private static PartitionMemory[] partitions = new PartitionMemory[TOTAL_PARTITIONS]; 

    public static void initialize() {
        for (int i = 0; i < CAPACITY; i++) 
            ram[i] = -1;
    }

   public static boolean allocateProcess(Process process) {
        PartitionMemory partition = PartitionMemory.allocatePartition(process);
        if (partition != null) {
            int start = partition.getStartAddress();
            partitions[start / PARTITION_SIZE] = partition;
            loadPartitionIntoRam(partition);
            return true;
        }
        else {
            System.out.println("null");
            return false;
        }
    }
    
    private static void loadPartitionIntoRam(PartitionMemory partition) {
        int start = partition.getStartAddress();
        int end = start + PARTITION_SIZE;
        int[] data = partition.getData();

        if (!isValidMemoryAccess(start, end)) {
            System.out.println("[ERROR] Memory access violation: Process attempting to write outside allocated space.");
            return;
        }

        for (int i = 0; i < PARTITION_SIZE; i++) {  // Upisuje podatke iz particije u RAM
            if (i < data.length) {
                ram[start + i] = data[i]; 
            } else {
                ram[start + i] = -1;
            }
        }
    }

    public static int[] readPartition(PartitionMemory partition) {
        int start = partition.getStartAddress();
        int end = start + PARTITION_SIZE;
        int[] data = new int[PARTITION_SIZE];

        if (!isValidMemoryAccess(start, end)) {
            System.out.println("[ERROR] Memory access violation: Process attempting to read outside allocated space.");
            return new int[0];
        }

        for (int i = 0; i < PARTITION_SIZE; i++) {  // Cita podatke iz RAM-a po particiji
            data[i] = ram[start + i];
        }
        return data;
    }

    private static boolean isValidMemoryAccess(int start, int end) {
        return start >= 0 && end <= CAPACITY;
    }
    
    public static void printRAM() {
        System.out.println("RAM status:");
        for (int i = 0; i < TOTAL_PARTITIONS; i++) {
            System.out.print("Particija " + i + ": ");
            if (partitions[i] != null) {
                System.out.println("Zauzeta procesom " + partitions[i].getProcess().getPid());
            } else {
                System.out.println("Slobodna");
            }
        }
    }

    public static void clearPartition(PartitionMemory partition) {
        int start = partition.getStartAddress();
        int end = start + PARTITION_SIZE;

        if (!isValidMemoryAccess(start, end)) {
            System.out.println("[ERROR] Memory access violation: Invalid partition clearing attempt.");
            return;
        }

        for (int i = start; i < end; i++) {
            ram[i] = -1;
        }
        partitions[start / PARTITION_SIZE] = null;
        partition.freePartition();
    }

    public static int getCapacity() {
        return CAPACITY;
    }
    
    public static int getAt(int i) {
        if (i < 0 || i >= CAPACITY) {
            System.out.println("[ERROR] Memory access violation: Invalid read access.");
            return -1;
        }
        return ram[i];
    }
    
    public static void setAt(int i, int value) {
        if (i < 0 || i >= CAPACITY) {
            System.out.println("[ERROR] Memory access violation: Invalid write access.");
            return;
        }
        ram[i] = value;
    }
    
    public static boolean isOcupied(int i) {
        if (i < 0 || i >= CAPACITY) {
            System.out.println("[ERROR] Memory access violation: Invalid isOcupied check.");
            return false;
        }
        return ram[i] != -1;  // ako je razlicito od -1, memorija je zauzeta
    }


   
}
