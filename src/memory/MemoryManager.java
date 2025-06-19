package memory;

import java.util.ArrayList;
import kernel.Process;

public class MemoryManager {

    public static ArrayList<PartitionMemory> partitionsInRam;

    public MemoryManager() {
        Ram.initialize();
        PartitionMemory.initialize();
        partitionsInRam = new ArrayList<>();
    }

    public int loadProcess(Process process) {
        boolean allocated = Ram.allocateProcess(process);
        if (!allocated) {
            throw new IllegalStateException("No free partitions for process " + process.getPid());
        }
        
        PartitionMemory partitionMemory = PartitionMemory.getPartitionByProcess(process);
        
        if (!partitionsInRam.contains(partitionMemory)) {
            return loadPartition(partitionMemory);
        } else {
            return process.getStartAddress();
        }
    }
    
    public int loadPartition(PartitionMemory partition) {
        if (partition != null) {
            partition.getProcess().setStartAddress(partition.getStartAddress());

            if (!isValidMemoryAccess(partition.getProcess(), partition)) {
                throw new SecurityException("Memory access violation: Process " + 
                        partition.getProcess().getPid() + " tried to access unauthorized memory.");
            }

            partitionsInRam.add(partition);
            return partition.getStartAddress();
        }
        return -1;
    }

    public int[] readProcess(Process process) {
        return readPartition(PartitionMemory.getPartitionByProcess(process));
    }

    public int[] readPartition(PartitionMemory partition) {
        if (partitionsInRam.contains(partition)) {
 
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
        SecondaryMemory.printMemoryAllocationTable();
    }

    public static ArrayList<PartitionMemory> getPartitionsInRam() {
        return partitionsInRam;
    }
    
    private boolean isValidMemoryAccess(Process process, PartitionMemory partition) {
        return process.getStartAddress() >= partition.getStartAddress() &&
               process.getStartAddress() + process.getSize() <= partition.getEndAddress();
    }
   
}