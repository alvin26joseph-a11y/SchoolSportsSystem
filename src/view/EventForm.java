package view;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class EventForm extends JFrame {

    private JTextField txtId, txtName, txtDate, txtVenue, 
                       txtCategory, txtMaxPart, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, 
                    btnSearch, btnRefresh;
    private JTable table;
    private DefaultTableModel tableModel;

    public EventForm() {
        initComponents();
        loadAllEvents();
    }

    private void initComponents() {
        setTitle("Event Management");
        setSize(960, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 255, 245));

        JPanel header = new JPanel(null);
        header.setBackground(new Color(39, 174, 96));
        header.setBounds(0, 0, 960, 50);
        JLabel lblT = new JLabel("EVENT MANAGEMENT", SwingConstants.CENTER);
        lblT.setFont(new Font("Arial", Font.BOLD, 18));
        lblT.setForeground(Color.WHITE);
        lblT.setBounds(0, 12, 960, 26);
        header.add(lblT);
        add(header);

        JPanel form = new JPanel(null);
        form.setBackground(Color.WHITE);
        form.setBounds(10, 58, 290, 460);
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(39, 174, 96)),
            "Event Details", 0, 0,
            new Font("Arial", Font.BOLD, 12), 
            new Color(39, 174, 96)));
        add(form);

        txtId       = addRow(form, "Event ID:",          0);
        txtName     = addRow(form, "Event Name:",        1);
        txtDate     = addRow(form, "Date (DD-MM-YYYY):", 2);
        txtVenue    = addRow(form, "Venue:",             3);
        txtCategory = addRow(form, "Category:",          4);
        txtMaxPart  = addRow(form, "Max Participants:",  5);

        txtId.setEditable(false);
        txtId.setBackground(new Color(225, 225, 225));
        txtDate.setToolTipText("Example: 25-03-2025");

        btnAdd    = makeBtn("ADD",    new Color(39,174,96),   10, 400, 63, 33);
        btnUpdate = makeBtn("UPDATE", new Color(41,128,185),  80, 400, 73, 33);
        btnDelete = makeBtn("DELETE", new Color(192,57,43),  160, 400, 68, 33);
        btnClear  = makeBtn("CLEAR",  new Color(127,140,141),235, 400, 55, 33);
        form.add(btnAdd); form.add(btnUpdate);
        form.add(btnDelete); form.add(btnClear);

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

        String[] cols = {"ID","Event Name","Date","Venue","Category","Max Part."};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 11));
        table.setRowHeight(23);
        table.setSelectionBackground(new Color(195, 255, 215));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        table.getTableHeader().setBackground(new Color(39, 174, 96));
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(308, 100, 635, 480);
        add(scroll);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) return;
                txtId.setText      (cell(row,0));
                txtName.setText    (cell(row,1));
                txtDate.setText    (cell(row,2));
                txtVenue.setText   (cell(row,3));
                txtCategory.setText(cell(row,4));
                txtMaxPart.setText (cell(row,5));
            }
            String cell(int r,int c){
                Object v=tableModel.getValueAt(r,c);
                return v!=null?v.toString():"";
            }
        });

        btnAdd.addActionListener    (e -> insertEvent());
        btnUpdate.addActionListener (e -> updateEvent());
        btnDelete.addActionListener (e -> deleteEvent());
        btnClear.addActionListener  (e -> clearForm());
        btnSearch.addActionListener (e -> searchEvents());
        btnRefresh.addActionListener(e -> { 
            txtSearch.setText(""); loadAllEvents(); 
        });

        setVisible(true);
    }

    private JTextField addRow(JPanel panel, String labelText, int index) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Arial", Font.BOLD, 11));
        lbl.setBounds(12, 26 + index * 58, 200, 20);
        panel.add(lbl);
        JTextField tf = new JTextField();
        tf.setBounds(12, 48 + index * 58, 262, 28);
        tf.setFont(new Font("Arial", Font.PLAIN, 11));
        panel.add(tf);
        return tf;
    }

    private void insertEvent() {
        if (!validateForm()) return;
        String sql = "INSERT INTO events VALUES " +
                     "(event_seq.NEXTVAL,?,TO_DATE(?,'DD-MM-YYYY'),?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtDate.getText().trim());
            ps.setString(3, txtVenue.getText().trim());
            ps.setString(4, txtCategory.getText().trim());
            ps.setInt(5, txtMaxPart.getText().trim().isEmpty() ? 50 :
                Integer.parseInt(txtMaxPart.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Event added successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllEvents(); clearForm();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Max Participants must be a number!",
                "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEvent() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Select an event from the table!",
                "Warning", JOptionPane.WARNING_MESSAGE); return;
        }
        if (!validateForm()) return;
        String sql = "UPDATE events SET event_name=?," +
                     "event_date=TO_DATE(?,'DD-MM-YYYY')," +
                     "venue=?,category=?,max_participants=? " +
                     "WHERE event_id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, txtName.getText().trim());
            ps.setString(2, txtDate.getText().trim());
            ps.setString(3, txtVenue.getText().trim());
            ps.setString(4, txtCategory.getText().trim());
            ps.setInt(5, txtMaxPart.getText().trim().isEmpty() ? 50 :
                Integer.parseInt(txtMaxPart.getText().trim()));
            ps.setInt(6, Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Event updated successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllEvents(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEvent() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Select an event!",
                "Warning", JOptionPane.WARNING_MESSAGE); return;
        }
        int c = JOptionPane.showConfirmDialog(this,
            "Delete event \"" + txtName.getText() + "\"?\n" +
            "Linked participation and results will also be deleted!",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (c != JOptionPane.YES_OPTION) return;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "DELETE FROM events WHERE event_id=?")) {
            ps.setInt(1, Integer.parseInt(txtId.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this,
                "Event deleted!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAllEvents(); clearForm();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAllEvents() {
        tableModel.setRowCount(0);
        String sql = "SELECT event_id,event_name," +
                     "TO_CHAR(event_date,'DD-MM-YYYY')," +
                     "venue,category,max_participants " +
                     "FROM events ORDER BY event_date";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),    rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6)});
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchEvents() {
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) { loadAllEvents(); return; }
        tableModel.setRowCount(0);
        String sql = "SELECT event_id,event_name," +
                     "TO_CHAR(event_date,'DD-MM-YYYY')," +
                     "venue,category,max_participants FROM events " +
                     "WHERE UPPER(event_name) LIKE UPPER(?) " +
                     "OR UPPER(category) LIKE UPPER(?) " +
                     "OR UPPER(venue) LIKE UPPER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1,"%"+kw+"%");
            ps.setString(2,"%"+kw+"%");
            ps.setString(3,"%"+kw+"%");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                tableModel.addRow(new Object[]{
                    rs.getInt(1),    rs.getString(2),
                    rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getInt(6)});
            if (tableModel.getRowCount()==0)
                JOptionPane.showMessageDialog(this,
                    "No events found for: "+kw,
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateForm() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Event Name is required!",
                "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (txtDate.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Date is required!",
                "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        if (!txtDate.getText().trim().matches("\\d{2}-\\d{2}-\\d{4}")) {
            JOptionPane.showMessageDialog(this,
                "Date must be DD-MM-YYYY format!\nExample: 25-03-2025",
                "Validation", JOptionPane.WARNING_MESSAGE); return false;
        }
        return true;
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtDate.setText("");
        txtVenue.setText(""); txtCategory.setText(""); 
        txtMaxPart.setText(""); txtSearch.setText("");
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
