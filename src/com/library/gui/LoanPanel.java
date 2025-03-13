package com.library.gui;

import com.library.enums.LibrarianPermission;
import com.library.enums.LoanStatus;
import com.library.gui.utils.DialogUtils;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.LoanTableModel;
import com.library.model.Book;
import com.library.model.BookItem;
import com.library.model.BookLoan;
import com.library.model.Member;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel untuk kelola peminjaman dan pengembalian buku
 */
public class LoanPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JTabbedPane tabbedPane;
    private JTable activeLoansTable;
    private JTable loanHistoryTable;
    private LoanTableModel activeLoansTableModel;
    private LoanTableModel loanHistoryTableModel;
    private JTextField searchActiveField;
    private JTextField searchHistoryField;
    private JButton issueButton;
    private JButton returnButton;
    private JButton extendButton;
    private JButton detailsButton;
    private JButton backButton;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Constructor untuk LoanPanel
     */
    public LoanPanel(LendifyGUI mainWindow) {
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
        JLabel titleLabel = new JLabel("Kelola Peminjaman dan Pengembalian", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);
        
        // Tabbed pane untuk peminjaman aktif dan riwayat
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Peminjaman Aktif", createActiveLoansPanel());
        tabbedPane.addTab("Riwayat Peminjaman", createLoanHistoryPanel());
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        issueButton = new JButton("Pinjamkan Buku");
        returnButton = new JButton("Kembalikan Buku");
        extendButton = new JButton("Perpanjang Peminjaman");
        detailsButton = new JButton("Lihat Detail");
        backButton = new JButton("Kembali");
        
        issueButton.addActionListener(e -> issueBook());
        returnButton.addActionListener(e -> returnBook());
        extendButton.addActionListener(e -> extendLoan());
        detailsButton.addActionListener(e -> viewLoanDetails());
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        
        buttonPanel.add(issueButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(extendButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set initial button states
        updateButtonStates();
        
        // Listener untuk tab change
        tabbedPane.addChangeListener(e -> updateButtonStates());
    }
    
    /**
     * Buat panel untuk peminjaman aktif
     */
    private JPanel createActiveLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchActiveField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchActiveLoans());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(searchActiveField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panel tabel
        activeLoansTableModel = new LoanTableModel(new ArrayList<>());
        activeLoansTable = new JTable(activeLoansTableModel);
        activeLoansTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeLoansTable.setRowHeight(25);
        activeLoansTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<LoanTableModel> sorter = new TableRowSorter<>(activeLoansTableModel);
        activeLoansTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        activeLoansTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewLoanDetails();
                }
            }
        });
        
        // Selection listener untuk update status tombol
        activeLoansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Panel untuk tabel dengan scrolling
        JScrollPane tableScrollPane = new JScrollPane(activeLoansTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Buat panel untuk riwayat peminjaman
     */
    private JPanel createLoanHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchHistoryField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchLoanHistory());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(searchHistoryField);
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Panel tabel
        loanHistoryTableModel = new LoanTableModel(new ArrayList<>());
        loanHistoryTable = new JTable(loanHistoryTableModel);
        loanHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loanHistoryTable.setRowHeight(25);
        loanHistoryTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<LoanTableModel> sorter = new TableRowSorter<>(loanHistoryTableModel);
        loanHistoryTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        loanHistoryTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewLoanDetails();
                }
            }
        });
        
        // Selection listener untuk update status tombol
        loanHistoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Panel untuk tabel dengan scrolling
        JScrollPane tableScrollPane = new JScrollPane(loanHistoryTable);
        panel.add(tableScrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Refresh data panel
     */
    public void refreshData() {
        // Kumpulkan semua data peminjaman dari anggota
        List<BookLoan> allLoans = new ArrayList<>();
        List<BookLoan> activeLoans = new ArrayList<>();
        List<BookLoan> completedLoans = new ArrayList<>();
        
        for (Member member : mainWindow.getMembers()) {
            for (BookLoan loan : member.getBookLoans()) {
                allLoans.add(loan);
                mainWindow.getLoans().put(loan.getLoanId(), loan);
                
                if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                    activeLoans.add(loan);
                } else {
                    completedLoans.add(loan);
                }
            }
        }
        
        activeLoansTableModel.setLoans(activeLoans);
        loanHistoryTableModel.setLoans(allLoans);
        
        updateButtonStates();
    }
    
    /**
     * Update status tombol berdasarkan tab yang aktif dan seleksi
     */
    private void updateButtonStates() {
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        boolean isActiveLoanSelected = selectedTabIndex == 0 && activeLoansTable.getSelectedRow() != -1;
        boolean isLoanHistorySelected = selectedTabIndex == 1 && loanHistoryTable.getSelectedRow() != -1;
        
        // Tombol peminjaman selalu aktif untuk non-basic
        issueButton.setEnabled(mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.BASIC);
        
        // Tombol pengembalian hanya aktif jika ada peminjaman aktif yang dipilih
        returnButton.setEnabled(isActiveLoanSelected);
        
        // Tombol perpanjangan hanya aktif jika ada peminjaman aktif yang dipilih
        extendButton.setEnabled(isActiveLoanSelected);
        
        // Tombol detail aktif jika ada peminjaman yang dipilih di tab manapun
        detailsButton.setEnabled(isActiveLoanSelected || isLoanHistorySelected);
    }
    
    /**
     * Cari peminjaman aktif berdasarkan keyword
     */
    private void searchActiveLoans() {
        String keyword = searchActiveField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<BookLoan> filteredList = new ArrayList<>();
        for (BookLoan loan : collectActiveLoans()) {
            if (loan.getLoanId().toLowerCase().contains(keyword) ||
                loan.getBookItem().getBook().getTitle().toLowerCase().contains(keyword) ||
                loan.getMember().getName().toLowerCase().contains(keyword) ||
                loan.getBookItem().getBarcode().toLowerCase().contains(keyword)) {
                filteredList.add(loan);
            }
        }
        
        activeLoansTableModel.setLoans(filteredList);
        updateButtonStates();
    }
    
    /**
     * Cari riwayat peminjaman berdasarkan keyword
     */
    private void searchLoanHistory() {
        String keyword = searchHistoryField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<BookLoan> filteredList = new ArrayList<>();
        for (BookLoan loan : collectAllLoans()) {
            if (loan.getLoanId().toLowerCase().contains(keyword) ||
                loan.getBookItem().getBook().getTitle().toLowerCase().contains(keyword) ||
                loan.getMember().getName().toLowerCase().contains(keyword) ||
                loan.getBookItem().getBarcode().toLowerCase().contains(keyword)) {
                filteredList.add(loan);
            }
        }
        
        loanHistoryTableModel.setLoans(filteredList);
        updateButtonStates();
    }
    
    /**
     * Collect all active loans
     */
    private List<BookLoan> collectActiveLoans() {
        List<BookLoan> activeLoans = new ArrayList<>();
        for (Member member : mainWindow.getMembers()) {
            for (BookLoan loan : member.getBookLoans()) {
                if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                    activeLoans.add(loan);
                }
            }
        }
        return activeLoans;
    }
    
    /**
     * Collect all loans
     */
    private List<BookLoan> collectAllLoans() {
        List<BookLoan> allLoans = new ArrayList<>();
        for (Member member : mainWindow.getMembers()) {
            allLoans.addAll(member.getBookLoans());
        }
        return allLoans;
    }
    
    /**
     * Pinjamkan buku ke anggota
     */
    private void issueBook() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk meminjamkan buku!", "Akses Ditolak");
            return;
        }
        
        // Pilih anggota
        Member member = DialogUtils.showMemberSelectionDialog(
            this, 
            mainWindow.getMembers(), 
            "Pilih Anggota"
        );
        
        if (member == null) {
            return; // User canceled
        }
        
        // Cek status anggota
        if (!member.isActive()) {
            GUIUtils.errorDialog(
                this, 
                "Anggota tidak aktif atau diblacklist! Tidak dapat meminjam buku.", 
                "Anggota Tidak Aktif"
            );
            return;
        }
        
        // Cek kuota peminjaman
        if (member.getCurrentBooksCount() >= member.getMaxBooks()) {
            GUIUtils.errorDialog(
                this, 
                "Anggota telah mencapai batas maksimum peminjaman (" + member.getMaxBooks() + " buku).", 
                "Batas Peminjaman Tercapai"
            );
            return;
        }
        
        // Pilih buku
        Book book = DialogUtils.showBookSelectionDialog(
            this, 
            mainWindow.getLibrary().getCollection().getBooks(), 
            "Pilih Buku"
        );
        
        if (book == null) {
            return; // User canceled
        }
        
        // Pilih salinan buku yang tersedia dan bisa dipinjam
        List<BookItem> availableItems = new ArrayList<>();
        for (BookItem item : book.getItems()) {
            if (item.isAvailable() && !item.isReferenceOnly()) {
                availableItems.add(item);
            }
        }
        
        if (availableItems.isEmpty()) {
            GUIUtils.errorDialog(
                this, 
                "Tidak ada salinan buku yang tersedia untuk dipinjam.", 
                "Tidak Ada Salinan Tersedia"
            );
            return;
        }
        
        BookItem bookItem = DialogUtils.showBookItemSelectionDialog(
            this, 
            availableItems, 
            "Pilih Salinan Buku"
        );
        
        if (bookItem == null) {
            return; // User canceled
        }
        
        try {
            // Pinjamkan buku
            BookLoan loan = mainWindow.issueBook(member, bookItem);
            
            refreshData();
            
            GUIUtils.infoDialog(
                this, 
                "Buku berhasil dipinjamkan:\n" +
                "ID Peminjaman: " + loan.getLoanId() + "\n" +
                "Buku: " + book.getTitle() + "\n" +
                "Peminjam: " + member.getName() + "\n" +
                "Tanggal Peminjaman: " + dateFormat.format(loan.getIssueDate()) + "\n" +
                "Tanggal Jatuh Tempo: " + dateFormat.format(loan.getDueDate()), 
                "Peminjaman Berhasil"
            );
        } catch (Exception ex) {
            GUIUtils.errorDialog(this, "Terjadi kesalahan saat meminjamkan buku: " + ex.getMessage(), "Error");
        }
    }
    
    /**
     * Kembalikan buku
     */
    private void returnBook() {
        int selectedRow = activeLoansTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih peminjaman yang akan dikembalikan!", "Tidak Ada Peminjaman Dipilih");
            return;
        }
        
        int modelRow = activeLoansTable.convertRowIndexToModel(selectedRow);
        BookLoan loan = activeLoansTableModel.getLoanAt(modelRow);
        
        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            GUIUtils.errorDialog(this, "Peminjaman ini tidak dalam status aktif atau terlambat!", "Status Tidak Valid");
            return;
        }
        
        try {
            mainWindow.returnBook(loan);
            
            double fine = loan.getFine();
            
            StringBuilder message = new StringBuilder();
            message.append("Buku berhasil dikembalikan:\n");
            message.append("Buku: ").append(loan.getBookItem().getBook().getTitle()).append("\n");
            message.append("Peminjam: ").append(loan.getMember().getName()).append("\n");
            message.append("Tanggal Kembali: ").append(dateFormat.format(loan.getReturnDate())).append("\n");
            
            if (fine > 0) {
                message.append("Denda: Rp").append(String.format("%.2f", fine)).append("\n\n");
                
                boolean payNow = GUIUtils.confirmDialog(
                    this, 
                    "Denda sebesar Rp" + String.format("%.2f", fine) + " akan dikenakan.\nApakah denda akan dibayar sekarang?",
                    "Konfirmasi Pembayaran Denda"
                );
                
                if (payNow) {
                    loan.getMember().payFine(fine);
                    message.append("Denda berhasil dibayar.");
                } else {
                    message.append("Denda belum dibayar.");
                }
            }
            
            refreshData();
            
            GUIUtils.infoDialog(
                this,
                message.toString(),
                "Pengembalian Berhasil"
            );
        } catch (Exception ex) {
            GUIUtils.errorDialog(this, "Terjadi kesalahan saat mengembalikan buku: " + ex.getMessage(), "Error");
        }
    }
    
    /**
     * Perpanjang peminjaman
     */
    private void extendLoan() {
        int selectedRow = activeLoansTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih peminjaman yang akan diperpanjang!", "Tidak Ada Peminjaman Dipilih");
            return;
        }
        
        int modelRow = activeLoansTable.convertRowIndexToModel(selectedRow);
        BookLoan loan = activeLoansTableModel.getLoanAt(modelRow);
        
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            GUIUtils.errorDialog(
                this, 
                "Peminjaman ini tidak dapat diperpanjang karena statusnya " + loan.getStatus().toString() + ".", 
                "Status Tidak Valid"
            );
            return;
        }
        
        // Panel untuk input jumlah hari
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        panel.add(new JLabel("ID Peminjaman:"));
        panel.add(new JLabel(loan.getLoanId()));
        
        panel.add(new JLabel("Buku:"));
        panel.add(new JLabel(loan.getBookItem().getBook().getTitle()));
        
        panel.add(new JLabel("Peminjam:"));
        panel.add(new JLabel(loan.getMember().getName()));
        
        panel.add(new JLabel("Tanggal Jatuh Tempo Saat Ini:"));
        panel.add(new JLabel(dateFormat.format(loan.getDueDate())));
        
        JTextField daysField = new JTextField("7", 5);
        panel.add(new JLabel("Jumlah Hari Perpanjangan:"));
        panel.add(daysField);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Perpanjang Peminjaman", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int days = Integer.parseInt(daysField.getText().trim());
                if (days <= 0) {
                    throw new IllegalArgumentException("Jumlah hari harus lebih dari 0!");
                }
                
                boolean extended = loan.extendDueDate(days);
                
                if (extended) {
                    refreshData();
                    
                    GUIUtils.infoDialog(
                        this, 
                        "Peminjaman berhasil diperpanjang:\n" +
                        "Buku: " + loan.getBookItem().getBook().getTitle() + "\n" +
                        "Peminjam: " + loan.getMember().getName() + "\n" +
                        "Tanggal Jatuh Tempo Baru: " + dateFormat.format(loan.getDueDate()), 
                        "Perpanjangan Berhasil"
                    );
                } else {
                    GUIUtils.errorDialog(
                        this, 
                        "Tidak dapat memperpanjang peminjaman. Kemungkinan penyebab:\n" +
                        "- Buku sudah terlambat\n" +
                        "- Buku memiliki reservasi dari anggota lain",
                        "Perpanjangan Gagal"
                    );
                }
            } catch (NumberFormatException e) {
                GUIUtils.errorDialog(this, "Jumlah hari harus berupa angka!", "Error");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Lihat detail peminjaman
     */
    private void viewLoanDetails() {
        int selectedTabIndex = tabbedPane.getSelectedIndex();
        BookLoan loan = null;
        
        if (selectedTabIndex == 0) {
            int selectedRow = activeLoansTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = activeLoansTable.convertRowIndexToModel(selectedRow);
                loan = activeLoansTableModel.getLoanAt(modelRow);
            }
        } else {
            int selectedRow = loanHistoryTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = loanHistoryTable.convertRowIndexToModel(selectedRow);
                loan = loanHistoryTableModel.getLoanAt(modelRow);
            }
        }
        
        if (loan == null) {
            GUIUtils.errorDialog(this, "Pilih peminjaman untuk melihat detail!", "Tidak Ada Peminjaman Dipilih");
            return;
        }
        
        DialogUtils.showLoanDetailsDialog(this, loan);
    }
}