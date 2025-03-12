package com.library.gui;

import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Kelas utama untuk antarmuka grafis Lendify
 */
public class LendifyGUI extends JFrame {
    // Model data
    private Library library;
    private Librarian currentLibrarian;
    private List<Member> members;
    private Map<String, BookCategory> categories;
    private Map<String, Book> books;
    private Map<String, BookItem> bookItems;
    private Map<String, BookLoan> loans;
    private Map<String, Reservation> reservations;
    
    // Komponen GUI utama
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private JLabel welcomeLabel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    // Panel-panel modul
    private DashboardPanel dashboardPanel;
    private LibrarianPanel librarianPanel;
    private CategoryPanel categoryPanel;
    private BookPanel bookPanel;
    private MemberPanel memberPanel;
    private LendingPanel lendingPanel;
    private ReservationPanel reservationPanel;
    private SearchPanel searchPanel;
    private StatisticsPanel statisticsPanel;
    
    // Konstanta untuk identifikasi panel
    public static final String DASHBOARD_PANEL = "DASHBOARD";
    public static final String LIBRARIANS_PANEL = "LIBRARIANS";
    public static final String CATEGORIES_PANEL = "CATEGORIES";
    public static final String BOOKS_PANEL = "BOOKS";
    public static final String MEMBERS_PANEL = "MEMBERS";
    public static final String LENDING_PANEL = "LENDING";
    public static final String RESERVATION_PANEL = "RESERVATION";
    public static final String SEARCH_PANEL = "SEARCH";
    public static final String STATISTICS_PANEL = "STATISTICS";
    
    /**
     * Konstruktor untuk GUI
     */
    public LendifyGUI(Library library, Librarian currentLibrarian, List<Member> members,
                      Map<String, BookCategory> categories, Map<String, Book> books,
                      Map<String, BookItem> bookItems, Map<String, BookLoan> loans,
                      Map<String, Reservation> reservations) {
        this.library = library;
        this.currentLibrarian = currentLibrarian;
        this.members = members;
        this.categories = categories;
        this.books = books;
        this.bookItems = bookItems;
        this.loans = loans;
        this.reservations = reservations;
        
        initializeGUI();
        createPanels();
    }
    
    /**
     * Inisialisasi komponen GUI
     */
    private void initializeGUI() {
        setTitle("Lendify - Sistem Manajemen Perpustakaan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Main layout setup
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        
        // Membuat panel header
        JPanel headerPanel = createHeaderPanel();
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Membuat panel menu di sebelah kiri
        menuPanel = createMenuPanel();
        contentPane.add(menuPanel, BorderLayout.WEST);
        
        // Membuat panel utama dengan CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // Menambahkan footer panel
        JPanel footerPanel = createFooterPanel();
        contentPane.add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Membuat panel-panel untuk setiap modul
     */
    private void createPanels() {
        // Inisialisasi panel-panel
        dashboardPanel = new DashboardPanel(this);
        librarianPanel = new LibrarianPanel(this);
        categoryPanel = new CategoryPanel(this);
        bookPanel = new BookPanel(this);
        memberPanel = new MemberPanel(this);
        lendingPanel = new LendingPanel(this);
        reservationPanel = new ReservationPanel(this);
        searchPanel = new SearchPanel(this);
        statisticsPanel = new StatisticsPanel(this);
        
        // Tambahkan panel-panel ke mainPanel
        mainPanel.add(dashboardPanel, DASHBOARD_PANEL);
        mainPanel.add(librarianPanel, LIBRARIANS_PANEL);
        mainPanel.add(categoryPanel, CATEGORIES_PANEL);
        mainPanel.add(bookPanel, BOOKS_PANEL);
        mainPanel.add(memberPanel, MEMBERS_PANEL);
        mainPanel.add(lendingPanel, LENDING_PANEL);
        mainPanel.add(reservationPanel, RESERVATION_PANEL);
        mainPanel.add(searchPanel, SEARCH_PANEL);
        mainPanel.add(statisticsPanel, STATISTICS_PANEL);
        
        // Tampilkan dashboard secara default
        cardLayout.show(mainPanel, DASHBOARD_PANEL);
    }
    
    /**
     * Membuat header panel
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel titleLabel = new JLabel("LENDIFY - SISTEM MANAJEMEN PERPUSTAKAAN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        welcomeLabel = new JLabel("Selamat datang, " + currentLibrarian.getName());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.WHITE);
        rightPanel.add(welcomeLabel);
        
        JButton logoutButton = new JButton("Keluar");
        logoutButton.setBackground(new Color(231, 76, 60));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                    "Apakah Anda yakin ingin keluar dari sistem?", 
                    "Konfirmasi Keluar", 
                    JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });
        rightPanel.add(logoutButton);
        
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Membuat menu panel
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(52, 73, 94));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Tambahkan tombol-tombol menu
        addMenuButton(panel, "Dashboard", "dashboard.png", DASHBOARD_PANEL);
        addMenuButton(panel, "Kelola Pustakawan", "librarian.png", LIBRARIANS_PANEL);
        addMenuButton(panel, "Kelola Kategori", "category.png", CATEGORIES_PANEL);
        addMenuButton(panel, "Kelola Buku", "book.png", BOOKS_PANEL);
        addMenuButton(panel, "Kelola Anggota", "member.png", MEMBERS_PANEL);
        addMenuButton(panel, "Peminjaman & Pengembalian", "lending.png", LENDING_PANEL);
        addMenuButton(panel, "Kelola Reservasi", "reservation.png", RESERVATION_PANEL);
        addMenuButton(panel, "Cari Buku", "search.png", SEARCH_PANEL);
        addMenuButton(panel, "Statistik", "statistics.png", STATISTICS_PANEL);
        
        // Tambahkan panel kosong untuk mendorong tombol "Terminal" ke bawah
        panel.add(Box.createVerticalGlue());
        
        // Tombol untuk kembali ke Terminal Mode
        JButton terminalButton = new JButton("Kembali ke Terminal Mode");
        terminalButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        terminalButton.setForeground(Color.WHITE);
        terminalButton.setBackground(new Color(192, 57, 43));
        terminalButton.setFocusPainted(false);
        terminalButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        terminalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        terminalButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Apakah Anda yakin ingin kembali ke mode Terminal?",
                    "Konfirmasi Mode Terminal",
                    JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose(); // Tutup jendela GUI
                
                // Restart aplikasi dalam mode terminal
                try {
                    // Menjalankan main program lagi dengan parameter khusus
                    ProcessBuilder pb = new ProcessBuilder("java", "-cp", System.getProperty("java.class.path"), 
                            "com.library.Main", "terminal");
                    pb.inheritIO();
                    pb.start();
                    
                    System.exit(0);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                            "Gagal membuka mode terminal: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(terminalButton);
        panel.add(Box.createVerticalStrut(20));
        
        return panel;
    }
    
    /**
     * Helper method untuk menambahkan tombol menu
     */
    private void addMenuButton(JPanel panel, String text, String iconName, String panelName) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(52, 73, 94));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Load icon jika tersedia
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/" + iconName));
            if (icon.getIconWidth() > 0) {
                button.setIcon(icon);
                button.setIconTextGap(10);
            }
        } catch (Exception e) {
            // Icon tidak tersedia, lanjutkan tanpa icon
        }
        
        button.addActionListener(e -> cardLayout.show(mainPanel, panelName));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 73, 94));
            }
        });
        
        panel.add(button);
        panel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Membuat panel footer
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));
        panel.setBackground(new Color(236, 240, 241));
        
        JLabel footerLabel = new JLabel("© 2024 Lendify - Sistem Manajemen Perpustakaan");
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(footerLabel, BorderLayout.WEST);
        
        JLabel versionLabel = new JLabel("Versi 1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(versionLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Akses ke Library
     */
    public Library getLibrary() {
        return library;
    }
    
    /**
     * Akses ke Librarian saat ini
     */
    public Librarian getCurrentLibrarian() {
        return currentLibrarian;
    }
    
    /**
     * Akses ke daftar Member
     */
    public List<Member> getMembers() {
        return members;
    }
    
    /**
     * Akses ke map Category
     */
    public Map<String, BookCategory> getCategories() {
        return categories;
    }
    
    /**
     * Akses ke map Book
     */
    public Map<String, Book> getBooks() {
        return books;
    }
    
    /**
     * Akses ke map BookItem
     */
    public Map<String, BookItem> getBookItems() {
        return bookItems;
    }
    
    /**
     * Akses ke map Loan
     */
    public Map<String, BookLoan> getLoans() {
        return loans;
    }
    
    /**
     * Akses ke map Reservation
     */
    public Map<String, Reservation> getReservations() {
        return reservations;
    }
    
    /**
     * Akses ke DateFormat
     */
    public SimpleDateFormat getDateFormat() {
        return dateFormat;
    }
    
    /**
     * Menampilkan panel tertentu
     */
    public void showPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }
    
    /**
     * Mendapatkan panel utama
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }
    
    /**
     * Mendapatkan card layout
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }
}