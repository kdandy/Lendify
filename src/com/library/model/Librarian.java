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
    
    // Constructors
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
    
    // Getters and Setters
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
    
    // Methods
    public BookItem addBookItem(Book book, String barcode) throws InvalidOperationException {
        if (permission == LibrarianPermission.BASIC) {
            throw new InvalidOperationException("You do not have permission to add book items. Required: FULL or ADMIN.");
        }
        
        BookItem bookItem = new BookItem(book, barcode);
        book.addBookItem(bookItem);
        return bookItem;
    }
    
    public Member addMember(Person person) throws InvalidOperationException {
        if (permission == LibrarianPermission.BASIC) {
            throw new InvalidOperationException("You do not have permission to add members. Required: FULL or ADMIN.");
        }
        
        String memberId = "M" + UUID.randomUUID().toString().substring(0, 8);
        Member member = new Member(person, memberId);
        return member;
    }
    
    public BookLoan issueBook(Member member, BookItem bookItem) throws InvalidOperationException, ReferenceOnlyException {
        // Check if the member is active
        if (!member.isActive() || member.getStatus() != MemberStatus.ACTIVE) {
            throw new InvalidOperationException("Cannot issue book to inactive or suspended member.");
        }
        
        // Check if the book is reference only
        if (bookItem.isReferenceOnly()) {
            throw new ReferenceOnlyException("Cannot issue reference book: " + bookItem.getBook().getTitle());
        }
        
        // Check if the book is available
        if (!bookItem.isAvailable()) {
            throw new InvalidOperationException("Book is not available for checkout: " + bookItem.getBook().getTitle());
        }
        
        try {
            BookLoan bookLoan = member.checkoutBook(bookItem);
            return bookLoan;
        } catch (Exception e) {
            throw new InvalidOperationException("Error issuing book: " + e.getMessage());
        }
    }
    
    public void returnBook(BookLoan bookLoan) throws InvalidOperationException {
        if (bookLoan.getStatus() != LoanStatus.ACTIVE && bookLoan.getStatus() != LoanStatus.OVERDUE) {
            throw new InvalidOperationException("Cannot return a book that is not active or overdue: " + bookLoan.getStatus());
        }
        
        Member member = bookLoan.getMember();
        member.returnBook(bookLoan);
        
        // Check if there are any reservations for this book
        Book book = bookLoan.getBookItem().getBook();
        for (Reservation reservation : book.getReservations()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                reservation.setStatus(ReservationStatus.FULFILLED);
                break;
            }
        }
    }
    
    public void updateBookInfo(Book book, String title, String author, String publisher, int year) throws InvalidOperationException {
        if (permission == LibrarianPermission.BASIC) {
            throw new InvalidOperationException("You do not have permission to update book information. Required: FULL or ADMIN.");
        }
        
        book.setTitle(title);
        book.setAuthor(author);
        book.setPublisher(publisher);
        book.setPublicationYear(year);
    }
    
    public void blacklistMember(Member member) throws InvalidOperationException {
        if (permission != LibrarianPermission.ADMIN) {
            throw new InvalidOperationException("You do not have permission to blacklist members. Required: ADMIN.");
        }
        
        member.setStatus(MemberStatus.BLACKLISTED);
        member.setActive(false);
    }
    
    public void removeBookItem(BookItem bookItem) throws InvalidOperationException {
        if (permission != LibrarianPermission.ADMIN) {
            throw new InvalidOperationException("You do not have permission to remove book items. Required: ADMIN.");
        }
        
        bookItem.setActive(false);
        Book book = bookItem.getBook();
        book.removeBookItem(bookItem);
    }
    
    public void processReservation(Reservation reservation) throws InvalidOperationException {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidOperationException("Cannot process a reservation that is not pending.");
        }
        
        Book book = reservation.getBook();
        // Check if any copy of the book is available
        for (BookItem bookItem : book.getAvailableItems()) {
            try {
                issueBook(reservation.getMember(), bookItem);
                reservation.setStatus(ReservationStatus.FULFILLED);
                return;
            } catch (Exception e) {
                // Try the next available copy
                continue;
            }
        }
        
        throw new InvalidOperationException("No copies of the book are available for the reservation.");
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