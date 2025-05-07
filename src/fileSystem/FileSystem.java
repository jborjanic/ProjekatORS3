package fileSystem;

import java.io.File;
import java.io.IOException;

public class FileSystem {
    private static File currentFolder; 

    public File getCurrentFolder() {
        return currentFolder;
    }

    public static void listFiles() {
        if (currentFolder != null && currentFolder.isDirectory()) {
            System.out.println("Listing files in current directory: " + currentFolder.getAbsolutePath());
            File[] files = currentFolder.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    System.out.println(file.getName());
                }
            } else {
                System.out.println("No files found.");
            }
        } else {
            System.out.println("Invalid current directory.");
        }
    }

    public static void changeDirectory(String dir) {
        System.out.println("Changing directory to: " + dir);
        File newDir = new File(dir);
        if (isValidDirectory(newDir)) {
            currentFolder = newDir;  
            System.out.println("Directory changed to: " + currentFolder.getAbsolutePath());
        } else {
            System.out.println("Invalid directory: " + dir);
        }
    }

    private static boolean isValidDirectory(File dir) {
        return dir.exists() && dir.isDirectory();  
    }

    public static void initializeDirectory(String path) {
        currentFolder = new File(path);
        if (currentFolder.exists() && currentFolder.isDirectory()) {
            System.out.println("Initial directory set to: " + currentFolder.getAbsolutePath());
        } else {
            System.out.println("Invalid initial directory: " + path);
            currentFolder = null;
        }
    }
}
