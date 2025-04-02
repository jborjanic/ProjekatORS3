package memory;
import kernel.Process;   

public class Ram { 
	private static final int CAPACITY = 128;
	private static final int PARTITION_SIZE = 16; 
	private static final int TOTAL_PARTITIONS = CAPACITY / PARTITION_SIZE; 

	private static int[] ram = new int[CAPACITY]; 
	private static PartitionMemory[] partitions = new PartitionMemory[TOTAL_PARTITIONS]; 

  	public static void initialize() {
		for (int i = 0; i < CAPACITY; i++) {
			ram[i] = -1;
		}
		PartitionMemory.initialize(); 
	}

	public static boolean allocateProcess(Process process) {
		PartitionMemory partition = PartitionMemory.allocatePartition(process);
		if (partition != null) {
			int start = partition.getPositionInMemory();
			partitions[start / PARTITION_SIZE] = partition; 
			loadPartitionIntoRam(partition);
			return true;
		}
		return false; 
	}

        private static void loadPartitionIntoRam(PartitionMemory partition) {
               int start = partition.getPositionInMemory();  
               int[] data = partition.getData();
               for (int i = 0; i < PARTITION_SIZE; i++) {
                        if (i < data.length) {
                               ram[start + i] = data[i]; 
                        } else {
                               ram[start + i] = -1; 
                        }
                }
        }

	// Briše proces iz memorije i oslobađa particiju
	public static void freeProcess(Process process) {
		PartitionMemory partition = PartitionMemory.getPartitionByProcess(process);
		if (partition != null) {
			int start = partition.getPositionInMemory();
			partitions[start / PARTITION_SIZE] = null; 
			for (int i = start; i < start + PARTITION_SIZE; i++) {
				ram[i] = -1;
			}
			partition.freePartition(); 
		}
	}

	// Čita podatke iz RAM-a po particiji
	public static int[] readPartition(PartitionMemory partition) {
		int start = partition.getPositionInMemory();
		int[] data = new int[PARTITION_SIZE];
		for (int i = 0; i < PARTITION_SIZE; i++) {
			data[i] = ram[start + i];
		}
		return data;
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
		int start = partition.getPositionInMemory();
		for (int i = start; i < start + PARTITION_SIZE; i++) {
			ram[i] = -1;
		}
		partitions[start / PARTITION_SIZE] = null;
		partition.freePartition();
	}

	public static int getCapacity() {
		return CAPACITY;
	}
        
        public static int getAt(int i){
                return ram[i];
        }

}
