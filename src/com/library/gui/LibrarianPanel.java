package com.library.gui;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Panel untuk manajemen pustakawan
 */
public class LibrarianPanel extends BasePanel {
    private DefaultTableModel librarianModel;
    private JTable librariansTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public LibrarianPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Kelola Pustakawan");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel tools dengan tombol-tombol aksi
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = createStyledButton("Tambah Pustakawan", new Color(39, 174, 96));
        addButton.addActionListener(e -> showAddLibrarianDialog());
        
        editButton = createStyledButton("Edit Pustakawan", new Color(41, 128, 185));
        editButton.setEnabled(false); // Diaktifkan ketika ada pustakawan yang dipilih
        editButton.addActionListener(e -> editSelectedLibrarian());
        
        deleteButton = createStyledButton("Hapus Pustakawan", new Color(231, 76, 60));
        deleteButton.setEnabled(false); // Diaktifkan ketika ada pustakawan yang dipilih
        deleteButton.addActionListener(e -> deleteSelectedLibrarian());
        
        // Sesuaikan status tombol berdasarkan izin pustakawan saat ini
        if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }        
        
        toolsPanel.add(addButton);
        toolsPanel.add(editButton);
        toolsPanel.add(deleteButton);
        
        add(toolsPanel, BorderLayout.CENTER);
        
        // Tabel pustakawan
        String[] columnNames = {"ID Staff", "Nama", "Posisi", "Email", "Telepon", "Level Akses"};
        librarianModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        // Isi data pustakawan
        refreshLibrariansTable();
        
        librariansTable = new JTable(librarianModel);
        librariansTable.setFillsViewportHeight(true);
        librariansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener untuk mengaktifkan tombol edit dan delete
        librariansTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && librariansTable.getSelectedRow() != -1) {
                    // Cek apakah memilih diri sendiri (tidak boleh dihapus)
                    String selectedStaffId = (String) librariansTable.getValueAt(librariansTable.getSelectedRow(), 0);
                    boolean isSelf = selectedStaffId.equals(currentLibrarian.getStaffId());
                    
                    // Hanya admin yang dapat mengedit informasi pustakawan
                    editButton.setEnabled(currentLibrarian.getPermission() == LibrarianPermission.ADMIN);
                    // Hanya admin yang dapat menghapus pustakawan (kecuali dirinya sendiri)
                    deleteButton.setEnabled(currentLibrarian.getPermission() == LibrarianPermission.ADMIN && !isSelf);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(librariansTable);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh tabel pustakawan
     */
    private void refreshLibrariansTable() {
        librarianModel.setRowCount(0); // Clear table
        
        for (Librarian librarian : library.getLibrarians()) {
            librarianModel.addRow(new Object[]{
                librarian.getStaffId(),
                librarian.getName(),
                librarian.getPosition(),
                librarian.getEmail(),
                librarian.getPhoneNumber(),
                librarian.getPermission().toString()
            });
        }
    }
    
    /**
     * Menampilkan dialog untuk menambah pustakawan baru
     */
    private void showAddLibrarianDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tambah Pustakawan Baru", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Person info
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Person:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(15);
        panel.add(idField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nama:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField addressField = new JTextField(15);
        panel.add(addressField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("No. Telepon:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField phoneField = new JTextField(15);
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);
        
        // Librarian info
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("ID Staff:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField staffIdField = new JTextField(15);
        panel.add(staffIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Posisi:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField positionField = new JTextField(15);
        panel.add(positionField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Gaji:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        JTextField salaryField = new JTextField(15);
        panel.add(salaryField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Level Akses:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 8;
        String[] permissions = {"BASIC", "FULL", "ADMIN"};
        JComboBox<String> permissionCombo = new JComboBox<>(permissions);
        panel.add(permissionCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validasi input
                if (idField.getText().isEmpty() || nameField.getText().isEmpty() || 
                    staffIdField.getText().isEmpty() || positionField.getText().isEmpty()) {
                    showErrorMessage("Semua field harus diisi!", "Error");
                    return;
                }
                
                // Buat Person
                Person person = new Person(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    phoneField.getText().trim()
                );
                person.setEmail(emailField.getText().trim());
                
                // Buat Librarian
                LibrarianPermission permission = LibrarianPermission.valueOf(
                    (String) permissionCombo.getSelectedItem()
                );
                
                double salary = 0;
                try {
                    salary = Double.parseDouble(salaryField.getText().trim());
                } catch (NumberFormatException ex) {
                    showErrorMessage("Gaji harus berupa angka!", "Error");
                    return;
                }
                
                Librarian librarian = new Librarian(
                    person,
                    staffIdField.getText().trim(),
                    positionField.getText().trim(),
                    salary,
                    permission
                );
                
                // Tambahkan ke library
                library.addLibrarian(librarian);
                
                dialog.dispose();
                showInfoMessage("Pustakawan berhasil ditambahkan!", "Sukses");
                
                // Refresh tabel pustakawan
                refreshLibrariansTable();
                
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Edit pustakawan yang dipilih
     */
    private void editSelectedLibrarian() {
        int row = librariansTable.getSelectedRow();
        if (row != -1) {
            String staffId = (String) librariansTable.getValueAt(row, 0);
            Librarian selectedLibrarian = findLibrarianByStaffId(staffId);
            if (selectedLibrarian != null) {
                showEditLibrarianDialog(selectedLibrarian);
            }
        }
    }
    
    /**
     * Hapus pustakawan yang dipilih
     */
    private void deleteSelectedLibrarian() {
        int row = librariansTable.getSelectedRow();
        if (row != -1) {
            String staffId = (String) librariansTable.getValueAt(row, 0);
            Librarian selectedLibrarian = findLibrarianByStaffId(staffId);
            
            if (selectedLibrarian != null && !selectedLibrarian.equals(currentLibrarian)) {
                boolean confirm = showConfirmDialog(
                    "Apakah Anda yakin ingin menghapus pustakawan " + selectedLibrarian.getName() + "?",
                    "Konfirmasi Hapus");
                
                if (confirm) {
                    library.removeLibrarian(selectedLibrarian);
                    refreshLibrariansTable();
                    showInfoMessage("Pustakawan berhasil dihapus.", "Sukses");
                }
            }
        }
    }
    
    /**
     * Menampilkan dialog untuk mengedit pustakawan
     * @param librarian Pustakawan yang akan diedit
     */
    private void showEditLibrarianDialog(Librarian librarian) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Pustakawan", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Person info
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Person:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField idField = new JTextField(librarian.getId(), 15);
        idField.setEditable(false); // ID tidak bisa diubah
        panel.add(idField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nama:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(librarian.getName(), 15);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField addressField = new JTextField(librarian.getAddress(), 15);
        panel.add(addressField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("No. Telepon:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField phoneField = new JTextField(librarian.getPhoneNumber(), 15);
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField emailField = new JTextField(librarian.getEmail(), 15);
        panel.add(emailField, gbc);
        
        // Librarian info
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("ID Staff:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField staffIdField = new JTextField(librarian.getStaffId(), 15);
        staffIdField.setEditable(false); // ID Staff tidak bisa diubah
        panel.add(staffIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Posisi:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField positionField = new JTextField(librarian.getPosition(), 15);
        panel.add(positionField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Gaji:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        JTextField salaryField = new JTextField(String.valueOf(librarian.getSalary()), 15);
        panel.add(salaryField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Level Akses:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 8;
        String[] permissions = {"BASIC", "FULL", "ADMIN"};
        JComboBox<String> permissionCombo = new JComboBox<>(permissions);
        permissionCombo.setSelectedItem(librarian.getPermission().toString());
        panel.add(permissionCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validasi input
                if (nameField.getText().isEmpty() || positionField.getText().isEmpty()) {
                    showErrorMessage("Semua field harus diisi!", "Error");
                    return;
                }
                
                // Update Librarian
                librarian.setName(nameField.getText().trim());
                librarian.setAddress(addressField.getText().trim());
                librarian.setPhoneNumber(phoneField.getText().trim());
                librarian.setEmail(emailField.getText().trim());
                librarian.setPosition(positionField.getText().trim());
                
                try {
                    double salary = Double.parseDouble(salaryField.getText().trim());
                    librarian.setSalary(salary);
                } catch (NumberFormatException ex) {
                    showErrorMessage("Gaji harus berupa angka!", "Error");
                    return;
                }
                
                LibrarianPermission permission = LibrarianPermission.valueOf(
                    (String) permissionCombo.getSelectedItem()
                );
                librarian.setPermission(permission);
                
                dialog.dispose();
                showInfoMessage("Pustakawan berhasil diupdate!", "Sukses");
                
                // Refresh tabel pustakawan
                refreshLibrariansTable();
                
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}