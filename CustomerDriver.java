import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

public class CustomerDriver extends Driver {
    private ListBarang listBarang;
    private AdminDriver adminDriver;

    // Kolom lebar untuk tabel
    private static final int W_ID = 8;
    private static final int W_NAME = 20;
    private static final int W_PRICE = 15;
    private static final int W_STOCK = 5;   
    

    public CustomerDriver(Customer customer, ListBarang listBarang, AdminDriver adminDriver) {
        super(customer);
        this.listBarang = listBarang;
        this.adminDriver = adminDriver;
    }

    private static String clean(String s) {
        if (s == null) return "";
        return s.replace("\r", " ").replace("\n", " ").replace("\t", " ").trim();
    }

    private static String truncate(String s, int max) {
        s = clean(s);
        if (s.length() <= max) return s;
        return s.substring(0, Math.max(0, max - 3)) + "...";
    }

    private static void printSeparator() {
        int[] widths = {W_ID, W_NAME, W_PRICE, W_STOCK};
        StringBuilder sb = new StringBuilder();
        for (int w : widths) {
            sb.append("+");
            for (int i = 0; i < w + 2; i++) sb.append("-");
        }
        sb.append("+");
        System.out.println(sb.toString());
    }

    private static void printHeader() {
        printSeparator();
        // ID left, Name left, Price right, Stok right
        String fmt = "| %-" + W_ID + "s | %-" + W_NAME + "s | %" + W_PRICE + "s | %" + W_STOCK + "s |%n";
        System.out.printf(fmt, "ID", "Nama Barang", "Harga", "Stok");
        printSeparator();
    }

    // Format baris tabel: harga sebagai double (%.2f) dan stok sebagai integer (right aligned)
    private static void printRow(String id, String name, double price, int stock) {
        String fmt = "| %-" + W_ID + "s | %-" + W_NAME + "s | %" + W_PRICE + ".2f | %" + W_STOCK + "d |%n";
        System.out.printf(fmt, id, name, price, stock);
    }

    @Override
    public void handleMenu() {
        Scanner sc = new Scanner(System.in);
        Customer customer = (Customer) akun;

        while (true) {
            customer.menu();
            int choice = InputUtils.readIntInRange(sc, 1, 6, "Pilih: ");

            switch (choice) {
                case 1:
                    System.out.println("Daftar Barang:");
                    printHeader();
                    for (Barang barang : listBarang.getBarangList()) {
                        printRow(
                            truncate(barang.getId(), W_ID),
                            truncate(barang.getNama(), W_NAME),
                            barang.getHarga(),
                            barang.getStok()
                        );
                    }
                    printSeparator();
                    break;

                case 2:
                    System.out.print("ID Barang: ");
                    String idBarang = sc.nextLine().trim();
                    Barang barang = listBarang.getBarang(idBarang);
                    if (barang == null) {
                        System.out.println("Barang tidak ditemukan.");
                        break;
                    }
                    int available = barang.getStok();
                    if (available <= 0) {
                        System.out.println("Stok barang habis.");
                        break;
                    }
                    int qty = InputUtils.readIntInRange(sc, 1, available, "Jumlah yang ingin dibeli (max " + available + "): ");
                    customer.getKeranjang().addBarang(barang, qty);
                    System.out.println(qty + " unit '" + barang.getNama() + "' berhasil ditambahkan ke keranjang.");
                    break;

                case 3:
                    System.out.println("Keranjang:");
                    List<CartItem> cartItems = customer.getKeranjang().getItems();
                    if (cartItems == null || cartItems.isEmpty()) {
                        System.out.println("Keranjang kosong.");
                    } else {
                        printHeader();
                        for (CartItem ci : cartItems) {
                            Barang b = ci.getBarang();
                            int q = ci.getQty();
                            printRow(
                                truncate(b.getId(), W_ID),
                                truncate(b.getNama(), W_NAME),
                                b.getHarga(),
                                q
                            );
                        }
                        printSeparator();
                    }
                    break;

                case 4:
                    List<CartItem> keranjangItems = customer.getKeranjang().getItems();
                    if (keranjangItems == null || keranjangItems.isEmpty()) {
                        System.out.println("Keranjang kosong.");
                        break;
                    }

                    System.out.println("Keranjang Anda:");
                    printHeader();
                    for (CartItem ci : keranjangItems) {
                        Barang b = ci.getBarang();
                        int q = ci.getQty();
                        printRow(
                            truncate(b.getId(), W_ID),
                            truncate(b.getNama(), W_NAME),
                            b.getHarga(),
                            q
                        );
                    }
                    printSeparator();

                    System.out.println("Masukkan ID barang yang ingin di-checkout (pisah koma), atau ketik 'Semua' untuk semua:");
                    System.out.print("Pilihan: ");
                    String line = sc.nextLine().trim();
                    Set<String> selectedIds = new HashSet<>();
                    List<CartItem> selectedCartItems = new ArrayList<>();

                    if (line.equalsIgnoreCase("Semua")) {
                        selectedCartItems.addAll(keranjangItems);
                        selectedIds.addAll(keranjangItems.stream().map(ci -> ci.getBarang().getId()).collect(Collectors.toSet()));
                    } else {
                        String[] parts = line.split(",");
                        for (String p : parts) {
                            String id = p.trim();
                            if (id.isEmpty()) continue;
                            Optional<CartItem> opt = keranjangItems.stream().filter(ci -> id.equals(ci.getBarang().getId())).findFirst();
                            if (opt.isPresent()) {
                                if (!selectedIds.contains(id)) {
                                    selectedIds.add(id);
                                    selectedCartItems.add(opt.get());
                                }
                            } else {
                                System.out.println("ID tidak ditemukan di keranjang: " + id);
                            }
                        }
                    }

                    if (selectedCartItems.isEmpty()) {
                        System.out.println("Tidak ada barang yang dipilih untuk checkout.");
                        break;
                    }

                    Map<String, Integer> qtyToCheckout = new HashMap<>();
                    for (CartItem ci : selectedCartItems) {
                        String id = ci.getBarang().getId();
                        int availableInCart = ci.getQty();
                        int chosen = InputUtils.readIntInRange(sc, 1, availableInCart,
                                "Masukkan jumlah yang ingin di-checkout untuk ID " + id + " (max " + availableInCart + "): ");
                        qtyToCheckout.put(id, chosen);
                    }

                    // Tampilkan menu pembayaran (menggunakan helper di Pembayaran) dan ambil objek pembayaran
                    Pembayaran pembayaran = Pembayaran.pilihMetodePembayaran(customer.getId());
                    
                    if (pembayaran == null) {
                        System.out.println("Pembayaran dibatalkan.");
                        break;
                    }

                    List<Barang> itemsForTransaction = new ArrayList<>();
                    for (CartItem ci : selectedCartItems) {
                        String id = ci.getBarang().getId();
                        int q = qtyToCheckout.getOrDefault(id, 0);
                        for (int i = 0; i < q; i++) itemsForTransaction.add(ci.getBarang());
                    }

                    // Buat transaksi & invoice (pembayaran dipilih di langkah sebelumnya)
                    Transaksi transaksi = new Transaksi(customer, itemsForTransaction);
                    Invoice invoice = new Invoice(transaksi, pembayaran);
                    customer.getInvoiceSelesai().add(invoice);
                    // Simpan invoice ke file tanpa menampilkannya
                    invoice.toString();

                    // Simpan entri transaksi ke Database/transaksi.txt
                   

                    for (Map.Entry<String, Integer> e : qtyToCheckout.entrySet()) {
                        String id = e.getKey();
                        int q = e.getValue();
                        boolean ok = listBarang.reduceStock(id, q);
                        if (!ok) System.out.println("Peringatan: gagal mengurangi stok untuk ID " + id);
                    }

                    for (CartItem ci : selectedCartItems) {
                        String id = ci.getBarang().getId();
                        int q = qtyToCheckout.getOrDefault(id, 0);
                        if (q >= ci.getQty()) {
                            customer.getKeranjang().removeById(id);
                        } else {
                            customer.getKeranjang().removeQuantity(id, q);
                        }
                    }

                    int totalUnits = itemsForTransaction.size();
                    System.out.println("Checkout berhasil untuk " + totalUnits + " unit dari " + selectedCartItems.size() + " item.");
                    break;

                case 5:
                    // Tampilkan riwayat belanja (Invoice) dari file Invoice.txt
                    System.out.println("Riwayat Belanja (Invoice):");
                    File invoiceFile = new File("Database" + File.separator + "Invoice.txt");
                    
                    if (!invoiceFile.exists()) {
                        System.out.println("Belum ada riwayat belanja.");
                    } else {
                        String username = customer.getUsername();
                        try (BufferedReader br = new BufferedReader(new FileReader(invoiceFile))) {
                            String lineInv;
                            boolean found = false;
                            StringBuilder currentInvoice = new StringBuilder();
                            boolean isCustomerInvoice = false;
                            
                            while ((lineInv = br.readLine()) != null) {
                                if (lineInv.startsWith("========== INVOICE ==========")) {
                                    // Jika ada invoice sebelumnya, tampilkan jika milik customer
                                    if (isCustomerInvoice && currentInvoice.length() > 0) {
                                        System.out.println(currentInvoice.toString());
                                        found = true;
                                    }
                                    currentInvoice.setLength(0);
                                    isCustomerInvoice = false;
                                }
                                currentInvoice.append(lineInv).append("\n");
                                if (lineInv.startsWith("Customer     : ")) {
                                    String user = lineInv.substring("Customer     : ".length()).trim();
                                    if (user.equalsIgnoreCase(username)) {
                                        isCustomerInvoice = true;
                                    }
                                }
                            }
                            // Tampilkan invoice terakhir jika milik customer
                            if (isCustomerInvoice && currentInvoice.length() > 0) {
                                System.out.println(currentInvoice.toString());
                                found = true;
                            }
                            if (!found) {
                                System.out.println("Belum ada riwayat belanja.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Gagal membaca riwayat belanja: " + ex.getMessage());
                        }
                    }
                    break;

                case 6:
                    System.out.println("Logout...");
                    return;

                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
}
