package fileSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.TreeItem;
import kernel.Process;
import memory.FileInMemory;
import shell.Shell;

public class FileSystem {
	private static File rootFolder;
	private static File currentFolder;
	private TreeItem<File> treeItem;

	public FileSystem(File path) {
		rootFolder = path;
		currentFolder = rootFolder;
		treeItem = new TreeItem<>(rootFolder);
		createTree(treeItem);
	}

	public void createTree(TreeItem<File> rootItem) {
		try (DirectoryStream<Path> directoryStream = Files
				.newDirectoryStream(Paths.get(rootItem.getValue().getAbsolutePath()))) {
			for (Path path : directoryStream) {
				TreeItem<File> newItem = new TreeItem<>(path.toFile());
				newItem.setExpanded(false);
				rootItem.getChildren().add(newItem);
				if (Files.isDirectory(path))    // ako je folder
					createTree(newItem);
				else { // Ucitava fajlove u sekundarnu memoriju
					byte[] content = Files.readAllBytes(newItem.getValue().toPath());
					FileInMemory newFile = new FileInMemory(newItem.getValue().getName(), content);
					if (!Shell.memory.contains(newItem.getValue().getName()))
						Shell.memory.save(newFile);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public TreeItem<File> getTreeItem() {
		treeItem = new TreeItem<>(currentFolder);
		createTree(treeItem);
		return treeItem;
	}
	
	public static void listFiles() {
	    System.out.println("Content of: " + currentFolder.getName());
	    System.out.printf("%-8s %-24s %8s%n", "Type", "Name", "Size");
	    for (TreeItem<File> file : Shell.tree.getTreeItem().getChildren()) {
	        byte[] fileContent = null;
	        try {
	            if (!file.getValue().isDirectory()) {
	                fileContent = Files.readAllBytes(file.getValue().toPath());
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        if (file.getValue().isDirectory()) {
	            System.out.printf("%-8s %-24s %8s%n", "Folder", file.getValue().getName(), "");
	        } else {
	            System.out.printf("%-8s %-24s %8s%n", "File", file.getValue().getName(), fileContent.length + " B");
	        }
	    }
	}

	public static void changeDirectory(String directory) {
	    if (directory.equals("..")) {
	        if (!currentFolder.equals(rootFolder)) {
	            currentFolder = currentFolder.getParentFile();
	            System.out.println("Moved to: " + currentFolder.getName());
	        } else {
	            System.out.println("Already at the root directory.");
	        }
	    } else {
	        boolean found = false;
	        for (TreeItem<File> file : Shell.tree.getTreeItem().getChildren()) {
	            if (file.getValue().getName().equals(directory) && file.getValue().isDirectory()) {
	                currentFolder = file.getValue();
	                System.out.println("Moved to: " + currentFolder.getName());
	                found = true;
	                break;
	            }
	        }
	        if (!found) {
	            System.out.println("Directory '" + directory + "' not found.");
	        }
	    }
	}


	public static void makeDirectory(String directory) {
		File folder = new File(currentFolder.getAbsolutePath() + "\\" + directory);
		if (!folder.exists()) {
			folder.mkdir();
		}
	}

	public static void deleteDirectory(String directory) {
		for (TreeItem<File> file : Shell.tree.getTreeItem().getChildren()) {
			if (file.getValue().getName().equals(directory) && file.getValue().isDirectory())
				file.getValue().delete();
		}
	}

	public static void renameDirectory(String old, String newName) {
		for (TreeItem<File> file : Shell.tree.getTreeItem().getChildren()) {
			if (file.getValue().getName().equals(old) && file.getValue().isDirectory())
				file.getValue().renameTo(new File(currentFolder.getAbsolutePath() + "\\" + newName));
		}
	}


	
	public static void createFile(Process process, int result) {
	    String name = process.getName().substring(0, process.getName().indexOf('.')) + "_output";
	    File newFile = new File(process.getFilePath().getParent() + "\\" + name + ".txt");

	    try {
	        // Ova verzija uvek kreira fajl ako ne postoji i prepisuje ako postoji
	        FileWriter fw = new FileWriter(newFile, false); // false znaci ne dodavati, nego prepisati
	        fw.write("Rezultat izvrsavanja: " + result);
	        fw.close();
	    } catch (IOException e) {
	        System.out.println("Error while creating file: " + e.getMessage());
	    }
	}



	public static void deleteFile(String name) {
		for (TreeItem<File> file : Shell.tree.getTreeItem().getChildren()) {
			if (file.getValue().getName().equals(name) && !file.getValue().isDirectory())
				file.getValue().delete();
			if (Shell.memory.contains(name)) {
				Shell.memory.deleteFile(Shell.memory.getFile(name));
			}
		}
	}

	public static File getCurrentFolder() {
		return currentFolder;
	}
}