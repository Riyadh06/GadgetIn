import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AdminDriver extends Driver {
    // Mengelola data barang dan transaksi
    private ListBarang listBarang;
    private ArrayList<String> listTransaksi;

    // Menginisialisasi admin dan daftar barang
    public AdminDriver(Admin admin, ListBarang listBarang) {
        super(admin);
        this.listBarang = listBarang;
        this.listTransaksi = new ArrayList<>();
    }

    // Menu utama admin
    @Override
    public void handleMenu() {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            displayMenu();
            int choice = InputUtils.readIntInRange(sc, 1, 6, "Pilih menu: ");

            switch (choice) {
                case 1:
                    tambahBarang(sc);
                    break;
                case 2:
                    editBarang(sc);
                    break;
                case 3:
                    lihatBarang();
                    break;
                case 4:
                    terimaPenjualan(sc);
                    break;
                case 5:
                    riwayatTransaksi();
                    break;
                case 6:
                    System.out.println("Logout...");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
    
    // Menambah barang baru
    private void tambahBarang(Scanner sc) {
        System.out.print("ID Barang (kosong untuk auto-generate): ");
        String idInput = sc.nextLine().trim();
        String id;
        while (true) {
            if (idInput.isEmpty()) {
                id = listBarang.generateId();
                break;
            }
            if (listBarang.getBarang(idInput) != null) {
                System.out.println("ID tersebut sudah ada di barang.txt. Masukkan ID lain atau kosong untuk auto-generate.");
                System.out.print("ID Barang (kosong untuk auto-generate): ");
                idInput = sc.nextLine().trim();
                continue;
            }
            id = idInput;
            break;
        }

        System.out.print("Nama Barang: ");
        String nama = sc.nextLine();
        double harga = InputUtils.readDouble(sc, "Harga: ");
        int stok = InputUtils.readPositiveInt(sc, "Stok: ");

        Barang baru = new Barang(id, nama, harga, stok);
        listBarang.addBarang(baru);
        System.out.println("Barang berhasil ditambahkan. ID = " + id);
    }
    
    // Mengedit harga barang
    private void editBarang(Scanner sc) {
        System.out.print("ID Barang: ");
        String idEdit = sc.nextLine();
        double hargaBaru = InputUtils.readDouble(sc, "Harga Baru: ");
        if (listBarang.editHarga(idEdit, hargaBaru)) {
            System.out.println("Harga barang berhasil diperbarui.");
        } else {
            System.out.println("Barang tidak ditemukan.");
        }
    }
    
    // Melihat daftar barang
    private void lihatBarang() {
        System.out.println("Daftar Barang:");
        System.out.println("+----------+----------------------+----------------------+-------+");
        System.out.printf("| %-8s | %-20s | %-20s | %-5s |\n", "ID", "Nama Barang", "Harga", "Stok");
        System.out.println("+----------+----------------------+----------------------+-------+");
        for (Barang barang : listBarang.getBarangList()) {
            System.out.printf("| %-8s | %-20s | %-20.2f | %-5d |\n",
                barang.getId(), barang.getNama(), barang.getHarga(), barang.getStok());
        }
        System.out.println("+----------+----------------------+----------------------+-------+");
    }

    // Menyimpan transaksi yang disetujui ke listTransaksi (simpan sebagai baris teks)
    public void approveTransaksi(String transaksiLine) {
        listTransaksi.add(transaksiLine);
        System.out.println("Transaksi diterima.");
    }

    // Melihat status pesanan customer dari Invoice.txt
    public void lihatStatusPesanan(String customerId) {
        File invoiceFile = new File("Database" + File.separator + "Invoice.txt");
        
        if (!invoiceFile.exists() || invoiceFile.length() == 0) {
            System.out.println("Tidak ada pesanan untuk dilihat.");
            return;
        }
        
        boolean adaPesanan = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(invoiceFile))) {
            String line;
            boolean isTargetCustomer = false;
            
            System.out.printf("%-20s %-15s %-10s %-15s %-10s%n", 
                "Nama Barang", "Harga Satuan", "Jumlah", "Total", "Status");
            System.out.printf("%-20s %-15s %-10s %-15s %-10s%n", 
                "--------------------", "---------------", "----------", "---------------", "----------");
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Customer     : ")) {
                    String customer = line.substring("Customer     : ".length()).trim();
                    isTargetCustomer = customer.equals(customerId);
                }
                
                if (isTargetCustomer) {
                    if (line.startsWith("Status Pemesanan : ")) {
                        String status = line.substring("Status Pemesanan : ".length()).trim();
                        adaPesanan = true;
                        // Parsing status
                        System.out.printf("%-20s %-15s %-10s %-15s %-10s%n", 
                            "[Invoice Found]", "---", "---", "---", status);
                    }
                }
                
                if (line.startsWith("=============================")) {
                    isTargetCustomer = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca status pesanan: " + e.getMessage());
            return;
        }
        
        if (!adaPesanan) {
            System.out.println("Tidak ada pesanan dengan ID pelanggan tersebut.");
        }
    }
    
    // Melihat history transaksi yang sudah disetujui dari Invoice.txt
    public void lihatHistory(String customerId) {
        File invoiceFile = new File("Database" + File.separator + "Invoice.txt");
        
        if (!invoiceFile.exists() || invoiceFile.length() == 0) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        
        System.out.println("History Transaksi Anda (Approved Only):");
        System.out.printf("%-20s %-15s %-10s %-15s %-10s%n", 
            "Nama Barang", "Harga Satuan", "Jumlah", "Total", "Status");
        System.out.printf("%-20s %-15s %-10s %-15s %-10s%n", 
            "--------------------", "---------------", "----------", "---------------", "----------");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(invoiceFile))) {
            String line;
            boolean isTargetCustomer = false;
            boolean isApproved = false;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Customer     : ")) {
                    String customer = line.substring("Customer     : ".length()).trim();
                    isTargetCustomer = customer.equals(customerId);
                }
                
                if (line.startsWith("Status Pemesanan : ")) {
                    String status = line.substring("Status Pemesanan : ".length()).trim();
                    isApproved = status.equals("Approved");
                }
                
                if (isTargetCustomer && isApproved && line.contains("Rp")) {
                    // Ini adalah baris item dalam invoice
                    System.out.println(line);
                }
                
                if (line.startsWith("=============================")) {
                    isTargetCustomer = false;
                    isApproved = false;
                }
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca history transaksi: " + e.getMessage());
        }
    }
    
    // Melihat riwayat transaksi keseluruhan (Admin)
    public void riwayatTransaksi() {
        File invoiceFile = new File("Database" + File.separator + "Invoice.txt");
        
        if (!invoiceFile.exists() || invoiceFile.length() == 0) {
            System.out.println("Belum ada transaksi.");
            return;
        }
        
        System.out.println("History Transaksi Keseluruhan:");
        System.out.println("================================================================================");
        System.out.printf("| %-10s | %-15s | %-15s | %-12s |%n", 
            "Customer", "Transaksi ID", "Waktu", "Status");
        System.out.println("================================================================================");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(invoiceFile))) {
            String line;
            String currentCustomer = "";
            String currentTxnId = "";
            String currentWaktu = "";
            String currentStatus;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Customer     : ")) {
                    currentCustomer = line.substring("Customer     : ".length()).trim();
                }
                if (line.startsWith("Transaksi ID : ")) {
                    currentTxnId = line.substring("Transaksi ID : ".length()).trim();
                }
                if (line.startsWith("Waktu        : ")) {
                    currentWaktu = line.substring("Waktu        : ".length()).trim();
                }
                if (line.startsWith("Status Pemesanan : ")) {
                    currentStatus = line.substring("Status Pemesanan : ".length()).trim();
                    System.out.printf("| %-10s | %-15s | %-15s | %-12s |%n", 
                        currentCustomer, currentTxnId, currentWaktu, currentStatus);
                }
            }
            System.out.println("================================================================================");
        } catch (IOException e) {
            System.out.println("Gagal membaca history transaksi: " + e.getMessage());
        }
    }
    
    // Terima/Setujui penjualan dari Invoice.txt
    public void terimaPenjualan(Scanner scanner) {
        File invoiceFile = new File("Database" + File.separator + "Invoice.txt");
        
        if (!invoiceFile.exists() || invoiceFile.length() == 0) {
            System.out.println("Belum ada transaksi yang perlu disetujui.");
            return;
        }
        
        List<String> invoiceLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(invoiceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                invoiceLines.add(line);
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca data transaksi: " + e.getMessage());
            return;
        }
        
        System.out.println("Daftar Transaksi Pending:");
        List<Integer> pendingIndexes = new ArrayList<>();
        int displayNum = 1;
        
        for (int i = 0; i < invoiceLines.size(); i++) {
            if (invoiceLines.get(i).startsWith("Status Pemesanan : ")) {
                String status = invoiceLines.get(i).substring("Status Pemesanan : ".length()).trim();
                if (status.equals("Pending")) {
                    // Cari customer ID untuk invoice ini
                    String customerName = "";
                    for (int j = i - 1; j >= 0; j--) {
                        if (invoiceLines.get(j).startsWith("Customer     : ")) {
                            customerName = invoiceLines.get(j).substring("Customer     : ".length()).trim();
                            break;
                        }
                    }
                    System.out.println(displayNum + ". Customer: " + customerName + " - Status: " + status);
                    pendingIndexes.add(i);
                    displayNum++;
                }
            }
        }
        
        if (pendingIndexes.isEmpty()) {
            System.out.println("Tidak ada transaksi dengan status Pending.");
            return;
        }
        
        System.out.print("Pilih nomor transaksi untuk disetujui (0 untuk batal): ");
        int pilihan = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        if (pilihan <= 0 || pilihan > pendingIndexes.size()) {
            System.out.println("Tidak ada transaksi yang dipilih.");
            return;
        }
        
        int selectedIndex = pendingIndexes.get(pilihan - 1);
        invoiceLines.set(selectedIndex, "Status Pemesanan : Approved");
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(invoiceFile))) {
            for (String invoiceLine : invoiceLines) {
                writer.println(invoiceLine);
            }
        } catch (IOException e) {
            System.out.println("Gagal menyimpan perubahan: " + e.getMessage());
            return;
        }
        
        System.out.println("Transaksi berhasil disetujui (Status diubah menjadi Approved).");
    }
}
