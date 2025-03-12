package com.library.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LibraryCollection {
    private List<Book> books;
    private List<BookCategory> categories;
    private Map<BookCategory, List<Book>> categoryBooks;
    
    // konstruktor
    public LibraryCollection() {
        this.books = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.categoryBooks = new HashMap<>();
    }
    
    // getters
    public List<Book> getBooks() {
        return books;
    }
    
    public List<BookCategory> getCategories() {
        return categories;
    }
    
    // metode
    public void addBook(Book book) {
        books.add(book);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
        
        // remove book from all categories
        for (BookCategory category : categories) {
            List<Book> categoryBookList = categoryBooks.get(category);
            if (categoryBookList != null) {
                categoryBookList.remove(book);
            }
        }
    }
    
    public void addCategory(BookCategory category) {
        categories.add(category);
        categoryBooks.put(category, new ArrayList<>());
    }
    
    public void removeCategory(BookCategory category) {
        categories.remove(category);
        categoryBooks.remove(category);
    }
    
    public void addBookToCategory(Book book, BookCategory category) {
        if (!books.contains(book)) {
            books.add(book);
        }
        
        if (!categories.contains(category)) {
            categories.add(category);
            categoryBooks.put(category, new ArrayList<>());
        }
        
        List<Book> categoryBookList = categoryBooks.get(category);
        if (!categoryBookList.contains(book)) {
            categoryBookList.add(book);
            category.addBook(book);
        }
    }
    
    public List<Book> getBooksInCategory(BookCategory category) {
        return categoryBooks.getOrDefault(category, new ArrayList<>());
    }
    
    public List<Book> searchBooks(String query) {
        String queryLower = query.toLowerCase();
        
        return books.stream()
                .filter(book -> 
                        book.getTitle().toLowerCase().contains(queryLower) ||
                        book.getAuthor().toLowerCase().contains(queryLower) ||
                        book.getPublisher().toLowerCase().contains(queryLower) ||
                        (book.getDescription() != null && book.getDescription().toLowerCase().contains(queryLower))
                )
                .collect(Collectors.toList());
    }
    
    public List<Book> getAvailableBooks() {
        return books.stream()
                .filter(book -> !book.getAvailableItems().isEmpty())
                .collect(Collectors.toList());
    }
    
    public int getTotalBooks() {
        return books.size();
    }
    
    public int getTotalCategories() {
        return categories.size();
    }
    
    @Override
    public String toString() {
        return "LibraryCollection{" +
                "books=" + books.size() +
                ", categories=" + categories.size() +
                '}';
    }
}