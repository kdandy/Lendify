package com.library.model;

import com.library.enums.ReservationStatus;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Reservation {
    private String reservationId;
    private Member member;
    private Book book;
    private Date reservationDate;
    private ReservationStatus status;
    
    // Constructor
    public Reservation(Member member, Book book) {
        this.reservationId = "R" + UUID.randomUUID().toString().substring(0, 8);
        this.member = member;
        this.book = book;
        this.reservationDate = new Date();
        this.status = ReservationStatus.PENDING;
        
        // Add this reservation to the book's reservation list
        book.addReservation(this);
    }
    
    // Getters and Setters
    public String getReservationId() {
        return reservationId;
    }
    
    public Member getMember() {
        return member;
    }
    
    public Book getBook() {
        return book;
    }
    
    public Date getReservationDate() {
        return reservationDate;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    // Methods
    public void cancelReservation() {
        this.status = ReservationStatus.CANCELLED;
        this.book.removeReservation(this);
    }
    
    public void markAsFulfilled() {
        this.status = ReservationStatus.FULFILLED;
    }
    
    public void markAsExpired() {
        this.status = ReservationStatus.EXPIRED;
        this.book.removeReservation(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(reservationId);
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", member=" + member.getName() +
                ", book=" + book.getTitle() +
                ", reservationDate=" + reservationDate +
                ", status=" + status +
                '}';
    }
}