package view;

import controller.AuthController;
import model.User;
import util.Validator;
import view.admin.AdminDashboard;
import view.user.UserDashboard;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private AuthController authController;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public LoginFrame() {
        authController = new AuthController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - Perpustakaan");
        setSize(420, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Center card
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(40, 40, 40, 40)));
        cardPanel.setMaximumSize(new Dimension(360, 400));

        // Logo/Icon
        JLabel lblIcon = new JLabel("", SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 48));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblIcon);
        cardPanel.add(Box.createVerticalStrut(10));

        // Title
        JLabel lblTitle = new JLabel("Perpustakaan", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblTitle);

        // Subtitle
        JLabel lblSubtitle = new JLabel("Silakan login untuk melanjutkan", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblSubtitle);
        cardPanel.add(Box.createVerticalStrut(30));

        // Username field
        cardPanel.add(createFormField("Username", txtUsername = new JTextField()));
        cardPanel.add(Box.createVerticalStrut(15));

        // Password field
        cardPanel.add(createFormField("Password", txtPassword = new JPasswordField()));
        cardPanel.add(Box.createVerticalStrut(25));

        // Login button
        btnLogin = new JButton("Login");
        btnLogin.setBackground(PRIMARY_COLOR);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        cardPanel.add(btnLogin);
        cardPanel.add(Box.createVerticalStrut(15));

        // Register link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);

        JLabel lblNoAccount = new JLabel("Belum punya akun?");
        lblNoAccount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblNoAccount.setForeground(TEXT_SECONDARY);

        btnRegister = new JButton("Daftar");
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnRegister.setForeground(PRIMARY_COLOR);
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerPanel.add(lblNoAccount);
        registerPanel.add(btnRegister);
        cardPanel.add(registerPanel);

        // Center the card with scroll
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(BG_COLOR);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(BG_COLOR);
        centerWrapper.add(scrollPane);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);
        add(mainPanel);

        // Event listeners
        btnLogin.addActionListener(e -> login());
        btnRegister.addActionListener(e -> openRegister());
        txtPassword.addActionListener(e -> login());
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);

        return panel;
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        String error = Validator.validateLogin(username, password);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = authController.login(username, password);

        if (user != null) {
            JOptionPane.showMessageDialog(this,
                    "Selamat datang, " + user.getNama() + "!",
                    "Login Berhasil", JOptionPane.INFORMATION_MESSAGE);

            if (user.isAdmin()) {
                new AdminDashboard(user).setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Username atau password salah!",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openRegister() {
        new RegisterFrame().setVisible(true);
        this.dispose();
    }
}
