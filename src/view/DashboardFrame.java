package view;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private final String loggedUser;

    public DashboardFrame(String username) {
        this.loggedUser = username;
        initComponents();
    }

    private void initComponents() {
        setTitle("Dashboard - School Sports Event Management");
        setSize(720, 520);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(236, 240, 250));

        JPanel header = new JPanel(null);
        header.setBackground(new Color(25, 55, 109));
        header.setBounds(0, 0, 720, 85);
        add(header);

        JLabel lblTitle = new JLabel("SCHOOL SPORTS EVENT MANAGEMENT",
            SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 12, 720, 30);
        header.add(lblTitle);

        JLabel lblSub = new JLabel(
            "Welcome, " + loggedUser + "  |  Admin Dashboard",
            SwingConstants.CENTER);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(new Color(180, 205, 240));
        lblSub.setBounds(0, 48, 720, 22);
        header.add(lblSub);

        JLabel lblMenu = new JLabel("Select a Module", SwingConstants.CENTER);
        lblMenu.setFont(new Font("Arial", Font.BOLD, 15));
        lblMenu.setForeground(new Color(25, 55, 109));
        lblMenu.setBounds(0, 105, 720, 25);
        add(lblMenu);

        String[] titles = {
            "Student Management", "Event Management",
            "Participation",      "Results"
        };
        String[] subtitles = {
            "Add / Edit / Delete Students",
            "Add / Edit / Delete Events",
            "Register Students to Events",
            "Record Event Results"
        };
        Color[] colors = {
            new Color(41, 128, 185),
            new Color(39, 174, 96),
            new Color(142, 68, 173),
            new Color(192, 57, 43)
        };

        int startX=55, startY=148, cardW=280, cardH=120, gap=30;

        for (int i = 0; i < 4; i++) {
            int col = i % 2, row = i / 2;
            int x = startX + col * (cardW + gap);
            int y = startY + row * (cardH + gap);

            JPanel card = new JPanel(null);
            card.setBounds(x, y, cardW, cardH);
            card.setBackground(colors[i]);

            JLabel lblCardTitle = new JLabel(titles[i], SwingConstants.CENTER);
            lblCardTitle.setFont(new Font("Arial", Font.BOLD, 15));
            lblCardTitle.setForeground(Color.WHITE);
            lblCardTitle.setBounds(0, 22, cardW, 26);
            card.add(lblCardTitle);

            JLabel lblCardSub = new JLabel(subtitles[i], SwingConstants.CENTER);
            lblCardSub.setFont(new Font("Arial", Font.PLAIN, 11));
            lblCardSub.setForeground(new Color(220, 240, 255));
            lblCardSub.setBounds(0, 52, cardW, 20);
            card.add(lblCardSub);

            JButton btnOpen = new JButton("OPEN");
            btnOpen.setBounds(90, 80, 100, 28);
            btnOpen.setBackground(Color.WHITE);
            btnOpen.setForeground(colors[i]);
            btnOpen.setFont(new Font("Arial", Font.BOLD, 11));
            btnOpen.setFocusPainted(false);
            btnOpen.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            final int idx = i;
            btnOpen.addActionListener(e -> openModule(idx));
            card.add(btnOpen);

            add(card);
        }

        JButton btnLogout = new JButton("LOGOUT");
        btnLogout.setBounds(290, 450, 140, 38);
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                new LoginFrame();
                dispose();
            }
        });
        add(btnLogout);

        setVisible(true);
    }

    private void openModule(int index) {
        switch (index) {
            case 0: new StudentForm();       break;
            case 1: new EventForm();         break;
            case 2: new ParticipationForm(); break;
            case 3: new ResultForm();        break;
        }
    }
}
