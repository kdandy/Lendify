package com.library.gui.utils;

import com.library.model.*;

import javax.swing.table.AbstractTableModel;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Kelas utilitas untuk model tabel
 */
public class TableModels {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Model tabel untuk pustakawan
     */
    public static class LibrarianTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<Librarian> librarians;
        private final String[] columnNames = {"ID Staff", "Nama", "Email", "Nomor Telepon", "Posisi", "Hak Akses"};
        
        public LibrarianTableModel(List<Librarian> librarians) {
            this.librarians = librarians;
        }
        
        public void setLibrarians(List<Librarian> librarians) {
            this.librarians = librarians;
            fireTableDataChanged();
        }
        
        public Librarian getLibrarianAt(int row) {
            if (row >= 0 && row < librarians.size()) {
                return librarians.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return librarians.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Librarian librarian = librarians.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return librarian.getStaffId();
                case 1: return librarian.getName();
                case 2: return librarian.getEmail();
                case 3: return librarian.getPhoneNumber();
                case 4: return librarian.getPosition();
                case 5: return librarian.getPermission();
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk kategori buku
     */
    public static class CategoryTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<BookCategory> categories;
        private final String[] columnNames = {"Nama", "Deskripsi", "Jumlah Buku"};
        
        public CategoryTableModel(List<BookCategory> categories) {
            this.categories = categories;
        }
        
        public void setCategories(List<BookCategory> categories) {
            this.categories = categories;
            fireTableDataChanged();
        }
        
        public BookCategory getCategoryAt(int row) {
            if (row >= 0 && row < categories.size()) {
                return categories.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return categories.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BookCategory category = categories.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return category.getName();
                case 1: return category.getDescription();
                case 2: return category.getBooks().size();
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk buku
     */
    public static class BookTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<Book> books;
        private final String[] columnNames = {"ISBN", "Judul", "Pengarang", "Penerbit", "Tahun", "Format", "Bahasa", "Jumlah Salinan"};
        
        public BookTableModel(List<Book> books) {
            this.books = books;
        }
        
        public void setBooks(List<Book> books) {
            this.books = books;
            fireTableDataChanged();
        }
        
        public Book getBookAt(int row) {
            if (row >= 0 && row < books.size()) {
                return books.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return books.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Book book = books.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return book.getISBN();
                case 1: return book.getTitle();
                case 2: return book.getAuthor();
                case 3: return book.getPublisher();
                case 4: return book.getPublicationYear();
                case 5: return book.getFormat();
                case 6: return book.getLanguage();
                case 7: return book.getItems().size() + " (" + book.getAvailableItems().size() + " tersedia)";
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk item buku (salinan)
     */
    public static class BookItemTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<BookItem> bookItems;
        private final String[] columnNames = {"Barcode", "Status", "Harga", "Lokasi", "Dapat Dipinjam"};
        
        public BookItemTableModel(List<BookItem> bookItems) {
            this.bookItems = bookItems;
        }
        
        public void setBookItems(List<BookItem> bookItems) {
            this.bookItems = bookItems;
            fireTableDataChanged();
        }
        
        public BookItem getBookItemAt(int row) {
            if (row >= 0 && row < bookItems.size()) {
                return bookItems.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return bookItems.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BookItem item = bookItems.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return item.getBarcode();
                case 1: return item.getStatus();
                case 2: return String.format("Rp %.2f", item.getPrice());
                case 3: return item.getLocation();
                case 4: return !item.isReferenceOnly() ? "Ya" : "Tidak";
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk anggota
     */
/**
 * Model tabel untuk anggota
 */
    public static class MemberTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<Member> members;
        private final String[] columnNames = {"ID", "Nama", "Email", "Telepon", "Tipe", "Status", "Expire Date"};
        
        public MemberTableModel(List<Member> members) {
            this.members = members;
        }
        
        public void setMembers(List<Member> members) {
            this.members = members;
            fireTableDataChanged();
        }
        
        public Member getMemberAt(int row) {
            if (row >= 0 && row < members.size()) {
                return members.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return members.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Member member = members.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return member.getMemberId();
                case 1: return member.getName();
                case 2: return member.getEmail();
                case 3: return member.getPhoneNumber();
                case 4: return member instanceof StudentMember ? "Mahasiswa" : "Reguler";
                case 5: return member.isActive() ? "Aktif" : "Tidak Aktif";
                case 6: 
                    // Add null check before formatting date
                    Date expiryDate = member.getExpiryDate();
                    return expiryDate != null ? DATE_FORMAT.format(expiryDate) : "N/A";
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk peminjaman
     */
    public static class LoanTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<BookLoan> loans;
        private final String[] columnNames = {"ID", "Buku", "Anggota", "Tanggal Pinjam", "Jatuh Tempo", "Status", "Denda"};
        
        public LoanTableModel(List<BookLoan> loans) {
            this.loans = loans;
        }
        
        public void setLoans(List<BookLoan> loans) {
            this.loans = loans;
            fireTableDataChanged();
        }
        
        public BookLoan getLoanAt(int row) {
            if (row >= 0 && row < loans.size()) {
                return loans.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return loans.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            BookLoan loan = loans.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return loan.getLoanId();
                case 1: return loan.getBookItem().getBook().getTitle();
                case 2: return loan.getMember().getName();
                case 3: return DATE_FORMAT.format(loan.getIssueDate());
                case 4: return DATE_FORMAT.format(loan.getDueDate());
                case 5: return loan.getStatus();
                case 6: return loan.getFine() > 0 ? String.format("Rp %.2f", loan.getFine()) : "-";
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk reservasi
     */
    public static class ReservationTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<Reservation> reservations;
        private final String[] columnNames = {"ID", "Buku", "Anggota", "Tanggal Reservasi", "Status"};
        
        public ReservationTableModel(List<Reservation> reservations) {
            this.reservations = reservations;
        }
        
        public void setReservations(List<Reservation> reservations) {
            this.reservations = reservations;
            fireTableDataChanged();
        }
        
        public Reservation getReservationAt(int row) {
            if (row >= 0 && row < reservations.size()) {
                return reservations.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return reservations.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Reservation reservation = reservations.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return reservation.getReservationId();
                case 1: return reservation.getBook().getTitle();
                case 2: return reservation.getMember().getName();
                case 3: return DATE_FORMAT.format(reservation.getReservationDate());
                case 4: return reservation.getStatus();
                default: return null;
            }
        }
    }
    
    /**
     * Model tabel untuk hasil pencarian buku
     */
    public static class SearchResultTableModel extends AbstractTableModel {
        private static final long serialVersionUID = 1L;
        private List<Book> books;
        private final String[] columnNames = {"ISBN", "Judul", "Pengarang", "Penerbit", "Tahun", "Ketersediaan"};
        
        public SearchResultTableModel(List<Book> books) {
            this.books = books;
        }
        
        public void setBooks(List<Book> books) {
            this.books = books;
            fireTableDataChanged();
        }
        
        public Book getBookAt(int row) {
            if (row >= 0 && row < books.size()) {
                return books.get(row);
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return books.size();
        }
        
        @Override
        public int getColumnCount() {
            return columnNames.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Book book = books.get(rowIndex);
            
            switch (columnIndex) {
                case 0: return book.getISBN();
                case 1: return book.getTitle();
                case 2: return book.getAuthor();
                case 3: return book.getPublisher();
                case 4: return book.getPublicationYear();
                case 5: return book.getAvailableItems().size() + " dari " + book.getItems().size() + " tersedia";
                default: return null;
            }
        }
    }
}