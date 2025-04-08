package kernel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import assembler.Operations;
import fileSystem.FileSystem;
import memory.MemoryManager;
import memory.Ram;
import shell.Shell;

public class ProcessScheduler extends Thread {

	public static Queue<Process> readyQueue = new LinkedList<>();;
	public static ArrayList<Process> allProcesses = new ArrayList<>();
	private static int timeQuantum = 3; 

	public ProcessScheduler() {
	}

	@Override
	public void run() { // pokrece izvrsavanja svih procesa
		while (!readyQueue.isEmpty()) {
			Process next = readyQueue.poll();  //petlja ide dok je red neprazan pa se ne moze desiti da je next = null, nema potrebe da to dodatno provjeravamo
			executeProcess(next);
			if (next.getState() != ProcessState.BLOCKED && next.getState() != ProcessState.TERMINATED && next.getState() != ProcessState.DONE) {
				next.setState(ProcessState.READY);
				readyQueue.add(next);  //to znaci ako se proces nije izvrsio do kraja vraca ga na kraj reda spremnih
			}
		}
		System.out.println("There are no processes to be executed");
	}

	private static void executeProcess(Process process) {
		Shell.currentlyExecuting = process;
		if (process.getPcValue() == -1) {  //znaci ako nije vec ranije pokretan
			System.out.println("Process " + process.getName() + " started to execute");
			int startAdress = Shell.manager.loadProcess(process);
			process.setStartAdress(startAdress);
			Shell.base = startAdress;
			Shell.limit = process.getInstructions().size();
			Shell.PC = 0;
			process.setState(ProcessState.RUNNING);
			execute(process, System.currentTimeMillis());
		} else {   //ako je proces ranije vec izvrsavan i sad se nastavlja
			System.out.println("Process " + process.getName() + " is executing again");
			int startAdress = Shell.manager.loadProcess(process);
			process.setStartAdress(startAdress);
			Shell.base = startAdress;
			Shell.limit = process.getInstructions().size();
			Shell.loadValues();  //ucitavamo kontekst
			process.setState(ProcessState.RUNNING);
			execute(process, System.currentTimeMillis());
		}
	}

	private static void execute(Process process, long startTime) {  //startTime je vrijeme kada je zapoceto izvrsavanje procesa
		while (process.getState() == ProcessState.RUNNING && System.currentTimeMillis() - startTime < timeQuantum) {
			int temp = Ram.getAt(Shell.PC + Shell.base);
			String instruction = Shell.fromIntToInstruction(temp);
			Shell.IR = instruction;
			Shell.executeMachineInstruction();
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			System.out.println("Error with thread");
		}
		if (process.getState() == ProcessState.BLOCKED) {
			System.out.println("Process " + process.getName() + " is blocked");
			Shell.saveValues();
		} else if (process.getState() == ProcessState.TERMINATED) {
			System.out.println("Process " + process.getName() + " is terminated");
			MemoryManager.removeProcess(process);
		} else if (process.getState() == ProcessState.DONE) {
			System.out.println("Process " + process.getName() + " is done");
			MemoryManager.removeProcess(process);
			FileSystem.createFile(process);
		} else { // process is switched by process scheduler
			Shell.saveValues();
		}
		Operations.clearRegisters();
	}

	public static void blockProcess(Integer pid) {
		if (pid < allProcesses.size()) {
			allProcesses.get(pid).block();
			return;
		}
		System.out.println("Process with PID " + pid + " doesnt exist, check and try again");
	}

	public static void unblockProcess(Integer pid) {
		if (pid < allProcesses.size()) {
			allProcesses.get(pid).unblock();
			return;
		}
		System.out.println("Process with PID " + pid + " doesnt exist, check and try again");
	}

	public static void terminateProcess(Integer pid) {
		if (pid < allProcesses.size()) {
			allProcesses.get(pid).terminate();
			return;
		}
		System.out.println("Process with PID " + pid + " doesnt exist, check and try again");
	}

	public static void listOfProcesses() {  //ispis liste procesa
		System.out.println("PID\tProgram\t\tSize\tState\t\tCurrent occupation of memory");
		for (Process process : allProcesses)
			System.out.println(process.getPid() + "\t" + process.getName() + "\t " + process.getSize() + "\t"
					+ ProcessState.state(process.getState())
					+ (ProcessState.state(process.getState()).length() > 8
							? "\t " + MemoryManager.memoryOccupiedByProcess(process)
							: "\t\t " + MemoryManager.memoryOccupiedByProcess(process)));
	}

	public Queue<Process> getReadyQueue() {
		return readyQueue;
	}

	public int getTimeQuantum() {
		return timeQuantum;
	}
}
}