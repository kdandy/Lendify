package com.library.model;

import com.library.enums.LoanStatus;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class BookLoan {
    private String loanId;
    private Member member;
    private BookItem bookItem;
    private Date issueDate;
    private Date dueDate;
    private Date returnDate;
    private double fine;
    private double finePaid;
    private LoanStatus status;
    
    // kostant
    private static final double FINE_PER_DAY = 1.0; // $1 per day
    
    // konstruktor
    public BookLoan(Member member, BookItem bookItem) {
        this.loanId = "L" + UUID.randomUUID().toString().substring(0, 8);
        this.member = member;
        this.bookItem = bookItem;
        this.issueDate = new Date();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.issueDate);
        calendar.add(Calendar.DAY_OF_MONTH, member.getMaxLoanDays());
        this.dueDate = calendar.getTime();
        
        this.fine = 0.0;
        this.finePaid = 0.0;
        this.status = LoanStatus.ACTIVE;
    }
    
    // getters and setters
    public String getLoanId() {
        return loanId;
    }
    
    public Member getMember() {
        return member;
    }
    
    public BookItem getBookItem() {
        return bookItem;
    }
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public Date getDueDate() {
        return dueDate;
    }
    
    public Date getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }
    
    public double getFine() {
        return fine;
    }
    
    public void setFine(double fine) {
        this.fine = fine;
    }
    
    public double getFinePaid() {
        return finePaid;
    }
    
    public void setFinePaid(double finePaid) {
        this.finePaid = finePaid;
    }
    
    public LoanStatus getStatus() {
        return status;
    }
    
    public void setStatus(LoanStatus status) {
        this.status = status;
    }
    
    // Methods
    public boolean isOverdue() {
        if (status != LoanStatus.ACTIVE) {
            return false;
        }
        
        Date currentDate = new Date();
        return currentDate.after(dueDate);
    }
    
    public double calculateFine() {
        if (returnDate == null && !isOverdue()) {
            return 0.0;
        }
        
        long daysLate;
        Date currentDate = new Date();
        
        if (returnDate != null) {
            // buku sudah dikembalikan
            daysLate = (returnDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
        } else {
            // buku belum dikembalikan
            daysLate = (currentDate.getTime() - dueDate.getTime()) / (1000 * 60 * 60 * 24);
        }
        
        return daysLate > 0 ? daysLate * FINE_PER_DAY : 0.0;
    }
    
    public boolean extendDueDate(int days) {
        // cek apakah buku sudah lewat tanggal jatuh tempo
        if (isOverdue()) {
            return false;
        }
        
        // cek apakah buku memiliki reservasi yang tertunda
        Book book = bookItem.getBook();
        if (!book.getPendingReservations().isEmpty()) {
            return false;
        }
        
        // perpanjang tanggal jatuh tempo
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.dueDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        this.dueDate = calendar.getTime();
        
        return true;
    }
    
    public void payFine(double amount) {
        this.finePaid += amount;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookLoan bookLoan = (BookLoan) o;
        return Objects.equals(loanId, bookLoan.loanId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(loanId);
    }
    
    @Override
    public String toString() {
        return "BookLoan{" +
                "loanId='" + loanId + '\'' +
                ", member=" + member.getName() +
                ", book=" + bookItem.getBook().getTitle() +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                ", status=" + status +
                ", fine=" + fine +
                '}';
    }
}