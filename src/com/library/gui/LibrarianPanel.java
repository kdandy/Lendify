package com.library.gui;

import com.library.enums.LibrarianPermission;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.LibrarianTableModel;
import com.library.model.Librarian;
import com.library.model.Person;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Panel untuk kelola pustakawan
 */
public class LibrarianPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JTable librarianTable;
    private LibrarianTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JTextField searchField;
    
    /**
     * Constructor untuk LibrarianPanel
     */
    public LibrarianPanel(LendifyGUI mainWindow) {
        this.mainWindow = mainWindow;
        setupUI();
    }
    
    /**
     * Setup komponen UI
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel judul
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Kelola Pustakawan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchLibrarians());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel tabel
        tableModel = new LibrarianTableModel(new ArrayList<>(mainWindow.getLibrary().getLibrarians()));
        librarianTable = new JTable(tableModel);
        librarianTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        librarianTable.setRowHeight(25);
        librarianTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<LibrarianTableModel> sorter = new TableRowSorter<>(tableModel);
        librarianTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        librarianTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editLibrarian();
                }
            }
        });
        
        // Panel untuk tabel dengan scrolling
        JScrollPane tableScrollPane = new JScrollPane(librarianTable);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah Pustakawan");
        editButton = new JButton("Edit Pustakawan");
        deleteButton = new JButton("Hapus Pustakawan");
        backButton = new JButton("Kembali");
        
        addButton.addActionListener(e -> addLibrarian());
        editButton.addActionListener(e -> editLibrarian());
        deleteButton.addActionListener(e -> deleteLibrarian());
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set initial button states
        updateButtonStates();
    }
    
    /**
     * Refresh data tabel
     */
    public void refreshData() {
        tableModel.setLibrarians(new ArrayList<>(mainWindow.getLibrary().getLibrarians()));
        updateButtonStates();
    }
    
    /**
     * Update status tombol berdasarkan hak akses
     */
    private void updateButtonStates() {
        boolean isAdmin = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.ADMIN;
        
        addButton.setEnabled(isAdmin);
        editButton.setEnabled(isAdmin);
        deleteButton.setEnabled(isAdmin);
    }
    
    /**
     * Cari pustakawan berdasarkan keyword
     */
    private void searchLibrarians() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<Librarian> filteredList = new ArrayList<>();
        for (Librarian librarian : mainWindow.getLibrary().getLibrarians()) {
            if (librarian.getName().toLowerCase().contains(keyword) ||
                librarian.getStaffId().toLowerCase().contains(keyword) ||
                librarian.getPosition().toLowerCase().contains(keyword) ||
                librarian.getEmail().toLowerCase().contains(keyword)) {
                filteredList.add(librarian);
            }
        }
        
        tableModel.setLibrarians(filteredList);
    }
    
    /**
     * Tambah pustakawan baru
     */
    private void addLibrarian() {
        if (mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.ADMIN) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menambah pustakawan!", "Akses Ditolak");
            return;
        }
        
        // Panel form untuk input data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField personIdField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField staffIdField = new JTextField(20);
        JTextField positionField = new JTextField(20);
        JTextField salaryField = new JTextField(20);
        JComboBox<LibrarianPermission> permissionCombo = new JComboBox<>(LibrarianPermission.values());
        
        formPanel.add(new JLabel("ID Person:"));
        formPanel.add(personIdField);
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Nomor Telepon:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("ID Staff:"));
        formPanel.add(staffIdField);
        formPanel.add(new JLabel("Posisi:"));
        formPanel.add(positionField);
        formPanel.add(new JLabel("Gaji:"));
        formPanel.add(salaryField);
        formPanel.add(new JLabel("Hak Akses:"));
        formPanel.add(permissionCombo);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Tambah Pustakawan", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                if (personIdField.getText().trim().isEmpty() || 
                    nameField.getText().trim().isEmpty() ||
                    staffIdField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("ID Person, Nama, dan ID Staff tidak boleh kosong!");
                }
                
                double salary = 0;
                try {
                    salary = Double.parseDouble(salaryField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Gaji harus berupa angka!");
                }
                
                // Buat objek Person
                Person person = new Person(
                    personIdField.getText().trim(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    phoneField.getText().trim()
                );
                person.setEmail(emailField.getText().trim());
                
                // Buat objek Librarian
                Librarian librarian = new Librarian(
                    person,
                    staffIdField.getText().trim(),
                    positionField.getText().trim(),
                    salary,
                    (LibrarianPermission) permissionCombo.getSelectedItem()
                );
                
                // Tambahkan ke library
                mainWindow.getLibrary().addLibrarian(librarian);
                refreshData();
                
                GUIUtils.infoDialog(this, "Pustakawan berhasil ditambahkan!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Edit pustakawan yang dipilih
     */
    private void editLibrarian() {
        if (mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.ADMIN) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk mengubah pustakawan!", "Akses Ditolak");
            return;
        }
        
        int selectedRow = librarianTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih pustakawan yang akan diubah!", "Tidak Ada Pustakawan Dipilih");
            return;
        }
        
        int modelRow = librarianTable.convertRowIndexToModel(selectedRow);
        Librarian librarian = tableModel.getLibrarianAt(modelRow);
        
        if (librarian.equals(mainWindow.getCurrentLibrarian())) {
            GUIUtils.errorDialog(this, "Anda tidak dapat mengubah data pustakawan yang sedang login!", "Akses Ditolak");
            return;
        }
        
        // Panel form untuk edit data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField nameField = new JTextField(librarian.getName(), 20);
        JTextField addressField = new JTextField(librarian.getAddress(), 20);
        JTextField phoneField = new JTextField(librarian.getPhoneNumber(), 20);
        JTextField emailField = new JTextField(librarian.getEmail(), 20);
        JTextField positionField = new JTextField(librarian.getPosition(), 20);
        JTextField salaryField = new JTextField(String.valueOf(librarian.getSalary()), 20);
        JComboBox<LibrarianPermission> permissionCombo = new JComboBox<>(LibrarianPermission.values());
        permissionCombo.setSelectedItem(librarian.getPermission());
        
        formPanel.add(new JLabel("ID Person:"));
        formPanel.add(new JLabel(librarian.getId()));
        formPanel.add(new JLabel("ID Staff:"));
        formPanel.add(new JLabel(librarian.getStaffId()));
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Nomor Telepon:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Posisi:"));
        formPanel.add(positionField);
        formPanel.add(new JLabel("Gaji:"));
        formPanel.add(salaryField);
        formPanel.add(new JLabel("Hak Akses:"));
        formPanel.add(permissionCombo);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Edit Pustakawan", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                if (nameField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Nama tidak boleh kosong!");
                }
                
                double salary = 0;
                try {
                    salary = Double.parseDouble(salaryField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Gaji harus berupa angka!");
                }
                
                // Update data
                librarian.setName(nameField.getText().trim());
                librarian.setAddress(addressField.getText().trim());
                librarian.setPhoneNumber(phoneField.getText().trim());
                librarian.setEmail(emailField.getText().trim());
                librarian.setPosition(positionField.getText().trim());
                librarian.setSalary(salary);
                librarian.setPermission((LibrarianPermission) permissionCombo.getSelectedItem());
                
                refreshData();
                
                GUIUtils.infoDialog(this, "Data pustakawan berhasil diubah!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Hapus pustakawan yang dipilih
     */
    private void deleteLibrarian() {
        if (mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.ADMIN) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menghapus pustakawan!", "Akses Ditolak");
            return;
        }
        
        int selectedRow = librarianTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih pustakawan yang akan dihapus!", "Tidak Ada Pustakawan Dipilih");
            return;
        }
        
        int modelRow = librarianTable.convertRowIndexToModel(selectedRow);
        Librarian librarian = tableModel.getLibrarianAt(modelRow);
        
        if (librarian.equals(mainWindow.getCurrentLibrarian())) {
            GUIUtils.errorDialog(this, "Anda tidak dapat menghapus akun pustakawan yang sedang login!", "Akses Ditolak");
            return;
        }
        
        boolean confirm = GUIUtils.confirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus pustakawan " + librarian.getName() + "?", 
            "Konfirmasi Hapus");
        
        if (confirm) {
            mainWindow.getLibrary().removeLibrarian(librarian);
            refreshData();
            GUIUtils.infoDialog(this, "Pustakawan berhasil dihapus!", "Sukses");
        }
    }
}