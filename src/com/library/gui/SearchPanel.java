package com.library.gui;

import com.library.gui.utils.DialogUtils;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.SearchResultTableModel;
import com.library.model.Book;
import com.library.model.BookCategory;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel untuk pencarian buku
 */
public class SearchPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private JComboBox<BookCategory> categoryCombo;
    private JTable resultTable;
    private SearchResultTableModel tableModel;
    private JButton searchButton;
    private JButton clearButton;
    private JButton viewDetailsButton;
    private JButton backButton;
    
    /**
     * Constructor untuk SearchPanel
     */
    public SearchPanel(LendifyGUI mainWindow) {
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
        JLabel titleLabel = new JLabel("Pencarian Buku", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Kriteria Pencarian"));
        
        // Panel input pencarian
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        // Tipe pencarian
        String[] searchTypes = {"Judul", "Pengarang", "ISBN", "Kategori"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchTypeCombo.addActionListener(e -> updateSearchFields());
        
        inputPanel.add(new JLabel("Cari berdasarkan:"));
        inputPanel.add(searchTypeCombo);
        
        // Field pencarian
        searchField = new JTextField(20);
        searchField.addActionListener(e -> performSearch());
        
        inputPanel.add(new JLabel("Kata kunci:"));
        inputPanel.add(searchField);
        
        // Combo kategori (initially hidden)
        List<BookCategory> categories = mainWindow.getLibrary().getCollection().getCategories();
        BookCategory[] categoryArray = categories.toArray(new BookCategory[0]);
        categoryCombo = new JComboBox<>(categoryArray);
        categoryCombo.setVisible(false);
        
        inputPanel.add(new JLabel("Kategori:"));
        inputPanel.add(categoryCombo);
        
        searchPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Panel tombol pencarian
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchButton = new JButton("Cari");
        clearButton = new JButton("Bersihkan");
        
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());
        
        buttonPanel.add(searchButton);
        buttonPanel.add(clearButton);
        
        searchPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Panel hasil pencarian
        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultPanel.setBorder(BorderFactory.createTitledBorder("Hasil Pencarian"));
        
        tableModel = new SearchResultTableModel(new ArrayList<>());
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<SearchResultTableModel> sorter = new TableRowSorter<>(tableModel);
        resultTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        resultTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewBookDetails();
                }
            }
        });
        
        // Selection listener untuk update status tombol
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        resultPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tombol hasil
        JPanel resultButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        viewDetailsButton = new JButton("Lihat Detail");
        backButton = new JButton("Kembali");
        
        viewDetailsButton.addActionListener(e -> viewBookDetails());
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        
        resultButtonPanel.add(viewDetailsButton);
        resultButtonPanel.add(backButton);
        
        resultPanel.add(resultButtonPanel, BorderLayout.SOUTH);
        
        add(resultPanel, BorderLayout.CENTER);
        
        // Status awal tombol
        updateButtonStates();
    }
    
    /**
     * Refresh data panel
     */
    public void refreshData() {
        // Refresh kategori
        List<BookCategory> categories = mainWindow.getLibrary().getCollection().getCategories();
        BookCategory[] categoryArray = categories.toArray(new BookCategory[0]);
        categoryCombo.removeAllItems();
        for (BookCategory category : categoryArray) {
            categoryCombo.addItem(category);
        }
        
        clearSearch();
    }
    
    /**
     * Update field pencarian berdasarkan tipe pencarian
     */
    private void updateSearchFields() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        
        if ("Kategori".equals(searchType)) {
            searchField.setVisible(false);
            categoryCombo.setVisible(true);
        } else {
            searchField.setVisible(true);
            categoryCombo.setVisible(false);
        }
    }
    
    /**
     * Lakukan pencarian berdasarkan kriteria
     */
    private void performSearch() {
        String searchType = (String) searchTypeCombo.getSelectedItem();
        List<Book> results = new ArrayList<>();
        
        try {
            switch (searchType) {
                case "Judul":
                    String titleKeyword = searchField.getText().trim();
                    if (titleKeyword.isEmpty()) {
                        throw new IllegalArgumentException("Kata kunci judul tidak boleh kosong!");
                    }
                    results = mainWindow.getLibrary().searchByTitle(titleKeyword);
                    break;
                
                case "Pengarang":
                    String authorKeyword = searchField.getText().trim();
                    if (authorKeyword.isEmpty()) {
                        throw new IllegalArgumentException("Kata kunci pengarang tidak boleh kosong!");
                    }
                    results = mainWindow.getLibrary().searchByAuthor(authorKeyword);
                    break;
                
                case "ISBN":
                    String isbn = searchField.getText().trim();
                    if (isbn.isEmpty()) {
                        throw new IllegalArgumentException("ISBN tidak boleh kosong!");
                    }
                    Book book = mainWindow.getBooks().get(isbn);
                    if (book != null) {
                        results.add(book);
                    }
                    break;
                
                case "Kategori":
                    BookCategory selectedCategory = (BookCategory) categoryCombo.getSelectedItem();
                    if (selectedCategory == null) {
                        throw new IllegalArgumentException("Pilih kategori terlebih dahulu!");
                    }
                    results = mainWindow.getLibrary().searchByCategory(selectedCategory);
                    break;
            }
            
            tableModel.setBooks(results);
            
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(
                    this, 
                    "Tidak ditemukan buku yang sesuai dengan kriteria pencarian.", 
                    "Tidak Ditemukan", 
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            
            updateButtonStates();
            
        } catch (Exception ex) {
            GUIUtils.errorDialog(this, ex.getMessage(), "Error");
        }
    }
    
    /**
     * Bersihkan hasil pencarian
     */
    private void clearSearch() {
        searchField.setText("");
        searchTypeCombo.setSelectedIndex(0);
        updateSearchFields();
        tableModel.setBooks(new ArrayList<>());
        updateButtonStates();
    }
    
    /**
     * Lihat detail buku
     */
    private void viewBookDetails() {
        Book book = getSelectedBook();
        if (book == null) {
            GUIUtils.errorDialog(this, "Pilih buku untuk melihat detail!", "Tidak Ada Buku Dipilih");
            return;
        }
        
        DialogUtils.showBookDetailsDialog(this, book);
    }
    
    /**
     * Mendapatkan buku yang dipilih di tabel
     */
    private Book getSelectedBook() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = resultTable.convertRowIndexToModel(selectedRow);
            return tableModel.getBookAt(modelRow);
        }
        return null;
    }
    
    /**
     * Update status tombol berdasarkan seleksi
     */
    private void updateButtonStates() {
        boolean bookSelected = resultTable.getSelectedRow() != -1;
        viewDetailsButton.setEnabled(bookSelected);
    }
}