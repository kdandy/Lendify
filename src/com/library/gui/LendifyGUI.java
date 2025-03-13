package com.library.gui;

import com.library.enums.*;
import com.library.model.*;
import com.library.gui.utils.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Class utama untuk aplikasi GUI Lendify
 */
public class LendifyGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private Library library;
    private Librarian currentLibrarian;
    
    // Map untuk data
    private Map<String, BookCategory> categories;
    private Map<String, Book> books;
    private Map<String, BookItem> bookItems;
    private Map<String, BookLoan> loans;
    private Map<String, Reservation> reservations;
    private java.util.List<Member> members;
    
    // Panel-panel utama
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private LoginPanel loginPanel;
    private MainPanel mainPanel;
    private LibrarianPanel librarianPanel;
    private CategoryPanel categoryPanel;
    private BookPanel bookPanel;
    private MemberPanel memberPanel;
    private LoanPanel loanPanel;
    private ReservationPanel reservationPanel;
    private SearchPanel searchPanel;
    private StatisticsPanel statisticsPanel;
    
    /**
     * Constructor utama untuk GUI Lendify
     */
    public LendifyGUI(Library library, Librarian currentLibrarian) {
        this.library = library;
        this.currentLibrarian = currentLibrarian;
        
        // Inisialisasi data
        this.categories = new HashMap<>();
        this.books = new HashMap<>();
        this.bookItems = new HashMap<>();
        this.loans = new HashMap<>();
        this.reservations = new HashMap<>();
        this.members = new ArrayList<>();
        
        // Tambahkan kategori yang sudah ada ke map
        for (BookCategory category : library.getCollection().getCategories()) {
            categories.put(category.getName(), category);
        }
        
        // Tambahkan buku yang sudah ada ke map
        for (Book book : library.getCollection().getBooks()) {
            books.put(book.getISBN(), book);
            
            // Tambahkan juga book items
            for (BookItem item : book.getItems()) {
                bookItems.put(item.getBarcode(), item);
            }
        }
        
        // Setup GUI
        setupGUI();
    }   

    /** 
     * Setup komponen GUI
     */ 
    private void setupGUI() {
        setTitle("Sistem Manajemen Perpustakaan - LENDIFY (GUI)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        
        // Setup card layout untuk switching panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Inisialisasi panels
        loginPanel = new LoginPanel(this);
        mainPanel = new MainPanel(this);
        librarianPanel = new LibrarianPanel(this);
        categoryPanel = new CategoryPanel(this);
        bookPanel = new BookPanel(this);
        memberPanel = new MemberPanel(this);
        loanPanel = new LoanPanel(this);
        reservationPanel = new ReservationPanel(this);
        searchPanel = new SearchPanel(this);
        statisticsPanel = new StatisticsPanel(this);
        
        // Tambahkan panels ke card layout
        cardPanel.add(loginPanel, "login");
        cardPanel.add(mainPanel, "main");
        cardPanel.add(librarianPanel, "librarian");
        cardPanel.add(categoryPanel, "category");
        cardPanel.add(bookPanel, "book");
        cardPanel.add(memberPanel, "member");
        cardPanel.add(loanPanel, "loan");
        cardPanel.add(reservationPanel, "reservation");
        cardPanel.add(searchPanel, "search");
        cardPanel.add(statisticsPanel, "statistics");
        
        // Default tampilkan panel utama (karena login sudah dilakukan di terminal)
        cardLayout.show(cardPanel, "main");
        
        add(cardPanel);
        
        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Keluar");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        
        JMenu helpMenu = new JMenu("Bantuan");
        JMenuItem aboutItem = new JMenuItem("Tentang");
        aboutItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "LENDIFY - Sistem Manajemen Perpustakaan Pemrograman Berorientasi Objek\nVersi 1.1\n© 2025", 
                "Tentang Aplikasi", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }
    
    // Getters dan Setters
    
    public Library getLibrary() {
        return library;
    }
    
    public Librarian getCurrentLibrarian() {
        return currentLibrarian;
    }
    
    public Map<String, BookCategory> getCategories() {
        return categories;
    }
    
    public Map<String, Book> getBooks() {
        return books;
    }
    
    public Map<String, BookItem> getBookItems() {
        return bookItems;
    }
    
    public Map<String, BookLoan> getLoans() {
        return loans;
    }
    
    public Map<String, Reservation> getReservations() {
        return reservations;
    }
    
    public java.util.List<Member> getMembers() {
        return members;
    }
    
    // Switch panel methods
    
    public void showLoginPanel() {
        cardLayout.show(cardPanel, "login");
    }
    
    public void showMainPanel() {
        cardLayout.show(cardPanel, "main");
    }
    
    public void showLibrarianPanel() {
        librarianPanel.refreshData();
        cardLayout.show(cardPanel, "librarian");
    }
    
    public void showCategoryPanel() {
        categoryPanel.refreshData();
        cardLayout.show(cardPanel, "category");
    }
    
    public void showBookPanel() {
        bookPanel.refreshData();
        cardLayout.show(cardPanel, "book");
    }
    
    public void showMemberPanel() {
        memberPanel.refreshData();
        cardLayout.show(cardPanel, "member");
    }
    
    public void showLoanPanel() {
        loanPanel.refreshData();
        cardLayout.show(cardPanel, "loan");
    }
    
    public void showReservationPanel() {
        reservationPanel.refreshData();
        cardLayout.show(cardPanel, "reservation");
    }
    
    public void showSearchPanel() {
        searchPanel.refreshData();
        cardLayout.show(cardPanel, "search");
    }
    
    public void showStatisticsPanel() {
        statisticsPanel.refreshData();
        cardLayout.show(cardPanel, "statistics");
    }

    // Utility methods untuk operasi data

    public void addCategory(BookCategory category) {
        library.addCategory(category);
        categories.put(category.getName(), category);
    }
    
    public void removeCategory(BookCategory category) {
        library.getCollection().removeCategory(category);
        categories.remove(category.getName());
    }
    
    public void addBook(Book book) {
        library.addBook(book);
        books.put(book.getISBN(), book);
    }
    
    public void removeBook(Book book) {
        library.getCollection().removeBook(book);
        books.remove(book.getISBN());
    }
    
    public void addBookToCategory(Book book, BookCategory category) {
        library.addBookToCategory(book, category);
    }
    
    public void addMember(Member member) {
        members.add(member);
    }
    
    public BookItem addBookItem(Book book, String barcode) throws Exception {
        BookItem item = currentLibrarian.addBookItem(book, barcode);
        bookItems.put(barcode, item);
        return item;
    }
    
    public BookLoan issueBook(Member member, BookItem bookItem) throws Exception {
        BookLoan loan = currentLibrarian.issueBook(member, bookItem);
        loans.put(loan.getLoanId(), loan);
        return loan;
    }
    
    public void returnBook(BookLoan loan) throws Exception {
        currentLibrarian.returnBook(loan);
    }
    
    public void processReservation(Reservation reservation) throws Exception {
        currentLibrarian.processReservation(reservation);
    }
    
    // Main method untuk testing
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Testing only - dalam implementasi sesungguhnya, ini dipanggil dari Main.java
                Person adminPerson = new Person("P001", "Admin", "Alamat Admin", "123456789");
                adminPerson.setEmail("admin@perpustakaan.com");
                Librarian admin = new Librarian(adminPerson, "L001", "Admin Perpustakaan", 0, LibrarianPermission.ADMIN);
                
                Library library = new Library("Perpustakaan Test", "Jl. Test No. 123");
                library.addLibrarian(admin);
                
                LendifyGUI gui = new LendifyGUI(library, admin);
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}