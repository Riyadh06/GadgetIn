import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Invoice {
    private Transaksi transaksi;
    private Pembayaran pembayaran;
    private String status;  // Status: Pending atau Approved

    private static final String INVOICE_FILE = "Database" + File.separator + "Invoice.txt";

    private static final int W_ID = 8;
    private static final int W_NAME = 20;
    private static final int W_PRICE = 12;
    private static final int W_QTY = 5;
    private static final int W_SUB = 12;
    

    public Invoice(Transaksi transaksi, Pembayaran pembayaran) {
        this(transaksi, pembayaran, "Pending");
    }

    public Invoice(Transaksi transaksi, Pembayaran pembayaran, String status) {
        this.transaksi = transaksi;
        this.pembayaran = pembayaran;
        this.status = (status != null) ? status : "Pending";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("========== INVOICE ==========\n");
        sb.append("Transaksi ID : ").append(transaksi.getId()).append('\n');
        sb.append("Customer     : ").append(transaksi.getCustomer() != null ? transaksi.getCustomer().getUsername() : "unknown").append('\n');
        sb.append("Waktu        : ").append(df.format(new Date(transaksi.getTimestamp()))).append("\n\n");

        String headerFmt = "%-" + W_ID + "s  %-"+ W_NAME + "s  %" + W_PRICE + "s  %" + W_QTY + "s  %" + W_SUB + "s%n";
        String rowFmt    = "%-" + W_ID + "s  %-"+ W_NAME + "s  %" + W_PRICE + ".2f  %" + W_QTY + "d  %" + W_SUB + ".2f%n";

        int lineLen = W_ID + 2 + W_NAME + 2 + W_PRICE + 2 + W_QTY + 2 + W_SUB;
        sb.append(String.format(headerFmt, "ID", "Nama Barang", "Harga", "Qty", "Subtotal"));
        sb.append(String.join("", Collections.nCopies(lineLen/1, "-"))).append("\n");

        // aggregate by id
        List<Barang> items = transaksi.getBarangList();
        Map<String, Integer> qtyMap = new LinkedHashMap<>();
        Map<String, Barang> rep = new HashMap<>();
        for (Barang b : items) {
            if (b == null) continue;
            qtyMap.put(b.getId(), qtyMap.getOrDefault(b.getId(), 0) + 1);
            rep.putIfAbsent(b.getId(), b);
        }

        double total = 0.0;
        for (Map.Entry<String,Integer> e : qtyMap.entrySet()) {
            String id = e.getKey();
            int qty = e.getValue();
            Barang b = rep.get(id);
            double price = b != null ? b.getHarga() : 0.0;
            double sub = price * qty;
            total += sub;
                sb.append(String.format(rowFmt,
                    id,
                    truncate(b != null ? b.getNama() : "", W_NAME),
                    price,
                    qty,
                    sub
                ));
        }

        sb.append(String.join("", Collections.nCopies(lineLen/1, "-"))).append("\n");
        sb.append(String.format("%-" + (W_ID+W_NAME+2) + "s  %" + (W_PRICE+W_QTY+W_SUB) + ".2f%n", "TOTAL:", total));
        sb.append("\nMetode Pembayaran: ").append(pembayaran != null ? pembayaran.toString() : "null").append("\n");
        sb.append("Status Pemesanan : ").append(status).append("\n");
        sb.append("=============================\n");

        // write to file (append) into Database/Invoice.txt
        try {
            File f = new File(INVOICE_FILE);
            File parentDir = f.getParentFile();
            if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
                bw.write(sb.toString());
                bw.newLine();
            }
        } catch (IOException ex) {
            // jika gagal menulis, tetap return invoice string
        }

        return sb.toString();
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        String t = s.replace("\r", " ").replace("\n", " ").trim();
        if (t.length() <= max) return t;
        return t.substring(0, Math.max(0, max - 3)) + "...";
    }
}

