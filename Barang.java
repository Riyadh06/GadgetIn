// Kelas Barang merepresentasikan barang dengan atribut ID, nama, harga, stok, deskripsi, brand.
public class Barang {
    private String id;
    private String nama;
    private double harga;
    private int stok;
    

    // Konstruktor lengkap (tanpa deskripsi/brand)
    public Barang(String id, String nama, double harga, int stok) {
        this.id = id == null ? "" : id;
        this.nama = nama == null ? "" : nama;
        this.harga = harga;
        this.stok = stok;
    }

    // Konstruktor ringkas (kompatibel dengan format lama file yang hanya 3 kolom)
    public Barang(String id, String nama, double harga) {
        this(id, nama, harga, 0);
    }

    // Getter untuk mendapatkan ID barang
    public String getId() {
        return id;
    }

    // Getter untuk mendapatkan nama barang
    public String getNama() {
        return nama;
    }

    // Getter untuk mendapatkan harga barang
    public double getHarga() {
        return harga;
    }

    // (deskripsi/brand telah dihapus)

    // Getter stok
    public int getStok() {
        return stok;
    }

    // Setter stok
    public void setStok(int stok) {
        this.stok = stok;
    }

    // Setter nama
    public void setNama(String nama) {
        this.nama = nama;
    }

    // (deskripsi/brand setters dihapus)

    // Setter harga
    public void setHarga(double harga) {
        this.harga = harga;
    }

    // Override toString
    @Override
    public String toString() {
        return "ID: " + id + ", Nama: " + nama + ", Harga: Rp " + harga + ", Stok: " + stok;
    }

    // Parse satu baris dari file (mendukung format CSV lama dan format lengkap)
    public static Barang fromFileString(String line) {
        if (line == null) return null;
        String l = line.trim();
        if (l.isEmpty()) return null;

        // coba parse sebagai CSV (koma) terlebih dahulu, lalu tab, lalu pipe
        String[] parts = l.split(",", -1);
        if (parts.length < 3) {
            if (l.contains("\t")) parts = l.split("\t", -1);
            else if (l.contains("|")) parts = l.split("\\|", -1);
        }

        // trim semua bagian
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }

        try {
            if (parts.length >= 4) {
                String id = unescape(parts[0]);
                String nama = unescape(parts[1]);
                double harga = Double.parseDouble(parts[2]);
                int stok = Integer.parseInt(parts[3]);
                return new Barang(id, nama, harga, stok);
            } else if (parts.length >= 3) {
                String id = unescape(parts[0]);
                String nama = unescape(parts[1]);
                double harga = Double.parseDouble(parts[2]);
                return new Barang(id, nama, harga); // stok default
            } else {
                return null;
            }
        } catch (Exception e) {
            // parsing gagal -> kembalikan null
            return null;
        }
    }

    // Utility untuk membersihkan field dari newline/tab dan trimming
    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\t", " ").replace("\r", " ").replace("\n", " ").trim();
    }
}
