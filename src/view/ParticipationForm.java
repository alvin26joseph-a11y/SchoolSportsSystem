package view;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ParticipationForm extends JFrame {

    private JTextField    txtPartId, txtSearch;
    private JComboBox<String> cmbStudent, cmbEvent;
    private JButton       btnRegister, btnDelete, btnClear, btnSearch, btnRefresh;
    private JTable        table;
    private DefaultTableModel tableModel;

    public ParticipationForm() {
        initComponents();
        populateComboBoxes();
        loadAllParticipation();
    }

    private void initComponents() {
        setTitle("Participation Management");
        setSize(980, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(248, 240, 255));

        // Header
        JPanel header = new JPanel(null);
        header.setBackground(new Color(142, 68, 173));
        header.setBounds(0, 0, 980, 50);
        JLabel lblT = new JLabel("PARTICIPATION MANAGEMENT", SwingConstants.CENTER);
        lblT.setFont(new Font("Arial", Font.BOLD, 18));
        lblT.setForeground(Color.WHITE);
        lblT.setBounds(0, 12, 980, 26);
        header.add(lblT);
        add(header);

        // Form Panel
        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setBounds(10, 58, 295, 340);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(142, 68, 173)),
            "Register Student for Event", 0, 0,
            new Font("Arial", Font.BOLD, 12), new Color(142, 68, 173)));
        add(form);

        JLabel lblId = new JLabel("Participation ID:");
        lblId.setFont(new Font("Arial", Font.BOLD, 11));
        lblId.setBounds(12, 26, 130, 20);
        form.add(lblId);

        txtPartId = new JTextField("Auto-generated");
        txtPartId.setBounds(12, 48, 265, 28);
        txtPartId.setEditable(false);
        txtPartId.setBackground(new Color(225, 225, 225));
        form.add(txtPartId);

        JLabel lblStudent = new JLabel("Select Student: *");
        lblStudent.setFont(new Font("Arial", Font.BOLD, 11));
        lblStudent.setBounds(12, 88, 160, 20);
        form.add(lblStudent);

        cmbStudent = new JComboBox<>();
        cmbStudent.setBounds(12, 110, 265, 30);
        cmbStudent.setFont(new Font("Arial", Font.PLAIN, 11));
        form.add(cmbStudent);

        JLabel lblEvent = new JLabel("Select Event: *");
        lblEvent.setFont(new Font("Arial", Font.BOLD, 11));
        lblEvent.setBounds(12, 152, 160, 20);
        form.add(lblEvent);

        cmbEvent = new JComboBox<>();
        cmbEvent.setBounds(12, 174, 265, 30);
        cmbEvent.setFont(new Font("Arial", Font.PLAIN, 11));
        form.add(cmbEvent);

        JLabel lblDate = new JLabel("Registration Date:");
        lblDate.setFont(new Font("Arial", Font.BOLD, 11));
        lblDate.setBounds(12, 216, 150, 20);
        form.add(lblDate);

        JTextField txtDate = new JTextField("Automatically set to Today");
        txtDate.setBounds(12, 238, 265, 28);
        txtDate.setEditable(false);
        txtDate.setFont(new Font("Arial", Font.ITALIC, 11));
        txtDate.setForeground(Color.GRAY);
        txtDate.setBackground(new Color(225, 225, 225));
        form.add(txtDate);

        btnRegister = makeBtn("REGISTER", new Color(39,174,96),  12, 285, 90, 33);
        btnDelete   = makeBtn("DELETE",   new Color(192,57,43), 115, 285, 75, 33);
        btnClear    = makeBtn("CLEAR",    new Color(127,140,141),203, 285, 68, 33);
        form.add(btnRegister); form.add(btnDelete); form.add(btnClear);

        // Info Note
        JTextArea note = new JTextArea(
            "HOW FOREIGN KEYS WORK HERE:\n\n" +
            "PARTICIPATION holds:\n" +
            "  student_id -> FK -> STUDENTS\n" +
            "  event_id   -> FK -> EVENTS\n\n" +
            "ON DELETE CASCADE:\n" +
            "  Deleting a Student or Event\n" +
            "  auto-removes participation.\n\n" +
            "UNIQUE constraint prevents\n" +
            "duplicate registrations.");
        note.setFont(new Font("Arial", Font.PLAIN, 10));
        note.setEditable(false);
        note.setBackground(new Color(255, 255, 210));
        note.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 200, 0)),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)));
        note.setBounds(10, 405, 295, 175);
        add(note);

        // Search bar
        JLabel lblSrch = new JLabel("Search:");
        lblSrch.setFont(new Font("Arial", Font.BOLD, 12));
        lblSrch.setBounds(325, 68, 60, 22);
        add(lblSrch);

        txtSearch = new JTextField();
        txtSearch.setBounds(387, 65, 200, 28);
        add(txtSearch);

        btnSearch  = makeBtn("SEARCH",   new Color(142,68,173), 597, 65, 88, 28);
        btnRefresh = makeBtn("SHOW ALL", new Color(41,128,185), 695, 65, 90, 28);
        add(btnSearch); add(btnRefresh);

        // Table
        String[] cols = {"Part.ID","Stu.ID","Student Name","Ev.ID","Event Name","Event Date","Reg. Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(23);
        table.setSelectionBackground(new Color(225, 200, 255));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(142, 68, 173));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(315, 100, 650, 500);
        add(scroll);

        // Row click
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0)
                    txtPartId.setText(tableModel.getValueAt(row,0).toString());
            }
        });

        btnRegister.addActionListener(e -> registerParticipation());
        btnDelete.addActionListener  (e -> deleteParticipation());
        btnClear.addActionListener   (e -> clearForm());
        btnSearch.addActionListener  (e -> searchParticipation());
        btnRefresh.addActionListener (e -> { txtSearch.setText(""); loadAllParticipation(); });

        setVisible(true);
    }

    private void populateComboBoxes() {
        try (Connection conn = DBConnection.getConnection()) {
            cmbStudent.removeAllItems();
            cmbStudent.addItem("-- Select Student --");
            ResultSet rs = conn.createStatement().executeQuery(
                "SELECT student_id,student_name,class_name FROM students ORDER BY student_name");
            while (rs.next())
                cmbStudent.addItem(rs.getInt(1)+" | "+rs.getString(2)+" ["+rs.getString(3)+"]");

            cmbEvent.removeAllItems();
            cmbEvent.addItem("-- Select Event --");
            rs = conn.createStatement().executeQuery(
                "SELECT event_id,event_name,TO_CHAR(event_date,'DD-MM-YYYY') FROM events ORDER BY event_date");
            while (rs.next())
                cmbEvent.addItem(rs.getInt(1)+" | "+rs.getString(2)+" ("+rs.getString(3)+")");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading dropdowns: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerParticipation() {
        if (cmbStudent.getSelectedIndex()==0 || cmbEvent.getSelectedIndex()==0) {
            JOptionPane.showMessageDialog(this, "Please select both a Student and an Event!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int studentId = Integer.parseInt(((String)cmbStudent.getSelectedItem()).split("\\|")[0].trim());
        int eventId   = Integer.parseInt(((String)cmbEvent.getSelectedItem()).split("\\|")[0].trim());

        String sql = "INSERT INTO participation VALUES (participation_seq.NEXTVAL,?,?,SYSDATE,'Registered')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, eventId);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student registered for event successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllParticipation(); clearForm();
        } catch (SQLException ex) {
            if (ex.getErrorCode()==1)
                JOptionPane.showMessageDialog(this, "This student is already registered for this event!", "Duplicate", JOptionPane.WARNING_MESSAGE);
            else
                JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteParticipation() {
        String idText = txtPartId.getText().trim();
        if (idText.isEmpty() || idText.equals("Auto-generated")) {
            JOptionPane.showMessageDialog(this, "Click a row in the table to select a record!", "Warning", JOptionPane.WARNING_MESSAGE); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete participation record #"+idText+"?\nThis will also delete the related Result (if any)!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM participation WHERE participation_id=?")) {
            ps.setInt(1, Integer.parseInt(idText));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Participation record deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllParticipation(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllParticipation() {
        tableModel.setRowCount(0);
        String sql =
            "SELECT p.participation_id,s.student_id,s.student_name," +
            "e.event_id,e.event_name," +
            "TO_CHAR(e.event_date,'DD-MM-YYYY')," +
            "TO_CHAR(p.registration_date,'DD-MM-YYYY') " +
            "FROM participation p " +
            "JOIN students s ON p.student_id=s.student_id " +
            "JOIN events   e ON p.event_id=e.event_id " +
            "ORDER BY p.participation_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getInt(2),rs.getString(3),
                    rs.getInt(4),rs.getString(5),rs.getString(6),rs.getString(7)});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchParticipation() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadAllParticipation(); return; }
        tableModel.setRowCount(0);
        String sql =
            "SELECT p.participation_id,s.student_id,s.student_name," +
            "e.event_id,e.event_name," +
            "TO_CHAR(e.event_date,'DD-MM-YYYY')," +
            "TO_CHAR(p.registration_date,'DD-MM-YYYY') " +
            "FROM participation p " +
            "JOIN students s ON p.student_id=s.student_id " +
            "JOIN events   e ON p.event_id=e.event_id " +
            "WHERE UPPER(s.student_name) LIKE UPPER(?) OR UPPER(e.event_name) LIKE UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,"%"+kw+"%"); ps.setString(2,"%"+kw+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getString(7)});
            if (tableModel.getRowCount()==0)
                JOptionPane.showMessageDialog(this, "No records found for: "+kw, "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtPartId.setText("Auto-generated");
        cmbStudent.setSelectedIndex(0);
        cmbEvent.setSelectedIndex(0);
        txtSearch.setText("");
        table.clearSelection();
    }

    private JButton makeBtn(String t, Color bg, int x, int y, int w, int h) {
        JButton b = new JButton(t);
        b.setBounds(x,y,w,h); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 11)); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
}