package memory;

import java.util.Arrays;

public class Block {
    private static final int SIZE = 64;
    private final byte[] content;
    private final int address;
    private boolean occupied;

    public Block(int address) {
        this.address = address;
        this.content = new byte[SIZE];  
        this.occupied = false;
    }

    public int getAddress() {
        return address;
    }

    public static int getSize() {
        return SIZE;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length); 
    }

    public void writeContent(byte[] data) {   
        if (data.length > SIZE) {  
            throw new IllegalArgumentException("Data exceeds block size!");
        }
        System.arraycopy(data, 0, content, 0, data.length);
        this.occupied = true;

    // Metoda za upis podataka u blok sa proverom adresnog prostora
    public void writeByteAt(int index, byte value) {
        if (index < 0 || index >= SIZE) {
            throw new IndexOutOfBoundsException("Memory access violation: invalid block address");
        }
        content[index] = value;
        this.occupied = true;
    }


    public boolean isOccupied() {
        return occupied;
    }
    
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public void clear() {  
        Arrays.fill(content, (byte) 0);
        this.occupied = false;
    }

    @Override
    public String toString() {
        return "Block address: " + address + ", Size: " + SIZE + ", Occupied: " + occupied;
    }
}
