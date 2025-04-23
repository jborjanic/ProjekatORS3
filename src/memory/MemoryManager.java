package memory;

import java.util.ArrayList;
import asembler.Operations;
import kernel.Process;

public class MemoryManager {

    public static ArrayList<PartitionMemory> partitionsInRam;

    public MemoryManager() {
        Ram.initialize();
        PartitionMemory.initialize();
        partitionsInRam = new ArrayList<>();
    }

    public int loadProcess(Process process) {  
        PartitionMemory partitionMemory = PartitionMemory.getPartitionByProcess(process);

        if (partitionMemory == null) {
            partitionMemory = PartitionMemory.allocatePartition(process);
            if (partitionMemory == null) {
                throw new IllegalStateException("No free partitions for process " + process.getPid());
            }
        }

        if (!partitionsInRam.contains(partitionMemory)) { 
            return loadPartition(partitionMemory);
        } else {
            return process.getStartAdress();
        }
    }

    public int loadPartition(PartitionMemory partition) {
        PartitionMemory freePartition = findFreePartition();

        if (freePartition != null) {
            // **ZAŠTITA ADRESNOG PROSTORA** - Provera da li proces može da pristupi ovoj particiji
            if (!isValidMemoryAccess(partition.getProcess(), freePartition)) {
                throw new SecurityException("Memory access violation: Process " + 
                        partition.getProcess().getPid() + " tried to access unauthorized memory.");
            }

            freePartition.setProcess(partition.getProcess());
            freePartition.setData(partition.getData());
            partitionsInRam.add(freePartition);
            return freePartition.getStartAddress();
        }
        return -1;
    }

    private PartitionMemory findFreePartition() {
        for (PartitionMemory partition : PartitionMemory.getAllPartitions()) {
            if (!partition.isOccupied()) {
                return partition;
            }
        }
        return null;
    }

    public int[] readProcess(Process process) {
        return readPartition(PartitionMemory.getPartitionByProcess(process));
    }

    public int[] readPartition(PartitionMemory partition) {
        if (partitionsInRam.contains(partition)) {
            // **ZAŠTITA ADRESNOG PROSTORA** - Proverava da li proces može da čita ovu particiju
            if (!isValidMemoryAccess(partition.getProcess(), partition)) {
                throw new SecurityException("Memory read violation: Process " + 
                        partition.getProcess().getPid() + " attempted to read unauthorized memory.");
            }
            return Ram.readPartition(partition);
        }
        return null;
    }

    public static boolean removeProcess(Process process) {
        return removePartition(PartitionMemory.getPartitionByProcess(process));
    }

    public static boolean removePartition(PartitionMemory partition) {
        if (partitionsInRam.contains(partition)) {
            Ram.clearPartition(partition);
            partition.setProcess(null);
            partitionsInRam.remove(partition);
            return true;
        }
        return false;
    }

    public static int memoryOccupiedByProcess(Process process) {
        for (PartitionMemory partition : partitionsInRam)
            if (partition.getProcess().getPid() == process.getPid())
                return partition.getSize();
        return 0;
    }

    public static void printMemory() {
        Ram.printRAM();
        Operations.printRegisters();
        SecondaryMemory.printMemoryAllocationTable();
    }

    public static ArrayList<PartitionMemory> getPartitionsInRam() {
        return partitionsInRam;
    }

    // **ZAŠTITA ADRESNOG PROSTORA** - Proverava da li proces ima pravo pristupa memoriji
    private boolean isValidMemoryAccess(Process process, PartitionMemory partition) {
        return process.getStartAdress() >= partition.getStartAddress() &&
               process.getStartAdress() + process.getSize() <= partition.getEndAddress();
    }
}
