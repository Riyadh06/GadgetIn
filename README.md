# GadgetIn - E-Commerce Management System

**GadgetIn** adalah aplikasi e-commerce berbasis Java yang memungkinkan Admin mengelola inventaris barang dan Customer untuk berbelanja barang elektronik (gadget). Aplikasi ini dibangun menggunakan konsep **Object-Oriented Programming (OOP)** dengan penyimpanan data berbasis file teks.

---

## 📋 Table of Contents
- [Fitur Utama](#fitur-utama)
- [Arsitektur Sistem](#arsitektur-sistem)
- [Struktur Kelas](#struktur-kelas)
- [Panduan Penggunaan](#panduan-penggunaan)
  - [Admin](#admin)
  - [Customer](#customer)
- [File Penyimpanan Data](#file-penyimpanan-data)
- [Alur Sistem](#alur-sistem)
- [Teknologi yang Digunakan](#teknologi-yang-digunakan)
- [Cara Menjalankan](#cara-menjalankan)

---

## 🚀 Fitur Utama

### **Admin Features**
1. **Kelola Barang (CRUD)**
   - Tambah barang baru dengan ID unik (auto-generate atau manual)
   - Edit harga barang
   - Hapus barang dari katalog
   - Lihat daftar semua barang dengan stok

2. **Persetujuan Transaksi**
   - Lihat daftar transaksi yang berstatus "Pending"
   - Setujui transaksi (ubah status menjadi "Approved")
   - Tracking history transaksi

3. **Autentikasi & Manajemen Akun**
   - Login dengan username dan password
   - Registrasi akun admin baru

### **Customer Features**
1. **Browsing Barang**
   - Lihat daftar lengkap barang yang tersedia
   - Informasi: ID, Nama, Harga, Stok

2. **Keranjang Belanja**
   - Tambah barang ke keranjang
   - Lihat isi keranjang
   - Pilih barang untuk checkout

3. **Checkout & Pembayaran**
   - Pilih barang yang ingin dibayar
   - Tentukan jumlah pembelian
   - Pilih metode pembayaran:
     - **QRIS** (dengan kode QR unik)
     - **Bank Transfer** (dengan nomor rekening)
     - **COD** (Cash on Delivery)
   - Invoice disimpan otomatis ke file

4. **Riwayat Transaksi**
   - Lihat semua invoice yang telah dibuat
   - Informasi: Transaksi ID, Customer, Waktu, Barang, Metode Pembayaran, **Status Pemesanan** (Pending/Approved)

5. **Autentikasi & Manajemen Akun**
   - Login dengan username dan password
   - Registrasi akun customer baru

---

## 🏗️ Arsitektur Sistem

```
GadgetIn Project
├── Core Classes
│   ├── Akun (Abstract Base Class)
│   ├── Admin (extends Akun)
│   ├── Customer (extends Akun)
│   └── Driver (Abstract Base Class)
│       ├── AdminDriver
│       └── CustomerDriver
│
├── Business Logic
│   ├── Barang
│   ├── ListBarang
│   ├── CartItem
│   ├── Keranjang
│   ├── Transaksi
│   ├── Invoice
│   ├── Pembayaran (Abstract)
│   │   ├── QRIS
│   │   ├── Bank
│   │   └── COD
│   └── LoginManager
│
├── Utilities
│   └── InputUtils
│
├── Database
│   ├── akun.txt (User accounts)
│   ├── barang.txt (Product inventory)
│   ├── transaksi.txt (Transaction tracking)
│   └── Invoice.txt (Invoice records)
│
└── Main Entry Point
    └── Main.java
```

---

## 📦 Struktur Kelas

### **1. Akun (Abstract Class)**
Kelas dasar untuk semua pengguna aplikasi.
```java
public abstract class Akun {
    - id: String
    - username: String
    - password: String
    - abstract menu(): void
}
```

### **2. Admin (extends Akun)**
Mewakili pengguna dengan role Admin.
- Dapat mengelola barang (tambah, edit, hapus, lihat)
- Dapat menyetujui transaksi

### **3. Customer (extends Akun)**
Mewakili pengguna dengan role Customer.
```java
- keranjang: Keranjang
- invoiceSelesai: ArrayList<Invoice>
```

### **4. Barang**
Merepresentasikan produk gadget.
```java
- id: String (unik)
- nama: String
- harga: double
- stok: int
```

### **5. CartItem**
Item di dalam keranjang belanja.
```java
- barang: Barang
- qty: int (jumlah)
```

### **6. Keranjang**
Keranjang belanja untuk setiap customer.
- `addBarang(barang, qty)`: Tambah barang ke keranjang
- `removeById(id)`: Hapus barang dari keranjang
- `getItems()`: Dapatkan daftar item

### **7. Transaksi**
Merepresentasikan transaksi pembelian.
```java
- id: String (UUID)
- customer: Customer
- barangList: List<Barang>
- total: double
- timestamp: long
```

### **8. Invoice (extends Pembayaran)**
Invoice untuk transaksi yang sudah dikonfirmasi.
```java
- transaksi: Transaksi
- pembayaran: Pembayaran
- status: String (Pending/Approved)
- Disimpan ke file Invoice.txt
```

### **9. Pembayaran (Abstract)**
Kelas dasar untuk metode pembayaran.

**Subclasses:**
- **QRIS**: Pembayaran via QRIS dengan kode QR unik
- **Bank**: Pembayaran via Transfer Bank dengan nomor rekening
- **COD**: Pembayaran saat barang diterima (Cash on Delivery)

### **10. LoginManager**
Mengelola autentikasi dan registrasi pengguna.
- `authenticate(username, password)`: Login
- `register(role, username, password, file)`: Registrasi
- `loadFromFile(filepath)`: Muat data akun dari file
- `saveToFile(filepath)`: Simpan data akun ke file

### **11. ListBarang**
Manajemen katalog barang.
- `addBarang(barang)`: Tambah barang
- `removeBarang(id)`: Hapus barang
- `editHarga(id, hargaBaru)`: Edit harga barang
- `reduceStock(id, qty)`: Kurangi stok barang
- `loadFromFile()`: Muat dari database
- `saveToFile()`: Simpan ke database

### **12. AdminDriver & CustomerDriver**
Menangani menu dan navigasi untuk Admin dan Customer.

---

## 💻 Panduan Penggunaan

### **Admin**

#### **Menu 1 - Tambah Barang**
```
Pilih: 1
ID Barang (kosong untuk auto-generate): 
Nama Barang: iPhone 15 Pro
Harga: 1500000
Stok: 10
✓ Barang berhasil ditambahkan. ID = GADG001
```

#### **Menu 2 - Hapus Barang**
```
Pilih: 2
ID Barang: GADG001
✓ Barang berhasil dihapus.
```

#### **Menu 3 - Edit Harga Barang**
```
Pilih: 3
ID Barang: GADG002
Harga Baru: 1200000
✓ Harga barang berhasil diperbarui.
```

#### **Menu 4 - Lihat Daftar Barang**
```
Pilih: 4
Daftar Barang:
+----------+----------------------+---------+-------+
| ID       | Nama Barang          |  Harga  | Stok  |
+----------+----------------------+---------+-------+
| GADG001  | iPhone 15 Pro        | 1500000 |  10   |
| GADG002  | Samsung Galaxy S24   | 1200000 |   5   |
+----------+----------------------+---------+-------+
```

#### **Menu 5 - Persetujuan Transaksi**
```
Pilih: 5
Daftar Transaksi Pending:
1. C2;Iphone;1000000.0;1;1000000.0;Pending
2. C3;Samsung;1200000.0;2;2400000.0;Pending

Pilih nomor transaksi untuk disetujui (0 batal): 1
✓ Transaksi diterima.
```

#### **Menu 6 - Logout**
Kembali ke menu utama dan disconnect.

---

### **Customer**

#### **Menu 1 - Lihat Daftar Barang**
```
Pilih: 1
Daftar Barang:
+----------+----------------------+----------+-------+
| ID       | Nama Barang          |  Harga   | Stok  |
+----------+----------------------+----------+-------+
| GADG001  | iPhone 15 Pro        | 1500000  |  10   |
| GADG002  | Samsung Galaxy S24   | 1200000  |   5   |
+----------+----------------------+----------+-------+
```

#### **Menu 2 - Tambah ke Keranjang**
```
Pilih: 2
ID Barang: GADG001
Jumlah yang ingin dibeli (max 10): 2
✓ 2 unit 'iPhone 15 Pro' berhasil ditambahkan ke keranjang.
```

#### **Menu 3 - Lihat Keranjang**
```
Pilih: 3
Keranjang:
+----------+----------------------+----------+-------+
| ID       | Nama Barang          |  Harga   | Qty   |
+----------+----------------------+----------+-------+
| GADG001  | iPhone 15 Pro        | 1500000  |   2   |
+----------+----------------------+----------+-------+
```

#### **Menu 4 - Checkout & Pembayaran**
```
Pilih: 4
Keranjang Anda:
+----------+----------------------+---------+-------+
| ID       | Nama Barang          |  Harga  | Stok  |
+----------+----------------------+---------+-------+
| GADG001  | iPhone 15 Pro        | 1500000 |   2   |
+----------+----------------------+---------+-------+

Masukkan ID barang yang ingin di-checkout (pisah koma), atau ketik 'Semua' untuk semua:
Pilihan: GADG001
Masukkan jumlah yang ingin di-checkout untuk ID GADG001 (max 2): 1

Pilih Metode Pembayaran:
1. QRIS
2. Bank Transfer
3. COD (Cash on Delivery)
Pilihan: 1

Metode Pembayaran: QRIS
Kode QR untuk Pembayaran: QRIS-971C393A
Silakan lakukan pembayaran dengan menggunakan QRIS dan konfirmasi setelah pembayaran.

✓ Checkout berhasil untuk 1 unit dari 1 item.
```

#### **Menu 5 - Riwayat Transaksi**
```
Pilih: 5
Riwayat Belanja (Invoice):

Transaksi ID : 550e8400-e29b-41d4-a716-446655440000
Customer     : customer
Waktu        : 2025-11-24 10:30:45

ID       Nama Barang              Harga    Qty  Subtotal
---------+------------------------+----------+-----+---------
GADG001  iPhone 15 Pro            1500000  1    1500000

TOTAL: 1500000.00

Metode Pembayaran: QRIS - ID: QRIS123ABC
Status Pemesanan : Pending
=============================
```

#### **Menu 6 - Logout**
Kembali ke menu utama dan disconnect.

---

## 📁 File Penyimpanan Data

### **1. Database/akun.txt**
Menyimpan data akun pengguna (Admin & Customer).
```
Format: id|username|password|role
Contoh:
A1|admin|admin123|admin
C1|customer|customer123|customer
C2|andi|andi123|customer
```

### **2. Database/barang.txt**
Menyimpan data katalog barang.
```
Format: id|nama|harga|stok
Contoh:
GADG001|iPhone 15 Pro|1500000|10
GADG002|Samsung Galaxy S24|1200000|5
GADG003|MacBook Pro|25000000|2
```

### **3. Database/transaksi.txt**
Menyimpan tracking status transaksi.
```
Format: customerId;namaBarang;harga;qty;subtotal;status
Contoh:
C2;Iphone;1000000.0;1;1000000.0;Approved
C2;Iphone;1000000.0;1;1000000.0;Pending
C3;Samsung;1200000.0;2;2400000.0;Pending
```

### **4. Database/Invoice.txt**
Menyimpan detail invoice lengkap setiap transaksi.
```
========== INVOICE ==========
Transaksi ID : 550e8400-e29b-41d4-a716-446655440000
Customer     : customer
Waktu        : 2025-11-24 10:30:45

ID       Nama Barang              Harga    Qty  Subtotal
GADG001  iPhone 15 Pro            1500000  1    1500000

TOTAL: 1500000.00

Metode Pembayaran: QRIS - ID: QRIS971C393A
Status Pemesanan : Pending
=============================
```

---

## 🔄 Alur Sistem

### **Alur Registrasi & Login**
```
Start
  ↓
Main Menu (Registrasi/Login/Keluar)
  ↓
Pilih Registrasi → Input username & password → Simpan ke akun.txt
  ↓
Pilih Login → Input username & password → Cek di akun.txt
  ↓
Jika Admin → AdminDriver.handleMenu()
  ↓
Jika Customer → CustomerDriver.handleMenu()
```

### **Alur Checkout Customer**
```
Customer Login
  ↓
Menu 1: Lihat Barang (dari barang.txt)
  ↓
Menu 2: Tambah ke Keranjang (ke Keranjang object)
  ↓
Menu 3: Lihat Keranjang
  ↓
Menu 4: Checkout
  ├─ Pilih barang di keranjang
  ├─ Tentukan qty
  ├─ Pilih metode pembayaran
  ├─ Buat Transaksi & Invoice
  ├─ Simpan ke Invoice.txt
  ├─ Simpan ke transaksi.txt (status: Pending)
  ├─ Update stok barang (barang.txt)
  └─ Hapus dari keranjang
  ↓
Menu 5: Riwayat Transaksi (baca Invoice.txt)
```

### **Alur Persetujuan Admin**
```
Admin Login
  ↓
Menu 5: Persetujuan Transaksi
  ├─ Baca transaksi.txt (filter status: Pending)
  ├─ Tampilkan daftar transaksi pending
  ├─ Pilih transaksi untuk disetujui
  ├─ Update status: Pending → Approved
  └─ Simpan ke transaksi.txt
```

---

## 🛠️ Teknologi yang Digunakan

- **Bahasa**: Java 8+
- **Database**: File Text (.txt)
- **OOP Concepts**: 
  - Inheritance (extends, implements)
  - Polymorphism (Abstract class, Interface)
  - Encapsulation (Private members, Getters/Setters)
  - Abstraction
- **Design Patterns**:
  - Factory Pattern (LoginManager)
  - Strategy Pattern (Pembayaran classes)
- **Libraries**:
  - `java.io.*` (File I/O)
  - `java.util.*` (Collections, Scanner)
  - `java.util.UUID` (Unique IDs)

---

## ▶️ Cara Menjalankan

### **Prasyarat**
- Java JDK 8 atau lebih tinggi
- IDE: IntelliJ IDEA, Eclipse, atau VS Code dengan Java Extension

### **Langkah 1: Compile Project**
```bash
cd "d:\kuliah\Semester 3\PBO\PRAKTIKUM\GadgetIn\Project"
javac *.java
```

### **Langkah 2: Jalankan Aplikasi**
```bash
java Main
```

### **Langkah 3: Ikuti Menu**
```
+--------------------+
|      Menu Utama    |
+--------------------+
| 1 | Registrasi     |
| 2 | Login          |
| 3 | Keluar         |
+--------------------+
Pilih: _
```

### **Default Credentials**
```
Admin:
- Username: admin
- Password: admin123

Customer:
- Username: customer
- Password: customer123
```

---

## 📊 Fitur Lanjutan

### **1. Status Tracking**
Setiap transaksi memiliki status:
- **Pending**: Transaksi baru, menunggu persetujuan admin
- **Approved**: Transaksi telah disetujui admin

### **2. Metode Pembayaran Dinamis**
Setiap metode pembayaran menghasilkan informasi unik:
- **QRIS**: Kode QR otomatis (format: QRIS-XXXXXXXX)
- **Bank**: Nomor rekening tetap (123-456-7890)
- **COD**: Instruksi pembayaran saat pengiriman

### **3. UUID untuk Transaksi**
Setiap transaksi memiliki ID unik menggunakan UUID untuk mencegah duplikasi data.

### **4. Format Tabel Dinamis**
Semua data ditampilkan dalam format tabel yang rapi dan mudah dibaca.

---

## 🐛 Troubleshooting

### **Masalah: File Database Tidak Ditemukan**
**Solusi**: Pastikan folder `Database/` ada di direktori project. Buat folder jika belum ada:
```bash
mkdir Database
```

### **Masalah: Error Saat Login**
**Solusi**: Pastikan file `akun.txt` ada di folder `Database/`. Program otomatis membuat akun default jika kosong.

### **Masalah: Stok Barang Tidak Berkurang**
**Solusi**: Pastikan checkout berhasil dan file `barang.txt` di-update. Cek konsol untuk pesan error.

---

## 📝 Catatan Pengembang

### **Struktur Database**
Semua data disimpan dalam format plain text terpisah:
- File terpisah memudahkan debugging dan auditing
- Setiap file memiliki format konsisten (delimiter: `|` atau `;`)
- Backup mudah dilakukan dengan copy-paste file

### **Future Improvements**
1. Migrasi ke database SQL (MySQL/PostgreSQL)
2. GUI dengan JavaFX atau Swing
3. Fitur Rating & Review untuk barang
4. Sistem diskon dan promo kode
5. Email notification untuk order confirmation
6. Real-time inventory tracking
7. Payment gateway integration (real QRIS, Bank API)

---

## 👥 Tim Pengembang
**Nama**:  Pembantai UAS Praktikum PBO 
**Anggota**: 1. Andre Alfaridzi   2408107010011
             2. Muhammad Riyadh   2408107010015
             3. Yogi Wanda Putra  2408107010056
**Institusi**: Universitas Syiah Kuala
**Tahun**: 2025

---

## 📄 Lisensi
Proyek ini adalah bagian dari praktikum kuliah dan bersifat akademis.

---

## 📞 Kontak & Support
Untuk pertanyaan atau issue, hubungi dosen pembimbing praktikum PBO.

---
