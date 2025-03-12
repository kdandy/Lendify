package com.library.gui;

import com.library.enums.*;
import com.library.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel untuk manajemen anggota perpustakaan
 */
public class MemberPanel extends BasePanel {
    private DefaultTableModel memberModel;
    private JTable membersTable;
    private JButton addButton;
    private JButton editButton;
    private JButton detailButton;
    private JButton renewButton;
    private JButton toggleButton;
    private JTextField searchField;

    /**
     * Konstruktor
     * @param parentFrame Frame parent (LendifyGUI)
     */
    public MemberPanel(LendifyGUI parentFrame) {
        super(parentFrame);
    }
    
    @Override
    protected void initComponents() {
        // Tambahkan judul panel
        JLabel titleLabel = createTitleLabel("Kelola Anggota");
        add(titleLabel, BorderLayout.NORTH);
        
        // Panel tombol aksi
        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = createStyledButton("Tambah Anggota", new Color(39, 174, 96));
        addButton.addActionListener(e -> showAddMemberDialog());
        
        editButton = createStyledButton("Edit Anggota", new Color(243, 156, 18));
        editButton.setEnabled(false);
        editButton.addActionListener(e -> editSelectedMember());
        
        detailButton = createStyledButton("Lihat Detail", new Color(52, 152, 219));
        detailButton.setEnabled(false);
        detailButton.addActionListener(e -> viewSelectedMemberDetails());
        
        renewButton = createStyledButton("Perpanjang Keanggotaan", new Color(41, 128, 185));
        renewButton.setEnabled(false);
        renewButton.addActionListener(e -> renewSelectedMembership());
        
        toggleButton = createStyledButton("Aktifkan/Nonaktifkan", new Color(231, 76, 60));
        toggleButton.setEnabled(false);
        toggleButton.addActionListener(e -> toggleSelectedMemberStatus());
        
        // Sesuaikan status tombol berdasarkan izin pustakawan saat ini
        if (currentLibrarian.getPermission() == LibrarianPermission.BASIC) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            renewButton.setEnabled(false);
            toggleButton.setEnabled(false);
        }
        
        toolsPanel.add(addButton);
        toolsPanel.add(editButton);
        toolsPanel.add(detailButton);
        toolsPanel.add(renewButton);
        toolsPanel.add(toggleButton);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        searchField = new JTextField(20);
        JButton searchButton = createStyledButton("Cari", new Color(52, 152, 219));
        searchButton.addActionListener(e -> searchMembers());
        
        searchPanel.add(new JLabel("Cari:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(toolsPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.CENTER);
        
        // Tabel anggota
        String[] columnNames = {"ID Anggota", "Nama", "Telepon", "Email", "Jenis", "Status", "Exp. Date"};
        memberModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable table
            }
        };
        
        // Isi data anggota
        refreshMembersTable(parentFrame.getMembers());
        
        membersTable = new JTable(memberModel);
        membersTable.setFillsViewportHeight(true);
        membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Listener untuk mengaktifkan tombol-tombol aksi
        membersTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting() && membersTable.getSelectedRow() != -1) {
                    boolean hasPermission = currentLibrarian.getPermission() != LibrarianPermission.BASIC;
                    
                    editButton.setEnabled(hasPermission);
                    detailButton.setEnabled(true); // Semua bisa lihat detail
                    renewButton.setEnabled(hasPermission);
                    toggleButton.setEnabled(hasPermission);
                } else {
                    editButton.setEnabled(false);
                    detailButton.setEnabled(false);
                    renewButton.setEnabled(false);
                    toggleButton.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(membersTable);
        add(scrollPane, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh tabel anggota
     * @param memberList Daftar anggota yang akan ditampilkan
     */
    private void refreshMembersTable(List<Member> memberList) {
        memberModel.setRowCount(0); // Clear table
        
        for (Member member : memberList) {
            String memberType = "Reguler";
            
            if (member instanceof StudentMember) {
                memberType = "Mahasiswa";
            } else if (member instanceof RegularMember) {
                memberType = ((RegularMember) member).isPremium() ? "Premium" : "Reguler";
            }
            
            memberModel.addRow(new Object[]{
                member.getMemberId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getEmail(),
                memberType,
                member.getStatus(),
                dateFormat.format(member.getExpiryDate())
            });
        }
    }
    
    /**
     * Mencari anggota berdasarkan kata kunci
     */
    private void searchMembers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshMembersTable(parentFrame.getMembers());
            return;
        }
        
        List<Member> results = new ArrayList<>();
        for (Member member : parentFrame.getMembers()) {
            if (member.getName().toLowerCase().contains(keyword) ||
                member.getMemberId().toLowerCase().contains(keyword) ||
                member.getEmail().toLowerCase().contains(keyword)) {
                results.add(member);
            }
        }
        
        refreshMembersTable(results);
    }
    
    /**
     * Menampilkan dialog untuk menambah anggota baru
     */
    private void showAddMemberDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Tambah Anggota Baru", true);
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Jenis anggota
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Jenis Anggota:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        String[] memberTypes = {"Mahasiswa", "Reguler"};
        JComboBox<String> typeCombo = new JComboBox<>(memberTypes);
        panel.add(typeCombo, gbc);
        
        // Person info
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("ID Person:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField idField = new JTextField(15);
        panel.add(idField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Nama:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField nameField = new JTextField(15);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField addressField = new JTextField(15);
        panel.add(addressField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("No. Telepon:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField phoneField = new JTextField(15);
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField emailField = new JTextField(15);
        panel.add(emailField, gbc);
        
        // Panel untuk info khusus mahasiswa
        JPanel studentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints studentGbc = new GridBagConstraints();
        studentGbc.fill = GridBagConstraints.HORIZONTAL;
        studentGbc.insets = new Insets(5, 5, 5, 5);
        
        studentGbc.gridx = 0;
        studentGbc.gridy = 0;
        studentPanel.add(new JLabel("ID Mahasiswa:"), studentGbc);
        
        studentGbc.gridx = 1;
        studentGbc.gridy = 0;
        JTextField studentIdField = new JTextField(15);
        studentPanel.add(studentIdField, studentGbc);
        
        studentGbc.gridx = 0;
        studentGbc.gridy = 1;
        studentPanel.add(new JLabel("Fakultas:"), studentGbc);
        
        studentGbc.gridx = 1;
        studentGbc.gridy = 1;
        JTextField facultyField = new JTextField(15);
        studentPanel.add(facultyField, studentGbc);
        
        studentGbc.gridx = 0;
        studentGbc.gridy = 2;
        studentPanel.add(new JLabel("Jurusan:"), studentGbc);
        
        studentGbc.gridx = 1;
        studentGbc.gridy = 2;
        JTextField departmentField = new JTextField(15);
        studentPanel.add(departmentField, studentGbc);
        
        studentGbc.gridx = 0;
        studentGbc.gridy = 3;
        studentPanel.add(new JLabel("Tahun Studi:"), studentGbc);
        
        studentGbc.gridx = 1;
        studentGbc.gridy = 3;
        JTextField yearOfStudyField = new JTextField(15);
        studentPanel.add(yearOfStudyField, studentGbc);
        
        // Panel untuk info khusus anggota reguler
        JPanel regularPanel = new JPanel(new GridBagLayout());
        GridBagConstraints regularGbc = new GridBagConstraints();
        regularGbc.fill = GridBagConstraints.HORIZONTAL;
        regularGbc.insets = new Insets(5, 5, 5, 5);
        
        regularGbc.gridx = 0;
        regularGbc.gridy = 0;
        regularPanel.add(new JLabel("Pekerjaan:"), regularGbc);
        
        regularGbc.gridx = 1;
        regularGbc.gridy = 0;
        JTextField occupationField = new JTextField(15);
        regularPanel.add(occupationField, regularGbc);
        
        regularGbc.gridx = 0;
        regularGbc.gridy = 1;
        regularPanel.add(new JLabel("Perusahaan/Institusi:"), regularGbc);
        
        regularGbc.gridx = 1;
        regularGbc.gridy = 1;
        JTextField employerField = new JTextField(15);
        regularPanel.add(employerField, regularGbc);
        
        regularGbc.gridx = 0;
        regularGbc.gridy = 2;
        regularPanel.add(new JLabel("Premium:"), regularGbc);
        
        regularGbc.gridx = 1;
        regularGbc.gridy = 2;
        JCheckBox premiumCheck = new JCheckBox();
        regularPanel.add(premiumCheck, regularGbc);
        
        // Panel kartu untuk menampilkan panel spesifik berdasarkan jenis anggota
        final JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.add(studentPanel, "STUDENT");
        cardPanel.add(regularPanel, "REGULAR");
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        panel.add(cardPanel, gbc);
        
        // Set default ke REGULAR
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "REGULAR");
        
        // Listener untuk mengubah panel berdasarkan jenis anggota yang dipilih
        typeCombo.addActionListener(e -> {
            String selectedType = (String) typeCombo.getSelectedItem();
            if (selectedType.equals("Mahasiswa")) {
                ((CardLayout) cardPanel.getLayout()).show(cardPanel, "STUDENT");
            } else {
                ((CardLayout) cardPanel.getLayout()).show(cardPanel, "REGULAR");
            }
        });
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
        saveButton.addActionListener(e -> {
            try {
                // Validasi input
                if (idField.getText().isEmpty() || nameField.getText().isEmpty()) {
                    showErrorMessage("ID dan Nama harus diisi!", "Error");
                    return;
                }
                
                // Buat Person
                Person person = new Person(
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    phoneField.getText().trim()
                );
                person.setEmail(emailField.getText().trim());
                
                // Buat Member berdasarkan jenis yang dipilih
                Member member = currentLibrarian.addMember(person);
                
                if (typeCombo.getSelectedItem().equals("Mahasiswa")) {
                    // Validasi input mahasiswa
                    if (studentIdField.getText().isEmpty() || facultyField.getText().isEmpty() || 
                        departmentField.getText().isEmpty() || yearOfStudyField.getText().isEmpty()) {
                        showErrorMessage("Semua field mahasiswa harus diisi!", "Error");
                        return;
                    }
                    
                    // Parse year of study
                    int yearOfStudy;
                    try {
                        yearOfStudy = Integer.parseInt(yearOfStudyField.getText().trim());
                    } catch (NumberFormatException ex) {
                        showErrorMessage("Tahun studi harus berupa angka!", "Error");
                        return;
                    }
                    
                    // Buat StudentMember
                    StudentMember studentMember = new StudentMember(
                        member,
                        studentIdField.getText().trim(),
                        facultyField.getText().trim(),
                        departmentField.getText().trim(),
                        yearOfStudy
                    );
                    parentFrame.getMembers().add(studentMember);
                    
                    showInfoMessage("Anggota mahasiswa berhasil ditambahkan!", "Sukses");
                } else {
                    // Anggota reguler
                    RegularMember regularMember = new RegularMember(
                        member,
                        occupationField.getText().trim(),
                        employerField.getText().trim(),
                        premiumCheck.isSelected()
                    );
                    parentFrame.getMembers().add(regularMember);
                    
                    showInfoMessage("Anggota reguler berhasil ditambahkan!", "Sukses");
                }
                
                dialog.dispose();
                
                // Refresh tabel anggota
                refreshMembersTable(parentFrame.getMembers());
                
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(buttonPanel, gbc);
        
        JScrollPane dialogScrollPane = new JScrollPane(panel);
        dialog.add(dialogScrollPane);
        dialog.setVisible(true);
    }
    
    /**
     * Edit anggota yang dipilih
     */
    private void editSelectedMember() {
        int row = membersTable.getSelectedRow();
        if (row != -1) {
            String memberId = (String) membersTable.getValueAt(row, 0);
            Member selectedMember = findMemberById(memberId);
            if (selectedMember != null) {
                showEditMemberDialog(selectedMember);
            }
        }
    }
    
    /**
     * Lihat detail anggota yang dipilih
     */
    private void viewSelectedMemberDetails() {
        int row = membersTable.getSelectedRow();
        if (row != -1) {
            String memberId = (String) membersTable.getValueAt(row, 0);
            Member selectedMember = findMemberById(memberId);
            if (selectedMember != null) {
                showMemberDetailsDialog(selectedMember);
            }
        }
    }
    
    /**
     * Perpanjang keanggotaan anggota yang dipilih
     */
    private void renewSelectedMembership() {
        int row = membersTable.getSelectedRow();
        if (row != -1) {
            String memberId = (String) membersTable.getValueAt(row, 0);
            Member selectedMember = findMemberById(memberId);
            if (selectedMember != null) {
                showRenewMembershipDialog(selectedMember);
            }
        }
    }
    
    /**
     * Toggle status anggota yang dipilih
     */
    private void toggleSelectedMemberStatus() {
        int row = membersTable.getSelectedRow();
        if (row != -1) {
            String memberId = (String) membersTable.getValueAt(row, 0);
            Member selectedMember = findMemberById(memberId);
            
            if (selectedMember != null) {
                boolean isActive = selectedMember.isActive();
                String action = isActive ? "nonaktifkan" : "aktifkan";
                
                boolean confirm = showConfirmDialog(
                    "Apakah Anda yakin ingin " + action + " anggota " + selectedMember.getName() + "?",
                    "Konfirmasi");
                
                if (confirm) {
                    selectedMember.setActive(!isActive);
                    // Menggunakan enum MemberStatus yang benar
                    selectedMember.setStatus(isActive ? MemberStatus.INACTIVE : MemberStatus.ACTIVE);
                    
                    // Refresh tabel
                    refreshMembersTable(parentFrame.getMembers());
                    showInfoMessage("Status anggota berhasil diubah.", "Sukses");
                }
            }
        }
    }
    
    /**
     * Menampilkan dialog untuk mengedit anggota
     * @param member Anggota yang akan diedit
     */
    private void showEditMemberDialog(Member member) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Anggota", true);
        dialog.setSize(500, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Member info
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Anggota:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField memberIdField = new JTextField(member.getMemberId(), 15);
        memberIdField.setEditable(false); // ID tidak bisa diubah
        panel.add(memberIdField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nama:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField nameField = new JTextField(member.getName(), 15);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Alamat:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField addressField = new JTextField(member.getAddress(), 15);
        panel.add(addressField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("No. Telepon:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField phoneField = new JTextField(member.getPhoneNumber(), 15);
        panel.add(phoneField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField emailField = new JTextField(member.getEmail(), 15);
        panel.add(emailField, gbc);
        
        // Fields khusus berdasarkan jenis anggota
        if (member instanceof StudentMember) {
            StudentMember studentMember = (StudentMember) member;
            
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel.add(new JLabel("ID Mahasiswa:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 5;
            JTextField studentIdField = new JTextField(studentMember.getStudentId(), 15);
            panel.add(studentIdField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 6;
            panel.add(new JLabel("Fakultas:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 6;
            JTextField facultyField = new JTextField(studentMember.getFaculty(), 15);
            panel.add(facultyField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 7;
            panel.add(new JLabel("Jurusan:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 7;
            JTextField departmentField = new JTextField(studentMember.getDepartment(), 15);
            panel.add(departmentField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 8;
            panel.add(new JLabel("Tahun Studi:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 8;
            JTextField yearOfStudyField = new JTextField(String.valueOf(studentMember.getYearOfStudy()), 15);
            panel.add(yearOfStudyField, gbc);
            
            // Buttons
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(20, 5, 5, 5);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton cancelButton = new JButton("Batal");
            cancelButton.addActionListener(e -> dialog.dispose());
            
            JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
            saveButton.addActionListener(e -> {
                try {
                    // Validasi input
                    if (nameField.getText().isEmpty()) {
                        showErrorMessage("Nama harus diisi!", "Error");
                        return;
                    }
                    
                    // Validasi input numerik
                    int yearOfStudy;
                    try {
                        yearOfStudy = Integer.parseInt(yearOfStudyField.getText().trim());
                    } catch (NumberFormatException ex) {
                        showErrorMessage("Tahun studi harus berupa angka!", "Error");
                        return;
                    }
                    
                    // Update data
                    member.setName(nameField.getText().trim());
                    member.setAddress(addressField.getText().trim());
                    member.setPhoneNumber(phoneField.getText().trim());
                    member.setEmail(emailField.getText().trim());
                    
                    studentMember.setStudentId(studentIdField.getText().trim());
                    studentMember.setFaculty(facultyField.getText().trim());
                    studentMember.setDepartment(departmentField.getText().trim());
                    studentMember.setYearOfStudy(yearOfStudy);
                    
                    dialog.dispose();
                    showInfoMessage("Anggota berhasil diupdate!", "Sukses");
                    
                    // Refresh tabel anggota
                    refreshMembersTable(parentFrame.getMembers());
                    
                } catch (Exception ex) {
                    showErrorMessage("Error: " + ex.getMessage(), "Error");
                }
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            panel.add(buttonPanel, gbc);
            
        } else if (member instanceof RegularMember) {
            RegularMember regularMember = (RegularMember) member;
            
            gbc.gridx = 0;
            gbc.gridy = 5;
            panel.add(new JLabel("Pekerjaan:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 5;
            JTextField occupationField = new JTextField(regularMember.getOccupation(), 15);
            panel.add(occupationField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 6;
            panel.add(new JLabel("Perusahaan/Institusi:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 6;
            JTextField employerField = new JTextField(regularMember.getEmployerName(), 15);
            panel.add(employerField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 7;
            panel.add(new JLabel("Premium:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 7;
            JCheckBox premiumCheck = new JCheckBox();
            premiumCheck.setSelected(regularMember.isPremium());
            panel.add(premiumCheck, gbc);
            
            // Buttons
            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(20, 5, 5, 5);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            
            JButton cancelButton = new JButton("Batal");
            cancelButton.addActionListener(e -> dialog.dispose());
            
            JButton saveButton = createStyledButton("Simpan", new Color(39, 174, 96));
            saveButton.addActionListener(e -> {
                try {
                    // Validasi input
                    if (nameField.getText().isEmpty()) {
                        showErrorMessage("Nama harus diisi!", "Error");
                        return;
                    }
                    
                    // Update data
                    member.setName(nameField.getText().trim());
                    member.setAddress(addressField.getText().trim());
                    member.setPhoneNumber(phoneField.getText().trim());
                    member.setEmail(emailField.getText().trim());
                    
                    regularMember.setOccupation(occupationField.getText().trim());
                    regularMember.setEmployerName(employerField.getText().trim());
                    regularMember.setPremium(premiumCheck.isSelected());
                    
                    dialog.dispose();
                    showInfoMessage("Anggota berhasil diupdate!", "Sukses");
                    
                    // Refresh tabel anggota
                    refreshMembersTable(parentFrame.getMembers());
                    
                } catch (Exception ex) {
                    showErrorMessage("Error: " + ex.getMessage(), "Error");
                }
            });
            
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            panel.add(buttonPanel, gbc);
        }
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Menampilkan dialog untuk melihat detail anggota
     * @param member Anggota yang detailnya akan ditampilkan
     */
    private void showMemberDetailsDialog(Member member) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Detail Anggota", true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel info anggota
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        infoPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        infoPanel.add(new JLabel("ID Anggota:"));
        infoPanel.add(new JLabel(member.getMemberId()));
        
        infoPanel.add(new JLabel("Nama:"));
        infoPanel.add(new JLabel(member.getName()));
        
        infoPanel.add(new JLabel("Alamat:"));
        infoPanel.add(new JLabel(member.getAddress()));
        
        infoPanel.add(new JLabel("No. Telepon:"));
        infoPanel.add(new JLabel(member.getPhoneNumber()));
        
        infoPanel.add(new JLabel("Email:"));
        infoPanel.add(new JLabel(member.getEmail()));
        
        infoPanel.add(new JLabel("Tanggal Registrasi:"));
        infoPanel.add(new JLabel(dateFormat.format(member.getRegistrationDate())));
        
        infoPanel.add(new JLabel("Tanggal Kadaluarsa:"));
        infoPanel.add(new JLabel(dateFormat.format(member.getExpiryDate())));
        
        infoPanel.add(new JLabel("Status:"));
        infoPanel.add(new JLabel(member.getStatus().toString()));
        
        infoPanel.add(new JLabel("Aktif:"));
        infoPanel.add(new JLabel(member.isActive() ? "Ya" : "Tidak"));
        
        if (member instanceof StudentMember) {
            StudentMember studentMember = (StudentMember) member;
            
            infoPanel.add(new JLabel("Jenis Anggota:"));
            infoPanel.add(new JLabel("Mahasiswa"));
            
            infoPanel.add(new JLabel("ID Mahasiswa:"));
            infoPanel.add(new JLabel(studentMember.getStudentId()));
            
            infoPanel.add(new JLabel("Fakultas:"));
            infoPanel.add(new JLabel(studentMember.getFaculty()));
            
            infoPanel.add(new JLabel("Jurusan:"));
            infoPanel.add(new JLabel(studentMember.getDepartment()));
            
            infoPanel.add(new JLabel("Tahun Studi:"));
            infoPanel.add(new JLabel(String.valueOf(studentMember.getYearOfStudy())));
        } else if (member instanceof RegularMember) {
            RegularMember regularMember = (RegularMember) member;
            
            infoPanel.add(new JLabel("Jenis Anggota:"));
            infoPanel.add(new JLabel("Reguler"));
            
            infoPanel.add(new JLabel("Pekerjaan:"));
            infoPanel.add(new JLabel(regularMember.getOccupation()));
            
            infoPanel.add(new JLabel("Perusahaan/Institusi:"));
            infoPanel.add(new JLabel(regularMember.getEmployerName()));
            
            infoPanel.add(new JLabel("Premium:"));
            infoPanel.add(new JLabel(regularMember.isPremium() ? "Ya" : "Tidak"));
        }
        
        infoPanel.add(new JLabel("Batas Buku:"));
        infoPanel.add(new JLabel(String.valueOf(member.getMaxBooks())));
        
        infoPanel.add(new JLabel("Durasi Peminjaman:"));
        infoPanel.add(new JLabel(member.getMaxLoanDays() + " hari"));
        
        infoPanel.add(new JLabel("Buku yang Dipinjam:"));
        infoPanel.add(new JLabel(String.valueOf(member.getCurrentBooksCount())));
        
        infoPanel.add(new JLabel("Total Denda Dibayar:"));
        infoPanel.add(new JLabel(String.format("Rp%.2f", member.getTotalFinesPaid())));
        
        JScrollPane infoScrollPane = new JScrollPane(infoPanel);
        infoScrollPane.setPreferredSize(new Dimension(500, 200));
        panel.add(infoScrollPane, BorderLayout.NORTH);
        
        // Tabbedpane untuk peminjaman dan reservasi
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab peminjaman aktif
        JPanel activeLoansPanel = new JPanel(new BorderLayout());
        
        String[] loanColumns = {"ID", "Judul Buku", "Tanggal Pinjam", "Jatuh Tempo", "Status"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (BookLoan loan : member.getBookLoans()) {
            if (loan.getStatus() == LoanStatus.ACTIVE || loan.getStatus() == LoanStatus.OVERDUE) {
                loanModel.addRow(new Object[]{
                    loan.getLoanId(),
                    loan.getBookItem().getBook().getTitle(),
                    dateFormat.format(loan.getIssueDate()),
                    dateFormat.format(loan.getDueDate()),
                    loan.getStatus()
                });
            }
        }
        
        JTable loansTable = new JTable(loanModel);
        loansTable.setFillsViewportHeight(true);
        JScrollPane loansScrollPane = new JScrollPane(loansTable);
        activeLoansPanel.add(loansScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Peminjaman Aktif", activeLoansPanel);
        
        // Tab reservasi
        JPanel reservationsPanel = new JPanel(new BorderLayout());
        
        String[] reservationColumns = {"ID", "Judul Buku", "Tanggal Reservasi", "Status"};
        DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Reservation reservation : member.getReservations()) {
            reservationModel.addRow(new Object[]{
                reservation.getReservationId(),
                reservation.getBook().getTitle(),
                dateFormat.format(reservation.getReservationDate()),
                reservation.getStatus()
            });
        }
        
        JTable reservationsTable = new JTable(reservationModel);
        reservationsTable.setFillsViewportHeight(true);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        reservationsPanel.add(reservationsScrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("Reservasi", reservationsPanel);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Tutup");
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    /**
     * Menampilkan dialog untuk memperpanjang keanggotaan
     * @param member Anggota yang keanggotaannya akan diperpanjang
     */
    private void showRenewMembershipDialog(Member member) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Perpanjang Keanggotaan", true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Anggota:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField nameField = new JTextField(member.getName(), 15);
        nameField.setEditable(false);
        panel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Tanggal Kadaluarsa Saat Ini:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField currentExpiryField = new JTextField(dateFormat.format(member.getExpiryDate()), 15);
        currentExpiryField.setEditable(false);
        panel.add(currentExpiryField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Jumlah Bulan Perpanjangan:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField monthsField = new JTextField("1", 15);
        panel.add(monthsField, gbc);
        
        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Batal");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton renewButton = createStyledButton("Perpanjang", new Color(39, 174, 96));
        renewButton.addActionListener(e -> {
            try {
                // Validasi input
                int months;
                try {
                    months = Integer.parseInt(monthsField.getText().trim());
                } catch (NumberFormatException ex) {
                    showErrorMessage("Jumlah bulan harus berupa angka!", "Error");
                    return;
                }
                
                if (months <= 0) {
                    showErrorMessage("Jumlah bulan harus positif!", "Error");
                    return;
                }
                
                // Perpanjang keanggotaan
                member.renewMembership(months);
                
                dialog.dispose();
                showInfoMessage("Keanggotaan berhasil diperpanjang hingga " + dateFormat.format(member.getExpiryDate()), "Sukses");
                
                // Refresh tabel anggota
                refreshMembersTable(parentFrame.getMembers());
                
            } catch (Exception ex) {
                showErrorMessage("Error: " + ex.getMessage(), "Error");
            }
        });

        JButton blacklistButton = createStyledButton("Blacklist Anggota", new Color(192, 57, 43));
        blacklistButton.setEnabled(false);
        blacklistButton.addActionListener(e -> {
            int row = membersTable.getSelectedRow();
            if (row != -1) {
                String memberId = (String) membersTable.getValueAt(row, 0);
                Member selectedMember = findMemberById(memberId);
                
                if (selectedMember != null && currentLibrarian.getPermission() == LibrarianPermission.ADMIN) {
                    boolean confirm = showConfirmDialog(
                        "Apakah Anda yakin ingin memasukkan anggota " + selectedMember.getName() + " ke dalam blacklist?",
                        "Konfirmasi Blacklist");
                    
                    if (confirm) {
                        // Menggunakan enum MemberStatus.BLACKLISTED
                        selectedMember.setActive(false);
                        selectedMember.setStatus(MemberStatus.BLACKLISTED);
                        
                        // Refresh tabel
                        refreshMembersTable(parentFrame.getMembers());
                        showInfoMessage("Anggota berhasil dimasukkan ke dalam blacklist.", "Sukses");
                    }
                }
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(renewButton);
        
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}