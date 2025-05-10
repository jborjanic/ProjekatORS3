package asembler;
import shell.Shell;
import kernel.ProcessState;


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

    public static Register R1 = new Register("R1", Constants.R1, 0);
    public static Register R2 = new Register("R2", Constants.R2, 0);
    public static Register R3 = new Register("R3", Constants.R3, 0);
    public static Register R4 = new Register("R4", Constants.R4, 0);

    public static void load(String operand) {
        Register r = getRegister(operand);
        if (r != null) {
            ACC.value = r.value;  
        } else if (operand.length() == 8) {
            ACC.value = Integer.parseInt(operand, 2);  
        }
    }

    public static void store(String reg) {
        Register r = getRegister(reg);
        if (r != null) {
            r.value = ACC.value;
        }
    }


    public static void add(String operand) {
        Register r = getRegister(operand);
        if (r != null) {
            ACC.value += r.value;
        } else if (operand.length() == 8) {
            ACC.value += Integer.parseInt(operand, 2);
        }
    }

    public static void sub(String operand) {
        Register r = getRegister(operand);
        if (r != null) {
            ACC.value -= r.value;
        } else if (operand.length() == 8) {
            ACC.value -= Integer.parseInt(operand, 2);
        }
    }

    public static void mul(String operand) {
        Register r = getRegister(operand);
        if (r != null) {
            ACC.value *= r.value;
        } else if (operand.length() == 8) {
            ACC.value *= Integer.parseInt(operand, 2);
        }
    }

    public static void div(String operand) {
        Register r = getRegister(operand);
        if (r != null && r.value != 0) {
            ACC.value /= r.value;
        } else if (operand.length() == 8) {
            int val = Integer.parseInt(operand, 2);
            if (val != 0) ACC.value /= val;
        }
    }
    
    public static void hlt() {
    	Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
    	
    }

    public static void inc() {
        ACC.value++;
    }

    public static void dec() {
        ACC.value--;
    }
    
    public static void hlt() {
		Shell.currentlyExecuting.setState(ProcessState.DONE);
	}
    
    public static void jmp(String adr) {
        int target = Integer.parseInt(adr, 2);
        if (target >= Shell.limit) {
            Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
            System.out.println("Invalid jump address in process: " + Shell.currentlyExecuting.getName());
            return;
        }
        Shell.PC = target;
    }

    public static void jmpe(String adr) {
        // Skok ako je ACC == 0
        if (ACC.value == 0) {
            int target = Integer.parseInt(adr, 2);
            if (target >= Shell.limit) {
                Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
                System.out.println("Invalid jmpe address in process: " + Shell.currentlyExecuting.getName());
                return;
            }
            Shell.PC = target;
        }
    }

    public static void jmpd(String adr) {
        // Skok ako ACC != 0
        if (ACC.value != 0) {
            int target = Integer.parseInt(adr, 2);
            if (target >= Shell.limit) {
                Shell.currentlyExecuting.setState(ProcessState.TERMINATED);
                System.out.println("Invalid jmpd address in process: " + Shell.currentlyExecuting.getName());
                return;
            }
            Shell.PC = target;
        }
    }


    public static void clearRegisters() {
        R1.value = 0;
        R2.value = 0;
        R3.value = 0;
        R4.value = 0;
        ACC.value = 0;
    }

    private static Register getRegister(String address) {
        switch (address) {
            case Constants.R1:
                return R1;
            case Constants.R2:
                return R2;
            case Constants.R3:
                return R3;
            case Constants.R4:
                return R4;
            default:
                return null;
        }
    }

    public static void printRegisters() {
        System.out.println(" \n *********** REGISTERS *********** ");
        System.out.println("ACC value : [ " + ACC.value + " ]");
        System.out.println("R1 value : [ " + R1.value + " ]");
        System.out.println("R2 value : [ " + R2.value + " ]");
        System.out.println("R3 value : [ " + R3.value + " ]");
        System.out.println("R4 value : [ " + R4.value + " ]");
    }
}
