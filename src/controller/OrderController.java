package controller;

import config.DatabaseConnection;
import model.Order;
import model.OrderDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderController {

    // Late fee per day
    public static final double DENDA_PER_HARI = 20000;

    // CREATE PEMINJAMAN
    public int createOrder(int userId, Date tanggalKembali) {
        String sql = "INSERT INTO orders (user_id, tanggal_kembali, status, denda) VALUES (?, ?, 'dipinjam', 0)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, userId);
            stmt.setDate(2, tanggalKembali);

            if (stmt.executeUpdate() > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // ADD ORDER DETAIL
    public boolean addOrderDetail(OrderDetail detail) {
        String sql = "INSERT INTO order_detail (order_id, buku_id, jumlah) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, detail.getOrderId());
            stmt.setInt(2, detail.getBukuId());
            stmt.setInt(3, detail.getJumlah());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // GET ALL ORDERS (for admin)
    public List<Order> getAll() {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT o.*, u.nama as user_name FROM orders o " +
                "JOIN users u ON o.user_id = u.id ORDER BY o.id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTanggalPinjam(rs.getTimestamp("tanggal_pinjam"));
                order.setTanggalKembali(rs.getDate("tanggal_kembali"));
                order.setTanggalDikembalikan(rs.getDate("tanggal_dikembalikan"));
                order.setStatus(rs.getString("status"));
                order.setDenda(rs.getDouble("denda"));
                order.setUserName(rs.getString("user_name"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    // GET ORDERS BY USER (for user history)
    public List<Order> getByUserId(int userId) {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTanggalPinjam(rs.getTimestamp("tanggal_pinjam"));
                order.setTanggalKembali(rs.getDate("tanggal_kembali"));
                order.setTanggalDikembalikan(rs.getDate("tanggal_dikembalikan"));
                order.setStatus(rs.getString("status"));
                order.setDenda(rs.getDouble("denda"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    // GET ORDER BY ID
    public Order getById(int orderId) {
        String sql = "SELECT o.*, u.nama as user_name FROM orders o " +
                "JOIN users u ON o.user_id = u.id WHERE o.id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTanggalPinjam(rs.getTimestamp("tanggal_pinjam"));
                order.setTanggalKembali(rs.getDate("tanggal_kembali"));
                order.setTanggalDikembalikan(rs.getDate("tanggal_dikembalikan"));
                order.setStatus(rs.getString("status"));
                order.setDenda(rs.getDouble("denda"));
                order.setUserName(rs.getString("user_name"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // GET ORDER DETAILS
    public List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT od.*, b.judul as buku_judul, b.penulis as buku_penulis " +
                "FROM order_detail od JOIN buku b ON od.buku_id = b.id " +
                "WHERE od.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setId(rs.getInt("id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setBukuId(rs.getInt("buku_id"));
                detail.setJumlah(rs.getInt("jumlah"));
                detail.setBukuJudul(rs.getString("buku_judul"));
                detail.setBukuPenulis(rs.getString("buku_penulis"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    // GET STATISTICS
    public int getTotalPinjaman() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActivePinjaman() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'dipinjam'";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTerlambat() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'dipinjam' AND tanggal_kembali < CURDATE()";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalDenda() {
        String sql = "SELECT SUM(denda) FROM orders WHERE denda > 0";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // CALCULATE LATE FEE
    public double hitungDenda(int orderId) {
        Order order = getById(orderId);
        if (order == null || order.getTanggalKembali() == null)
            return 0;

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());

        if (today.after(order.getTanggalKembali())) {
            long diffMillis = today.getTime() - order.getTanggalKembali().getTime();
            long diffDays = diffMillis / (24 * 60 * 60 * 1000);
            return diffDays * DENDA_PER_HARI;
        }
        return 0;
    }

    // UPDATE STATUS - KEMBALIKAN BUKU (with fine calculation) - OLD METHOD
    public boolean kembalikanBuku(int orderId, double denda) {
        String sql = "UPDATE orders SET status = 'dikembalikan', tanggal_dikembalikan = CURDATE(), denda = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, denda);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE STATUS - KEMBALIKAN BUKU with specific return date
    public boolean kembalikanBukuDenganTanggal(int orderId, double denda, java.sql.Date tanggalDikembalikan) {
        String sql = "UPDATE orders SET status = 'dikembalikan', tanggal_dikembalikan = ?, denda = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, tanggalDikembalikan);
            stmt.setDouble(2, denda);
            stmt.setInt(3, orderId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // UPDATE ORDER STATUS
    public boolean updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // DELETE ORDER
    public boolean delete(int orderId) {
        String sqlDetail = "DELETE FROM order_detail WHERE order_id = ?";
        String sqlOrder = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmtDetail = conn.prepareStatement(sqlDetail);
            stmtDetail.setInt(1, orderId);
            stmtDetail.executeUpdate();

            PreparedStatement stmtOrder = conn.prepareStatement(sqlOrder);
            stmtOrder.setInt(1, orderId);
            return stmtOrder.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
