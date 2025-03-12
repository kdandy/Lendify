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

/**
 * Panel untuk pencarian buku
 */
public class SearchPanel extends BasePanel {
    private JTextField searchField;
    private JComboBox<String> searchTypeCombo;
    private DefaultTableModel resultModel;
    private JTable resultsTable;
    private JButton detailButton;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public SearchPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Cari Buku");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        
        searchField = new JTextField(30);
        searchPanel.add(searchField);
        
        String[] searchTypes = {"Judul", "Pengarang", "Kategori"};
        searchTypeCombo = new JComboBox<>(searchTypes);
        searchPanel.add(searchTypeCombo);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);
        
        add(searchPanel, BorderLayout.CENTER);
        
        // Panel hasil pencarian
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Hasil Pencarian"));
        
        // Tabel hasil pencarian
        String[] resultColumns = {"ISBN", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Tersedia"};
        resultModel = new DefaultTableModel(resultColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        resultsTable = new JTable(resultModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setFillsViewportHeight(true);
        JScrollPane resultsScrollPane = new JScrollPane(resultsTable);
        resultsPanel.add(resultsScrollPane, BorderLayout.CENTER);
        
        // Panel tombol detail
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        detailButton = createStyledButton("Lihat Detail", new Color(52, 152, 219));
        detailButton.setEnabled(false);
        detailButton.addActionListener(e -> viewSelectedBookDetails());
        buttonPanel.add(detailButton);
        
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(resultsPanel, BorderLayout.SOUTH);
        
        // Enable detail button when a row is selected
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                detailButton.setEnabled(resultsTable.getSelectedRow() != -1);
            }
        });
    }
    
    /**
     * Mencari buku berdasarkan kriteria
     */
    private void searchBooks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            showErrorMessage("Masukkan kata kunci pencarian!", "Error");
            return;
        }
        
        String searchType = (String) searchTypeCombo.getSelectedItem();
        List<Book> results = new ArrayList<>();
        
        if (searchType.equals("Judul")) {
            results = library.searchByTitle(keyword);
        } else if (searchType.equals("Pengarang")) {
            results = library.searchByAuthor(keyword);
        } else if (searchType.equals("Kategori")) {
            // Cari kategori yang cocok
            BookCategory category = null;
            for (BookCategory cat : library.getCollection().getCategories()) {
                if (cat.getName().toLowerCase().contains(keyword.toLowerCase())) {
                    category = cat;
                    break;
                }
            }
            
            if (category != null) {
                results = library.searchByCategory(category);
            }
        }
        
        // Clear model
        resultModel.setRowCount(0);
        
        // Add search results
        for (Book book : results) {
            // Get categories as string
            StringBuilder categoryString = new StringBuilder();
            for (BookCategory category : book.getCategories()) {
                if (categoryString.length() > 0) {
                    categoryString.append(", ");
                }
                categoryString.append(category.getName());
            }
            
            resultModel.addRow(new Object[]{
                book.getISBN(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublicationYear(),
                categoryString.toString(),
                book.getAvailableItems().size() + "/" + book.getItems().size()
            });
        }
        
        if (results.isEmpty()) {
            showInfoMessage("Tidak ditemukan buku yang sesuai dengan kriteria pencarian.", "Info");
        }
    }
    
    /**
     * Lihat detail buku yang dipilih
     */
    private void viewSelectedBookDetails() {
        int row = resultsTable.getSelectedRow();
        if (row != -1) {
            String isbn = (String) resultsTable.getValueAt(row, 0);
            Book selectedBook = parentFrame.getBooks().get(isbn);
            if (selectedBook != null) {
                showBookDetailsDialog(selectedBook);
            }
        }
    }
    
    /**
     * Menampilkan dialog detail buku
     * @param book Buku yang akan ditampilkan detailnya
     */
    private void showBookDetailsDialog(Book book) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detail Buku", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel info buku
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        infoPanel.add(new JLabel("ISBN:"));
        infoPanel.add(new JLabel(book.getISBN()));
        
        infoPanel.add(new JLabel("Judul:"));
        infoPanel.add(new JLabel(book.getTitle()));
        
        infoPanel.add(new JLabel("Pengarang:"));
        infoPanel.add(new JLabel(book.getAuthor()));
        
        infoPanel.add(new JLabel("Penerbit:"));
        infoPanel.add(new JLabel(book.getPublisher()));
        
        infoPanel.add(new JLabel("Tahun Terbit:"));
        infoPanel.add(new JLabel(String.valueOf(book.getPublicationYear())));
        
        infoPanel.add(new JLabel("Jumlah Halaman:"));
        infoPanel.add(new JLabel(String.valueOf(book.getNumberOfPages())));
        
        infoPanel.add(new JLabel("Format:"));
        infoPanel.add(new JLabel(book.getFormat().toString()));
        
        infoPanel.add(new JLabel("Bahasa:"));
        infoPanel.add(new JLabel(book.getLanguage().toString()));
        
        infoPanel.add(new JLabel("Kategori:"));
        StringBuilder categories = new StringBuilder();
        for (BookCategory category : book.getCategories()) {
            if (categories.length() > 0) {
                categories.append(", ");
            }
            categories.append(category.getName());
        }
        infoPanel.add(new JLabel(categories.toString()));
        
        infoPanel.add(new JLabel("Deskripsi:"));
        JTextArea descArea = new JTextArea(book.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(panel.getBackground());
        JScrollPane descScrollPane = new JScrollPane(descArea);
        descScrollPane.setBorder(null);
        infoPanel.add(descScrollPane);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        
        // Tabel salinan buku
        String[] columnNames = {"Barcode", "Status", "Lokasi", "Harga", "Jenis"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        for (BookItem item : book.getItems()) {
            model.addRow(new Object[]{
                item.getBarcode(),
                item.getStatus(),
                item.getLocation(),
                String.format("Rp%.2f", item.getPrice()),
                item.isReferenceOnly() ? "Hanya Referensi" : "Dapat Dipinjam"
            });
        }
        
        JTable itemsTable = new JTable(model);
        itemsTable.setFillsViewportHeight(true);
        
        JScrollPane tableScrollPane = new JScrollPane(itemsTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Tutup");
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}