package view;

import controller.AuthController;
import model.User;
import util.Validator;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtNama;
    private JTextField txtEmail;
    private JButton btnRegister;
    private JButton btnBack;
    private AuthController authController;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color BG_COLOR = new Color(249, 250, 251);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public RegisterFrame() {
        authController = new AuthController();
        initComponents();
    }

    private void initComponents() {
        setTitle("Daftar - Perpustakaan");
        setSize(420, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BG_COLOR);

        // Card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(35, 50, 35, 50)));

        // Icon & Title
        JLabel lblIcon = new JLabel("📝", SwingConstants.CENTER);
        lblIcon.setFont(new Font("SansSerif", Font.PLAIN, 40));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblIcon);
        cardPanel.add(Box.createVerticalStrut(8));

        JLabel lblTitle = new JLabel("Daftar Anggota", SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblTitle);
        cardPanel.add(Box.createVerticalStrut(25));

        // Form fields
        cardPanel.add(createFormField("Username *", txtUsername = new JTextField()));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(createFormField("Password *", txtPassword = new JPasswordField()));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(createFormField("Konfirmasi Password *", txtConfirmPassword = new JPasswordField()));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(createFormField("Nama Lengkap *", txtNama = new JTextField()));
        cardPanel.add(Box.createVerticalStrut(12));
        cardPanel.add(createFormField("Email", txtEmail = new JTextField()));
        cardPanel.add(Box.createVerticalStrut(20));

        // Register button
        btnRegister = new JButton("Daftar Sekarang");
        btnRegister.setBackground(SUCCESS_COLOR);
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegister.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        cardPanel.add(btnRegister);
        cardPanel.add(Box.createVerticalStrut(12));

        // Back link
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        backPanel.setOpaque(false);

        JLabel lblHaveAccount = new JLabel("Sudah punya akun?");
        lblHaveAccount.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblHaveAccount.setForeground(TEXT_SECONDARY);

        btnBack = new JButton("Login");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBack.setForeground(PRIMARY_COLOR);
        btnBack.setBorderPainted(false);
        btnBack.setContentAreaFilled(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        backPanel.add(lblHaveAccount);
        backPanel.add(btnBack);
        cardPanel.add(backPanel);

        // Center wrapper with scroll
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

        // Events
        btnRegister.addActionListener(e -> register());
        btnBack.addActionListener(e -> backToLogin());
    }

    private JPanel createFormField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lbl.setForeground(TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);

        return panel;
    }

    private void register() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String nama = txtNama.getText().trim();
        String email = txtEmail.getText().trim();

        String error = Validator.validateRegister(username, password, nama, email);
        if (error != null) {
            JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password tidak cocok!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authController.isUsernameExists(username)) {
            JOptionPane.showMessageDialog(this, "Username sudah digunakan!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User(username, password, "user", nama, email);
        if (authController.register(user)) {
            JOptionPane.showMessageDialog(this,
                    "Registrasi berhasil! Silakan login dengan akun baru Anda.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            backToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi gagal!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backToLogin() {
        new LoginFrame().setVisible(true);
        this.dispose();
    }
}
