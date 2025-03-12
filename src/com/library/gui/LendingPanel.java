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
 * Panel untuk manajemen peminjaman dan pengembalian buku
 */
public class LendingPanel extends BasePanel {
    private JTabbedPane tabbedPane;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public LendingPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Kelola Peminjaman & Pengembalian");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel dengan tabs untuk berbagai fungsi peminjaman
        tabbedPane = new JTabbedPane();
        
        // Tab Peminjaman Baru
        tabbedPane.addTab("Pinjamkan Buku", createIssueBooksPanel());
        
        // Tab Pengembalian
        tabbedPane.addTab("Kembalikan Buku", createReturnBooksPanel());
        
        // Tab Peminjaman Aktif
        tabbedPane.addTab("Peminjaman Aktif", createActiveLoansPanel());
        
        // Tab Riwayat Peminjaman
        tabbedPane.addTab("Riwayat Peminjaman", createLoanHistoryPanel());
        
        // Tab Perpanjangan Peminjaman
        tabbedPane.addTab("Perpanjang Peminjaman", createExtendLoanPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Membuat panel untuk pinjamkan buku
     */
    private JPanel createIssueBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel untuk memilih anggota
        JPanel memberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        memberPanel.setBorder(BorderFactory.createTitledBorder("Pilih Anggota"));
        
        memberPanel.add(new JLabel("Anggota:"));
        
        JComboBox<String> memberCombo = new JComboBox<>();
        memberCombo.setPreferredSize(new Dimension(300, 25));
        for (Member member : parentFrame.getMembers()) {
            memberCombo.addItem(member.getMemberId() + " - " + member.getName());
        }
        memberPanel.add(memberCombo);
        
        // Panel untuk memilih buku
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBorder(BorderFactory.createTitledBorder("Pilih Buku"));
        
        // Tabel buku
        String[] bookColumns = {"ISBN", "Judul", "Pengarang", "Tersedia"};
        DefaultTableModel bookModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data buku yang tersedia
        for (Book book : library.getCollection().getBooks()) {
            if (!book.getAvailableItems().isEmpty()) {
                bookModel.addRow(new Object[]{
                    book.getISBN(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getAvailableItems().size() + " salinan"
                });
            }
        }
        
        JTable booksTable = new JTable(bookModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setFillsViewportHeight(true);
        JScrollPane bookScrollPane = new JScrollPane(booksTable);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);
        
        // Panel untuk memilih salinan buku
        JPanel copyPanel = new JPanel(new BorderLayout());
        copyPanel.setBorder(BorderFactory.createTitledBorder("Salinan Buku"));
        
        String[] copyColumns = {"Barcode", "Status", "Lokasi", "Jenis"};
        DefaultTableModel copyModel = new DefaultTableModel(copyColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable copiesTable = new JTable(copyModel);
        copiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        copiesTable.setFillsViewportHeight(true);
        JScrollPane copyScrollPane = new JScrollPane(copiesTable);
        copyPanel.add(copyScrollPane, BorderLayout.CENTER);
        
        // Ketika buku dipilih, update tabel salinan
        booksTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && booksTable.getSelectedRow() != -1) {
                    String isbn = (String) booksTable.getValueAt(booksTable.getSelectedRow(), 0);
                    Book selectedBook = parentFrame.getBooks().get(isbn);
                    
                    if (selectedBook != null) {
                        // Clear model
                        copyModel.setRowCount(0);
                        
                        // Add available copies
                        for (BookItem item : selectedBook.getAvailableItems()) {
                            if (!item.isReferenceOnly()) { // Hanya tampilkan yang bisa dipinjam
                                copyModel.addRow(new Object[]{
                                    item.getBarcode(),
                                    item.getStatus(),
                                    item.getLocation(),
                                    item.isReferenceOnly() ? "Hanya Referensi" : "Dapat Dipinjam"
                                });
                            }
                        }
                    }
                }
            }
        });
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton issueButton = createStyledButton("Pinjamkan Buku", new Color(39, 174, 96));
        issueButton.addActionListener(e -> {
            if (memberCombo.getSelectedIndex() == -1) {
                showErrorMessage("Silakan pilih anggota!", "Error");
                return;
            }
            
            if (booksTable.getSelectedRow() == -1) {
                showErrorMessage("Silakan pilih buku!", "Error");
                return;
            }
            
            if (copiesTable.getSelectedRow() == -1) {
                showErrorMessage("Silakan pilih salinan buku!", "Error");
                return;
            }
            
            // Ambil anggota yang dipilih
            String memberString = (String) memberCombo.getSelectedItem();
            String memberId = memberString.split(" - ")[0];
            Member selectedMember = findMemberById(memberId);
            
            // Ambil salinan buku yang dipilih
            String barcode = (String) copiesTable.getValueAt(copiesTable.getSelectedRow(), 0);
            BookItem selectedItem = parentFrame.getBookItems().get(barcode);
            
            if (selectedMember != null && selectedItem != null) {
                try {
                    // Pinjamkan buku
                    BookLoan loan = currentLibrarian.issueBook(selectedMember, selectedItem);
                    parentFrame.getLoans().put(loan.getLoanId(), loan);
                    
                    showInfoMessage(
                            "Buku berhasil dipinjamkan kepada " + selectedMember.getName() + "\n" +
                            "ID Peminjaman: " + loan.getLoanId() + "\n" +
                            "Tanggal Jatuh Tempo: " + dateFormat.format(loan.getDueDate()),
                            "Sukses");
                    
                    // Refresh tabel
                    bookModel.setRowCount(0);
                    for (Book book : library.getCollection().getBooks()) {
                        if (!book.getAvailableItems().isEmpty()) {
                            bookModel.addRow(new Object[]{
                                book.getISBN(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getAvailableItems().size() + " salinan"
                            });
                        }
                    }
                    
                    copyModel.setRowCount(0);
                    
                } catch (Exception ex) {
                    showErrorMessage("Error: " + ex.getMessage(), "Error");
                }
            }
        });
        
        buttonPanel.add(issueButton);
        
        // Gabungkan semua komponen
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(memberPanel, BorderLayout.NORTH);
        topPanel.add(bookPanel, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(copyPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setResizeWeight(0.5);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Membuat panel untuk mengembalikan buku
     */
    private JPanel createReturnBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("ID Peminjaman:"));
        JTextField loanIdField = new JTextField(15);
        searchPanel.add(loanIdField);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Tampilkan Semua", new Color(52, 152, 219));
        searchPanel.add(showAllButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabel peminjaman aktif
        String[] loanColumns = {"ID Peminjaman", "Anggota", "Buku", "Barcode", "Tanggal Pinjam", "Jatuh Tempo", "Status"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data peminjaman aktif
        for (BookLoan loan : parentFrame.getLoans().values()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                loanModel.addRow(new Object[]{
                    loan.getLoanId(),
                    loan.getMember().getName(),
                    loan.getBookItem().getBook().getTitle(),
                    loan.getBookItem().getBarcode(),
                    dateFormat.format(loan.getIssueDate()),
                    dateFormat.format(loan.getDueDate()),
                    loan.getStatus()
                });
            }
        }
        
        JTable loansTable = new JTable(loanModel);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loansTable.setFillsViewportHeight(true);
        JScrollPane loansScrollPane = new JScrollPane(loansTable);
        panel.add(loansScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        JButton returnButton = createStyledButton("Kembalikan Buku", new Color(39, 174, 96));
        returnButton.setEnabled(false);
        
        // Enable return button when a row is selected
        loansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                returnButton.setEnabled(loansTable.getSelectedRow() != -1);
            }
        });
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String loanId = loanIdField.getText().trim();
            if (loanId.isEmpty()) {
                showErrorMessage("Masukkan ID peminjaman!", "Error");
                return;
            }
            
            BookLoan loan = parentFrame.getLoans().get(loanId);
            if (loan == null) {
                showErrorMessage("Peminjaman tidak ditemukan!", "Error");
                return;
            }
            
            if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
                showErrorMessage("Peminjaman ini sudah dikembalikan atau dibatalkan!", "Error");
                return;
            }
            
            // Clear model
            loanModel.setRowCount(0);
            
            // Add the found loan
            loanModel.addRow(new Object[]{
                loan.getLoanId(),
                loan.getMember().getName(),
                loan.getBookItem().getBook().getTitle(),
                loan.getBookItem().getBarcode(),
                dateFormat.format(loan.getIssueDate()),
                dateFormat.format(loan.getDueDate()),
                loan.getStatus()
            });
        });
        
        // Aksi tampilkan semua
        showAllButton.addActionListener(e -> {
            // Clear model
            loanModel.setRowCount(0);
            
            // Add all active loans
            for (BookLoan loan : parentFrame.getLoans().values()) {
                if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                    loanModel.addRow(new Object[]{
                        loan.getLoanId(),
                        loan.getMember().getName(),
                        loan.getBookItem().getBook().getTitle(),
                        loan.getBookItem().getBarcode(),
                        dateFormat.format(loan.getIssueDate()),
                        dateFormat.format(loan.getDueDate()),
                        loan.getStatus()
                    });
                }
            }
        });
        
        // Aksi refresh
        refreshButton.addActionListener(e -> {
            // Reuse showAllButton action
            showAllButton.doClick();
        });
        
        // Aksi pengembalian
        returnButton.addActionListener(e -> {
            int row = loansTable.getSelectedRow();
            if (row != -1) {
                String loanId = (String) loansTable.getValueAt(row, 0);
                BookLoan loan = parentFrame.getLoans().get(loanId);
                
                if (loan != null) {
                    try {
                        currentLibrarian.returnBook(loan);
                        
                        // Setelah pengembalian, status akan berubah menjadi COMPLETED sesuai enum
                        // loan.getStatus() == LoanStatus.COMPLETED
                        
                        double fine = loan.getFine();
                        String message = "Buku berhasil dikembalikan.";
                        
                        if (fine > 0) {
                            message += "\nDenda: Rp" + String.format("%.2f", fine);
                            
                            int payNow = JOptionPane.showConfirmDialog(
                                panel,
                                "Denda sebesar Rp" + String.format("%.2f", fine) + " dikenakan.\nApakah denda akan dibayar sekarang?",
                                "Konfirmasi Pembayaran Denda",
                                JOptionPane.YES_NO_OPTION
                            );
                            
                            if (payNow == JOptionPane.YES_OPTION) {
                                loan.getMember().payFine(fine);
                                message += "\nDenda telah dibayar.";
                            } else {
                                message += "\nDenda belum dibayar.";
                            }
                        }
                        
                        showInfoMessage(message, "Sukses");
                        
                        // Refresh table
                        showAllButton.doClick();
                        
                    } catch (Exception ex) {
                        showErrorMessage("Error: " + ex.getMessage(), "Error");
                    }
                }
            }
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(returnButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Membuat panel untuk menampilkan peminjaman aktif
     */
    private JPanel createActiveLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Cari:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Tampilkan Semua", new Color(52, 152, 219));
        searchPanel.add(showAllButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabel peminjaman aktif
        String[] loanColumns = {"ID Peminjaman", "Anggota", "Buku", "Barcode", "Tanggal Pinjam", "Jatuh Tempo", "Status"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data peminjaman aktif
        refreshActiveLoansTable(loanModel);
        
        JTable loansTable = new JTable(loanModel);
        loansTable.setFillsViewportHeight(true);
        JScrollPane loansScrollPane = new JScrollPane(loansTable);
        panel.add(loansScrollPane, BorderLayout.CENTER);
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                refreshActiveLoansTable(loanModel);
                return;
            }
            
            // Clear model
            loanModel.setRowCount(0);
            
            // Add matching loans
            for (BookLoan loan : parentFrame.getLoans().values()) {
                if ((loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) &&
                    (loan.getMember().getName().toLowerCase().contains(keyword) ||
                     loan.getBookItem().getBook().getTitle().toLowerCase().contains(keyword) ||
                     loan.getLoanId().toLowerCase().contains(keyword))) {
                    
                    loanModel.addRow(new Object[]{
                        loan.getLoanId(),
                        loan.getMember().getName(),
                        loan.getBookItem().getBook().getTitle(),
                        loan.getBookItem().getBarcode(),
                        dateFormat.format(loan.getIssueDate()),
                        dateFormat.format(loan.getDueDate()),
                        loan.getStatus()
                    });
                }
            }
        });
        
        // Aksi tampilkan semua
        showAllButton.addActionListener(e -> {
            refreshActiveLoansTable(loanModel);
        });
        
        // Panel tombol refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            refreshActiveLoansTable(loanModel);
        });
        
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Refresh tabel peminjaman aktif
     */
    private void refreshActiveLoansTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        for (BookLoan loan : parentFrame.getLoans().values()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                model.addRow(new Object[]{
                    loan.getLoanId(),
                    loan.getMember().getName(),
                    loan.getBookItem().getBook().getTitle(),
                    loan.getBookItem().getBarcode(),
                    dateFormat.format(loan.getIssueDate()),
                    dateFormat.format(loan.getDueDate()),
                    loan.getStatus()
                });
            }
        }
    }
    
    /**
     * Membuat panel untuk menampilkan riwayat peminjaman
     */
    private JPanel createLoanHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("Cari:"));
        JTextField searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Tampilkan Semua", new Color(52, 152, 219));
        searchPanel.add(showAllButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabel riwayat peminjaman
        String[] loanColumns = {"ID Peminjaman", "Anggota", "Buku", "Tgl Pinjam", "Jatuh Tempo", "Tgl Kembali", "Status", "Denda"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data riwayat peminjaman
        refreshLoanHistoryTable(loanModel);
        
        JTable loansTable = new JTable(loanModel);
        loansTable.setFillsViewportHeight(true);
        JScrollPane loansScrollPane = new JScrollPane(loansTable);
        panel.add(loansScrollPane, BorderLayout.CENTER);
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                refreshLoanHistoryTable(loanModel);
                return;
            }
            
            // Clear model
            loanModel.setRowCount(0);
            
            // Add matching loans
            for (BookLoan loan : parentFrame.getLoans().values()) {
                if (loan.getMember().getName().toLowerCase().contains(keyword) ||
                    loan.getBookItem().getBook().getTitle().toLowerCase().contains(keyword) ||
                    loan.getLoanId().toLowerCase().contains(keyword)) {
                    
                    String returnDate = loan.getReturnDate() != null ? 
                            dateFormat.format(loan.getReturnDate()) : "-";
                    
                    loanModel.addRow(new Object[]{
                        loan.getLoanId(),
                        loan.getMember().getName(),
                        loan.getBookItem().getBook().getTitle(),
                        dateFormat.format(loan.getIssueDate()),
                        dateFormat.format(loan.getDueDate()),
                        returnDate,
                        loan.getStatus(),
                        String.format("Rp%.2f", loan.getFine())
                    });
                }
            }
        });
        
        // Aksi tampilkan semua
        showAllButton.addActionListener(e -> {
            refreshLoanHistoryTable(loanModel);
        });
        
        // Panel tombol refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            refreshLoanHistoryTable(loanModel);
        });
        
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Refresh tabel riwayat peminjaman
     */
    private void refreshLoanHistoryTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        for (BookLoan loan : parentFrame.getLoans().values()) {
            String returnDate = loan.getReturnDate() != null ? 
                    dateFormat.format(loan.getReturnDate()) : "-";
            
            model.addRow(new Object[]{
                loan.getLoanId(),
                loan.getMember().getName(),
                loan.getBookItem().getBook().getTitle(),
                dateFormat.format(loan.getIssueDate()),
                dateFormat.format(loan.getDueDate()),
                returnDate,
                loan.getStatus(),
                String.format("Rp%.2f", loan.getFine())
            });
        }
    }
    
    /**
     * Membuat panel untuk perpanjangan peminjaman
     */
    private JPanel createExtendLoanPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("ID Peminjaman:"));
        JTextField loanIdField = new JTextField(15);
        searchPanel.add(loanIdField);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Tampilkan Peminjaman Aktif", new Color(52, 152, 219));
        searchPanel.add(showAllButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabel peminjaman aktif
        String[] loanColumns = {"ID Peminjaman", "Anggota", "Buku", "Tanggal Pinjam", "Jatuh Tempo", "Status"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data peminjaman aktif
        for (BookLoan loan : parentFrame.getLoans().values()) {
            if (loan.getStatus() == LoanStatus.ACTIVE) { // Hanya tampilkan yang ACTIVE, bukan OVERDUE
                loanModel.addRow(new Object[]{
                    loan.getLoanId(),
                    loan.getMember().getName(),
                    loan.getBookItem().getBook().getTitle(),
                    dateFormat.format(loan.getIssueDate()),
                    dateFormat.format(loan.getDueDate()),
                    loan.getStatus()
                });
            }
        }
        
        JTable loansTable = new JTable(loanModel);
        loansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loansTable.setFillsViewportHeight(true);
        JScrollPane loansScrollPane = new JScrollPane(loansTable);
        panel.add(loansScrollPane, BorderLayout.CENTER);
        
        // Panel perpanjangan
        JPanel extendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        extendPanel.add(new JLabel("Jumlah Hari Perpanjangan:"));
        JTextField daysField = new JTextField("7", 5);
        extendPanel.add(daysField);
        
        JButton extendButton = createStyledButton("Perpanjang", new Color(39, 174, 96));
        extendButton.setEnabled(false);
        extendPanel.add(extendButton);
        
        panel.add(extendPanel, BorderLayout.SOUTH);
        
        // Enable extend button when a row is selected
        loansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                extendButton.setEnabled(loansTable.getSelectedRow() != -1);
            }
        });
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String loanId = loanIdField.getText().trim();
            if (loanId.isEmpty()) {
                showErrorMessage("Masukkan ID peminjaman!", "Error");
                return;
            }
            
            BookLoan loan = parentFrame.getLoans().get(loanId);
            if (loan == null) {
                showErrorMessage("Peminjaman tidak ditemukan!", "Error");
                return;
            }
            
            if (loan.getStatus() != LoanStatus.ACTIVE) {
                showErrorMessage("Hanya peminjaman dengan status ACTIVE yang dapat diperpanjang!", "Error");
                return;
            }
            
            // Clear model
            loanModel.setRowCount(0);
            
            // Add the found loan
            loanModel.addRow(new Object[]{
                loan.getLoanId(),
                loan.getMember().getName(),
                loan.getBookItem().getBook().getTitle(),
                dateFormat.format(loan.getIssueDate()),
                dateFormat.format(loan.getDueDate()),
                loan.getStatus()
            });
        });
        
        // Aksi tampilkan semua
        showAllButton.addActionListener(e -> {
            // Clear model
            loanModel.setRowCount(0);
            
            // Add all active loans
            for (BookLoan loan : parentFrame.getLoans().values()) {
                if (loan.getStatus() == LoanStatus.ACTIVE) {
                    loanModel.addRow(new Object[]{
                        loan.getLoanId(),
                        loan.getMember().getName(),
                        loan.getBookItem().getBook().getTitle(),
                        dateFormat.format(loan.getIssueDate()),
                        dateFormat.format(loan.getDueDate()),
                        loan.getStatus()
                    });
                }
            }
        });
        
        // Aksi perpanjangan
        extendButton.addActionListener(e -> {
            int row = loansTable.getSelectedRow();
            if (row != -1) {
                String loanId = (String) loansTable.getValueAt(row, 0);
                BookLoan loan = parentFrame.getLoans().get(loanId);
                
                if (loan != null) {
                    // Parse input
                    int days;
                    try {
                        days = Integer.parseInt(daysField.getText().trim());
                    } catch (NumberFormatException ex) {
                        showErrorMessage("Jumlah hari harus berupa angka!", "Error");
                        return;
                    }
                    
                    if (days <= 0) {
                        showErrorMessage("Jumlah hari harus positif!", "Error");
                        return;
                    }
                    
                    // Extend loan
                    boolean extended = loan.extendDueDate(days);
                    
                    if (extended) {
                        showInfoMessage(
                                "Peminjaman berhasil diperpanjang:\n" +
                                "Tanggal Jatuh Tempo Baru: " + dateFormat.format(loan.getDueDate()),
                                "Sukses");
                        
                        // Update row
                        loansTable.setValueAt(dateFormat.format(loan.getDueDate()), row, 4);
                    } else {
                        showErrorMessage(
                                "Tidak dapat memperpanjang peminjaman. Kemungkinan penyebab:\n" +
                                "- Buku sudah terlambat\n" +
                                "- Buku memiliki reservasi dari anggota lain",
                                "Gagal");
                    }
                }
            }
        });
        
        return panel;
    }
}