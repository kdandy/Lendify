# Lendify

Lendify adalah sistem manajemen perpustakaan berbasis Java yang menerapkan konsep Pemrograman Berorientasi Objek (OOP) dengan relasi agregasi, komposisi, dan inheritance. Sistem ini memungkinkan perpustakaan untuk mengelola koleksi buku, anggota, dan transaksi peminjaman dengan cara yang efisien.

## Fitur Utama
- Manajemen anggota (reguler dan mahasiswa)
- Manajemen koleksi buku dan kategori
- Peminjaman dan pengembalian buku
- Reservasi buku
- Perhitungan denda keterlambatan
- Pencarian buku berdasarkan judul, pengarang, dan kategori
- Pengelolaan akses pustakawan berbasis hak akses

## Struktur Kelas
1. **Book**: Mewakili data buku dalam perpustakaan.
2. **Member**: Mewakili data anggota perpustakaan.
3. **Loan**: Mewakili proses peminjaman buku.
4. **Library**: Menyimpan koleksi buku dan anggota.
5. **Person**: Superclass untuk Member.
6. **Address**: Mewakili alamat anggota (komposisi dengan Member).
7. **Main**: Class utama untuk menjalankan program.

## Struktur Direktori
```
LibrarySystem/
├── src/
│   └── com/
│       └── library/
│           ├── exception/
│           │   └── LibraryException.java
│           ├── model/
│           │   ├── Person.java
│           │   ├── Member.java
│           │   ├── StudentMember.java
│           │   ├── RegularMember.java
│           │   ├── Librarian.java
│           │   ├── MemberStatus.java
│           │   ├── LibrarianPermission.java
│           │   ├── Library.java
│           │   ├── LibraryCollection.java
│           │   ├── Book.java
│           │   ├── BookItem.java
│           │   ├── BookCategory.java
│           │   ├── BookLoan.java
│           │   ├── Reservation.java
│           │   ├── BookFormat.java
│           │   ├── Language.java
│           │   ├── BookStatus.java
│           │   ├── LoanStatus.java
│           │   └── ReservationStatus.java
│           └── main/
│               └── Main.java
└── README.md
```

## Cara Menjalankan Aplikasi
1. Clone repositori:
```bash
git clone https://github.com/username/lendify.git
cd lendify
```

2. Buat direktori untuk file hasil kompilasi:
```bash
mkdir -p bin
```

3. Kompilasi semua file Java:
```bash
javac -d bin src/com/library/exception/*.java src/com/library/model/*.java src/com/library/main/*.java
```

4. Jalankan program:
```bash
java -cp bin com.library.main.Main
```

## Contoh Penggunaan
Berikut adalah contoh penggunaan dasar sistem Lendify:
```java
// Membuat perpustakaan
Library library = new Library("Perpustakaan Kota", "Jl. Pemuda No. 123");

// Membuat pustakawan
Person librarianPerson = new Person("L001", "Budi Santoso", "Jl. Kebun Raya", "081234567890");
Librarian librarian = new Librarian(librarianPerson, "LIB001", "Kepala Pustakawan", 5000000, LibrarianPermission.ADMIN);
library.addLibrarian(librarian);

// Membuat kategori buku
BookCategory fictionCategory = new BookCategory("Fiksi", "Buku-buku fiksi populer");
library.getCollection().addCategory(fictionCategory);

// Menambah buku
Book novel = new Book("978-979-1234-56-7", "Laskar Pelangi", "Andrea Hirata", "Bentang Pustaka", 2005);
library.getCollection().addBook(novel);
library.getCollection().addBookToCategory(novel, fictionCategory);

// Menambah item buku (salinan fisik)
BookItem novelCopy = librarian.addBookItem(novel, "LP001");

// Membuat anggota
Person memberPerson = new Person("M001", "Ani Wijaya", "Jl. Pahlawan", "087654321098");
Member member = librarian.addMember(memberPerson);

// Melakukan peminjaman
try {
    BookLoan loan = librarian.issueBook(member, novelCopy);
    System.out.println("Buku berhasil dipinjam sampai: " + loan.getDueDate());
} catch (LibraryException e) {
    System.out.println("Gagal meminjam buku: " + e.getMessage());
}

// Melakukan pengembalian
try {
    librarian.returnBook(loan);
    System.out.println("Buku berhasil dikembalikan");
    
    if (loan.getFine() > 0) {
        System.out.println("Denda keterlambatan: $" + loan.getFine());
        member.payFine(loan.getFine());
    }
} catch (LibraryException e) {
    System.out.println("Gagal mengembalikan buku: " + e.getMessage());
}
```

## Exception Handling
Sistem ini menggunakan custom exception LibraryException untuk menangani berbagai skenario error, seperti:

- Anggota tidak aktif saat meminjam buku
- Anggota dalam daftar hitam
- Anggota memiliki denda yang belum dibayar
- Anggota sudah mencapai batas maksimum peminjaman
- Buku tidak tersedia atau hanya untuk referensi
- Dan lain-lain

Contoh penanganan exception:
```bash
try {
    BookLoan loan = member.checkoutBook(bookItem);
    System.out.println("Peminjaman berhasil!");
} catch (LibraryException e) {
    System.out.println("Error: " + e.getMessage());
}
```

## Relasi Antar Kelas
Sistem ini mengimplementasikan tiga jenis relasi utama:

### 1. Inheritance (Pewarisan):

Person → Member → StudentMember/RegularMember
Person → Librarian


### 1. Composition (Komposisi):

Library → LibraryCollection
Book → BookItem


### 1. Aggregation (Agregasi):

Library ○─ Librarian
LibraryCollection ○─ BookCategory
BookCategory ○─ Book
Member ○─ BookLoan
BookItem ○─ BookLoan
Member ○─ Reservation


## Kontribusi
Kontribusi sangat diterima! Silakan buat pull request atau buka issue untuk diskusi.

## Lisensi
[MIT License](LICENSE)
