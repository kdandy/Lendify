package com.library.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;

import com.library.enums.*;
import com.library.model.*;

/**
 * Kelas abstrak dasar untuk semua panel dalam aplikasi
 */
public abstract class BasePanel extends JPanel {
    protected LendifyGUI parentFrame;
    
    // Akses ke data aplikasi melalui parent frame
    protected Library library;
    protected Librarian currentLibrarian;
    protected SimpleDateFormat dateFormat;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public BasePanel(LendifyGUI parentFrame) {
        this.parentFrame = parentFrame;
        this.library = parentFrame.getLibrary();
        this.currentLibrarian = parentFrame.getCurrentLibrarian();
        this.dateFormat = parentFrame.getDateFormat();
        
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }
    
    /**
     * Method yang harus diimplementasikan oleh subclass untuk inisialisasi komponen
     */
    protected abstract void initComponents();
    
    /**
     * Membuat label judul untuk panel
     * @param title Judul panel
     * @return Label judul yang sudah diformat
     */
    protected JLabel createTitleLabel(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        return titleLabel;
    }
    
    /**
     * Menemukan pustakawan berdasarkan ID Staff
     * @param staffId ID Staff pustakawan
     * @return Pustakawan yang ditemukan atau null jika tidak ada
     */
    protected Librarian findLibrarianByStaffId(String staffId) {
        for (Librarian librarian : library.getLibrarians()) {
            if (librarian.getStaffId().equals(staffId)) {
                return librarian;
            }
        }
        return null;
    }
    
    /**
     * Menemukan anggota berdasarkan ID
     * @param memberId ID anggota
     * @return Anggota yang ditemukan atau null jika tidak ada
     */
    protected Member findMemberById(String memberId) {
        for (Member member : parentFrame.getMembers()) {
            if (member.getMemberId().equals(memberId)) {
                return member;
            }
        }
        return null;
    }
    
    /**
     * Menampilkan pesan error
     * @param message Pesan error
     * @param title Judul dialog
     */
    protected void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Menampilkan pesan informasi
     * @param message Pesan informasi
     * @param title Judul dialog
     */
    protected void showInfoMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Menampilkan dialog konfirmasi
     * @param message Pesan konfirmasi
     * @param title Judul dialog
     * @return true jika user memilih Yes, false jika No
     */
    protected boolean showConfirmDialog(String message, String title) {
        return JOptionPane.showConfirmDialog(this, message, title, 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    /**
     * Membuat tombol dengan style tertentu
     * @param text Teks pada tombol
     * @param color Warna latar tombol
     * @return Tombol yang sudah diformat
     */
    protected JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        return button;
    }
    
    /**
     * Membuat panel dengan border layout dan title
     * @param title Judul panel
     * @return Panel yang sudah diformat
     */
    protected JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }
}