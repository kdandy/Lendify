package com.library.gui.utils;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Kelas utilitas untuk menangani dialog-dialog yang umum digunakan
 */
public class DialogUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Membuat dialog untuk informasi detail buku
     */
    public static void showBookDetailsDialog(Component parent, Book book) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel informasi dasar buku
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        infoPanel.add(createLabel("ISBN:"));
        infoPanel.add(createValueLabel(book.getISBN()));
        
        infoPanel.add(createLabel("Judul:"));
        infoPanel.add(createValueLabel(book.getTitle()));
        
        infoPanel.add(createLabel("Pengarang:"));
        infoPanel.add(createValueLabel(book.getAuthor()));
        
        infoPanel.add(createLabel("Penerbit:"));
        infoPanel.add(createValueLabel(book.getPublisher()));
        
        infoPanel.add(createLabel("Tahun Terbit:"));
        infoPanel.add(createValueLabel(String.valueOf(book.getPublicationYear())));
        
        infoPanel.add(createLabel("Format:"));
        infoPanel.add(createValueLabel(book.getFormat().toString()));
        
        infoPanel.add(createLabel("Bahasa:"));
        infoPanel.add(createValueLabel(book.getLanguage().toString()));
        
        infoPanel.add(createLabel("Jumlah Halaman:"));
        infoPanel.add(createValueLabel(String.valueOf(book.getNumberOfPages())));
        
        infoPanel.add(createLabel("Deskripsi:"));
        JTextArea descArea = new JTextArea(book.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(new JLabel().getBackground());
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 100));
        
        // Tambahkan komponen ke panel
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(descScroll, BorderLayout.CENTER);
        
        // Panel untuk daftar salinan buku (book items)
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Salinan Buku"));
        
        String[] columnNames = {"Barcode", "Status", "Lokasi", "Referensi", "Harga"};
        Object[][] data = new Object[book.getItems().size()][5];
        
        int i = 0;
        for (BookItem item : book.getItems()) {
            data[i][0] = item.getBarcode();
            data[i][1] = item.getStatus();
            data[i][2] = item.getLocation();
            data[i][3] = item.isReferenceOnly() ? "Ya" : "Tidak";
            data[i][4] = String.format("Rp %.2f", item.getPrice());
            i++;
        }
        
        JTable itemsTable = new JTable(data, columnNames);
        itemsTable.setEnabled(false);
        JScrollPane tableScroll = new JScrollPane(itemsTable);
        tableScroll.setPreferredSize(new Dimension(500, 150));
        
        itemsPanel.add(tableScroll, BorderLayout.CENTER);
        
        // Panel kategori
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.setBorder(BorderFactory.createTitledBorder("Kategori"));
        
        StringBuilder categoryText = new StringBuilder();
        for (BookCategory category : book.getCategories()) {
            if (categoryText.length() > 0) {
                categoryText.append(", ");
            }
            categoryText.append(category.getName());
        }
        
        JLabel categoryLabel = new JLabel(categoryText.length() > 0 ? categoryText.toString() : "Tidak ada kategori");
        categoryPanel.add(categoryLabel, BorderLayout.CENTER);
        
        // Tambahkan panel tambahan
        JPanel additionalPanel = new JPanel(new BorderLayout(5, 5));
        additionalPanel.add(itemsPanel, BorderLayout.CENTER);
        additionalPanel.add(categoryPanel, BorderLayout.SOUTH);
        
        panel.add(additionalPanel, BorderLayout.SOUTH);
        
        // Tampilkan dialog
        JOptionPane.showMessageDialog(
            parent,
            panel,
            "Detail Buku: " + book.getTitle(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Membuat dialog untuk informasi detail anggota
     */
    public static void showMemberDetailsDialog(Component parent, Member member) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel informasi dasar anggota
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        infoPanel.add(createLabel("ID Anggota:"));
        infoPanel.add(createValueLabel(member.getMemberId()));
        
        infoPanel.add(createLabel("Nama:"));
        infoPanel.add(createValueLabel(member.getName()));
        
        infoPanel.add(createLabel("Alamat:"));
        infoPanel.add(createValueLabel(member.getAddress()));
        
        infoPanel.add(createLabel("Telepon:"));
        infoPanel.add(createValueLabel(member.getPhoneNumber()));
        
        infoPanel.add(createLabel("Email:"));
        infoPanel.add(createValueLabel(member.getEmail()));
        
        infoPanel.add(createLabel("Tanggal Registrasi:"));
        infoPanel.add(createValueLabel(DATE_FORMAT.format(member.getRegistrationDate())));
        
        infoPanel.add(createLabel("Tanggal Kadaluarsa:"));
        infoPanel.add(createValueLabel(DATE_FORMAT.format(member.getExpiryDate())));
        
        infoPanel.add(createLabel("Status:"));
        infoPanel.add(createValueLabel(member.getStatus().toString()));
        
        infoPanel.add(createLabel("Aktif:"));
        infoPanel.add(createValueLabel(member.isActive() ? "Ya" : "Tidak"));
        
        infoPanel.add(createLabel("Batas Buku:"));
        infoPanel.add(createValueLabel(String.valueOf(member.getMaxBooks())));
        
        infoPanel.add(createLabel("Durasi Peminjaman:"));
        infoPanel.add(createValueLabel(member.getMaxLoanDays() + " hari"));
        
        infoPanel.add(createLabel("Buku yang Sedang Dipinjam:"));
        infoPanel.add(createValueLabel(String.valueOf(member.getCurrentBooksCount())));
        
        infoPanel.add(createLabel("Total Denda Dibayar:"));
        infoPanel.add(createValueLabel(String.format("Rp %.2f", member.getTotalFinesPaid())));
        
        // Informasi khusus berdasarkan jenis anggota
        if (member instanceof StudentMember) {
            StudentMember studentMember = (StudentMember) member;
            
            infoPanel.add(createLabel("Jenis Anggota:"));
            infoPanel.add(createValueLabel("Mahasiswa"));
            
            infoPanel.add(createLabel("ID Mahasiswa:"));
            infoPanel.add(createValueLabel(studentMember.getStudentId()));
            
            infoPanel.add(createLabel("Fakultas:"));
            infoPanel.add(createValueLabel(studentMember.getFaculty()));
            
            infoPanel.add(createLabel("Jurusan:"));
            infoPanel.add(createValueLabel(studentMember.getDepartment()));
            
            infoPanel.add(createLabel("Tahun Studi:"));
            infoPanel.add(createValueLabel(String.valueOf(studentMember.getYearOfStudy())));
        } else if (member instanceof RegularMember) {
            RegularMember regularMember = (RegularMember) member;
            
            infoPanel.add(createLabel("Jenis Anggota:"));
            infoPanel.add(createValueLabel("Reguler"));
            
            infoPanel.add(createLabel("Pekerjaan:"));
            infoPanel.add(createValueLabel(regularMember.getOccupation()));
            
            infoPanel.add(createLabel("Perusahaan/Institusi:"));
            infoPanel.add(createValueLabel(regularMember.getEmployerName()));
            
            infoPanel.add(createLabel("Premium:"));
            infoPanel.add(createValueLabel(regularMember.isPremium() ? "Ya" : "Tidak"));
        }
        
        panel.add(new JScrollPane(infoPanel), BorderLayout.NORTH);
        
        // Panel untuk daftar peminjaman aktif
        List<BookLoan> activeLoans = new ArrayList<>();
        for (BookLoan loan : member.getBookLoans()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                activeLoans.add(loan);
            }
        }
        
        if (!activeLoans.isEmpty()) {
            JPanel loansPanel = new JPanel(new BorderLayout());
            loansPanel.setBorder(BorderFactory.createTitledBorder("Peminjaman Aktif"));
            
            String[] loanColumns = {"ID", "Buku", "Tanggal Pinjam", "Jatuh Tempo", "Status"};
            Object[][] loanData = new Object[activeLoans.size()][5];
            
            for (int i = 0; i < activeLoans.size(); i++) {
                BookLoan loan = activeLoans.get(i);
                loanData[i][0] = loan.getLoanId();
                loanData[i][1] = loan.getBookItem().getBook().getTitle();
                loanData[i][2] = DATE_FORMAT.format(loan.getIssueDate());
                loanData[i][3] = DATE_FORMAT.format(loan.getDueDate());
                loanData[i][4] = loan.getStatus();
            }
            
            JTable loansTable = new JTable(loanData, loanColumns);
            loansTable.setEnabled(false);
            JScrollPane loansScroll = new JScrollPane(loansTable);
            loansScroll.setPreferredSize(new Dimension(500, 100));
            
            loansPanel.add(loansScroll, BorderLayout.CENTER);
            panel.add(loansPanel, BorderLayout.CENTER);
        }
        
        // Panel untuk daftar reservasi aktif
        List<Reservation> activeReservations = new ArrayList<>();
        for (Reservation reservation : member.getReservations()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                activeReservations.add(reservation);
            }
        }
        
        if (!activeReservations.isEmpty()) {
            JPanel reservationsPanel = new JPanel(new BorderLayout());
            reservationsPanel.setBorder(BorderFactory.createTitledBorder("Reservasi Aktif"));
            
            String[] reservationColumns = {"ID", "Buku", "Tanggal Reservasi", "Status"};
            Object[][] reservationData = new Object[activeReservations.size()][4];
            
            for (int i = 0; i < activeReservations.size(); i++) {
                Reservation reservation = activeReservations.get(i);
                reservationData[i][0] = reservation.getReservationId();
                reservationData[i][1] = reservation.getBook().getTitle();
                reservationData[i][2] = DATE_FORMAT.format(reservation.getReservationDate());
                reservationData[i][3] = reservation.getStatus();
            }
            
            JTable reservationsTable = new JTable(reservationData, reservationColumns);
            reservationsTable.setEnabled(false);
            JScrollPane reservationsScroll = new JScrollPane(reservationsTable);
            reservationsScroll.setPreferredSize(new Dimension(500, 100));
            
            reservationsPanel.add(reservationsScroll, BorderLayout.CENTER);
            
            if (panel.getComponentCount() == 2) {
                // Jika sudah ada peminjaman, tambahkan panel reservasi di bawah
                panel.add(reservationsPanel, BorderLayout.SOUTH);
            } else {
                // Jika belum ada peminjaman, tambahkan panel reservasi di tengah
                panel.add(reservationsPanel, BorderLayout.CENTER);
            }
        }
        
        // Tampilkan dialog
        JOptionPane.showMessageDialog(
            parent,
            new JScrollPane(panel),
            "Detail Anggota: " + member.getName(),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Membuat dialog untuk informasi detail peminjaman
     */
    public static void showLoanDetailsDialog(Component parent, BookLoan loan) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel informasi dasar peminjaman
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        infoPanel.add(createLabel("ID Peminjaman:"));
        infoPanel.add(createValueLabel(loan.getLoanId()));
        
        infoPanel.add(createLabel("Judul Buku:"));
        infoPanel.add(createValueLabel(loan.getBookItem().getBook().getTitle()));
        
        infoPanel.add(createLabel("ISBN:"));
        infoPanel.add(createValueLabel(loan.getBookItem().getBook().getISBN()));
        
        infoPanel.add(createLabel("Barcode Buku:"));
        infoPanel.add(createValueLabel(loan.getBookItem().getBarcode()));
        
        infoPanel.add(createLabel("Anggota:"));
        infoPanel.add(createValueLabel(loan.getMember().getName()));
        
        infoPanel.add(createLabel("ID Anggota:"));
        infoPanel.add(createValueLabel(loan.getMember().getMemberId()));
        
        infoPanel.add(createLabel("Tanggal Peminjaman:"));
        infoPanel.add(createValueLabel(DATE_FORMAT.format(loan.getIssueDate())));
        
        infoPanel.add(createLabel("Tanggal Jatuh Tempo:"));
        infoPanel.add(createValueLabel(DATE_FORMAT.format(loan.getDueDate())));
        
        infoPanel.add(createLabel("Status:"));
        infoPanel.add(createValueLabel(loan.getStatus().toString()));
        
        if (loan.getReturnDate() != null) {
            infoPanel.add(createLabel("Tanggal Pengembalian:"));
            infoPanel.add(createValueLabel(DATE_FORMAT.format(loan.getReturnDate())));
        }
        
        infoPanel.add(createLabel("Denda:"));
        infoPanel.add(createValueLabel(loan.getFine() > 0 ? String.format("Rp %.2f", loan.getFine()) : "-"));
        
        infoPanel.add(createLabel("Informasi:"));
        infoPanel.add(createValueLabel("ID: " + loan.getLoanId() + ", Status: " + loan.getStatus()));
        
        panel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        
        // Tampilkan dialog
        JOptionPane.showMessageDialog(
            parent,
            panel,
            "Detail Peminjaman",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Membuat dialog untuk informasi detail reservasi
     */
    public static void showReservationDetailsDialog(Component parent, Reservation reservation) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel informasi dasar reservasi
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        infoPanel.add(createLabel("ID Reservasi:"));
        infoPanel.add(createValueLabel(reservation.getReservationId()));
        
        infoPanel.add(createLabel("Judul Buku:"));
        infoPanel.add(createValueLabel(reservation.getBook().getTitle()));
        
        infoPanel.add(createLabel("ISBN:"));
        infoPanel.add(createValueLabel(reservation.getBook().getISBN()));
        
        infoPanel.add(createLabel("Anggota:"));
        infoPanel.add(createValueLabel(reservation.getMember().getName()));
        
        infoPanel.add(createLabel("ID Anggota:"));
        infoPanel.add(createValueLabel(reservation.getMember().getMemberId()));
        
        infoPanel.add(createLabel("Tanggal Reservasi:"));
        infoPanel.add(createValueLabel(DATE_FORMAT.format(reservation.getReservationDate())));
        
        infoPanel.add(createLabel("Status:"));
        infoPanel.add(createValueLabel(reservation.getStatus().toString()));
        
        panel.add(new JScrollPane(infoPanel), BorderLayout.CENTER);
        
        // Tampilkan dialog
        JOptionPane.showMessageDialog(
            parent,
            panel,
            "Detail Reservasi",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Dialog untuk memilih anggota
     */
    public static Member showMemberSelectionDialog(Component parent, java.util.List<Member> members, String title) {
        if (members == null || members.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Tidak ada anggota yang tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String[] columnNames = {"ID", "Nama", "Tipe", "Status"};
        Object[][] data = new Object[members.size()][4];
        
        for (int i = 0; i < members.size(); i++) {
            Member member = members.get(i);
            data[i][0] = member.getMemberId();
            data[i][1] = member.getName();
            
            if (member instanceof StudentMember) {
                data[i][2] = "Mahasiswa";
            } else if (member instanceof RegularMember) {
                data[i][2] = "Reguler";
            } else {
                data[i][2] = "Anggota";
            }
            
            data[i][3] = member.getStatus();
        }
        
        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            scrollPane,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                return members.get(selectedRow);
            }
        }
        
        return null;
    }
    
    /**
     * Dialog untuk memilih buku
     */
    public static Book showBookSelectionDialog(Component parent, java.util.List<Book> books, String title) {
        if (books == null || books.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Tidak ada buku yang tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String[] columnNames = {"ISBN", "Judul", "Pengarang", "Penerbit", "Tahun", "Ketersediaan"};
        Object[][] data = new Object[books.size()][6];
        
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            data[i][0] = book.getISBN();
            data[i][1] = book.getTitle();
            data[i][2] = book.getAuthor();
            data[i][3] = book.getPublisher();
            data[i][4] = book.getPublicationYear();
            data[i][5] = book.getAvailableItems().size() + " dari " + book.getItems().size();
        }
        
        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            scrollPane,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                return books.get(selectedRow);
            }
        }
        
        return null;
    }
    
    /**
     * Dialog untuk memilih salinan buku (book item)
     */
    public static BookItem showBookItemSelectionDialog(Component parent, java.util.List<BookItem> bookItems, String title) {
        if (bookItems == null || bookItems.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Tidak ada salinan buku yang tersedia!", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        
        String[] columnNames = {"Barcode", "Status", "Lokasi", "Referensi", "Dapat Dipinjam"};
        Object[][] data = new Object[bookItems.size()][5];
        
        for (int i = 0; i < bookItems.size(); i++) {
            BookItem item = bookItems.get(i);
            data[i][0] = item.getBarcode();
            data[i][1] = item.getStatus();
            data[i][2] = item.getLocation();
            data[i][3] = item.isReferenceOnly() ? "Ya" : "Tidak";
            data[i][4] = item.isAvailable() && !item.isReferenceOnly() ? "Ya" : "Tidak";
        }
        
        JTable table = new JTable(data, columnNames);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            scrollPane,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                return bookItems.get(selectedRow);
            }
        }
        
        return null;
    }
    
    /**
     * Membuat dialog untuk input tanggal
     */
    public static Date showDateInputDialog(Component parent, String title, String message, Date defaultDate) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        if (message != null && !message.isEmpty()) {
            JLabel messageLabel = new JLabel(message);
            panel.add(messageLabel, BorderLayout.NORTH);
        }
        
        JTextField dateField = new JTextField(10);
        if (defaultDate != null) {
            dateField.setText(DATE_FORMAT.format(defaultDate));
        }
        
        JLabel formatLabel = new JLabel("Format: dd-MM-yyyy");
        formatLabel.setFont(new Font(formatLabel.getFont().getName(), Font.ITALIC, 11));
        
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.add(dateField, BorderLayout.CENTER);
        inputPanel.add(formatLabel, BorderLayout.SOUTH);
        
        panel.add(inputPanel, BorderLayout.CENTER);
        
        int result = JOptionPane.showConfirmDialog(
            parent,
            panel,
            title,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String dateStr = dateField.getText().trim();
            if (!dateStr.isEmpty()) {
                try {
                    return DATE_FORMAT.parse(dateStr);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(
                        parent,
                        "Format tanggal tidak valid! Gunakan format dd-MM-yyyy.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        }
        
        return null;
    }
    
    // Helper methods
    
    /**
     * Buat label dengan alignment kanan
     */
    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
        return label;
    }
    
    /**
     * Buat label untuk menampilkan nilai
     */
    private static JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        return label;
    }
}