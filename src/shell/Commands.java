package shell;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class Commands {
    private static String command;  
    private static ArrayList<String> commandList = new ArrayList<>(); 
    private static int iter;  //broj unijetih komandi do sada
    private static PrintStream outStream;

    public static void returnCommand() {
        commandList.add(command); 
        iter = commandList.size(); 
        command = command.toLowerCase();  
        String[] commands = command.split(" "); 

        switch (commands[0]) {

            case "ls":
                if (commands.length == 1) {
                    Exe.ls(); 
                } else errorWithParameters();
                break;

            case "cd":
                if (commands.length == 2) {
                    Exe.cd(commands[1]); 
                } else errorWithParameters();
                break;

            case "mem":
                if (commands.length == 1) {
                    Exe.mem();
                } else errorWithParameters();
                break;

            case "load":
                if (commands.length == 2) {
                    Exe.load(commands[1]); 
                } else errorWithParameters();
                break;

            case "exe":
                Exe.exe();
                break;

            case "pr":
                if (commands.length == 1) {
                    Exe.pr(); 
                } else errorWithParameters();
                break;

            case "trm":
                if (commands.length == 2) {
                    Exe.trm(commands[1]); 
                } else errorWithParameters();
                break;

            case "help":
                if (commands.length == 1) {
                    Exe.help();
                } else errorWithParameters();
                break;

            default:
                System.out.println("That command doesn't exist!");
        }
    }
    
    public static void readCommand(PipedInputStream input, int length) throws IOException {
        // Primer: samo ispiši komandu u izlaz (ovde bi išao pravi parser i izvršavanje)
        System.out.println("Executed: " + input);
    }
    
    public static String getCommand() {
        // Za sada neka vraća dummy tekst; ili koristi ako želiš da pokažeš rezultat
        return "Command executed.\n";
    }
    public static void setOut(OutputStream out) {
        outStream = new PrintStream(out, true);
        System.setOut(outStream); // Preusmerava System.out
    }


    private static void errorWithParameters() {
        System.out.println("Parameters for command are incorrect!\n");
    }
}