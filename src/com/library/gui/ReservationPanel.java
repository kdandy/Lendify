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
 * Panel untuk manajemen reservasi buku
 */
public class ReservationPanel extends BasePanel {
    private JTabbedPane tabbedPane;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public ReservationPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Kelola Reservasi");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel dengan tabs untuk berbagai fungsi reservasi
        tabbedPane = new JTabbedPane();
        
        // Tab Reservasi Baru
        tabbedPane.addTab("Buat Reservasi", createNewReservationPanel());
        
        // Tab Daftar Reservasi
        tabbedPane.addTab("Daftar Reservasi", createReservationListPanel());
        
        // Tab Proses Reservasi
        tabbedPane.addTab("Proses Reservasi", createProcessReservationPanel());
        
        // Tab Batalkan Reservasi
        tabbedPane.addTab("Batalkan Reservasi", createCancelReservationPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Membuat panel untuk reservasi baru
     */
    private JPanel createNewReservationPanel() {
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
        String[] bookColumns = {"ISBN", "Judul", "Pengarang", "Tersedia", "Dapat Direservasi"};
        DefaultTableModel bookModel = new DefaultTableModel(bookColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data buku yang tidak tersedia
        for (Book book : library.getCollection().getBooks()) {
            if (book.getAvailableItems().isEmpty() && !book.getItems().isEmpty()) {
                bookModel.addRow(new Object[]{
                    book.getISBN(),
                    book.getTitle(),
                    book.getAuthor(),
                    "0 salinan",
                    book.hasReservation() ? "Tidak" : "Ya"
                });
            }
        }
        
        JTable booksTable = new JTable(bookModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setFillsViewportHeight(true);
        JScrollPane bookScrollPane = new JScrollPane(booksTable);
        bookPanel.add(bookScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton reserveButton = createStyledButton("Reservasi Buku", new Color(39, 174, 96));
        reserveButton.addActionListener(e -> {
            if (memberCombo.getSelectedIndex() == -1) {
                showErrorMessage("Silakan pilih anggota!", "Error");
                return;
            }
            
            if (booksTable.getSelectedRow() == -1) {
                showErrorMessage("Silakan pilih buku!", "Error");
                return;
            }
            
            // Periksa apakah buku dapat direservasi
            if (booksTable.getValueAt(booksTable.getSelectedRow(), 4).equals("Tidak")) {
                showErrorMessage("Buku ini tidak dapat direservasi karena sudah ada reservasi yang menunggu.", "Error");
                return;
            }
            
            // Ambil anggota yang dipilih
            String memberString = (String) memberCombo.getSelectedItem();
            String memberId = memberString.split(" - ")[0];
            Member selectedMember = findMemberById(memberId);
            
            // Ambil buku yang dipilih
            String isbn = (String) booksTable.getValueAt(booksTable.getSelectedRow(), 0);
            Book selectedBook = parentFrame.getBooks().get(isbn);
            
            if (selectedMember != null && selectedBook != null) {
                try {
                    // Buat reservasi
                    Reservation reservation = selectedMember.reserveBook(selectedBook);
                    parentFrame.getReservations().put(reservation.getReservationId(), reservation);
                    
                    showInfoMessage(
                            "Reservasi berhasil dibuat:\n" +
                            "ID Reservasi: " + reservation.getReservationId() + "\n" +
                            "Buku: " + selectedBook.getTitle() + "\n" +
                            "Anggota: " + selectedMember.getName() + "\n" +
                            "Tanggal Reservasi: " + dateFormat.format(reservation.getReservationDate()) + "\n" +
                            "Status: " + reservation.getStatus(),
                            "Sukses");
                    
                    // Refresh tabel
                    bookModel.setRowCount(0);
                    for (Book book : library.getCollection().getBooks()) {
                        if (book.getAvailableItems().isEmpty() && !book.getItems().isEmpty()) {
                            bookModel.addRow(new Object[]{
                                book.getISBN(),
                                book.getTitle(),
                                book.getAuthor(),
                                "0 salinan",
                                book.hasReservation() ? "Tidak" : "Ya"
                            });
                        }
                    }
                    
                } catch (Exception ex) {
                    showErrorMessage("Error: " + ex.getMessage(), "Error");
                }
            }
        });
        
        buttonPanel.add(reserveButton);
        
        // Gabungkan semua komponen
        panel.add(memberPanel, BorderLayout.NORTH);
        panel.add(bookPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Membuat panel untuk menampilkan daftar reservasi
     */
    private JPanel createReservationListPanel() {
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
        
        // Tabel reservasi
        String[] reservationColumns = {"ID Reservasi", "Anggota", "Buku", "Tanggal Reservasi", "Status"};
        DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data reservasi
        refreshReservationsTable(reservationModel);
        
        JTable reservationsTable = new JTable(reservationModel);
        reservationsTable.setFillsViewportHeight(true);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        panel.add(reservationsScrollPane, BorderLayout.CENTER);
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                refreshReservationsTable(reservationModel);
                return;
            }
            
            // Clear model
            reservationModel.setRowCount(0);
            
            // Add matching reservations
            for (Reservation reservation : parentFrame.getReservations().values()) {
                if (reservation.getMember().getName().toLowerCase().contains(keyword) ||
                    reservation.getBook().getTitle().toLowerCase().contains(keyword) ||
                    reservation.getReservationId().toLowerCase().contains(keyword)) {
                    
                    reservationModel.addRow(new Object[]{
                        reservation.getReservationId(),
                        reservation.getMember().getName(),
                        reservation.getBook().getTitle(),
                        dateFormat.format(reservation.getReservationDate()),
                        reservation.getStatus()
                    });
                }
            }
        });
        
        // Aksi tampilkan semua
        showAllButton.addActionListener(e -> {
            refreshReservationsTable(reservationModel);
        });
        
        // Panel tombol refresh
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        refreshButton.addActionListener(e -> {
            refreshReservationsTable(reservationModel);
        });
        
        buttonPanel.add(refreshButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Refresh tabel reservasi
     */
    private void refreshReservationsTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        for (Reservation reservation : parentFrame.getReservations().values()) {
            model.addRow(new Object[]{
                reservation.getReservationId(),
                reservation.getMember().getName(),
                reservation.getBook().getTitle(),
                dateFormat.format(reservation.getReservationDate()),
                reservation.getStatus()
            });
        }
    }
    
    /**
     * Membuat panel untuk memproses reservasi
     */
    private JPanel createProcessReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("ID Reservasi:"));
        JTextField reservationIdField = new JTextField(15);
        searchPanel.add(reservationIdField);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Tampilkan Reservasi Pending", new Color(52, 152, 219));
        searchPanel.add(showAllButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabel reservasi pending
        String[] reservationColumns = {"ID Reservasi", "Anggota", "Buku", "Tanggal Reservasi", "Status", "Ketersediaan Buku"};
        DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data reservasi pending
        for (Reservation reservation : parentFrame.getReservations().values()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                boolean bookAvailable = !reservation.getBook().getAvailableItems().isEmpty();
                
                reservationModel.addRow(new Object[]{
                    reservation.getReservationId(),
                    reservation.getMember().getName(),
                    reservation.getBook().getTitle(),
                    dateFormat.format(reservation.getReservationDate()),
                    reservation.getStatus(),
                    bookAvailable ? "Tersedia" : "Tidak Tersedia"
                });
            }
        }
        
        JTable reservationsTable = new JTable(reservationModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setFillsViewportHeight(true);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        panel.add(reservationsScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        JButton processButton = createStyledButton("Proses Reservasi", new Color(39, 174, 96));
        processButton.setEnabled(false);
        
        // Enable process button when a row is selected
        reservationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = reservationsTable.getSelectedRow();
                if (row != -1) {
                    boolean bookAvailable = reservationsTable.getValueAt(row, 5).equals("Tersedia");
                    processButton.setEnabled(bookAvailable);
                } else {
                    processButton.setEnabled(false);
                }
            }
        });
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String reservationId = reservationIdField.getText().trim();
            if (reservationId.isEmpty()) {
                showErrorMessage("Masukkan ID reservasi!", "Error");
                return;
            }
            
            Reservation reservation = parentFrame.getReservations().get(reservationId);
            if (reservation == null) {
                showErrorMessage("Reservasi tidak ditemukan!", "Error");
                return;
            }
            
            if (reservation.getStatus() != ReservationStatus.PENDING) {
                showErrorMessage("Reservasi ini sudah diproses atau dibatalkan!", "Error");
                return;
            }
            
            // Clear model
            reservationModel.setRowCount(0);
            
            // Add the found reservation
            boolean bookAvailable = !reservation.getBook().getAvailableItems().isEmpty();
            
            reservationModel.addRow(new Object[]{
                reservation.getReservationId(),
                reservation.getMember().getName(),
                reservation.getBook().getTitle(),
                dateFormat.format(reservation.getReservationDate()),
                reservation.getStatus(),
                bookAvailable ? "Tersedia" : "Tidak Tersedia"
            });
        });
        
        // Aksi tampilkan semua reservasi pending
        showAllButton.addActionListener(e -> {
            // Clear model
            reservationModel.setRowCount(0);
            
            // Add all pending reservations
            for (Reservation reservation : parentFrame.getReservations().values()) {
                if (reservation.getStatus() == ReservationStatus.PENDING) {
                    boolean bookAvailable = !reservation.getBook().getAvailableItems().isEmpty();
                    
                    reservationModel.addRow(new Object[]{
                        reservation.getReservationId(),
                        reservation.getMember().getName(),
                        reservation.getBook().getTitle(),
                        dateFormat.format(reservation.getReservationDate()),
                        reservation.getStatus(),
                        bookAvailable ? "Tersedia" : "Tidak Tersedia"
                    });
                }
            }
        });
        
        // Aksi refresh
        refreshButton.addActionListener(e -> {
            // Reuse showAllButton action
            showAllButton.doClick();
        });
        
        // Aksi proses reservasi
        processButton.addActionListener(e -> {
            int row = reservationsTable.getSelectedRow();
            if (row != -1) {
                String reservationId = (String) reservationsTable.getValueAt(row, 0);
                Reservation reservation = parentFrame.getReservations().get(reservationId);
                
                if (reservation != null) {
                    try {
                        currentLibrarian.processReservation(reservation);
                        // Setelah diproses, status reservasi menjadi FULFILLED sesuai enum
                        // reservation.getStatus() == ReservationStatus.FULFILLED
                        
                        showInfoMessage(
                                "Reservasi berhasil diproses:\n" +
                                "Buku: " + reservation.getBook().getTitle() + "\n" +
                                "Anggota: " + reservation.getMember().getName() + "\n" +
                                "Status: " + reservation.getStatus(),
                                "Sukses");
                        
                        // Refresh table
                        showAllButton.doClick();
                        
                    } catch (Exception ex) {
                        showErrorMessage("Error: " + ex.getMessage(), "Error");
                    }
                }
            }
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(processButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Membuat panel untuk membatalkan reservasi
     */
    private JPanel createCancelReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        searchPanel.add(new JLabel("ID Reservasi:"));
        JTextField reservationIdField = new JTextField(15);
        searchPanel.add(reservationIdField);
        
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchPanel.add(searchButton);
        
        JButton showAllButton = createStyledButton("Tampilkan Reservasi Pending", new Color(52, 152, 219));
        searchPanel.add(showAllButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Tabel reservasi pending
        String[] reservationColumns = {"ID Reservasi", "Anggota", "Buku", "Tanggal Reservasi", "Status"};
        DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Isi data reservasi pending
        for (Reservation reservation : parentFrame.getReservations().values()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                reservationModel.addRow(new Object[]{
                    reservation.getReservationId(),
                    reservation.getMember().getName(),
                    reservation.getBook().getTitle(),
                    dateFormat.format(reservation.getReservationDate()),
                    reservation.getStatus()
                });
            }
        }
        
        JTable reservationsTable = new JTable(reservationModel);
        reservationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationsTable.setFillsViewportHeight(true);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        panel.add(reservationsScrollPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton refreshButton = createStyledButton("Refresh", new Color(52, 152, 219));
        
        JButton cancelButton = createStyledButton("Batalkan Reservasi", new Color(231, 76, 60));
        cancelButton.setEnabled(false);
        
        // Enable cancel button when a row is selected
        reservationsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cancelButton.setEnabled(reservationsTable.getSelectedRow() != -1);
            }
        });
        
        // Aksi pencarian
        searchButton.addActionListener(e -> {
            String reservationId = reservationIdField.getText().trim();
            if (reservationId.isEmpty()) {
                showErrorMessage("Masukkan ID reservasi!", "Error");
                return;
            }
            
            Reservation reservation = parentFrame.getReservations().get(reservationId);
            if (reservation == null) {
                showErrorMessage("Reservasi tidak ditemukan!", "Error");
                return;
            }
            
            if (reservation.getStatus() != ReservationStatus.PENDING) {
                showErrorMessage("Reservasi ini sudah diproses atau dibatalkan!", "Error");
                return;
            }
            
            // Clear model
            reservationModel.setRowCount(0);
            
            // Add the found reservation
            reservationModel.addRow(new Object[]{
                reservation.getReservationId(),
                reservation.getMember().getName(),
                reservation.getBook().getTitle(),
                dateFormat.format(reservation.getReservationDate()),
                reservation.getStatus()
            });
        });
        
        // Aksi tampilkan semua reservasi pending
        showAllButton.addActionListener(e -> {
            // Clear model
            reservationModel.setRowCount(0);
            
            // Add all pending reservations
            for (Reservation reservation : parentFrame.getReservations().values()) {
                if (reservation.getStatus() == ReservationStatus.PENDING) {
                    reservationModel.addRow(new Object[]{
                        reservation.getReservationId(),
                        reservation.getMember().getName(),
                        reservation.getBook().getTitle(),
                        dateFormat.format(reservation.getReservationDate()),
                        reservation.getStatus()
                    });
                }
            }
        });
        
        // Aksi refresh
        refreshButton.addActionListener(e -> {
            // Reuse showAllButton action
            showAllButton.doClick();
        });
        
        // Aksi batalkan reservasi
        cancelButton.addActionListener(e -> {
            int row = reservationsTable.getSelectedRow();
            if (row != -1) {
                String reservationId = (String) reservationsTable.getValueAt(row, 0);
                Reservation reservation = parentFrame.getReservations().get(reservationId);
                
                if (reservation != null) {
                    boolean confirm = showConfirmDialog(
                        "Apakah Anda yakin ingin membatalkan reservasi ini?",
                        "Konfirmasi Pembatalan");
                    
                    if (confirm) {
                        reservation.cancelReservation();
                        
                        showInfoMessage("Reservasi berhasil dibatalkan.", "Sukses");
                        
                        // Refresh table
                        showAllButton.doClick();
                    }
                }
            }
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(cancelButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
}