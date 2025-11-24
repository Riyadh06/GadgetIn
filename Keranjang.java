import java.util.*;

public class Keranjang {
    private final List<CartItem> items = new ArrayList<>();

    // tambah barang dengan qty (jika sudah ada, tambah qty)
    public void addBarang(Barang b, int qty) {
        if (b == null || qty <= 0) return;
        CartItem found = findById(b.getId());
        if (found != null) {
            found.addQty(qty);
        } else {
            items.add(new CartItem(b, qty));
        }
    }

    // kompatibilitas: tambah 1 unit
    public void addBarang(Barang b) { addBarang(b, 1); }

    public List<CartItem> getItems() { return Collections.unmodifiableList(items); }

    // kembalikan daftar Barang (flatten) - kompatibilitas lama
    public List<Barang> getBarang() {
        List<Barang> flat = new ArrayList<>();
        for (CartItem it : items) {
            for (int i = 0; i < it.getQty(); i++) flat.add(it.getBarang());
        }
        return flat;
    }

    public void clear() { items.clear(); }

    public boolean removeById(String id) {
        CartItem f = findById(id);
        if (f != null) {
            items.remove(f);
            return true;
        }
        return false;
    }

    // hapus sejumlah qty; jika qty >= item.qty maka hapus item
    public boolean removeQuantity(String id, int qty) {
        if (qty <= 0) return false;
        CartItem f = findById(id);
        if (f == null) return false;
        if (qty >= f.getQty()) {
            items.remove(f);
        } else {
            f.setQty(f.getQty() - qty);
        }
        return true;
    }

    private CartItem findById(String id) {
        if (id == null) return null;
        for (CartItem it : items) if (id.equals(it.getBarang().getId())) return it;
        return null;
    }
}
