import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class LoginManager {
    private ArrayList<Akun> akunList;

    public LoginManager() {
        this.akunList = new ArrayList<>();
    }

    public void addAkun(Akun akun) {
        akunList.add(akun);
    }

    public boolean hasUsername(String username) {
        for (Akun a : akunList) {
            if (a.getUsername().equals(username)) return true;
        }
        return false;
    }

    public Akun authenticate(String username, String password) {
        for (Akun a : akunList) {
            if (a.getUsername().equals(username) && a.getPassword().equals(password)) {
                return a;
            }
        }
        return null;
    }

    public Admin getAnyAdmin() {
        for (Akun a : akunList) {
            if (a instanceof Admin) return (Admin) a;
        }
        return null;
    }

    public Customer getAnyCustomer() {
        for (Akun a : akunList) {
            if (a instanceof Customer) return (Customer) a;
        }
        return null;
    }

    // Load akun from file with format: role,id,username,password
    public void loadFromFile(String filePath) {
        File f = new File(filePath);
        File parent = f.getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (Exception e) {
                System.out.println("Gagal membuat file akun: " + e.getMessage());
                return;
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length < 4) continue;
                String role = parts[0];
                String id = parts[1];
                String username = parts[2];
                String password = parts[3];
                if (hasUsername(username)) continue; // skip duplicates
                if (role.equalsIgnoreCase("admin")) {
                    addAkun(new Admin(id, username, password));
                } else if (role.equalsIgnoreCase("customer")) {
                    addAkun(new Customer(id, username, password));
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal membaca file akun: " + e.getMessage());
        }
    }

    // Register a new akun and append to file. role should be "admin" or "customer".
    public Akun register(String role, String username, String password, String filePath) {
        if (hasUsername(username)) {
            return null;
        }
        String id = generateIdForRole(role);
        Akun akun = null;
        if (role.equalsIgnoreCase("admin")) {
            akun = new Admin(id, username, password);
        } else if (role.equalsIgnoreCase("customer")) {
            akun = new Customer(id, username, password);
        } else {
            return null;
        }
        addAkun(akun);
        appendToFile(role, akun, filePath);
        return akun;
    }

    private String generateIdForRole(String role) {
        int count = 0;
        if (role.equalsIgnoreCase("admin")) {
            for (Akun a : akunList) if (a instanceof Admin) count++;
            return "A" + (count + 1);
        } else {
            for (Akun a : akunList) if (a instanceof Customer) count++;
            return "C" + (count + 1);
        }
    }

    private void appendToFile(String role, Akun akun, String filePath) {
        try {
            File f = new File(filePath);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
        } catch (Exception ignored) {}
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(role + "," + akun.getId() + "," + akun.getUsername() + "," + akun.getPassword());
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            System.out.println("Gagal menulis ke file akun: " + e.getMessage());
        }
    }
}

