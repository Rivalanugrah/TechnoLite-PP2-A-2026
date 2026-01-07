# Sistem Perpustakaan

Aplikasi manajemen perpustakaan berbasis Java Swing dengan MySQL.

## Fitur

### Admin
- **Kelola Buku**: CRUD data buku (judul, penulis, penerbit, kategori, tahun terbit, stok)
- **Peminjaman**: Lihat semua peminjaman, kembalikan buku, kelola denda
- **Data User**: Lihat dan hapus anggota perpustakaan

### User (Anggota)
- **Katalog Buku**: Lihat dan pinjam buku yang tersedia
- **Riwayat Peminjaman**: Lihat status peminjaman dan denda

## Aturan Peminjaman

| Aturan | Nilai |
|--------|-------|
| Maksimal lama pinjam | **10 hari** |
| Denda keterlambatan | **Rp 20.000/hari** |
| Maksimal pinjam buku | 5 buku per transaksi |

## Menjalankan Aplikasi

### 1. Setup Database
Pastikan MySQL (MAMP) berjalan di port 8889, lalu:
```bash
/Applications/MAMP/Library/bin/mysql -u root -proot < sql/create_tables.sql
```

### 2. Compile
```bash
javac -cp ".:lib/mysql-connector-j-8.0.33.jar" -d out src/Main.java src/config/*.java src/model/*.java src/controller/*.java src/util/*.java src/view/*.java src/view/admin/*.java src/view/user/*.java
```

### 3. Run
```bash
java -cp "out:lib/mysql-connector-j-8.0.33.jar" Main
```

## Login

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| User | (daftar baru) | - |

## Struktur Project

```
src/
├── Main.java
├── config/
│   └── DatabaseConnection.java
├── model/
│   ├── User.java
│   ├── Buku.java
│   ├── Order.java
│   └── OrderDetail.java
├── controller/
│   ├── AuthController.java
│   ├── BukuController.java
│   ├── UserController.java
│   └── OrderController.java
├── view/
│   ├── LoginFrame.java
│   ├── RegisterFrame.java
│   ├── admin/
│   │   ├── AdminDashboard.java
│   │   ├── BukuPanel.java
│   │   ├── RevenuePanel.java
│   │   └── UserPanel.java
│   └── user/
│       ├── UserDashboard.java
│       ├── KatalogPanel.java
│       └── OrderPanel.java
└── util/
    ├── Validator.java
    └── PDFExporter.java
```

## Database Schema

- **users**: Data admin dan anggota
- **buku**: Koleksi buku perpustakaan
- **orders**: Data peminjaman (tanggal pinjam, tanggal kembali, denda)
- **order_detail**: Detail buku yang dipinjam
