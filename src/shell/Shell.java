package shell;

import java.io.File;
import asembler.Constants;
import asembler.Operations;
import fileSystem.FileSystem;
import kernel.ProcessScheduler;
import kernel.Process;
import memory.MemoryManager;
import memory.SecondaryMemory;

public class Shell {
    public static FileSystem tree;
    public static MemoryManager manager;
    public static SecondaryMemory memory;
    public static Process currentlyExecuting = null;
    public static int PC;
    public static String IR;
    public static int limit;
    public static int base;

    public static void boot() {
        new ProcessScheduler();
        manager = new MemoryManager();
        memory = new SecondaryMemory();
        tree = new FileSystem(new File("Programs"));
    }

    public static void executeMachineInstruction() {
        String operation = IR.substring(0, 4);
        boolean programCounterChanged = false;

        if (operation.equals(Operations.hlt)) {
            Operations.hlt();
        } else if (operation.equals(Operations.load)) {
            String operand = IR.substring(4, 12);
            Operations.load(operand);
        } else if (operation.equals(Operations.store)) {
            String operand = IR.substring(4, 12);
            Operations.store(operand);
        } else if (operation.equals(Operations.add)) {
            String operand = IR.substring(4, 12);
            Operations.add(operand);
        } else if (operation.equals(Operations.sub)) {
            String operand = IR.substring(4, 12);
            Operations.sub(operand);
        } else if (operation.equals(Operations.mul)) {
            String operand = IR.substring(4, 12);
            Operations.mul(operand);
        } else if (operation.equals(Operations.div)) {
            String operand = IR.substring(4, 12);
            Operations.div(operand);
        } else if (operation.equals(Operations.inc)) {
            Operations.inc();
        } else if (operation.equals(Operations.dec)) {
            Operations.dec();
        } else if (operation.equals(Operations.jmp)) {
            String adr = IR.substring(4, 12);
            Operations.jmp(adr);
            programCounterChanged = true;
        } else if (operation.equals(Operations.jmpe)) {
            String adr = IR.substring(4, 12);
            Operations.jmpe(adr);
            programCounterChanged = true;
        } else if (operation.equals(Operations.jmpd)) {
            String adr = IR.substring(4, 12);
            Operations.jmpd(adr);
            programCounterChanged = true;
        }

        if (!programCounterChanged) PC++;
    }

    public static String assemblerToMachineInstruction(String line) {
        String[] command = line.split("[ ,]+");
        String instruction = "";

        switch (command[0]) {
            case "HLT":
                return Operations.hlt;
            case "LOAD":
                instruction += Operations.load + toBinary(command[1]);
                break;
            case "STORE":
                instruction += Operations.store + toBinary(command[1]);
                break;
            case "ADD":
                instruction += Operations.add + toBinary(command[1]);
                break;
            case "SUB":
                instruction += Operations.sub + toBinary(command[1]);
                break;
            case "MUL":
                instruction += Operations.mul + toBinary(command[1]);
                break;
            case "DIV":
                instruction += Operations.div + toBinary(command[1]);
                break;
            case "INC":
                instruction += Operations.inc + "00000000";
                break;
            case "DEC":
                instruction += Operations.dec + "00000000";
                break;
            case "JMP":
                instruction += Operations.jmp + toBinary(command[1]);
                break;
            case "JMPE":
                instruction += Operations.jmpe + toBinary(command[1]);
                break;
            case "JMPD":
                instruction += Operations.jmpd + toBinary(command[1]);
                break;
        }

        return instruction;
    }

    public static String toBinary(String s) {
        int number = Integer.parseInt(s);
        String bin = Integer.toBinaryString(number);
        while (bin.length() < 8) {
            bin = "0" + bin;
        }
        return bin;
    }

    public static String fromIntToInstruction(int val) {
        String inst = Integer.toBinaryString(val);
        while (inst.length() < 12) {
            inst = "0" + inst;
        }
        return inst;
    }

    public static void saveValues() {
        int[] registers = {
            Operations.R1.value, Operations.R2.value,
            Operations.R3.value, Operations.R4.value
        };
        currentlyExecuting.setValuesOfRegisters(registers);
        currentlyExecuting.setPcValue(PC);
    }

    public static void loadValues() {
        int[] registers = currentlyExecuting.getValuesOfRegisters();
        Operations.R1.value = registers[0];
        Operations.R2.value = registers[1];
        Operations.R3.value = registers[2];
        Operations.R4.value = registers[3];
        PC = currentlyExecuting.getPcValue();
    }
}