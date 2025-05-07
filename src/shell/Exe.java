package shell;

import kernel.ProcessScheduler;
import kernel.Process;
import memory.MemoryManager;
import fileSystem.FileSystem;

public class Exe {
    private static final MemoryManager memoryManager = new MemoryManager();

    public static void ls() {
        FileSystem.listFiles();
    }

    public static void cd(String par) {
        FileSystem.changeDirectory(par);
    }

    public static void mem() {
        MemoryManager.printMemory();
    }

    public static void load(String par) {
        try {
            Process process = new Process(par);
            int startAddr = memoryManager.loadProcess(process);
            if (startAddr >= 0) {
                System.out.println("Process loaded at address " + startAddr);
            } else {
                System.out.println("Failed to load process.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unknown error during process loading.");
        }
    }

    public static void exe() {
        new ProcessScheduler().start();
    }

    public static void pr() {
        ProcessScheduler.listOfProcesses();
    }

    public static void trm(String par) {
        try {
            int pid = Integer.parseInt(par);
            Process process = ProcessScheduler.getProcessByPid(pid);
            if (process != null) {
                ProcessScheduler.terminateProcess(pid);
                MemoryManager.removeProcess(process);
                System.out.println("Process " + pid + " terminated and memory freed.");
            } else {
                System.out.println("No process with PID " + pid + " found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid PID format.");
        }
    }

    public static void help() {
        System.out.println("""
            LS - Show directory
            CD <path> - Change directory
            MEM - Show memory
            LOAD <program> - Load process into memory
            EXE - Start Round Robin process scheduling
            PR - List of processes
            TRM <PID> - Terminate process
            HELP - Show available commands
        """);
    }
}
