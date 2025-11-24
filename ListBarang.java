import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListBarang {
    private List<Barang> barangList = new ArrayList<>();
    private String basepath = "Database";
    private String filePath = basepath + File.separator + "barang.txt";

    public ListBarang() {}

    public void setFilePath(String path) {
        if (path == null || path.trim().isEmpty()) return;
        // if the provided path looks like a plain filename, place it under Database/
        File p = new File(path);
        if (p.isAbsolute() || path.contains(File.separator) || path.contains("/") || path.contains("\\")) {
            filePath = path;
        } else {
            filePath = basepath + File.separator + path;
        }
    }

    public List<Barang> getBarangList() {
        loadFromFile();
        return Collections.unmodifiableList(barangList);
    }

    public Barang getBarang(String id) {
        loadFromFile();
        if (id == null) return null;
        for (Barang b : barangList) if (id.equals(b.getId())) return b;
        return null;
    }

    public String generateId() {
        loadFromFile();
        int max = 0;
        for (Barang b : barangList) {
            String id = b.getId();
            try {
                String digits = id.replaceAll("\\D+", "");
                if (!digits.isEmpty()) {
                    int v = Integer.parseInt(digits);
                    if (v > max) max = v;
                }
            } catch (Exception ignored) {}
        }
        return String.valueOf(max + 1);
    }

    public void loadFromFile() {
        barangList.clear();
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                System.out.println("Gagal membuat file barang: " + e.getMessage());
                return;
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                Barang b = Barang.fromFileString(line);
                if (b != null) barangList.add(b);
            }
        } catch (IOException e) {
            System.out.println("Gagal membaca file barang: " + e.getMessage());
        }
    }

    public void saveToFile() {
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {
            for (Barang b : barangList) {
                String line = b.getId() + "," + escape(b.getNama()) + "," + b.getHarga()
                        + "," + b.getStok();
                bw.write(line);
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            System.out.println("Gagal menyimpan barang: " + e.getMessage());
        }
    }

    public void addBarang(Barang b) {
        if (b == null) return;
        loadFromFile();
        barangList.add(b);
        saveToFile();
    }

    public boolean removeBarang(String id) {
        loadFromFile();
        Barang target = getBarang(id);
        if (target != null) {
            barangList.remove(target);
            saveToFile();
            return true;
        }
        return false;
    }

    public boolean editHarga(String id, double newHarga) {
        loadFromFile();
        Barang target = getBarang(id);
        if (target != null) {
            target.setHarga(newHarga);
            saveToFile();
            return true;
        }
        return false;
    }

    public boolean editBarangFull(String id, String nama, double harga, int stok) {
        loadFromFile();
        Barang target = getBarang(id);
        if (target != null) {
            target.setNama(nama);
            target.setHarga(harga);
            target.setStok(stok);
            saveToFile();
            return true;
        }
        return false;
    }

    // New: kurangi stok barang sebanyak qty, simpan perubahan ke file
    public boolean reduceStock(String id, int qty) {
        if (id == null || qty <= 0) return false;
        loadFromFile();
        for (Barang b : barangList) {
            if (id.equals(b.getId())) {
                int cur = b.getStok();
                int updated = Math.max(0, cur - qty);
                b.setStok(updated);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace(",", " ").replace("\r", " ").replace("\n", " ").trim();
    }
}
