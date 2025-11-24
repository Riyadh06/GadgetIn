import java.io.File;
import java.util.Scanner;

public class Main {
   public Main() {
   }

   // Helper: baca integer dalam rentang [min, max], ulangi sampai valid
   private static int readIntInRange(Scanner sc, int min, int max, String prompt) {
      int choice;
      while (true) {
         System.out.print(prompt);
         try {
            String line = sc.nextLine().trim();
            choice = Integer.parseInt(line);
            if (choice < min || choice > max) {
               System.out.println("Pilihan tidak valid. Masukkan angka antara " + min + " dan " + max + ".");
               continue;
            }
            return choice;
         } catch (NumberFormatException ex) {
            System.out.println("Input tidak valid. Silakan masukkan angka.");
         }
      }
   }

   public static void main(String[] args) {
      ListBarang listBarang = new ListBarang();
      String barangFile = "Database" + File.separator + "barang.txt";
      listBarang.setFilePath(barangFile);
      // perbaikan: loadFromFile tanpa argumen
      listBarang.loadFromFile();

      LoginManager loginManager = new LoginManager();
      String akunFile = "Database" + File.separator + "akun.txt";
      loginManager.loadFromFile(akunFile);

      Admin admin = loginManager.getAnyAdmin();
      if (admin == null) {
         admin = (Admin) loginManager.register("admin", "admin", "admin123", akunFile);
         if (admin == null) {
            admin = new Admin("A1", "admin", "admin123");
            loginManager.addAkun(admin);
         }
      }

      Customer customer = loginManager.getAnyCustomer();
      if (customer == null) {
         customer = (Customer) loginManager.register("customer", "customer", "customer123", akunFile);
         if (customer == null) {
            customer = new Customer("C1", "customer", "customer123");
            loginManager.addAkun(customer);
         }
      }

      AdminDriver adminDriver = new AdminDriver(admin, listBarang);
      Scanner sc = new Scanner(System.in);

      while(true) {
          
        System.out.println("+--------------------+");
        System.out.println("|      Menu Utama    |");
        System.out.println("+--------------------+");
        System.out.println("| 1 | Registrasi     |");
        System.out.println("| 2 | Login          |");
        System.out.println("| 3 | Keluar         |");
        System.out.println("+--------------------+");
         int menu = readIntInRange(sc, 1, 3, "Pilih: ");
         if (menu == 1) {
            System.out.println("--- Registrasi Akun ---");
            System.out.println("Pilih role: 1. Admin  2. Customer");
            int roleChoice = readIntInRange(sc, 1, 2, "Pilihan: ");
            String role = roleChoice == 1 ? "admin" : "customer";
            System.out.print("Username: ");
            String username = sc.nextLine();
            if (loginManager.hasUsername(username)) {
               System.out.println("Username sudah digunakan. Silakan pilih username lain.");
            } else {
               System.out.print("Password: ");
               String password = sc.nextLine();
               Akun newAkun = loginManager.register(role, username, password, akunFile);
               if (newAkun != null) {
                  System.out.println("Registrasi berhasil. Anda dapat login sekarang.");
               } else {
                  System.out.println("Registrasi gagal.");
               }
            }
         } else if (menu == 2) {
            System.out.print("Username: ");
            String username = sc.nextLine();
            System.out.print("Password: ");
            String password = sc.nextLine();
            Akun akun = loginManager.authenticate(username, password);
            if (akun == null) {
               System.out.println("Username atau password salah.");
            } else {
               System.out.println("Login berhasil. Selamat datang di GadgetIn, " + akun.getUsername() + "!"+ "\nTemukan Gadget Impianmu di sini.");
               if (akun instanceof Admin) {
                  AdminDriver adminMenu = new AdminDriver((Admin) akun, listBarang);
                  adminMenu.handleMenu();
               } else if (akun instanceof Customer) {
                  CustomerDriver customerMenu = new CustomerDriver((Customer) akun, listBarang, adminDriver);
                  customerMenu.handleMenu();
               } else {
                  System.out.println("Tipe akun tidak dikenali.");
               }
               System.out.println("Kembali ke menu login.");
            }
         } else { // menu == 3
            System.out.println("Terima kasih! Jangan lupa kembali lagi.");
            sc.close();
            return;
         }
      }
   }
}
