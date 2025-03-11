package com.library.model;

import com.library.enums.BookStatus;

import java.util.Date;
import java.util.Objects;

public class BookItem {
    private String barcode;
    private Book book;
    private boolean isAvailable;
    private boolean isReferenceOnly;
    private Date purchaseDate;
    private double price;
    private BookStatus status;
    private String location;
    private boolean isActive;
    
    // Constructor
    public BookItem(Book book, String barcode) {
        this.book = book;
        this.barcode = barcode;
        this.isAvailable = true;
        this.isReferenceOnly = false;
        this.purchaseDate = new Date();
        this.status = BookStatus.AVAILABLE;
        this.isActive = true;
    }
    
    // Getters and Setters
    public String getBarcode() {
        return barcode;
    }
    
    public Book getBook() {
        return book;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public void setAvailable(boolean available) {
        isAvailable = available;
        this.status = available ? BookStatus.AVAILABLE : BookStatus.LOANED;
    }
    
    public boolean isReferenceOnly() {
        return isReferenceOnly;
    }
    
    public void setReferenceOnly(boolean referenceOnly) {
        isReferenceOnly = referenceOnly;
    }
    
    public Date getPurchaseDate() {
        return purchaseDate;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public BookStatus getStatus() {
        return status;
    }
    
    public void setStatus(BookStatus status) {
        this.status = status;
        this.isAvailable = (status == BookStatus.AVAILABLE);
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
    }
    
    // Methods
    public void checkout() {
        setAvailable(false);
        setStatus(BookStatus.LOANED);
    }
    
    public void checkin() {
        setAvailable(true);
        setStatus(BookStatus.AVAILABLE);
    }
    
    public void markAsReserved() {
        setStatus(BookStatus.RESERVED);
    }
    
    public void markAsLost() {
        setStatus(BookStatus.LOST);
        setAvailable(false);
    }
    
    public void markAsDamaged() {
        setStatus(BookStatus.DAMAGED);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookItem bookItem = (BookItem) o;
        return Objects.equals(barcode, bookItem.barcode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(barcode);
    }
    
    @Override
    public String toString() {
        return "BookItem{" +
                "barcode='" + barcode + '\'' +
                ", book=" + book.getTitle() +
                ", status=" + status +
                ", isAvailable=" + isAvailable +
                ", isReferenceOnly=" + isReferenceOnly +
                '}';
    }
}