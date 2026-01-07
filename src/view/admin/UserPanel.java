package view.admin;

import controller.UserController;
import model.User;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotalUsers;
    private UserController userController;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public UserPanel() {
        userController = new UserController();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(BG_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Data User Terdaftar");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_PRIMARY);

        lblTotalUsers = new JLabel("  •  0 users");
        lblTotalUsers.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTotalUsers.setForeground(TEXT_SECONDARY);

        titlePanel.add(lblTitle);
        titlePanel.add(lblTotalUsers);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "ID", "Username", "Nama Lengkap", "Email", "Role", "Tanggal Daftar" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(79, 70, 229));
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setFont(new Font("SansSerif", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(0, 45));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(CARD_COLOR);

        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.setOpaque(false);

        JButton btnRefresh = createButton("🔄 Refresh", PRIMARY_COLOR);
        JButton btnDelete = createButton("🗑️ Hapus User", DANGER_COLOR);

        btnRefresh.addActionListener(e -> loadData());
        btnDelete.addActionListener(e -> deleteUser());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnDelete);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<User> users = userController.getAll();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm");

        for (User user : users) {
            String roleDisplay = "admin".equals(user.getRole()) ? "Admin" : "User";

            tableModel.addRow(new Object[] {
                    user.getId(),
                    user.getUsername(),
                    user.getNama(),
                    user.getEmail() != null ? user.getEmail() : "-",
                    roleDisplay,
                    user.getCreatedAt() != null ? dateFormat.format(user.getCreatedAt()) : "-"
            });
        }

        lblTotalUsers.setText("  •  " + users.size() + " users");
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih user yang akan dihapus!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String role = (String) tableModel.getValueAt(row, 4);
        if (role.contains("Admin")) {
            JOptionPane.showMessageDialog(this, "Tidak dapat menghapus admin!",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int userId = (int) tableModel.getValueAt(row, 0);
        String username = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin menghapus user '" + username + "'?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userController.delete(userId)) {
                JOptionPane.showMessageDialog(this, "User berhasil dihapus!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal menghapus user! User mungkin memiliki order.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
