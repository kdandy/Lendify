package com.library.gui;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

/**
 * Panel untuk menampilkan statistik perpustakaan
 */
public class StatisticsPanel extends BasePanel {

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public StatisticsPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Statistik Perpustakaan");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel isi
        JPanel contentPanel = new JPanel(new BorderLayout());
        
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 20, 10));
        infoPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Data perpustakaan
        infoPanel.add(createLabelWithTitle("Nama Perpustakaan", library.getName()));
        infoPanel.add(createLabelWithTitle("Alamat", library.getAddress()));
        
        infoPanel.add(createLabelWithTitle("Jumlah Pustakawan", String.valueOf(library.getLibrarians().size())));
        infoPanel.add(createLabelWithTitle("Jumlah Buku", String.valueOf(library.getCollection().getTotalBooks())));
        
        infoPanel.add(createLabelWithTitle("Jumlah Kategori", String.valueOf(library.getCollection().getTotalCategories())));
        infoPanel.add(createLabelWithTitle("Jumlah Anggota", String.valueOf(parentFrame.getMembers().size())));
        
        // Hitung jumlah peminjaman aktif
        int activeLoans = 0;
        for (BookLoan loan : parentFrame.getLoans().values()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                activeLoans++;
            }
        }
        infoPanel.add(createLabelWithTitle("Peminjaman Aktif", String.valueOf(activeLoans)));
        
        // Hitung jumlah reservasi pending
        int pendingReservations = 0;
        for (Reservation reservation : parentFrame.getReservations().values()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                pendingReservations++;
            }
        }
        infoPanel.add(createLabelWithTitle("Reservasi Menunggu", String.valueOf(pendingReservations)));
        
        // Hitung total denda
        double totalFines = 0;
        for (Member member : parentFrame.getMembers()) {
            totalFines += member.getTotalFinesPaid();
        }
        infoPanel.add(createLabelWithTitle("Total Denda Terkumpul", String.format("Rp%.2f", totalFines)));
        
        contentPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Tabel buku paling populer
        JPanel topBooksPanel = new JPanel(new BorderLayout());
        topBooksPanel.setBorder(BorderFactory.createTitledBorder("Buku Paling Sering Dipinjam"));
        
        String[] topBookColumns = {"Judul", "Pengarang", "Jumlah Peminjaman"};
        DefaultTableModel topBookModel = new DefaultTableModel(topBookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Hitung jumlah peminjaman per buku
        Map<Book, Integer> bookLoanCount = new HashMap<>();
        for (BookLoan loan : parentFrame.getLoans().values()) {
            Book book = loan.getBookItem().getBook();
            bookLoanCount.put(book, bookLoanCount.getOrDefault(book, 0) + 1);
        }
        
        // Urutkan buku berdasarkan jumlah peminjaman
        List<Entry<Book, Integer>> sortedBooks = new ArrayList<>(bookLoanCount.entrySet());
        sortedBooks.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // Ambil 10 buku teratas
        int count = Math.min(sortedBooks.size(), 10);
        for (int i = 0; i < count; i++) {
            Entry<Book, Integer> entry = sortedBooks.get(i);
            Book book = entry.getKey();
            Integer loanCount = entry.getValue();
            
            topBookModel.addRow(new Object[]{
                book.getTitle(),
                book.getAuthor(),
                loanCount
            });
        }
        
        JTable topBooksTable = new JTable(topBookModel);
        topBooksTable.setFillsViewportHeight(true);
        JScrollPane topBooksScrollPane = new JScrollPane(topBooksTable);
        topBooksPanel.add(topBooksScrollPane, BorderLayout.CENTER);
        
        // Tabel anggota paling aktif
        JPanel topMembersPanel = new JPanel(new BorderLayout());
        topMembersPanel.setBorder(BorderFactory.createTitledBorder("Anggota Paling Aktif"));
        
        String[] topMemberColumns = {"ID Anggota", "Nama", "Jumlah Peminjaman"};
        DefaultTableModel topMemberModel = new DefaultTableModel(topMemberColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Hitung jumlah peminjaman per anggota
        Map<Member, Integer> memberLoanCount = new HashMap<>();
        for (BookLoan loan : parentFrame.getLoans().values()) {
            Member member = loan.getMember();
            memberLoanCount.put(member, memberLoanCount.getOrDefault(member, 0) + 1);
        }
        
        // Urutkan anggota berdasarkan jumlah peminjaman
        List<Entry<Member, Integer>> sortedMembers = new ArrayList<>(memberLoanCount.entrySet());
        sortedMembers.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        // Ambil 10 anggota teratas
        count = Math.min(sortedMembers.size(), 10);
        for (int i = 0; i < count; i++) {
            Entry<Member, Integer> entry = sortedMembers.get(i);
            Member member = entry.getKey();
            Integer loanCount = entry.getValue();
            
            topMemberModel.addRow(new Object[]{
                member.getMemberId(),
                member.getName(),
                loanCount
            });
        }
        
        JTable topMembersTable = new JTable(topMemberModel);
        topMembersTable.setFillsViewportHeight(true);
        JScrollPane topMembersScrollPane = new JScrollPane(topMembersTable);
        topMembersPanel.add(topMembersScrollPane, BorderLayout.CENTER);
        
        // Layout panel statistik
        JPanel statsTablesPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        statsTablesPanel.add(topBooksPanel);
        statsTablesPanel.add(topMembersPanel);
        
        contentPanel.add(statsTablesPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh Statistik", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            parentFrame.showPanel(LendifyGUI.STATISTICS_PANEL);
        });
        
        JButton printButton = createStyledButton("Cetak Statistik", new Color(39, 174, 96));
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                    "Fitur cetak statistik akan segera tersedia.", 
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(printButton);
        
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Membuat label dengan judul
     * @param title Judul label
     * @param value Nilai yang ditampilkan
     * @return Panel berisi label dengan judul
     */
    private JPanel createLabelWithTitle(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel(title + ":");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(titleLabel, BorderLayout.WEST);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(valueLabel, BorderLayout.EAST);
        
        return panel;
    }
}