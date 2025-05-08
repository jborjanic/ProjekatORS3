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

    private static void errorWithParameters() {
        System.out.println("Parameters for command are incorrect!\n");
    }
}