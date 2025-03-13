package com.library.gui;

import com.library.enums.LibrarianPermission;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.CategoryTableModel;
import com.library.model.BookCategory;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Panel untuk kelola kategori buku
 */
public class CategoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JTable categoryTable;
    private CategoryTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton backButton;
    private JTextField searchField;
    
    /**
     * Constructor untuk CategoryPanel
     */
    public CategoryPanel(LendifyGUI mainWindow) {
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
        JLabel titleLabel = new JLabel("Kelola Kategori Buku", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchCategories());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel tabel
        tableModel = new CategoryTableModel(new ArrayList<>(mainWindow.getLibrary().getCollection().getCategories()));
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setRowHeight(25);
        categoryTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<CategoryTableModel> sorter = new TableRowSorter<>(tableModel);
        categoryTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        categoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editCategory();
                }
            }
        });
        
        // Panel untuk tabel dengan scrolling
        JScrollPane tableScrollPane = new JScrollPane(categoryTable);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah Kategori");
        editButton = new JButton("Edit Kategori");
        deleteButton = new JButton("Hapus Kategori");
        backButton = new JButton("Kembali");
        
        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deleteButton.addActionListener(e -> deleteCategory());
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
        tableModel.setCategories(new ArrayList<>(mainWindow.getLibrary().getCollection().getCategories()));
        updateButtonStates();
    }
    
    /**
     * Update status tombol berdasarkan hak akses
     */
    private void updateButtonStates() {
        boolean isBasic = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC;
        boolean isAdmin = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.ADMIN;
        
        addButton.setEnabled(!isBasic);
        editButton.setEnabled(!isBasic);
        deleteButton.setEnabled(isAdmin);
    }
    
    /**
     * Cari kategori berdasarkan keyword
     */
    private void searchCategories() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<BookCategory> filteredList = new ArrayList<>();
        for (BookCategory category : mainWindow.getLibrary().getCollection().getCategories()) {
            if (category.getName().toLowerCase().contains(keyword) ||
                category.getDescription().toLowerCase().contains(keyword)) {
                filteredList.add(category);
            }
        }
        
        tableModel.setCategories(filteredList);
    }
    
    /**
     * Tambah kategori baru
     */
    private void addCategory() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menambah kategori!", "Akses Ditolak");
            return;
        }
        
        // Panel form untuk input data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField nameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        
        formPanel.add(new JLabel("Nama Kategori:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Deskripsi:"));
        formPanel.add(descriptionScrollPane);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Tambah Kategori", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Nama kategori tidak boleh kosong!");
                }
                
                // Cek apakah kategori dengan nama sama sudah ada
                for (BookCategory category : mainWindow.getLibrary().getCollection().getCategories()) {
                    if (category.getName().equalsIgnoreCase(name)) {
                        throw new IllegalArgumentException("Kategori dengan nama " + name + " sudah ada!");
                    }
                }
                
                // Buat objek BookCategory
                BookCategory category = new BookCategory(name, description);
                
                // Tambahkan ke library
                mainWindow.addCategory(category);
                refreshData();
                
                GUIUtils.infoDialog(this, "Kategori berhasil ditambahkan!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Edit kategori yang dipilih
     */
    private void editCategory() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk mengubah kategori!", "Akses Ditolak");
            return;
        }
        
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih kategori yang akan diubah!", "Tidak Ada Kategori Dipilih");
            return;
        }
        
        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);
        BookCategory category = tableModel.getCategoryAt(modelRow);
        
        // Panel form untuk edit data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField nameField = new JTextField(category.getName(), 20);
        JTextArea descriptionArea = new JTextArea(category.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        
        formPanel.add(new JLabel("Nama Kategori:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Deskripsi:"));
        formPanel.add(descriptionScrollPane);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Edit Kategori", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                if (name.isEmpty()) {
                    throw new IllegalArgumentException("Nama kategori tidak boleh kosong!");
                }
                
                // Cek apakah kategori dengan nama sama sudah ada (kecuali kategori yang sedang diedit)
                for (BookCategory cat : mainWindow.getLibrary().getCollection().getCategories()) {
                    if (cat.getName().equalsIgnoreCase(name) && !cat.equals(category)) {
                        throw new IllegalArgumentException("Kategori dengan nama " + name + " sudah ada!");
                    }
                }
                
                // Update dari map jika nama berubah
                if (!category.getName().equals(name)) {
                    mainWindow.getCategories().remove(category.getName());
                    category.setName(name);
                    mainWindow.getCategories().put(name, category);
                } else {
                    category.setName(name);
                }
                
                category.setDescription(description);
                
                refreshData();
                
                GUIUtils.infoDialog(this, "Kategori berhasil diubah!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Hapus kategori yang dipilih
     */
    private void deleteCategory() {
        if (mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.ADMIN) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menghapus kategori!", "Akses Ditolak");
            return;
        }
        
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih kategori yang akan dihapus!", "Tidak Ada Kategori Dipilih");
            return;
        }
        
        int modelRow = categoryTable.convertRowIndexToModel(selectedRow);
        BookCategory category = tableModel.getCategoryAt(modelRow);
        
        // Cek apakah kategori memiliki buku
        if (!category.getBooks().isEmpty()) {
            boolean force = GUIUtils.confirmDialog(
                this, 
                "Kategori ini memiliki " + category.getBooks().size() + " buku terkait. Tetap hapus?", 
                "Peringatan"
            );
            
            if (!force) {
                return;
            }
        }
        
        boolean confirm = GUIUtils.confirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus kategori " + category.getName() + "?", 
            "Konfirmasi Hapus"
        );
        
        if (confirm) {
            mainWindow.removeCategory(category);
            refreshData();
            GUIUtils.infoDialog(this, "Kategori berhasil dihapus!", "Sukses");
        }
    }
}