package com.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Library {
    private String name;
    private String address;
    private LibraryCollection collection;
    private List<Librarian> librarians;
    
    // Constructor
    public Library(String name, String address) {
        this.name = name;
        this.address = address;
        this.collection = new LibraryCollection();
        this.librarians = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public LibraryCollection getCollection() {
        return collection;
    }
    
    // Methods
    public void addLibrarian(Librarian librarian) {
        librarians.add(librarian);
    }
    
    public void removeLibrarian(Librarian librarian) {
        librarians.remove(librarian);
    }
    
    public List<Librarian> getLibrarians() {
        return librarians;
    }
    
    public List<Book> searchByTitle(String title) {
        String titleLower = title.toLowerCase();
        return collection.getBooks().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(titleLower))
                .collect(Collectors.toList());
    }
    
    public List<Book> searchByAuthor(String author) {
        String authorLower = author.toLowerCase();
        return collection.getBooks().stream()
                .filter(book -> book.getAuthor().toLowerCase().contains(authorLower))
                .collect(Collectors.toList());
    }
    
    public List<Book> searchByCategory(BookCategory category) {
        return collection.getBooksInCategory(category);
    }
    
    public void addBook(Book book) {
        collection.addBook(book);
    }
    
    public void addBookToCategory(Book book, BookCategory category) {
        collection.addBookToCategory(book, category);
    }
    
    public void addCategory(BookCategory category) {
        collection.addCategory(category);
    }
    
    @Override
    public String toString() {
        return "Library{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", books=" + collection.getTotalBooks() +
                ", categories=" + collection.getTotalCategories() +
                ", librarians=" + librarians.size() +
                '}';
    }
}