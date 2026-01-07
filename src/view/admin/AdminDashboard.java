package view.admin;

import java.awt.*;
import javax.swing.*;
import model.User;
import view.LoginFrame;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color HEADER_BG = new Color(17, 24, 39);
    private static final Color BG_COLOR = new Color(249, 250, 251);

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Admin - Perpustakaan");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Modern Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Left - Logo & Title
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        JLabel lblLogo = new JLabel("📚");
        lblLogo.setFont(new Font("SansSerif", Font.PLAIN, 28));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblBrand = new JLabel("Perpustakaan");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblBrand.setForeground(Color.WHITE);

        JLabel lblAdmin = new JLabel("  |  Admin Panel");
        lblAdmin.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblAdmin.setForeground(new Color(156, 163, 175));

        leftHeader.add(lblLogo);
        leftHeader.add(lblBrand);
        leftHeader.add(lblAdmin);

        // Right - User info & Logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        JLabel lblUser = new JLabel(currentUser.getNama());
        lblUser.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblUser.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBackground(DANGER_COLOR);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        rightHeader.add(lblUser);
        rightHeader.add(Box.createHorizontalStrut(10));
        rightHeader.add(btnLogout);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane - TOP position
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        tabbedPane.setBackground(BG_COLOR);

        // Add tabs with icons
        tabbedPane.addTab("  Kelola Buku  ", new BukuPanel());
        tabbedPane.addTab("  Peminjaman  ", new RevenuePanel());
        tabbedPane.addTab("  Data User  ", new UserPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?", "Konfirmasi Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }
}
