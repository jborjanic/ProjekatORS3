package memory;

import java.util.ArrayList;
import kernel.Process;   

public class PartitionMemory {
	private static final int PARTITION_SIZE = 16; // Fiksna velicina particije
	private static final int TOTAL_PARTITIONS = 8; // Ukupan broj particija

	private int[] data;
	private int positionInMemory = -1;  // Pocetna adresa particije u memoriji
	private Process process;
	private boolean occupied;
	private PartitionMemory next; 
	private static ArrayList<PartitionMemory> allPartitions = new ArrayList<>();

	
	public static void initialize() {    // resetuje i ponovo inicijalizuje memorijske particije
		allPartitions.clear();   // da ne ostanu stare particije iz prethodnog pokretanja
		for (int i = 0; i < TOTAL_PARTITIONS; i++) {
			allPartitions.add(new PartitionMemory(i * PARTITION_SIZE));  //  odredi adresu (pocetak) svake particije
		}
	}  // memorija se dijeli na fiksne dijelove, i svaka particija ima svoju pocetnu adresu

	// Privatni konstruktor za inicijalizaciju praznih particija
	private PartitionMemory(int position) { 
		this.positionInMemory = position;
		this.data = new int[PARTITION_SIZE];
		this.occupied = false;
	}

	// Traži slobodnu particiju i dodeljuje joj proces
	public static PartitionMemory allocatePartition(Process process) {
	    // Provera da li proces staje u jednu particiju
	    if (process.getSize() <= PARTITION_SIZE) {
	        // Traži slobodnu particiju
	        for (PartitionMemory partition : allPartitions) {
	            if (!partition.isOccupied()) {
	                partition.setProcess(process);
	                partition.loadProcessData(process);
	                partition.setOccupied(true); // Označava particiju kao zauzetu
	                return partition; // Vraća particiju u kojoj je proces smešten
	            }
	        }
	    }
	    return null; // Nema dovoljno slobodnih particija
	}


	// Oslobađa particiju
	public void freePartition() {
	    this.process = null;  // Briše referencu na proces
	    this.occupied = false;  // Označava particiju kao slobodnu
	    this.data = new int[PARTITION_SIZE]; // Resetuje podatke u particiji
	}

	// Učitava instrukcije procesa u particiju
	private void loadProcessData(Process process) {
		for (int i = 0; i < Math.min(process.getInstructions().size(), PARTITION_SIZE); i++) {  // ANDJELA!!!
			this.data[i] = Integer.parseInt(process.getInstructions().get(i), 2); // Smijesta konvertovanu vrijednost u memorijsku particiju
		}
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

	public int getPositionInMemory() {
		return positionInMemory;
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