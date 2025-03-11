# Lendify

Lendify adalah aplikasi sistem peminjaman buku di perpustakaan yang dirancang menggunakan bahasa pemrograman Java. Aplikasi ini memanfaatkan prinsip Pemrograman Berorientasi Objek (OOP) dan menerapkan relasi seperti agregasi, komposisi, dan inheritance dalam desainnya.

## Fitur Utama
- Manajemen data buku (tambah, hapus, edit, cari)
- Manajemen data anggota (tambah, hapus, edit)
- Proses peminjaman dan pengembalian buku
- Penerapan exception handling untuk validasi data
- Tampilan hasil melalui console Java

## Struktur Kelas
1. **Book**: Mewakili data buku dalam perpustakaan.
2. **Member**: Mewakili data anggota perpustakaan.
3. **Loan**: Mewakili proses peminjaman buku.
4. **Library**: Menyimpan koleksi buku dan anggota.
5. **Person**: Superclass untuk Member.
6. **Address**: Mewakili alamat anggota (komposisi dengan Member).
7. **Main**: Class utama untuk menjalankan program.

## Cara Menjalankan Aplikasi
1. Clone repositori:
```bash
git clone https://github.com/kdandy/Lendify.git
```

2. Masuk ke direktori proyek:
```bash
cd Lendify
```

3. Compile semua file Java:
```bash
javac *.java
```

4. Jalankan program:
```bash
java Main
```

## Struktur Direktori
```
Lendify/
├── Book.java
├── Member.java
├── Loan.java
├── Library.java
├── Person.java
├── Address.java
├── Main.java
└── README.md
```

## Contoh Penggunaan
```java
// Contoh pembuatan objek anggota dan buku
Member member = new Member("123", "Alice", new Address("Jalan Merdeka", "Semarang", "50275"));
Book book = new Book("001", "Pemrograman Java", "Budi Santoso");

// Proses peminjaman buku
Loan loan = new Loan(member, book);
loan.borrowBook();
```

## Exception Handling
- Validasi input data saat pembuatan objek.
- Exception untuk penanganan kesalahan saat peminjaman buku yang tidak tersedia.

## Kontribusi
Kontribusi sangat diterima! Silakan buat pull request atau buka issue untuk diskusi.

## Lisensi
[MIT License](LICENSE)
