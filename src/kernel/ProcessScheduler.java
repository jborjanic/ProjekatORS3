package kernel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import asembler.Operations;
import fileSystem.FileSystem;
import memory.MemoryManager;
import memory.Ram;
import shell.Shell;

public class ProcessScheduler extends Thread {

	public static Queue<Process> readyQueue = new LinkedList<>();
	public static ArrayList<Process> allProcesses = new ArrayList<>();
	private static int timeQuantum = 3; 

	public ProcessScheduler() {
	}

	@Override
	public void run() { 
		while (!readyQueue.isEmpty()) {
			Process next = readyQueue.poll(); 
			executeProcess(next);
			if (next.getState() != ProcessState.BLOCKED && next.getState() != ProcessState.TERMINATED && next.getState() != ProcessState.DONE) {
				next.setState(ProcessState.READY);
				readyQueue.add(next); 
			}
		}
		System.out.println("There are no processes to be executed");
	}
	
	private static void executeProcess(Process process) {
	    Shell.currentlyExecuting = process;

	    if (process.getPcValue() == -1) {
	        System.out.println("Process " + process.getName() + " started to execute");
	        Shell.base = process.getStartAddress();
	        Shell.PC = 0;
	        Shell.limit = process.getInstructions().size();

	        process.setState(ProcessState.RUNNING);
	        execute(process, System.currentTimeMillis());

	    } else {
	        System.out.println("Process " + process.getName() + " is executing again");

	        Shell.base = process.getStartAddress();
	        Shell.PC = process.getPcValue();
	        Shell.limit = process.getInstructions().size();
	        Shell.loadValues();

	        process.setState(ProcessState.RUNNING);
	        execute(process, System.currentTimeMillis());
	    }
	}
	
	private static void execute(Process process, long startTime) {  
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
	        Operations.clearRegisters();
	    } else if (process.getState() == ProcessState.DONE) {
	        System.out.println("Process " + process.getName() + " is done");
	        //int result = Ram.getAt(process.getStartAdress() + 3); 
	        int result = Ram.getAt(3); 
	        FileSystem.createFile(process, result);
	        MemoryManager.removeProcess(process);
	        Operations.clearRegisters();
	    } else { 
	        Shell.saveValues();
	    }
	    process.setPcValue(Shell.PC);
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
	
	public static void listOfProcesses() {
	    System.out.printf("%-4s %-18s %-6s %-10s %s%n", "PID", "Program", "Size", "State", "Current occupation of memory");
	    for (Process process : allProcesses) {
	        System.out.printf("%-4d %-18s %-6d %-10s %s%n",
	                process.getPid(),
	                process.getName(),
	                process.getSize(),
	                ProcessState.state(process.getState()),
	                MemoryManager.memoryOccupiedByProcess(process));
	    }
	}
	
	public Queue<Process> getReadyQueue() {
		return readyQueue;
	}

	public int getTimeQuantum() {
		return timeQuantum;
	}
	
	public static Process getProcessByPid(int pid) {
	    for (Process process : allProcesses) {
	        if (process.getPid() == pid) {
	            return process;
	        }
	    }
	    return null;
	}
	

}