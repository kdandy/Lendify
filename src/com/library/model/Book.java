package com.library.model;

import com.library.enums.BookFormat;
import com.library.enums.Language;
import com.library.enums.ReservationStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private int publicationYear;
    private String description;
    private int numberOfPages;
    private List<BookItem> items;
    private BookFormat format;
    private Language language;
    private List<Reservation> reservations;
    private List<BookCategory> categories; // Tambahkan field categories
    
    // konsruktor
    public Book() {
        this.items = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.categories = new ArrayList<>(); // Inisialisasi categories
    }
    
    public Book(String isbn, String title, String author, String publisher, int year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = year;
        this.items = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.categories = new ArrayList<>(); // Inisialisasi categories
    }
    
    public Book(String isbn, String title, String author, String publisher, int year, 
                String description, int pages, BookFormat format, Language language) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = year;
        this.description = description;
        this.numberOfPages = pages;
        this.format = format;
        this.language = language;
        this.items = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.categories = new ArrayList<>(); // Inisialisasi categories
    }
    
    // getters and setters
    public String getISBN() {
        return isbn;
    }
    
    public void setISBN(String isbn) {
        this.isbn = isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    public int getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(int year) {
        this.publicationYear = year;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getNumberOfPages() {
        return numberOfPages;
    }
    
    public void setNumberOfPages(int pages) {
        this.numberOfPages = pages;
    }
    
    public List<BookItem> getItems() {
        return items;
    }
    
    public BookFormat getFormat() {
        return format;
    }
    
    public void setFormat(BookFormat format) {
        this.format = format;
    }
    
    public Language getLanguage() {
        return language;
    }
    
    public void setLanguage(Language language) {
        this.language = language;
    }
    
    public List<Reservation> getReservations() {
        return reservations;
    }
    
    /**
     * Mendapatkan daftar kategori buku
     * @return Daftar kategori
     */
    public List<BookCategory> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return categories;
    }
    
    /**
     * Menambahkan buku ke kategori
     * @param category kategori yang akan ditambahkan
     */
    public void addCategory(BookCategory category) {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        if (!categories.contains(category)) {
            categories.add(category);
            // Pastikan buku juga ditambahkan ke daftar buku dalam kategori (relasi bidirectional)
            if (!category.getBooks().contains(this)) {
                category.addBook(this);
            }
        }
    }
    
    /**
     * Menghapus buku dari kategori
     * @param category kategori yang akan dihapus
     */
    public void removeCategory(BookCategory category) {
        if (categories != null) {
            categories.remove(category);
            // Pastikan buku juga dihapus dari daftar buku dalam kategori (relasi bidirectional)
            if (category.getBooks().contains(this)) {
                category.removeBook(this);
            }
        }
    }
    
    // metode
    public void addBookItem(BookItem item) {
        items.add(item);
    }
    
    public void removeBookItem(BookItem item) {
        items.remove(item);
    }
    
    public List<BookItem> getAvailableItems() {
        return items.stream()
                .filter(item -> item.isAvailable() && item.isActive())
                .collect(Collectors.toList());
    }
    
    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
    }
    
    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
    }
    
    public List<Reservation> getPendingReservations() {
        return reservations.stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publicationYear=" + publicationYear +
                ", format=" + format +
                ", language=" + language +
                ", total copies=" + items.size() +
                ", available copies=" + getAvailableItems().size() +
                '}';
    }
}