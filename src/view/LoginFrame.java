package view;

import db.DBConnection;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField     txtUsername;
    private JPasswordField txtPassword;
    private JButton        btnLogin, btnClear;

    public LoginFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("School Sports Event Management System");
        setSize(460, 370);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(25, 55, 109));

        JLabel lblLine1 = new JLabel("SCHOOL SPORTS EVENT", SwingConstants.CENTER);
        lblLine1.setFont(new Font("Arial", Font.BOLD, 20));
        lblLine1.setForeground(Color.WHITE);
        lblLine1.setBounds(30, 25, 400, 30);
        add(lblLine1);

        JLabel lblLine2 = new JLabel("MANAGEMENT SYSTEM", SwingConstants.CENTER);
        lblLine2.setFont(new Font("Arial", Font.BOLD, 18));
        lblLine2.setForeground(new Color(255, 215, 0));
        lblLine2.setBounds(30, 58, 400, 28);
        add(lblLine2);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBounds(55, 105, 345, 210);
        card.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        add(card);

        JLabel lblAdmin = new JLabel("Admin Login", SwingConstants.CENTER);
        lblAdmin.setFont(new Font("Arial", Font.BOLD, 14));
        lblAdmin.setForeground(new Color(25, 55, 109));
        lblAdmin.setBounds(0, 12, 345, 25);
        card.add(lblAdmin);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));
        lblUser.setBounds(30, 55, 90, 25);
        card.add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(130, 55, 185, 30);
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(txtUsername);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Arial", Font.BOLD, 12));
        lblPass.setBounds(30, 100, 90, 25);
        card.add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(130, 100, 185, 30);
        card.add(txtPassword);

        btnLogin = createButton("LOGIN", new Color(25, 55, 109), 55, 155, 105, 35);
        btnClear = createButton("CLEAR", new Color(192, 57, 43), 185, 155, 105, 35);
        card.add(btnLogin);
        card.add(btnClear);

        JLabel lblFooter = new JLabel("© School Sports Management v1.0", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 10));
        lblFooter.setForeground(new Color(180, 200, 230));
        lblFooter.setBounds(50, 325, 360, 18);
        add(lblFooter);

        btnLogin.addActionListener(e -> doLogin());
        btnClear.addActionListener(e -> clearForm());
        txtPassword.addActionListener(e -> doLogin());

        setVisible(true);
        txtUsername.requestFocus();
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter both Username and Password!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT admin_id, username FROM admin_users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this,
                    "Welcome, " + rs.getString("username") + "! Login successful.",
                    "Login Success", JOptionPane.INFORMATION_MESSAGE);
                new DashboardFrame(username);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password. Please try again.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Database connection error:\n" + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    private JButton createButton(String text, Color bg, int x, int y, int w, int h) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, w, h);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}