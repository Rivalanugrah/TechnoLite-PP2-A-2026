package view.admin;

import controller.OrderController;
import controller.BukuController;
import model.Order;
import model.OrderDetail;
import util.PDFExporter;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RevenuePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalPinjaman;
    private JLabel lblAktif;
    private JLabel lblTerlambat;
    private JLabel lblTotalDenda;
    private OrderController orderController;
    private BukuController bukuController;
    private NumberFormat currencyFormat;
    private List<Order> currentOrders;

    private static final double DENDA_PER_HARI = 20000;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public RevenuePanel() {
        orderController = new OrderController();
        bukuController = new BukuController();
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BG_COLOR);

        // Summary Cards (Top)
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 12, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setPreferredSize(new Dimension(0, 95));

        lblTotalPinjaman = new JLabel("0");
        lblTotalPinjaman.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTotalPinjaman.setForeground(PRIMARY_COLOR);

        lblAktif = new JLabel("0");
        lblAktif.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblAktif.setForeground(SUCCESS_COLOR);

        lblTerlambat = new JLabel("0");
        lblTerlambat.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTerlambat.setForeground(DANGER_COLOR);

        lblTotalDenda = new JLabel("Rp 0");
        lblTotalDenda.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotalDenda.setForeground(WARNING_COLOR);

        summaryPanel.add(createCard("📚 Total", lblTotalPinjaman, PRIMARY_COLOR));
        summaryPanel.add(createCard("📖 Dipinjam", lblAktif, SUCCESS_COLOR));
        summaryPanel.add(createCard("⚠️ Terlambat", lblTerlambat, DANGER_COLOR));
        summaryPanel.add(createCard("💰 Total Denda", lblTotalDenda, WARNING_COLOR));

        add(summaryPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Daftar Peminjaman");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_PRIMARY);
        tablePanel.add(lblTitle, BorderLayout.NORTH);

        // Added Tgl Dikembalikan column
        String[] columns = { "ID", "Peminjam", "Tgl Pinjam", "Batas Kembali", "Tgl Dikembalikan", "Status", "Denda" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(79, 70, 229));
        table.setSelectionForeground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setPreferredSize(new Dimension(0, 40));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(90);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(CARD_COLOR);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        buttonPanel.setOpaque(false);

        JButton btnRefresh = createBtn("🔄 Refresh", PRIMARY_COLOR);
        JButton btnKembalikan = createBtn("✅ Kembalikan", SUCCESS_COLOR);
        JButton btnKembalikanTelat = createBtn("⚠️ Kembalikan Terlambat", WARNING_COLOR);
        JButton btnExportPDF = createBtn("📄 Export PDF", new Color(59, 130, 246));
        JButton btnHapus = createBtn("🗑️ Hapus", DANGER_COLOR);

        btnRefresh.addActionListener(e -> loadData());
        btnKembalikan.addActionListener(e -> kembalikanBuku(false));
        btnKembalikanTelat.addActionListener(e -> kembalikanBuku(true));
        btnExportPDF.addActionListener(e -> exportToPDF());
        btnHapus.addActionListener(e -> hapusPeminjaman());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnKembalikan);
        buttonPanel.add(btnKembalikanTelat);
        buttonPanel.add(btnExportPDF);
        buttonPanel.add(btnHapus);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblTitle.setForeground(TEXT_SECONDARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(lblTitle);
        content.add(Box.createVerticalStrut(5));
        content.add(valueLabel);

        JPanel accent = new JPanel();
        accent.setBackground(accentColor);
        accent.setPreferredSize(new Dimension(4, 0));

        card.add(accent, BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JButton createBtn(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        currentOrders = orderController.getAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        double totalDenda = 0;
        int terlambatCount = 0;
        int aktifCount = 0;

        for (Order order : currentOrders) {
            // Calculate current denda for active loans
            double denda = order.getDenda();

            if ("dipinjam".equals(order.getStatus())) {
                denda = order.hitungDenda();
                aktifCount++;
                if (order.isTerlambat()) {
                    terlambatCount++;
                }
            } else if ("dikembalikan".equals(order.getStatus()) && order.getDenda() > 0) {
                // Already returned but had late fee - count as was late
                terlambatCount++;
            }

            String statusDisplay = getStatusBadge(order);
            String dendaDisplay = denda > 0 ? currencyFormat.format(denda) : "-";

            // Show actual return date if available
            String tglDikembalikan = order.getTanggalDikembalikan() != null
                    ? dateFormat.format(order.getTanggalDikembalikan())
                    : "-";

            tableModel.addRow(new Object[] {
                    order.getId(),
                    order.getUserName(),
                    dateFormat.format(order.getTanggalPinjam()),
                    dateFormat.format(order.getTanggalKembali()),
                    tglDikembalikan,
                    statusDisplay,
                    dendaDisplay
            });

            totalDenda += denda;
        }

        // Update stats
        lblTotalPinjaman.setText(String.valueOf(currentOrders.size()));
        lblAktif.setText(String.valueOf(aktifCount));
        lblTerlambat.setText(String.valueOf(terlambatCount));
        lblTotalDenda.setText(currencyFormat.format(totalDenda));
    }

    private String getStatusBadge(Order order) {
        String status = order.getStatus();
        if ("dikembalikan".equals(status)) {
            if (order.getDenda() > 0) {
                return "Telat+Denda";
            }
            return "Dikembalikan";
        } else if ("dipinjam".equals(status)) {
            if (order.isTerlambat()) {
                return "TERLAMBAT " + order.getHariTerlambat() + "hr";
            }
            return "Dipinjam";
        }
        return status;
    }

    private void exportToPDF() {
        if (currentOrders == null || currentOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk di-export!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan PDF");
        fileChooser.setSelectedFile(new java.io.File("laporan_peminjaman.txt"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            if (PDFExporter.exportReport(currentOrders, filePath)) {
                JOptionPane.showMessageDialog(this,
                        "Laporan berhasil disimpan!\n\nFile: " + filePath,
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan laporan!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void kembalikanBuku(boolean simulateTerlambat) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih peminjaman terlebih dahulu!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = (String) tableModel.getValueAt(row, 5);
        if (status.contains("Dikembalikan") || status.contains("Denda")) {
            JOptionPane.showMessageDialog(this, "Buku sudah dikembalikan!",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(row, 0);
        Order order = orderController.getById(orderId);

        double denda = 0;
        int hariTerlambat = 0;

        if (simulateTerlambat) {
            // Show dialog to input days late
            String input = JOptionPane.showInputDialog(this,
                    "Masukkan jumlah hari keterlambatan:",
                    "Input Hari Terlambat", JOptionPane.QUESTION_MESSAGE);

            if (input == null || input.trim().isEmpty()) {
                return;
            }

            try {
                hariTerlambat = Integer.parseInt(input.trim());
                if (hariTerlambat <= 0) {
                    JOptionPane.showMessageDialog(this, "Masukkan angka positif!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                denda = hariTerlambat * DENDA_PER_HARI;

                String message = String.format(
                        "PENGEMBALIAN TERLAMBAT!\n\n" +
                                "Peminjaman #%d\n" +
                                "Terlambat: %d hari\n" +
                                "Denda: %s\n\n" +
                                "(Denda = %d hari × Rp 20.000)\n\n" +
                                "Konfirmasi pengembalian?",
                        orderId, hariTerlambat, currencyFormat.format(denda), hariTerlambat);

                int confirm = JOptionPane.showConfirmDialog(this, message,
                        "Konfirmasi Pengembalian Terlambat", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Masukkan angka yang valid!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            // Check if actually late
            denda = order.hitungDenda();
            hariTerlambat = (int) order.getHariTerlambat();

            if (denda > 0) {
                String message = String.format(
                        "Peminjaman ini TERLAMBAT %d hari!\n\n" +
                                "Denda: %s\n\n" +
                                "Konfirmasi pengembalian dengan denda?",
                        hariTerlambat, currencyFormat.format(denda));

                int confirm = JOptionPane.showConfirmDialog(this, message,
                        "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            } else {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Konfirmasi pengembalian tepat waktu?\n\nPeminjaman #" + orderId + "\nDenda: Rp 0",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }
        }

        // Restore book stock
        List<OrderDetail> details = orderController.getOrderDetails(orderId);
        for (OrderDetail detail : details) {
            bukuController.kembalikanStok(detail.getBukuId(), detail.getJumlah());
        }

        // Calculate actual return date for late returns
        java.sql.Date actualReturnDate;
        if (simulateTerlambat && hariTerlambat > 0) {
            // Set actual return date = batas kembali + hari terlambat
            Calendar cal = Calendar.getInstance();
            cal.setTime(order.getTanggalKembali());
            cal.add(Calendar.DAY_OF_MONTH, hariTerlambat);
            actualReturnDate = new java.sql.Date(cal.getTimeInMillis());
        } else {
            actualReturnDate = new java.sql.Date(System.currentTimeMillis());
        }

        // Process return with actual date
        if (orderController.kembalikanBukuDenganTanggal(orderId, denda, actualReturnDate)) {
            String successMsg = denda > 0
                    ? "Buku dikembalikan!\n\nDenda yang harus dibayar: " + currencyFormat.format(denda)
                    : "Buku berhasil dikembalikan tepat waktu!";

            JOptionPane.showMessageDialog(this, successMsg,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal mengembalikan buku!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusPeminjaman() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih peminjaman yang akan dihapus!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus peminjaman #" + orderId + "?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (orderController.delete(orderId)) {
                JOptionPane.showMessageDialog(this, "Peminjaman berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus peminjaman!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
