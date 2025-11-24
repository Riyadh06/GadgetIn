import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Transaksi {
    private String id;
    private Customer customer;
    private List<Barang> barangList;
    private double total;
    private long timestamp;

    // Konstruktor baru: terima Customer + list Barang
    public Transaksi(Customer customer, List<Barang> items) {
        this.id = UUID.randomUUID().toString();
        this.customer = customer;
        this.barangList = (items == null) ? new ArrayList<>() : new ArrayList<>(items);
        this.timestamp = System.currentTimeMillis();
        this.total = calculateTotal();
    }

    // Jika ada konstruktor lain di project, biarkan tetap ada (overload)
    // ...existing code...

    private double calculateTotal() {
        double sum = 0.0;
        for (Barang b : barangList) {
            if (b != null) sum += b.getHarga();
        }
        return sum;
    }

    // getters
    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<Barang> getBarangList() { return new ArrayList<>(barangList); }
    public double getTotal() { return total; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "Transaksi{id=" + id + ", customer=" + (customer != null ? customer.getUsername() : "null")
                + ", items=" + barangList.size() + ", total=" + total + "}";
    }
}

