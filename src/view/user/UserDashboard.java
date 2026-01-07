package view.user;

import model.User;
import view.LoginFrame;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class UserDashboard extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;

    // Modern colors - same as admin
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color HEADER_BG = new Color(17, 24, 39); // Same dark color as admin
    private static final Color BG_COLOR = new Color(249, 250, 251);

    public UserDashboard(User user) {
        this.currentUser = user;
        initComponents();
    }

    private void initComponents() {
        setTitle("Perpustakaan - Portal Anggota");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(BG_COLOR);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Header - same dark style as admin
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        // Left - Logo & Welcome
        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftHeader.setOpaque(false);

        JLabel lblLogo = new JLabel("");
        lblLogo.setFont(new Font("SansSerif", Font.PLAIN, 28));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblBrand = new JLabel("Perpustakaan");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblBrand.setForeground(Color.WHITE);

        leftHeader.add(lblLogo);
        leftHeader.add(lblBrand);

        // Right - User info & Logout
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightHeader.setOpaque(false);

        JLabel lblUser = new JLabel("Halo, " + currentUser.getNama());
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

        // Tabbed Pane
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabbedPane.setBackground(BG_COLOR);

        tabbedPane.addTab("  Katalog Buku  ", new KatalogPanel(currentUser));
        tabbedPane.addTab("  Riwayat Peminjaman  ", new OrderPanel(currentUser));

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
