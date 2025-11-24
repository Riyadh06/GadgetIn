import java.util.ArrayList;


public class Customer extends Akun {
    private Keranjang keranjang;
    private ArrayList<Invoice> invoiceSelesai;

    // Konstruktor Customer yang memanggil konstruktor superclass (Akun)
    public Customer(String id, String username, String password) {
        super(id, username, password);
        this.keranjang = new Keranjang(); // Membuat objek Keranjang baru untuk pelanggan.
        this.invoiceSelesai = new ArrayList<>(); // Inisialisasi ArrayList untuk invoice selesai.
    }

    // Method ini digunakan untuk mengakses keranjang customer.
    public Keranjang getKeranjang() {
        return keranjang;
    }

    // Method ini digunakan untuk mengakses daftar transaksi yang sudah selesai.
    public ArrayList<Invoice> getInvoiceSelesai() {
        return invoiceSelesai;
    }

    @Override
    public void menu() {
        System.out.println("+----------------------------------------+");
        System.out.println("|              Menu Customer             |");
        System.out.println("+----------------------------------------+");
        System.out.println("| 1 | Lihat Daftar Barang                |");
        System.out.println("| 2 | Tambah Barang ke Keranjang         |");         
        System.out.println("| 3 | Lihat Keranjang                    |");
        System.out.println("| 4 | Checkout                           |");
        System.out.println("| 5 | Lihat Riwayat Transaksi            |");
        System.out.println("| 6 | Logout                             |");
        System.out.println("+----------------------------------------+");
    }
}
