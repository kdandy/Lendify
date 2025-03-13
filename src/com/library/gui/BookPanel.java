package com.library.gui;

import com.library.enums.BookFormat;
import com.library.enums.Language;
import com.library.enums.LibrarianPermission;
import com.library.gui.utils.DialogUtils;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.BookTableModel;
import com.library.gui.utils.TableModels.BookItemTableModel;
import com.library.model.Book;
import com.library.model.BookCategory;
import com.library.model.BookItem;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Panel untuk kelola buku
 */

public class BookPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    
    // Panel untuk daftar buku
    private JPanel bookListPanel;
    private JTable bookTable;
    private BookTableModel bookTableModel;
    private JTextField bookSearchField;
    
    // Panel untuk detail buku yang dipilih
    private JPanel bookDetailsPanel;
    private JLabel titleValueLabel;
    private JLabel authorValueLabel;
    private JLabel publisherValueLabel;
    private JLabel yearValueLabel;
    private JLabel isbnValueLabel;
    private JLabel formatValueLabel;
    private JLabel languageValueLabel;
    private JLabel pagesValueLabel;
    private JTextArea descriptionArea;
    
    // Panel untuk salinan buku (book items)
    private JPanel itemsPanel;
    private JTable itemsTable;
    private BookItemTableModel itemsTableModel;
    
    // Tombol-tombol aksi
    private JButton addBookButton;
    private JButton editBookButton;
    private JButton deleteBookButton;
    private JButton addItemButton;
    private JButton editItemButton;
    private JButton deleteItemButton;
    private JButton backButton;
    
    /**
     * Constructor untuk BookPanel
     */
    public BookPanel(LendifyGUI mainWindow) {
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
        JLabel titleLabel = new JLabel("Kelola Buku", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel utama dengan split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.5);
        
        // Panel kiri untuk daftar buku
        bookListPanel = createBookListPanel();
        splitPane.setLeftComponent(bookListPanel);
        
        // Panel kanan untuk detail buku
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        
        // Panel detail buku
        bookDetailsPanel = createBookDetailsPanel();
        rightPanel.add(bookDetailsPanel, BorderLayout.NORTH);
        
        // Panel salinan buku
        itemsPanel = createBookItemsPanel();
        rightPanel.add(itemsPanel, BorderLayout.CENTER);
        
        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        backButton = new JButton("Kembali ke Menu Utama");
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set initial button states
        updateButtonStates();
    }
    
    /**
     * Buat panel untuk daftar buku
     */
    private JPanel createBookListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Daftar Buku"));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bookSearchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchBooks());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(bookSearchField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panel tabel
        bookTableModel = new BookTableModel(new ArrayList<>(mainWindow.getLibrary().getCollection().getBooks()));
        bookTable = new JTable(bookTableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<BookTableModel> sorter = new TableRowSorter<>(bookTableModel);
        bookTable.setRowSorter(sorter);
        
        // Listener untuk seleksi buku
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedBook();
            }
        });
        
        // Mouse listener untuk double-click
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Book book = getSelectedBook();
                    if (book != null) {
                        DialogUtils.showBookDetailsDialog(BookPanel.this, book);
                    }
                }
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(bookTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        addBookButton = new JButton("Tambah Buku");
        editBookButton = new JButton("Edit Buku");
        deleteBookButton = new JButton("Hapus Buku");
        
        addBookButton.addActionListener(e -> addBook());
        editBookButton.addActionListener(e -> editBook());
        deleteBookButton.addActionListener(e -> deleteBook());
        
        buttonPanel.add(addBookButton);
        buttonPanel.add(editBookButton);
        buttonPanel.add(deleteBookButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Buat panel untuk detail buku
     */
    private JPanel createBookDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Detail Buku"));
        
        // Panel informasi dasar
        JPanel infoPanel = new JPanel(new GridLayout(4, 4, 5, 5));
        
        // Baris 1
        infoPanel.add(new JLabel("Judul:", SwingConstants.RIGHT));
        titleValueLabel = new JLabel("");
        infoPanel.add(titleValueLabel);
        
        infoPanel.add(new JLabel("ISBN:", SwingConstants.RIGHT));
        isbnValueLabel = new JLabel("");
        infoPanel.add(isbnValueLabel);
        
        // Baris 2
        infoPanel.add(new JLabel("Pengarang:", SwingConstants.RIGHT));
        authorValueLabel = new JLabel("");
        infoPanel.add(authorValueLabel);
        
        infoPanel.add(new JLabel("Penerbit:", SwingConstants.RIGHT));
        publisherValueLabel = new JLabel("");
        infoPanel.add(publisherValueLabel);
        
        // Baris 3
        infoPanel.add(new JLabel("Tahun:", SwingConstants.RIGHT));
        yearValueLabel = new JLabel("");
        infoPanel.add(yearValueLabel);
        
        infoPanel.add(new JLabel("Format:", SwingConstants.RIGHT));
        formatValueLabel = new JLabel("");
        infoPanel.add(formatValueLabel);
        
        // Baris 4
        infoPanel.add(new JLabel("Bahasa:", SwingConstants.RIGHT));
        languageValueLabel = new JLabel("");
        infoPanel.add(languageValueLabel);
        
        infoPanel.add(new JLabel("Halaman:", SwingConstants.RIGHT));
        pagesValueLabel = new JLabel("");
        infoPanel.add(pagesValueLabel);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Panel deskripsi
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBorder(BorderFactory.createTitledBorder("Deskripsi"));
        
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descPanel.add(descScrollPane, BorderLayout.CENTER);
        
        panel.add(descPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Buat panel untuk salinan buku
     */
    private JPanel createBookItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Salinan Buku"));
        
        // Tabel salinan buku
        itemsTableModel = new BookItemTableModel(new ArrayList<>());
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.setRowHeight(25);
        itemsTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane tableScrollPane = new JScrollPane(itemsTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        addItemButton = new JButton("Tambah Salinan");
        editItemButton = new JButton("Edit Salinan");
        deleteItemButton = new JButton("Hapus Salinan");
        
        addItemButton.addActionListener(e -> addBookItem());
        editItemButton.addActionListener(e -> editBookItem());
        deleteItemButton.addActionListener(e -> deleteBookItem());
        
        buttonPanel.add(addItemButton);
        buttonPanel.add(editItemButton);
        buttonPanel.add(deleteItemButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Refresh data panel
     */
    public void refreshData() {
        bookTableModel.setBooks(new ArrayList<>(mainWindow.getLibrary().getCollection().getBooks()));
        updateButtonStates();
        clearBookDetails();
    }
    
    /**
     * Update status tombol berdasarkan hak akses dan seleksi
     */
    private void updateButtonStates() {
        boolean isBasic = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC;
        boolean isAdmin = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.ADMIN;
        boolean bookSelected = bookTable.getSelectedRow() != -1;
        boolean itemSelected = itemsTable.getSelectedRow() != -1;
        
        // Tombol buku
        addBookButton.setEnabled(!isBasic);
        editBookButton.setEnabled(!isBasic && bookSelected);
        deleteBookButton.setEnabled(isAdmin && bookSelected);
        
        // Tombol salinan buku
        addItemButton.setEnabled(!isBasic && bookSelected);
        editItemButton.setEnabled(!isBasic && itemSelected);
        deleteItemButton.setEnabled(!isBasic && itemSelected);
    }
    
    /**
     * Cari buku berdasarkan keyword
     */
    private void searchBooks() {
        String keyword = bookSearchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<Book> filteredList = new ArrayList<>();
        for (Book book : mainWindow.getLibrary().getCollection().getBooks()) {
            if (book.getTitle().toLowerCase().contains(keyword) ||
                book.getAuthor().toLowerCase().contains(keyword) ||
                book.getISBN().toLowerCase().contains(keyword) ||
                book.getPublisher().toLowerCase().contains(keyword)) {
                filteredList.add(book);
            }
        }
        
        bookTableModel.setBooks(filteredList);
        clearBookDetails();
    }
    
    /**
     * Tambah buku baru
     */
    private void addBook() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menambah buku!", "Akses Ditolak");
            return;
        }
        
        // Panel form untuk input data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField isbnField = new JTextField(20);
        JTextField titleField = new JTextField(20);
        JTextField authorField = new JTextField(20);
        JTextField publisherField = new JTextField(20);
        JTextField yearField = new JTextField(20);
        JTextField pagesField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        JComboBox<BookFormat> formatCombo = new JComboBox<>(BookFormat.values());
        JComboBox<Language> languageCombo = new JComboBox<>(Language.values());
        
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("Judul:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Pengarang:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Penerbit:"));
        formPanel.add(publisherField);
        formPanel.add(new JLabel("Tahun Terbit:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("Jumlah Halaman:"));
        formPanel.add(pagesField);
        formPanel.add(new JLabel("Format:"));
        formPanel.add(formatCombo);
        formPanel.add(new JLabel("Bahasa:"));
        formPanel.add(languageCombo);
        
        JPanel completePanel = new JPanel(new BorderLayout(5, 5));
        completePanel.add(formPanel, BorderLayout.NORTH);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBorder(BorderFactory.createTitledBorder("Deskripsi"));
        descPanel.add(descScrollPane, BorderLayout.CENTER);
        
        completePanel.add(descPanel, BorderLayout.CENTER);
        
        // Panel untuk kategori
        JPanel categoryPanel = new JPanel(new BorderLayout(5, 5));
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Kategori"));
        
        List<BookCategory> allCategories = mainWindow.getLibrary().getCollection().getCategories();
        JList<BookCategory> categoryList = new JList<>(allCategories.toArray(new BookCategory[0]));
        categoryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        categoryPanel.add(new JScrollPane(categoryList), BorderLayout.CENTER);
        completePanel.add(categoryPanel, BorderLayout.SOUTH);
        
        completePanel.setPreferredSize(new Dimension(500, 600));
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, completePanel, "Tambah Buku", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                if (isbnField.getText().trim().isEmpty() || 
                    titleField.getText().trim().isEmpty() ||
                    authorField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("ISBN, Judul, dan Pengarang tidak boleh kosong!");
                }
                
                int year = 0;
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Tahun harus berupa angka!");
                }
                
                int pages = 0;
                try {
                    pages = Integer.parseInt(pagesField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Jumlah halaman harus berupa angka!");
                }
                
                // Buat objek Book
                Book book = new Book(
                    isbnField.getText().trim(),
                    titleField.getText().trim(),
                    authorField.getText().trim(),
                    publisherField.getText().trim(),
                    year,
                    descriptionArea.getText().trim(),
                    pages,
                    (BookFormat) formatCombo.getSelectedItem(),
                    (Language) languageCombo.getSelectedItem()
                );
                
                // Tambahkan ke library
                mainWindow.addBook(book);
                
                // Tambahkan ke kategori yang dipilih
                int[] selectedIndices = categoryList.getSelectedIndices();
                for (int index : selectedIndices) {
                    if (index >= 0 && index < allCategories.size()) {
                        BookCategory category = allCategories.get(index);
                        mainWindow.addBookToCategory(book, category);
                    }
                }
                
                refreshData();
                
                GUIUtils.infoDialog(this, "Buku berhasil ditambahkan!", "Sukses");
                
                // Tanya user apakah ingin menambahkan salinan buku
                boolean addCopy = GUIUtils.confirmDialog(
                    this, 
                    "Apakah Anda ingin menambahkan salinan buku?", 
                    "Tambah Salinan");
                
                if (addCopy) {
                    // Select the newly added book
                    for (int i = 0; i < bookTableModel.getRowCount(); i++) {
                        if (bookTableModel.getBookAt(i).getISBN().equals(book.getISBN())) {
                            bookTable.setRowSelectionInterval(i, i);
                            displaySelectedBook();
                            break;
                        }
                    }
                    
                    addBookItem();
                }
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Edit buku yang dipilih
     */
    private void editBook() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk mengubah buku!", "Akses Ditolak");
            return;
        }
        
        Book book = getSelectedBook();
        if (book == null) {
            GUIUtils.errorDialog(this, "Pilih buku yang akan diubah!", "Tidak Ada Buku Dipilih");
            return;
        }
        
        // Panel form untuk edit data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField titleField = new JTextField(book.getTitle(), 20);
        JTextField authorField = new JTextField(book.getAuthor(), 20);
        JTextField publisherField = new JTextField(book.getPublisher(), 20);
        JTextField yearField = new JTextField(String.valueOf(book.getPublicationYear()), 20);
        JTextField pagesField = new JTextField(String.valueOf(book.getNumberOfPages()), 20);
        
        JTextArea descriptionArea = new JTextArea(book.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        
        JComboBox<BookFormat> formatCombo = new JComboBox<>(BookFormat.values());
        formatCombo.setSelectedItem(book.getFormat());
        
        JComboBox<Language> languageCombo = new JComboBox<>(Language.values());
        languageCombo.setSelectedItem(book.getLanguage());
        
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(new JLabel(book.getISBN()));
        formPanel.add(new JLabel("Judul:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Pengarang:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Penerbit:"));
        formPanel.add(publisherField);
        formPanel.add(new JLabel("Tahun Terbit:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("Jumlah Halaman:"));
        formPanel.add(pagesField);
        formPanel.add(new JLabel("Format:"));
        formPanel.add(formatCombo);
        formPanel.add(new JLabel("Bahasa:"));
        formPanel.add(languageCombo);
        
        JPanel completePanel = new JPanel(new BorderLayout(5, 5));
        completePanel.add(formPanel, BorderLayout.NORTH);
        
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBorder(BorderFactory.createTitledBorder("Deskripsi"));
        descPanel.add(descScrollPane, BorderLayout.CENTER);
        
        completePanel.add(descPanel, BorderLayout.CENTER);
        
        // Panel untuk kategori
        JPanel categoryPanel = new JPanel(new BorderLayout(5, 5));
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Kategori"));
        
        List<BookCategory> allCategories = mainWindow.getLibrary().getCollection().getCategories();
        JList<BookCategory> categoryList = new JList<>(allCategories.toArray(new BookCategory[0]));
        categoryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Pre-select existing categories
        List<Integer> selectedIndices = new ArrayList<>();
        for (int i = 0; i < allCategories.size(); i++) {
            if (book.getCategories().contains(allCategories.get(i))) {
                selectedIndices.add(i);
            }
        }
        
        int[] indices = new int[selectedIndices.size()];
        for (int i = 0; i < selectedIndices.size(); i++) {
            indices[i] = selectedIndices.get(i);
        }
        categoryList.setSelectedIndices(indices);
        
        categoryPanel.add(new JScrollPane(categoryList), BorderLayout.CENTER);
        completePanel.add(categoryPanel, BorderLayout.SOUTH);
        
        completePanel.setPreferredSize(new Dimension(500, 600));
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, completePanel, "Edit Buku", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                if (titleField.getText().trim().isEmpty() ||
                    authorField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("Judul dan Pengarang tidak boleh kosong!");
                }
                
                int year = 0;
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Tahun harus berupa angka!");
                }
                
                int pages = 0;
                try {
                    pages = Integer.parseInt(pagesField.getText().trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Jumlah halaman harus berupa angka!");
                }
                
                // Update data
                book.setTitle(titleField.getText().trim());
                book.setAuthor(authorField.getText().trim());
                book.setPublisher(publisherField.getText().trim());
                book.setPublicationYear(year);
                book.setNumberOfPages(pages);
                book.setDescription(descriptionArea.getText().trim());
                book.setFormat((BookFormat) formatCombo.getSelectedItem());
                book.setLanguage((Language) languageCombo.getSelectedItem());
                
                // Update kategori
                // Hapus semua kategori
                for (BookCategory category : new ArrayList<>(book.getCategories())) {
                    category.removeBook(book);
                }
                
                // Tambahkan kategori yang dipilih
                int[] selectedNewIndices = categoryList.getSelectedIndices();
                for (int index : selectedNewIndices) {
                    if (index >= 0 && index < allCategories.size()) {
                        BookCategory category = allCategories.get(index);
                        mainWindow.addBookToCategory(book, category);
                    }
                }
                
                refreshData();
                displaySelectedBook();
                
                GUIUtils.infoDialog(this, "Buku berhasil diubah!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Hapus buku yang dipilih
     */
    private void deleteBook() {
        if (mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.ADMIN) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menghapus buku!", "Akses Ditolak");
            return;
        }
        
        Book book = getSelectedBook();
        if (book == null) {
            GUIUtils.errorDialog(this, "Pilih buku yang akan dihapus!", "Tidak Ada Buku Dipilih");
            return;
        }
        
        // Cek apakah buku memiliki salinan yang dipinjam
        boolean hasBorrowedItems = false;
        for (BookItem item : book.getItems()) {
            if (!item.isAvailable()) {
                hasBorrowedItems = true;
                break;
            }
        }
        
        if (hasBorrowedItems) {
            GUIUtils.errorDialog(this, "Buku tidak dapat dihapus karena memiliki salinan yang sedang dipinjam!", "Tidak Dapat Menghapus");
            return;
        }
        
        boolean confirm = GUIUtils.confirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus buku '" + book.getTitle() + "'? Semua salinan buku juga akan dihapus.", 
            "Konfirmasi Hapus");
        
        if (confirm) {
            mainWindow.removeBook(book);
            refreshData();
            GUIUtils.infoDialog(this, "Buku berhasil dihapus!", "Sukses");
        }
    }
    
    /**
     * Tambah salinan buku
     */
    private void addBookItem() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menambah salinan buku!", "Akses Ditolak");
            return;
        }
        
        Book book = getSelectedBook();
        if (book == null) {
            GUIUtils.errorDialog(this, "Pilih buku terlebih dahulu!", "Tidak Ada Buku Dipilih");
            return;
        }
        
        // Panel form untuk input data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField barcodeField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JCheckBox referenceOnlyCheckbox = new JCheckBox("Hanya untuk referensi");
        
        formPanel.add(new JLabel("Barcode:"));
        formPanel.add(barcodeField);
        formPanel.add(new JLabel("Lokasi:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Harga:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel(""));
        formPanel.add(referenceOnlyCheckbox);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Tambah Salinan Buku", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                String barcode = barcodeField.getText().trim();
                if (barcode.isEmpty()) {
                    throw new IllegalArgumentException("Barcode tidak boleh kosong!");
                }
                
                // Cek apakah barcode sudah digunakan
                if (mainWindow.getBookItems().containsKey(barcode)) {
                    throw new IllegalArgumentException("Barcode sudah digunakan oleh salinan buku lain!");
                }
                
                String location = locationField.getText().trim();
                
                double price = 0.0;
                try {
                    if (!priceField.getText().trim().isEmpty()) {
                        price = Double.parseDouble(priceField.getText().trim());
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Harga harus berupa angka!");
                }
                
                boolean referenceOnly = referenceOnlyCheckbox.isSelected();
                
                // Buat salinan buku
                BookItem bookItem = mainWindow.addBookItem(book, barcode);
                bookItem.setLocation(location);
                bookItem.setPrice(price);
                bookItem.setReferenceOnly(referenceOnly);
                
                refreshBookItems();
                
                GUIUtils.infoDialog(this, "Salinan buku berhasil ditambahkan!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Edit salinan buku yang dipilih
     */
    private void editBookItem() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk mengubah salinan buku!", "Akses Ditolak");
            return;
        }
        
        BookItem bookItem = getSelectedBookItem();
        if (bookItem == null) {
            GUIUtils.errorDialog(this, "Pilih salinan buku yang akan diubah!", "Tidak Ada Salinan Dipilih");
            return;
        }
        
        // Panel form untuk edit data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField locationField = new JTextField(bookItem.getLocation(), 20);
        JTextField priceField = new JTextField(String.valueOf(bookItem.getPrice()), 20);
        JCheckBox referenceOnlyCheckbox = new JCheckBox("Hanya untuk referensi", bookItem.isReferenceOnly());
        
        formPanel.add(new JLabel("Barcode:"));
        formPanel.add(new JLabel(bookItem.getBarcode()));
        formPanel.add(new JLabel("Status:"));
        formPanel.add(new JLabel(bookItem.getStatus().toString()));
        formPanel.add(new JLabel("Lokasi:"));
        formPanel.add(locationField);
        formPanel.add(new JLabel("Harga:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel(""));
        formPanel.add(referenceOnlyCheckbox);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Edit Salinan Buku", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                double price = 0.0;
                try {
                    if (!priceField.getText().trim().isEmpty()) {
                        price = Double.parseDouble(priceField.getText().trim());
                    }
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Harga harus berupa angka!");
                }
                
                // Update data
                bookItem.setLocation(locationField.getText().trim());
                bookItem.setPrice(price);
                bookItem.setReferenceOnly(referenceOnlyCheckbox.isSelected());
                
                refreshBookItems();
                
                GUIUtils.infoDialog(this, "Salinan buku berhasil diubah!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Hapus salinan buku yang dipilih
     */
    private void deleteBookItem() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menghapus salinan buku!", "Akses Ditolak");
            return;
        }
        
        BookItem bookItem = getSelectedBookItem();
        if (bookItem == null) {
            GUIUtils.errorDialog(this, "Pilih salinan buku yang akan dihapus!", "Tidak Ada Salinan Dipilih");
            return;
        }
        
        // Cek apakah salinan buku sedang dipinjam
        if (!bookItem.isAvailable()) {
            GUIUtils.errorDialog(this, "Salinan buku tidak dapat dihapus karena sedang dipinjam!", "Tidak Dapat Menghapus");
            return;
        }
        
        boolean confirm = GUIUtils.confirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus salinan buku dengan barcode " + bookItem.getBarcode() + "?", 
            "Konfirmasi Hapus");
        
        if (confirm) {
            Book book = bookItem.getBook();
            book.removeBookItem(bookItem);
            mainWindow.getBookItems().remove(bookItem.getBarcode());
            refreshBookItems();
            GUIUtils.infoDialog(this, "Salinan buku berhasil dihapus!", "Sukses");
        }
    }
    
    /**
     * Mendapatkan buku yang dipilih di tabel
     */
    private Book getSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            return bookTableModel.getBookAt(modelRow);
        }
        return null;
    }
    
    /**
     * Mendapatkan salinan buku yang dipilih di tabel
     */
    private BookItem getSelectedBookItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
            return itemsTableModel.getBookItemAt(modelRow);
        }
        return null;
    }
    
    /**
     * Menampilkan detail buku yang dipilih
     */
    private void displaySelectedBook() {
        Book book = getSelectedBook();
        if (book != null) {
            // Update detail buku
            titleValueLabel.setText(book.getTitle());
            authorValueLabel.setText(book.getAuthor());
            publisherValueLabel.setText(book.getPublisher());
            yearValueLabel.setText(String.valueOf(book.getPublicationYear()));
            isbnValueLabel.setText(book.getISBN());
            formatValueLabel.setText(book.getFormat().toString());
            languageValueLabel.setText(book.getLanguage().toString());
            pagesValueLabel.setText(String.valueOf(book.getNumberOfPages()));
            descriptionArea.setText(book.getDescription());
            
            // Update tabel salinan buku
            refreshBookItems();
        } else {
            clearBookDetails();
        }
        
        updateButtonStates();
    }
    
    /**
     * Refresh tabel salinan buku
     */
    private void refreshBookItems() {
        Book book = getSelectedBook();
        if (book != null) {
            itemsTableModel.setBookItems(new ArrayList<>(book.getItems()));
        } else {
            itemsTableModel.setBookItems(new ArrayList<>());
        }
        updateButtonStates();
    }
    
    /**
     * Clear detail buku
     */
    private void clearBookDetails() {
        titleValueLabel.setText("");
        authorValueLabel.setText("");
        publisherValueLabel.setText("");
        yearValueLabel.setText("");
        isbnValueLabel.setText("");
        formatValueLabel.setText("");
        languageValueLabel.setText("");
        pagesValueLabel.setText("");
        descriptionArea.setText("");
        
        itemsTableModel.setBookItems(new ArrayList<>());
        updateButtonStates();
    }
}