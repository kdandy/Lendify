package com.library.gui;

import com.library.enums.LibrarianPermission;
import com.library.enums.ReservationStatus;
import com.library.gui.utils.DialogUtils;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.ReservationTableModel;
import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Reservation;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel untuk kelola reservasi buku
 */
public class ReservationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JTable reservationTable;
    private ReservationTableModel tableModel;
    private JTextField searchField;
    private JButton createButton;
    private JButton processButton;
    private JButton cancelButton;
    private JButton detailsButton;
    private JButton backButton;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Constructor untuk ReservationPanel
     */
    public ReservationPanel(LendifyGUI mainWindow) {
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
        JLabel titleLabel = new JLabel("Kelola Reservasi", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchReservations());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel tabel
        tableModel = new ReservationTableModel(new ArrayList<>());
        reservationTable = new JTable(tableModel);
        reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationTable.setRowHeight(25);
        reservationTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<ReservationTableModel> sorter = new TableRowSorter<>(tableModel);
        reservationTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        reservationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewReservationDetails();
                }
            }
        });
        
        // Selection listener untuk update status tombol
        reservationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // Panel untuk tabel dengan scrolling
        JScrollPane tableScrollPane = new JScrollPane(reservationTable);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter Status"));
        
        JRadioButton allRadio = new JRadioButton("Semua", true);
        JRadioButton pendingRadio = new JRadioButton("Pending");
        JRadioButton completedRadio = new JRadioButton("Completed");
        JRadioButton cancelledRadio = new JRadioButton("Cancelled");
        
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(allRadio);
        statusGroup.add(pendingRadio);
        statusGroup.add(completedRadio);
        statusGroup.add(cancelledRadio);
        
        allRadio.addActionListener(e -> filterReservations(null));
        pendingRadio.addActionListener(e -> filterReservations(ReservationStatus.PENDING));
        completedRadio.addActionListener(e -> filterReservations(ReservationStatus.FULFILLED));
        cancelledRadio.addActionListener(e -> filterReservations(ReservationStatus.CANCELLED));
        
        filterPanel.add(allRadio);
        filterPanel.add(pendingRadio);
        filterPanel.add(completedRadio);
        filterPanel.add(cancelledRadio);
        
        // Panel tombol
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.add(filterPanel, BorderLayout.NORTH);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        createButton = new JButton("Buat Reservasi");
        processButton = new JButton("Proses Reservasi");
        cancelButton = new JButton("Batalkan Reservasi");
        detailsButton = new JButton("Lihat Detail");
        backButton = new JButton("Kembali");
        
        createButton.addActionListener(e -> createReservation());
        processButton.addActionListener(e -> processReservation());
        cancelButton.addActionListener(e -> cancelReservation());
        detailsButton.addActionListener(e -> viewReservationDetails());
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        
        buttonPanel.add(createButton);
        buttonPanel.add(processButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(backButton);
        
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(actionPanel, BorderLayout.SOUTH);
        
        // Set initial button states
        updateButtonStates();
    }
    
    /**
     * Refresh data panel
     */
    public void refreshData() {
        // Kumpulkan semua data reservasi dari anggota
        List<Reservation> allReservations = new ArrayList<>();
        
        for (Member member : mainWindow.getMembers()) {
            for (Reservation reservation : member.getReservations()) {
                allReservations.add(reservation);
                mainWindow.getReservations().put(reservation.getReservationId(), reservation);
            }
        }
        
        tableModel.setReservations(allReservations);
        updateButtonStates();
    }
    
    /**
     * Update status tombol berdasarkan hak akses dan seleksi
     */
    private void updateButtonStates() {
        boolean isBasic = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC;
        boolean reservationSelected = reservationTable.getSelectedRow() != -1;
        boolean isPendingSelected = false;
        
        if (reservationSelected) {
            int modelRow = reservationTable.convertRowIndexToModel(reservationTable.getSelectedRow());
            Reservation reservation = tableModel.getReservationAt(modelRow);
            isPendingSelected = (reservation.getStatus() == ReservationStatus.PENDING);
        }
        
        // Tombol buat reservasi selalu aktif
        createButton.setEnabled(true);
        
        // Tombol proses reservasi aktif jika ada reservasi pending yang dipilih
        processButton.setEnabled(reservationSelected && isPendingSelected);
        
        // Tombol batalkan reservasi aktif jika ada reservasi pending yang dipilih
        cancelButton.setEnabled(reservationSelected && isPendingSelected);
        
        // Tombol detail aktif jika ada reservasi yang dipilih
        detailsButton.setEnabled(reservationSelected);
    }
    
    /**
     * Cari reservasi berdasarkan keyword
     */
    private void searchReservations() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<Reservation> filteredList = new ArrayList<>();
        for (Reservation reservation : collectAllReservations()) {
            if (reservation.getReservationId().toLowerCase().contains(keyword) ||
                reservation.getBook().getTitle().toLowerCase().contains(keyword) ||
                reservation.getMember().getName().toLowerCase().contains(keyword) ||
                dateFormat.format(reservation.getReservationDate()).contains(keyword)) {
                filteredList.add(reservation);
            }
        }
        
        tableModel.setReservations(filteredList);
        updateButtonStates();
    }
    
    /**
     * Filter reservasi berdasarkan status
     */
    private void filterReservations(ReservationStatus status) {
        if (status == null) {
            refreshData();
            return;
        }
        
        ArrayList<Reservation> filteredList = new ArrayList<>();
        for (Reservation reservation : collectAllReservations()) {
            if (reservation.getStatus() == status) {
                filteredList.add(reservation);
            }
        }
        
        tableModel.setReservations(filteredList);
        updateButtonStates();
    }
    
    /**
     * Collect all reservations
     */
    private List<Reservation> collectAllReservations() {
        List<Reservation> allReservations = new ArrayList<>();
        for (Member member : mainWindow.getMembers()) {
            allReservations.addAll(member.getReservations());
        }
        return allReservations;
    }
    
    /**
     * Buat reservasi baru
     */
    private void createReservation() {
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
                "Anggota tidak aktif atau diblacklist! Tidak dapat melakukan reservasi.", 
                "Anggota Tidak Aktif"
            );
            return;
        }
        
        // Pilih buku
        List<Book> unavailableBooks = new ArrayList<>();
        for (Book book : mainWindow.getLibrary().getCollection().getBooks()) {
            if (book.getAvailableItems().isEmpty() && !book.getItems().isEmpty()) {
                unavailableBooks.add(book);
            }
        }
        
        if (unavailableBooks.isEmpty()) {
            GUIUtils.errorDialog(
                this, 
                "Tidak ada buku yang perlu direservasi. Semua buku tersedia atau tidak memiliki salinan.", 
                "Reservasi Tidak Diperlukan"
            );
            return;
        }
        
        Book book = DialogUtils.showBookSelectionDialog(
            this, 
            unavailableBooks, 
            "Pilih Buku untuk Reservasi"
        );
        
        if (book == null) {
            return; // User canceled
        }
        
        // Cek apakah anggota sudah memiliki reservasi atau peminjaman untuk buku ini
        boolean alreadyReservedOrBorrowed = false;
        for (Reservation reservation : member.getReservations()) {
            if (reservation.getBook().equals(book) && 
                reservation.getStatus() == ReservationStatus.PENDING) {
                alreadyReservedOrBorrowed = true;
                break;
            }
        }
        
        if (alreadyReservedOrBorrowed) {
            GUIUtils.errorDialog(
                this, 
                "Anggota sudah memiliki reservasi aktif untuk buku ini.", 
                "Reservasi Duplikat"
            );
            return;
        }
        
        try {
            // Buat reservasi
            Reservation reservation = member.reserveBook(book);
            mainWindow.getReservations().put(reservation.getReservationId(), reservation);
            
            refreshData();
            
            GUIUtils.infoDialog(
                this, 
                "Reservasi berhasil dibuat:\n" +
                "ID Reservasi: " + reservation.getReservationId() + "\n" +
                "Buku: " + book.getTitle() + "\n" +
                "Anggota: " + member.getName() + "\n" +
                "Tanggal Reservasi: " + dateFormat.format(reservation.getReservationDate()) + "\n" +
                "Status: " + reservation.getStatus(), 
                "Reservasi Berhasil"
            );
        } catch (Exception ex) {
            GUIUtils.errorDialog(this, "Terjadi kesalahan saat membuat reservasi: " + ex.getMessage(), "Error");
        }
    }
    
    /**
     * Proses reservasi yang dipilih
     */
    private void processReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih reservasi yang akan diproses!", "Tidak Ada Reservasi Dipilih");
            return;
        }
        
        int modelRow = reservationTable.convertRowIndexToModel(selectedRow);
        Reservation reservation = tableModel.getReservationAt(modelRow);
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            GUIUtils.errorDialog(
                this, 
                "Reservasi ini tidak dalam status pending! Status saat ini: " + reservation.getStatus(), 
                "Status Tidak Valid"
            );
            return;
        }
        
        Book book = reservation.getBook();
        if (book.getAvailableItems().isEmpty()) {
            GUIUtils.errorDialog(
                this, 
                "Tidak ada salinan buku yang tersedia untuk reservasi ini.", 
                "Tidak Ada Salinan Tersedia"
            );
            return;
        }
        
        try {
            mainWindow.processReservation(reservation);
            
            refreshData();
            
            GUIUtils.infoDialog(
                this, 
                "Reservasi berhasil diproses:\n" +
                "Buku: " + reservation.getBook().getTitle() + "\n" +
                "Anggota: " + reservation.getMember().getName() + "\n" +
                "Status: " + reservation.getStatus(), 
                "Reservasi Diproses"
            );
        } catch (Exception ex) {
            GUIUtils.errorDialog(this, "Terjadi kesalahan saat memproses reservasi: " + ex.getMessage(), "Error");
        }
    }
    
    /**
     * Batalkan reservasi yang dipilih
     */
    private void cancelReservation() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih reservasi yang akan dibatalkan!", "Tidak Ada Reservasi Dipilih");
            return;
        }
        
        int modelRow = reservationTable.convertRowIndexToModel(selectedRow);
        Reservation reservation = tableModel.getReservationAt(modelRow);
        
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            GUIUtils.errorDialog(
                this, 
                "Reservasi ini tidak dalam status pending! Status saat ini: " + reservation.getStatus(), 
                "Status Tidak Valid"
            );
            return;
        }
        
        boolean confirm = GUIUtils.confirmDialog(
            this, 
            "Apakah Anda yakin ingin membatalkan reservasi ini?\n" +
            "Buku: " + reservation.getBook().getTitle() + "\n" +
            "Anggota: " + reservation.getMember().getName(), 
            "Konfirmasi Pembatalan"
        );
        
        if (confirm) {
            reservation.cancelReservation();
            
            refreshData();
            
            GUIUtils.infoDialog(this, "Reservasi berhasil dibatalkan.", "Reservasi Dibatalkan");
        }
    }
    
    /**
     * Lihat detail reservasi
     */
    private void viewReservationDetails() {
        int selectedRow = reservationTable.getSelectedRow();
        if (selectedRow == -1) {
            GUIUtils.errorDialog(this, "Pilih reservasi untuk melihat detail!", "Tidak Ada Reservasi Dipilih");
            return;
        }
        
        int modelRow = reservationTable.convertRowIndexToModel(selectedRow);
        Reservation reservation = tableModel.getReservationAt(modelRow);
        
        DialogUtils.showReservationDetailsDialog(this, reservation);
    }
}