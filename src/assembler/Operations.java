package assembler;

public class Operations {
	public static Register R1 = new Register("R1", Constants.R1, 0);
	public static Register R2 = new Register("R2", Constants.R2, 0);
	public static Register R3 = new Register("R3", Constants.R3, 0);
	public static Register R4 = new Register("R4", Constants.R4, 0);
	
	public static void printRegisters() {
		System.out.println("Registers:");
		System.out.println("R1 value - [ " + R1.value + " ]");
		System.out.println("R2 value - [ " + R2.value + " ]");
		System.out.println("R3 value - [ " + R3.value + " ]");
		System.out.println("R4 value - [ " + R4.value + " ]");
	}
}
