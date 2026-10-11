import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class PeopleMenu extends JFrame {
    public JPanel mainPanel;
    private JTextField idField;
    private JTextField namaField;
    private JTextField tahunLahirField;
    private JComboBox<String> kategoriComboBox;
    private JTextField emailField; // Komponen JTextField/JComboBox sesuai atribut Email
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JButton deleteButton;
    private JTable personTable;
    private JLabel titleLabel;

    private ArrayList<Person> listPerson;
    private int selectedIndex = -1;

    public PeopleMenu() {
        listPerson = new ArrayList<>();
        populateList();

        if (personTable != null) {
            personTable.setModel(setTable());
        }

        if (titleLabel != null) {
            titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        }

        // Pilihan Kategori untuk JComboBox
        if (kategoriComboBox != null) {
            String[] kategoriData = { "???", "Full-time", "Part-time", "Contract", "Internship" };
            kategoriComboBox.setModel(new DefaultComboBoxModel<>(kategoriData));
        }

        if (deleteButton != null) {
            deleteButton.setVisible(false);
        }

        // Listener Tombol Add / Update
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

        // Listener Tombol Cancel
        if (cancelButton != null) {
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    clearForm();
                }
            });
        }

        // Listener Tombol Delete (Lengkap dengan Prompt Konfirmasi)
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

        // Listener Klik Baris Tabel
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

    public void populateList() {
        listPerson.add(new Person("EMP001", "Budi Santoso", 1992, "Full-time", "budi.santoso@company.com"));
        listPerson.add(new Person("EMP002", "Siti Aminah", 1997, "Full-time", "siti.aminah@company.com"));
        listPerson.add(new Person("EMP003", "Rian Pratama", 1995, "Contract", "rian.pratama@company.com"));
    }

    public DefaultTableModel setTable() {
        Object[] cols = { "No", "ID", "Nama", "Tahun Lahir", "Kategori", "Email" };
        DefaultTableModel tmp = new DefaultTableModel(null, cols);

        for (int i = 0; i < listPerson.size(); i++) {
            Object[] row = {
                    i + 1,
                    listPerson.get(i).getId(),
                    listPerson.get(i).getNama(),
                    listPerson.get(i).getTahunLahir(),
                    listPerson.get(i).getKategori(),
                    listPerson.get(i).getEmail()
            };
            tmp.addRow(row);
        }
        return tmp;
    }

    public void insertData() {
        try {
            String id = idField != null ? idField.getText() : "";
            String nama = namaField != null ? namaField.getText() : "";
            int tahun = tahunLahirField != null ? Integer.parseInt(tahunLahirField.getText()) : 0;
            String kategori = kategoriComboBox != null ? kategoriComboBox.getSelectedItem().toString() : "";
            String email = emailField != null ? emailField.getText() : "";

            listPerson.add(new Person(id, nama, tahun, kategori, email));
            if (personTable != null) personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateData() {
        try {
            String id = idField != null ? idField.getText() : "";
            String nama = namaField != null ? namaField.getText() : "";
            int tahun = tahunLahirField != null ? Integer.parseInt(tahunLahirField.getText()) : 0;
            String kategori = kategoriComboBox != null ? kategoriComboBox.getSelectedItem().toString() : "";
            String email = emailField != null ? emailField.getText() : "";

            listPerson.get(selectedIndex).setId(id);
            listPerson.get(selectedIndex).setNama(nama);
            listPerson.get(selectedIndex).setTahunLahir(tahun);
            listPerson.get(selectedIndex).setKategori(kategori);
            listPerson.get(selectedIndex).setEmail(email);

            if (personTable != null) personTable.setModel(setTable());
            clearForm();
            JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Tahun lahir harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void deleteData() {
        listPerson.remove(selectedIndex);
        if (personTable != null) personTable.setModel(setTable());
        clearForm();
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
    }

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