package view.admin;

import controller.BukuController;
import model.Buku;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class BukuPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtJudul, txtPenulis, txtPenerbit, txtKategori, txtTahun, txtStok;
    private JTextArea txtDeskripsi;
    private JButton btnTambah, btnUbah, btnHapus, btnClear;
    private BukuController bukuController;
    private int selectedId = -1;

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

    public BukuPanel() {
        bukuController = new BukuController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BG_COLOR);

        // Form Panel (Left) - WIDER and SCROLLABLE
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setPreferredSize(new Dimension(320, 0));
        formContainer.setBackground(CARD_COLOR);
        formContainer.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel("Form Buku");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(lblTitle);
        formPanel.add(Box.createVerticalStrut(15));

        // Form fields with better sizing
        txtJudul = createTextField();
        txtPenulis = createTextField();
        txtPenerbit = createTextField();
        txtKategori = createTextField();
        txtTahun = createTextField();
        txtStok = createTextField();

        formPanel.add(createFormRow("Judul Buku *", txtJudul));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormRow("Penulis *", txtPenulis));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormRow("Penerbit *", txtPenerbit));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormRow("Kategori", txtKategori));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormRow("Tahun Terbit", txtTahun));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFormRow("Stok *", txtStok));
        formPanel.add(Box.createVerticalStrut(10));

        // Description with scroll
        JPanel descPanel = new JPanel(new BorderLayout(0, 5));
        descPanel.setOpaque(false);
        descPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel("Deskripsi");
        lblDesc.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblDesc.setForeground(TEXT_SECONDARY);

        txtDeskripsi = new JTextArea(3, 20);
        txtDeskripsi.setLineWrap(true);
        txtDeskripsi.setWrapStyleWord(true);
        txtDeskripsi.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtDeskripsi.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        JScrollPane descScroll = new JScrollPane(txtDeskripsi);
        descScroll.setPreferredSize(new Dimension(0, 70));

        descPanel.add(lblDesc, BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createVerticalStrut(15));

        // Buttons in 2x2 grid
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        btnTambah = createButton("Tambah", SUCCESS_COLOR);
        btnUbah = createButton("Ubah", WARNING_COLOR);
        btnHapus = createButton("Hapus", DANGER_COLOR);
        btnClear = createButton("Clear", new Color(156, 163, 175));

        btnPanel.add(btnTambah);
        btnPanel.add(btnUbah);
        btnPanel.add(btnHapus);
        btnPanel.add(btnClear);
        formPanel.add(btnPanel);

        // Add form to scroll pane
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        formContainer.add(formScroll, BorderLayout.CENTER);

        add(formContainer, BorderLayout.WEST);

        // Table Panel (Center)
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Event listeners
        btnTambah.addActionListener(e -> tambahBuku());
        btnUbah.addActionListener(e -> ubahBuku());
        btnHapus.addActionListener(e -> hapusBuku());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting())
                selectRow();
        });
    }

    private JPanel createFormRow(String label, JTextField field) {
        JPanel row = new JPanel(new BorderLayout(0, 4));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(TEXT_SECONDARY);

        row.add(lbl, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        return row;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(0, 32));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BG_COLOR);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Daftar Buku Perpustakaan");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_PRIMARY);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBackground(PRIMARY_COLOR);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> loadData());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Table - Added Penerbit column
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
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(79, 70, 229));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 11));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(0, 40));

        // Center align ID, Tahun, Stok
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
        table.getColumnModel().getColumn(6).setPreferredWidth(40);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(CARD_COLOR);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
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
                    buku.getKategori(),
                    buku.getTahunTerbit() > 0 ? buku.getTahunTerbit() : "-",
                    buku.getStok()
            });
        }
    }

    private void selectRow() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            selectedId = (int) tableModel.getValueAt(row, 0);
            Buku buku = bukuController.getById(selectedId);
            if (buku != null) {
                txtJudul.setText(buku.getJudul());
                txtPenulis.setText(buku.getPenulis());
                txtPenerbit.setText(buku.getPenerbit() != null ? buku.getPenerbit() : "");
                txtKategori.setText(buku.getKategori() != null ? buku.getKategori() : "");
                txtTahun.setText(buku.getTahunTerbit() > 0 ? String.valueOf(buku.getTahunTerbit()) : "");
                txtStok.setText(String.valueOf(buku.getStok()));
                txtDeskripsi.setText(buku.getDeskripsi() != null ? buku.getDeskripsi() : "");
            }
        }
    }

    private void tambahBuku() {
        if (!validateForm())
            return;

        Buku buku = createBukuFromForm();
        if (bukuController.create(buku)) {
            showSuccess("Buku berhasil ditambahkan!");
            loadData();
            clearForm();
        } else {
            showError("Gagal menambahkan buku!");
        }
    }

    private void ubahBuku() {
        if (selectedId < 0) {
            showWarning("Pilih buku yang akan diubah!");
            return;
        }
        if (!validateForm())
            return;

        Buku buku = createBukuFromForm();
        buku.setId(selectedId);

        if (bukuController.update(buku)) {
            showSuccess("Buku berhasil diubah!");
            loadData();
            clearForm();
        } else {
            showError("Gagal mengubah buku!");
        }
    }

    private void hapusBuku() {
        if (selectedId < 0) {
            showWarning("Pilih buku yang akan dihapus!");
            return;
        }

        if (!bukuController.canDelete(selectedId)) {
            showError("Buku tidak dapat dihapus karena sedang dipinjam!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus buku ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bukuController.delete(selectedId)) {
                showSuccess("Buku berhasil dihapus!");
                loadData();
                clearForm();
            } else {
                showError("Gagal menghapus buku!");
            }
        }
    }

    private boolean validateForm() {
        if (txtJudul.getText().trim().isEmpty()) {
            showError("Judul tidak boleh kosong!");
            return false;
        }
        if (txtPenulis.getText().trim().isEmpty()) {
            showError("Penulis tidak boleh kosong!");
            return false;
        }
        if (txtPenerbit.getText().trim().isEmpty()) {
            showError("Penerbit tidak boleh kosong!");
            return false;
        }
        if (txtStok.getText().trim().isEmpty()) {
            showError("Stok tidak boleh kosong!");
            return false;
        }
        try {
            Integer.parseInt(txtStok.getText().trim());
        } catch (NumberFormatException e) {
            showError("Stok harus berupa angka!");
            return false;
        }
        if (!txtTahun.getText().trim().isEmpty()) {
            try {
                Integer.parseInt(txtTahun.getText().trim());
            } catch (NumberFormatException e) {
                showError("Tahun terbit harus berupa angka!");
                return false;
            }
        }
        return true;
    }

    private Buku createBukuFromForm() {
        Buku buku = new Buku();
        buku.setJudul(txtJudul.getText().trim());
        buku.setPenulis(txtPenulis.getText().trim());
        buku.setPenerbit(txtPenerbit.getText().trim());
        buku.setKategori(txtKategori.getText().trim());
        buku.setTahunTerbit(txtTahun.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtTahun.getText().trim()));
        buku.setStok(Integer.parseInt(txtStok.getText().trim()));
        buku.setDeskripsi(txtDeskripsi.getText().trim());
        return buku;
    }

    private void clearForm() {
        selectedId = -1;
        txtJudul.setText("");
        txtPenulis.setText("");
        txtPenerbit.setText("");
        txtKategori.setText("");
        txtTahun.setText("");
        txtStok.setText("");
        txtDeskripsi.setText("");
        table.clearSelection();
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }
}
