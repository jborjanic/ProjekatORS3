package memory;

import java.util.Arrays;

public class Block {
    private static final int SIZE = 64;  // Fiksna veličina svakog bloka
    private final byte[] content;
    private final int address;
    private boolean occupied;

    // Konstruktor sada ne prima veličinu, koristi statički SIZE
    public Block(int address) {
        this.address = address;
        this.content = new byte[SIZE];  // Fiksna veličina bloka
        this.occupied = false;
    }

    public int getAddress() {
        return address;
    }

    // Vraća fiksnu veličinu svakog bloka (koja je statička)
    public static int getSize() {
        return SIZE;
    }

    // Vraća kopiju sadržaja bloka
    public byte[] getContent() {
        return Arrays.copyOf(content, content.length); // Vraćamo kopiju da izbegnemo neovlašćene izmene
    }

    // Metoda za upisivanje sadržaja u blok, osigurava da se ne upiše više nego što je dozvoljeno
    public void writeContent(byte[] data) {   
        if (data.length > SIZE) {  // Provjerava da li je data veća od dozvoljenog
            throw new IllegalArgumentException("Data exceeds block size!");
        }
        System.arraycopy(data, 0, content, 0, data.length);
        this.occupied = true;
    }

    // Metoda za čitanje podataka iz bloka sa proverom adresnog prostora
    public byte readByteAt(int index) {
        if (index < 0 || index >= SIZE) {
            throw new IndexOutOfBoundsException("Memory access violation: invalid block address");
        }
        return content[index];
    }

    // Metoda za upis podataka u blok sa proverom adresnog prostora
    public void writeByteAt(int index, byte value) {
        if (index < 0 || index >= SIZE) {
            throw new IndexOutOfBoundsException("Memory access violation: invalid block address");
        }
        content[index] = value;
        this.occupied = true;
    }

    // Provjerava da li je blok zauzet
    public boolean isOccupied() {
        return occupied;
    }
    
    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    // Resetuje sadržaj bloka i postavlja ga na prazno
    public void clear() {  
        Arrays.fill(content, (byte) 0);
        this.occupied = false;
    }

    @Override
    public String toString() {
        return "Block address: " + address + ", Size: " + SIZE + ", Occupied: " + occupied;
    }
}
