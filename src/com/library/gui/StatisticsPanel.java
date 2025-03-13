package com.library.gui;

import com.library.enums.LoanStatus;
import com.library.enums.ReservationStatus;
import com.library.model.Book;
import com.library.model.BookCategory;
import com.library.model.BookLoan;
import com.library.model.Member;
import com.library.model.RegularMember;
import com.library.model.Reservation;
import com.library.model.StudentMember;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Panel untuk statistik perpustakaan
 */
public class StatisticsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JPanel summaryPanel;
    private JPanel chartsPanel;
    private JButton refreshButton;
    private JButton backButton;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    // Labels untuk data statistik
    private JLabel totalBooksLabel;
    private JLabel totalCategoriesLabel;
    private JLabel totalMembersLabel;
    private JLabel activeLoansLabel;
    private JLabel pendingReservationsLabel;
    private JLabel totalFinesLabel;
    private JLabel studentMembersLabel;
    private JLabel regularMembersLabel;
    private JLabel availableBooksLabel;
    private JLabel borrowedBooksLabel;
    
    /**
     * Constructor untuk StatisticsPanel
     */
    public StatisticsPanel(LendifyGUI mainWindow) {
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
        JLabel titleLabel = new JLabel("Statistik Perpustakaan", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Info perpustakaan
        JPanel libraryInfoPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        libraryInfoPanel.add(new JLabel("Perpustakaan: " + mainWindow.getLibrary().getName()));
        libraryInfoPanel.add(new JLabel("Alamat: " + mainWindow.getLibrary().getAddress()));
        
        titlePanel.add(libraryInfoPanel, BorderLayout.SOUTH);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel utama dengan split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.5);
        
        // Panel atas untuk ringkasan statistik
        summaryPanel = createSummaryPanel();
        splitPane.setTopComponent(summaryPanel);
        
        // Panel bawah untuk grafik dan chart
        chartsPanel = createChartsPanel();
        splitPane.setBottomComponent(chartsPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        refreshButton = new JButton("Refresh Data");
        backButton = new JButton("Kembali");
        
        refreshButton.addActionListener(e -> refreshData());
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Refresh data awal
        refreshData();
    }
    
    /**
     * Buat panel ringkasan statistik
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Ringkasan Statistik"));
        
        // Panel statistik utama (2 kolom)
        JPanel statsPanel = new JPanel(new GridLayout(5, 4, 15, 5));
        
        // Kolom 1: Statistik buku
        statsPanel.add(createStatsLabel("Total Buku:"));
        totalBooksLabel = createValueLabel("0");
        statsPanel.add(totalBooksLabel);
        
        statsPanel.add(createStatsLabel("Total Kategori:"));
        totalCategoriesLabel = createValueLabel("0");
        statsPanel.add(totalCategoriesLabel);
        
        statsPanel.add(createStatsLabel("Buku Tersedia:"));
        availableBooksLabel = createValueLabel("0");
        statsPanel.add(availableBooksLabel);
        
        statsPanel.add(createStatsLabel("Buku Dipinjam:"));
        borrowedBooksLabel = createValueLabel("0");
        statsPanel.add(borrowedBooksLabel);
        
        statsPanel.add(createStatsLabel("Total Denda:"));
        totalFinesLabel = createValueLabel("Rp 0,00");
        statsPanel.add(totalFinesLabel);
        
        // Kolom 2: Statistik anggota dan transaksi
        statsPanel.add(createStatsLabel("Total Anggota:"));
        totalMembersLabel = createValueLabel("0");
        statsPanel.add(totalMembersLabel);
        
        statsPanel.add(createStatsLabel("Anggota Mahasiswa:"));
        studentMembersLabel = createValueLabel("0");
        statsPanel.add(studentMembersLabel);
        
        statsPanel.add(createStatsLabel("Anggota Reguler:"));
        regularMembersLabel = createValueLabel("0");
        statsPanel.add(regularMembersLabel);
        
        statsPanel.add(createStatsLabel("Peminjaman Aktif:"));
        activeLoansLabel = createValueLabel("0");
        statsPanel.add(activeLoansLabel);
        
        statsPanel.add(createStatsLabel("Reservasi Pending:"));
        pendingReservationsLabel = createValueLabel("0");
        statsPanel.add(pendingReservationsLabel);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Buat panel untuk grafik dan chart
     */
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 10));
        
        // Panel untuk distribusi kategori
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Distribusi Buku per Kategori"));
        
        // Akan diisi saat refresh data
        categoryPanel.add(new JLabel("Data akan dimuat saat refresh...", SwingConstants.CENTER));
        
        // Panel untuk statistik peminjaman
        JPanel loanPanel = new JPanel(new BorderLayout());
        loanPanel.setBorder(BorderFactory.createTitledBorder("Statistik Peminjaman"));
        
        // Akan diisi saat refresh data
        loanPanel.add(new JLabel("Data akan dimuat saat refresh...", SwingConstants.CENTER));
        
        panel.add(categoryPanel);
        panel.add(loanPanel);
        
        return panel;
    }
    
    /**
     * Refresh data statistik
     */
    public void refreshData() {
        // Hitung statistik
        int totalBooks = mainWindow.getLibrary().getCollection().getTotalBooks();
        int totalCategories = mainWindow.getLibrary().getCollection().getTotalCategories();
        int totalMembers = mainWindow.getMembers().size();
        
        int studentMembers = 0;
        int regularMembers = 0;
        double totalFines = 0.0;
        
        // Hitung jumlah anggota berdasarkan tipe
        for (Member member : mainWindow.getMembers()) {
            if (member instanceof StudentMember) {
                studentMembers++;
            } else if (member instanceof RegularMember) {
                regularMembers++;
            }
            
            totalFines += member.getTotalFinesPaid();
        }
        
        // Hitung peminjaman aktif
        int activeLoans = 0;
        int borrowedBooks = 0;
        for (Member member : mainWindow.getMembers()) {
            for (BookLoan loan : member.getBookLoans()) {
                if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                    activeLoans++;
                    borrowedBooks++;
                }
            }
        }
        
        // Hitung reservasi pending
        int pendingReservations = 0;
        for (Member member : mainWindow.getMembers()) {
            for (Reservation reservation : member.getReservations()) {
                if (reservation.getStatus() == ReservationStatus.PENDING) {
                    pendingReservations++;
                }
            }
        }
        
        // Hitung buku tersedia
        int availableBooks = 0;
        for (Book book : mainWindow.getLibrary().getCollection().getBooks()) {
            availableBooks += book.getAvailableItems().size();
        }
        
        // Update labels
        totalBooksLabel.setText(String.valueOf(totalBooks));
        totalCategoriesLabel.setText(String.valueOf(totalCategories));
        totalMembersLabel.setText(String.valueOf(totalMembers));
        studentMembersLabel.setText(String.valueOf(studentMembers));
        regularMembersLabel.setText(String.valueOf(regularMembers));
        activeLoansLabel.setText(String.valueOf(activeLoans));
        pendingReservationsLabel.setText(String.valueOf(pendingReservations));
        totalFinesLabel.setText(String.format("Rp %.2f", totalFines));
        availableBooksLabel.setText(String.valueOf(availableBooks));
        borrowedBooksLabel.setText(String.valueOf(borrowedBooks));
        
        // Update panel grafik distribusi kategori
        updateCategoryChart();
        
        // Update panel statistik peminjaman
        updateLoanStatistics();
    }
    
    /**
     * Update chart distribusi kategori
     */
    private void updateCategoryChart() {
        // Ambil semua kategori
        List<BookCategory> categories = mainWindow.getLibrary().getCollection().getCategories();
        
        // Jika tidak ada kategori, tampilkan pesan
        if (categories.isEmpty()) {
            JPanel categoryPanel = (JPanel) chartsPanel.getComponent(0);
            categoryPanel.removeAll();
            categoryPanel.add(new JLabel("Tidak ada data kategori.", SwingConstants.CENTER));
            categoryPanel.revalidate();
            categoryPanel.repaint();
            return;
        }
        
        // Buat panel untuk chart
        JPanel categoryPanel = (JPanel) chartsPanel.getComponent(0);
        categoryPanel.removeAll();
        
        // Buat tabel untuk menampilkan data
        String[] columnNames = {"Kategori", "Jumlah Buku", "Persentase"};
        Object[][] data = new Object[categories.size()][3];
        
        int totalBooksInCategories = 0;
        for (BookCategory category : categories) {
            totalBooksInCategories += category.getBooks().size();
        }
        
        for (int i = 0; i < categories.size(); i++) {
            BookCategory category = categories.get(i);
            int bookCount = category.getBooks().size();
            double percentage = totalBooksInCategories > 0 ? 
                (double) bookCount / totalBooksInCategories * 100 : 0;
            
            data[i][0] = category.getName();
            data[i][1] = bookCount;
            data[i][2] = String.format("%.1f%%", percentage);
        }
        
        JTable categoryTable = new JTable(data, columnNames);
        categoryTable.setEnabled(false);
        
        categoryPanel.setLayout(new BorderLayout());
        categoryPanel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        
        categoryPanel.revalidate();
        categoryPanel.repaint();
    }
    
    /**
     * Update statistik peminjaman
     */
    private void updateLoanStatistics() {
        // Buat panel untuk statistik peminjaman
        JPanel loanPanel = (JPanel) chartsPanel.getComponent(1);
        loanPanel.removeAll();
        
        // Kumpulkan data peminjaman
        int totalLoans = 0;
        int activeLoans = 0;
        int completedLoans = 0;
        int overdueLoans = 0;
        
        for (Member member : mainWindow.getMembers()) {
            for (BookLoan loan : member.getBookLoans()) {
                totalLoans++;
                
                switch (loan.getStatus()) {
                    case ACTIVE:
                        activeLoans++;
                        break;
                    case COMPLETED:
                        completedLoans++;
                        break;
                    case OVERDUE:
                        overdueLoans++;
                        break;
                }
            }
        }
        
        // Buat tabel untuk statistik peminjaman
        String[] loanColumnNames = {"Status", "Jumlah", "Persentase"};
        Object[][] loanData = new Object[3][3];
        
        loanData[0][0] = "Aktif";
        loanData[0][1] = activeLoans;
        loanData[0][2] = totalLoans > 0 ? String.format("%.1f%%", (double) activeLoans / totalLoans * 100) : "0.0%";
        
        loanData[1][0] = "Selesai";
        loanData[1][1] = completedLoans;
        loanData[1][2] = totalLoans > 0 ? String.format("%.1f%%", (double) completedLoans / totalLoans * 100) : "0.0%";
        
        loanData[2][0] = "Terlambat";
        loanData[2][1] = overdueLoans;
        loanData[2][2] = totalLoans > 0 ? String.format("%.1f%%", (double) overdueLoans / totalLoans * 100) : "0.0%";
        
        JTable loanTable = new JTable(loanData, loanColumnNames);
        loanTable.setEnabled(false);
        
        // Panel untuk total peminjaman
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        totalPanel.add(new JLabel("Total Peminjaman: " + totalLoans));
        
        loanPanel.setLayout(new BorderLayout());
        loanPanel.add(new JScrollPane(loanTable), BorderLayout.CENTER);
        loanPanel.add(totalPanel, BorderLayout.SOUTH);
        
        loanPanel.revalidate();
        loanPanel.repaint();
    }
    
    /**
     * Buat label judul statistik dengan style
     */
    private JLabel createStatsLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.RIGHT);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
        return label;
    }
    
    /**
     * Buat label nilai statistik dengan style
     */
    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font(label.getFont().getName(), Font.PLAIN, label.getFont().getSize()));
        return label;
    }
}