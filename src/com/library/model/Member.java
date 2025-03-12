package com.library.model;

import com.library.enums.MemberStatus;
import com.library.enums.LoanStatus;
import com.library.exception.InactiveAccountException;
import com.library.exception.MaxBooksReachedException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Member extends Person {
    private String memberId;
    private Date registrationDate;
    private Date expiryDate;
    private boolean isActive;
    private MemberStatus status;
    private List<BookLoan> bookLoans;
    private List<Reservation> reservations;
    private double totalFinesPaid;
    
    // konstruktor
    public Member() {
        this.bookLoans = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.registrationDate = new Date();
        this.isActive = true;
        this.status = MemberStatus.ACTIVE;
        this.totalFinesPaid = 0.0;
    }
    
    public Member(Person person, String memberId) {
        super(person.getId(), person.getName(), person.getAddress(), person.getPhoneNumber());
        setEmail(person.getEmail());
        this.memberId = memberId;
        this.bookLoans = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.registrationDate = new Date();
        this.isActive = true;
        this.status = MemberStatus.ACTIVE;
        this.totalFinesPaid = 0.0;
        
        // seting expiry date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.registrationDate);
        calendar.add(Calendar.YEAR, 1);
        this.expiryDate = calendar.getTime();
    }
    
    // getter dan setter
    public String getMemberId() {
        return memberId;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    public MemberStatus getStatus() {
        return status;
    }
    
    public void setStatus(MemberStatus status) {
        this.status = status;
        if (status == MemberStatus.BLACKLISTED || status == MemberStatus.INACTIVE) {
            this.isActive = false;
        }
    }
    
    public List<BookLoan> getBookLoans() {
        return bookLoans;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    public double getTotalFinesPaid() {
        return totalFinesPaid;
    }
    
    // metode
    public double calculateFine(BookItem book) {
        double totalFine = 0.0;
        for (BookLoan loan : bookLoans) {
            if (loan.getBookItem().equals(book)) {
                totalFine += loan.calculateFine();
            }
        }
        return totalFine;
    }
    
    public double calculateTotalFines() {
        double totalFine = 0.0;
        for (BookLoan loan : bookLoans) {
            totalFine += loan.calculateFine();
        }
        return totalFine;
    }
    
    public BookLoan checkoutBook(BookItem book) throws InactiveAccountException, MaxBooksReachedException {
        if (!isActive) {
            throw new InactiveAccountException("Your account is not active. Please contact the librarian.");
        }
        
        if (getCurrentBooksCount() >= getMaxBooks()) {
            throw new MaxBooksReachedException("You have reached the maximum number of books that can be checked out.");
        }
        
        BookLoan loan = new BookLoan(this, book);
        bookLoans.add(loan);
        book.checkout();
        return loan;
    }
    
    public void returnBook(BookLoan loan) {
        loan.setReturnDate(new Date());
        loan.setStatus(LoanStatus.COMPLETED);
        loan.getBookItem().checkin();
        
        double fine = loan.calculateFine();
        loan.setFine(fine);
    }
    
    public void payFine(double amount) {
        totalFinesPaid += amount;
    }
    
    public void renewMembership(int months) {
        if (expiryDate.before(new Date())) {
            // jika membership sudah expired, extend dari tanggal sekarang
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, months);
            expiryDate = calendar.getTime();
        } else {
            // jika membership masih aktif, extend dari expiry date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(expiryDate);
            calendar.add(Calendar.MONTH, months);
            expiryDate = calendar.getTime();
        }
        setActive(true);
        setStatus(MemberStatus.ACTIVE);
    }
    
    public Reservation reserveBook(Book book) throws InactiveAccountException {
        if (!isActive) {
            throw new InactiveAccountException("Your account is not active. Please contact the librarian.");
        }
        
        Reservation reservation = new Reservation(this, book);
        reservations.add(reservation);
        return reservation;
    }
    
    public void cancelReservation(Reservation reservation) {
        reservation.cancelReservation();
        reservations.remove(reservation);
    }
    
    public int getMaxBooks() {
        return 5;
    }
    
    public int getMaxLoanDays() {
        return 14;
    }
    
    public int getCurrentBooksCount() {
        int count = 0;
        for (BookLoan loan : bookLoans) {
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }
    
    @Override
    public String toString() {
        return "Member{" +
                "memberId='" + memberId + '\'' +
                ", name='" + getName() + '\'' +
                ", status=" + status +
                ", active=" + isActive +
                ", books checked out=" + getCurrentBooksCount() +
                '}';
    }
}