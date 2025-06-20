package shell;

import kernel.ProcessScheduler;
import kernel.Process;
import memory.MemoryManager;

import java.io.File;

import fileSystem.FileSystem;
import javafx.scene.control.TreeItem;

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
            if (!process.isValid()) {
                System.out.println("Process " + par + " was not loaded because the program does not exist.");
                return;
            }
            int startAddr = memoryManager.loadProcess(process);
            if (startAddr >= 0) {
                System.out.println("Process loaded at address " + startAddr + ".");
            } else {
                System.out.println("Failed to load process.");
            }
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
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
    
    public static void mkdir(String name) {
        File folder = new File(FileSystem.getCurrentFolder().getAbsolutePath() + "\\" + name);
        if (folder.exists() && folder.isDirectory()) {
            System.out.println("Directory '" + name + "' already exists.");
        } else {
            FileSystem.makeDirectory(name);
            System.out.println("Directory '" + name + "' created.");
        }
    }

    public static void rm(String name) {
        for (TreeItem<File> file : Shell.tree.getTreeItem().getChildren()) {
            if (file.getValue().getName().equals(name)) {
                if (file.getValue().isDirectory()) {
                    if (file.getValue().delete()) {
                        System.out.println("Directory '" + name + "' deleted.");
                    } else {
                        System.out.println("Failed to delete directory '" + name + "'.");
                    }
                } else {
                    FileSystem.deleteFile(name);
                    System.out.println("File '" + name + "' deleted.");
                }
                return;
            }
        }
        System.out.println("File or directory '" + name + "' not found.");
    }
    
    public static void exit() {
        System.out.println("Exiting OS emulator...");
        System.exit(0);
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
            MKDIR <name> - Create a new directory
            RM <name> - Delete file or directory
            EXIT - Exit the OS emulator
            HELP - Show available commands
        """);
    }
}
