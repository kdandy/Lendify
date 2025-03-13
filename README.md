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

[Diagram Class Lendify](https://mermaid.live/view#pako:eNq1WuuOIrcSfpUWUiQmw7wAWkVigNklhwEEs6uco_1jGgOd7QvH3T1ZtMk8e3y_lhvIZufH0O0q21Wfy-Wqcn_rpdUO94a9NEd1PcnQgaDic5nQv59-SmblEZOsQWWKRdsKk7oqk3d_Pjwkz7jYYiLaxbNo3zTtDpdNjLzGhzZHxCbbo86zLUEkQ6UgaVHGVXGq6qzJKkkRjOfkZ93pPK7yHKeG5bGqvnA6e5g1uPDGHB0OBB9QOGYVChJMwZnYwGPU4ENFzmZO1aJZHBRU47xClpxMPIBk9VnjGpNXS1rxny-cgvCbaGN_D5uGZOUhyXZhW4kKHLai3Y7gug4Jp2NV4kVrFswm4gJluWm-F5L078KmbDdMRKcBl8C8yZlNgzWjarQHPOBmtuvfKZJLWdChY7SRmChGXplpYyxTpi5IrOXUtmqU77WyV4BxKSE8rUFeWyIIFKiPEJGvC8j3V2g-0swA8yk4ZWYb0YSad0I3TlY3hJsja_Dp-OspI2ePsq2qHKMyyeoR3UOvNklIsGlQ09ZJzX8s6pzO9aZ2xluylU8Bi7VH3qiI-sVm3FXtNsdJUzUof8pKXK-Qg6CQxDFg2XTidjyUm22gsYmY6LMkx0xp7UHI-FzAuMFpIAG6ApKRJLhubwEoI9sAuzwKV87mIu0LbADVvDbkLvuLgzDrIKD3rNXoZ2xGKArvHqEw4j9aa5BVKi-syUUg5E9RntKDqcFM4j6zsKF2zJDomt-oCaqYHnH6pWo5yuGwrrfnHQhuWlJy9pxSDE8o8wmdubSoqNqyUZOHfASX-A-hfn3MTv2iKpsjRSQrG4iZLSj25KV8wQEkcaDRQW7R-ta-czqFU7FNgr6y4Tl2VJyAyBSfoDNMH7eEqsahrccMAocL8HNObAK5u1owzIBDc4_oejfnkLDDJ0SaAtvSPVApkjNGZLlnc1q97h0ZHDfjUoR3UWY7MKKZY1KKBPmf7x1rYKll2iyNhPX43kYOG3N5T2KSGHmip4xx_NcIEFpEbUsQqAi6CCWQjyTEa0kXYgP2sKUNoPvXdgNg506QDdl5labtydvKJqI75dUZk4UbJZrDe0VwkbWFJZ0zn2PTLsW3QyMGZMO39h04optWLbE5Llxkl3qoeNhnBgZ59BzwUVw7kwBqw4GcNSmkGhwySjlOvs4_0uB0tgQ7VbTfQy7VzehM8Ph7lZWU7kWPMnCrEbWJsxP5yblpXFZkdc2Ss5N-tFTSjI6FmlY_vpNyW2mJlBcy1u8YZiB1Ugf4wBJ_mADqBW6XTxDNauR0MfqvBm04BN1w6cDohg2vxRIxYSBtEJRxYT1UYFNWkgfAg-GeENPFEh7XiHwJ6aA7zdpU_GaFR4NkiwgrpVgSulUH1RnMJXR87niUuhVRmO_5tnLgGyJJlTF1RZPtaUeXng9Z7itHuSZrcjtfb5tjRSyDpkhn9RETN1KInHPbHKVfKD8cnEABaVGJeFSDHmjvB8akSmlqfXNUGvVsZ8ivXVtGCWtHqVepMhmstsG3JFePdeBmzv3OAsrdv1YRMTKbzR3U2X5Q-YMyGKeqwbC2acxUbu1VU1tNj4_nF2blfcfW7Yz4Degy4juh726IS51UfbCfyoehUzYEusft0jKqb1DBRBRLwFqKmu8tkXJk2OZ7RieHbWBJpbucH93R7wOx-ndhtcGtNHhlg7EWxuHS0oa-2E1To96jk40OdXlZwKFv7ialfql0T9vVXjuKgnJW3m5OtjXy5fh_i8m5w3wB--PFdajeXG-BnIJvKsBX8n0DBIbqQPEyat6ecr_NcisoF69Tkp08z8q6lrxwutyv0AGD24GdJG9JRv_bZEZ5qkiBmmTPf-yuqDy0dLgklw8Wutzg7vwGBo7x2t9zqv6wkQc2hlYXBptkEEAIKxPYDDQE1Hbkk1-w3zwuYqeMcLyxE0q42Fhwq1TpZDAmAyY6E6Nx9FbBNh9wEGY_rsviFuUVQDhc_TsbPJdDwcdHCu2qlkja6w0es_BhBhzHkRMMiJo11IH9xPkt5DtCwtpdA8ACwT7uohgThUs9AvrQeEFuvQyAYXfmBNmF0NSLZi-xsw3wirIc0Vym08IiLprfKwJuWqYrnqfjwQJ8YaOEAKlrvMcElylelvnZz-ZPLT1nUI3hdP5EstQXI3YNJGXPq9QvXcVvlu6vyta8g1VQY75A-PahdbWrLmMUSHARyMMpemmzsgCDM_IVAy2WkJs7H4Ol52MkgJFKVtedUm3ZYx-pp-57GFdrYr91dxRqcgvpzOe9ix6jNry1lfbKjrr945X3Teqepx8hZWX_UqrpfDtwdb4Jhjz39lhesgh51-sTxktn5dV54C0OPwIWq2M4QMmCd-HVUh60J9wGNRnho3ixBbrH3sHNorQCe7R9VkZa3ZvmByZ-xNXdK_WuKf5A1859qKQkfRf3hnCBisUxCgrY9Uy6iGsNC0znt5URt_UkAbrs1gxuvp_Rs5v16bhK5tKwdel0LloutYK3uCIjasid1ctXTKiBwZ7WvZCGQMFfG1zu1ILs0FlHP85o4P2Qrn9BjsaqlTll-67t5YcOcpvogby9YkkQ2wJ29Q7aBTKVj36jEK-IX94iwPHuDt5RLdd2GujYaSoBN_SNgn_Xft2HPRJha6XfvcM0Icbi45NffjGE0fhl9mlq3mcLv2XzcbOaLibTiWl6nI_G_5nPNi-qseuGyLqluUagx9FmNjavTx_nc0vayfNs0XlEyNz9mpk-jNaT8fLTdG2aVqPVdP1IlTNN08fl0nodfZzMlqYJUlxVCa6RYbp4T3H8YOM_WS6mm9loYdp-Ha1GtM1ak6f1dDG2er2frp_tHsuXD0qtCE63mMin0Ww-epxb86-pNOtPtknMl1RE533zYt4mo-fR-y5jMSfjPzHa8fJ5NZ--2POzdZ18nEICdfvHG8Rg22K2eO8Y69NsPrflGI8W46nbNP1tNVtrMHqDHh27oOdMb9jjs37uNUdMI7_ekD7uEPnyufe5ZHw0Z6825zLtDRvS4kGPVO3h2BvuUV7TN3GdIz_pVSwnVP6vqgrNhHdZU5Fn-Q0w-_nrbylGt-g)

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
│   ├── icons/
│   │   ├── UNDIPOfficial.png
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
│       ├── gui/
│       │   ├── utils/
│       │   │   ├── DialogUtils.java
│       │   │   ├── GUIUtils.java
│       │   │   └── TableModels.java
│       │   ├── BookPanel.java
│       │   ├── CategoryPanel.java
│       │   ├── LendifyGUI.java
│       │   ├── LibrarianPanel.java
│       │   ├── LoanPanel.java
│       │   ├── LoginPanel.java
│       │   ├── MainPanel.java
│       │   ├── MemberPanel.java
│       │   ├── ReservationPanel.java
│       │   ├── SearchPanel.java
│       │   └── StatisticsPanel.java
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
git clone https://github.com/kdandy/lendify.git
cd lendify
```

2. Buat direktori untuk file hasil kompilasi:
```bash
mkdir -p bin
```

3. Kompilasi semua file Java:
```bash
javac -d bin src/com/library/enums/*.java src/com/library/exception/*.java src/com/library/model/*.java src/com/library/gui/*.java src/com/library/gui/utils/*.java src/com/library/Main.java
```

4. Jalankan program:
```bash
java -cp bin com.library.Main
```

## Penggunaan Interface Interaktif

Setelah menjalankan program, Anda akan melihat tampilan menu utama. Berikut adalah panduan penggunaan interface interaktif:

### Menu Utama
Menu utama menampilkan berbagai opsi untuk mengelola perpustakaan:
```
==== MENU UTAMA ====
1. Kelola Pustakawan
2. Kelola Kategori Buku
3. Kelola Buku
4. Kelola Anggota
5. Kelola Peminjaman dan Pengembalian
6. Kelola Reservasi
7. Cari Buku
8. Lihat Statistik Perpustakaan
9. Jalankan Demo Mode
0. Keluar
```

### Demo Mode
Untuk pengguna pertama kali, pilih opsi "9. Jalankan Demo Mode" untuk mengisi database dengan data sampel. Ini akan memungkinkan Anda untuk menguji fitur-fitur tanpa perlu membuat data manual.

### Kelola Pustakawan
Menu ini memungkinkan Anda untuk:
- Melihat daftar pustakawan
- Menambah pustakawan baru dengan tingkat akses berbeda (BASIC, FULL, ADMIN)
- Mengubah informasi pustakawan
- Menghapus pustakawan

### Kelola Kategori Buku
Menu ini memungkinkan Anda untuk:
- Melihat daftar kategori
- Menambah kategori baru
- Mengubah informasi kategori
- Menghapus kategori

### Kelola Buku
Menu ini memungkinkan Anda untuk:
- Melihat daftar buku
- Menambah buku baru
- Menambah salinan buku (BookItem)
- Mengubah informasi buku
- Melihat detail buku termasuk salinan yang tersedia
- Menghapus buku

### Kelola Anggota
Menu ini memungkinkan Anda untuk:
- Melihat daftar anggota
- Menambah anggota baru (mahasiswa atau reguler)
- Mengubah informasi anggota
- Melihat detail anggota termasuk peminjaman aktif
- Memperpanjang keanggotaan
- Mengaktifkan/menonaktifkan anggota

### Kelola Peminjaman dan Pengembalian
Menu ini memungkinkan Anda untuk:
- Pinjamkan buku kepada anggota
- Kembalikan buku
- Lihat daftar peminjaman aktif
- Lihat riwayat peminjaman
- Perpanjang masa peminjaman

### Kelola Reservasi
Menu ini memungkinkan Anda untuk:
- Membuat reservasi buku yang sedang dipinjam
- Melihat daftar reservasi
- Memproses reservasi ketika buku tersedia
- Membatalkan reservasi

### Cari Buku
Menu ini memungkinkan Anda untuk mencari buku berdasarkan:
- Judul
- Pengarang
- Kategori

### Statistik Perpustakaan
Menampilkan berbagai statistik perpustakaan seperti:
- Jumlah buku
- Jumlah anggota
- Jumlah peminjaman aktif
- Jumlah denda yang terkumpul

## Contoh Penggunaan
Berikut adalah contoh alur penggunaan dasar sistem Lendify:

1. Jalankan program dan pilih mode demo (opsi 9) untuk mengisi database dengan data sampel.
2. Tambahkan kategori buku menggunakan menu "Kelola Kategori Buku".
3. Tambahkan buku dan salinannya menggunakan menu "Kelola Buku".
4. Tambahkan anggota menggunakan menu "Kelola Anggota".
5. Pinjamkan buku kepada anggota melalui menu "Kelola Peminjaman dan Pengembalian".
6. Kembalikan buku yang dipinjam dan proses denda jika ada.
7. Buat reservasi untuk buku yang sedang dipinjam menggunakan menu "Kelola Reservasi".

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
- Nama    : Dandy Faishal Fahmi         - 24060123140136
- Nama    : Fauzan Hadi                 - 24060123140176
- Nama    : Gaza Al-ghazali Chansa      - 24060123140183
- Nama    : Diva Arvis Permata          - 24060123130102
- Nama    : Ganendra Dzahwan Yulianto   - 24060123140148


## Kontribusi
Kontribusi sangat diterima! Silakan buat pull request atau buka issue untuk diskusi.

## Lisensi
[MIT License](LICENSE)
