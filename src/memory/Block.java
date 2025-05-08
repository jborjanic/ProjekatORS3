package memory;

import java.util.Arrays;

public class Block {
    private static final int SIZE = 64;
    private final byte[] content;
    private final int address;
    private boolean occupied;
    private Block nextFree; // za ulančavanje slobodnih blokova

    public Block(int address) {
        this.address = address;
        this.content = new byte[SIZE];
        this.occupied = false;
        this.nextFree = null;
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
        if (data != null && data.length > SIZE) {
            throw new IllegalArgumentException("Data exceeds block size!");
        }
        Arrays.fill(content, (byte) 0);
        if (data != null) {
            System.arraycopy(data, 0, content, 0, data.length);
            this.occupied = true;
        } else {
            this.occupied = false;
        }
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public Block getNextFree() {
        return nextFree;
    }

    public void setNextFree(Block nextFree) {
        this.nextFree = nextFree;
    }

    public void clear() {
        Arrays.fill(content, (byte) 0);
        this.occupied = false;
        this.nextFree = null;
    }

    @Override
    public String toString() {
        return "Block address: " + address + ", Occupied: " + occupied;
    }
}
