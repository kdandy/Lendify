package com.library.gui;

import com.library.gui.utils.GUIUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel login untuk aplikasi Lendify
 */
public class LoginPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final String SYSTEM_PASSWORD = "lendify";
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    
    private LendifyGUI mainWindow;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private JLabel messageLabel;
    private int loginAttempts = 0;
    
    /**
     * Constructor untuk LoginPanel
     */
    public LoginPanel(LendifyGUI mainWindow) {
        this.mainWindow = mainWindow;
        setupUI();
    }
    
    /**
     * Setup komponen UI
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        
        // Panel utama
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Logo dan judul
        JLabel titleLabel = new JLabel("LENDIFY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Sistem Manajemen Perpustakaan", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form login
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        loginPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Password field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel passwordLabel = new JLabel("Password: ");
        passwordField = new JPasswordField(20);
        passwordField.addActionListener(e -> attemptLogin());
        
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        
        // Message label
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loginButton = new JButton("Login");
        exitButton = new JButton("Keluar");
        
        loginButton.addActionListener(e -> attemptLogin());
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        // Tambahkan komponen ke panel
        loginPanel.add(passwordPanel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(messageLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(buttonPanel);
        
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        mainPanel.add(loginPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Coba login dengan password yang diinput
     */
    private void attemptLogin() {
        String password = new String(passwordField.getPassword());
        
        if (password.equals(SYSTEM_PASSWORD)) {
            messageLabel.setText("");
            mainWindow.showMainPanel();
        } else {
            loginAttempts++;
            int remainingAttempts = MAX_LOGIN_ATTEMPTS - loginAttempts;
            
            if (remainingAttempts > 0) {
                messageLabel.setText("Password salah. Sisa percobaan: " + remainingAttempts);
            } else {
                messageLabel.setText("Terlalu banyak percobaan gagal. Program akan ditutup.");
                loginButton.setEnabled(false);
                Timer timer = new Timer(2000, e -> System.exit(0));
                timer.setRepeats(false);
                timer.start();
            }
        }
        
        passwordField.setText("");
    }
    
    /**
     * Reset login attempts counter
     */
    public void resetLoginAttempts() {
        loginAttempts = 0;
        messageLabel.setText("");
        loginButton.setEnabled(true);
    }
}