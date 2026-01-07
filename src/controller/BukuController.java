package controller;

import config.DatabaseConnection;
import model.Buku;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BukuController {

    // CREATE
    public boolean create(Buku buku) {
        String sql = "INSERT INTO buku (judul, penulis, penerbit, kategori, tahun_terbit, stok, deskripsi) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, buku.getJudul());
            stmt.setString(2, buku.getPenulis());
            stmt.setString(3, buku.getPenerbit());
            stmt.setString(4, buku.getKategori());
            stmt.setInt(5, buku.getTahunTerbit());
            stmt.setInt(6, buku.getStok());
            stmt.setString(7, buku.getDeskripsi());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // READ ALL
    public List<Buku> getAll() {
        List<Buku> bukuList = new ArrayList<>();
        String sql = "SELECT * FROM buku ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Buku buku = new Buku();
                buku.setId(rs.getInt("id"));
                buku.setJudul(rs.getString("judul"));
                buku.setPenulis(rs.getString("penulis"));
                buku.setPenerbit(rs.getString("penerbit"));
                buku.setKategori(rs.getString("kategori"));
                buku.setTahunTerbit(rs.getInt("tahun_terbit"));
                buku.setStok(rs.getInt("stok"));
                buku.setDeskripsi(rs.getString("deskripsi"));
                bukuList.add(buku);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bukuList;
    }

    // READ BY ID
    public Buku getById(int id) {
        String sql = "SELECT * FROM buku WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Buku buku = new Buku();
                buku.setId(rs.getInt("id"));
                buku.setJudul(rs.getString("judul"));
                buku.setPenulis(rs.getString("penulis"));
                buku.setPenerbit(rs.getString("penerbit"));
                buku.setKategori(rs.getString("kategori"));
                buku.setTahunTerbit(rs.getInt("tahun_terbit"));
                buku.setStok(rs.getInt("stok"));
                buku.setDeskripsi(rs.getString("deskripsi"));
                return buku;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // UPDATE
    public boolean update(Buku buku) {
        String sql = "UPDATE buku SET judul=?, penulis=?, penerbit=?, kategori=?, " +
                "tahun_terbit=?, stok=?, deskripsi=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, buku.getJudul());
            stmt.setString(2, buku.getPenulis());
            stmt.setString(3, buku.getPenerbit());
            stmt.setString(4, buku.getKategori());
            stmt.setInt(5, buku.getTahunTerbit());
            stmt.setInt(6, buku.getStok());
            stmt.setString(7, buku.getDeskripsi());
            stmt.setInt(8, buku.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE
    public boolean delete(int id) {
        String checkSql = "SELECT COUNT(*) FROM order_detail WHERE buku_id = ?";
        String deleteSql = "DELETE FROM buku WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, id);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                return false;
            }

            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, id);
            return deleteStmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Check if book can be deleted
    public boolean canDelete(int id) {
        String sql = "SELECT COUNT(*) FROM order_detail WHERE buku_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE STOK
    public boolean updateStok(int bukuId, int jumlahKurang) {
        String sql = "UPDATE buku SET stok = stok - ? WHERE id = ? AND stok >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jumlahKurang);
            stmt.setInt(2, bukuId);
            stmt.setInt(3, jumlahKurang);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // KEMBALIKAN STOK (when book is returned)
    public boolean kembalikanStok(int bukuId, int jumlah) {
        String sql = "UPDATE buku SET stok = stok + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, jumlah);
            stmt.setInt(2, bukuId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
