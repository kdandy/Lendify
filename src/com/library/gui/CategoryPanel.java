package com.library.gui;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.awt.Dialog.ModalityType;
import java.util.List;
import java.util.ArrayList;

public class CategoryPanel extends BasePanel {
    private DefaultTableModel categoryModel;
    private JTable categoriesTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public CategoryPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }

    @Override
    protected void initComponents() {
        JLabel titleLabel = createTitleLabel("Kelola Kategori Buku");
        add(titleLabel, BorderLayout.NORTH);

        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton = createStyledButton("Tambah Kategori", new Color(39, 174, 96));
        addButton.addActionListener(e -> showAddCategoryDialog());

        editButton = createStyledButton("Edit Kategori", new Color(41, 128, 185));
        editButton.setEnabled(false); // Disabled initially
        editButton.addActionListener(e -> editSelectedCategory());

        deleteButton = createStyledButton("Hapus Kategori", new Color(231, 76, 60));
        deleteButton.setEnabled(false); // Disabled initially
        deleteButton.addActionListener(e -> deleteSelectedCategory());

        // Check permissions and enable buttons accordingly
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        toolsPanel.add(addButton);
        toolsPanel.add(editButton);
        toolsPanel.add(deleteButton);

        add(toolsPanel, BorderLayout.CENTER);

        // Table to display categories
        String[] columnNames = {"Nama Kategori", "Deskripsi", "Jumlah Buku"};
        categoryModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is not editable
            }
        };

        refreshCategoriesTable();

        categoriesTable = new JTable(categoryModel);
        categoriesTable.setFillsViewportHeight(true);
        categoriesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        categoriesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && categoriesTable.getSelectedRow() != -1) {
                    boolean hasPermission = currentLibrarian.getPermission() != LibrarianPermission.BASIC;
                    boolean isAdmin = currentLibrarian.getPermission() == LibrarianPermission.ADMIN;

                    editButton.setEnabled(hasPermission);
                    deleteButton.setEnabled(isAdmin);
                } else {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(categoriesTable);
        add(scrollPane, BorderLayout.SOUTH);
    }

    private void refreshCategoriesTable() {
        categoryModel.setRowCount(0); // Clear previous rows

        for (BookCategory category : library.getCollection().getCategories()) {
            categoryModel.addRow(new Object[]{
                    category.getName(),
                    category.getDescription(),
                    countBooksInCategory(category)
            });
        }
    }

    private int countBooksInCategory(BookCategory category) {
        int count = 0;
        for (Book book : library.getCollection().getBooks()) {
            if (book.getCategories().contains(category)) {
                count++;
            }
        }
        return count;
    }

    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tambah Kategori Baru", ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nama Kategori:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Deskripsi:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextArea descriptionArea = new JTextArea(5, 15);
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (nameField.getText().isEmpty()) {
                    showErrorMessage("Nama kategori harus diisi!", "Error");
                    return;
                }

                // Check for duplicate category name
                Map<String, BookCategory> categories = parentFrame.getCategories();
                if (categories.containsKey(nameField.getText().trim())) {
                    showErrorMessage("Kategori dengan nama tersebut sudah ada!", "Error");
                    return;
                }

                // Create new category
                BookCategory category = new BookCategory(
                        nameField.getText().trim(),
                        descriptionArea.getText().trim()
                );

                // Add category to library
                library.addCategory(category);
                categories.put(category.getName(), category);

                dialog.dispose();
                showInfoMessage("Kategori berhasil ditambahkan!", "Sukses");

                // Refresh category table
                refreshCategoriesTable();

            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void editSelectedCategory() {
        int row = categoriesTable.getSelectedRow();
        if (row != -1) {
            String categoryName = (String) categoriesTable.getValueAt(row, 0);
            BookCategory selectedCategory = parentFrame.getCategories().get(categoryName);
            if (selectedCategory != null) {
                showEditCategoryDialog(selectedCategory);
            }
        }
    }

    private void deleteSelectedCategory() {
        int row = categoriesTable.getSelectedRow();
        if (row != -1) {
            String categoryName = (String) categoriesTable.getValueAt(row, 0);
            BookCategory selectedCategory = parentFrame.getCategories().get(categoryName);

            if (selectedCategory != null) {
                boolean confirm = showConfirmDialog(
                        "Apakah Anda yakin ingin menghapus kategori " + selectedCategory.getName() + "?",
                        "Konfirmasi Hapus");

                if (confirm) {
                    library.getCollection().removeCategory(selectedCategory);
                    parentFrame.getCategories().remove(selectedCategory.getName());
                    refreshCategoriesTable();
                    showInfoMessage("Kategori berhasil dihapus.", "Sukses");
                }
            }
        }
    }

    private void showEditCategoryDialog(BookCategory category) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Kategori", ModalityType.APPLICATION_MODAL);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Nama Kategori:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(category.getName(), 15);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Deskripsi:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextArea descriptionArea = new JTextArea(category.getDescription(), 5, 15);
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        panel.add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (nameField.getText().isEmpty()) {
                    showErrorMessage("Nama kategori harus diisi!", "Error");
                    return;
                }

                // Check for duplicate category name if changed
                String oldName = category.getName();
                String newName = nameField.getText().trim();
                Map<String, BookCategory> categories = parentFrame.getCategories();

                if (!oldName.equals(newName) && categories.containsKey(newName)) {
                    showErrorMessage("Kategori dengan nama tersebut sudah ada!", "Error");
                    return;
                }

                // Update category
                if (!oldName.equals(newName)) {
                    categories.remove(oldName);
                    category.setName(newName);
                    categories.put(newName, category);
                }

                category.setDescription(descriptionArea.getText().trim());

                dialog.dispose();
                showInfoMessage("Kategori berhasil diupdate!", "Sukses");

                // Refresh category table
                refreshCategoriesTable();

            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }
}