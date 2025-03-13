package com.library.gui.utils;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Kelas utilitas untuk komponen GUI
 */
public class GUIUtils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Membuat panel dengan border dan title
     */
    public static JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title));
        return panel;
    }
    
    /**
     * Membuat panel dengan border dan title serta layout
     */
    public static JPanel createTitledPanel(String title, LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), title));
        return panel;
    }
    
    /**
     * Membuat panel dengan form layout (label di kiri, komponen di kanan)
     */
    public static JPanel createFormPanel(int rows, int cols) {
        JPanel panel = new JPanel(new GridLayout(rows, cols, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return panel;
    }
    
    /**
     * Membuat label dengan text
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }
    
    /**
     * Membuat text field dengan lebar tertentu
     */
    public static JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        return field;
    }
    
    /**
     * Membuat button dengan text dan action
     */
    public static JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }
    
    /**
     * Membuat combobox dengan array of items
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        return comboBox;
    }
    
    /**
     * Membuat panel button dengan layout FlowLayout
     */
    public static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return panel;
    }
    
    /**
     * Membuat scroll pane untuk component
     */
    public static JScrollPane createScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return scrollPane;
    }
    
    /**
     * Mengambil String dari Date dengan format standard
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Dialog untuk konfirmasi
     */
    public static boolean confirmDialog(Component parent, String message, String title) {
        int result = JOptionPane.showConfirmDialog(
            parent, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    /**
     * Dialog untuk informasi
     */
    public static void infoDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Dialog untuk error
     */
    public static void errorDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(
            parent, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Dialog untuk input text
     */
    public static String inputDialog(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Mendapatkan nilai String dari text field, empty string jika null
     */
    public static String getTextFieldValue(JTextField textField) {
        String value = textField.getText();
        return value == null ? "" : value.trim();
    }
    
    /**
     * Mendapatkan nilai int dari text field, 0 jika error
     */
    public static int getIntFromTextField(JTextField textField) {
        try {
            return Integer.parseInt(textField.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    /**
     * Mendapatkan nilai double dari text field, 0.0 jika error
     */
    public static double getDoubleFromTextField(JTextField textField) {
        try {
            return Double.parseDouble(textField.getText().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}