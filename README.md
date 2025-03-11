# Lendify
Lendify adalah sistem manajemen perpustakaan berbasis Java yang menerapkan konsep Pemrograman Berorientasi Objek (OOP) dengan relasi agregasi, komposisi, dan inheritance. Sistem ini memungkinkan perpustakaan untuk mengelola koleksi buku, anggota, dan transaksi peminjaman dengan cara yang efisien.

## Fitur Utama
- Manajemen anggota (reguler dan mahasiswa) dengan perbedaan hak dan batas peminjaman
- Manajemen koleksi buku, salinan fisik (book items), dan kategori
- Sistem peminjaman dan pengembalian buku dengan validasi status
- Reservasi buku yang sedang dipinjam dengan sistem antrian
- Perhitungan denda otomatis untuk keterlambatan pengembalian
- Pencarian buku berdasarkan judul, pengarang, dan kategori
- Pengelolaan akses pustakawan berbasis tingkat hak akses (BASIC, FULL, ADMIN)
- Pengelolaan status buku (tersedia, referensi saja, dipinjam, rusak, hilang)
- Perpanjangan masa peminjaman dengan validasi reservasi
- Pembayaran denda keterlambatan
- Pemantauan status anggota (aktif, tidak aktif, diblokir)
- Pengelolaan tanggal kadaluarsa keanggotaan dengan perpanjangan

## UML Class Diagram

[Diagram Class Lendify](https://dub.sh/Xq077wr)

## Struktur Class
1. Person: Class dasar untuk Member dan Librarian, berisi informasi umum seperti nama dan kontak.
2. Member: Mewakili anggota perpustakaan (extends Person), mengelola peminjaman dan reservasi.
3. StudentMember: Jenis anggota mahasiswa (extends Member) dengan atribut khusus seperti ID mahasiswa dan fakultas.
4. RegularMember: Jenis anggota reguler (extends Member) dengan atribut khusus seperti pekerjaan dan status premium.
5. Librarian: Petugas perpustakaan (extends Person), mengelola operasi perpustakaan seperti peminjaman dan pengembalian.
6. Library: Class utama yang mewakili perpustakaan, berisi koleksi dan data pustakawan.
7. LibraryCollection: Mengelola koleksi buku dan kategori buku.
8. Book: Mewakili informasi tentang buku seperti judul, pengarang, dan penerbit.
9. BookItem: Mewakili salinan fisik dari buku (item yang dapat dipinjam).
10. BookCategory: Mewakili kategori untuk mengelompokkan buku.
11. BookLoan: Mewakili transaksi peminjaman buku oleh anggota.
12. Reservation: Mewakili pemesanan buku yang sedang dipinjam.
13. Main: Class utama yang berisi method main untuk menjalankan program.

## Struktur Direktori
```
src/
├── com/
│   └── library/
│       ├── enums/
│       │   ├── BookFormat.java
│       │   ├── BookStatus.java
│       │   ├── Language.java
│       │   ├── LibrarianPermission.java
│       │   ├── LoanStatus.java
│       │   ├── MemberStatus.java
│       │   └── ReservationStatus.java
│       ├── exception/
│       │   ├── BookNotFoundException.java
│       │   ├── InactiveAccountException.java
│       │   ├── InvalidOperationException.java
│       │   ├── MaxBooksReachedException.java
│       │   └── ReferenceOnlyException.java
│       ├── model/
│       │   ├── Book.java
│       │   ├── BookCategory.java
│       │   ├── BookItem.java
│       │   ├── BookLoan.java
│       │   ├── Library.java
│       │   ├── LibraryCollection.java
│       │   ├── Librarian.java
│       │   ├── Member.java
│       │   ├── Person.java
│       │   ├── RegularMember.java
│       │   ├── Reservation.java
│       │   └── StudentMember.java
│       └── Main.java
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
javac -d bin src/com/library/enums/*.java src/com/library/exception/*.java src/com/library/model/*.java src/com/library/Main.java
```

4. Jalankan program:
```bash
java -cp bin com.library.Main
```

## Contoh Penggunaan
Berikut adalah contoh penggunaan dasar sistem Lendify:
```java
// Membuat perpustakaan
Library library = new Library("Perpustakaan Kota", "Jl. Pahlawan No. 10");

// Membuat pustakawan
Person adminPerson = new Person("P001", "John Smith", "Jl. Merdeka No. 5", "081234567890");
adminPerson.setEmail("john.smith@library.com");
Librarian librarian = new Librarian(adminPerson, "L001", "Kepala Pustakawan", 50000, LibrarianPermission.ADMIN);
library.addLibrarian(librarian);

// Membuat kategori buku
BookCategory fictionCategory = new BookCategory("Fiksi", "Buku-buku cerita fiksi");
library.addCategory(fictionCategory);

// Menambah buku
Book novel = new Book("978-3-16-148410-0", "Laskar Pelangi", "Andrea Hirata", "Bentang Pustaka", 2005);
library.addBook(novel);
library.addBookToCategory(novel, fictionCategory);

// Menambah item buku (salinan fisik)
BookItem novelCopy = librarian.addBookItem(novel, "LP001");

// Membuat anggota
Person memberPerson = new Person("P002", "Ani Wijaya", "Jl. Kebon Sirih No. 3", "087654321098");
Member member = librarian.addMember(memberPerson);

// Melakukan peminjaman
try {
    BookLoan loan = librarian.issueBook(member, novelCopy);
    System.out.println("Buku berhasil dipinjam sampai: " + loan.getDueDate());
} catch (Exception e) {
    System.out.println("Gagal meminjam buku: " + e.getMessage());
}
```

## Exception Handling
Sistem ini menggunakan beberapa class exception khusus yang masing-masing menangani kesalahan spesifik:

- BookNotFoundException: Dilemparkan ketika buku yang dicari tidak ditemukan dalam koleksi.
- InactiveAccountException: Dilemparkan ketika operasi dilakukan pada akun anggota yang tidak aktif.
- InvalidOperationException: Dilemparkan untuk operasi yang tidak valid (misalnya, ketika pustakawan tidak memiliki izin yang cukup).
- MaxBooksReachedException: Dilemparkan ketika anggota mencoba meminjam buku melebihi batas maksimum yang diizinkan.
- ReferenceOnlyException: Dilemparkan ketika mencoba meminjam buku yang hanya untuk referensi.

Semua Class exception ini merupakan turunan dari class Exception Java standar, sehingga memiliki semua fungsionalitas dari Class tersebut seperti pesan kesalahan, stack trace, dll.

### Exception Handling pada Method Peminjaman (checkoutBook)
Method checkoutBook di Class Member mengimplementasikan exception handling sebagai berikut:
```bash
public BookLoan checkoutBook(BookItem book) throws InactiveAccountException, MaxBooksReachedException {
    if (!isActive) {
        throw new InactiveAccountException("Your account is not active. Please contact the librarian.");
    }
    
    if (getCurrentBooksCount() >= getMaxBooks()) {
        throw new MaxBooksReachedException("You have reached the maximum number of books that can be checked out.");
    }
    
    BookLoan loan = new BookLoan(this, book);
    bookLoans.add(loan);
    book.checkout();
    return loan;
}
```
Method ini menangani dua kondisi error potensial:
- Jika akun anggota tidak aktif, InactiveAccountException dilemparkan.
- Jika anggota telah mencapai batas maksimum peminjaman buku, MaxBooksReachedException dilemparkan.

### Exception Handling pada Method Pengembalian (returnBook)
Method returnBook di Class Librarian mengimplementasikan exception handling sebagai berikut:
```bash
public void returnBook(BookLoan bookLoan) throws InvalidOperationException {
    if (bookLoan.getStatus() != LoanStatus.ACTIVE && bookLoan.getStatus() != LoanStatus.OVERDUE) {
        throw new InvalidOperationException("Cannot return a book that is not active or overdue: " + bookLoan.getStatus());
    }
    
    Member member = bookLoan.getMember();
    member.returnBook(bookLoan);
    
    // Check if there are any reservations for this book
    Book book = bookLoan.getBookItem().getBook();
    for (Reservation reservation : book.getReservations()) {
        if (reservation.getStatus() == ReservationStatus.PENDING) {
            reservation.setStatus(ReservationStatus.FULFILLED);
            break;
        }
    }
}
```
Method ini memeriksa apakah status peminjaman adalah ACTIVE atau OVERDUE. 
Jika tidak, method melemparkan InvalidOperationException. 
Ini memastikan bahwa hanya buku yang benar-benar dipinjam yang dapat dikembalikan.

### Exception Handling pada Method Pembayaran Denda (payFine)
Method payFine di Class Member cukup sederhana dan tidak melemparkan exception secara langsung:
```bash
public void payFine(double amount) {
    totalFinesPaid += amount;
}
```
Meskipun tidak ada exception khusus yang dilemparkan di sini, method ini dapat menjadi bagian dari blok try-catch yang lebih besar untuk menangani exception dari operasi database atau sistem eksternal dalam implementasi yang lebih kompleks.

### Exception Handling pada Method Reservasi (reserveBook)
Method reserveBook di Class Member mengimplementasikan exception handling sebagai berikut:

```bash
public Reservation reserveBook(Book book) throws InactiveAccountException {
    if (!isActive) {
        throw new InactiveAccountException("Your account is not active. Please contact the librarian.");
    }
    
    Reservation reservation = new Reservation(this, book);
    reservations.add(reservation);
    return reservation;
}
```
Method ini memeriksa apakah akun anggota aktif sebelum membuat reservasi. Jika tidak aktif, method melemparkan InactiveAccountException.

### Exception Handling pada Method addBookItem di Librarian
Method addBookItem di class Librarian mengimplementasikan exception handling sebagai berikut:
```bash
public BookItem addBookItem(Book book, String barcode) throws InvalidOperationException {
    if (permission == LibrarianPermission.BASIC) {
        throw new InvalidOperationException("You do not have permission to add book items. Required: FULL or ADMIN.");
    }
    
    BookItem bookItem = new BookItem(book, barcode);
    book.addBookItem(bookItem);
    return bookItem;
}
```
Method ini memeriksa apakah pustakawan memiliki izin yang cukup untuk menambahkan item buku. Jika pustakawan hanya memiliki izin BASIC, method melemparkan InvalidOperationException.

### Try-Catch di Main Class
Class Main menggunakan try-catch untuk menangani berbagai exception yang mungkin dilemparkan selama eksekusi program:
```bash
try {
    // Create a library
    Library library = new Library("Perpustakaan Kota", "Jl. Utama No. 123, Kotaville");
    // ... rest of the code ...
} catch (Exception e) {
    System.out.println("Terjadi kesalahan: " + e.getMessage());
    e.printStackTrace();
}
```
Blok try-catch ini melingkupi seluruh kode utama, memastikan bahwa program tidak akan crash akibat exception yang tidak tertangani. Jika terjadi exception, pesan kesalahan akan ditampilkan dan stack trace akan dicetak untuk membantu debugging.

### Nested Try-Catch untuk Operasi Spesifik
Class Main juga menggunakan nested try-catch untuk menangani exception yang mungkin terjadi dalam operasi tertentu:
```bash
try {
    BookLoan loan1 = assistantLibrarian.issueBook(regularMember, book1Copy1);
    System.out.println("Meminjamkan '" + book1.getTitle() + "' kepada " + regularMember.getName());
    System.out.println("  Tanggal jatuh tempo: " + dateFormat.format(loan1.getDueDate()));
    
    // ... more code ...
    
    // Try to issue reference book (should throw exception)
    try {
        BookLoan loan3 = assistantLibrarian.issueBook(studentMember, book2Copy3);
    } catch (ReferenceOnlyException e) {
        System.out.println("Pengecualian: " + e.getMessage());
    }
    
    // ... rest of the code ...
} catch (Exception e) {
    System.out.println("Terjadi kesalahan: " + e.getMessage());
    e.printStackTrace();
}
```
Dalam contoh ini, ada try-catch bersarang yang spesifik menangani ReferenceOnlyException saat mencoba meminjam buku referensi. Ini memungkinkan program untuk menangani kesalahan ini secara khusus dan melanjutkan eksekusi, sementara exception lain akan ditangani oleh try-catch luar.
Contoh lain nested try-catch adalah saat memproses reservasi:
```bash
try {
    assistantLibrarian.processReservation(reservation);
    System.out.println("Reservasi diproses untuk " + studentMember.getName());
    System.out.println("Buku '" + book2.getTitle() + "' telah dipinjamkan");
    System.out.println("Status Reservasi: " + reservation.getStatus());
} catch (InvalidOperationException e) {
    System.out.println("Tidak dapat memproses reservasi: " + e.getMessage());
}
```
Ini menangani InvalidOperationException yang mungkin dilemparkan saat mencoba memproses reservasi. Jika gagal, program akan menampilkan pesan kesalahan spesifik tetapi tetap berjalan.

### Penggunaan Reflection dengan Exception Handling
Class Main juga menggunakan exception handling saat memanipulasi tanggal menggunakan reflection API:
```bash
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
```

## Penjelasan Relasi Antar Class
### Relasi Inheritance (Pewarisan)
1. Person ← Member
```bash
Jenis Relasi: Inheritance
Multiplicity: 1:1 (Setiap Member adalah tepat satu Person)
Navigability: Unidirectional dari Member ke Person
Penjelasan: Member adalah jenis spesifik dari Person. Member mewarisi semua atribut dan perilaku dari Person, seperti nama, alamat, dan informasi kontak, namun memiliki atribut dan metode tambahan terkait keanggotaan seperti tanggal pendaftaran dan tanggal kedaluwarsa.
```
2. Member ← StudentMember
```bash
Jenis Relasi: Inheritance
Multiplicity: 1:1 (Setiap StudentMember adalah tepat satu Member)
Navigability: Unidirectional dari StudentMember ke Member
Penjelasan: StudentMember adalah jenis spesifik dari Member. StudentMember memiliki atribut dan metode tambahan yang berkaitan dengan mahasiswa, seperti NIM, fakultas, dan jurusan.
```
3. Member ← RegularMember
```bash
Jenis Relasi: Inheritance
Multiplicity: 1:1 (Setiap RegularMember adalah tepat satu Member)
Navigability: Unidirectional dari RegularMember ke Member
Penjelasan: RegularMember adalah jenis spesifik dari Member. RegularMember memiliki atribut dan metode tambahan yang berkaitan dengan anggota umum, seperti pekerjaan dan status premium.
```
4. Person ← Librarian
```bash
Jenis Relasi: Inheritance
Multiplicity: 1:1 (Setiap Librarian adalah tepat satu Person)
Navigability: Unidirectional dari Librarian ke Person
Penjelasan: Librarian adalah jenis spesifik dari Person. Librarian memiliki atribut dan metode tambahan yang berkaitan dengan tugas sebagai pustakawan, seperti ID staf, posisi, dan wewenang.
```
### Relasi Composition (Komposisi)
1. Library → LibraryCollection
```bash
Jenis Relasi: Composition
Multiplicity: 1:1 (Setiap Library memiliki tepat satu LibraryCollection)
Navigability: Unidirectional dari Library ke LibraryCollection
Penjelasan: LibraryCollection adalah bagian yang tidak terpisahkan dari Library. LibraryCollection tidak dapat ada tanpa Library. Jika Library dihapus, maka LibraryCollection juga dihapus.
```
2. Book → BookItem
```bash
Jenis Relasi: Composition
Multiplicity: 1:* (Setiap Book dapat memiliki banyak BookItem)
Navigability: Unidirectional dari Book ke BookItem
Penjelasan: BookItem adalah bagian yang tidak terpisahkan dari Book. BookItem merepresentasikan salinan fisik dari sebuah buku. Satu buku dapat memiliki beberapa salinan fisik. Jika Book dihapus, maka semua BookItem terkait juga dihapus.
```
### Relasi Aggregation (Agregasi)
1. Library ○─ Librarian
```bash
Jenis Relasi: Aggregation
Multiplicity: 1:* (Setiap Library dapat memiliki banyak Librarian)
Navigability: Unidirectional dari Library ke Librarian
Penjelasan: Library memiliki Librarian, tetapi Librarian dapat ada tanpa Library tertentu. Librarian dapat dipindahkan ke Library lain tanpa mempengaruhi eksistensinya.
```
2. LibraryCollection ○─ BookCategory
```bash
Jenis Relasi: Aggregation
Multiplicity: 1:* (Setiap LibraryCollection dapat memiliki banyak BookCategory)
Navigability: Unidirectional dari LibraryCollection ke BookCategory
Penjelasan: LibraryCollection memiliki BookCategory, tetapi BookCategory dapat ada tanpa LibraryCollection tertentu. BookCategory dapat digunakan di LibraryCollection lain.
```
3. BookCategory ○─ Book
```bash
Jenis Relasi: Aggregation
Multiplicity: 1:* (Setiap BookCategory dapat memiliki banyak Book)
Navigability: Unidirectional dari BookCategory ke Book
Penjelasan: BookCategory memiliki Book, tetapi Book dapat ada tanpa dikategorikan. Book dapat masuk ke dalam beberapa kategori dan dapat dipindahkan antar kategori.
```
4. Member ○─ BookLoan
```bash
Jenis Relasi: Aggregation
Multiplicity: 1:* (Setiap Member dapat memiliki banyak BookLoan)
Navigability: Unidirectional dari Member ke BookLoan
Penjelasan: Member memiliki BookLoan yang merepresentasikan peminjaman buku. Member dapat meminjam banyak buku, dan catatan peminjaman (BookLoan) tetap ada bahkan setelah buku dikembalikan untuk tujuan pencatatan.
```
5. BookItem ○─ BookLoan
```bash
Jenis Relasi: Aggregation
Multiplicity: 1:* (Setiap BookItem dapat terlibat dalam banyak BookLoan seiring waktu)
Navigability: Unidirectional dari BookItem ke BookLoan
Penjelasan: BookItem terkait dengan BookLoan yang merepresentasikan peminjaman. Satu BookItem dapat dipinjam berkali-kali oleh anggota yang berbeda seiring waktu.
```
6. Member ○─ Reservation
```bash
Jenis Relasi: Aggregation
Multiplicity: 1:* (Setiap Member dapat memiliki banyak Reservation)
Navigability: Unidirectional dari Member ke Reservation
Penjelasan: Member dapat membuat reservasi untuk buku yang sedang dipinjam. Member dapat membuat banyak reservasi untuk buku yang berbeda.
```

## Author
### Kelompok 2 Kelas B Pemrograman Berorientasi Objek(PBO) - Lendify (Sistem Peminjaman Buku di Perpustakaan)
- Nama    : Dandy Faishal Fahmi     - 24060123140136
- Nama    : Fauzan Hadi             - 24060123140176
- Nama    : Gaza Al-ghazali Chansa  - 24060123140183
- Nama    : Diva Arvis Permata      - 24060123130102
- Nama    : Ganen                   - 24060123140148


## Kontribusi
Kontribusi sangat diterima! Silakan buat pull request atau buka issue untuk diskusi.

## Lisensi
[MIT License](LICENSE)
