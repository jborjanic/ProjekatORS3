package asembler;
import shell.Shell;
import kernel.ProcessState;
import memory.Ram;


public class Operations {

    public static final String hlt = "0000";
    public static final String load = "0110";
    public static final String store = "0001";
    public static final String add = "0010";
    public static final String sub = "0011";
    public static final String mul = "0100";
    public static final String div = "1000";
    public static final String jmpe = "1001";
    public static final String jmpd = "1010";
    public static final String jmp = "1101";
    public static final String dec = "1110";
    public static final String inc = "1111";


    public static Register ACC = new Register("ACC", "1100", 0);    

    public static void load(String operand) {
        try {
            int address = Integer.parseInt(operand, 2);  
            int value = Ram.getAt(address);             
            ACC.value = value;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary address: " + operand);
        }
    }

   
    public static void add(String operand) {
        try {
            int address = Integer.parseInt(operand, 2);  
            int value = Ram.getAt(address);            
            ACC.value += value;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary address: " + operand);
        }
    }
    
    public static void mul(String operand) {
        try {
            int address = Integer.parseInt(operand, 2); 
            int value = Ram.getAt(address);              
            ACC.value *= value;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary address: " + operand);
        }
    }


    public static void store(String operand) {
        try {
            int address = Integer.parseInt(operand, 2); 
            Ram.setAt(address, ACC.value);               
            System.out.println("[DEBUG] Stored ACC value " + ACC.value + " at RAM[" + address + "]");
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary address: " + operand);
        }
    }

    public static void hlt() {
    	Shell.currentlyExecuting.setState(ProcessState.DONE);
    	
    }
    
    public static void sub(String operand) {
        try {
            int address = Integer.parseInt(operand, 2);  
            int value = Ram.getAt(address);
            ACC.value -= value;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary address: " + operand);
        }
    }

    public static void div(String operand) {
        try {
            int address = Integer.parseInt(operand, 2);
            int value = Ram.getAt(address);
            if (value != 0) {
                ACC.value /= value;
            } else {
                System.out.println("[ERROR] Division by zero attempted.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary address: " + operand);
        }
    }

    public static void inc() {
        ACC.value++;
    }

    public static void dec() {
        ACC.value--;
    }

    public static void jmp(String adr) {
        try {
            int target = Integer.parseInt(adr, 2);
            if (target >= Shell.limit) {
                Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
                System.out.println("[ERROR] Invalid jump address: " + target);
                return;
            }
            Shell.PC = target;
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid binary jump address: " + adr);
        }
    }

    public static void jmpe(String adr) {
        if (ACC.value == 0) {
            try {
                int target = Integer.parseInt(adr, 2);
                if (target >= Shell.limit) {
                    Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
                    System.out.println("[ERROR] Invalid jmpe address: " + target);
                    return;
                }
                Shell.PC = target;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid binary jmpe address: " + adr);
            }
        }
    }

    public static void jmpd(String adr) {
        if (ACC.value != 0) {
            try {
                int target = Integer.parseInt(adr, 2);
                if (target >= Shell.limit) {
                    Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
                    System.out.println("[ERROR] Invalid jmpd address: " + target);
                    return;
                }
                Shell.PC = target;
            } catch (NumberFormatException e) {
                System.out.println("[ERROR] Invalid binary jmpd address: " + adr);
            }
        }
    }

    public static void clearRegisters() {
        ACC.value = 0;
    }    
 
    public static void printRegisters() {
        System.out.println(" \n *********** REGISTERS *********** ");
        System.out.println("ACC value : [ " + ACC.value + " ]");
    }
}
