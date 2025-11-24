public abstract class Akun {
    protected String id;
    protected String username;
    protected String password;

    public Akun(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Method abstract menu yang harus diimplementasikan oleh kelas turunan.
    // Berfungsi untuk menampilkan atau menangani logika menu berdasarkan jenis akun.
    public abstract void menu();
}
