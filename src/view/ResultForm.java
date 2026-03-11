package view;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ResultForm extends JFrame {

    private JTextField        txtResultId, txtPosition, txtScore, txtRemarks, txtSearch;
    private JComboBox<String> cmbParticipation;
    private JButton           btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnRefresh;
    private JTable            table;
    private DefaultTableModel tableModel;

    public ResultForm() {
        initComponents();
        loadParticipationCombo();
        loadAllResults();
    }

    private void initComponents() {
        setTitle("Result Management");
        setSize(1020, 630);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(255, 242, 242));

        // Header
        JPanel header = new JPanel(null);
        header.setBackground(new Color(192, 57, 43));
        header.setBounds(0, 0, 1020, 50);
        JLabel lblT = new JLabel("RESULT MANAGEMENT", SwingConstants.CENTER);
        lblT.setFont(new Font("Arial", Font.BOLD, 18));
        lblT.setForeground(Color.WHITE);
        lblT.setBounds(0, 12, 1020, 26);
        header.add(lblT);
        add(header);

        // Form Panel
        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setBounds(10, 58, 295, 400);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43)),
            "Result Details", 0, 0,
            new Font("Arial", Font.BOLD, 12), new Color(192, 57, 43)));
        add(form);

        lbl(form, "Result ID:", 12, 26);
        txtResultId = fld(form, 12, 48, 265);
        txtResultId.setEditable(false);
        txtResultId.setBackground(new Color(225, 225, 225));

        lbl(form, "Participation (Student > Event): *", 12, 86);
        cmbParticipation = new JComboBox<>();
        cmbParticipation.setBounds(12, 108, 265, 30);
        cmbParticipation.setFont(new Font("Arial", Font.PLAIN, 10));
        form.add(cmbParticipation);

        lbl(form, "Position (1=1st, 2=2nd...): *", 12, 150);
        txtPosition = fld(form, 12, 172, 265);

        lbl(form, "Score (e.g. 95.50): *", 12, 212);
        txtScore = fld(form, 12, 234, 265);

        lbl(form, "Remarks:", 12, 274);
        txtRemarks = fld(form, 12, 296, 265);

        btnAdd    = makeBtn("ADD",    new Color(39,174,96),   12, 344, 63, 33);
        btnUpdate = makeBtn("UPDATE", new Color(41,128,185),  82, 344, 73, 33);
        btnDelete = makeBtn("DELETE", new Color(192,57,43),  162, 344, 68, 33);
        btnClear  = makeBtn("CLEAR",  new Color(127,140,141),237, 344, 58, 33);
        form.add(btnAdd); form.add(btnUpdate);
        form.add(btnDelete); form.add(btnClear);

        // Search bar
        JLabel lblSrch = new JLabel("Search:");
        lblSrch.setFont(new Font("Arial", Font.BOLD, 12));
        lblSrch.setBounds(325, 68, 60, 22);
        add(lblSrch);

        txtSearch = new JTextField();
        txtSearch.setBounds(387, 65, 200, 28);
        add(txtSearch);

        btnSearch  = makeBtn("SEARCH",   new Color(192,57,43),  597, 65, 88, 28);
        btnRefresh = makeBtn("SHOW ALL", new Color(41,128,185), 695, 65, 90, 28);
        add(btnSearch); add(btnRefresh);

        // Table
        String[] cols = {"Res.ID","Part.ID","Student Name","Event Name","Position","Score","Remarks","Result Date"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(23);
        table.setSelectionBackground(new Color(255, 200, 200));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(192, 57, 43));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(315, 100, 688, 510);
        add(scroll);

        // Row click
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                txtResultId.setText(cell(row,0));
                String pid = cell(row,1);
                for (int i=0; i<cmbParticipation.getItemCount(); i++) {
                    String item = cmbParticipation.getItemAt(i);
                    if (item!=null && item.startsWith(pid+" |")) {
                        cmbParticipation.setSelectedIndex(i); break;
                    }
                }
                txtPosition.setText(cell(row,4));
                txtScore.setText   (cell(row,5));
                txtRemarks.setText (cell(row,6));
            }
            String cell(int r,int c){Object v=tableModel.getValueAt(r,c);return v!=null?v.toString():"";}
        });

        btnAdd.addActionListener    (e -> insertResult());
        btnUpdate.addActionListener (e -> updateResult());
        btnDelete.addActionListener (e -> deleteResult());
        btnClear.addActionListener  (e -> clearForm());
        btnSearch.addActionListener (e -> searchResults());
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); loadAllResults(); });

        setVisible(true);
    }

    private void loadParticipationCombo() {
        try (Connection conn = DBConnection.getConnection()) {
            cmbParticipation.removeAllItems();
            cmbParticipation.addItem("-- Select Participation --");
            String sql =
                "SELECT p.participation_id,s.student_name,e.event_name " +
                "FROM participation p " +
                "JOIN students s ON p.student_id=s.student_id " +
                "JOIN events   e ON p.event_id=e.event_id " +
                "ORDER BY p.participation_id";
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next())
                cmbParticipation.addItem(rs.getInt(1)+" | "+rs.getString(2)+" > "+rs.getString(3));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertResult() {
        if (!validateForm()) return;
        int partId = parseComboId();
        if (partId==-1) return;
        String sql = "INSERT INTO results VALUES (result_seq.NEXTVAL,?,?,?,?,SYSDATE)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, partId);
            ps.setInt   (2, Integer.parseInt(txtPosition.getText().trim()));
            ps.setDouble(3, Double.parseDouble(txtScore.getText().trim()));
            ps.setString(4, txtRemarks.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Result added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllResults(); clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Position must be integer. Score must be decimal!", "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResult() {
        if (txtResultId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a result row from the table first!", "Warning", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!validateForm()) return;
        String sql = "UPDATE results SET position=?,score=?,remarks=? WHERE result_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt   (1, Integer.parseInt(txtPosition.getText().trim()));
            ps.setDouble(2, Double.parseDouble(txtScore.getText().trim()));
            ps.setString(3, txtRemarks.getText().trim());
            ps.setInt   (4, Integer.parseInt(txtResultId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Result updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllResults(); clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Position and Score must be valid numbers!", "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteResult() {
        if (txtResultId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a result row first!", "Warning", JOptionPane.WARNING_MESSAGE); return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Delete result record #"+txtResultId.getText()+"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM results WHERE result_id=?")) {
            ps.setInt(1, Integer.parseInt(txtResultId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Result deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllResults(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllResults() {
        tableModel.setRowCount(0);
        String sql =
            "SELECT r.result_id,r.participation_id,s.student_name,e.event_name," +
            "r.position,r.score,r.remarks,TO_CHAR(r.result_date,'DD-MM-YYYY') " +
            "FROM results r " +
            "JOIN participation p ON r.participation_id=p.participation_id " +
            "JOIN students      s ON p.student_id=s.student_id " +
            "JOIN events        e ON p.event_id=e.event_id " +
            "ORDER BY r.result_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),
                    rs.getInt(5),rs.getDouble(6),rs.getString(7),rs.getString(8)});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchResults() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadAllResults(); return; }
        tableModel.setRowCount(0);
        String sql =
            "SELECT r.result_id,r.participation_id,s.student_name,e.event_name," +
            "r.position,r.score,r.remarks,TO_CHAR(r.result_date,'DD-MM-YYYY') " +
            "FROM results r " +
            "JOIN participation p ON r.participation_id=p.participation_id " +
            "JOIN students      s ON p.student_id=s.student_id " +
            "JOIN events        e ON p.event_id=e.event_id " +
            "WHERE UPPER(s.student_name) LIKE UPPER(?) OR UPPER(e.event_name) LIKE UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,"%"+kw+"%"); ps.setString(2,"%"+kw+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                tableModel.addRow(new Object[]{rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getInt(5),rs.getDouble(6),rs.getString(7),rs.getString(8)});
            if (tableModel.getRowCount()==0)
                JOptionPane.showMessageDialog(this, "No results found for: "+kw, "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: "+ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int parseComboId() {
        Object sel = cmbParticipation.getSelectedItem();
        if (sel==null || cmbParticipation.getSelectedIndex()==0) return -1;
        try { return Integer.parseInt(((String)sel).split("\\|")[0].trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    private boolean validateForm() {
        if (cmbParticipation.getSelectedIndex()==0) {
            JOptionPane.showMessageDialog(this, "Please select a Participation record!", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (txtPosition.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Position is required!", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (txtScore.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Score is required!", "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        return true;
    }

    private void clearForm() {
        txtResultId.setText(""); txtPosition.setText("");
        txtScore.setText(""); txtRemarks.setText(""); txtSearch.setText("");
        cmbParticipation.setSelectedIndex(0);
        table.clearSelection();
    }

    private void lbl(JPanel p, String t, int x, int y) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Arial", Font.BOLD, 11));
        l.setBounds(x, y, 270, 20); p.add(l);
    }
    private JTextField fld(JPanel p, int x, int y, int w) {
        JTextField tf = new JTextField();
        tf.setBounds(x, y, w, 28);
        tf.setFont(new Font("Arial", Font.PLAIN, 11)); p.add(tf); return tf;
    }
    private JButton makeBtn(String t, Color bg, int x, int y, int w, int h) {
        JButton b = new JButton(t);
        b.setBounds(x,y,w,h); b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 11)); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
}
