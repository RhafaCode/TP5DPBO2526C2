import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PeopleMenu extends JFrame {
    public JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JTextField emailField;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JTable personTable;
    private JLabel titleLabel;

    private Database database;
    private int selectedIndex = -1;

    public PeopleMenu() {
        // inisialisasi objek database
        database = new Database();

        if (personTable != null) {
            personTable.setModel(setTable());
        }

        if (titleLabel != null) {
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        }

        if (kategoriComboBox != null) {
            String[] kategoriData = { "???", "Full-time", "Part-time", "Contract", "Internship" };
            kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));
        }

        if (deleteButton != null) {
            deleteButton.setVisible(false);
        }

        // listener tombol simpan atau ubah data
        if (addUpdateButton != null) {
            addUpdateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectedIndex == -1) {
                        insertData();
                    } else {
                        updateData();
                    }
                }
            });
        }

        // listener tombol batal untuk mengosongkan form
        if (cancelButton != null) {
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearForm();
                }
            });
        }

        // listener tombol hapus data dengan konfirmasi
        if (deleteButton != null) {
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (selectedIndex != -1) {
                        int confirm = JOptionPane.showConfirmDialog(
                                null,
                                "Apakah Anda yakin ingin menghapus data ini?",
                                "Konfirmasi Hapus",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE
                        );

                        if (confirm == JOptionPane.YES_OPTION) {
                            deleteData();
                        }
                    }
                }
            });
        }

        // listener klik baris tabel untuk mengisi kembali form
        if (personTable != null) {
            personTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedIndex = personTable.getSelectedRow();

                    String curId = personTable.getModel().getValueAt(selectedIndex, 1).toString();
                    String curNama = personTable.getModel().getValueAt(selectedIndex, 2).toString();
                    String curTahun = personTable.getModel().getValueAt(selectedIndex, 3).toString();
                    String curKategori = personTable.getModel().getValueAt(selectedIndex, 4).toString();
                    String curEmail = personTable.getModel().getValueAt(selectedIndex, 5).toString();

                    if (idField != null) idField.setText(curId);
                    if (namaField != null) namaField.setText(curNama);
                    if (tahunLahirField != null) tahunLahirField.setText(curTahun);
                    if (kategoriComboBox != null) kategoriComboBox.setSelectedItem(curKategori);
                    if (emailField != null) emailField.setText(curEmail);

                    if (addUpdateButton != null) addUpdateButton.setText("Update");
                    if (deleteButton != null) deleteButton.setVisible(true);
                }
            });
        }
    }

    // memuat data dari database mysql ke dalam tabel gui
    public final DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Email" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM person");
            int i = 1;
            while (resultSet.next()) {
                Object[] row = new Object[6];
                row[0] = i++;
                row[1] = resultSet.getString("id");
                row[2] = resultSet.getString("nama");
                row[3] = resultSet.getInt("thn_lahir");
                row[4] = resultSet.getString("kategori");
                row[5] = resultSet.getString("email");
                tmp.addRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tmp;
    }

    // menambahkan data baru ke dalam database
    public void insertData() {
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunStr = tahunLahirField.getText().trim();
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String email = emailField.getText().trim();

        // validasi semua kolom input wajib diisi
        if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty() || email.isEmpty() || kategori.equals("???")) {
            JOptionPane.showMessageDialog(null, "Semua kolom input harus diisi!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // validasi pengecekan duplikasi id di database
        try {
            ResultSet checkId = database.selectQuery("SELECT * FROM person WHERE id = '" + id + "'");
            if (checkId.next()) {
                JOptionPane.showMessageDialog(null, "ID sudah ada! Gunakan ID yang lain.", "Error ID Duplicate", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        try {
            int tahun = Integer.parseInt(tahunStr);
            String sql = "INSERT INTO person VALUES ('" + id + "', '" + nama + "', " + tahun + ", '" + kategori + "', '" + email + "')";
            database.insertUpdateDeleteQuery(sql);

            if (personTable != null) personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // memperbarui data yang dipilih di database
    public void updateData() {
        String id = idField.getText().trim();
        String nama = namaField.getText().trim();
        String tahunStr = tahunLahirField.getText().trim();
        String kategori = kategoriComboBox.getSelectedItem().toString();
        String email = emailField.getText().trim();

        // validasi semua kolom input wajib diisi saat update
        if (id.isEmpty() || nama.isEmpty() || tahunStr.isEmpty() || email.isEmpty() || kategori.equals("???")) {
            JOptionPane.showMessageDialog(null, "Semua kolom input harus diisi!", "Error Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int tahun = Integer.parseInt(tahunStr);
            String sql = "UPDATE person SET nama = '" + nama + "', thn_lahir = " + tahun +
                    ", kategori = '" + kategori + "', email = '" + email + "' WHERE id = '" + id + "'";
            database.insertUpdateDeleteQuery(sql);

            if (personTable != null) personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // menghapus data berdasarkan id dari database
    public void deleteData() {
        String id = idField.getText();
        String sql = "DELETE FROM person WHERE id = '" + id + "'";
        database.insertUpdateDeleteQuery(sql);

        if (personTable != null) personTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
    }

    // mengosongkan semua field inputan form
    public void clearForm() {
        if (idField != null) idField.setText("");
        if (namaField != null) namaField.setText("");
        if (tahunLahirField != null) tahunLahirField.setText("");
        if (kategoriComboBox != null) kategoriComboBox.setSelectedIndex(0);
        if (emailField != null) emailField.setText("");
        if (addUpdateButton != null) addUpdateButton.setText("Add");
        if (deleteButton != null) deleteButton.setVisible(false);
        selectedIndex = -1;
    }

    // menjalankan aplikasi gui
    public static void main(String[] args) {
        PeopleMenu menu = new PeopleMenu();
        menu.setSize(700, 600);
        menu.setLocationRelativeTo(null);
        menu.setContentPane(menu.mainPanel);
        menu.getContentPane().setBackground(Color.WHITE);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}