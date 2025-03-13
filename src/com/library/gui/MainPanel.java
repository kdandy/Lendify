package com.library.gui;

import com.library.gui.utils.GUIUtils;
import com.library.model.Library;
import com.library.model.Librarian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel utama untuk aplikasi Lendify setelah login
 */
public class MainPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JPanel welcomePanel;
    private JPanel menuPanel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss");
    
    /**
     * Constructor untuk MainPanel
     */
    public MainPanel(LendifyGUI mainWindow) {
        this.mainWindow = mainWindow;
        setupUI();
    }
    
    /**
     * Setup komponen UI
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel selamat datang
        welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);
        
        // Panel menu utama
        menuPanel = createMenuPanel();
        add(menuPanel, BorderLayout.CENTER);
        
        // Panel footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Membuat panel selamat datang dengan informasi pengguna
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Judul dan logo
        JPanel titlePanel = new JPanel(new BorderLayout(10, 0));
        
        // Tambahkan logo UNDIP di sebelah kiri judul
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/com/icons/UNDIPOfficial.png"));
            if (logoIcon.getIconWidth() > 0) {
                // Resize logo agar sesuai
                Image img = logoIcon.getImage();
                Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                logoIcon = new ImageIcon(scaledImg);
                
                JLabel logoLabel = new JLabel(logoIcon);
                logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                titlePanel.add(logoLabel, BorderLayout.WEST);
            }
        } catch (Exception e) {
            System.out.println("Logo UNDIP tidak ditemukan: " + e.getMessage());
        }
        
        JLabel titleLabel = new JLabel("SISTEM MANAJEMEN PERPUSTAKAAN LENDIFY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Informasi perpustakaan dan pustakawan
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        Library library = mainWindow.getLibrary();
        Librarian librarian = mainWindow.getCurrentLibrarian();
        
        infoPanel.add(new JLabel("Perpustakaan:", SwingConstants.RIGHT));
        infoPanel.add(new JLabel(library.getName()));
        
        infoPanel.add(new JLabel("Alamat:", SwingConstants.RIGHT));
        infoPanel.add(new JLabel(library.getAddress()));
        
        infoPanel.add(new JLabel("Pustakawan:", SwingConstants.RIGHT));
        infoPanel.add(new JLabel(librarian.getName() + " (" + librarian.getPosition() + ")"));
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        // Tanggal dan jam
        JLabel dateLabel = new JLabel(dateFormat.format(new Date()), SwingConstants.RIGHT);
        panel.add(dateLabel, BorderLayout.SOUTH);
        
        // Update waktu setiap detik
        Timer timer = new Timer(1000, e -> dateLabel.setText(dateFormat.format(new Date())));
        timer.start();
        
        return panel;
    }
    
    /**
     * Membuat panel menu utama dengan tombol-tombol
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 3, 15, 15)); // 4 baris, 3 kolom (total 12 slot)
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Tombol-tombol menu
        JButton searchButton = createMenuButton("Cari Buku", "search.png", e -> mainWindow.showSearchPanel());
        JButton librarianButton = createMenuButton("Kelola Pustakawan", "role-2.png", e -> mainWindow.showLibrarianPanel());
        JButton categoryButton = createMenuButton("Kelola Kategori", "kategori.png", e -> mainWindow.showCategoryPanel());
        JButton bookButton = createMenuButton("Kelola Buku", "book.png", e -> mainWindow.showBookPanel());
        JButton memberButton = createMenuButton("Kelola Anggota", "user.png", e -> mainWindow.showMemberPanel());
        JButton loanButton = createMenuButton("Peminjaman & Pengembalian", "pinjaman.png", e -> mainWindow.showLoanPanel());
        JButton reservationButton = createMenuButton("Kelola Reservasi", "reservasi.png", e -> mainWindow.showReservationPanel());
        JButton statisticsButton = createMenuButton("Statistik Perpustakaan", "line.png", e -> mainWindow.showStatisticsPanel());
        JButton demoButton = createMenuButton("Jalankan Demo", "demo.png", e -> mainWindow.runDemoMode());
        JButton exitButton = createMenuButton("Keluar", "exit.png", e -> System.exit(0));
        
        // Tambahkan tombol ke panel
        panel.add(librarianButton);
        panel.add(searchButton);
        panel.add(categoryButton);
        panel.add(bookButton);
        panel.add(memberButton);
        panel.add(loanButton);
        panel.add(reservationButton);
        panel.add(statisticsButton);
        panel.add(demoButton);
        panel.add(exitButton);
        // Dua slot kosong
        panel.add(new JLabel());
        panel.add(new JLabel());
        
        return panel;
    }
    
    /**
     * Membuat tombol menu dengan ikon dan aksi
     */
    private JButton createMenuButton(String text, String iconName, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setMargin(new Insets(10, 10, 10, 10));
        button.setFocusPainted(false);
        
        // Coba load icon jika ada
        try {
            System.out.println("Mencoba memuat ikon: " + "/icons/" + iconName);
            URL iconURL = getClass().getResource("/com/icons/" + iconName);
            System.out.println("URL ikon: " + iconURL);
            
            if (iconURL != null) {
                ImageIcon icon = new ImageIcon(iconURL);
                if (icon.getIconWidth() > 0) {
                    // Sesuaikan ukuran gambar
                    Image img = icon.getImage();
                    Image scaledImg = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(scaledImg);
                    
                    button.setIcon(icon);
                    button.setHorizontalTextPosition(SwingConstants.CENTER);
                    button.setVerticalTextPosition(SwingConstants.BOTTOM);
                }
            } else {
                System.out.println("Ikon tidak ditemukan di classpath");
            }
        } catch (Exception e) {
            System.out.println("Error saat memuat ikon: " + e.getMessage());
            e.printStackTrace();
        }
        
        button.addActionListener(action);
        button.setPreferredSize(new Dimension(120, 100)); // Ukuran tombol yang konsisten
        
        return button;
    }
    
    /**
     * Membuat panel footer
     */
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JLabel footerLabel = new JLabel("© 2025 Lendify - Sistem Manajemen Perpustakaan Pemrograman Berorientasi Objek", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        
        panel.add(footerLabel, BorderLayout.CENTER);
        
        return panel;
    }
}