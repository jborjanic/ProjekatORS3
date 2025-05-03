package shell;
import kernel.Process;

import fileSystem.FileSystem;
import memory.MemoryManager;
import memory.SecondaryMemory;

public class Shell {
	
public static FileSystem tree;
public static MemoryManager manager;	
public static SecondaryMemory memory;
public static int limit;
public static Process currentlyExecuting = null;
public static int PC;


}

