package kernel;

import java.util.ArrayList;

public class Process {
	private ArrayList<String> instructions;
	private int startAdress;
	private int pid;
	private int size;
	
	public ArrayList<String> getInstructions() {
		return instructions;
	}
	
	public int getStartAdress() {
		return startAdress;
	}
	
	public int getPid() {
		return pid;
	}
	
	public int getSize() {
		return size;
	}


}
