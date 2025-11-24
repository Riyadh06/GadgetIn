// Kelas Admin adalah subclass dari kelas Akun.
// Kelas ini merepresentasikan akun dengan peran sebagai Admin.
public class Admin extends Akun {
    // Konstruktor Admin yang memanggil konstruktor superclass (Akun)
    public Admin(String id, String username, String password) {
        super(id, username, password);
    }

    // Implementasi method abstract menu() dari kelas Akun.
    // Method ini digunakan untuk menampilkan daftar menu yang hanya tersedia untuk Admin.
    @Override
    public void menu() {
        System.out.println("+---------------------------+");
        System.out.println("|        Menu Admin         |");
        System.out.println("+---------------------------+");
        System.out.println("| 1 | Tambah Barang         |");
        System.out.println("| 2 | Edit Barang           |");
        System.out.println("| 3 | Lihat Barang          |");
        System.out.println("| 4 | Terima Penjualan      |");
        System.out.println("| 5 | History Transaksi     |");
        System.out.println("| 6 | Logout                |");
        System.out.println("+---------------------------+");
    }
}
