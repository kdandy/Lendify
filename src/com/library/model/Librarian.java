package com.library.model;

import com.library.enums.LibrarianPermission;
import com.library.enums.LoanStatus;
import com.library.enums.MemberStatus;
import com.library.enums.ReservationStatus;
import com.library.exception.InvalidOperationException;
import com.library.exception.ReferenceOnlyException;

import java.util.Date;
import java.util.UUID;

public class Librarian extends Person {
    private String staffId;
    private String position;
    private Date joiningDate;
    private double salary;
    private LibrarianPermission permission;
    
    // konsruktor
    public Librarian() {
        this.joiningDate = new Date();
        this.permission = LibrarianPermission.BASIC;
    }
    
    public Librarian(Person person, String staffId, String position) {
        super(person.getId(), person.getName(), person.getAddress(), person.getPhoneNumber());
        setEmail(person.getEmail());
        this.staffId = staffId;
        this.position = position;
        this.joiningDate = new Date();
        this.permission = LibrarianPermission.BASIC;
    }
    
    public Librarian(Person person, String staffId, String position, double salary, LibrarianPermission permission) {
        super(person.getId(), person.getName(), person.getAddress(), person.getPhoneNumber());
        setEmail(person.getEmail());
        this.staffId = staffId;
        this.position = position;
        this.salary = salary;
        this.permission = permission;
        this.joiningDate = new Date();
    }
    
    // getter dan setter
    public String getStaffId() {
        return staffId;
    }
    
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    
    public String getPosition() {
        return position;
    }
    
    public void setPosition(String position) {
        this.position = position;
    }
    
    public Date getJoiningDate() {
        return joiningDate;
    }
    
    public double getSalary() {
        return salary;
    }
    
    public void setSalary(double salary) {
        this.salary = salary;
    }
    
    public LibrarianPermission getPermission() {
        return permission;
    }
    
    public void setPermission(LibrarianPermission permission) {
        this.permission = permission;
    }
    
    // mutator
    public BookItem addBookItem(Book book, String barcode) throws InvalidOperationException {
        if (permission == LibrarianPermission.BASIC) {
            throw new InvalidOperationException("Anda tidak memiliki izin untuk menambahkan item buku. Diperlukan: Admin.");
        }
        
        BookItem bookItem = new BookItem(book, barcode);
        book.addBookItem(bookItem);
        return bookItem;
    }
    
    public Member addMember(Person person) throws InvalidOperationException {
        if (permission == LibrarianPermission.BASIC) {
            throw new InvalidOperationException("Anda tidak memiliki izin untuk menambahkan anggota. Diperlukan: Admin.");
        }
        
        String memberId = "M" + UUID.randomUUID().toString().substring(0, 8);
        Member member = new Member(person, memberId);
        return member;
    }
    
    public BookLoan issueBook(Member member, BookItem bookItem) throws InvalidOperationException, ReferenceOnlyException {
        // periksa apakah anggota aktif
        if (!member.isActive() || member.getStatus() != MemberStatus.ACTIVE) {
            throw new InvalidOperationException("Tidak dapat menerbitkan buku kepada anggota yang tidak aktif atau ditangguhkan.");
        }
        
        // periksa apakah buku tersebut hanya referensi
        if (bookItem.isReferenceOnly()) {
            throw new ReferenceOnlyException("Tidak dapat menerbitkan buku referensi: " + bookItem.getBook().getTitle());
        }
        
        // periksa apakah buku ada
        if (!bookItem.isAvailable()) {
            throw new InvalidOperationException("Buku tidak tersedia untuk pembayaran: " + bookItem.getBook().getTitle());
        }
        
        try {
            BookLoan bookLoan = member.checkoutBook(bookItem);
            return bookLoan;
        } catch (Exception e) {
            throw new InvalidOperationException("Terjadi kesalahan saat menerbitkan buku: " + e.getMessage());
        }
    }
    
    public void returnBook(BookLoan bookLoan) throws InvalidOperationException {
        if (bookLoan.getStatus() != LoanStatus.ACTIVE && bookLoan.getStatus() != LoanStatus.OVERDUE) {
            throw new InvalidOperationException("Tidak dapat mengembalikan buku yang tidak aktif atau lewat waktu pengembalian: " + bookLoan.getStatus());
        }
        
        Member member = bookLoan.getMember();
        BookItem returnedItem = bookLoan.getBookItem();
        member.returnBook(bookLoan);
        
        // cek apakah ada reservasi yang menunggu
        Book book = returnedItem.getBook();
        for (Reservation reservation : book.getReservations()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                try {
                    // Coba pinjamkan buku ke anggota yang melakukan reservasi
                    BookLoan newLoan = issueBook(reservation.getMember(), returnedItem);
                    reservation.setStatus(ReservationStatus.FULFILLED);
                    System.out.println("Reservasi yang tertunda sedang diproses untuk: " + reservation.getMember().getName());
                    System.out.println("Buku yang diterbitkan dengan ID pinjaman: " + newLoan.getLoanId());
                    break; // Hanya proses satu reservasi (yang paling lama menunggu)
                } catch (Exception e) {
                    System.out.println("Gagal memproses reservasi untuk " + reservation.getMember().getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    public void updateBookInfo(Book book, String title, String author, String publisher, int year) throws InvalidOperationException {
        if (permission == LibrarianPermission.BASIC) {
            throw new InvalidOperationException("Anda tidak memiliki izin untuk memperbarui informasi buku. Diperlukan: Admin.");
        }
        
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPublicationYear(year);
    }
    
    public void blacklistMember(Member member) throws InvalidOperationException {
        if (permission != LibrarianPermission.ADMIN) {
            throw new InvalidOperationException("Anda tidak memiliki izin untuk memasukkan anggota ke dalam daftar hitam. Diperlukan: Admin");
        }
        
        member.setStatus(MemberStatus.BLACKLISTED);
        member.setActive(false);
    }
    
    public void removeBookItem(BookItem bookItem) throws InvalidOperationException {
        if (permission != LibrarianPermission.ADMIN) {
            throw new InvalidOperationException("Anda tidak memiliki izin untuk menghapus item buku. Diperlukan: Admin");
        }
        
        bookItem.setActive(false);
        Book book = bookItem.getBook();
        book.removeBookItem(bookItem);
    }
    
    public void processReservation(Reservation reservation) throws InvalidOperationException {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidOperationException("Tidak dapat memproses reservasi yang tidak tertunda.");
        }
        
        Book book = reservation.getBook();
        // cek apakah ada buku yang tersedia
        if (book.getAvailableItems().isEmpty()) {
            // tidak ada buku tersedia, tetap dalam status PENDING
            System.out.println("Tidak ada salinan yang tersedia. Reservasi masih dalam status PENDING.");
            System.out.println("Reservasi akan diproses ketika salinannya tersedia.");
            return; // keluar dari metode tanpa mengubah status
        }
        
        // ada buku tersedia, coba pinjamkan
        for (BookItem bookItem : book.getAvailableItems()) {
            try {
                BookLoan loan = issueBook(reservation.getMember(), bookItem);
                reservation.setStatus(ReservationStatus.FULFILLED);
                System.out.println("Buku berhasil diterbitkan. ID Peminjaman: " + loan.getLoanId());
                return;
            } catch (Exception e) {
                // coba salinan berikutnya jika ada
                continue;
            }
        }
        
        // jika sampai di sini, berarti ada masalah dengan peminjaman
        throw new InvalidOperationException("Gagal menerbitkan buku untuk reservasi.");
    }
    
    @Override
    public String toString() {
        return "Librarian{" +
                "name='" + getName() + '\'' +
                ", staffId='" + staffId + '\'' +
                ", position='" + position + '\'' +
                ", permission=" + permission +
                '}';
    }
}