package com.library.gui;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel untuk dashboard
 */
public class DashboardPanel extends BasePanel {

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public DashboardPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Dashboard");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel kartu statistik
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Tambahkan kartu statistik
        contentPanel.add(createStatCard("Total Buku", String.valueOf(library.getCollection().getTotalBooks()), 
                new Color(41, 128, 185)));
        
        contentPanel.add(createStatCard("Total Anggota", String.valueOf(parentFrame.getMembers().size()), 
                new Color(39, 174, 96)));
        
        contentPanel.add(createStatCard("Peminjaman Aktif", String.valueOf(countActiveLoans()), 
                new Color(243, 156, 18)));
        
        contentPanel.add(createStatCard("Reservasi Pending", String.valueOf(countPendingReservations()), 
                new Color(211, 84, 0)));
        
        contentPanel.add(createStatCard("Kategori", String.valueOf(parentFrame.getCategories().size()), 
                new Color(142, 68, 173)));
        
        contentPanel.add(createStatCard("Pustakawan", String.valueOf(library.getLibrarians().size()), 
                new Color(127, 140, 141)));
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Panel aktivitas terkini
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JLabel recentLabel = new JLabel("Aktivitas Terkini");
        recentLabel.setFont(new Font("Arial", Font.BOLD, 18));
        recentPanel.add(recentLabel, BorderLayout.NORTH);
        
        // Tabel untuk aktivitas terkini (peminjaman terbaru)
        String[] columnNames = {"Tanggal", "Aktivitas", "Anggota", "Buku", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        // Ambil beberapa peminjaman terbaru untuk ditampilkan
        addRecentActivities(model);
        
        JTable recentTable = new JTable(model);
        recentTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(recentTable);
        recentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(recentPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Membuat kartu statistik
     * @param title Judul kartu
     * @param value Nilai statistik
     * @param color Warna kartu
     * @return Panel kartu statistik
     */
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                new EmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Menghitung jumlah peminjaman aktif
     * @return Jumlah peminjaman aktif
     */
    private int countActiveLoans() {
        int count = 0;
        for (BookLoan loan : parentFrame.getLoans().values()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Menghitung jumlah reservasi yang belum diproses
     * @return Jumlah reservasi pending
     */
    private int countPendingReservations() {
        int count = 0;
        for (Reservation reservation : parentFrame.getReservations().values()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Menambahkan aktivitas terkini ke tabel
     * @param model Model tabel yang akan diisi
     */
    private void addRecentActivities(DefaultTableModel model) {
        List<Object[]> activities = new ArrayList<>();
        
        // Tambahkan peminjaman terbaru
        for (BookLoan loan : parentFrame.getLoans().values()) {
            String activity = (loan.getStatus() == LoanStatus.RETURNED) ? "Pengembalian" : "Peminjaman";
            String date = dateFormat.format(loan.getIssueDate());
            String member = loan.getMember().getName();
            String book = loan.getBookItem().getBook().getTitle();
            String status = loan.getStatus().toString();
            
            activities.add(new Object[]{date, activity, member, book, status});
        }
        
        // Tambahkan reservasi terbaru
        for (Reservation reservation : parentFrame.getReservations().values()) {
            String date = dateFormat.format(reservation.getReservationDate());
            String member = reservation.getMember().getName();
            String book = reservation.getBook().getTitle();
            String status = reservation.getStatus().toString();
            
            activities.add(new Object[]{date, "Reservasi", member, book, status});
        }
        
        // Urutkan berdasarkan tanggal (kolom 0), dengan asumsi format tanggal yang sama
        activities.sort((a, b) -> ((String)b[0]).compareTo((String)a[0]));
        
        // Ambil 10 aktivitas teratas
        int count = Math.min(activities.size(), 10);
        for (int i = 0; i < count; i++) {
            model.addRow(activities.get(i));
        }
    }
}