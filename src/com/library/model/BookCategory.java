package com.library.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookCategory {
    private String name;
    private String description;
    private List<Book> books;
    
    // konstruktor
    public BookCategory(String name, String description) {
        this.name = name;
        this.description = description;
        this.books = new ArrayList<>();
    }
    
    // getters and setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<Book> getBooks() {
        return books;
    }
    
    // mutator
    public void addBook(Book book) {
        books.add(book);
    }
    
    public void removeBook(Book book) {
        books.remove(book);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCategory that = (BookCategory) o;
        return Objects.equals(name, that.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return "BookCategory{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", booksCount=" + books.size() +
                '}';
    }
}