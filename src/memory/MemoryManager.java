package memory;

import java.util.ArrayList;

import assembler.Operations;
import kernel.Process;

public class MemoryManager {

	public static ArrayList<PartitionMemory> partitionsInRam;   // zasto se uzima Ram? - primarna memorija; znamo sta je trenutno aktivno

	public MemoryManager() {
		Ram.initialize();
		PartitionMemory.initialize();
		partitionsInRam = new ArrayList<>();
	}

	public int loadProcess(Process process) {  
	    PartitionMemory partitionMemory = PartitionMemory.getPartitionByProcess(process); // Dobija memorijsku particiju za proces

	    if (partitionMemory == null) { // Ako proces još nije u memoriji, alociraj mu particiju
	        partitionMemory = PartitionMemory.allocatePartition(process);
	        if (partitionMemory == null) {
	            throw new IllegalStateException("There are no free partitions for the process." + process.getPid());
	        }
	    }

	    if (!partitionsInRam.contains(partitionMemory)) { 
	        return loadPartition(partitionMemory);  
	    } else {
	        return process.getStartAdress(); // Ako je proces već u RAM-u, vrati njegovu početnu adresu
	    }
	}

	public int loadPartition(PartitionMemory partition) { // za ucitavanje particije u RAM, ako postoji slobodna memorijska particija
		PartitionMemory freePartition = findFreePartition(); // nadji slobodnu particiju

		if (freePartition != null) {
			freePartition.setProcess(partition.getProcess());
			freePartition.setData(partition.getData());         // postavlja sadrzaj memorije
			partitionsInRam.add(freePartition);
			return freePartition.getPositionInMemory();             // vraca pocetnu memorijsku adresu gde je proces ucitan    
		}
		return -1; // Nema slobodnih particija
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
		return readPartition(PartitionMemory.getPartitionByProcess(process));  // pronalazi memorijsku particiju gde je proces smjesten
                                                                                       // cita podatke iz particije
	}

	public int[] readPartition(PartitionMemory partition) {
		if (partitionsInRam.contains(partition))
			return Ram.readPartition(partition);
			//return Ram.readPartition(partition.getPositionInMemory(), partition.getSize());
		return null;
	}

	public static boolean removeProcess(Process process) {
		return removePartition(PartitionMemory.getPartitionByProcess(process));
	}

	public static boolean removePartition(PartitionMemory partition) {
		if (partitionsInRam.contains(partition)) {
			Ram.clearPartition(partition);
		   //Ram.clearPartition(partition.getPositionInMemory(), partition.getSize());
			partition.setProcess(null); 
			partitionsInRam.remove(partition);
			return true;
		}
		return false;
	}

	public static int memoryOccupiedByProcess(Process process) {  // vraca velicinu memorije zauzetu od strane tog procesa
		for (PartitionMemory partition : partitionsInRam)
			if (partition.getProcess().getPid() == process.getPid())
				return partition.getSize();    
		return 0;   // ako proces nije pronadjen, vraca 0
	}
	
	public static void printMemory() {
		Ram.printRAM();
		Operations.printRegisters();
		SecondaryMemory.printMemoryAllocationTable();
	}

	public static ArrayList<PartitionMemory> getPartitionsInRam() {
		return partitionsInRam;
	}
}