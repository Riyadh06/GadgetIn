public abstract class Driver {
    protected Akun akun;

    // Konstruktor untuk menginisialisasi atribut akun.
    public Driver(Akun akun) { 
        this.akun = akun;
    }

    // Method abstract untuk menangani menu.
    // Harus diimplementasikan oleh kelas turunan untuk memberikan logika spesifik pada menu.
    public abstract void handleMenu();
    
    // Method untuk menampilkan menu
    public void displayMenu() {
        akun.menu();
    }
    
    // Method untuk membaca pilihan menu
    public int readMenuChoice(int minChoice, int maxChoice) {
        return InputUtils.readIntInRange(new java.util.Scanner(System.in), minChoice, maxChoice, "Pilih menu: ");
    }
}
