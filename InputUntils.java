import java.util.Scanner;

public class InputUtils {
    // Baca integer dalam rentang [min, max], ulangi sampai valid
    public static int readIntInRange(Scanner sc, int min, int max, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            if (line == null || line.trim().isEmpty()) {
                System.out.println("Input kosong. Silakan coba lagi.");
                continue;
            }
            line = line.trim();
            try {
                long maybe = Long.parseLong(line);
                if (maybe < min || maybe > max) {
                    System.out.println("Pilihan tidak valid. Masukkan angka antara " + min + " dan " + max + ".");
                    continue;
                }
                return (int) maybe;
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Silakan masukkan angka.");
            }
        }
    }

    // Baca integer positif (untuk jumlah), ulangi sampai valid
    public static int readPositiveInt(Scanner sc, String prompt) {
        return readIntInRange(sc, 1, Integer.MAX_VALUE, prompt);
    }

    // Baca double (mis. untuk harga), ulangi sampai valid
    public static double readDouble(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine();
            if (line == null || line.trim().isEmpty()) {
                System.out.println("Input kosong. Silakan coba lagi.");
                continue;
            }
            line = line.trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Silakan masukkan angka (contoh: 12500 atau 12500.50).");
            }
        }
    }
}
