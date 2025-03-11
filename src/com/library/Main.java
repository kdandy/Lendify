package com.library;

import com.library.enums.*;
import com.library.exception.*;
import com.library.model.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("    SISTEM MANAJEMEN PERPUSTAKAAN");
        System.out.println("============================================");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        
        try {
            // Membuat perpustakaan
            Library library = new Library("Perpustakaan Kota", "Jl. Utama No. 123, Kotaville");
            System.out.println("Perpustakaan dibuat: " + library.getName());
            System.out.println();
            
            // Membuat dan menambahkan pustakawan
            System.out.println("=== Membuat Pustakawan ===");
            Person adminPerson = new Person("P001", "Budi Santoso", "Jl. Oak No. 456", "555-1234");
            adminPerson.setEmail("budi.santoso@perpustakaan.com");
            Librarian adminLibrarian = new Librarian(adminPerson, "L001", "Kepala Pustakawan", 50000, LibrarianPermission.ADMIN);
            
            Person assistantPerson = new Person("P002", "Sari Wijaya", "Jl. Pinus No. 789", "555-5678");
            assistantPerson.setEmail("sari.wijaya@perpustakaan.com");
            Librarian assistantLibrarian = new Librarian(assistantPerson, "L002", "Asisten Pustakawan", 35000, LibrarianPermission.FULL);
            
            library.addLibrarian(adminLibrarian);
            library.addLibrarian(assistantLibrarian);
            
            System.out.println("Pustakawan ditambahkan: " + adminLibrarian.getName() + " (" + adminLibrarian.getPosition() + ")");
            System.out.println("Pustakawan ditambahkan: " + assistantLibrarian.getName() + " (" + assistantLibrarian.getPosition() + ")");
            System.out.println();
            
            // Membuat kategori buku
            System.out.println("=== Membuat Kategori Buku ===");
            BookCategory fictionCategory = new BookCategory("Fiksi", "Novel, cerita pendek, dan karya fiksi lainnya");
            BookCategory nonFictionCategory = new BookCategory("Non-Fiksi", "Karya faktual, biografi, dan materi pendidikan");
            BookCategory scienceCategory = new BookCategory("Sains", "Buku tentang berbagai disiplin ilmu sains");
            
            library.addCategory(fictionCategory);
            library.addCategory(nonFictionCategory);
            library.addCategory(scienceCategory);
            
            System.out.println("Kategori ditambahkan: Fiksi, Non-Fiksi, Sains");
            System.out.println();
            
            // Membuat buku
            System.out.println("=== Membuat Buku ===");
            Book book1 = new Book("978-1234567897", "Petualangan Hebat", "Alice Penulis", "Buku Inc.", 2022,
                                 "Sebuah cerita petualangan yang menarik", 320, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            Book book2 = new Book("978-9876543210", "Sejarah Sains", "Bob Sejarawan", "Penerbit Akademik", 2021,
                                 "Sejarah komprehensif tentang penemuan ilmiah", 450, BookFormat.HARDCOVER, Language.INDONESIAN);
            
            Book book3 = new Book("978-5678901234", "Dasar-dasar Pemrograman", "Charlie Koder", "Buku Teknologi", 2023,
                                 "Pengantar konsep pemrograman", 280, BookFormat.PAPERBACK, Language.INDONESIAN);
            
            library.addBook(book1);
            library.addBook(book2);
            library.addBook(book3);
            
            library.addBookToCategory(book1, fictionCategory);
            library.addBookToCategory(book2, nonFictionCategory);
            library.addBookToCategory(book2, scienceCategory);
            library.addBookToCategory(book3, nonFictionCategory);
            library.addBookToCategory(book3, scienceCategory);
            
            System.out.println("Buku ditambahkan: " + book1.getTitle() + " oleh " + book1.getAuthor());
            System.out.println("Buku ditambahkan: " + book2.getTitle() + " oleh " + book2.getAuthor());
            System.out.println("Buku ditambahkan: " + book3.getTitle() + " oleh " + book3.getAuthor());
            System.out.println();
            
            // Menambahkan salinan buku
            System.out.println("=== Menambahkan Salinan Buku ===");
            
            BookItem book1Copy1 = adminLibrarian.addBookItem(book1, "B1001");
            adminLibrarian.addBookItem(book1, "B1002"); // Membuat salinan kedua
            adminLibrarian.addBookItem(book2, "B2001"); // Membuat salinan pertama dari buku 2
            adminLibrarian.addBookItem(book2, "B2002"); // Membuat salinan kedua dari buku 2
            BookItem book2Copy3 = adminLibrarian.addBookItem(book2, "B2003");
            BookItem book3Copy1 = adminLibrarian.addBookItem(book3, "B3001");
            
            // Menandai satu salinan sebagai referensi saja
            book2Copy3.setReferenceOnly(true);
            
            System.out.println("Ditambahkan 2 salinan dari: " + book1.getTitle());
            System.out.println("Ditambahkan 3 salinan dari: " + book2.getTitle() + " (1 hanya referensi)");
            System.out.println("Ditambahkan 1 salinan dari: " + book3.getTitle());
            System.out.println();
            
            // Membuat anggota perpustakaan
            System.out.println("=== Membuat Anggota Perpustakaan ===");
            
            // Anggota reguler
            Person person1 = new Person("P003", "Dimas Wicaksono", "Jl. Elm No. 101", "555-9012");
            person1.setEmail("dimas.wicaksono@email.com");
            Member member1 = adminLibrarian.addMember(person1);
            RegularMember regularMember = new RegularMember(member1, "Insinyur", "Perusahaan Teknologi", true);
            
            // Anggota mahasiswa
            Person person2 = new Person("P004", "Eni Permata", "Jl. Cedar No. 202", "555-3456");
            person2.setEmail("eni.permata@universitas.edu");
            Member member2 = adminLibrarian.addMember(person2);
            StudentMember studentMember = new StudentMember(member2, "S12345", "Teknik", "Ilmu Komputer", 3);
            
            System.out.println("Anggota reguler ditambahkan: " + regularMember.getName() + " (" + regularMember.getOccupation() + ")");
            System.out.println("Anggota mahasiswa ditambahkan: " + studentMember.getName() + " (ID Mahasiswa: " + studentMember.getStudentId() + ")");
            System.out.println();
            
            // Meminjamkan buku kepada anggota
            System.out.println("=== Meminjamkan Buku kepada Anggota ===");
            
            try {
                BookLoan loan1 = assistantLibrarian.issueBook(regularMember, book1Copy1);
                System.out.println("Meminjamkan '" + book1.getTitle() + "' kepada " + regularMember.getName());
                System.out.println("  Tanggal jatuh tempo: " + dateFormat.format(loan1.getDueDate()));
                
                BookLoan loan2 = assistantLibrarian.issueBook(studentMember, book3Copy1);
                System.out.println("Meminjamkan '" + book3.getTitle() + "' kepada " + studentMember.getName());
                System.out.println("  Tanggal jatuh tempo: " + dateFormat.format(loan2.getDueDate()));
                
                // Mencoba meminjamkan buku referensi (seharusnya menimbulkan exception)
                try {
                    assistantLibrarian.issueBook(studentMember, book2Copy3);
                } catch (ReferenceOnlyException e) {
                    System.out.println("Pengecualian: " + e.getMessage());
                }
                
                System.out.println();
                
                // Membuat reservasi
                System.out.println("=== Membuat Reservasi ===");
                Reservation reservation = studentMember.reserveBook(book2);
                System.out.println(studentMember.getName() + " mereservasi '" + book2.getTitle() + "'");
                System.out.println("ID Reservasi: " + reservation.getReservationId());
                System.out.println("Status Reservasi: " + reservation.getStatus());
                System.out.println();
                
                // Pengembalian buku
                System.out.println("=== Mengembalikan Buku ===");
                
                // Mensimulasikan buku terlambat dengan mengatur tanggal peminjaman ke masa lalu
                Calendar pastCalendar = Calendar.getInstance();
                pastCalendar.setTime(loan1.getIssueDate());
                pastCalendar.add(Calendar.DAY_OF_MONTH, -35); // 35 hari yang lalu
                
                // Menggunakan reflection untuk memodifikasi tanggal peminjaman dan jatuh tempo untuk keperluan demonstrasi
                try {
                    java.lang.reflect.Field issueDateField = BookLoan.class.getDeclaredField("issueDate");
                    issueDateField.setAccessible(true);
                    issueDateField.set(loan1, pastCalendar.getTime());
                    
                    java.lang.reflect.Field dueDateField = BookLoan.class.getDeclaredField("dueDate");
                    dueDateField.setAccessible(true);
                    
                    Calendar dueDateCalendar = Calendar.getInstance();
                    dueDateCalendar.setTime(pastCalendar.getTime());
                    dueDateCalendar.add(Calendar.DAY_OF_MONTH, regularMember.getMaxLoanDays());
                    dueDateField.set(loan1, dueDateCalendar.getTime());
                } catch (Exception e) {
                    System.out.println("Tidak dapat mensimulasikan buku terlambat: " + e.getMessage());
                }
                
                // Mengembalikan buku yang terlambat
                assistantLibrarian.returnBook(loan1);
                double fine = loan1.getFine();
                System.out.println("Mengembalikan '" + book1.getTitle() + "' dari " + regularMember.getName());
                System.out.println("  Tanggal pengembalian: " + dateFormat.format(loan1.getReturnDate()));
                System.out.println("  Denda untuk keterlambatan: Rp" + String.format("%.2f", fine));
                
                // Membayar denda
                regularMember.payFine(fine);
                System.out.println("  " + regularMember.getName() + " membayar denda sebesar Rp" + String.format("%.2f", fine));
                System.out.println();
                
                // Memproses reservasi
                System.out.println("=== Memproses Reservasi ===");
                try {
                    assistantLibrarian.processReservation(reservation);
                    System.out.println("Reservasi diproses untuk " + studentMember.getName());
                    System.out.println("Buku '" + book2.getTitle() + "' telah dipinjamkan");
                    System.out.println("Status Reservasi: " + reservation.getStatus());
                } catch (InvalidOperationException e) {
                    System.out.println("Tidak dapat memproses reservasi: " + e.getMessage());
                }
                System.out.println();
                
                // Mencari buku
                System.out.println("=== Mencari Buku ===");
                System.out.println("Mencari 'Sains':");
                List<Book> searchResults = library.searchByTitle("Sains");
                for (Book book : searchResults) {
                    System.out.println("  " + book.getTitle() + " oleh " + book.getAuthor());
                }
                
                System.out.println("Buku dalam kategori Sains:");
                List<Book> scienceBooks = library.searchByCategory(scienceCategory);
                for (Book book : scienceBooks) {
                    System.out.println("  " + book.getTitle() + " oleh " + book.getAuthor());
                }
                System.out.println();
                
                // Statistik perpustakaan
                System.out.println("=== Statistik Perpustakaan ===");
                System.out.println(library.toString());
                System.out.println("Total buku: " + library.getCollection().getTotalBooks());
                System.out.println("Total kategori: " + library.getCollection().getTotalCategories());
                System.out.println("Total pustakawan: " + library.getLibrarians().size());
                
            } catch (Exception e) {
                System.out.println("Terjadi kesalahan: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
            e.printStackTrace();
        }
    }
}