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

            case "mkdir":
                if (commands.length == 2) {
                    Exe.mkdir(commands[1]);
                } else errorWithParameters();
                break;
                
            case "rm":
                if (commands.length == 2) {
                    Exe.rm(commands[1]);
                } else errorWithParameters();
                break;
                
            case "exit":
                if (commands.length == 1) {
                    Exe.exit();
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
   
    public static String previous() {
        String rez = "";
        if (!commandList.isEmpty()) {
            if (iter >= 0) {
                iter--;
                if (iter <= 0)
                    iter = 0;
                rez = commandList.get(iter);
            }
        }
        return rez;
    }

    public static String next() {
        String rez = "";
        if (!commandList.isEmpty()) {
            if (iter < commandList.size() - 1) {
                iter++;
                if (iter > commandList.size() - 1)
                    iter = commandList.size() - 1;
                rez = commandList.get(iter);
            }
        }
        return rez;
    }

    public static void readACommand(PipedInputStream inp, int len) {
        command = "";
        char c;

        for (int i = 0; i < len; i++) {
            try {
                c = (char) inp.read();
                command += c;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error while reading a command");
            }
        }
    }

    public static void setOut(OutputStream out) {
        System.setOut(new PrintStream(out, true));
    }

    private static void errorWithParameters() {
        String s = "Parameters for command are incorrect!\n";
        System.out.println(s);
    }
}