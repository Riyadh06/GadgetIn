public class CartItem {
    private Barang barang;
    private int qty;

    public CartItem(Barang barang, int qty) {
        this.barang = barang;
        this.qty = Math.max(0, qty);
    }

    public Barang getBarang() { return barang; }
    public int getQty() { return qty; }
    public void setQty(int qty) { this.qty = Math.max(0, qty); }
    public void addQty(int delta) { this.qty = Math.max(0, this.qty + delta); }

    @Override
    public String toString() {
        return barang.getId() + " x" + qty + " (" + barang.getNama() + ")";
    }
}
