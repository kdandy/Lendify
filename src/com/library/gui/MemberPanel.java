package com.library.gui;

import com.library.enums.LibrarianPermission;
import com.library.enums.MemberStatus;
import com.library.gui.utils.DialogUtils;
import com.library.gui.utils.GUIUtils;
import com.library.gui.utils.TableModels.MemberTableModel;
import com.library.model.Member;
import com.library.model.Person;
import com.library.model.RegularMember;
import com.library.model.StudentMember;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Panel untuk kelola anggota perpustakaan
 */
public class MemberPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private LendifyGUI mainWindow;
    private JTable memberTable;
    private MemberTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton detailsButton;
    private JButton renewButton;
    private JButton toggleStatusButton;
    private JButton backButton;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    
    /**
     * Constructor untuk MemberPanel
     */
    public MemberPanel(LendifyGUI mainWindow) {
        this.mainWindow = mainWindow;
        setupUI();
    }
    
    /**
     * Setup komponen UI
     */
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Panel judul
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Kelola Anggota", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        // Panel pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Cari");
        searchButton.addActionListener(e -> searchMembers());
        
        searchPanel.add(new JLabel("Cari: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        titlePanel.add(searchPanel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel tabel
        tableModel = new MemberTableModel(new ArrayList<>(mainWindow.getMembers()));
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(25);
        memberTable.getTableHeader().setReorderingAllowed(false);
        
        // Sorter untuk tabel
        TableRowSorter<MemberTableModel> sorter = new TableRowSorter<>(tableModel);
        memberTable.setRowSorter(sorter);
        
        // Mouse listener untuk double-click
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewMemberDetails();
                }
            }
        });
        
        // Panel untuk tabel dengan scrolling
        JScrollPane tableScrollPane = new JScrollPane(memberTable);
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Panel filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        
        JRadioButton allRadio = new JRadioButton("Semua", true);
        JRadioButton studentRadio = new JRadioButton("Mahasiswa");
        JRadioButton regularRadio = new JRadioButton("Reguler");
        JRadioButton activeRadio = new JRadioButton("Aktif");
        JRadioButton inactiveRadio = new JRadioButton("Tidak Aktif");
        
        ButtonGroup typeGroup = new ButtonGroup();
        typeGroup.add(allRadio);
        typeGroup.add(studentRadio);
        typeGroup.add(regularRadio);
        
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(allRadio);
        statusGroup.add(activeRadio);
        statusGroup.add(inactiveRadio);
        
        allRadio.addActionListener(e -> filterMembers(null, null));
        studentRadio.addActionListener(e -> filterMembers("student", null));
        regularRadio.addActionListener(e -> filterMembers("regular", null));
        activeRadio.addActionListener(e -> filterMembers(null, true));
        inactiveRadio.addActionListener(e -> filterMembers(null, false));
        
        filterPanel.add(new JLabel("Tipe:"));
        filterPanel.add(allRadio);
        filterPanel.add(studentRadio);
        filterPanel.add(regularRadio);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(activeRadio);
        filterPanel.add(inactiveRadio);
        
        // Panel aksi
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        addButton = new JButton("Tambah Anggota");
        editButton = new JButton("Edit Anggota");
        detailsButton = new JButton("Lihat Detail");
        renewButton = new JButton("Perpanjang Keanggotaan");
        toggleStatusButton = new JButton("Aktifkan/Nonaktifkan");
        backButton = new JButton("Kembali");
        
        addButton.addActionListener(e -> addMember());
        editButton.addActionListener(e -> editMember());
        detailsButton.addActionListener(e -> viewMemberDetails());
        renewButton.addActionListener(e -> renewMembership());
        toggleStatusButton.addActionListener(e -> toggleMemberStatus());
        backButton.addActionListener(e -> mainWindow.showMainPanel());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(detailsButton);
        buttonPanel.add(renewButton);
        buttonPanel.add(toggleStatusButton);
        buttonPanel.add(backButton);
        
        actionPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(actionPanel, BorderLayout.SOUTH);
        
        // Set initial button states
        updateButtonStates();
    }
    
    /**
     * Refresh data panel
     */
    public void refreshData() {
        tableModel.setMembers(new ArrayList<>(mainWindow.getMembers()));
        updateButtonStates();
    }
    
    /**
     * Update status tombol berdasarkan hak akses dan seleksi
     */
    private void updateButtonStates() {
        boolean isBasic = mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC;
        boolean memberSelected = memberTable.getSelectedRow() != -1;
        
        addButton.setEnabled(!isBasic);
        editButton.setEnabled(memberSelected);
        detailsButton.setEnabled(memberSelected);
        renewButton.setEnabled(!isBasic && memberSelected);
        toggleStatusButton.setEnabled(!isBasic && memberSelected);
    }
    
    /**
     * Cari anggota berdasarkan keyword
     */
    private void searchMembers() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            refreshData();
            return;
        }
        
        ArrayList<Member> filteredList = new ArrayList<>();
        for (Member member : mainWindow.getMembers()) {
            if (member.getName().toLowerCase().contains(keyword) ||
                member.getMemberId().toLowerCase().contains(keyword) ||
                member.getEmail().toLowerCase().contains(keyword) ||
                member.getPhoneNumber().toLowerCase().contains(keyword)) {
                filteredList.add(member);
            }
        }
        
        tableModel.setMembers(filteredList);
        updateButtonStates();
    }
    
    /**
     * Filter anggota berdasarkan tipe dan status
     */
    private void filterMembers(String type, Boolean active) {
        ArrayList<Member> filteredList = new ArrayList<>();
        for (Member member : mainWindow.getMembers()) {
            boolean typeMatch = true;
            boolean statusMatch = true;
            
            if (type != null) {
                if (type.equals("student") && !(member instanceof StudentMember)) {
                    typeMatch = false;
                } else if (type.equals("regular") && !(member instanceof RegularMember)) {
                    typeMatch = false;
                }
            }
            
            if (active != null && member.isActive() != active) {
                statusMatch = false;
            }
            
            if (typeMatch && statusMatch) {
                filteredList.add(member);
            }
        }
        
        tableModel.setMembers(filteredList);
        updateButtonStates();
    }
    
    /**
     * Tambah anggota baru
     */
    private void addMember() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk menambah anggota!", "Akses Ditolak");
            return;
        }
        
        // Panel untuk memilih tipe anggota
        String[] options = {"Mahasiswa", "Reguler", "Batal"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Pilih jenis anggota yang akan ditambahkan:",
            "Tambah Anggota",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
            return; // User canceled
        }
        
        boolean isStudent = (choice == 0);
        
        // Panel form untuk input data anggota
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        // Form data person
        JTextField personIdField = new JTextField(15);
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(15);
        JTextField emailField = new JTextField(20);
        
        formPanel.add(new JLabel("ID Person:"));
        formPanel.add(personIdField);
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Nomor Telepon:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        // Form data spesifik berdasarkan tipe anggota
        JTextField studentIdField = null;
        JTextField facultyField = null;
        JTextField departmentField = null;
        JTextField yearOfStudyField = null;
        
        JTextField occupationField = null;
        JTextField employerField = null;
        JCheckBox premiumCheckbox = null;
        
        if (isStudent) {
            studentIdField = new JTextField(15);
            facultyField = new JTextField(20);
            departmentField = new JTextField(20);
            yearOfStudyField = new JTextField(5);
            
            formPanel.add(new JLabel("ID Mahasiswa:"));
            formPanel.add(studentIdField);
            formPanel.add(new JLabel("Fakultas:"));
            formPanel.add(facultyField);
            formPanel.add(new JLabel("Jurusan:"));
            formPanel.add(departmentField);
            formPanel.add(new JLabel("Tahun Studi:"));
            formPanel.add(yearOfStudyField);
        } else {
            occupationField = new JTextField(20);
            employerField = new JTextField(20);
            premiumCheckbox = new JCheckBox("Anggota Premium");
            
            formPanel.add(new JLabel("Pekerjaan:"));
            formPanel.add(occupationField);
            formPanel.add(new JLabel("Nama Perusahaan/Institusi:"));
            formPanel.add(employerField);
            formPanel.add(new JLabel("Status Keanggotaan:"));
            formPanel.add(premiumCheckbox);
        }
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, formPanel, "Tambah Anggota " + (isStudent ? "Mahasiswa" : "Reguler"), 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validasi input
                if (personIdField.getText().trim().isEmpty() || 
                    nameField.getText().trim().isEmpty()) {
                    throw new IllegalArgumentException("ID Person dan Nama tidak boleh kosong!");
                }
                
                // Buat objek Person
                Person person = new Person(
                    personIdField.getText().trim(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    phoneField.getText().trim()
                );
                person.setEmail(emailField.getText().trim());
                
                // Tambahkan anggota sesuai tipe
                Member member = mainWindow.getCurrentLibrarian().addMember(person);
                
                if (isStudent) {
                    if (studentIdField.getText().trim().isEmpty()) {
                        throw new IllegalArgumentException("ID Mahasiswa tidak boleh kosong!");
                    }
                    
                    int yearOfStudy = 1;
                    try {
                        if (!yearOfStudyField.getText().trim().isEmpty()) {
                            yearOfStudy = Integer.parseInt(yearOfStudyField.getText().trim());
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Tahun Studi harus berupa angka!");
                    }
                    
                    StudentMember studentMember = new StudentMember(
                        member,
                        studentIdField.getText().trim(),
                        facultyField.getText().trim(),
                        departmentField.getText().trim(),
                        yearOfStudy
                    );
                    
                    mainWindow.addMember(studentMember);
                } else {
                    RegularMember regularMember = new RegularMember(
                        member,
                        occupationField.getText().trim(),
                        employerField.getText().trim(),
                        premiumCheckbox.isSelected()
                    );
                    
                    mainWindow.addMember(regularMember);
                }
                
                refreshData();
                
                GUIUtils.infoDialog(this, "Anggota berhasil ditambahkan!", "Sukses");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Edit anggota yang dipilih
     */
    private void editMember() {
        Member member = getSelectedMember();
        if (member == null) {
            GUIUtils.errorDialog(this, "Pilih anggota yang akan diubah!", "Tidak Ada Anggota Dipilih");
            return;
        }
        
        // Panel form untuk edit data
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        JTextField nameField = new JTextField(member.getName(), 20);
        JTextField addressField = new JTextField(member.getAddress(), 20);
        JTextField phoneField = new JTextField(member.getPhoneNumber(), 15);
        JTextField emailField = new JTextField(member.getEmail(), 20);
        
        formPanel.add(new JLabel("ID Person:"));
        formPanel.add(new JLabel(member.getId()));
        formPanel.add(new JLabel("ID Anggota:"));
        formPanel.add(new JLabel(member.getMemberId()));
        formPanel.add(new JLabel("Nama:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Alamat:"));
        formPanel.add(addressField);
        formPanel.add(new JLabel("Nomor Telepon:"));
        formPanel.add(phoneField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        // Form data spesifik berdasarkan tipe anggota
        if (member instanceof StudentMember) {
            StudentMember studentMember = (StudentMember) member;
            
            JTextField studentIdField = new JTextField(studentMember.getStudentId(), 15);
            JTextField facultyField = new JTextField(studentMember.getFaculty(), 20);
            JTextField departmentField = new JTextField(studentMember.getDepartment(), 20);
            JTextField yearOfStudyField = new JTextField(String.valueOf(studentMember.getYearOfStudy()), 5);
            
            formPanel.add(new JLabel("ID Mahasiswa:"));
            formPanel.add(studentIdField);
            formPanel.add(new JLabel("Fakultas:"));
            formPanel.add(facultyField);
            formPanel.add(new JLabel("Jurusan:"));
            formPanel.add(departmentField);
            formPanel.add(new JLabel("Tahun Studi:"));
            formPanel.add(yearOfStudyField);
            
            // Tampilkan dialog
            int result = JOptionPane.showConfirmDialog(
                this, formPanel, "Edit Anggota Mahasiswa", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Validasi input
                    if (nameField.getText().trim().isEmpty() ||
                        studentIdField.getText().trim().isEmpty()) {
                        throw new IllegalArgumentException("Nama dan ID Mahasiswa tidak boleh kosong!");
                    }
                    
                    int yearOfStudy = 1;
                    try {
                        if (!yearOfStudyField.getText().trim().isEmpty()) {
                            yearOfStudy = Integer.parseInt(yearOfStudyField.getText().trim());
                        }
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Tahun Studi harus berupa angka!");
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
                    
                    refreshData();
                    
                    GUIUtils.infoDialog(this, "Anggota berhasil diubah!", "Sukses");
                } catch (Exception ex) {
                    GUIUtils.errorDialog(this, ex.getMessage(), "Error");
                }
            }
        } else if (member instanceof RegularMember) {
            RegularMember regularMember = (RegularMember) member;
            
            JTextField occupationField = new JTextField(regularMember.getOccupation(), 20);
            JTextField employerField = new JTextField(regularMember.getEmployerName(), 20);
            JCheckBox premiumCheckbox = new JCheckBox("Anggota Premium", regularMember.isPremium());
            
            formPanel.add(new JLabel("Pekerjaan:"));
            formPanel.add(occupationField);
            formPanel.add(new JLabel("Nama Perusahaan/Institusi:"));
            formPanel.add(employerField);
            formPanel.add(new JLabel("Status Keanggotaan:"));
            formPanel.add(premiumCheckbox);
            
            // Tampilkan dialog
            int result = JOptionPane.showConfirmDialog(
                this, formPanel, "Edit Anggota Reguler", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                try {
                    // Validasi input
                    if (nameField.getText().trim().isEmpty()) {
                        throw new IllegalArgumentException("Nama tidak boleh kosong!");
                    }
                    
                    // Update data
                    member.setName(nameField.getText().trim());
                    member.setAddress(addressField.getText().trim());
                    member.setPhoneNumber(phoneField.getText().trim());
                    member.setEmail(emailField.getText().trim());
                    
                    regularMember.setOccupation(occupationField.getText().trim());
                    regularMember.setEmployerName(employerField.getText().trim());
                    regularMember.setPremium(premiumCheckbox.isSelected());
                    
                    refreshData();
                    
                    GUIUtils.infoDialog(this, "Anggota berhasil diubah!", "Sukses");
                } catch (Exception ex) {
                    GUIUtils.errorDialog(this, ex.getMessage(), "Error");
                }
            }
        }
    }
    
    /**
     * Lihat detail anggota
     */
    private void viewMemberDetails() {
        Member member = getSelectedMember();
        if (member == null) {
            GUIUtils.errorDialog(this, "Pilih anggota untuk melihat detail!", "Tidak Ada Anggota Dipilih");
            return;
        }
        
        DialogUtils.showMemberDetailsDialog(this, member);
    }
    
    /**
     * Perpanjang keanggotaan anggota
     */
    private void renewMembership() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk memperpanjang keanggotaan!", "Akses Ditolak");
            return;
        }
        
        Member member = getSelectedMember();
        if (member == null) {
            GUIUtils.errorDialog(this, "Pilih anggota yang akan diperpanjang keanggotaannya!", "Tidak Ada Anggota Dipilih");
            return;
        }
        
        // Panel untuk input jumlah bulan
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        
        panel.add(new JLabel("Anggota:"));
        panel.add(new JLabel(member.getName()));
        
        panel.add(new JLabel("ID Anggota:"));
        panel.add(new JLabel(member.getMemberId()));
        
        panel.add(new JLabel("Tanggal Kadaluarsa Saat Ini:"));
        panel.add(new JLabel(dateFormat.format(member.getExpiryDate())));
        
        JTextField monthsField = new JTextField("12", 5);
        panel.add(new JLabel("Jumlah Bulan Perpanjangan:"));
        panel.add(monthsField);
        
        // Tampilkan dialog
        int result = JOptionPane.showConfirmDialog(
            this, panel, "Perpanjang Keanggotaan", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int months = Integer.parseInt(monthsField.getText().trim());
                if (months <= 0) {
                    throw new IllegalArgumentException("Jumlah bulan harus lebih dari 0!");
                }
                
                // Perpanjang keanggotaan
                member.renewMembership(months);
                
                refreshData();
                
                GUIUtils.infoDialog(
                    this, 
                    "Keanggotaan berhasil diperpanjang!\nTanggal Kadaluarsa Baru: " + dateFormat.format(member.getExpiryDate()), 
                    "Sukses"
                );
            } catch (NumberFormatException e) {
                GUIUtils.errorDialog(this, "Jumlah bulan harus berupa angka!", "Error");
            } catch (Exception ex) {
                GUIUtils.errorDialog(this, ex.getMessage(), "Error");
            }
        }
    }
    
    /**
     * Aktifkan/nonaktifkan anggota
     */
    private void toggleMemberStatus() {
        if (mainWindow.getCurrentLibrarian().getPermission() == LibrarianPermission.BASIC) {
            GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk mengubah status anggota!", "Akses Ditolak");
            return;
        }
        
        Member member = getSelectedMember();
        if (member == null) {
            GUIUtils.errorDialog(this, "Pilih anggota yang akan diubah statusnya!", "Tidak Ada Anggota Dipilih");
            return;
        }
        
        // Panel untuk memilih status
        String[] options = {"Aktifkan", "Nonaktifkan", "Blacklist", "Batal"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Status saat ini: " + (member.isActive() ? "Aktif" : "Tidak Aktif") + "\nPilih tindakan:",
            "Ubah Status Anggota",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
            return; // User canceled
        }
        
        try {
            switch (choice) {
                case 0: // Aktifkan
                    member.setActive(true);
                    member.setStatus(MemberStatus.ACTIVE);
                    break;
                case 1: // Nonaktifkan
                    member.setActive(false);
                    member.setStatus(MemberStatus.INACTIVE);
                    break;
                case 2: // Blacklist
                    if (mainWindow.getCurrentLibrarian().getPermission() != LibrarianPermission.ADMIN) {
                        GUIUtils.errorDialog(this, "Anda tidak memiliki hak akses untuk melakukan blacklist anggota!", "Akses Ditolak");
                        return;
                    }
                    
                    boolean confirm = GUIUtils.confirmDialog(
                        this, 
                        "Apakah Anda yakin ingin memasukkan anggota ini ke dalam blacklist?", 
                        "Konfirmasi Blacklist"
                    );
                    
                    if (confirm) {
                        mainWindow.getCurrentLibrarian().blacklistMember(member);
                    } else {
                        return;
                    }
                    break;
            }
            
            refreshData();
            
            GUIUtils.infoDialog(this, "Status anggota berhasil diubah!", "Sukses");
        } catch (Exception ex) {
            GUIUtils.errorDialog(this, ex.getMessage(), "Error");
        }
    }
    
    /**
     * Mendapatkan anggota yang dipilih di tabel
     */
    private Member getSelectedMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = memberTable.convertRowIndexToModel(selectedRow);
            return tableModel.getMemberAt(modelRow);
        }
        return null;
    }
}