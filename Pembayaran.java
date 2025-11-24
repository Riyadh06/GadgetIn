import java.util.Scanner;
import java.util.UUID;

// Kelas abstrak ini merepresentasikan metode pembayaran secara umum.
public abstract class Pembayaran {
    protected String id;

    public Pembayaran(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " - ID: " + id;
    }

    // Utility interaktif: tampilkan pilihan metode pembayaran, panggil handler yang sesuai,
    // dan kembalikan objek Pembayaran yang dipilih (atau null jika batal/tidak valid)
    public static Pembayaran pilihMetodePembayaran(String customerId) {
        System.out.println("Pilih Metode Pembayaran:");
        System.out.println("1. QRIS");
        System.out.println("2. Bank Transfer");
        System.out.println("3. COD (Cash on Delivery)");

        Scanner scanner = new Scanner(System.in);
        int pilihan;
        try {
            pilihan = Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception ex) {
            System.out.println("Pilihan tidak valid.");
            return null;
        }

        switch (pilihan) {
            case 1: {
                QRIS q = new QRIS();
                q.bayarQRIS(customerId);
                return q;
            }
            case 2: {
                Bank b = new Bank();
                b.bayarBankTransfer(customerId);
                return b;
            }
            case 3: {
                COD c = new COD();
                c.bayarCOD(customerId);
                return c;
            }
            default:
                System.out.println("Pilihan tidak valid.");
                return null;
        }
    }
}

// Kelas QRIS merupakan subclass dari Pembayaran, yang merepresentasikan metode pembayaran QRIS.
class QRIS extends Pembayaran {
    public QRIS() {
        super("QRIS" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    // tampilkan instruksi pembayaran via QRIS
    public void bayarQRIS(String customerId) {
        String kodeQR = "QRIS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("Metode Pembayaran: QRIS");
        System.out.println("Kode QR untuk Pembayaran: " + kodeQR);
        System.out.println("Silakan lakukan pembayaran dengan menggunakan QRIS dan konfirmasi setelah pembayaran.");
    }
}

// Kelas Bank merupakan subclass dari Pembayaran, yang merepresentasikan metode pembayaran melalui bank.
class Bank extends Pembayaran {
    public Bank() {
        super("BANK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    // tampilkan instruksi pembayaran via bank transfer
    public void bayarBankTransfer(String customerId) {
        String nomorRekening = "123-456-7890";
        String namaBank = "Bank ABC";
        System.out.println("Metode Pembayaran: Bank Transfer");
        System.out.println("Silakan transfer ke nomor rekening berikut:");
        System.out.println("Nama Bank: " + namaBank);
        System.out.println("Nomor Rekening: " + nomorRekening);
        System.out.println("Konfirmasi setelah transfer dilakukan.");
    }
}

// Kelas COD merupakan subclass dari Pembayaran, yang merepresentasikan metode pembayaran tunai saat pengiriman.
class COD extends Pembayaran {
    public COD() {
        super("COD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    }

    // tampilkan instruksi pembayaran via COD
    public void bayarCOD(String customerId) {
        System.out.println("Metode Pembayaran: COD (Cash on Delivery)");
        System.out.println("Anda dapat membayar langsung kepada kurir saat barang diterima.");
    }
}

