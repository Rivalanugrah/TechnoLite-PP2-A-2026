-- Database untuk Sistem Perpustakaan

CREATE DATABASE IF NOT EXISTS perpustakaan_db;
USE perpustakaan_db;

-- Drop existing tables to recreate
DROP TABLE IF EXISTS order_detail;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS buku;
DROP TABLE IF EXISTS users;

-- Tabel Users
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'user') DEFAULT 'user',
    nama VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Tabel Buku
CREATE TABLE IF NOT EXISTS buku (
    id INT AUTO_INCREMENT PRIMARY KEY,
    judul VARCHAR(200) NOT NULL,
    penulis VARCHAR(100) NOT NULL,
    penerbit VARCHAR(100),
    kategori VARCHAR(50),
    tahun_terbit INT,
    stok INT DEFAULT 0,
    deskripsi TEXT
);

-- Tabel Peminjaman (Orders)
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    tanggal_pinjam DATETIME DEFAULT CURRENT_TIMESTAMP,
    tanggal_kembali DATE NOT NULL,
    tanggal_dikembalikan DATE,
    status ENUM('dipinjam', 'dikembalikan', 'terlambat') DEFAULT 'dipinjam',
    denda DOUBLE DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tabel Detail Peminjaman
CREATE TABLE IF NOT EXISTS order_detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    buku_id INT NOT NULL,
    jumlah INT NOT NULL DEFAULT 1,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (buku_id) REFERENCES buku(id)
);

-- Insert default admin
INSERT INTO users (username, password, role, nama, email) 
VALUES ('admin', 'admin123', 'admin', 'Administrator', 'admin@perpustakaan.com');

-- Insert sample books
INSERT INTO buku (judul, penulis, penerbit, kategori, tahun_terbit, stok, deskripsi) VALUES
('Laskar Pelangi', 'Andrea Hirata', 'Bentang Pustaka', 'Novel', 2005, 5, 'Novel inspiratif tentang pendidikan'),
('Bumi Manusia', 'Pramoedya Ananta Toer', 'Hasta Mitra', 'Novel', 1980, 3, 'Tetralogi Buru bagian pertama'),
('Filosofi Teras', 'Henry Manampiring', 'Kompas', 'Self-Help', 2018, 4, 'Filosofi Stoa untuk kehidupan modern'),
('Atomic Habits', 'James Clear', 'Gramedia', 'Self-Help', 2018, 3, 'Cara mudah membangun kebiasaan baik'),
('Clean Code', 'Robert C. Martin', 'Prentice Hall', 'Programming', 2008, 2, 'Panduan menulis kode yang bersih'),
('Sapiens', 'Yuval Noah Harari', 'Harper', 'Sejarah', 2011, 3, 'Sejarah singkat umat manusia'),
('Dunia Sophie', 'Jostein Gaarder', 'Mizan', 'Filsafat', 1991, 4, 'Novel sejarah filsafat');
