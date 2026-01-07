package view.user;

import controller.BukuController;
import controller.OrderController;
import model.Buku;
import model.OrderDetail;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class KatalogPanel extends JPanel {
    private User currentUser;
    private JTable table;
    private DefaultTableModel tableModel;
    private JSpinner spinnerJumlah;
    private JSpinner spinnerHari;
    private JLabel lblSelectedBook;
    private JLabel lblPenulis;
    private JLabel lblPenerbit;
    private JLabel lblStok;
    private JLabel lblTanggalKembali;
    private JTextArea txtDeskripsi;
    private BukuController bukuController;
    private OrderController orderController;
    private Buku selectedBuku;

    // Max borrow days
    private static final int MAX_HARI_PINJAM = 10;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public KatalogPanel(User user) {
        this.currentUser = user;
        this.bukuController = new BukuController();
        this.orderController = new OrderController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BG_COLOR);

        // Table Panel (Center)
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Katalog Buku Perpustakaan");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_PRIMARY);
        tablePanel.add(lblTitle, BorderLayout.NORTH);

        // Added Penerbit column
        String[] columns = { "ID", "Judul", "Penulis", "Penerbit", "Kategori", "Tahun", "Stok" };
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
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(50);
        table.getColumnModel().getColumn(6).setPreferredWidth(50);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(CARD_COLOR);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Borrow Panel (Right)
        JPanel borrowPanel = new JPanel();
        borrowPanel.setLayout(new BoxLayout(borrowPanel, BoxLayout.Y_AXIS));
        borrowPanel.setPreferredSize(new Dimension(300, 0));
        borrowPanel.setBackground(CARD_COLOR);
        borrowPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Title
        JLabel lblBorrow = new JLabel("Detail & Pinjam Buku");
        lblBorrow.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblBorrow.setForeground(TEXT_PRIMARY);
        lblBorrow.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.add(lblBorrow);
        borrowPanel.add(Box.createVerticalStrut(15));

        // Selected book info
        lblSelectedBook = new JLabel("Pilih buku dari tabel");
        lblSelectedBook.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblSelectedBook.setForeground(TEXT_PRIMARY);
        lblSelectedBook.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.add(lblSelectedBook);
        borrowPanel.add(Box.createVerticalStrut(3));

        lblPenulis = new JLabel("-");
        lblPenulis.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblPenulis.setForeground(TEXT_SECONDARY);
        lblPenulis.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.add(lblPenulis);
        borrowPanel.add(Box.createVerticalStrut(3));

        lblPenerbit = new JLabel("-");
        lblPenerbit.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblPenerbit.setForeground(TEXT_SECONDARY);
        lblPenerbit.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.add(lblPenerbit);
        borrowPanel.add(Box.createVerticalStrut(10));

        // Deskripsi
        JLabel lblDescTitle = new JLabel("Deskripsi:");
        lblDescTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblDescTitle.setForeground(TEXT_SECONDARY);
        lblDescTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.add(lblDescTitle);
        borrowPanel.add(Box.createVerticalStrut(5));

        txtDeskripsi = new JTextArea(3, 20);
        txtDeskripsi.setEditable(false);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtDeskripsi.setForeground(TEXT_SECONDARY);
        txtDeskripsi.setBackground(new Color(249, 250, 251));
        txtDeskripsi.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        txtDeskripsi.setText("Pilih buku untuk melihat deskripsi");

        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        descScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        descScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        descScroll.setBorder(null);
        borrowPanel.add(descScroll);
        borrowPanel.add(Box.createVerticalStrut(10));

        // Info rows
        borrowPanel.add(createInfoRow("Stok Tersedia:", lblStok = new JLabel("-")));
        borrowPanel.add(Box.createVerticalStrut(10));

        // Jumlah Buku
        JPanel qtyPanel = new JPanel(new BorderLayout(10, 0));
        qtyPanel.setOpaque(false);
        qtyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        qtyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lblQty = new JLabel("Jumlah Buku:");
        lblQty.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblQty.setForeground(TEXT_SECONDARY);

        spinnerJumlah = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        spinnerJumlah.setFont(new Font("SansSerif", Font.BOLD, 12));

        qtyPanel.add(lblQty, BorderLayout.WEST);
        qtyPanel.add(spinnerJumlah, BorderLayout.CENTER);
        borrowPanel.add(qtyPanel);
        borrowPanel.add(Box.createVerticalStrut(8));

        // Lama Pinjam (Hari)
        JPanel hariPanel = new JPanel(new BorderLayout(10, 0));
        hariPanel.setOpaque(false);
        hariPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        hariPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lblHari = new JLabel("Lama Pinjam:");
        lblHari.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblHari.setForeground(TEXT_SECONDARY);

        spinnerHari = new JSpinner(new SpinnerNumberModel(7, 1, MAX_HARI_PINJAM, 1));
        spinnerHari.setFont(new Font("SansSerif", Font.BOLD, 12));
        spinnerHari.addChangeListener(e -> updateTanggalKembali());

        JLabel lblHariMax = new JLabel("hari (max " + MAX_HARI_PINJAM + ")");
        lblHariMax.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblHariMax.setForeground(TEXT_SECONDARY);

        hariPanel.add(lblHari, BorderLayout.WEST);
        hariPanel.add(spinnerHari, BorderLayout.CENTER);
        hariPanel.add(lblHariMax, BorderLayout.EAST);
        borrowPanel.add(hariPanel);
        borrowPanel.add(Box.createVerticalStrut(8));

        // Tanggal Kembali (calculated)
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        datePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        datePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        JLabel lblDateLabel = new JLabel("Tgl Kembali:");
        lblDateLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblDateLabel.setForeground(TEXT_SECONDARY);

        lblTanggalKembali = new JLabel("-");
        lblTanggalKembali.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTanggalKembali.setForeground(PRIMARY_COLOR);

        datePanel.add(lblDateLabel, BorderLayout.WEST);
        datePanel.add(lblTanggalKembali, BorderLayout.EAST);
        borrowPanel.add(datePanel);
        borrowPanel.add(Box.createVerticalStrut(6));

        // Warning denda
        JLabel lblWarning = new JLabel("<html><small>Denda terlambat: Rp 20.000/hari</small></html>");
        lblWarning.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblWarning.setForeground(WARNING_COLOR);
        lblWarning.setAlignmentX(Component.LEFT_ALIGNMENT);
        borrowPanel.add(lblWarning);
        borrowPanel.add(Box.createVerticalStrut(12));

        // Borrow button
        JButton btnPinjam = new JButton("Pinjam Sekarang");
        btnPinjam.setBackground(SUCCESS_COLOR);
        btnPinjam.setForeground(Color.WHITE);
        btnPinjam.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnPinjam.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        btnPinjam.setFocusPainted(false);
        btnPinjam.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPinjam.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPinjam.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnPinjam.addActionListener(e -> pinjamBuku());
        borrowPanel.add(btnPinjam);
        borrowPanel.add(Box.createVerticalStrut(8));

        // Refresh button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnRefresh.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnRefresh.addActionListener(e -> loadData());
        borrowPanel.add(btnRefresh);

        add(borrowPanel, BorderLayout.EAST);

        // Table selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                selectBook();
        });

        // Initialize return date
        updateTanggalKembali();
    }

    private JPanel createInfoRow(String label, JLabel valueLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(TEXT_SECONDARY);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        valueLabel.setForeground(SUCCESS_COLOR);

        row.add(lbl, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        return row;
    }

    private void updateTanggalKembali() {
        int hari = (int) spinnerHari.getValue();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, hari);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        lblTanggalKembali.setText(sdf.format(cal.getTime()));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Buku> bukuList = bukuController.getAll();

        for (Buku buku : bukuList) {
            tableModel.addRow(new Object[] {
                    buku.getId(),
                    buku.getJudul(),
                    buku.getPenulis(),
                    buku.getPenerbit() != null ? buku.getPenerbit() : "-",
                    buku.getKategori() != null ? buku.getKategori() : "-",
                    buku.getTahunTerbit() > 0 ? buku.getTahunTerbit() : "-",
                    buku.getStok()
            });
        }

        resetSelection();
    }

    private void resetSelection() {
        selectedBuku = null;
        lblSelectedBook.setText("Pilih buku dari tabel");
        lblPenulis.setText("-");
        lblPenerbit.setText("-");
        lblStok.setText("-");
        txtDeskripsi.setText("Pilih buku untuk melihat deskripsi");
        spinnerJumlah.setValue(1);
        spinnerHari.setValue(7);
        updateTanggalKembali();
    }

    private void selectBook() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int bukuId = (int) tableModel.getValueAt(row, 0);
            selectedBuku = bukuController.getById(bukuId);

            if (selectedBuku != null) {
                lblSelectedBook.setText(selectedBuku.getJudul());
                lblPenulis.setText("oleh " + selectedBuku.getPenulis());
                lblPenerbit
                        .setText(selectedBuku.getPenerbit() != null ? "Penerbit: " + selectedBuku.getPenerbit() : "");
                lblStok.setText(selectedBuku.getStok() + " tersedia");

                // Show description
                String desc = selectedBuku.getDeskripsi();
                if (desc != null && !desc.trim().isEmpty()) {
                    txtDeskripsi.setText(desc);
                } else {
                    txtDeskripsi.setText("Tidak ada deskripsi");
                }

                int maxBorrow = Math.min(selectedBuku.getStok(), 5);
                ((SpinnerNumberModel) spinnerJumlah.getModel()).setMaximum(maxBorrow > 0 ? maxBorrow : 1);
                spinnerJumlah.setValue(1);
            }
        }
    }

    private void pinjamBuku() {
        if (selectedBuku == null) {
            JOptionPane.showMessageDialog(this, "Pilih buku yang akan dipinjam!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (selectedBuku.getStok() <= 0) {
            JOptionPane.showMessageDialog(this, "Maaf, buku tidak tersedia!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int jumlah = (int) spinnerJumlah.getValue();
        int hari = (int) spinnerHari.getValue();

        if (jumlah > selectedBuku.getStok()) {
            JOptionPane.showMessageDialog(this, "Stok tidak mencukupi! Tersedia: " + selectedBuku.getStok(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Calculate return date
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, hari);
        java.sql.Date tanggalKembali = new java.sql.Date(cal.getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        String message = String.format(
                "Konfirmasi Peminjaman:\n\n" +
                        "Buku: %s\n" +
                        "Penulis: %s\n" +
                        "Penerbit: %s\n" +
                        "Jumlah: %d buku\n" +
                        "Lama Pinjam: %d hari\n" +
                        "Kembali: %s\n\n" +
                        "Denda keterlambatan: Rp 20.000/hari\n\n" +
                        "Lanjutkan peminjaman?",
                selectedBuku.getJudul(), selectedBuku.getPenulis(),
                selectedBuku.getPenerbit() != null ? selectedBuku.getPenerbit() : "-",
                jumlah, hari, sdf.format(tanggalKembali));

        int confirm = JOptionPane.showConfirmDialog(this, message,
                "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = orderController.createOrder(currentUser.getId(), tanggalKembali);

            if (orderId > 0) {
                OrderDetail detail = new OrderDetail(orderId, selectedBuku.getId(), jumlah);
                orderController.addOrderDetail(detail);
                bukuController.updateStok(selectedBuku.getId(), jumlah);

                JOptionPane.showMessageDialog(this,
                        "Peminjaman berhasil!\n\n" +
                                "ID Peminjaman: #" + orderId + "\n" +
                                "Harap kembalikan sebelum: " + sdf.format(tanggalKembali) + "\n\n" +
                                "Keterlambatan akan dikenakan denda Rp 20.000/hari",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal memproses peminjaman!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
