package com.library.gui;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Panel untuk manajemen buku
 */
public class BookPanel extends BasePanel {
    private DefaultTableModel bookModel;
    private JTable booksTable;
    private JButton addButton;
    private JButton addCopyButton;
    private JButton editButton;
    private JButton detailButton;
    private JButton deleteButton;
    private JTextField searchField;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public BookPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Kelola Buku");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel tombol aksi
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = createStyledButton("Tambah Buku", new Color(39, 174, 96));
        addButton.addActionListener(e -> showAddBookDialog());
        
        addCopyButton = createStyledButton("Tambah Salinan", new Color(41, 128, 185));
        addCopyButton.setEnabled(false);
        addCopyButton.addActionListener(e -> addCopyToSelectedBook());
        
        editButton = createStyledButton("Edit Buku", new Color(243, 156, 18));
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editSelectedBook());
        
        detailButton = createStyledButton("Lihat Detail", new Color(52, 152, 219));
        detailButton.setEnabled(false);
        detailButton.addActionListener(e -> viewSelectedBookDetails());
        
        deleteButton = createStyledButton("Hapus Buku", new Color(231, 76, 60));
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(e -> deleteSelectedBook());
        
        // Sesuaikan status tombol berdasarkan izin pustakawan saat ini
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            addButton.setEnabled(false);
            addCopyButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
        
        toolsPanel.add(addButton);
        toolsPanel.add(addCopyButton);
        toolsPanel.add(editButton);
        toolsPanel.add(detailButton);
        toolsPanel.add(deleteButton);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchBooks());
        
        searchPanel.add(new JLabel("Cari:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolsPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.CENTER);
        
        // Tabel buku
        String[] columnNames = {"ISBN", "Judul", "Pengarang", "Penerbit", "Tahun", "Format", "Salinan"};
        bookModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        // Isi data buku
        refreshBooksTable(library.getCollection().getBooks());
        
        booksTable = new JTable(bookModel);
        booksTable.setFillsViewportHeight(true);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener untuk mengaktifkan tombol-tombol aksi
        booksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && booksTable.getSelectedRow() != -1) {
                    boolean hasPermission = currentLibrarian.getPermission() != LibrarianPermission.BASIC;
                    boolean isAdmin = currentLibrarian.getPermission() == LibrarianPermission.ADMIN;
                    
                    addCopyButton.setEnabled(hasPermission);
                    editButton.setEnabled(hasPermission);
                    detailButton.setEnabled(true); // Semua bisa lihat detail
                    deleteButton.setEnabled(isAdmin);
                } else {
                    addCopyButton.setEnabled(false);
                    editButton.setEnabled(false);
                    detailButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(booksTable);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh tabel buku
     * @param bookList Daftar buku yang akan ditampilkan
     */
    private void refreshBooksTable(List<Book> bookList) {
        bookModel.setRowCount(0); // Clear table
        
        for (Book book : bookList) {
            bookModel.addRow(new Object[]{
                book.getISBN(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationYear(),
                book.getFormat().toString(),
                book.getItems().size() + " (" + book.getAvailableItems().size() + " tersedia)"
            });
        }
    }
    
    /**
     * Mencari buku berdasarkan kata kunci
     */
    private void searchBooks() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshBooksTable(library.getCollection().getBooks());
            return;
        }
        
        List<Book> results = new ArrayList<>();
        for (Book book : library.getCollection().getBooks()) {
            if (book.getTitle().toLowerCase().contains(keyword) ||
                book.getAuthor().toLowerCase().contains(keyword) ||
                book.getISBN().toLowerCase().contains(keyword)) {
                results.add(book);
            }
        }
        
        refreshBooksTable(results);
    }
    
    /**
     * Menampilkan dialog untuk menambah buku baru
     */
    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Window) getParent(), "Tambah Buku Baru", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Book info
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField isbnField = new JTextField(15);
        panel.add(isbnField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Judul:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField titleField = new JTextField(15);
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Pengarang:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField authorField = new JTextField(15);
        panel.add(authorField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Penerbit:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField publisherField = new JTextField(15);
        panel.add(publisherField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Tahun Terbit:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField yearField = new JTextField(15);
        panel.add(yearField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Deskripsi:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextArea descriptionArea = new JTextArea(3, 15);
        descriptionArea.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        panel.add(descScrollPane, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Jumlah Halaman:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField pagesField = new JTextField(15);
        panel.add(pagesField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Format:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        String[] formats = {"HARDCOVER", "PAPERBACK", "EBOOK", "AUDIOBOOK"};
        JComboBox<String> formatCombo = new JComboBox<>(formats);
        panel.add(formatCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Bahasa:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 8;
        String[] languages = {"INDONESIAN", "ENGLISH", "JAPANESE", "FRENCH", "GERMAN", "OTHER"};
        JComboBox<String> languageCombo = new JComboBox<>(languages);
        panel.add(languageCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("Kategori:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 9;
        JComboBox<String> categoryCombo = new JComboBox<>();
        categoryCombo.addItem("-- Pilih Kategori --");
        for (BookCategory category : library.getCollection().getCategories()) {
            categoryCombo.addItem(category.getName());
        }
        panel.add(categoryCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validasi input
                if (isbnField.getText().isEmpty() || titleField.getText().isEmpty() || 
                    authorField.getText().isEmpty() || publisherField.getText().isEmpty() ||
                    yearField.getText().isEmpty() || pagesField.getText().isEmpty()) {
                    showErrorMessage("Semua field harus diisi!", "Error");
                    return;
                }
                
                // Cek duplikat ISBN
                Map<String, Book> books = parentFrame.getBooks();
                if (books.containsKey(isbnField.getText().trim())) {
                    showErrorMessage("Buku dengan ISBN tersebut sudah ada!", "Error");
                    return;
                }
                
                // Parse input numerik
                int year, pages;
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                    pages = Integer.parseInt(pagesField.getText().trim());
                } catch (NumberFormatException ex) {
                    showErrorMessage("Tahun dan jumlah halaman harus berupa angka!", "Error");
                    return;
                }
                
                // Buat buku baru
                BookFormat format = BookFormat.valueOf((String) formatCombo.getSelectedItem());
                Language language = Language.valueOf((String) languageCombo.getSelectedItem());
                
                Book book = new Book(
                    isbnField.getText().trim(),
                    titleField.getText().trim(),
                    authorField.getText().trim(),
                    publisherField.getText().trim(),
                    year,
                    descriptionArea.getText().trim(),
                    pages,
                    format,
                    language
                );
                
                // Tambahkan ke library
                library.addBook(book);
                books.put(book.getISBN(), book);
                
                // Tambahkan ke kategori jika dipilih
                if (categoryCombo.getSelectedIndex() > 0) {
                    String categoryName = (String) categoryCombo.getSelectedItem();
                    BookCategory selectedCategory = parentFrame.getCategories().get(categoryName);
                    if (selectedCategory != null) {
                        library.addBookToCategory(book, selectedCategory);
                    }
                }
                
                dialog.dispose();
                showInfoMessage("Buku berhasil ditambahkan!", "Sukses");
                
                // Tanyakan apakah ingin menambahkan salinan buku
                int addCopy = JOptionPane.showConfirmDialog(
                    this, 
                    "Apakah Anda ingin menambahkan salinan buku ini sekarang?",
                    "Tambah Salinan", 
                    JOptionPane.YES_NO_OPTION);
                
                if (addCopy == JOptionPane.YES_OPTION) {
                    showAddBookCopyDialog(book);
                }
                
                // Refresh tabel buku
                refreshBooksTable(library.getCollection().getBooks());
                
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(buttonPanel, gbc);
        
        JScrollPane dialogScrollPane = new JScrollPane(panel);
        dialog.add(dialogScrollPane);
        dialog.setVisible(true);
    }
    
    /**
     * Tambah salinan ke buku yang dipilih
     */
    private void addCopyToSelectedBook() {
        int row = booksTable.getSelectedRow();
        if (row != -1) {
            String isbn = (String) booksTable.getValueAt(row, 0);
            Book selectedBook = parentFrame.getBooks().get(isbn);
            if (selectedBook != null) {
                showAddBookCopyDialog(selectedBook);
            }
        }
    }
    
    /**
     * Edit buku yang dipilih
     */
    private void editSelectedBook() {
        int row = booksTable.getSelectedRow();
        if (row != -1) {
            String isbn = (String) booksTable.getValueAt(row, 0);
            Book selectedBook = parentFrame.getBooks().get(isbn);
            if (selectedBook != null) {
                showEditBookDialog(selectedBook);
            }
        }
    }
    
    /**
     * Lihat detail buku yang dipilih
     */
    private void viewSelectedBookDetails() {
        int row = booksTable.getSelectedRow();
        if (row != -1) {
            String isbn = (String) booksTable.getValueAt(row, 0);
            Book selectedBook = parentFrame.getBooks().get(isbn);
            if (selectedBook != null) {
                showBookDetailsDialog(selectedBook);
            }
        }
    }
    
    /**
     * Hapus buku yang dipilih
     */
    private void deleteSelectedBook() {
        int row = booksTable.getSelectedRow();
        if (row != -1) {
            String isbn = (String) booksTable.getValueAt(row, 0);
            Book selectedBook = parentFrame.getBooks().get(isbn);
            
            if (selectedBook != null) {
                boolean confirm = showConfirmDialog(
                    "Apakah Anda yakin ingin menghapus buku " + selectedBook.getTitle() + "?",
                    "Konfirmasi Hapus");
                
                if (confirm) {
                    library.getCollection().removeBook(selectedBook);
                    parentFrame.getBooks().remove(selectedBook.getISBN());
                    refreshBooksTable(library.getCollection().getBooks());
                    showInfoMessage("Buku berhasil dihapus.", "Sukses");
                }
            }
        }
    }
    
    private void showEditBookDialog(Book book) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Buku", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Book info
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField isbnField = new JTextField(book.getISBN(), 15);
        isbnField.setEditable(false);
        panel.add(isbnField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Judul:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField titleField = new JTextField(book.getTitle(), 15);
        panel.add(titleField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Pengarang:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField authorField = new JTextField(book.getAuthor(), 15);
        panel.add(authorField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Penerbit:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField publisherField = new JTextField(book.getPublisher(), 15);
        panel.add(publisherField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Tahun Terbit:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField yearField = new JTextField(String.valueOf(book.getPublicationYear()), 15);
        panel.add(yearField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Deskripsi:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextArea descriptionArea = new JTextArea(book.getDescription(), 3, 15);
        descriptionArea.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        panel.add(descScrollPane, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Jumlah Halaman:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField pagesField = new JTextField(String.valueOf(book.getPages()), 15);
        panel.add(pagesField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Format:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        String[] formats = {"HARDCOVER", "PAPERBACK", "EBOOK", "AUDIOBOOK"};
        JComboBox<String> formatCombo = new JComboBox<>(formats);
        formatCombo.setSelectedItem(book.getFormat().toString());
        panel.add(formatCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Bahasa:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 8;
        String[] languages = {"INDONESIAN", "ENGLISH", "JAPANESE", "FRENCH", "GERMAN", "OTHER"};
        JComboBox<String> languageCombo = new JComboBox<>(languages);
        languageCombo.setSelectedItem(book.getLanguage().toString());
        panel.add(languageCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("Kategori:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 9;
        JComboBox<String> categoryCombo = new JComboBox<>();
        categoryCombo.addItem("-- Pilih Kategori --");
        for (BookCategory category : library.getCollection().getCategories()) {
            categoryCombo.addItem(category.getName());
        }
        panel.add(categoryCombo, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validasi input
                if (titleField.getText().isEmpty() || authorField.getText().isEmpty() || 
                    publisherField.getText().isEmpty() || yearField.getText().isEmpty() || 
                    pagesField.getText().isEmpty()) {
                    showErrorMessage("Semua field harus diisi!", "Error");
                    return;
                }
                
                // Parse input numerik
                int year, pages;
                try {
                    year = Integer.parseInt(yearField.getText().trim());
                    pages = Integer.parseInt(pagesField.getText().trim());
                } catch (NumberFormatException ex) {
                    showErrorMessage("Tahun dan jumlah halaman harus berupa angka!", "Error");
                    return;
                }
                
                // Update buku
                book.setTitle(titleField.getText().trim());
                book.setAuthor(authorField.getText().trim());
                book.setPublisher(publisherField.getText().trim());
                book.setPublicationYear(year);
                book.setDescription(descriptionArea.getText().trim());
                book.setPages(pages);
                book.setFormat(BookFormat.valueOf((String) formatCombo.getSelectedItem()));
                book.setLanguage(Language.valueOf((String) languageCombo.getSelectedItem()));
                
                // Update kategori jika dipilih
                if (categoryCombo.getSelectedIndex() > 0) {
                    String categoryName = (String) categoryCombo.getSelectedItem();
                    BookCategory selectedCategory = parentFrame.getCategories().get(categoryName);
                    if (selectedCategory != null) {
                        library.addBookToCategory(book, selectedCategory);
                    }
                }
                
                dialog.dispose();
                showInfoMessage("Buku berhasil diperbarui!", "Sukses");
                
                // Refresh tabel buku
                refreshBooksTable(library.getCollection().getBooks());
                
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(buttonPanel, gbc);
        
        JScrollPane dialogScrollPane = new JScrollPane(panel);
        dialog.add(dialogScrollPane);
        dialog.setVisible(true);
    }
    
    /**
     * Menampilkan dialog untuk menambah salinan buku
     * @param book Buku yang akan ditambah salinannya
     */
    private void showAddBookCopyDialog(Book book) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tambah Salinan Buku", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Book info
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Buku:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField bookField = new JTextField(book.getTitle(), 15);
        bookField.setEditable(false);
        panel.add(bookField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("ISBN:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField isbnField = new JTextField(book.getISBN(), 15);
        isbnField.setEditable(false);
        panel.add(isbnField, gbc);
        
        // Copy info
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Kode Barcode:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField barcodeField = new JTextField(15);
        panel.add(barcodeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Harga:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField priceField = new JTextField("0.0", 15);
        panel.add(priceField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Lokasi:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField locationField = new JTextField(15);
        panel.add(locationField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Hanya Referensi:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JCheckBox referenceOnlyCheck = new JCheckBox();
        panel.add(referenceOnlyCheck, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Tutup");
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        
        panel.add(buttonPanel, gbc);
        
        JScrollPane dialogScrollPane = new JScrollPane(panel);
        dialog.add(dialogScrollPane);
        dialog.setVisible(true);
    }
}