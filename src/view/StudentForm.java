package view;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentForm extends JFrame {

    private JTextField txtId, txtName, txtClass, txtSection,
                       txtGender, txtContact, txtEmail, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear,
                    btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public StudentForm() {
        initComponents();
        loadAllStudents();
    }

    private void initComponents() {
        setTitle("Student Management");
        setSize(920, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 246, 255));

        JPanel header = new JPanel(null);
        header.setBackground(new Color(41, 128, 185));
        header.setBounds(0, 0, 920, 50);
        JLabel lblTitle = new JLabel("STUDENT MANAGEMENT", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(0, 12, 920, 26);
        header.add(lblTitle);
        add(header);

        JPanel formPanel = new JPanel(null);
        formPanel.setBackground(Color.WHITE);
        formPanel.setBounds(10, 58, 290, 510);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185)),
            "Student Details", 0, 0,
            new Font("Arial", Font.BOLD, 12),
            new Color(41, 128, 185)));
        add(formPanel);

        String[] labels = {"Student ID:", "Full Name:", "Class:",
                           "Section:", "Gender:", "Contact No:", "Email:"};
        JTextField[] fields = new JTextField[7];
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Arial", Font.BOLD, 11));
            lbl.setBounds(12, 28 + i * 48, 110, 20);
            formPanel.add(lbl);
            fields[i] = new JTextField();
            fields[i].setBounds(12, 50 + i * 48, 260, 28);
            fields[i].setFont(new Font("Arial", Font.PLAIN, 11));
            formPanel.add(fields[i]);
        }

        txtId = fields[0]; txtName = fields[1]; txtClass = fields[2];
        txtSection = fields[3]; txtGender = fields[4];
        txtContact = fields[5]; txtEmail = fields[6];
        txtId.setEditable(false);
        txtId.setBackground(new Color(225, 225, 225));

        btnAdd    = makeBtn("ADD",    new Color(39,174,96),   10, 390, 63, 33);
        btnUpdate = makeBtn("UPDATE", new Color(41,128,185),  80, 390, 73, 33);
        btnDelete = makeBtn("DELETE", new Color(192,57,43),  160, 390, 68, 33);
        btnClear  = makeBtn("CLEAR",  new Color(127,140,141),235, 390, 55, 33);
        formPanel.add(btnAdd); formPanel.add(btnUpdate);
        formPanel.add(btnDelete); formPanel.add(btnClear);

        JLabel lblSrch = new JLabel("Search:");
        lblSrch.setFont(new Font("Arial", Font.BOLD, 12));
        lblSrch.setBounds(318, 68, 60, 22);
        add(lblSrch);

        txtSearch = new JTextField();
        txtSearch.setBounds(380, 65, 200, 28);
        add(txtSearch);

        btnSearch  = makeBtn("SEARCH",   new Color(142,68,173), 590, 65, 88, 28);
        btnRefresh = makeBtn("SHOW ALL", new Color(41,128,185), 688, 65, 90, 28);
        add(btnSearch); add(btnRefresh);

        String[] cols = {"ID","Full Name","Class","Section",
                         "Gender","Contact","Email"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(23);
        table.setSelectionBackground(new Color(190, 220, 255));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(308, 100, 595, 480);
        add(scroll);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                txtId.setText     (v(row,0)); txtName.setText   (v(row,1));
                txtClass.setText  (v(row,2)); txtSection.setText(v(row,3));
                txtGender.setText (v(row,4)); txtContact.setText(v(row,5));
                txtEmail.setText  (v(row,6));
            }
            String v(int r,int c){
                Object o=tableModel.getValueAt(r,c);
                return o!=null?o.toString():"";
            }
        });

        btnAdd.addActionListener    (e -> insertStudent());
        btnUpdate.addActionListener (e -> updateStudent());
        btnDelete.addActionListener (e -> deleteStudent());
        btnClear.addActionListener  (e -> clearForm());
        btnSearch.addActionListener (e -> searchStudents());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); loadAllStudents();
        });

        setVisible(true);
    }

    private void insertStudent() {
        if (!validateForm()) return;
        String sql = "INSERT INTO students VALUES (student_seq.NEXTVAL,?,?,?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,txtName.getText().trim());
            ps.setString(2,txtClass.getText().trim());
            ps.setString(3,txtSection.getText().trim());
            ps.setString(4,txtGender.getText().trim());
            ps.setString(5,txtContact.getText().trim());
            ps.setString(6,txtEmail.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Student added successfully!",
                "Success",JOptionPane.INFORMATION_MESSAGE);
            loadAllStudents(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Select a student from the table first!",
                "Warning",JOptionPane.WARNING_MESSAGE); return;
        }
        if (!validateForm()) return;
        String sql = "UPDATE students SET student_name=?,class_name=?," +
                     "section=?,gender=?,contact=?,email=? WHERE student_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,txtName.getText().trim());
            ps.setString(2,txtClass.getText().trim());
            ps.setString(3,txtSection.getText().trim());
            ps.setString(4,txtGender.getText().trim());
            ps.setString(5,txtContact.getText().trim());
            ps.setString(6,txtEmail.getText().trim());
            ps.setInt   (7,Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Student updated successfully!",
                "Success",JOptionPane.INFORMATION_MESSAGE);
            loadAllStudents(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Select a student first!",
                "Warning",JOptionPane.WARNING_MESSAGE); return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Delete student \""+txtName.getText()+"\"?\n"+
            "This also deletes their participation and results!",
            "Confirm Delete",JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM students WHERE student_id=?")) {
            ps.setInt(1,Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,"Student deleted!",
                "Success",JOptionPane.INFORMATION_MESSAGE);
            loadAllStudents(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllStudents() {
        tableModel.setRowCount(0);
        String sql = "SELECT student_id,student_name,class_name,section," +
                     "gender,contact,email FROM students ORDER BY student_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),
                    rs.getString(7)});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchStudents() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadAllStudents(); return; }
        tableModel.setRowCount(0);
        String sql = "SELECT student_id,student_name,class_name,section," +
                     "gender,contact,email FROM students WHERE " +
                     "UPPER(student_name) LIKE UPPER(?) OR TO_CHAR(student_id) LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,"%"+kw+"%"); ps.setString(2,"%"+kw+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),
                    rs.getString(7)});
            if (tableModel.getRowCount()==0)
                JOptionPane.showMessageDialog(this,
                    "No students found for: "+kw,
                    "Info",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),
                "DB Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Student Name is required!",
                "Validation",JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus(); return false;
        }
        if (txtClass.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,"Class is required!",
                "Validation",JOptionPane.WARNING_MESSAGE);
            txtClass.requestFocus(); return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtClass.setText("");
        txtSection.setText(""); txtGender.setText("");
        txtContact.setText(""); txtEmail.setText(""); txtSearch.setText("");
        table.clearSelection(); txtName.requestFocus();
    }

    private JButton makeBtn(String t,Color bg,int x,int y,int w,int h) {
        JButton b = new JButton(t);
        b.setBounds(x,y,w,h); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial",Font.BOLD,11)); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
