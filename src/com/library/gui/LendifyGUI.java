package com.library.gui;

import com.library.enums.*;
import com.library.model.*;


import javax.swing.*;
import java.awt.*;
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
        setTitle("Sistem Manajemen Perpustakaan - Lendify (Kelompok 2)");
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
        
        // Tambahkan menu Demo Mode
        JMenuItem demoItem = new JMenuItem("Jalankan Demo Mode");
        demoItem.addActionListener(e -> runDemoMode());
        
        helpMenu.add(aboutItem);
        helpMenu.addSeparator();
        helpMenu.add(demoItem); // Tambahkan item menu demo
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Menjalankan mode demo untuk membuat data sampel
     */
    public void runDemoMode() {
        if (JOptionPane.showConfirmDialog(
                this,
                "Mode ini akan membuat data sampel untuk mendemonstrasikan fungsionalitas sistem.\nData yang sudah ada akan dihapus.\nApakah Anda yakin ingin melanjutkan?",
                "Konfirmasi Demo Mode",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return;
        }
        
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            
            // Bersihkan data yang ada
            library.getCollection().getBooks().clear();
            library.getCollection().getCategories().clear();
            members.clear();
            books.clear();
            bookItems.clear();
            loans.clear();
            reservations.clear();
            
            // Buat kategori
            BookCategory fictionCategory = new BookCategory("Fiksi", "Novel, cerita pendek, dan karya fiksi lainnya");
            BookCategory nonFictionCategory = new BookCategory("Non-Fiksi", "Karya faktual, biografi, dan materi pendidikan");
            BookCategory scienceCategory = new BookCategory("Sains", "Buku tentang berbagai disiplin ilmu sains");
            BookCategory technologyCategory = new BookCategory("Teknologi", "Buku tentang komputer, internet, dan teknologi lainnya");
            BookCategory historyCategory = new BookCategory("Sejarah", "Buku-buku tentang sejarah dunia dan lokal");
            BookCategory philosophyCategory = new BookCategory("Filosofi", "Buku-buku tentang pemikiran dan filsafat");
            BookCategory biographyCategory = new BookCategory("Biografi", "Kisah hidup tokoh-tokoh terkenal");
            BookCategory cookingCategory = new BookCategory("Memasak", "Buku resep dan teknik memasak");
            BookCategory artCategory = new BookCategory("Seni", "Buku tentang seni rupa, musik, dan pertunjukan");
            BookCategory travelCategory = new BookCategory("Perjalanan", "Panduan perjalanan dan cerita petualangan");
            
            library.addCategory(fictionCategory);
            library.addCategory(nonFictionCategory);
            library.addCategory(scienceCategory);
            library.addCategory(technologyCategory);
            library.addCategory(historyCategory);
            library.addCategory(philosophyCategory);
            library.addCategory(biographyCategory);
            library.addCategory(cookingCategory);
            library.addCategory(artCategory);
            library.addCategory(travelCategory);
            
            categories.put(fictionCategory.getName(), fictionCategory);
            categories.put(nonFictionCategory.getName(), nonFictionCategory);
            categories.put(scienceCategory.getName(), scienceCategory);
            categories.put(technologyCategory.getName(), technologyCategory);
            categories.put(historyCategory.getName(), historyCategory);
            categories.put(philosophyCategory.getName(), philosophyCategory);
            categories.put(biographyCategory.getName(), biographyCategory);
            categories.put(cookingCategory.getName(), cookingCategory);
            categories.put(artCategory.getName(), artCategory);
            categories.put(travelCategory.getName(), travelCategory);
            
            // buat buku
            // buku Fiksi
            Book book1 = new Book("978-1234567897", "Petualangan Hebat", "Alice Penulis", "Buku Inc.", 2022,
                                    "Sebuah cerita petualangan yang menarik", 320, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book2 = new Book("978-2345678901", "Misteri Pulau Jawa", "Budi Sastro", "Gramedia", 2020,
                                    "Kisah misteri yang terjadi di Pulau Jawa", 275, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book3 = new Book("978-3456789012", "Cinta di Kala Senja", "Citra Dewi", "Mizan", 2021,
                                    "Roman percintaan dengan latar belakang Indonesia", 310, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book4 = new Book("978-4567890123", "Detektif Cilik", "Doni Kusuma", "Erlangga", 2019,
                                    "Kisah anak-anak yang memecahkan misteri kriminal", 220, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            // buku Non-Fiksi & Sains
            Book book5 = new Book("978-9876543210", "Sejarah Sains", "Bob Sejarawan", "Penerbit Akademik", 2021,
                                    "Sejarah komprehensif tentang penemuan ilmiah", 450, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            Book book6 = new Book("978-8765432109", "Fisika Dasar", "Eko Fisikawan", "UI Press", 2018,
                                    "Pengantar konsep dasar fisika", 400, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            Book book7 = new Book("978-7654321098", "Tubuh Manusia", "Fany Dokter", "FK Press", 2022,
                                    "Eksplorasi anatomi tubuh manusia", 380, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            // buku Teknologi
            Book book8 = new Book("978-5678901234", "Dasar-dasar Pemrograman", "Charlie Koder", "Buku Teknologi", 2023,
                                    "Pengantar konsep pemrograman", 280, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book9 = new Book("978-6789012345", "Jaringan Komputer", "Gani Network", "InfoKomputer", 2020,
                                    "Panduan memahami jaringan komputer", 340, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book10 = new Book("978-7890123456", "Kecerdasan Buatan", "Hana AI", "Informatika", 2023,
                                    "Pengenalan machine learning dan AI", 420, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            // buku Sejarah
            Book book11 = new Book("978-8901234567", "Sejarah Indonesia", "Indra Historian", "Balai Pustaka", 2021,
                                    "Perjalanan sejarah Indonesia dari masa ke masa", 520, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            Book book12 = new Book("978-9012345678", "Perang Dunia II", "Joko Military", "Kompas", 2019,
                                    "Analisis komprehensif Perang Dunia II", 480, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            // buku Filosofi
            Book book13 = new Book("978-0123456789", "Filsafat Hidup", "Kartini Filosof", "Bentang Pustaka", 2020,
                                    "Pandangan filosofis tentang kehidupan", 290, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            // buku Biografi
            Book book14 = new Book("978-1234509876", "Biografi Soekarno", "Lina Biographer", "Gramedia", 2018,
                                    "Kisah hidup sang proklamator", 500, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            // buku Memasak
            Book book15 = new Book("978-2345609876", "Masakan Nusantara", "Mira Chef", "Koki Media", 2022,
                                    "Kumpulan resep masakan dari seluruh Indonesia", 350, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            // buku Seni
            Book book16 = new Book("978-3456709876", "Seni Lukis Modern", "Nina Artist", "Galeri Buku", 2020,
                                    "Perkembangan seni lukis dari era modern", 320, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            // buku Perjalanan
            Book book17 = new Book("978-4567809876", "Jelajah Indonesia Timur", "Oki Traveler", "Travel Books", 2021,
                                    "Panduan perjalanan ke Indonesia bagian timur", 310, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book18 = new Book("978-5678909876", "Backpacking Eropa", "Putra Wanderer", "Compass", 2019,
                                    "Tips berhemat saat traveling ke Eropa", 260, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            // buku Bahasa Asing
            Book book19 = new Book("978-6789009876", "Professional Software Development", "Quin Coder", "O'Reilly", 2021,
                                    "Best practices in software engineering", 400, BookFormat.PAPERBACK, Language.ENGLISH);
            
            Book book20 = new Book("978-7890109876", "L'art de la Cuisine", "Remy Chef", "Français Livres", 2020,
                                    "The art of French cooking", 320, BookFormat.HARDCOVER, Language.FRENCH);
            
            // tambahkan semua buku ke perpustakaan
            java.util.List<Book> allBooks = java.util.List.of(book1, book2, book3, book4, book5, book6, book7, book8, book9, book10,
                                        book11, book12, book13, book14, book15, book16, book17, book18, book19, book20);
            
            for (Book book : allBooks) {
                addBook(book);
            }
            
            // kategorikan buku
            addBookToCategory(book1, fictionCategory);
            addBookToCategory(book2, fictionCategory);
            addBookToCategory(book3, fictionCategory);
            addBookToCategory(book4, fictionCategory);
            
            addBookToCategory(book5, nonFictionCategory);
            addBookToCategory(book5, scienceCategory);
            addBookToCategory(book6, scienceCategory);
            addBookToCategory(book7, scienceCategory);
            addBookToCategory(book7, nonFictionCategory);
            
            addBookToCategory(book8, technologyCategory);
            addBookToCategory(book8, nonFictionCategory);
            addBookToCategory(book9, technologyCategory);
            addBookToCategory(book10, technologyCategory);
            addBookToCategory(book10, scienceCategory);
            
            addBookToCategory(book11, historyCategory);
            addBookToCategory(book11, nonFictionCategory);
            addBookToCategory(book12, historyCategory);
            
            addBookToCategory(book13, philosophyCategory);
            addBookToCategory(book13, nonFictionCategory);
            
            addBookToCategory(book14, biographyCategory);
            addBookToCategory(book14, nonFictionCategory);
            addBookToCategory(book14, historyCategory);
            
            addBookToCategory(book15, cookingCategory);
            addBookToCategory(book15, nonFictionCategory);
            
            addBookToCategory(book16, artCategory);
            addBookToCategory(book16, nonFictionCategory);
            
            addBookToCategory(book17, travelCategory);
            addBookToCategory(book17, nonFictionCategory);
            addBookToCategory(book18, travelCategory);
            
            addBookToCategory(book19, technologyCategory);
            addBookToCategory(book20, cookingCategory);
            
            // tambahkan salinan buku (minimal 2 salinan per buku)
            java.util.Map<Book, java.util.List<BookItem>> bookCopies = new java.util.HashMap<>();
            
            for (Book book : allBooks) {
                java.util.List<BookItem> copies = new java.util.ArrayList<>();
                String isbnShort = book.getISBN().substring(4, 9);
                
                // buat 2-4 salinan per buku
                int numCopies = 2 + (int)(Math.random() * 3); // 2 sampai 4 salinan
                
                for (int i = 1; i <= numCopies; i++) {
                    try {
                        String barcode = isbnShort + "-" + String.format("%03d", i);
                        BookItem copy = addBookItem(book, barcode);
                        copies.add(copy);
                        
                        // beberapa buku (10%) ditandai sebagai referensi saja
                        if (i == numCopies && Math.random() < 0.1) {
                            copy.setReferenceOnly(true);
                        }
                        
                        // tetapkan lokasi dan harga
                        copy.setLocation("Rak " + (char)('A' + Math.random() * 6) + "-" + (int)(Math.random() * 100 + 1));
                        copy.setPrice(50000 + Math.random() * 150000);
                    } catch (Exception e) {
                        System.err.println("Error adding book item: " + e.getMessage());
                    }
                }
                
                bookCopies.put(book, copies);
            }
            
            // buat anggota
            java.util.List<Person> personList = new java.util.ArrayList<>();
            java.util.List<Member> memberList = new java.util.ArrayList<>();
            
            // anggota reguler
            Person person1 = new Person("P001", "Dandy", "Jl. Timoho Timur 1A", "082125555645");
            person1.setEmail("dandy@students.undip.ac.id");
            personList.add(person1);

            Person person2 = new Person("P002", "Gaza", "Jl. Mawar No. 25", "082345678901");
            person2.setEmail("gaza@students.undip.ac.id");
            personList.add(person2);

            Person person3 = new Person("P003", "Vava", "Jl. Melati No. 42", "083456789012");
            person3.setEmail("vava@students.undip.ac.id");
            personList.add(person3);

            Person person4 = new Person("P004", "Fauzan", "Jl. Anggrek No. 7", "084567890123");
            person4.setEmail("fauzan@students.undip.ac.id");
            personList.add(person4);

            Person person5 = new Person("P005", "Ganen", "Jl. Kenanga No. 15", "085678901234");
            person5.setEmail("ganen@students.undip.ac.id");
            personList.add(person5);

            // anggota mahasiswa
            Person person6 = new Person("P006", "Fani Wijaya", "Jl. Kampus No. 3", "086789012345");
            person6.setEmail("fani.wijaya@students.undip.ac.id");
            personList.add(person6);

            Person person7 = new Person("P007", "Gunawan Hidayat", "Jl. Pendidikan No. 8", "087890123456");
            person7.setEmail("gunawan.hidayat@students.undip.ac.id");
            personList.add(person7);

            Person person8 = new Person("P008", "Hani Susanti", "Jl. Mahasiswa No. 12", "088901234567");
            person8.setEmail("hani.susanti@students.undip.ac.id");
            personList.add(person8);

            Person person9 = new Person("P009", "Indra Kusuma", "Jl. Ilmu No. 21", "089012345678");
            person9.setEmail("indra.kusuma@students.undip.ac.id");
            personList.add(person9);

            Person person10 = new Person("P010", "Joko Widodo", "Jl. Cendekia No. 17", "089123456789");
            person10.setEmail("joko.widodo@students.undip.ac.id");
            personList.add(person10);
            
            // Buat anggota dari person
            try {
                for (int i = 0; i < personList.size(); i++) {
                    Person person = personList.get(i);
                    Member member = currentLibrarian.addMember(person);
                    
                    if (i < 5) { // 5 anggota reguler
                        boolean isPremium = i % 2 == 0; // Setiap anggota genap adalah premium
                        RegularMember regularMember = new RegularMember(
                            member, 
                            "Profesional", 
                            "PT. Maju Bersama", 
                            isPremium
                        );
                        memberList.add(regularMember);
                        members.add(regularMember);
                    } else { // 5 anggota mahasiswa
                        StudentMember studentMember = new StudentMember(
                            member,
                            "S" + (1000 + i * 111),
                            "Teknik",
                            new String[]{"Informatika", "Elektro", "Industri", "Mesin", "Sipil"}[i - 5],
                            (int)(Math.random() * 4) + 1 // Tahun 1-4
                        );
                        memberList.add(studentMember);
                        members.add(studentMember);
                    }
                }
                
                // Buat peminjaman (setiap anggota meminjam 1-3 buku)
                java.util.List<BookLoan> loanList = new java.util.ArrayList<>();
                
                for (Member member : memberList) {
                    int numLoans = (int)(Math.random() * 3) + 1; // 1-3 peminjaman
                    
                    for (int i = 0; i < numLoans && i < allBooks.size(); i++) {
                        Book book = allBooks.get((int)(Math.random() * allBooks.size()));
                        java.util.List<BookItem> copies = bookCopies.get(book);
                        
                        if (copies != null && !copies.isEmpty()) {
                            // Cari salinan yang tersedia dan bisa dipinjam
                            BookItem availableCopy = null;
                            for (BookItem copy : copies) {
                                if (copy.isAvailable() && !copy.isReferenceOnly()) {
                                    availableCopy = copy;
                                    break;
                                }
                            }
                            
                            if (availableCopy != null) {
                                try {
                                    BookLoan loan = issueBook(member, availableCopy);
                                    loanList.add(loan);
                                    
                                    // 10% peminjaman sudah jatuh tempo (overdue)
                                    if (Math.random() < 0.1) {
                                        // Gunakan reflection untuk memodifikasi tanggal peminjaman menjadi lebih lama
                                        Calendar pastCalendar = Calendar.getInstance();
                                        pastCalendar.add(Calendar.DAY_OF_MONTH, -45); // 45 hari yang lalu
                                        
                                        try {
                                            java.lang.reflect.Field issueDateField = BookLoan.class.getDeclaredField("issueDate");
                                            issueDateField.setAccessible(true);
                                            issueDateField.set(loan, pastCalendar.getTime());
                                            
                                            java.lang.reflect.Field dueDateField = BookLoan.class.getDeclaredField("dueDate");
                                            dueDateField.setAccessible(true);
                                            
                                            Calendar dueDateCalendar = Calendar.getInstance();
                                            dueDateCalendar.setTime(pastCalendar.getTime());
                                            dueDateCalendar.add(Calendar.DAY_OF_MONTH, member.getMaxLoanDays());
                                            dueDateField.set(loan, dueDateCalendar.getTime());
                                        } catch (Exception e) {
                                            System.err.println("Tidak dapat memodifikasi tanggal: " + e.getMessage());
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Gagal membuat peminjaman: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
                
                // Buat beberapa peminjaman sudah dikembalikan
                for (int i = 0; i < 5 && i < loanList.size(); i++) {
                    try {
                        BookLoan loan = loanList.get(i);
                        returnBook(loan);
                        
                        // 50% kemungkinan ada denda
                        if (Math.random() < 0.5) {
                            double fine = 5000 + (Math.random() * 20000);
                            
                            // Set fine melalui reflection karena setFine() mungkin tidak langsung tersedia
                            try {
                                java.lang.reflect.Field fineField = BookLoan.class.getDeclaredField("fine");
                                fineField.setAccessible(true);
                                fineField.set(loan, fine);
                                loan.getMember().payFine(fine);
                            } catch (Exception e) {
                                System.err.println("Tidak dapat mengatur denda: " + e.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Gagal mengembalikan buku: " + e.getMessage());
                    }
                }
                
                // Buat reservasi (20% anggota melakukan reservasi)
                for (int i = 0; i < memberList.size(); i++) {
                    if (Math.random() < 0.2) { // 20% probabilitas
                        Member member = memberList.get(i);
                        
                        // Pilih buku secara acak yang semua salinannya sedang dipinjam
                        for (Book book : allBooks) {
                            if (book.getAvailableItems().isEmpty() && !book.getItems().isEmpty()) {
                                try {
                                    Reservation reservation = member.reserveBook(book);
                                    reservations.put(reservation.getReservationId(), reservation);
                                    break; // Satu anggota hanya membuat satu reservasi
                                } catch (Exception e) {
                                    System.err.println("Gagal membuat reservasi: " + e.getMessage());
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error creating members: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Refresh data di semua panel untuk menampilkan data baru
            showMainPanel();
            
            JOptionPane.showMessageDialog(
                this,
                "Demo mode berhasil dijalankan. Data sampel telah dibuat:\n" +
                "- " + books.size() + " buku dengan " + bookItems.size() + " salinan\n" +
                "- " + categories.size() + " kategori\n" +
                "- " + members.size() + " anggota\n" +
                "- " + loans.size() + " peminjaman\n" +
                "- " + reservations.size() + " reservasi\n\n" +
                "Silakan jelajahi data melalui menu yang tersedia.",
                "Demo Mode Berhasil",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                this,
                "Terjadi kesalahan saat menjalankan demo: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            e.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
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
                Person adminPerson = new Person("P001", "Admin", "Alamat Admin", "082125555645");
                adminPerson.setEmail("hello@dandy.my.id");
                Librarian admin = new Librarian(adminPerson, "L001", "Admin Perpustakaan", 0, LibrarianPermission.ADMIN);
                
                Library library = new Library("Perpustakaan Admin", "Jl. Timoho Timur");
                library.addLibrarian(admin);
                
                LendifyGUI gui = new LendifyGUI(library, admin);
                gui.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}