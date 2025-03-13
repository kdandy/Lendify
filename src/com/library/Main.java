package com.library;

import com.library.enums.*;
import com.library.model.*;
import com.library.gui.LendifyGUI;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit; // sistem login
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    // objek global
    private static Library library;
    private static Librarian currentLibrarian;
    private static List<Member> members = new ArrayList<>();
    private static Map<String, BookCategory> categories = new HashMap<>();
    private static Map<String, Book> books = new HashMap<>();
    private static Map<String, BookItem> bookItems = new HashMap<>();
    private static Map<String, BookLoan> loans = new HashMap<>();
    private static Map<String, Reservation> reservations = new HashMap<>();
    
    // akses sistem
    private static final String SYSTEM_PASSWORD = "lendify";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    // akses sistem    
    public static void main(String[] args) {
        System.out.println("=============================================");
        System.out.println("  SISTEM MANAJEMEN PERPUSTAKAAN - LENDIFY");
        System.out.println("=============================================");
        
        // sistem login
        if (!login()) {
            System.out.println("Terlalu banyak percobaan gagal. Program berakhir.");
            System.exit(0);
        }
        
        // animasi loading sederhana
        System.out.print("Memuat sistem");
        for (int i = 0; i < 5; i++) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                System.out.print(".");
            } catch (InterruptedException e) {
            }
        }
        System.out.println("\nSistem siap digunakan!");
        // sistem login end
        initializeLibrary();
        
        // Pilihan mode GUI atau Terminal
        System.out.println("\n==== PILIH MODE APLIKASI ====");
        System.out.println("1. Mode Terminal");
        System.out.println("2. Mode GUI");
        int modeChoice = getIntInput("Pilih mode: ");
        
        if (modeChoice == 2) {
            // Jalankan versi GUI
            System.out.println("Memulai aplikasi dalam mode GUI...");
            launchGUI();
        } else {
            // Jalankan versi Terminal
            System.out.println("Memulai aplikasi dalam mode Terminal...");
            runTerminalMode();
        }
    }
    
    /**
     * menjalankan aplikasi dalam mode GUI
     */
    private static void launchGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // inisialisasi dan jalankan GUI
            SwingUtilities.invokeLater(() -> {
                try {
                    LendifyGUI gui = new LendifyGUI(library, currentLibrarian);
                    
                    // transfer data dari objek global ke GUI
                    for (BookCategory category : categories.values()) {
                        gui.getCategories().put(category.getName(), category);
                    }
                    
                    for (Book book : books.values()) {
                        gui.getBooks().put(book.getISBN(), book);
                    }
                    
                    for (BookItem item : bookItems.values()) {
                        gui.getBookItems().put(item.getBarcode(), item);
                    }
                    
                    for (BookLoan loan : loans.values()) {
                        gui.getLoans().put(loan.getLoanId(), loan);
                    }
                    
                    for (Reservation reservation : reservations.values()) {
                        gui.getReservations().put(reservation.getReservationId(), reservation);
                    }
                    
                    gui.getMembers().addAll(members);
                    
                    // refresh data pada semua panel
                    gui.showMainPanel();
                    gui.setVisible(true);
                } catch (Exception e) {
                    System.err.println("Error saat menjalankan GUI: " + e.getMessage());
                    e.printStackTrace();
                    System.out.println("\nMemulai mode Terminal sebagai fallback...");
                    runTerminalMode();
                }
            });
        } catch (Exception e) {
            System.err.println("Error saat inisialisasi GUI: " + e.getMessage());
            System.out.println("\nMemulai mode Terminal sebagai fallback...");
            runTerminalMode();
        }
    }
    
    /**
     * menjalankan aplikasi dalam mode Terminal
     */
    private static void runTerminalMode() {
        boolean exit = false;
        while (!exit) {
            displayMainMenu();
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    manageLibrarians();
                    break;
                case 2:
                    manageCategories();
                    break;
                case 3:
                    manageBooks();
                    break;
                case 4:
                    manageMembers();
                    break;
                case 5:
                    manageLending();
                    break;
                case 6:
                    manageReservations();
                    break;
                case 7:
                    searchBooks();
                    break;
                case 8:
                    displayStatistics();
                    break;
                case 9:
                    runDemoMode();
                    break;
                case 0:
                    exit = true;
                    System.out.println("Terima kasih telah menggunakan Sistem Manajemen Perpustakaan Lendify!");
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * sistem login
     * @return true jika login berhasil, false jika gagal setelah beberapa percobaan
     */
    private static boolean login() {
        int attempts = 0;
        boolean loginSuccess = false;
        
        while (attempts < MAX_LOGIN_ATTEMPTS && !loginSuccess) {
            System.out.print("Masukkan password: ");
            String password = scanner.nextLine().trim();
            
            if (password.equals(SYSTEM_PASSWORD)) {
                System.out.println("Login berhasil! Selamat datang di Sistem Manajemen Perpustakaan Lendify.");
                loginSuccess = true;
            } else {
                attempts++;
                int remainingAttempts = MAX_LOGIN_ATTEMPTS - attempts;
                
                if (remainingAttempts > 0) {
                    System.out.println("Password salah. Sisa percobaan: " + remainingAttempts);
                } else {
                    System.out.println("Password salah.");
                }
            }
        }
        
        return loginSuccess;
    }
    
    // SISTEM LOGIN END
    
    private static void displayMainMenu() {
        System.out.println("\n==== MENU UTAMA ====");
        System.out.println("1. Kelola Pustakawan");
        System.out.println("2. Kelola Kategori Buku");
        System.out.println("3. Kelola Buku");
        System.out.println("4. Kelola Anggota");
        System.out.println("5. Kelola Peminjaman dan Pengembalian");
        System.out.println("6. Kelola Reservasi");
        System.out.println("7. Cari Buku");
        System.out.println("8. Lihat Statistik Perpustakaan");
        System.out.println("9. Jalankan Demo Mode");
        System.out.println("0. Keluar");
    }
    
    private static void initializeLibrary() {
        // membuat perpustakaan
        System.out.print("Masukkan nama perpustakaan: ");
        String name = scanner.nextLine();
        System.out.print("Masukkan alamat perpustakaan: ");
        String address = scanner.nextLine();
        
        library = new Library(name, address);
        System.out.println("Perpustakaan berhasil dibuat: " + library.getName());
        
        // membuat pustakawan admin default
        try {
            Person adminPerson = new Person("P001", "Admin", "Alamat Admin", "082125555645");
            adminPerson.setEmail("admin@lendify.com");
            currentLibrarian = new Librarian(adminPerson, "L001", "Admin Perpustakaan", 0, LibrarianPermission.ADMIN);
            library.addLibrarian(currentLibrarian);
            System.out.println("Pustakawan admin default dibuat.");
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat membuat pustakawan admin: " + e.getMessage());
        }
    }
    
    private static void manageLibrarians() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n==== KELOLA PUSTAKAWAN ====");
            System.out.println("1. Lihat Daftar Pustakawan");
            System.out.println("2. Tambah Pustakawan Baru");
            System.out.println("3. Ubah Informasi Pustakawan");
            System.out.println("4. Hapus Pustakawan");
            System.out.println("0. Kembali ke Menu Utama");
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    displayLibrarians();
                    break;
                case 2:
                    addLibrarian();
                    break;
                case 3:
                    updateLibrarian();
                    break;
                case 4:
                    removeLibrarian();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
    
    private static void displayLibrarians() {
        List<Librarian> librarians = library.getLibrarians();
        if (librarians.isEmpty()) {
            System.out.println("Tidak ada pustakawan yang terdaftar.");
        } else {
            System.out.println("\nDaftar Pustakawan:");
            for (int i = 0; i < librarians.size(); i++) {
                Librarian librarian = librarians.get(i);
                System.out.printf("%d. %s (%s) - %s%n", 
                        i + 1, 
                        librarian.getName(), 
                        librarian.getStaffId(), 
                        librarian.getPosition());
            }
        }
    }
    
    private static void addLibrarian() {
        if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
            System.out.println("Anda tidak memiliki hak akses untuk menambah pustakawan.");
            return;
        }
        
        System.out.println("\nTambah Pustakawan Baru:");
        
        try {
            System.out.print("ID Person: ");
            String id = scanner.nextLine();
            System.out.print("Nama: ");
            String name = scanner.nextLine();
            System.out.print("Alamat: ");
            String address = scanner.nextLine();
            System.out.print("Nomor Telepon: ");
            String phone = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            
            Person person = new Person(id, name, address, phone);
            person.setEmail(email);
            
            System.out.print("ID Staff: ");
            String staffId = scanner.nextLine();
            System.out.print("Posisi: ");
            String position = scanner.nextLine();
            System.out.print("Gaji: ");
            double salary = getDoubleInput("");
            
            System.out.println("Level Hak Akses:");
            System.out.println("1. BASIC");
            System.out.println("2. FULL");
            System.out.println("3. ADMIN");
            int permissionChoice = getIntInput("Pilih hak akses: ");
            
            LibrarianPermission permission;
            switch (permissionChoice) {
                case 1:
                    permission = LibrarianPermission.BASIC;
                    break;
                case 2:
                    permission = LibrarianPermission.FULL;
                    break;
                case 3:
                    permission = LibrarianPermission.ADMIN;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Menggunakan BASIC sebagai default.");
                    permission = LibrarianPermission.BASIC;
            }
            
            Librarian librarian = new Librarian(person, staffId, position, salary, permission);
            library.addLibrarian(librarian);
            
            System.out.println("Pustakawan berhasil ditambahkan: " + librarian.getName());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void updateLibrarian() {
        if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
            System.out.println("Anda tidak memiliki hak akses untuk mengubah pustakawan.");
            return;
        }
        
        displayLibrarians();
        
        List<Librarian> librarians = library.getLibrarians();
        if (librarians.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor pustakawan yang akan diubah (0 untuk batal): ");
        if (index < 1 || index > librarians.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Librarian librarian = librarians.get(index - 1);
        
        System.out.println("\nUbah Informasi Pustakawan:");
        System.out.println("1. Ubah Nama");
        System.out.println("2. Ubah Alamat");
        System.out.println("3. Ubah Nomor Telepon");
        System.out.println("4. Ubah Email");
        System.out.println("5. Ubah Posisi");
        System.out.println("6. Ubah Gaji");
        System.out.println("7. Ubah Hak Akses");
        System.out.println("0. Batal");
        
        int choice = getIntInput("Pilih informasi yang akan diubah: ");
        
        try {
            switch (choice) {
                case 1:
                    System.out.print("Nama baru: ");
                    String name = scanner.nextLine();
                    librarian.setName(name);
                    break;
                case 2:
                    System.out.print("Alamat baru: ");
                    String address = scanner.nextLine();
                    librarian.setAddress(address);
                    break;
                case 3:
                    System.out.print("Nomor telepon baru: ");
                    String phone = scanner.nextLine();
                    librarian.setPhoneNumber(phone);
                    break;
                case 4:
                    System.out.print("Email baru: ");
                    String email = scanner.nextLine();
                    librarian.setEmail(email);
                    break;
                case 5:
                    System.out.print("Posisi baru: ");
                    String position = scanner.nextLine();
                    librarian.setPosition(position);
                    break;
                case 6:
                    System.out.print("Gaji baru: ");
                    double salary = getDoubleInput("");
                    librarian.setSalary(salary);
                    break;
                case 7:
                    System.out.println("Level Hak Akses baru:");
                    System.out.println("1. BASIC");
                    System.out.println("2. FULL");
                    System.out.println("3. ADMIN");
                    int permissionChoice = getIntInput("Pilih hak akses: ");
                    
                    LibrarianPermission permission;
                    switch (permissionChoice) {
                        case 1:
                            permission = LibrarianPermission.BASIC;
                            break;
                        case 2:
                            permission = LibrarianPermission.FULL;
                            break;
                        case 3:
                            permission = LibrarianPermission.ADMIN;
                            break;
                        default:
                            System.out.println("Pilihan tidak valid. Tidak ada perubahan.");
                            return;
                    }
                    
                    librarian.setPermission(permission);
                    break;
                case 0:
                    System.out.println("Perubahan dibatalkan.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
                    return;
            }
            
            System.out.println("Informasi pustakawan berhasil diubah.");
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void removeLibrarian() {
        if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
            System.out.println("Anda tidak memiliki hak akses untuk menghapus pustakawan.");
            return;
        }
        
        displayLibrarians();
        
        List<Librarian> librarians = library.getLibrarians();
        if (librarians.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor pustakawan yang akan dihapus (0 untuk batal): ");
        if (index < 1 || index > librarians.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Librarian librarian = librarians.get(index - 1);
        
        // mencegah penghapusan diri sendiri
        if (librarian.equals(currentLibrarian)) {
            System.out.println("Anda tidak dapat menghapus akun pustakawan yang sedang digunakan.");
            return;
        }
        
        System.out.print("Apakah Anda yakin ingin menghapus pustakawan " + librarian.getName() + "? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            library.removeLibrarian(librarian);
            System.out.println("Pustakawan berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
    
    private static void manageCategories() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n==== KELOLA KATEGORI BUKU ====");
            System.out.println("1. Lihat Daftar Kategori");
            System.out.println("2. Tambah Kategori Baru");
            System.out.println("3. Ubah Informasi Kategori");
            System.out.println("4. Hapus Kategori");
            System.out.println("0. Kembali ke Menu Utama");
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    displayCategories();
                    break;
                case 2:
                    addCategory();
                    break;
                case 3:
                    updateCategory();
                    break;
                case 4:
                    removeCategory();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
    
    private static void displayCategories() {
        List<BookCategory> categoryList = library.getCollection().getCategories();
        if (categoryList.isEmpty()) {
            System.out.println("Tidak ada kategori yang terdaftar.");
        } else {
            System.out.println("\nDaftar Kategori:");
            for (int i = 0; i < categoryList.size(); i++) {
                BookCategory category = categoryList.get(i);
                System.out.printf("%d. %s - %s%n", 
                        i + 1, 
                        category.getName(), 
                        category.getDescription());
            }
        }
    }
    
    private static void addCategory() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk menambah kategori.");
            return;
        }
        
        System.out.println("\nTambah Kategori Baru:");
        
        try {
            System.out.print("Nama Kategori: ");
            String name = scanner.nextLine();
            System.out.print("Deskripsi: ");
            String description = scanner.nextLine();
            
            BookCategory category = new BookCategory(name, description);
            library.addCategory(category);
            categories.put(name, category);
            
            System.out.println("Kategori berhasil ditambahkan: " + category.getName());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void updateCategory() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk mengubah kategori.");
            return;
        }
        
        displayCategories();
        
        List<BookCategory> categoryList = library.getCollection().getCategories();
        if (categoryList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor kategori yang akan diubah (0 untuk batal): ");
        if (index < 1 || index > categoryList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        BookCategory category = categoryList.get(index - 1);
        
        System.out.println("\nUbah Informasi Kategori:");
        System.out.println("1. Ubah Nama");
        System.out.println("2. Ubah Deskripsi");
        System.out.println("0. Batal");
        
        int choice = getIntInput("Pilih informasi yang akan diubah: ");
        
        try {
            switch (choice) {
                case 1:
                    System.out.print("Nama baru: ");
                    String name = scanner.nextLine();
                    categories.remove(category.getName());
                    category.setName(name);
                    categories.put(name, category);
                    break;
                case 2:
                    System.out.print("Deskripsi baru: ");
                    String description = scanner.nextLine();
                    category.setDescription(description);
                    break;
                case 0:
                    System.out.println("Perubahan dibatalkan.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
                    return;
            }
            
            System.out.println("Informasi kategori berhasil diubah.");
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void removeCategory() {
        if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
            System.out.println("Anda tidak memiliki hak akses untuk menghapus kategori.");
            return;
        }
        
        displayCategories();
        
        List<BookCategory> categoryList = library.getCollection().getCategories();
        if (categoryList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor kategori yang akan dihapus (0 untuk batal): ");
        if (index < 1 || index > categoryList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        BookCategory category = categoryList.get(index - 1);
        
        System.out.print("Apakah Anda yakin ingin menghapus kategori " + category.getName() + "? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            library.getCollection().removeCategory(category);
            categories.remove(category.getName());
            System.out.println("Kategori berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
    
    private static void manageBooks() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n==== KELOLA BUKU ====");
            System.out.println("1. Lihat Daftar Buku");
            System.out.println("2. Tambah Buku Baru");
            System.out.println("3. Tambah Salinan Buku");
            System.out.println("4. Ubah Informasi Buku");
            System.out.println("5. Lihat Detail Buku");
            System.out.println("6. Hapus Buku");
            System.out.println("0. Kembali ke Menu Utama");
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    displayBooks();
                    break;
                case 2:
                    addBook();
                    break;
                case 3:
                    addBookItem();
                    break;
                case 4:
                    updateBook();
                    break;
                case 5:
                    viewBookDetails();
                    break;
                case 6:
                    removeBook();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
    
    private static void displayBooks() {
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            System.out.println("Tidak ada buku yang terdaftar.");
        } else {
            System.out.println("\nDaftar Buku:");
            for (int i = 0; i < bookList.size(); i++) {
                Book book = bookList.get(i);
                System.out.printf("%d. %s oleh %s (%s) - %d salinan tersedia%n", 
                        i + 1, 
                        book.getTitle(), 
                        book.getAuthor(), 
                        book.getISBN(), 
                        book.getAvailableItems().size());
            }
        }
    }
    
    private static void addBook() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk menambah buku.");
            return;
        }
        
        System.out.println("\nTambah Buku Baru:");
        
        try {
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine();
            System.out.print("Judul: ");
            String title = scanner.nextLine();
            System.out.print("Pengarang: ");
            String author = scanner.nextLine();
            System.out.print("Penerbit: ");
            String publisher = scanner.nextLine();
            System.out.print("Tahun Terbit: ");
            int year = getIntInput("");
            System.out.print("Deskripsi: ");
            String description = scanner.nextLine();
            System.out.print("Jumlah Halaman: ");
            int pages = getIntInput("");
            
            System.out.println("Format Buku:");
            System.out.println("1. HARDCOVER");
            System.out.println("2. PAPERBACK");
            System.out.println("3. EBOOK");
            System.out.println("4. AUDIOBOOK");
            int formatChoice = getIntInput("Pilih format: ");
            
            BookFormat format;
            switch (formatChoice) {
                case 1:
                    format = BookFormat.HARDCOVER;
                    break;
                case 2:
                    format = BookFormat.PAPERBACK;
                    break;
                case 3:
                    format = BookFormat.EBOOK;
                    break;
                case 4:
                    format = BookFormat.AUDIOBOOK;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Menggunakan PAPERBACK sebagai default.");
                    format = BookFormat.PAPERBACK;
            }
            
            System.out.println("Bahasa Buku:");
            System.out.println("1. INDONESIAN");
            System.out.println("2. ENGLISH");
            System.out.println("3. JAPANESE");
            System.out.println("4. FRENCH");
            System.out.println("5. GERMAN");
            System.out.println("6. OTHER");
            int languageChoice = getIntInput("Pilih bahasa: ");
            
            Language language;
            switch (languageChoice) {
                case 1:
                    language = Language.INDONESIAN;
                    break;
                case 2:
                    language = Language.ENGLISH;
                    break;
                case 3:
                    language = Language.JAPANESE;
                    break;
                case 4:
                    language = Language.FRENCH;
                    break;
                case 5:
                    language = Language.GERMAN;
                    break;
                case 6:
                    language = Language.OTHER;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Menggunakan INDONESIAN sebagai default.");
                    language = Language.INDONESIAN;
            }
            
            Book book = new Book(isbn, title, author, publisher, year, description, pages, format, language);
            library.addBook(book);
            books.put(isbn, book);
            
            System.out.println("Buku berhasil ditambahkan: " + book.getTitle());
            
            // tambahkan buku ke kategori
            System.out.print("Apakah Anda ingin menambahkan buku ini ke kategori? (y/n): ");
            String addToCategory = scanner.nextLine().trim().toLowerCase();
            
            if (addToCategory.equals("y")) {
                displayCategories();
                List<BookCategory> categoryList = library.getCollection().getCategories();
                if (!categoryList.isEmpty()) {
                    int categoryIndex = getIntInput("Pilih nomor kategori: ");
                    if (categoryIndex >= 1 && categoryIndex <= categoryList.size()) {
                        BookCategory category = categoryList.get(categoryIndex - 1);
                        library.addBookToCategory(book, category);
                        System.out.println("Buku ditambahkan ke kategori: " + category.getName());
                    } else {
                        System.out.println("Nomor kategori tidak valid.");
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void addBookItem() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk menambah salinan buku.");
            return;
        }
        
        displayBooks();
        
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor buku yang akan ditambahkan salinannya (0 untuk batal): ");
        if (index < 1 || index > bookList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Book book = bookList.get(index - 1);
        
        System.out.println("\nTambah Salinan Buku '" + book.getTitle() + "':");
        
        try {
            System.out.print("Kode Barcode: ");
            String barcode = scanner.nextLine();
            
            BookItem bookItem = currentLibrarian.addBookItem(book, barcode);
            bookItems.put(barcode, bookItem);
            
            System.out.print("Apakah salinan ini hanya untuk referensi? (y/n): ");
            String referenceOnly = scanner.nextLine().trim().toLowerCase();
            
            if (referenceOnly.equals("y")) {
                bookItem.setReferenceOnly(true);
                System.out.println("Salinan ditandai sebagai hanya untuk referensi.");
            }
            
            System.out.print("Masukkan harga buku: ");
            double price = getDoubleInput("");
            bookItem.setPrice(price);
            
            System.out.print("Masukkan lokasi penyimpanan: ");
            String location = scanner.nextLine();
            bookItem.setLocation(location);
            
            System.out.println("Salinan buku berhasil ditambahkan dengan barcode: " + bookItem.getBarcode());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void updateBook() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk mengubah informasi buku.");
            return;
        }
        
        displayBooks();
        
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor buku yang akan diubah (0 untuk batal): ");
        if (index < 1 || index > bookList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Book book = bookList.get(index - 1);
        
        System.out.println("\nUbah Informasi Buku '" + book.getTitle() + "':");
        System.out.println("1. Ubah Judul");
        System.out.println("2. Ubah Pengarang");
        System.out.println("3. Ubah Penerbit");
        System.out.println("4. Ubah Tahun Terbit");
        System.out.println("5. Ubah Deskripsi");
        System.out.println("6. Ubah Jumlah Halaman");
        System.out.println("7. Ubah Format");
        System.out.println("8. Ubah Bahasa");
        System.out.println("0. Batal");
        
        int choice = getIntInput("Pilih informasi yang akan diubah: ");
        
        try {
            switch (choice) {
                case 1:
                    System.out.print("Judul baru: ");
                    String title = scanner.nextLine();
                    book.setTitle(title);
                    break;
                case 2:
                    System.out.print("Pengarang baru: ");
                    String author = scanner.nextLine();
                    book.setAuthor(author);
                    break;
                case 3:
                    System.out.print("Penerbit baru: ");
                    String publisher = scanner.nextLine();
                    book.setPublisher(publisher);
                    break;
                case 4:
                    System.out.print("Tahun terbit baru: ");
                    int year = getIntInput("");
                    book.setPublicationYear(year);
                    break;
                case 5:
                    System.out.print("Deskripsi baru: ");
                    String description = scanner.nextLine();
                    book.setDescription(description);
                    break;
                case 6:
                    System.out.print("Jumlah halaman baru: ");
                    int pages = getIntInput("");
                    book.setNumberOfPages(pages);
                    break;
                case 7:
                    System.out.println("Format Buku Baru:");
                    System.out.println("1. HARDCOVER");
                    System.out.println("2. PAPERBACK");
                    System.out.println("3. EBOOK");
                    System.out.println("4. AUDIOBOOK");
                    int formatChoice = getIntInput("Pilih format: ");
                    
                    BookFormat format;
                    switch (formatChoice) {
                        case 1:
                            format = BookFormat.HARDCOVER;
                            break;
                        case 2:
                            format = BookFormat.PAPERBACK;
                            break;
                        case 3:
                            format = BookFormat.EBOOK;
                            break;
                        case 4:
                            format = BookFormat.AUDIOBOOK;
                            break;
                        default:
                            System.out.println("Pilihan tidak valid. Tidak ada perubahan.");
                            return;
                    }
                    
                    book.setFormat(format);
                    break;
                case 8:
                    System.out.println("Bahasa Buku Baru:");
                    System.out.println("1. INDONESIAN");
                    System.out.println("2. ENGLISH");
                    System.out.println("3. JAPANESE");
                    System.out.println("4. FRENCH");
                    System.out.println("5. GERMAN");
                    System.out.println("6. OTHER");
                    int languageChoice = getIntInput("Pilih bahasa: ");
                    
                    Language language;
                    switch (languageChoice) {
                        case 1:
                            language = Language.INDONESIAN;
                            break;
                        case 2:
                            language = Language.ENGLISH;
                            break;
                        case 3:
                            language = Language.JAPANESE;
                            break;
                        case 4:
                            language = Language.FRENCH;
                            break;
                        case 5:
                            language = Language.GERMAN;
                            break;
                        case 6:
                            language = Language.OTHER;
                            break;
                        default:
                            System.out.println("Pilihan tidak valid. Tidak ada perubahan.");
                            return;
                    }
                    
                    book.setLanguage(language);
                    break;
                case 0:
                    System.out.println("Perubahan dibatalkan.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
                    return;
            }
            
            System.out.println("Informasi buku berhasil diubah.");
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void viewBookDetails() {
        displayBooks();
        
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor buku untuk melihat detail (0 untuk batal): ");
        if (index < 1 || index > bookList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Book book = bookList.get(index - 1);
        
        System.out.println("\n=== Detail Buku ===");
        System.out.println("ISBN: " + book.getISBN());
        System.out.println("Judul: " + book.getTitle());
        System.out.println("Pengarang: " + book.getAuthor());
        System.out.println("Penerbit: " + book.getPublisher());
        System.out.println("Tahun Terbit: " + book.getPublicationYear());
        System.out.println("Deskripsi: " + book.getDescription());
        System.out.println("Jumlah Halaman: " + book.getNumberOfPages());
        System.out.println("Format: " + book.getFormat());
        System.out.println("Bahasa: " + book.getLanguage());
        
        // tampilkan salinan buku
        List<BookItem> items = book.getItems();
        if (items.isEmpty()) {
            System.out.println("\nBuku ini belum memiliki salinan.");
        } else {
            System.out.println("\nSalinan Buku (" + items.size() + " total, " + book.getAvailableItems().size() + " tersedia):");
            for (int i = 0; i < items.size(); i++) {
                BookItem item = items.get(i);
                System.out.printf("%d. Barcode: %s - Status: %s - %s%n", 
                        i + 1, 
                        item.getBarcode(), 
                        item.getStatus(),
                        item.isReferenceOnly() ? "Hanya Referensi" : "Dapat Dipinjam");
            }
        }
    }
    
    private static void removeBook() {
        if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
            System.out.println("Anda tidak memiliki hak akses untuk menghapus buku.");
            return;
        }
        
        displayBooks();
        
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor buku yang akan dihapus (0 untuk batal): ");
        if (index < 1 || index > bookList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Book book = bookList.get(index - 1);
        
        System.out.print("Apakah Anda yakin ingin menghapus buku '" + book.getTitle() + "'? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (confirm.equals("y")) {
            library.getCollection().removeBook(book);
            books.remove(book.getISBN());
            System.out.println("Buku berhasil dihapus.");
        } else {
            System.out.println("Penghapusan dibatalkan.");
        }
    }
    
    private static void manageMembers() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n==== KELOLA ANGGOTA ====");
            System.out.println("1. Lihat Daftar Anggota");
            System.out.println("2. Tambah Anggota Baru");
            System.out.println("3. Ubah Informasi Anggota");
            System.out.println("4. Lihat Detail Anggota");
            System.out.println("5. Perpanjang Keanggotaan");
            System.out.println("6. Nonaktifkan/Aktifkan Anggota");
            System.out.println("0. Kembali ke Menu Utama");
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    displayMembers();
                    break;
                case 2:
                    addMember();
                    break;
                case 3:
                    updateMember();
                    break;
                case 4:
                    viewMemberDetails();
                    break;
                case 5:
                    renewMembership();
                    break;
                case 6:
                    toggleMemberStatus();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }

    private static void displayMembers() {
        if (members.isEmpty()) {
            System.out.println("Tidak ada anggota yang terdaftar.");
        } else {
            System.out.println("\nDaftar Anggota:");
            for (int i = 0; i < members.size(); i++) {
                Member member = members.get(i);
                String memberType = "Reguler";
                
                if (member instanceof StudentMember) {
                    memberType = "Mahasiswa";
                } else if (member instanceof RegularMember) {
                    memberType = "Reguler";
                }
                
                System.out.printf("%d. %s (%s) - %s - %s%n", 
                        i + 1, 
                        member.getName(), 
                        member.getMemberId(), 
                        memberType,
                        member.isActive() ? "Aktif" : "Tidak Aktif");
            }
        }
    }
    
    private static void addMember() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk menambah anggota.");
            return;
        }
        
        System.out.println("\nTambah Anggota Baru:");
        
        try {
            System.out.print("ID Person: ");
            String id = scanner.nextLine();
            System.out.print("Nama: ");
            String name = scanner.nextLine();
            System.out.print("Alamat: ");
            String address = scanner.nextLine();
            System.out.print("Nomor Telepon: ");
            String phone = scanner.nextLine();
            System.out.print("Email: ");
            String email = scanner.nextLine();
            
            Person person = new Person(id, name, address, phone);
            person.setEmail(email);
            
            Member member = currentLibrarian.addMember(person);
            
            System.out.println("Jenis Anggota:");
            System.out.println("1. Mahasiswa");
            System.out.println("2. Reguler");
            int typeChoice = getIntInput("Pilih jenis anggota: ");
            
            if (typeChoice == 1) {
                // mahasiswa
                System.out.print("ID Mahasiswa: ");
                String studentId = scanner.nextLine();
                System.out.print("Fakultas: ");
                String faculty = scanner.nextLine();
                System.out.print("Jurusan: ");
                String department = scanner.nextLine();
                System.out.print("Tahun Studi: ");
                int yearOfStudy = getIntInput("");
                
                StudentMember studentMember = new StudentMember(member, studentId, faculty, department, yearOfStudy);
                members.add(studentMember);
                
                System.out.println("Anggota mahasiswa berhasil ditambahkan: " + studentMember.getName());
            } else {
                // reguler
                System.out.print("Pekerjaan: ");
                String occupation = scanner.nextLine();
                System.out.print("Nama Perusahaan/Institusi: ");
                String employerName = scanner.nextLine();
                System.out.print("Apakah anggota premium? (y/n): ");
                String isPremiumStr = scanner.nextLine().trim().toLowerCase();
                boolean isPremium = isPremiumStr.equals("y");
                
                RegularMember regularMember = new RegularMember(member, occupation, employerName, isPremium);
                members.add(regularMember);
                
                System.out.println("Anggota reguler berhasil ditambahkan: " + regularMember.getName());
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void updateMember() {
        displayMembers();
        
        if (members.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor anggota yang akan diubah (0 untuk batal): ");
        if (index < 1 || index > members.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Member member = members.get(index - 1);
        
        System.out.println("\nUbah Informasi Anggota '" + member.getName() + "':");
        System.out.println("1. Ubah Nama");
        System.out.println("2. Ubah Alamat");
        System.out.println("3. Ubah Nomor Telepon");
        System.out.println("4. Ubah Email");
        
        if (member instanceof StudentMember) {
            System.out.println("5. Ubah ID Mahasiswa");
            System.out.println("6. Ubah Fakultas");
            System.out.println("7. Ubah Jurusan");
            System.out.println("8. Ubah Tahun Studi");
        } else if (member instanceof RegularMember) {
            System.out.println("5. Ubah Pekerjaan");
            System.out.println("6. Ubah Nama Perusahaan/Institusi");
            System.out.println("7. Ubah Status Premium");
        }
        
        System.out.println("0. Batal");
        
        int choice = getIntInput("Pilih informasi yang akan diubah: ");
        
        try {
            switch (choice) {
                case 1:
                    System.out.print("Nama baru: ");
                    String name = scanner.nextLine();
                    member.setName(name);
                    break;
                case 2:
                    System.out.print("Alamat baru: ");
                    String address = scanner.nextLine();
                    member.setAddress(address);
                    break;
                case 3:
                    System.out.print("Nomor telepon baru: ");
                    String phone = scanner.nextLine();
                    member.setPhoneNumber(phone);
                    break;
                case 4:
                    System.out.print("Email baru: ");
                    String email = scanner.nextLine();
                    member.setEmail(email);
                    break;
                case 5:
                    if (member instanceof StudentMember) {
                        System.out.print("ID Mahasiswa baru: ");
                        String studentId = scanner.nextLine();
                        ((StudentMember) member).setStudentId(studentId);
                    } else if (member instanceof RegularMember) {
                        System.out.print("Pekerjaan baru: ");
                        String occupation = scanner.nextLine();
                        ((RegularMember) member).setOccupation(occupation);
                    }
                    break;
                case 6:
                    if (member instanceof StudentMember) {
                        System.out.print("Fakultas baru: ");
                        String faculty = scanner.nextLine();
                        ((StudentMember) member).setFaculty(faculty);
                    } else if (member instanceof RegularMember) {
                        System.out.print("Nama Perusahaan/Institusi baru: ");
                        String employerName = scanner.nextLine();
                        ((RegularMember) member).setEmployerName(employerName);
                    }
                    break;
                case 7:
                    if (member instanceof StudentMember) {
                        System.out.print("Jurusan baru: ");
                        String department = scanner.nextLine();
                        ((StudentMember) member).setDepartment(department);
                    } else if (member instanceof RegularMember) {
                        System.out.print("Apakah anggota premium? (y/n): ");
                        String isPremiumStr = scanner.nextLine().trim().toLowerCase();
                        boolean isPremium = isPremiumStr.equals("y");
                        ((RegularMember) member).setPremium(isPremium);
                    }
                    break;
                case 8:
                    if (member instanceof StudentMember) {
                        System.out.print("Tahun Studi baru: ");
                        int yearOfStudy = getIntInput("");
                        ((StudentMember) member).setYearOfStudy(yearOfStudy);
                    }
                    break;
                case 0:
                    System.out.println("Perubahan dibatalkan.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
                    return;
            }
            
            System.out.println("Informasi anggota berhasil diubah.");
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void viewMemberDetails() {
        displayMembers();
        
        if (members.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor anggota untuk melihat detail (0 untuk batal): ");
        if (index < 1 || index > members.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Member member = members.get(index - 1);
        
        System.out.println("\n=== Detail Anggota ===");
        System.out.println("ID Anggota: " + member.getMemberId());
        System.out.println("Nama: " + member.getName());
        System.out.println("Alamat: " + member.getAddress());
        System.out.println("Nomor Telepon: " + member.getPhoneNumber());
        System.out.println("Email: " + member.getEmail());
        System.out.println("Tanggal Registrasi: " + dateFormat.format(member.getRegistrationDate()));
        System.out.println("Tanggal Kadaluarsa: " + dateFormat.format(member.getExpiryDate()));
        System.out.println("Status: " + member.getStatus());
        System.out.println("Aktif: " + (member.isActive() ? "Ya" : "Tidak"));
        
        if (member instanceof StudentMember) {
            StudentMember studentMember = (StudentMember) member;
            System.out.println("Jenis Anggota: Mahasiswa");
            System.out.println("ID Mahasiswa: " + studentMember.getStudentId());
            System.out.println("Fakultas: " + studentMember.getFaculty());
            System.out.println("Jurusan: " + studentMember.getDepartment());
            System.out.println("Tahun Studi: " + studentMember.getYearOfStudy());
        } else if (member instanceof RegularMember) {
            RegularMember regularMember = (RegularMember) member;
            System.out.println("Jenis Anggota: Reguler");
            System.out.println("Pekerjaan: " + regularMember.getOccupation());
            System.out.println("Perusahaan/Institusi: " + regularMember.getEmployerName());
            System.out.println("Premium: " + (regularMember.isPremium() ? "Ya" : "Tidak"));
        }
        
        System.out.println("Batas Buku: " + member.getMaxBooks());
        System.out.println("Durasi Peminjaman: " + member.getMaxLoanDays() + " hari");
        System.out.println("Buku yang Sedang Dipinjam: " + member.getCurrentBooksCount());
        System.out.println("Total Denda yang Dibayar: Rp" + String.format("%.2f", member.getTotalFinesPaid()));
        
        // tampilkan peminjaman aktif
        List<BookLoan> activeLoans = new ArrayList<>();
        for (BookLoan loan : member.getBookLoans()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                activeLoans.add(loan);
            }
        }
        
        if (activeLoans.isEmpty()) {
            System.out.println("\nTidak ada peminjaman aktif.");
        } else {
            System.out.println("\nPeminjaman Aktif:");
            for (int i = 0; i < activeLoans.size(); i++) {
                BookLoan loan = activeLoans.get(i);
                System.out.printf("%d. '%s' (Barcode: %s) - Jatuh Tempo: %s - Status: %s%n", 
                        i + 1, 
                        loan.getBookItem().getBook().getTitle(), 
                        loan.getBookItem().getBarcode(), 
                        dateFormat.format(loan.getDueDate()),
                        loan.getStatus());
            }
        }
        
        // tampilkan reservasi aktif
        List<Reservation> activeReservations = new ArrayList<>();
        for (Reservation reservation : member.getReservations()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                activeReservations.add(reservation);
            }
        }
        
        if (activeReservations.isEmpty()) {
            System.out.println("\nTidak ada reservasi aktif.");
        } else {
            System.out.println("\nReservasi Aktif:");
            for (int i = 0; i < activeReservations.size(); i++) {
                Reservation reservation = activeReservations.get(i);
                System.out.printf("%d. '%s' - ID Reservasi: %s - Tanggal: %s%n", 
                        i + 1, 
                        reservation.getBook().getTitle(), 
                        reservation.getReservationId(), 
                        dateFormat.format(reservation.getReservationDate()));
            }
        }
    }
    
    private static void renewMembership() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk memperpanjang keanggotaan.");
            return;
        }
        
        displayMembers();
        
        if (members.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor anggota yang akan diperpanjang keanggotaannya (0 untuk batal): ");
        if (index < 1 || index > members.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Member member = members.get(index - 1);
        
        System.out.println("\nPerpanjang Keanggotaan untuk '" + member.getName() + "':");
        System.out.println("Tanggal Kadaluarsa Saat Ini: " + dateFormat.format(member.getExpiryDate()));
        
        int months = getIntInput("Masukkan jumlah bulan perpanjangan: ");
        
        if (months <= 0) {
            System.out.println("Jumlah bulan tidak valid. Perpanjangan dibatalkan.");
            return;
        }
        
        member.renewMembership(months);
        System.out.println("Keanggotaan berhasil diperpanjang.");
        System.out.println("Tanggal Kadaluarsa Baru: " + dateFormat.format(member.getExpiryDate()));
    }
    
    private static void toggleMemberStatus() {
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            System.out.println("Anda tidak memiliki hak akses untuk mengubah status anggota.");
            return;
        }
        
        displayMembers();
        
        if (members.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor anggota yang akan diubah statusnya (0 untuk batal): ");
        if (index < 1 || index > members.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Member member = members.get(index - 1);
        
        System.out.println("\nUbah Status Anggota '" + member.getName() + "':");
        System.out.println("Status Saat Ini: " + (member.isActive() ? "Aktif" : "Tidak Aktif"));
        
        System.out.println("1. Aktifkan");
        System.out.println("2. Nonaktifkan");
        System.out.println("3. Blacklist");
        System.out.println("0. Batal");
        
        int choice = getIntInput("Pilih tindakan: ");
        
        try {
            switch (choice) {
                case 1:
                    member.setActive(true);
                    member.setStatus(MemberStatus.ACTIVE);
                    System.out.println("Anggota berhasil diaktifkan.");
                    break;
                case 2:
                    member.setActive(false);
                    member.setStatus(MemberStatus.INACTIVE);
                    System.out.println("Anggota berhasil dinonaktifkan.");
                    break;
                case 3:
                    if (currentLibrarian.getPermission() != LibrarianPermission.ADMIN) {
                        System.out.println("Anda tidak memiliki hak akses untuk melakukan blacklist anggota.");
                        return;
                    }
                    
                    currentLibrarian.blacklistMember(member);
                    System.out.println("Anggota berhasil dimasukkan ke dalam blacklist.");
                    break;
                case 0:
                    System.out.println("Perubahan dibatalkan.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.");
                    return;
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
    
    private static void manageLending() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n==== KELOLA PEMINJAMAN DAN PENGEMBALIAN ====");
            System.out.println("1. Pinjamkan Buku");
            System.out.println("2. Kembalikan Buku");
            System.out.println("3. Lihat Daftar Peminjaman Aktif");
            System.out.println("4. Lihat Riwayat Peminjaman");
            System.out.println("5. Perpanjang Peminjaman");
            System.out.println("0. Kembali ke Menu Utama");
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    issueBook();
                    break;
                case 2:
                    returnBook();
                    break;
                case 3:
                    viewActiveLoans();
                    break;
                case 4:
                    viewLoanHistory();
                    break;
                case 5:
                    extendLoan();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
    
    private static void issueBook() {
        // milih anggota
        displayMembers();
        
        if (members.isEmpty()) {
            System.out.println("Tidak ada anggota yang terdaftar.");
            return;
        }
        
        int memberIndex = getIntInput("Pilih nomor anggota yang akan meminjam buku (0 untuk batal): ");
        if (memberIndex < 1 || memberIndex > members.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Member member = members.get(memberIndex - 1);
        
        // milih buku
        displayBooks();
        
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            System.out.println("Tidak ada buku yang terdaftar.");
            return;
        }
        
        int bookIndex = getIntInput("Pilih nomor buku yang akan dipinjam (0 untuk batal): ");
        if (bookIndex < 1 || bookIndex > bookList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Book book = bookList.get(bookIndex - 1);
        
        // milih salinan buku
        List<BookItem> availableItems = book.getAvailableItems();
        if (availableItems.isEmpty()) {
            System.out.println("Tidak ada salinan buku yang tersedia untuk dipinjam.");
            return;
        }
        
        System.out.println("\nSalinan Buku yang Tersedia:");
        for (int i = 0; i < availableItems.size(); i++) {
            BookItem item = availableItems.get(i);
            System.out.printf("%d. Barcode: %s - %s%n", 
                    i + 1, 
                    item.getBarcode(), 
                    item.isReferenceOnly() ? "Hanya Referensi" : "Dapat Dipinjam");
        }
        
        int itemIndex = getIntInput("Pilih nomor salinan buku yang akan dipinjam (0 untuk batal): ");
        if (itemIndex < 1 || itemIndex > availableItems.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        BookItem bookItem = availableItems.get(itemIndex - 1);
        
        try {
            BookLoan loan = currentLibrarian.issueBook(member, bookItem);
            loans.put(loan.getLoanId(), loan);
            System.out.println("Buku berhasil dipinjamkan:");
            System.out.println("ID Peminjaman: " + loan.getLoanId());
            System.out.println("Buku: " + book.getTitle());
            System.out.println("Peminjam: " + member.getName());
            System.out.println("Tanggal Peminjaman: " + dateFormat.format(loan.getIssueDate()));
            System.out.println("Tanggal Jatuh Tempo: " + dateFormat.format(loan.getDueDate()));
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat meminjamkan buku: " + e.getMessage());
        }
    }
    
    private static void returnBook() {
        // tampilkan daftar peminjaman aktif
        viewActiveLoans();
        
        if (loans.isEmpty()) {
            System.out.println("Tidak ada peminjaman yang aktif.");
            return;
        }
        
        String loanId = getStringInput("Masukkan ID peminjaman yang akan dikembalikan (0 untuk batal): ");
        if (loanId.equals("0")) {
            System.out.println("Pengembalian dibatalkan.");
            return;
        }
        
        BookLoan loan = loans.get(loanId);
        if (loan == null) {
            System.out.println("ID peminjaman tidak valid.");
            return;
        }
        
        try {
            currentLibrarian.returnBook(loan);
            
            double fine = loan.getFine();
            
            System.out.println("Buku berhasil dikembalikan:");
            System.out.println("Buku: " + loan.getBookItem().getBook().getTitle());
            System.out.println("Peminjam: " + loan.getMember().getName());
            System.out.println("Tanggal Kembali: " + dateFormat.format(loan.getReturnDate()));
            
            if (fine > 0) {
                System.out.println("Denda: Rp" + String.format("%.2f", fine));
                System.out.print("Apakah denda akan dibayar sekarang? (y/n): ");
                String payNow = scanner.nextLine().trim().toLowerCase();
                
                if (payNow.equals("y")) {
                    loan.getMember().payFine(fine);
                    System.out.println("Denda berhasil dibayar.");
                } else {
                    System.out.println("Denda belum dibayar.");
                }
            }
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat mengembalikan buku: " + e.getMessage());
        }
    }
    
    private static void viewActiveLoans() {
        List<BookLoan> activeLoans = new ArrayList<>();
        
        // kumpulkan semua peminjaman aktif dari semua anggota
        for (Member member : members) {
            for (BookLoan loan : member.getBookLoans()) {
                if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                    activeLoans.add(loan);
                    loans.put(loan.getLoanId(), loan); // pastikan ada di map loans
                }
            }
        }
        
        if (activeLoans.isEmpty()) {
            System.out.println("Tidak ada peminjaman aktif.");
        } else {
            System.out.println("\nDaftar Peminjaman Aktif:");
            for (int i = 0; i < activeLoans.size(); i++) {
                BookLoan loan = activeLoans.get(i);
                System.out.printf("%d. ID: %s - '%s' - Peminjam: %s - Jatuh Tempo: %s - Status: %s%n", 
                        i + 1, 
                        loan.getLoanId(),
                        loan.getBookItem().getBook().getTitle(), 
                        loan.getMember().getName(), 
                        dateFormat.format(loan.getDueDate()),
                        loan.getStatus());
            }
        }
    }
    
    private static void viewLoanHistory() {
        List<BookLoan> allLoans = new ArrayList<>();
        
        // kumpulkan semua peminjaman dari semua anggota
        for (Member member : members) {
            allLoans.addAll(member.getBookLoans());
        }
        
        if (allLoans.isEmpty()) {
            System.out.println("Tidak ada riwayat peminjaman.");
        } else {
            System.out.println("\nRiwayat Peminjaman:");
            for (int i = 0; i < allLoans.size(); i++) {
                BookLoan loan = allLoans.get(i);
                String returnDateStr = loan.getReturnDate() != null ? 
                        dateFormat.format(loan.getReturnDate()) : "Belum dikembalikan";
                
                System.out.printf("%d. ID: %s - '%s' - Peminjam: %s - Tgl Pinjam: %s - Jatuh Tempo: %s - Tgl Kembali: %s - Status: %s%n", 
                        i + 1, 
                        loan.getLoanId(),
                        loan.getBookItem().getBook().getTitle(), 
                        loan.getMember().getName(), 
                        dateFormat.format(loan.getIssueDate()),
                        dateFormat.format(loan.getDueDate()),
                        returnDateStr,
                        loan.getStatus());
            }
        }
    }
    
    private static void extendLoan() {
        // tampilkan daftar peminjaman aktif
        viewActiveLoans();
        
        if (loans.isEmpty()) {
            System.out.println("Tidak ada peminjaman yang aktif.");
            return;
        }
        
        String loanId = getStringInput("Masukkan ID peminjaman yang akan diperpanjang (0 untuk batal): ");
        if (loanId.equals("0")) {
            System.out.println("Perpanjangan dibatalkan.");
            return;
        }
        
        BookLoan loan = loans.get(loanId);
        if (loan == null) {
            System.out.println("ID peminjaman tidak valid.");
            return;
        }
        
        int days = getIntInput("Masukkan jumlah hari perpanjangan: ");
        if (days <= 0) {
            System.out.println("Jumlah hari tidak valid. Perpanjangan dibatalkan.");
            return;
        }
        
        boolean extended = loan.extendDueDate(days);
        
        if (extended) {
            System.out.println("Peminjaman berhasil diperpanjang:");
            System.out.println("Buku: " + loan.getBookItem().getBook().getTitle());
            System.out.println("Peminjam: " + loan.getMember().getName());
            System.out.println("Tanggal Jatuh Tempo Baru: " + dateFormat.format(loan.getDueDate()));
        } else {
            System.out.println("Tidak dapat memperpanjang peminjaman. Kemungkinan penyebab:");
            System.out.println("- Buku sudah terlambat");
            System.out.println("- Buku memiliki reservasi dari anggota lain");
        }
    }
    
    private static void manageReservations() {
        boolean back = false;
        
        while (!back) {
            System.out.println("\n==== KELOLA RESERVASI ====");
            System.out.println("1. Buat Reservasi Baru");
            System.out.println("2. Lihat Daftar Reservasi");
            System.out.println("3. Proses Reservasi");
            System.out.println("4. Batalkan Reservasi");
            System.out.println("0. Kembali ke Menu Utama");
            
            int choice = getIntInput("Pilih menu: ");
            
            switch (choice) {
                case 1:
                    createReservation();
                    break;
                case 2:
                    viewReservations();
                    break;
                case 3:
                    processReservation();
                    break;
                case 4:
                    cancelReservation();
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid. Silakan coba lagi.");
            }
        }
    }
    
    private static void createReservation() {
        // pilih anggota
        displayMembers();
        
        if (members.isEmpty()) {
            System.out.println("Tidak ada anggota yang terdaftar.");
            return;
        }
        
        int memberIndex = getIntInput("Pilih nomor anggota yang akan mereservasi buku (0 untuk batal): ");
        if (memberIndex < 1 || memberIndex > members.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Member member = members.get(memberIndex - 1);
        
        // pilih buku
        displayBooks();
        
        List<Book> bookList = library.getCollection().getBooks();
        if (bookList.isEmpty()) {
            System.out.println("Tidak ada buku yang terdaftar.");
            return;
        }
        
        int bookIndex = getIntInput("Pilih nomor buku yang akan direservasi (0 untuk batal): ");
        if (bookIndex < 1 || bookIndex > bookList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Book book = bookList.get(bookIndex - 1);
        
        // cek ketersediaan buku
        if (!book.getAvailableItems().isEmpty()) {
            System.out.println("Buku ini sudah tersedia. Reservasi tidak diperlukan.");
            return;
        }
        
        try {
            Reservation reservation = member.reserveBook(book);
            reservations.put(reservation.getReservationId(), reservation);
            
            System.out.println("Reservasi berhasil dibuat:");
            System.out.println("ID Reservasi: " + reservation.getReservationId());
            System.out.println("Buku: " + book.getTitle());
            System.out.println("Anggota: " + member.getName());
            System.out.println("Tanggal Reservasi: " + dateFormat.format(reservation.getReservationDate()));
            System.out.println("Status: " + reservation.getStatus());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat membuat reservasi: " + e.getMessage());
        }
    }
    
    private static void viewReservations() {
        List<Reservation> allReservations = new ArrayList<>();
        
        // kumpulkan semua reservasi dari semua anggota
        for (Member member : members) {
            allReservations.addAll(member.getReservations());
        }
        
        if (allReservations.isEmpty()) {
            System.out.println("Tidak ada reservasi yang terdaftar.");
        } else {
            System.out.println("\nDaftar Reservasi:");
            for (int i = 0; i < allReservations.size(); i++) {
                Reservation reservation = allReservations.get(i);
                reservations.put(reservation.getReservationId(), reservation); // Pastikan ada di map reservations
                
                System.out.printf("%d. ID: %s - '%s' - Anggota: %s - Tanggal: %s - Status: %s%n", 
                        i + 1, 
                        reservation.getReservationId(),
                        reservation.getBook().getTitle(), 
                        reservation.getMember().getName(), 
                        dateFormat.format(reservation.getReservationDate()),
                        reservation.getStatus());
            }
        }
    }
    
    private static void processReservation() {
        // tampilkan daftar reservasi pending
        List<Reservation> pendingReservations = new ArrayList<>();
        
        for (Reservation reservation : reservations.values()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                pendingReservations.add(reservation);
            }
        }
        
        if (pendingReservations.isEmpty()) {
            System.out.println("Tidak ada reservasi yang menunggu diproses.");
            return;
        }
        
        System.out.println("\nDaftar Reservasi Pending:");
        for (int i = 0; i < pendingReservations.size(); i++) {
            Reservation reservation = pendingReservations.get(i);
            System.out.printf("%d. ID: %s - '%s' - Anggota: %s - Tanggal: %s%n", 
                    i + 1, 
                    reservation.getReservationId(),
                    reservation.getBook().getTitle(), 
                    reservation.getMember().getName(), 
                    dateFormat.format(reservation.getReservationDate()));
        }
        
        int index = getIntInput("Pilih nomor reservasi yang akan diproses (0 untuk batal): ");
        if (index < 1 || index > pendingReservations.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Reservation reservation = pendingReservations.get(index - 1);
        
        Book book = reservation.getBook();
        if (book.getAvailableItems().isEmpty()) {
            System.out.println("Tidak ada salinan buku yang tersedia untuk reservasi ini.");
            System.out.println("Reservasi tidak dapat diproses saat ini.");
            return;
        }
        
        try {
            currentLibrarian.processReservation(reservation);
            System.out.println("Reservasi berhasil diproses:");
            System.out.println("Buku: " + reservation.getBook().getTitle());
            System.out.println("Anggota: " + reservation.getMember().getName());
            System.out.println("Status: " + reservation.getStatus());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat memproses reservasi: " + e.getMessage());
        }
    }
    
    private static void cancelReservation() {
        // tampilkan daftar reservasi pending
        List<Reservation> pendingReservations = new ArrayList<>();
        
        for (Reservation reservation : reservations.values()) {
            if (reservation.getStatus() == ReservationStatus.PENDING) {
                pendingReservations.add(reservation);
            }
        }
        
        if (pendingReservations.isEmpty()) {
            System.out.println("Tidak ada reservasi aktif yang dapat dibatalkan.");
            return;
        }
        
        System.out.println("\nDaftar Reservasi Aktif:");
        for (int i = 0; i < pendingReservations.size(); i++) {
            Reservation reservation = pendingReservations.get(i);
            System.out.printf("%d. ID: %s - '%s' - Anggota: %s - Tanggal: %s%n", 
                    i + 1, 
                    reservation.getReservationId(),
                    reservation.getBook().getTitle(), 
                    reservation.getMember().getName(), 
                    dateFormat.format(reservation.getReservationDate()));
        }
        
        int index = getIntInput("Pilih nomor reservasi yang akan dibatalkan (0 untuk batal): ");
        if (index < 1 || index > pendingReservations.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        Reservation reservation = pendingReservations.get(index - 1);
        
        reservation.cancelReservation();
        System.out.println("Reservasi berhasil dibatalkan.");
    }
    
    private static void searchBooks() {
        System.out.println("\n==== CARI BUKU ====");
        System.out.println("1. Cari berdasarkan Judul");
        System.out.println("2. Cari berdasarkan Pengarang");
        System.out.println("3. Cari berdasarkan Kategori");
        System.out.println("0. Kembali ke Menu Utama");
        
        int choice = getIntInput("Pilih metode pencarian: ");
        
        switch (choice) {
            case 1:
                searchByTitle();
                break;
            case 2:
                searchByAuthor();
                break;
            case 3:
                searchByCategory();
                break;
            case 0:
                return;
            default:
                System.out.println("Pilihan tidak valid.");
        }
    }
    
    private static void searchByTitle() {
        String title = getStringInput("Masukkan judul buku atau kata kunci: ");
        
        List<Book> results = library.searchByTitle(title);
        displaySearchResults(results);
    }
    
    private static void searchByAuthor() {
        String author = getStringInput("Masukkan nama pengarang atau kata kunci: ");
        
        List<Book> results = library.searchByAuthor(author);
        displaySearchResults(results);
    }
    
    private static void searchByCategory() {
        displayCategories();
        
        List<BookCategory> categoryList = library.getCollection().getCategories();
        if (categoryList.isEmpty()) {
            return;
        }
        
        int index = getIntInput("Pilih nomor kategori (0 untuk batal): ");
        if (index < 1 || index > categoryList.size()) {
            System.out.println("Nomor tidak valid atau dibatalkan.");
            return;
        }
        
        BookCategory category = categoryList.get(index - 1);
        
        List<Book> results = library.searchByCategory(category);
        displaySearchResults(results);
    }
    
    private static void displaySearchResults(List<Book> results) {
        if (results.isEmpty()) {
            System.out.println("Tidak ditemukan buku yang sesuai dengan kriteria pencarian.");
        } else {
            System.out.println("\nHasil Pencarian (" + results.size() + " buku ditemukan):");
            for (int i = 0; i < results.size(); i++) {
                Book book = results.get(i);
                System.out.printf("%d. %s oleh %s (%s) - %d salinan tersedia%n", 
                        i + 1, 
                        book.getTitle(), 
                        book.getAuthor(), 
                        book.getISBN(), 
                        book.getAvailableItems().size());
            }
            
            System.out.print("Apakah ingin melihat detail buku? (y/n): ");
            String viewDetail = scanner.nextLine().trim().toLowerCase();
            
            if (viewDetail.equals("y")) {
                int bookIndex = getIntInput("Pilih nomor buku untuk melihat detail (0 untuk batal): ");
                if (bookIndex >= 1 && bookIndex <= results.size()) {
                    Book book = results.get(bookIndex - 1);
                    
                    System.out.println("\n=== Detail Buku ===");
                    System.out.println("ISBN: " + book.getISBN());
                    System.out.println("Judul: " + book.getTitle());
                    System.out.println("Pengarang: " + book.getAuthor());
                    System.out.println("Penerbit: " + book.getPublisher());
                    System.out.println("Tahun Terbit: " + book.getPublicationYear());
                    System.out.println("Deskripsi: " + book.getDescription());
                    System.out.println("Jumlah Halaman: " + book.getNumberOfPages());
                    System.out.println("Format: " + book.getFormat());
                    System.out.println("Bahasa: " + book.getLanguage());
                    
                    // Tampilkan salinan buku
                    List<BookItem> items = book.getItems();
                    if (items.isEmpty()) {
                        System.out.println("\nBuku ini belum memiliki salinan.");
                    } else {
                        System.out.println("\nSalinan Buku (" + items.size() + " total, " + book.getAvailableItems().size() + " tersedia):");
                        for (int i = 0; i < items.size(); i++) {
                            BookItem item = items.get(i);
                            System.out.printf("%d. Barcode: %s - Status: %s - %s%n", 
                                    i + 1, 
                                    item.getBarcode(), 
                                    item.getStatus(),
                                    item.isReferenceOnly() ? "Hanya Referensi" : "Dapat Dipinjam");
                        }
                    }
                }
            }
        }
    }
    
    private static void displayStatistics() {
        System.out.println("\n=== STATISTIK PERPUSTAKAAN ===");
        System.out.println("Nama Perpustakaan: " + library.getName());
        System.out.println("Alamat: " + library.getAddress());
        System.out.println("Jumlah Pustakawan: " + library.getLibrarians().size());
        System.out.println("Jumlah Buku: " + library.getCollection().getTotalBooks());
        System.out.println("Jumlah Kategori: " + library.getCollection().getTotalCategories());
        System.out.println("Jumlah Anggota: " + members.size());
        
        // Hitung jumlah peminjaman aktif
        int activeLoans = 0;
        for (Member member : members) {
            for (BookLoan loan : member.getBookLoans()) {
                if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                    activeLoans++;
                }
            }
        }
        System.out.println("Jumlah Peminjaman Aktif: " + activeLoans);
        
        // Hitung jumlah reservasi pending
        int pendingReservations = 0;
        for (Member member : members) {
            for (Reservation reservation : member.getReservations()) {
                if (reservation.getStatus() == ReservationStatus.PENDING) {
                    pendingReservations++;
                }
            }
        }
        System.out.println("Jumlah Reservasi Menunggu: " + pendingReservations);
        
        // Hitung total denda
        double totalFines = 0;
        for (Member member : members) {
            totalFines += member.getTotalFinesPaid();
        }
        System.out.println("Total Denda yang Terkumpul: Rp" + String.format("%.2f", totalFines));
    }
    
    private static void runDemoMode() {
        System.out.println("\n==== DEMO MODE ====");
        System.out.println("Mode ini akan membuat data sampel untuk mendemonstrasikan fungsionalitas sistem.");
        System.out.print("Apakah Anda yakin ingin menjalankan mode demo? (y/n): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        
        if (!confirm.equals("y")) {
            System.out.println("Demo dibatalkan.");
            return;
        }
        
        try {
            // bersihkan data yang ada
            library.getCollection().getBooks().clear();
            library.getCollection().getCategories().clear();
            members.clear();
            books.clear();
            bookItems.clear();
            loans.clear();
            reservations.clear();
            
            // buat kategori
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
            List<Book> allBooks = List.of(book1, book2, book3, book4, book5, book6, book7, book8, book9, book10,
                                        book11, book12, book13, book14, book15, book16, book17, book18, book19, book20);
            
            for (Book book : allBooks) {
                library.addBook(book);
                books.put(book.getISBN(), book);
            }
            
            // kategorikan buku
            library.addBookToCategory(book1, fictionCategory);
            library.addBookToCategory(book2, fictionCategory);
            library.addBookToCategory(book3, fictionCategory);
            library.addBookToCategory(book4, fictionCategory);
            library.addBookToCategory(book5, nonFictionCategory);
            library.addBookToCategory(book5, scienceCategory);
            library.addBookToCategory(book6, scienceCategory);
            library.addBookToCategory(book7, scienceCategory);
            library.addBookToCategory(book7, nonFictionCategory);
            library.addBookToCategory(book8, technologyCategory);
            library.addBookToCategory(book8, nonFictionCategory);
            library.addBookToCategory(book9, technologyCategory);
            library.addBookToCategory(book10, technologyCategory);
            library.addBookToCategory(book10, scienceCategory);
            library.addBookToCategory(book11, historyCategory);
            library.addBookToCategory(book11, nonFictionCategory);
            library.addBookToCategory(book12, historyCategory);
            library.addBookToCategory(book13, philosophyCategory);
            library.addBookToCategory(book13, nonFictionCategory);
            library.addBookToCategory(book14, biographyCategory);
            library.addBookToCategory(book14, nonFictionCategory);
            library.addBookToCategory(book14, historyCategory);
            library.addBookToCategory(book15, cookingCategory);
            library.addBookToCategory(book15, nonFictionCategory);
            library.addBookToCategory(book16, artCategory);
            library.addBookToCategory(book16, nonFictionCategory);
            library.addBookToCategory(book17, travelCategory);
            library.addBookToCategory(book17, nonFictionCategory);
            library.addBookToCategory(book18, travelCategory);
            library.addBookToCategory(book19, technologyCategory);
            library.addBookToCategory(book20, cookingCategory);
            
            // tambahkan salinan buku (minimal 2 salinan per buku)
            Map<Book, List<BookItem>> bookCopies = new HashMap<>();
            
            for (Book book : allBooks) {
                List<BookItem> copies = new ArrayList<>();
                String isbnShort = book.getISBN().substring(4, 9);
                
                // buat 2-4 salinan per buku
                int numCopies = 2 + (int)(Math.random() * 3); // 2 sampai 4 salinan
                
                for (int i = 1; i <= numCopies; i++) {
                    String barcode = isbnShort + "-" + String.format("%03d", i);
                    BookItem copy = currentLibrarian.addBookItem(book, barcode);
                    copies.add(copy);
                    bookItems.put(barcode, copy);
                    
                    // beberapa buku (10%) ditandai sebagai referensi saja
                    if (i == numCopies && Math.random() < 0.1) {
                        copy.setReferenceOnly(true);
                    }
                    
                    // tetapkan lokasi dan harga
                    copy.setLocation("Rak " + (char)('A' + Math.random() * 6) + "-" + (int)(Math.random() * 100 + 1));
                    copy.setPrice(50000 + Math.random() * 150000);
                }
                
                bookCopies.put(book, copies);
            }
            
            // buat anggota
            List<Person> personList = new ArrayList<>();
            List<Member> memberList = new ArrayList<>();
            
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
            
            // buat anggota dari person
            for (int i = 0; i < personList.size(); i++) {
                Person person = personList.get(i);
                Member member = currentLibrarian.addMember(person);
                
                if (i < 5) { // 5 anggota reguler
                    boolean isPremium = i % 2 == 0; // setiap anggota genap adalah premium
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
            
            // buat peminjaman (setiap anggota meminjam 1-3 buku)
            List<BookLoan> loanList = new ArrayList<>();
            
            for (Member member : memberList) {
                int numLoans = (int)(Math.random() * 3) + 1; // 1-3 peminjaman
                
                for (int i = 0; i < numLoans && i < allBooks.size(); i++) {
                    Book book = allBooks.get((int)(Math.random() * allBooks.size()));
                    List<BookItem> copies = bookCopies.get(book);
                    
                    // cari salinan yang tersedia dan bisa dipinjam
                    BookItem availableCopy = null;
                    for (BookItem copy : copies) {
                        if (copy.isAvailable() && !copy.isReferenceOnly()) {
                            availableCopy = copy;
                            break;
                        }
                    }
                    
                    if (availableCopy != null) {
                        try {
                            BookLoan loan = currentLibrarian.issueBook(member, availableCopy);
                            loanList.add(loan);
                            loans.put(loan.getLoanId(), loan);
                            
                            // 10% peminjaman sudah jatuh tempo (overdue)
                            if (Math.random() < 0.1) {
                                // gunakan reflection untuk memodifikasi tanggal peminjaman menjadi lebih lama
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
                                    System.out.println("Tidak dapat memodifikasi tanggal: " + e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Gagal membuat peminjaman: " + e.getMessage());
                        }
                    }
                }
            }
            
            // Buat beberapa peminjaman sudah dikembalikan
            for (int i = 0; i < 5 && i < loanList.size(); i++) {
                try {
                    BookLoan loan = loanList.get(i);
                    currentLibrarian.returnBook(loan);
                    
                    // 50% kemungkinan ada denda
                    if (Math.random() < 0.5) {
                        double fine = 5000 + (Math.random() * 20000);
                        loan.setFine(fine);
                        loan.getMember().payFine(fine);
                    }
                } catch (Exception e) {
                    System.out.println("Gagal mengembalikan buku: " + e.getMessage());
                }
            }
            
            // buat reservasi (20% anggota melakukan reservasi)
            for (int i = 0; i < memberList.size(); i++) {
                if (Math.random() < 0.2) { // 20% probabilitas
                    Member member = memberList.get(i);
                    
                    // pilih buku secara acak yang semua salinannya sedang dipinjam
                    for (Book book : allBooks) {
                        if (book.getAvailableItems().isEmpty() && !book.getItems().isEmpty()) {
                            try {
                                Reservation reservation = member.reserveBook(book);
                                reservations.put(reservation.getReservationId(), reservation);
                                break; // satu anggota hanya membuat satu reservasi
                            } catch (Exception e) {
                                System.out.println("Gagal membuat reservasi: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            
            System.out.println("Demo mode berhasil dijalankan. Data sampel telah dibuat:");
            System.out.println("- " + allBooks.size() + " buku dengan " + bookItems.size() + " salinan");
            System.out.println("- " + categories.size() + " kategori");
            System.out.println("- " + members.size() + " anggota");
            System.out.println("- " + loans.size() + " peminjaman");
            System.out.println("- " + reservations.size() + " reservasi");
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan saat menjalankan demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Masukkan angka.");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Input tidak valid. Masukkan angka.");
            }
        }
    }
    
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}