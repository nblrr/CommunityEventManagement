<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Event;
use App\Models\Community;
use App\Models\Category;
use App\Models\User;
use App\Models\EventRegistration;
use App\Models\EventRating;
use App\Models\ForumMessage;
use App\Models\Notification;
use App\Models\TrustedApplication;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class EventSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $communities = Community::all();
        $allUsers = User::all();

        if ($communities->isEmpty()) {
            return;
        }

        $allRegistrations = [];
        $allRatings = [];
        $allNotifications = [];

        // Predefined realistic event titles and descriptions per community
        $eventsDataMap = [
            // Technology
            'Android Developers Hub' => [
                ['title' => 'Kotlin Jetpack Compose Bootcamp', 'desc' => 'Bootcamp intensif membangun UI deklaratif Android modern menggunakan Jetpack Compose.'],
                ['title' => 'Android Performance Profiling', 'desc' => 'Belajar menganalisis memory leak, rendering lag, dan optimasi battery usage di Android.'],
                ['title' => 'Kotlin Coroutines & Flow', 'desc' => 'Menguasai asynchronous programming dan reactive streams menggunakan Kotlin Coroutines.'],
                ['title' => 'Clean Architecture in Android', 'desc' => 'Bagaimana mengimplementasikan MVVM/MVI dengan Clean Architecture di project Android.'],
                ['title' => 'Dependency Injection with Hilt', 'desc' => 'Workshop praktis mengelola dependensi aplikasi menggunakan Dagger Hilt.'],
                ['title' => 'Android Testing Mastery', 'desc' => 'Belajar membuat unit test, integration test, dan UI test menggunakan Espresso dan MockK.'],
                // Ongoing & Upcoming
                ['title' => 'KMP (Kotlin Multiplatform) Intro', 'desc' => 'Eksplorasi membagikan business logic antara Android dan iOS menggunakan Kotlin.'],
                ['title' => 'Modern Android Build with Gradle', 'desc' => 'Tips mempercepat build time gradle dan mengelola version catalog.'],
                ['title' => 'Jetpack Compose Animation Clinic', 'desc' => 'Membuat micro-interactions dan transisi screen yang smooth di Compose.'],
                ['title' => 'Android Security Best Practices', 'desc' => 'Mengamankan data lokal aplikasi menggunakan EncryptedSharedPreferences dan Biometric.'],
            ],
            'Web Development Circle' => [
                ['title' => 'Vite + React Best Practices', 'desc' => 'Sesi berbagi mengenai optimasi build react menggunakan Vite dan struktur project berskala besar.'],
                ['title' => 'React & Vite Performance', 'desc' => 'Teknik profiling, lazy loading, dan code splitting untuk mereduksi load time aplikasi web React.'],
                ['title' => 'TailwindCSS Advanced Techniques', 'desc' => 'Mengoptimalkan konfigurasi Tailwind, custom utility, dan responsive design.'],
                ['title' => 'Backend API Security Workshop', 'desc' => 'Praktik terbaik mengamankan API backend dari celah keamanan umum menggunakan OWASP standard.'],
                ['title' => 'Tech Meetup: Dockerizing Laravel', 'desc' => 'Belajar dasar containerization menggunakan Docker untuk aplikasi PHP Laravel.'],
                ['title' => 'Vue.js 3 Composition API', 'desc' => 'Migrasi dari Options API ke Composition API untuk kode yang lebih modular dan clean.'],
                // Ongoing & Upcoming
                ['title' => 'Intro to Next.js App Router', 'desc' => 'Eksplorasi routing sistem baru di Next.js untuk optimasi rendering di sisi server.'],
                ['title' => 'TypeScript Deep Dive for Web', 'desc' => 'Memahami advanced types, generics, dan integrasi TypeScript di React.'],
                ['title' => 'Web Performance Optimization 101', 'desc' => 'Langkah-langkah taktis memperbaiki Core Web Vitals pada website modern.'],
                ['title' => 'Building Real-time App with Socket.io', 'desc' => 'Membangun aplikasi chat real-time menggunakan Node.js dan WebSockets.'],
            ],
            'Cyber Security Society' => [
                ['title' => 'Ethical Hacking & Pentesting', 'desc' => 'Mengenal metodologi penetration testing dan etika dasar dalam cyber security.'],
                ['title' => 'OWASP Top 10 Vulnerabilities', 'desc' => 'Memahami celah keamanan web paling kritis dan cara melakukan mitigasinya.'],
                ['title' => 'Network Forensics Workshop', 'desc' => 'Belajar menganalisis paket data jaringan menggunakan Wireshark untuk deteksi intrusi.'],
                ['title' => 'Linux Privilege Escalation', 'desc' => 'Eksplorasi teknik meningkatkan hak akses pada sistem operasi Linux saat audit.'],
                ['title' => 'Web Application Firewalls (WAF)', 'desc' => 'Konfigurasi dan implementasi WAF untuk memproteksi aplikasi web dari exploit.'],
                ['title' => 'Cryptography Fundamentals', 'desc' => 'Memahami algoritma enkripsi simetris, asimetris, dan mekanisme hashing.'],
                // Ongoing & Upcoming
                ['title' => 'CTF (Capture The Flag) Warmup', 'desc' => 'Latihan bersama menyelesaikan tantangan Jeopardy CTF untuk mengasah skill hacking.'],
                ['title' => 'Social Engineering Defenses', 'desc' => 'Cara mengenali serangan phishing dan mengedukasi organisasi agar terhindar dari social engineering.'],
                ['title' => 'Kubernetes Security hardening', 'desc' => 'Best practices mengamankan cluster Kubernetes dan container runtime.'],
                ['title' => 'Secure Coding in PHP & Go', 'desc' => 'Panduan menulis kode yang aman dari SQL injection, XSS, dan CSRF.'],
            ],

            // Sports
            'Solo Runners Club' => [
                ['title' => 'Weekly Sprint Session Manahan', 'desc' => 'Latihan sprint interval di track Stadion Manahan Solo untuk melatih kapasitas paru-paru.'],
                ['title' => 'Marathon Prep Talk & Nutrition', 'desc' => 'Diskusi bersama pelatih mengenai persiapan fisik dan pemenuhan nutrisi sebelum lari marathon.'],
                ['title' => 'Weekly Run Benteng Vastenburg', 'desc' => 'Lari sore bersama mengelilingi Benteng Vastenburg dilanjutkan dengan pendinginan santai.'],
                ['title' => 'Cardio Endurance Training Guide', 'desc' => 'Tips meningkatkan daya tahan jantung bagi pemula agar tidak mudah lelah.'],
                ['title' => 'Sunday Morning Run Solo Car Free Day', 'desc' => 'Rutinitas lari pagi sejauh 5K menyusuri Jalan Slamet Riyadi Solo saat CFD.'],
                ['title' => 'Night Run Solo Heritage', 'desc' => 'Lari malam menyusuri jalan-jalan bersejarah di kota Solo dengan rute santai.'],
                // Ongoing & Upcoming
                ['title' => 'Solo Trail Run 10K', 'desc' => 'Petualangan lari lintas alam menyusuri perbukitan di kawasan Karanganyar.'],
                ['title' => 'Pace Management Workshop', 'desc' => 'Belajar mengatur tempo lari (pace) agar energi tidak cepat habis di jarak jauh.'],
                ['title' => 'Running Gear and Shoes Review', 'desc' => 'Bedah jenis sepatu lari (daily trainer vs carbon plate) sesuai gaya lari Anda.'],
                ['title' => 'Solo Half Marathon Practice', 'desc' => 'Latihan bersama dengan target jarak 15K sebagai persiapan Half Marathon.'],
            ],
            'Basketball Community' => [
                ['title' => 'Weekly Pick-up Games', 'desc' => 'Pertandingan basket santai antar anggota komunitas untuk melatih kekompakan.'],
                ['title' => 'Shooting Clinic: Form & Precision', 'desc' => 'Latihan membetulkan form shooting dan meningkatkan akurasi tembakan tiga angka.'],
                ['title' => 'Defense Fundamentals Workshop', 'desc' => 'Belajar teknik man-to-man defense, zone defense, dan komunikasi di lapangan.'],
                ['title' => 'Dribbling and Crossover Drill', 'desc' => 'Latihan meningkatkan ball handling dan kelincahan melewati defender.'],
                ['title' => 'Physical Conditioning for Basketball', 'desc' => 'Latihan fisik khusus melatih vertical jump, sprint, dan stamina bermain basket.'],
                ['title' => '3on3 Half-Court Mini Tournament', 'desc' => 'Turnamen 3 lawan 3 setengah lapangan internal komunitas.'],
                // Ongoing & Upcoming
                ['title' => 'Fast Break Strategy Session', 'desc' => 'Latihan taktik transisi cepat dari defense ke offense untuk poin mudah.'],
                ['title' => 'Rebounding and Boxing Out Clinic', 'desc' => 'Teknik memenangkan perebutan bola pantul (rebound) di bawah ring.'],
                ['title' => 'Referees Rules and Violations Study', 'desc' => 'Bedah aturan resmi basket FIBA agar paham saat bertanding kompetitif.'],
                ['title' => 'Basketball Cup Solo 2026', 'desc' => 'Turnamen basket antar komunitas se-Karesidenan Surakarta.'],
            ],
            'Badminton Society' => [
                ['title' => 'Weekly Double Play Senayan', 'desc' => 'Latihan tanding ganda badminton di Hall Senayan dengan fokus rotasi pemain.'],
                ['title' => 'Badminton Technique Class', 'desc' => 'Pelatihan teknik dasar grip raket, footwork, dan cara melakukan lob yang benar.'],
                ['title' => 'Mixed Doubles Session Pluit', 'desc' => 'Tanding persahabatan ganda campuran untuk menguji kekompakan komunikasi partner.'],
                ['title' => 'Singles Drill Session Palmerah', 'desc' => 'Latihan fisik khusus pemain tunggal yang menuntut kecepatan dan stamina tinggi.'],
                ['title' => 'Friendly Match Senayan GBK', 'desc' => 'Tanding santai antar anggota komunitas di lapangan badminton GBK.'],
                ['title' => 'Badminton Smash & Drop Shot Drill', 'desc' => 'Fokus latihan teknik melakukan smash tajam dan drop shot tipis di depan net.'],
                // Ongoing & Upcoming
                ['title' => 'Fun Tournament BSD Cup 2026', 'desc' => 'Turnamen badminton internal komunitas dengan hadiah menarik untuk kategori ganda.'],
                ['title' => 'Footwork Mastery Clinic', 'desc' => 'Sesi intensif melatih kelincahan kaki agar menjangkau semua sudut lapangan.'],
                ['title' => 'Serving and Netting Workshop', 'desc' => 'Meningkatkan akurasi servis pendek dan tipisnya netting untuk mengontrol permainan.'],
                ['title' => 'Badminton Physics & Strategy', 'desc' => 'Diskusi santai mengenai arah angin lapangan, ketegangan senar raket, dan strategi bertahan.'],
            ],

            // Art & Design
            'Indonesia Digital Artists' => [
                ['title' => 'Digital Painting Demo', 'desc' => 'Demonstrasi melukis lanskap secara digital menggunakan Photoshop dari nol.'],
                ['title' => 'Blender 3D for Beginners', 'desc' => 'Pengenalan antarmuka Blender, teknik modeling dasar, dan pemberian material.'],
                ['title' => 'Character Design Critique', 'desc' => 'Sesi review dan feedback portofolio desain karakter buatan anggota komunitas.'],
                ['title' => 'Webtoon Drawing Class', 'desc' => 'Langkah-langkah merancang storyboard, paneling, dan pewarnaan komik web.'],
                ['title' => 'Watercolor Digital Techniques', 'desc' => 'Meniru efek cat air tradisional menggunakan brush digital di Clip Studio Paint.'],
                ['title' => 'Vector Illustration Workshop', 'desc' => 'Membuat flat illustration menggunakan Adobe Illustrator untuk aset microstock.'],
                // Ongoing & Upcoming
                ['title' => 'Concept Art Masterclass: Sci-Fi', 'desc' => 'Mempelajari pembuatan konsep kendaraan dan kota masa depan (Sci-Fi concept).'],
                ['title' => 'Anatomy Drawing Workshop', 'desc' => 'Panduan menggambar proporsi tubuh manusia dan gestur dinamis secara akurat.'],
                ['title' => 'Lighting and Shading in Art', 'desc' => 'Memahami teori cahaya, bayangan, dan teknik mewarnai agar gambar terlihat bervolume.'],
                ['title' => 'Freelance Illustrator Career Guide', 'desc' => 'Tips mendapatkan klien internasional dan menentukan tarif karya seni Anda.'],
            ],
            'UI/UX Jakarta Collective' => [
                ['title' => 'Figma Design System Workshop', 'desc' => 'Belajar membuat library komponen, token warna, dan autolayout di Figma.'],
                ['title' => 'Wireframing Masterclass', 'desc' => 'Teknik membuat low-fidelity wireframe untuk memvalidasi alur aplikasi dengan cepat.'],
                ['title' => 'Usability Testing Workshop', 'desc' => 'Praktik melakukan tes kegunaan aplikasi ke user asli menggunakan metode kualitatif.'],
                ['title' => 'Mobile App UI Patterns Talk', 'desc' => 'Membedah pola navigasi, form input, dan komponen UI populer di iOS & Android.'],
                ['title' => 'Landing Page UX Checklist', 'desc' => 'Daftar aspek UX penting pada landing page untuk meningkatkan tingkat konversi.'],
                ['title' => 'UI/UX Portfolio Critique Session', 'desc' => 'Review portofolio studi kasus UX oleh desainer senior dari startup unicorn.'],
                // Ongoing & Upcoming
                ['title' => 'Figma Prototyping: Interactive Components', 'desc' => 'Mengeksplorasi pembuatan animasi mikro dan prototipe interaktif di Figma.'],
                ['title' => 'UX Research Methods Deep Dive', 'desc' => 'Belajar metode survei, card sorting, dan user interview untuk riset produk.'],
                ['title' => 'Designing for Accessibility (a11y)', 'desc' => 'Bagaimana membuat produk digital yang ramah bagi pengguna disabilitas.'],
                ['title' => 'UX Writing: Crafting Clear Copy', 'desc' => 'Memahami peran mikro-kop (microcopy) dalam memandu user menggunakan aplikasi.'],
            ],
            'Creative Sketchers Club' => [
                ['title' => 'Urban Sketching: Heritage Building', 'desc' => 'Menggambar arsitektur bangunan kolonial secara langsung dengan pen dan tinta.'],
                ['title' => 'Watercolor Wash Techniques', 'desc' => 'Belajar mencampur air dan cat air di atas kertas bertekstur kasar untuk latar belakang sketsa.'],
                ['title' => 'Perspective Drawing Basics', 'desc' => 'Memahami perspektif 1 titik dan 2 titik hilang untuk menggambar interior ruangan.'],
                ['title' => 'Still Life Sketching Class', 'desc' => 'Latihan menangkap proporsi dan arsir bayangan pada objek benda mati di sekitar kita.'],
                ['title' => 'Portrait Sketching in Graphite', 'desc' => 'Teknik menggambar wajah manusia menggunakan pensil grafit dengan proporsi tepat.'],
                ['title' => 'Travel Sketchbook Setup', 'desc' => 'Tips menyusun peralatan menggambar yang praktis dan ringkas saat bepergian.'],
                // Ongoing & Upcoming
                ['title' => 'Botanical Sketching Workshop', 'desc' => 'Menggambar bunga dan daun secara detail menggunakan drawing pen dan watercolor.'],
                ['title' => 'Ink and Wash Masterclass', 'desc' => 'Menggabungkan ketegasan garis tinta dengan kelembutan sapuan cat air.'],
                ['title' => 'Shading Techniques with Charcoal', 'desc' => 'Eksplorasi media arang (charcoal) untuk sketsa bernuansa dramatis dan kontras tinggi.'],
                ['title' => 'Sketching Outdoor Meetup', 'desc' => 'Berkumpul di taman kota untuk menggambar suasana taman secara langsung.'],
            ],

            // Music
            'Bandung Indie Music' => [
                ['title' => 'Home Studio Setup on Budget', 'desc' => 'Tips memilih audio interface, mikrofon, dan DAW murah untuk rekaman di rumah.'],
                ['title' => 'Songwriting Workshop', 'desc' => 'Menemukan progresi akord, menulis lirik puitis, dan menyusun struktur lagu yang enak.'],
                ['title' => 'Indie Band Jam Session', 'desc' => 'Sesi jamming antar grup musik band lokal di studio musik ternama Bandung.'],
                ['title' => 'Acoustic Open Mic Night', 'desc' => 'Wadah bagi musisi solo dan duo menampilkan lagu karya mereka secara akustik.'],
                ['title' => 'Mixing & Mastering Basics', 'desc' => 'Pengenalan EQ, kompresor, reverb, dan limiter dalam tahap pasca-produksi lagu.'],
                ['title' => 'Indie Stage Showcase Bandung', 'desc' => 'Konser mini menampilkan band-band baru berbakat dari komunitas Bandung.'],
                // Ongoing & Upcoming
                ['title' => 'Band Branding & Social Media', 'desc' => 'Strategi merilis lagu secara digital dan memasarkannya ke Spotify/TikTok.'],
                ['title' => 'Vocal Recording Techniques', 'desc' => 'Cara mengambil rekaman vokal yang bersih tanpa noise di kamar tidur.'],
                ['title' => 'Live Performance Stage Act', 'desc' => 'Melatih rasa percaya diri dan interaksi penonton saat tampil live di panggung.'],
                ['title' => 'Copyright & Royalty for Indie Musicians', 'desc' => 'Memahami hak cipta lagu dan cara mendaftarkannya ke lembaga kolektif royalti.'],
            ],
            'Jakarta Acoustic Jam' => [
                ['title' => 'Classic Rock Acoustic Jam', 'desc' => 'Menyanyikan lagu-lagu rock klasik tahun 80-an dengan aransemen gitar akustik.'],
                ['title' => 'Jazz Vocal Recital Session', 'desc' => 'Latihan bersama membawakan lagu standar jazz dengan iringan piano/gitar.'],
                ['title' => 'Guitar Fingerstyle Lesson', 'desc' => 'Belajar teknik memetik gitar fingerstyle untuk mengiringi lagu secara solo.'],
                ['title' => 'Cafe Jamming Session Senopati', 'desc' => 'Sesi kolaborasi akustik santai di kafe Senopati di malam akhir pekan.'],
                ['title' => 'Acoustic Night Cafe Senopati', 'desc' => 'Acara perform akustik intim yang menampilkan bakat terbaik anggota komunitas.'],
                ['title' => 'Song Covers Sharing & Feedback', 'desc' => 'Saling membagikan video cover lagu dan memberikan masukan aransemen.'],
                // Ongoing & Upcoming
                ['title' => 'Percussive Fingerstyle Clinic', 'desc' => 'Teknik memainkan gitar akustik sekaligus sebagai alat perkusi (ketukan).'],
                ['title' => 'Sing-Along Gathering: Pop Indo 90s', 'desc' => 'Bernostalgia menyanyikan lagu-lagu pop legendaris Indonesia era 90-an bersama.'],
                ['title' => 'Songwriting Acoustic Challenge', 'desc' => 'Tantangan menulis dan merekam lagu akustik pendek dalam waktu 24 jam.'],
                ['title' => 'Choosing the Right Acoustic Guitar', 'desc' => 'Panduan memilih kayu gitar, bentuk bodi (dreadnought vs concert), dan preamp.'],
            ],
            'Solo Classical Symphony' => [
                ['title' => 'Violin Basic Bowing Techniques', 'desc' => 'Pelatihan teknik menggesek biola agar menghasilkan nada yang bersih dan stabil.'],
                ['title' => 'Music Theory 101: Reading Notes', 'desc' => 'Belajar membaca not balok, memahami tanda kunci, tempo, dan ketukan.'],
                ['title' => 'Classical Piano Recital Masterclass', 'desc' => 'Review penampilan piano solo untuk karya Chopin dan Mozart oleh pianis profesional.'],
                ['title' => 'Cello Ensembles Session', 'desc' => 'Latihan bersama membawakan musik orkestra klasik khusus instrumen cello.'],
                ['title' => 'Chamber Music Gathering', 'desc' => 'Sesi kolaborasi instrumen gesek dan tiup membawakan lagu-lagu orkestra kamar.'],
                ['title' => 'Orchestra Conducting Introduction', 'desc' => 'Pengenalan peran dirigen (conductor) dan bahasa tubuh memimpin orkestra.'],
                // Ongoing & Upcoming
                ['title' => 'Baroque Period Music Study', 'desc' => 'Eksplorasi karya Johann Sebastian Bach dan karakteristik musik zaman Baroque.'],
                ['title' => 'Classical Guitar Ensemble Practice', 'desc' => 'Latihan membagi suara gitar klasik untuk membawakan aransemen simfoni.'],
                ['title' => 'String Quartet Live Session', 'desc' => 'Konser mini string quartet membawakan lagu klasik romantis.'],
                ['title' => 'Audition Prep for Solo Symphony', 'desc' => 'Tips dan trik mempersiapkan diri sebelum mengikuti audisi grup orkestra.'],
            ],

            // Education & Science
            'Indonesia Space Science Community' => [
                ['title' => 'Solar System Explorer Lecture', 'desc' => 'Kuliah umum virtual membahas formasi planet dan karakteristik bulan-bulan di tata surya.'],
                ['title' => 'Astrophotography Basics', 'desc' => 'Cara memotret galaksi Bima Sakti (Milky Way) menggunakan kamera DSLR sederhana.'],
                ['title' => 'Space Exploration Talk', 'desc' => 'Diskusi tentang misi Mars Rover terbaru dan rencana kolonisasi planet merah.'],
                ['title' => 'Telescope Setup Guide', 'desc' => 'Panduan merakit, kalibrasi finder-scope, dan merawat lensa teropong bintang.'],
                ['title' => 'Rocket Propulsion Seminar', 'desc' => 'Mempelajari prinsip kerja mesin roket kimiawi dan roket ion masa depan.'],
                ['title' => 'Bosscha Stargazing Night', 'desc' => 'Kunjungan langsung ke Observatorium Bosscha Lembang untuk meneropong bintang.'],
                // Ongoing & Upcoming
                ['title' => 'Supermoon Observation Meetup', 'desc' => 'Berkumpul di dataran tinggi untuk mengamati fenomena Supermoon terbesar tahun ini.'],
                ['title' => 'Introduction to Cosmology', 'desc' => 'Memahami asal-usul alam semesta, Big Bang, materi gelap (dark matter), dan energi gelap.'],
                ['title' => 'Life Beyond Earth: Astrobiology', 'desc' => 'Mencari tahu kemungkinan adanya mikroba di laut bawah es bulan Europa atau Enceladus.'],
                ['title' => 'Satellite Design & Cubesat', 'desc' => 'Bagaimana satelit mini (Cubesat) dirakit dan diluncurkan ke orbit rendah bumi.'],
            ],
            'Klub Debat Bahasa Inggris' => [
                ['title' => 'Public Speaking Bootcamp', 'desc' => 'Latihan mengatasi demam panggung, intonasi suara, dan bahasa tubuh saat berpidato.'],
                ['title' => 'Logical Fallacies Session', 'desc' => 'Mengidentifikasi argumen cacat logika (fallacy) agar bisa mematahkan poin lawan.'],
                ['title' => 'Asian Parliamentary Debate Practice', 'desc' => 'Latihan debat format parlemen Asia (3 vs 3) dengan mosi-mosi sosial-ekonomi.'],
                ['title' => 'Debate Exhibition Match', 'desc' => 'Menonton tanding ekshibisi dari debater nasional berpengalaman untuk belajar teknik.'],
                ['title' => 'Impromptu Speaking Tips', 'desc' => 'Tips menyusun struktur argumen kuat hanya dalam waktu persiapan 5 menit.'],
                ['title' => 'English Debate Practice Session', 'desc' => 'Latihan rutin debat mingguan dengan mosi bertema teknologi dan pendidikan.'],
                // Ongoing & Upcoming
                ['title' => 'British Parliamentary Format Class', 'desc' => 'Pengenalan format parlemen Inggris (BP) yang membagi tim menjadi 4 fraksi.'],
                ['title' => 'Motion Analysis & Case Building', 'desc' => 'Cara membedah mosi debat rumit dan merancang argumen utama yang solid.'],
                ['title' => 'Rebuttal Mastery Workshop', 'desc' => 'Teknik menyerang argumen lawan secara tajam, logis, dan persuasif.'],
                ['title' => 'Adjudication Core Principles', 'desc' => 'Belajar menjadi juri debat (adjudicator) yang adil dan objektif.'],
            ],
            'National Science Society' => [
                ['title' => 'Physics in Daily Life Seminar', 'desc' => 'Menjelaskan fenomena fisika sederhana seperti gravitasi, gaya gesek, dan aerodinamika.'],
                ['title' => 'Introduction to Genetics & DNA', 'desc' => 'Membahas prinsip dasar hereditas Mendel, mutasi gen, dan rekayasa genetika modern.'],
                ['title' => 'Chemical Reactions Demo', 'desc' => 'Demonstrasi eksperimen kimia aman seperti pembuatan sabun dan reaksi asam basa.'],
                ['title' => 'Climate Science Discussion', 'desc' => 'Memahami sains di balik pemanasan global, efek rumah kaca, dan perubahan iklim.'],
                ['title' => 'Mathematics & Fibonacci Code', 'desc' => 'Mengeksplorasi pola deret Fibonacci dan rasio emas (golden ratio) pada struktur alam.'],
                ['title' => 'Renewable Energy Solutions', 'desc' => 'Mempelajari cara kerja panel surya, turbin angin, dan energi geothermal.'],
                // Ongoing & Upcoming
                ['title' => 'Introduction to Quantum Physics', 'desc' => 'Penjelasan konsep dualitas gelombang-partikel dan prinsip ketidakpastian Heisenberg.'],
                ['title' => 'Neuroscience: How Brain Learns', 'desc' => 'Membahas neuroplastisitas otak dan mekanisme memori saat menyimpan informasi baru.'],
                ['title' => 'AI and Cognitive Science', 'desc' => 'Diskusi bagaimana AI meniru cara berpikir kognitif manusia.'],
                ['title' => 'Space Exploration Technology', 'desc' => 'Sains di balik perlindungan astronot dari radiasi kosmik saat perjalanan luar angkasa.'],
            ],

            // Business & Finance
            'Indonesian Startup Founders Hub' => [
                ['title' => 'Startup Valuation Talk', 'desc' => 'Memahami cara menghitung valuasi startup di tahap awal sebelum pendanaan.'],
                ['title' => 'Fundraising Strategies Panel', 'desc' => 'Diskusi panel dengan modal ventura lokal mengenai kriteria startup yang layak didanai.'],
                ['title' => 'Lean Canvas Workshop', 'desc' => 'Praktik merangkum model bisnis startup dalam satu lembar Lean Canvas.'],
                ['title' => 'Co-founder Dating Night', 'desc' => 'Wadah mempertemukan founder teknis (CTO) dengan founder bisnis (CEO).'],
                ['title' => 'Growth Hacking Seminar', 'desc' => 'Taktik pemasaran organik bernilai tinggi untuk menaikkan retensi pengguna secara eksponensial.'],
                ['title' => 'Startup Pitching Day Jakarta', 'desc' => 'Kesempatan bagi founder mempresentasikan ide startup di depan investor.'],
                // Ongoing & Upcoming
                ['title' => 'Product-Market Fit Workshop', 'desc' => 'Metode memvalidasi produk ke pasar agar tidak membuat fitur yang tidak dibutuhkan.'],
                ['title' => 'Cap Table & ESOP Management', 'desc' => 'Cara mengelola pembagian kepemilikan saham startup dan opsi saham karyawan.'],
                ['title' => 'Legal and Incorporation for Startups', 'desc' => 'Aspek hukum pendirian PT, pendaftaran HAKI, dan kontrak kerja sama.'],
                ['title' => 'Startup Exit Strategy: M&A vs IPO', 'desc' => 'Memahami proses merger, akuisisi, dan melantai di bursa saham.'],
            ],
            'Investor Saham Pemula' => [
                ['title' => 'Technical Analysis 101', 'desc' => 'Belajar membaca grafik lilin (candlestick), garis tren, dan indikator MACD/RSI.'],
                ['title' => 'Reading Financial Reports', 'desc' => 'Cara membedah laporan laba rugi, neraca keuangan, dan arus kas emiten saham.'],
                ['title' => 'IDX Stock Pick Discussion', 'desc' => 'Menganalisis emiten berkinerja baik di kuartal terakhir untuk prospek investasi.'],
                ['title' => 'Portfolio Diversification Seminar', 'desc' => 'Membagi aset investasi secara proporsional guna meminimalkan risiko kerugian.'],
                ['title' => 'Dividend Investing Guide', 'desc' => 'Strategi mengumpulkan saham blue-chip berdividen besar untuk pasif inkam jangka panjang.'],
                ['title' => 'Fundamental Analysis for Beginners', 'desc' => 'Panduan menghitung valuasi harga wajar saham menggunakan rasio PER dan PBV.'],
                // Ongoing & Upcoming
                ['title' => 'Macroeconomics & Stock Market', 'desc' => 'Pengaruh suku bunga BI, inflasi, dan nilai tukar rupiah terhadap indeks saham.'],
                ['title' => 'Value Investing Strategy ala Buffet', 'desc' => 'Mencari emiten salah harga yang memiliki moat bisnis yang kuat.'],
                ['title' => 'Avoiding Pump and Dump Stocks', 'desc' => 'Tips mengenali saham gorengan bernilai transaksi janggal agar modal tetap aman.'],
                ['title' => 'IDX Stock Screener Tutorial', 'desc' => 'Menggunakan fitur filter emiten untuk mempermudah proses seleksi saham harian.'],
            ],
            'Young Entrepreneurs Circle' => [
                ['title' => 'UMKM Digitalization Seminar', 'desc' => 'Langkah-langkah mendigitalisasi toko fisik tradisional ke e-commerce dan e-wallet.'],
                ['title' => 'Product Branding & Packaging', 'desc' => 'Meningkatkan daya tarik produk kuliner dan kriya lewat desain kemasan eksklusif.'],
                ['title' => 'Cash Flow Management Guide', 'desc' => 'Mengatur keuangan usaha mikro agar operasional tetap berjalan sehat.'],
                ['title' => 'Instagram Ads for Local Business', 'desc' => 'Trik beriklan di Instagram targeting wilayah terdekat untuk bisnis ritel/F&B.'],
                ['title' => 'Customer Retention Strategies', 'desc' => 'Cara merancang program loyalitas konsumen agar mereka terus membeli kembali.'],
                ['title' => 'Supply Chain and Sourcing Tips', 'desc' => 'Mencari pemasok bahan baku termurah dengan kualitas terjamin untuk skala produksi.'],
                // Ongoing & Upcoming
                ['title' => 'Franchising Business Model', 'desc' => 'Mempersiapkan SOP dan sistem kemitraan (franchise) untuk ekspansi bisnis.'],
                ['title' => 'Tax Compliance for Small Business', 'desc' => 'Penjelasan kewajiban pajak UMKM dan cara pelaporannya secara mudah.'],
                ['title' => 'Pitching to Angel Investors', 'desc' => 'Cara merancang proposal bisnis yang menarik minat pendanaan investor lokal.'],
                ['title' => 'E-Commerce Live Selling Workshop', 'desc' => 'Strategi melakukan live selling di TikTok dan Shopee untuk mendongkrak omzet.'],
            ],

            // Gaming
            'Esports Community' => [
                ['title' => 'Draft Pick Analysis', 'desc' => 'Bedah hero counter, meta ban-pick terbaru, dan strategi komposisi tim Esports.'],
                ['title' => 'Esports Career Seminar', 'desc' => 'Peluang karir di industri esports sebagai pro-player, pelatih, caster, atau manajer.'],
                ['title' => 'Team Coordination in Shooters', 'desc' => 'Latihan komunikasi antar-role dalam game tactical shooter.'],
                ['title' => 'Tournament Management Workshop', 'desc' => 'Cara menyelenggarakan turnamen esports skala komunitas yang profesional.'],
                ['title' => 'Pro Player Physical Training', 'desc' => 'Latihan refleks mata, tangan, dan kebugaran tubuh bagi pemain kompetitif.'],
                ['title' => 'Regular Fun Match Esports', 'desc' => 'Mabar kompetitif antar anggota komunitas dengan hadiah seru.'],
                // Ongoing & Upcoming
                ['title' => 'Esports Shoutcasting Clinic', 'desc' => 'Belajar menganalisis pertandingan live dan membawakannya dengan heboh sebagai caster.'],
                ['title' => 'Team Sponsorship Pitching', 'desc' => 'Cara merancang proposal sponsor untuk tim esports lokal.'],
                ['title' => 'Mental Toughness in Esports', 'desc' => 'Mengatasi stres turnamen dan menjaga motivasi saat menelan kekalahan beruntun.'],
                ['title' => 'Esports Cup Indonesia 2026', 'desc' => 'Turnamen tingkat nasional yang mempertemukan komunitas game terbesar.'],
            ],
            'Valorant Indonesia' => [
                ['title' => 'Aim Positioning & Crosshair Placement', 'desc' => 'Latihan dasar menempatkan crosshair setinggi kepala musuh saat mengintip (peeking).'],
                ['title' => 'Smoke Setup & Agent Execution', 'desc' => 'Tutorial menaruh smoke strategis menggunakan Omen dan Brimstone di map Bind.'],
                ['title' => 'Valorant Rank Push: Plat/Diamond', 'desc' => 'Mabar terkoordinasi untuk naik pangkat dari Platinum ke Diamond.'],
                ['title' => 'Valorant Echo and Buy Rounds Strategy', 'desc' => 'Mengelola ekonomi tim, kapan harus save kredit dan kapan harus force buy.'],
                ['title' => 'Agent Lineup Mastery: Sova/Viper', 'desc' => 'Mempelajari lineup panah Sova dan snake bite Viper untuk post-plant default.'],
                ['title' => 'Valorant 5v5 Friendly Tournament', 'desc' => 'Turnamen persahabatan 5v5 internal komunitas Valorant.'],
                // Ongoing & Upcoming
                ['title' => 'Map Control & Rotations in Lotus', 'desc' => 'Cara menjaga map 3 site (Lotus) dan melakukan rotasi cepat.'],
                ['title' => 'Duelist Aggressive Entry Tutorial', 'desc' => 'Bagaimana Jett/Raze melakukan entry kill dengan dukungan flash tim.'],
                ['title' => 'Valorant VCT Analysis Session', 'desc' => 'Menganalisis taktik tim profesional dunia dari rekaman turnamen terbaru.'],
                ['title' => 'Valorant Community Showcase BSD', 'desc' => 'Pertemuan offline komunitas Valorant Jabodetabek.'],
            ],
            'Mobile Legends Community' => [
                ['title' => 'Midlaner Rotation Class', 'desc' => 'Tips rotasi efektif bagi role midlaner untuk memenangkan lane lain.'],
                ['title' => 'Ranked Push Together Event', 'desc' => 'Berkumpul bersama untuk push rank tier Mythic secara berkelompok (party).'],
                ['title' => 'MLBB Fun Tournament Season 12', 'desc' => 'Turnamen 5v5 persahabatan komunitas berhadiah ribuan diamond.'],
                ['title' => 'Mobile Legends META Analysis', 'desc' => 'Diskusi komprehensif hero terkuat (META) hasil update patch terbaru.'],
                ['title' => 'Roamer and Shotcaller Mastery', 'desc' => 'Melatih kemampuan inisiator war dan memandu keputusan tim secara real-time.'],
                ['title' => 'Jungler Objective: Retri Battle', 'desc' => 'Latihan adu ketepatan spell Retribution saat perebutan Lord dan Turtle.'],
                // Ongoing & Upcoming
                ['title' => 'Goldlaner Positioning Guide', 'desc' => 'Bagaimana menjaga posisi saat war agar tidak gampang tereliminasi.'],
                ['title' => 'Exp Laner Cut Wave and Freeze Lane', 'desc' => 'Taktik menekan lane lawan di Exp lane agar musuh miskin gold.'],
                ['title' => 'MLBB Coach Analysis Session', 'desc' => 'Review gameplay rekaman pertandingan anggota oleh mantan analis tim pro.'],
                ['title' => 'Esports Mental Health Talk', 'desc' => 'Menjaga kesehatan mental dan fisik dari kelelahan akibat bermain game terlalu lama.'],
            ],

            // Photography
            'Street Photography Solo' => [
                ['title' => 'Night Street Photography', 'desc' => 'Tips menangkap ekspresi orang dan bayangan estetis di bawah penerangan lampu jalanan.'],
                ['title' => 'Street Photowalk Ngarsopuro', 'desc' => 'Kegiatan hunting foto bersama menyusuri koridor budaya Ngarsopuro Solo.'],
                ['title' => 'Black & White Editing Clinic', 'desc' => 'Mengolah foto street agar berkarakter kuat menggunakan kontras hitam putih.'],
                ['title' => 'Candid Street Photography Tips', 'desc' => 'Trik memotret momen candid secara natural tanpa membuat subjek terganggu.'],
                ['title' => 'Street Portraits Solo Pasar Gede', 'desc' => 'Hunting foto interaksi sosial pedagang dan pembeli di Pasar Gede Solo.'],
                ['title' => 'Decisive Moment Mastery', 'desc' => 'Melatih refleks mata dan jari untuk menekan tombol shutter di waktu paling tepat.'],
                // Ongoing & Upcoming
                ['title' => 'Solo MRT Station Photowalk', 'desc' => 'Mencari sudut estetik stasiun KRL Solo-Jogja.'],
                ['title' => 'Street Photography Book Project', 'desc' => 'Diskusi mengurasi foto terbaik anggota untuk diterbitkan menjadi buku kolektif.'],
                ['title' => 'Composition: Geometry & Shadow', 'desc' => 'Belajar memanfaatkan garis arsitektur dan bayangan matahari sebagai framing.'],
                ['title' => 'Street Photo Review & Critique', 'desc' => 'Bedah foto hasil hunting bersama fotografer senior.'],
            ],
            'Photography Club' => [
                ['title' => 'Architectural Hunt Meetup', 'desc' => 'Berburu foto geometri dan simetri pada gedung-gedung pencakar langit.'],
                ['title' => 'Visual Storytelling Talk', 'desc' => 'Bagaimana membuat sebuah foto tunggal menceritakan emosi dan cerita yang mendalam.'],
                ['title' => 'Street Portraits Workshop', 'desc' => 'Teknik mendekati orang asing di jalanan untuk meminta izin memotret potret mereka.'],
                ['title' => 'Lightroom Desktop Editing Clinic', 'desc' => 'Belajar color grading, curve adjustment, dan masking menggunakan Adobe Lightroom.'],
                ['title' => 'Landscape Photography Trip', 'desc' => 'Berburu pemandangan matahari terbit (sunrise) di pegunungan terdekat.'],
                ['title' => 'Studio Lighting Workshop', 'desc' => 'Mempelajari setup lighting studio (key light, fill light, hair light) untuk potret komersial.'],
                // Ongoing & Upcoming
                ['title' => 'Portrait Photography with Model', 'desc' => 'Praktik langsung memotret model dengan konsep fashion outdoor.'],
                ['title' => 'Astro-Photography Milky Way Hunting', 'desc' => 'Trip luar kota untuk memotret bintang dan galaksi di tempat minim polusi cahaya.'],
                ['title' => 'Product Photography for UMKM', 'desc' => 'Membantu pelaku UMKM membuat foto katalog produk komersial yang premium.'],
                ['title' => 'Macro Photography Exploration', 'desc' => 'Mengabadikan detail ekstrim serangga dan tetesan air embun menggunakan lensa makro.'],
            ],
            'Cinematography Indonesia' => [
                ['title' => 'Camera Movement Mastery', 'desc' => 'Latihan mengoperasikan stabilizer, gimbal, dan teknik panning/tilting manual.'],
                ['title' => 'Color Grading: LOG to Rec709', 'desc' => 'Memahami konversi profil warna LOG dari berbagai sensor kamera dan grading estetis.'],
                ['title' => 'Film Sound Design Introduction', 'desc' => 'Pentingnya foley, sound effect, dan mixing audio agar film terasa hidup.'],
                ['title' => 'Screenplay Writing Basics', 'desc' => 'Mempelajari format naskah film standar industri dan struktur cerita 3 babak.'],
                ['title' => 'Directing Actors Workshop', 'desc' => 'Bagaimana sutradara mengomunikasikan emosi karakter kepada aktor di set.'],
                ['title' => 'Short Film Showcase & Review', 'desc' => 'Pemutaran film pendek karya anggota komunitas dilanjutkan sesi diskusi.'],
                // Ongoing & Upcoming
                ['title' => 'Lighting for Narrative Film', 'desc' => 'Teknik menciptakan mood dramatis menggunakan konsep low-key dan high-key lighting.'],
                ['title' => 'Video Editing in DaVinci Resolve', 'desc' => 'Workflow editing, cutting, dan basic transition menggunakan DaVinci Resolve.'],
                ['title' => 'Music Video Production Clinic', 'desc' => 'Langkah-langkah memproduksi video klip musik dari konsep hingga delivery.'],
                ['title' => 'Short Film Project Pitching', 'desc' => 'Wadah presentasi proposal film pendek untuk mencari kru dan pendanaan.'],
            ],

            // Environment
            'Green Earth Indonesia' => [
                ['title' => 'Mangrove Clean Up Event', 'desc' => 'Aksi sukarela membersihkan sampah plastik di kawasan hutan mangrove Pantai Indah Kapuk.'],
                ['title' => 'Reforestation Seminar Indo', 'desc' => 'Edukasi tentang pentingnya penanaman kembali pohon endemik untuk mencegah tanah longsor.'],
                ['title' => 'Plastic Recycling Workshop', 'desc' => 'Mengolah sampah botol plastik bekas menjadi barang berguna seperti pot tanaman.'],
                ['title' => 'Eco Brick Crafting Session', 'desc' => 'Membuat ecobrick dari sampah plastik kemasan untuk digunakan sebagai bahan bangunan alternatif.'],
                ['title' => 'Volunteer Gathering Bogor', 'desc' => 'Temu akrab para relawan lingkungan guna mendiskusikan rencana kerja semester depan.'],
                ['title' => 'Tree Planting Day Bogor', 'desc' => 'Menanam ratusan bibit pohon mahoni di lahan kritis kaki gunung Salak Bogor.'],
                // Ongoing & Upcoming
                ['title' => 'River Cleaning Action Ciliwung', 'desc' => 'Aksi kolaboratif mengangkut sampah di bantaran sungai Ciliwung bersama warga lokal.'],
                ['title' => 'Introduction to Permaculture', 'desc' => 'Mempelajari prinsip bertani selaras dengan ekosistem alam di lahan perkotaan.'],
                ['title' => 'Urban Farming & Seed Swapping', 'desc' => 'Berbagi bibit tanaman dan belajar menanam sayuran organik di pekarangan rumah.'],
                ['title' => 'Climate Change Action Talk', 'desc' => 'Diskusi panel mengenai kontribusi individu dalam mengurangi emisi jejak karbon harian.'],
            ],
            'Zero Waste Jakarta' => [
                ['title' => 'Plastic-Free Kitchen Guide', 'desc' => 'Tips mengganti wadah plastik dapur dengan alternatif ramah lingkungan seperti kaca/bambu.'],
                ['title' => 'DIY Eco Soap Making', 'desc' => 'Membuat sabun mandi organik ramah lingkungan menggunakan minyak kelapa dan lidah buaya.'],
                ['title' => 'Zero Waste Picnic Gathering', 'desc' => 'Piknik bersama di taman kota dengan membawa bekal wadah sendiri tanpa menghasilkan sampah.'],
                ['title' => 'Eco-friendly Lifestyle Seminar', 'desc' => 'Pengenalan konsep 5R (Refuse, Reduce, Reuse, Recycle, Rot) dalam keseharian.'],
                ['title' => 'Bulk Store Shopping Walk', 'desc' => 'Kunjungan berkelompok belanja kebutuhan dapur di toko curah (bulk store) membawa wadah sendiri.'],
                ['title' => 'Household Composting Workshop', 'desc' => 'Belajar membuat komposter sederhana skala rumah tangga dari sisa makanan organik dapur.'],
                // Ongoing & Upcoming
                ['title' => 'Decluttering & Preloved Bazaar', 'desc' => 'Kegiatan membersihkan barang tak terpakai di rumah dan menjualnya sebagai barang preloved.'],
                ['title' => 'Making Eco-Enzyme at Home', 'desc' => 'Memanfaatkan kulit buah sisa menjadi cairan pembersih alami serbaguna.'],
                ['title' => 'Upcycling Old Clothes Workshop', 'desc' => 'Mengubah kaos atau pakaian bekas menjadi tas belanja (tote bag) modis.'],
                ['title' => 'Zero Waste Home audit guide', 'desc' => 'Langkah-langkah mengaudit jumlah produksi sampah di rumah masing-masing.'],
            ],
            'Nature Conservation Club' => [
                ['title' => 'Wildlife Protection Webinar', 'desc' => 'Sosialisasi undang-undang perlindungan satwa langka dan upaya melawan perburuan liar.'],
                ['title' => 'Hutan Hujan Tropis Study', 'desc' => 'Mempelajari keanekaragaman hayati flora dan fauna di hutan hujan tropis Indonesia.'],
                ['title' => 'Coral Reef Restoration Talk', 'desc' => 'Upaya transplantasi terumbu karang buatan guna menyelamatkan ekosistem laut.'],
                ['title' => 'Eco-Tourism Principles Seminar', 'desc' => 'Bagaimana melakukan perjalanan wisata alam tanpa merusak lingkungan setempat.'],
                ['title' => 'Birdwatching Meetup at National Park', 'desc' => 'Trip pengamatan burung liar di taman nasional terdekat dibimbing oleh ornitolog.'],
                ['title' => 'Forest Fire Prevention Discussion', 'desc' => 'Membahas sistem deteksi dini dan mitigasi kebakaran hutan saat musim kemarau.'],
                // Ongoing & Upcoming
                ['title' => 'National Park Conservation Volunteer', 'desc' => 'Program relawan akhir pekan membantu pemeliharaan jalur pendakian dan penanaman pohon.'],
                ['title' => 'Water Source Conservation Campaign', 'desc' => 'Upaya penanaman pohon di area mata air untuk menjaga debit air bawah tanah.'],
                ['title' => 'Exploring Biodiversity in Gunung Gede', 'desc' => 'Trip edukatif mendaki Gunung Gede untuk mencatat keanekaragaman lumut dan pakis.'],
                ['title' => 'Marine Ecology Seminar', 'desc' => 'Memahami rantai makanan laut dan dampak pencemaran mikroplastik pada biota laut.'],
            ],

            // Health & Wellness
            'Yogyakarta Yoga & Mindfulness' => [
                ['title' => 'Mindfulness Meditation Session', 'desc' => 'Sesi meditasi hening terpandu untuk menenangkan pikiran dan meredakan stres harian.'],
                ['title' => 'Yin Yoga for Relaxation', 'desc' => 'Latihan yoga gerakan perlahan dengan menahan pose lebih lama untuk fleksibilitas sendi.'],
                ['title' => 'Breathwork (Pranayama) Workshop', 'desc' => 'Teknik melatih pernapasan dalam guna meningkatkan pasokan oksigen dan ketenangan jiwa.'],
                ['title' => 'Sound Healing Session Yogyakarta', 'desc' => 'Relaksasi mendalam menggunakan getaran frekuensi suara dari singing bowl.'],
                ['title' => 'Restorative Yoga Meetup', 'desc' => 'Yoga menggunakan alat bantu bantal/balok untuk merilekskan otot yang tegang.'],
                ['title' => 'Gentle Flow Yoga Prambanan', 'desc' => 'Latihan yoga mengalir lembut dengan latar belakang pemandangan candi Prambanan.'],
                // Ongoing & Upcoming
                ['title' => 'Sunrise Yoga at Bukit Rhema', 'desc' => 'Yoga menyambut matahari terbit di atas bukit dengan udara pegunungan yang sejuk.'],
                ['title' => 'Mindful Eating and Gut Health', 'desc' => 'Edukasi cara makan dengan penuh kesadaran dan pengaruhnya bagi kesehatan pencernaan.'],
                ['title' => 'Vinyasa Flow for Strength', 'desc' => 'Yoga dinamis yang melatih kekuatan otot inti, keseimbangan, dan kardio.'],
                ['title' => 'Self-Compassion Meditation Retreat', 'desc' => 'Meditasi khusus menumbuhkan rasa penerimaan diri dan berdamai dengan masa lalu.'],
            ],
            'Klub Nutrisi & Hidup Sehat' => [
                ['title' => 'Nutrition Basics Seminar', 'desc' => 'Memahami fungsi makronutrisi (karbohidrat, protein, lemak) dan mikronutrisi bagi tubuh.'],
                ['title' => 'Home Workout Drill for Beginners', 'desc' => 'Rangkaian latihan beban tubuh (bodyweight) sederhana tanpa alat yang bisa dilakukan di rumah.'],
                ['title' => 'Calorie Deficit Guide Talk', 'desc' => 'Tips menurunkan berat badan secara sehat tanpa harus menahan lapar ekstrem.'],
                ['title' => 'Cooking Healthy Indomie Lesson', 'desc' => 'Kombinasi kreasi masak Indomie instan agar lebih bergizi dan rendah kalori.'],
                ['title' => 'Mindful Eating Talk & Practice', 'desc' => 'Mengenali rasa lapar fisik vs lapar emosional untuk mengendalikan nafsu makan.'],
                ['title' => 'Healthy Meal Prep Workshop', 'desc' => 'Tutorial memasak dan mengemas bekal sehat selama 3 hari ke depan untuk efisiensi.'],
                // Ongoing & Upcoming
                ['title' => 'Fat Loss vs Weight Loss Seminar', 'desc' => 'Memahami perbedaan susut lemak tubuh dan turunnya berat badan secara keseluruhan.'],
                ['title' => 'Intermittent Fasting Guide', 'desc' => 'Cara aman menerapkan metode puasa berkala bagi kesehatan metabolisme.'],
                ['title' => 'Smoothies and Healthy Juices Demo', 'desc' => 'Demonstrasi membuat jus sayur buah padat nutrisi untuk detoksifikasi alami.'],
                ['title' => 'HIIT Workout Challenge 15 Mins', 'desc' => 'Latihan interval intensitas tinggi selama 15 menit bersama pelatih kebugaran.'],
            ],
            'Mental Health Alliance' => [
                ['title' => 'Anxiety Management Workshop', 'desc' => 'Mengidentifikasi pemicu kecemasan dan latihan grounding technique 5-4-3-2-1.'],
                ['title' => 'Burnout Recovery Guide', 'desc' => 'Mengatasi stres kronis akibat pekerjaan dan merancang batasan profesional yang sehat.'],
                ['title' => 'Art Therapy Support Group', 'desc' => 'Mengekspresikan emosi terpendam melalui melukis bebas dalam sesi terapi kelompok.'],
                ['title' => 'Building Healthy Boundaries', 'desc' => 'Belajar berkata "tidak" tanpa rasa bersalah guna menjaga keseimbangan mental.'],
                ['title' => 'Peer Support Sharing Session', 'desc' => 'Wadah aman bertukar cerita dan saling mendengarkan keluh kesah anggota.'],
                ['title' => 'Introduction to Cognitive Therapy', 'desc' => 'Memahami hubungan antara pikiran otomatis, emosi, dan tindakan kita sehari-hari.'],
                // Ongoing & Upcoming
                ['title' => 'Sleep Hygiene & Sleep Quality Guide', 'desc' => 'Kebiasaan baik sebelum tidur untuk mengatasi insomnia kronis.'],
                ['title' => 'Emotional Regulation in Crisis', 'desc' => 'Tips mengendalikan amarah dan kesedihan mendalam agar tetap rasional.'],
                ['title' => 'Journaling for Self-Discovery', 'desc' => 'Latihan menulis jurnal harian dengan prompts psikologis untuk mengenal diri lebih baik.'],
                ['title' => 'Mental Health First Aid for Friends', 'desc' => 'Cara membantu teman terdekat yang sedang mengalami panic attack atau depresi.'],
            ]
        ];

        // Image map from Unsplash
        $categoryImageMap = [
            'Technology' => 'https://images.unsplash.com/photo-1518770660439-4636190af475',
            'Sports' => 'https://images.unsplash.com/photo-1461896836934-ffe607ba8211',
            'Art & Design' => 'https://images.unsplash.com/photo-1513364776144-60967b0f800f',
            'Music' => 'https://images.unsplash.com/photo-1511192336575-5a79af67a629',
            'Education & Science' => 'https://images.unsplash.com/photo-1507679799987-c73779587ccf',
            'Business & Finance' => 'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f',
            'Gaming' => 'https://images.unsplash.com/photo-1538481199705-c710c4e965fc',
            'Photography' => 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32',
            'Environment' => 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b',
            'Health & Wellness' => 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b',
        ];

        // Realistic review comments in Indonesian
        $ratingComments = [
            5 => [
                'Sangat seru dan bermanfaat! Ditunggu event selanjutnya.',
                'Materi yang disampaikan sangat jelas, speaker sangat kompeten.',
                'Terorganisir dengan sangat rapi, terima kasih panitia!',
                'Sangat menginspirasi! Penjelasannya sangat mendalam.',
                'Luar biasa! Banyak insight baru yang bisa langsung diterapkan.'
            ],
            4 => [
                'Acara berjalan lancar, materinya cukup menarik.',
                'Bagus sekali untuk pemula yang ingin belajar dasar-dasar topik ini.',
                'Sesi tanya jawabnya interaktif dan menambah wawasan.',
                'Tempatnya nyaman dan speaker menyampaikan materi dengan santai.',
                'Secara keseluruhan sangat mendidik dan bernilai tinggi.'
            ],
            3 => [
                'Materi bagus tapi durasi penyampaian terasa kurang lama.',
                'Cukup mendidik, meskipun beberapa poin agak terlalu teknis.',
                'Penyampaian pembicara sedikit monoton tapi isi slide-nya bagus.',
                'Acaranya lumayan oke, semoga di event berikutnya bisa lebih baik lagi.'
            ],
            2 => [
                'Waktu pelaksanaan sedikit molor dan suara pembicara kurang terdengar jelas.',
                'Topik menarik tapi panitia kurang siap secara teknis.'
            ],
            1 => [
                'Sangat tidak terorganisir dan melenceng dari deskripsi acara.',
                'Kendala teknis koneksi sangat mengganggu sepanjang jalannya sesi.'
            ]
        ];

        // Forum message patterns per category for realistic posts & replies
        $forumPatterns = [
            'Technology' => [
                'post' => [
                    'Ada rekomendasi laptop budget 10 jutaan untuk coding mobile dev?',
                    'Bagaimana cara meminimalkan bug memory leak di Jetpack Compose?',
                    'Lebih baik pakai API RESTful biasa atau ganti ke GraphQL ya untuk proyek besar?',
                    'Adakah yang sedang belajar Next.js App Router di sini?',
                    'Sharing dong framework CSS favorit kalian buat project cepat.',
                    'Belajar git merge vs rebase, enakan pake workflow yang mana ya?',
                    'Lagi nyoba setup Docker buat Laravel, ada yang punya boilerplate clean?',
                    'Bagaimana prospek Kotlin Multiplatform (KMP) di tahun ini?',
                ],
                'reply' => [
                    'Bisa coba lirik ThinkPad bekas seri L atau Asus Vivobook gan, ramah di dompet.',
                    'Coba cek state management Anda, biasanya karena lupa dispose listener.',
                    'Untuk proyek skala besar, RESTful yang terstruktur sudah cukup, GraphQL punya setup cost besar.',
                    'Saya sedang pelajari! Memang konsep Server Component-nya agak membingungkan di awal.',
                    'TailwindCSS masih jadi andalan saya karena utility-first nya cepat dipelajari.',
                    'Enakan rebase biar history-nya linear dan rapi di git log.',
                    'Coba cek repo GitHub laravel-docker, di sana lengkap templatenya.',
                    'KMP lagi naik daun banget, banyak company mulai migrasi dari Flutter.',
                ]
            ],
            'Sports' => [
                'post' => [
                    'Adakah tips mencegah cedera lutut saat latihan lari jarak jauh?',
                    'Rekomendasi raket bulutangkis untuk pemula tipe kontrol?',
                    'Berapa kali idealnya latihan kardio dalam seminggu bagi pemula?',
                    'Bagaimana cara konsisten bangun jam 5 pagi buat olahraga?',
                    'Sharing rute lari pagi yang teduh di sekitar Solo dong.',
                    'Tips nutrisi sebelum lari pagi agar terhindar dari kram perut.',
                    'Gimana cara melatih lompatan vertikal buat main basket?',
                    'Ada yang tahu tempat servis senar raket yang recommended di Solo?',
                ],
                'reply' => [
                    'Jangan lupa pemanasan dinamis dan penguatan otot paha (quadriceps).',
                    'Coba pakai Yonex Nanoray atau Li-Ning Windstorm, ringan dan cocok buat pemula.',
                    'Idealnya 2-3 kali seminggu dengan durasi 30 menit per sesi, jangan langsung diforsir.',
                    'Tidur lebih awal sebelum jam 10 malam dan taruh alarm jauh dari tempat tidur.',
                    'Kawasan Stadion Manahan Solo rutenya luas, teduh, dan udaranya segar.',
                    'Hindari makan berat minimal 1 jam sebelum lari, cukup minum air hangat saja.',
                    'Coba rutin latihan calf raises dan squat jumps secara teratur.',
                    'Coba mampir ke toko olahraga dekat pasar Gede, di sana pengerjaannya rapi.',
                ]
            ],
            'Art & Design' => [
                'post' => [
                    'Ada yang tahu tempat download brush gratis buat Photoshop yang bagus?',
                    'Bagaimana cara menentukan palet warna yang harmonis untuk UI aplikasi?',
                    'Butuh waktu berapa lama bagi kalian untuk menguasai anatomi dasar?',
                    'Software alternatif gratis pengganti Adobe Illustrator apa ya?',
                    'Tips mengatasi creative block saat kehabisan ide menggambar.',
                    'Media cat air merk apa yang ramah kantong buat pemula?',
                    'Ada tips bikin bayangan arsir pensil agar terlihat berdimensi?',
                    'Sharing dong portofolio desain karakter kalian untuk masukan.',
                ],
                'reply' => [
                    'Coba cari di BrushLovers atau langsung ke DeviantArt, banyak creator bagi gratis.',
                    'Gunakan tools seperti Coolors.co atau Adobe Color untuk generate palet berbasis harmoni.',
                    'Konsisten latihan sketsa 30 menit sehari, butuh waktu sekitar 3-6 bulan untuk mulai lancar.',
                    'Inkscape atau Figma sebenarnya sangat mumpuni untuk membuat grafis vektor gratis.',
                    'Coba istirahat sejenak, jalan-jalan keluar, atau cari inspirasi di Pinterest/Behance.',
                    'Merk Pentel atau Kuretake lumayan terjangkau dan pigmentasinya cukup bagus.',
                    'Perhatikan arah datang cahaya dan gunakan pensil berkarakter lunak seperti 4B atau 6B.',
                    'Kirim link artstation/behance kamu gan, nanti saya coba kasih feedback detail.',
                ]
            ],
            'Music' => [
                'post' => [
                    'Bagaimana cara melatih jangkauan vokal (vocal range) secara mandiri?',
                    'Rekomendasi audio interface terbaik di kisaran harga di bawah 1.5 juta?',
                    'Bagaimana menyusun struktur lagu pop agar tidak terdengar membosankan?',
                    'Ada yang tahu chord progresi yang paling sering dipakai lagu hits?',
                    'Tips meredam gema di kamar tidur untuk rekaman vokal rumahan.',
                    'Teknik bowing biola bagi pemula biar gesekannya tidak mendecit.',
                    'Buku teori musik klasik apa yang paling gampang dipelajari?',
                    'Butuh berapa lama untuk lancar membaca not balok bagi pemula?',
                ],
                'reply' => [
                    'Lakukan pemanasan vokal lip trill dan humming setiap pagi secara teratur.',
                    'Focusrite Scarlett Solo Gen 3 atau Behringer UMC202HD sangat bagus di kelas harga itu.',
                    'Coba tambahkan variasi dinamika di bagian bridge atau ganti instrumen pengiring.',
                    'Progresi chord I - V - vi - IV paling sering dipakai, dijamin terdengar familiar.',
                    'Gunakan karpet tebal, pasang tirai jendela, dan taruh gantungan baju di sudut kamar.',
                    'Jaga posisi bow tetap tegak lurus dengan senar dan kurangi tekanan berlebih dari bahu.',
                    'Coba baca "Music Theory for Dummies", bahasanya santai dan penjelasannya runtut.',
                    'Tergantung konsistensi latihan membaca partitur, biasanya 1-2 bulan sudah mulai lancar.',
                ]
            ],
            'Education & Science' => [
                'post' => [
                    'Ada rekomendasi buku astronomi populer untuk pemula?',
                    'Bagaimana format menyusun argumen debat bahasa Inggris yang kuat?',
                    'Apakah teori Big Bang adalah satu-satunya penjelasan asal mula alam semesta?',
                    'Tips belajar bahasa Inggris cepat lewat film atau podcast.',
                    'Bagaimana cara kerja teleskop pantul (reflektor) dibanding teropong biasa?',
                    'Ada rekomendasi eksperimen sains sederhana yang aman buat anak-anak?',
                    'Bagaimana cara memahami konsep fisika kuantum dengan mudah?',
                    'Tips konsisten membaca buku jurnal ilmiah tanpa cepat bosan.',
                ],
                'reply' => [
                    'Buku "Cosmos" karya Carl Sagan wajib dibaca, bahasanya sangat indah dan puitis.',
                    'Gunakan struktur A-R-E-L: Assertion, Reasoning, Evidence, Link-back.',
                    'Saat ini Big Bang adalah model paling didukung bukti ilmiah, seperti radiasi latar kosmik.',
                    'Tonton tanpa subtitle bahasa Indonesia, gunakan subtitle bahasa Inggris untuk melatih listening.',
                    'Teleskop reflektor menggunakan cermin cekung untuk mengumpulkan cahaya, bebas dari cacat warna.',
                    'Bisa coba membuat simulasi gunung meletus pake baking soda dan cuka dapur.',
                    'Tonton video animasi dari channel YouTube Kurzgesagt atau Veritasium, sangat membantu visualisasi.',
                    'Mulailah membaca abstrak dan kesimpulan terlebih dahulu untuk memahami inti penelitian.',
                ]
            ],
            'Business & Finance' => [
                'post' => [
                    'Bagaimana cara membedakan saham yang under-valued dengan yang jebakan (value trap)?',
                    'Lebih baik fokus bangun produk dulu atau cari pendanaan di tahap startup awal?',
                    'Tips mengelola dana darurat bagi pekerja lepas (freelancer).',
                    'Berapa persen alokasi investasi saham yang ideal untuk anak muda?',
                    'Bagaimana cara mendaftarkan merek dagang secara legal di Indonesia?',
                    'Gimana cara bikin laporan arus kas sederhana buat warung makan?',
                    'Tips memilih model bisnis franchise yang aman dan legal.',
                    'Ada yang tahu cara efektif dapetin pembeli pertama lewat IG Ads?',
                ],
                'reply' => [
                    'Periksa apakah labanya konsisten bertumbuh atau utangnya terlalu menumpuk.',
                    'Fokus ke Product-Market Fit dulu. Investor tidak akan mendanai ide tanpa bukti traksi.',
                    'Freelancer sebaiknya menyiapkan dana darurat minimal 6-9 bulan rata-rata pengeluaran bulanan.',
                    'Bisa gunakan rumus 100 dikurangi umur Anda untuk porsi aset berisiko seperti saham.',
                    'Bisa diajukan online lewat portal resmi DJKI Kemenkumham, biayanya relatif terjangkau.',
                    'Catat setiap uang masuk dan keluar secara harian menggunakan aplikasi kasir gratis.',
                    'Pilihlah franchise yang sudah memiliki izin STPW dan memiliki histori keuangan yang transparan.',
                    'Gunakan objektif target wilayah lokal radius 5km dan gunakan video ulasan yang menarik.',
                ]
            ],
            'Gaming' => [
                'post' => [
                    'Adakah tips rotasi lane yang baik buat roamer di META Mobile Legends sekarang?',
                    'PC gaming budget 8 juta sekarang sudah bisa dapet spek apa aja ya?',
                    'Siapa hero counter terbaik untuk melawan Harith di gold lane?',
                    'Apakah upgrade RAM dari 8GB ke 16GB sangat terasa untuk gaming?',
                    'Turnamen MLBB komunitas berikutnya kapan diadakan lagi?',
                    'Rekomendasi agent Valorant yang gampang dimainkan buat pemula.',
                    'Ada tips buat ningkatin aim dan recoil control di Valorant?',
                    'Setup keyboard mechanical custom apa yang paling cocok buat gaming?',
                ],
                'reply' => [
                    'Selalu berikan vision di area turtle/lord dan bantu midlaner clear wave lebih dulu.',
                    'Bisa dapet Intel Core i3 gen 12/Ryzen 5 dengan VGA GTX 1650 atau RX 6600 bekas.',
                    'Gunakan Minsitthar untuk membatasi dash-nya atau pakai hero burst seperti Brody/Popol.',
                    'Sangat terasa! Stuttering di game berat open world akan berkurang jauh.',
                    'Turnamen cup komunitas akan segera diselenggarakan di akhir bulan ini, pantau terus info event!',
                    'Sage atau Brimstone sangat direkomendasikan karena utility-nya mudah dipahami.',
                    'Latihan rutin di Range selama 15 menit sebelum match dan jaga crosshair placement setinggi kepala.',
                    'Pilih switch berkarakter linear seperti Red Switch agar respon tombol lebih cepat.',
                ]
            ],
            'Photography' => [
                'post' => [
                    'Adakah yang punya tips memotret street photo candid tanpa terlihat mencurigakan?',
                    'Lebih baik beli lensa prime 50mm f/1.8 atau lensa zoom bawaan kit dulu?',
                    'Bagaimana cara menjaga kestabilan tangan saat memotret dengan HP tanpa tripod?',
                    'Aplikasi edit foto HP apa yang paling komplit fiturnya selain Lightroom?',
                    'Sharing dong spot foto street paling estetik di daerah Solo.',
                    'Gimana cara bikin video cinematic pake kamera mirrorless entry-level?',
                    'Rekomendasi stabilizer gimbal budget 1 jutaan buat videografi.',
                    'Tips color grading video LOG agar terlihat natural.',
                ],
                'reply' => [
                    'Gunakan lensa yang tidak terlalu besar, bertingkahlah seperti turis biasa dan tersenyumlah.',
                    'Lensa 50mm f/1.8 sangat bagus untuk melatih komposisi dan menghasilkan efek bokeh indah.',
                    'Posisikan siku menempel di dada untuk menopang HP dan tahan napas sesaat saat menekan shutter.',
                    'Snapseed sangat bagus untuk editing lokal, atau VSCO jika menyukai filter warna film.',
                    'Sepanjang koridor jalan Slamet Riyadi dan bangunan pasar Gede sangat estetik.',
                    'Rekam di frame rate 24fps dengan shutter speed 1/50 dan gunakan filter ND untuk motion blur.',
                    'Bisa lirik Brica B-Steady PRO atau Moza Mini-P bekas, keduanya lumayan stabil.',
                    'Gunakan conversion LUT resmi ke Rec709 terlebih dahulu baru atur kontras dan saturasi.',
                ]
            ],
            'Environment' => [
                'post' => [
                    'Di mana kita bisa menyalurkan sampah plastik yang sudah terpilah di Solo?',
                    'Bagaimana cara memulai membuat ecobrick di rumah dari sampah kemasan?',
                    'Ada tips membuat pupuk kompos dari sampah dapur agar tidak bau menyengat?',
                    'Apakah penggunaan tas belanja spunbond benar-benar ramah lingkungan?',
                    'Bagaimana cara bergabung menjadi relawan penanaman pohon minggu depan?',
                    'Ada tips memulai gaya hidup zero-waste bagi mahasiswa kos?',
                    'Cara menyalurkan minyak jelantah bekas dapur agar tidak mencemari lingkungan.',
                    'Bagaimana dampak limbah deterjen terhadap kelestarian sungai?',
                ],
                'reply' => [
                    'Bisa disalurkan ke Bank Sampah terdekat atau lewat layanan dropbox Waste4Change.',
                    'Bersihkan dan keringkan sampah plastik, gunting kecil-kecil, lalu padatkan ke dalam botol bekas.',
                    'Pastikan kelembapan terjaga dan imbangi sampah basah (hijau) dengan sampah kering (cokelat/daun).',
                    'Spunbond baru ramah lingkungan jika dipakai berulang-ulang ratusan kali, bukan sekali pakai.',
                    'Cukup klik daftar di halaman detail event Penanaman Pohon di aplikasi ini!',
                    'Mulai bawa botol minum sendiri dan kurangi memesan makanan dengan styrofoam.',
                    'Kumpulkan di wadah tertutup lalu donasikan ke komunitas pengolah biodiesel setempat.',
                    'Dapat menyebabkan eutrofikasi dan meracuni biota air, gunakan deterjen ramah lingkungan.',
                ]
            ],
            'Health & Wellness' => [
                'post' => [
                    'Bagaimana mengatasi insomnia dan pikiran cemas sebelum tidur?',
                    'Ada rekomendasi gerakan yoga sederhana setelah seharian duduk bekerja?',
                    'Tips merancang menu makanan diet sehat yang kenyang tahan lama.',
                    'Apakah meditasi 10 menit sehari sudah cukup untuk melatih fokus?',
                    'Olahraga apa yang paling cocok untuk membakar lemak perut dengan cepat?',
                    'Bagaimana cara mengatasi burnout kerja yang sudah parah?',
                    'Ada tips journaling buat mengurangi tingkat kecemasan harian?',
                    'Tanda-tanda tubuh dehidrasi yang sering diabaikan sehari-hari.',
                ],
                'reply' => [
                    'Matikan layar HP minimal 1 jam sebelum tidur dan lakukan latihan pernapasan 4-7-8.',
                    'Gerakan Child’s Pose, Cat-Cow stretch, dan Cobra Pose sangat baik meredakan kaku punggung.',
                    'Perbanyak porsi serat dari sayuran dan protein seperti dada ayam atau tahu tempe.',
                    'Sangat cukup! Konsistensi jauh lebih penting daripada durasi yang lama tapi jarang.',
                    'Kombinasi latihan kekuatan (strength training) dan kardio HIIT, diiringi defisit kalori.',
                    'Ambil cuti sejenak untuk detoks digital dan bicarakan pembagian beban kerja dengan tim.',
                    'Tuliskan 3 hal yang disyukuri setiap pagi dan tumpahkan emosi negatif di kertas tanpa filter.',
                    'Mudah lelah, sakit kepala ringan, dan warna urine yang pekat.',
                ]
            ]
        ];

        // Generic reply fillers for Indonesian conversational touch
        $conversationalFillers = [
            'Sangat setuju gan, info ini sangat bermanfaat.',
            'Mantap, makasih banyak sharing wawasannya!',
            'Izin menyimak kelanjutan thread ini ya.',
            'Up! Semoga makin banyak yang merespon.',
            'Wah baru tahu saya tentang aspek ini, makasih!',
            'Menarik sekali diskusinya, ditunggu kelanjutannya.',
            'Keren nih komunitasnya makin aktif dan seru.',
            'Setuju, semoga event selanjutnya membahas hal ini secara offline.',
        ];

        // Process each community to generate its 10 events
        foreach ($communities as $index => $community) {
            $category = $community->category;
            $categoryName = $category->name;
            $commName = $community->name;

            // Get events data for this community
            $eventsPool = $eventsDataMap[$commName] ?? [
                ['title' => 'Event Keren ' . $commName, 'desc' => 'Deskripsi event komunitas.'],
            ];

            // Get community members
            $memberUserIds = DB::table('community_members')
                ->where('community_id', $community->id)
                ->pluck('user_id')
                ->toArray();

            if (empty($memberUserIds)) {
                $memberUserIds = $allUsers->pluck('id')->toArray();
            }

            $membersCount = count($memberUserIds);

            // Determine community type based on member count
            // Large: 80-120. Medium: 40-80. Small: 15-40.
            if ($membersCount >= 80) {
                // Large community capacities
                $capacities = [150, 200, 100, 75, 100, 150, 50, 75, 50, 75];
            } elseif ($membersCount >= 40) {
                // Medium community capacities
                $capacities = [75, 100, 50, 50, 75, 30, 50, 30, 30, 30];
            } else {
                // Small community capacities
                $capacities = [50, 30, 20, 20, 30, 20, 20, 20, 20, 20];
            }

            // Assign registration brackets for the 10 events
            // Exactly:
            // 0: Empty (0% filled) - Event index 0
            // 1: 25%-50% filled - Event index 1, 2
            // 2: 50%-80% filled - Event index 3, 4, 5, 6
            // 3: 80%-100% filled - Event index 7, 8
            // 4: Full (100% filled) - Event index 9
            $brackets = [0, 1, 1, 2, 2, 2, 2, 3, 3, 4];

            // Generate 10 events per community (6 COMPLETED, 3 UPCOMING, 1 ONGOING)
            for ($i = 0; $i < 10; $i++) {
                // Status mapping:
                // 0 to 5: COMPLETED (6 events)
                // 6 to 8: UPCOMING (3 events)
                // 9: ONGOING (1 event)
                
                $bracket = $brackets[$i];
                $chosenCapacity = $capacities[$i];

                // Cap the capacity if it exceeds the community member count to ensure registrations can be fulfilled
                $capacity = min($chosenCapacity, $membersCount);
                if ($capacity <= 0) {
                    $capacity = 20; // safe fallback
                }

                if ($i < 6) {
                    $status = 'COMPLETED';
                    $eventDate = Carbon::now()->subDays(rand(7, 90))->format('Y-m-d');
                    $eventTime = sprintf('%02d:00', rand(8, 20));
                    $endTime = sprintf('%02d:00', rand(21, 23));
                } elseif ($i < 9) {
                    $status = 'UPCOMING';
                    $eventDate = Carbon::now()->addDays(rand(2, 60))->format('Y-m-d');
                    $eventTime = sprintf('%02d:00', rand(8, 20));
                    $endTime = sprintf('%02d:00', rand(21, 23));
                } else {
                    $status = 'ONGOING';
                    $eventDate = Carbon::now()->toDateString();
                    // Ongoing is set covering current time
                    $eventTime = Carbon::now()->subHours(1)->format('H:i');
                    $endTime = Carbon::now()->addHours(2)->format('H:i');
                }

                $eventInfo = $eventsPool[$i] ?? ['title' => "Event {$i} - {$commName}", 'desc' => "Deskripsi untuk event {$i}"];

                // Determine registration count (R) based on bracket
                if ($bracket === 0) {
                    $regCount = 0;
                } elseif ($bracket === 1) {
                    $regCount = (int)round($capacity * rand(25, 50) / 100);
                    $regCount = max(1, min($regCount, $membersCount));
                } elseif ($bracket === 2) {
                    $regCount = (int)round($capacity * rand(50, 80) / 100);
                    $regCount = max(1, min($regCount, $membersCount));
                } elseif ($bracket === 3) {
                    // Capped below 100% capacity
                    $regCount = (int)round($capacity * rand(80, 95) / 100);
                    $regCount = max(1, min($regCount, $capacity - 1, $membersCount));
                } else {
                    // Full (100% capacity)
                    $regCount = $capacity;
                }

                // Pick attendees from community members
                $attendeeIds = [];
                if ($regCount > 0) {
                    $shuffledMembers = $memberUserIds;
                    shuffle($shuffledMembers);
                    $attendeeIds = array_slice($shuffledMembers, 0, $regCount);
                }

                // Choose a cover image matching the category
                $coverImage = $categoryImageMap[$categoryName] ?? 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4';

                // Create the Event record
                $event = Event::create([
                    'community_id' => $community->id,
                    'category_id' => $community->category_id,
                    'title' => $eventInfo['title'],
                    'description' => $eventInfo['desc'],
                    'event_date' => $eventDate,
                    'event_time' => $eventTime,
                    'end_time' => $endTime,
                    'location' => rand(0, 1) ? 'Zoom Meeting' : 'Gedung Serbaguna ' . $categoryName . ' Hall ' . rand(1, 3),
                    'is_online' => rand(0, 1) ? true : false,
                    'max_attendees' => $chosenCapacity,
                    'attendee_count' => count($attendeeIds), // registrations with status REGISTERED or ATTENDED
                    'cover_image_url' => $coverImage,
                    'status' => $status,
                ]);

                // Create registrations for selected attendees
                // For finished events (COMPLETED), 70% to 90% actually attend.
                $attendedUserIds = [];
                if (!empty($attendeeIds)) {
                    if ($status === 'COMPLETED') {
                        $attendedCount = (int)round(count($attendeeIds) * rand(70, 90) / 100);
                        $attendedCount = max(1, min($attendedCount, count($attendeeIds)));
                        $attendedUserIds = array_slice($attendeeIds, 0, $attendedCount);
                    }

                    foreach ($attendeeIds as $userId) {
                        $isAttended = in_array($userId, $attendedUserIds);
                        $regStatus = $status === 'COMPLETED' ? ($isAttended ? 'ATTENDED' : 'REGISTERED') : 'REGISTERED';
                        
                        $allRegistrations[] = [
                            'user_id' => $userId,
                            'event_id' => $event->id,
                            'status' => $regStatus,
                            'registered_at' => Carbon::parse($eventDate)->subDays(rand(1, 5))->toDateTimeString(),
                            'attended_at' => ($status === 'COMPLETED' && $isAttended) ? Carbon::parse($eventDate)->toDateTimeString() : null,
                            'created_at' => now(),
                            'updated_at' => now(),
                        ];
                    }
                }

                // Create ratings for COMPLETED events (Only by ATTENDED users)
                if ($status === 'COMPLETED' && !empty($attendedUserIds)) {
                    // Let 50% to 100% of attended users leave ratings
                    $ratersCount = (int)round(count($attendedUserIds) * rand(50, 100) / 100);
                    $ratersCount = max(1, $ratersCount);
                    
                    $raters = array_slice($attendedUserIds, 0, $ratersCount);

                    $eventRatings = [];
                    foreach ($raters as $userId) {
                        // Weighted probability matching:
                        // 5 Stars: 45%, 4 Stars: 35%, 3 Stars: 15%, 2 Stars: 4%, 1 Star: 1%
                        $rand = rand(1, 100);
                        if ($rand <= 45) {
                            $ratingValue = 5;
                        } elseif ($rand <= 80) {
                            $ratingValue = 4;
                        } elseif ($rand <= 95) {
                            $ratingValue = 3;
                        } elseif ($rand <= 99) {
                            $ratingValue = 2;
                        } else {
                            $ratingValue = 1;
                        }
                        $eventRatings[$userId] = $ratingValue;
                    }

                    // Enforce average between 4.1 and 4.6
                    if (count($eventRatings) > 0) {
                        $maxAttempts = 100;
                        $attempt = 0;
                        while ($attempt < $maxAttempts) {
                            $sum = array_sum($eventRatings);
                            $avg = $sum / count($eventRatings);
                            if ($avg >= 4.1 && $avg <= 4.6) {
                                break;
                            }
                            if ($avg < 4.1) {
                                // Too low, increase someone's rating
                                $increased = false;
                                foreach ($eventRatings as $userId => $val) {
                                    if ($val < 5) {
                                        $eventRatings[$userId] = $val + 1;
                                        $increased = true;
                                        break;
                                    }
                                }
                                if (!$increased) break;
                            } else {
                                // Too high, decrease someone's rating
                                $decreased = false;
                                foreach ($eventRatings as $userId => $val) {
                                    if ($val > 1) {
                                        $eventRatings[$userId] = $val - 1;
                                        $decreased = true;
                                        break;
                                    }
                                }
                                if (!$decreased) break;
                            }
                            $attempt++;
                        }
                    }

                    foreach ($eventRatings as $userId => $ratingValue) {
                        $commentsPool = $ratingComments[$ratingValue] ?? ['Event yang bagus.'];
                        $comment = $commentsPool[array_rand($commentsPool)];

                        $allRatings[] = [
                            'user_id' => $userId,
                            'event_id' => $event->id,
                            'rating' => $ratingValue,
                            'comment' => $comment,
                            'created_at' => Carbon::parse($eventDate)->addHours(rand(2, 6))->toDateTimeString(),
                            'updated_at' => Carbon::parse($eventDate)->addHours(rand(2, 6))->toDateTimeString(),
                        ];
                    }
                }
            }

            // 4. Seed Forum Messages (Posts and Replies)
            // Highly Active communities: 5 communities (indices 0 to 4) -> 50 to 100 messages
            // Active communities: 10 communities (indices 5 to 14) -> 20 to 50 messages
            // Regular communities: 15 communities (indices 15 to 29) -> 5 to 20 messages
            if ($index <= 4) {
                $targetMessages = rand(55, 95);
            } elseif ($index <= 14) {
                $targetMessages = rand(25, 45);
            } else {
                $targetMessages = rand(7, 18);
            }

            $patterns = $forumPatterns[$categoryName] ?? [
                'post' => ['Halo semuanya!', 'Ada wacana kumpul?'],
                'reply' => ['Halo juga!', 'Boleh diatur om.']
            ];

            $messagesCreated = 0;
            $lastMessageTime = Carbon::now()->subDays(rand(10, 45));

            while ($messagesCreated < $targetMessages) {
                // Determine if this is a thread post or a reply
                // Let's create threads where each thread has 2-5 comments
                $postSenderId = $memberUserIds[array_rand($memberUserIds)];
                $postMessage = $patterns['post'][rand(0, count($patterns['post']) - 1)];
                
                // Add slight unique variations so that ripgrep or validations don't see exact duplicate texts
                $variant = rand(1, 100);
                if ($variant > 70) {
                    $postMessage .= " " . $conversationalFillers[array_rand($conversationalFillers)];
                }

                $lastMessageTime = Carbon::parse($lastMessageTime)->addHours(rand(2, 24));
                if ($lastMessageTime->gt(Carbon::now())) {
                    $lastMessageTime = Carbon::now()->subMinutes(rand(1, 60));
                }

                ForumMessage::create([
                    'community_id' => $community->id,
                    'sender_id' => $postSenderId,
                    'message' => $postMessage,
                    'created_at' => $lastMessageTime,
                    'updated_at' => $lastMessageTime,
                ]);

                $messagesCreated++;

                if ($messagesCreated >= $targetMessages) {
                    break;
                }

                // Create replies
                $replies = rand(2, 5);
                for ($r = 0; $r < $replies; $r++) {
                    if ($messagesCreated >= $targetMessages) {
                        break;
                    }

                    $replySenderId = $memberUserIds[array_rand($memberUserIds)];
                    $replyMessage = $patterns['reply'][rand(0, count($patterns['reply']) - 1)];
                    
                    if (rand(0, 1)) {
                        $replyMessage = $conversationalFillers[array_rand($conversationalFillers)];
                    }

                    $lastMessageTime = Carbon::parse($lastMessageTime)->addMinutes(rand(5, 120));
                    if ($lastMessageTime->gt(Carbon::now())) {
                        $lastMessageTime = Carbon::now()->subMinutes(rand(1, 10));
                    }

                    ForumMessage::create([
                        'community_id' => $community->id,
                        'sender_id' => $replySenderId,
                        'message' => $replyMessage,
                        'created_at' => $lastMessageTime,
                        'updated_at' => $lastMessageTime,
                    ]);

                    $messagesCreated++;
                }
            }
        }

        // Bulk insert all registrations in chunks of 500
        if (!empty($allRegistrations)) {
            foreach (array_chunk($allRegistrations, 500) as $chunk) {
                DB::table('event_registrations')->insert($chunk);
            }
        }

        // Bulk insert all ratings in chunks of 500
        if (!empty($allRatings)) {
            foreach (array_chunk($allRatings, 500) as $chunk) {
                DB::table('event_ratings')->insert($chunk);
            }
        }

        // 5. Seed Notifications
        // Seed 150 notifications spread across random users
        $types = ['EVENT', 'COMMUNITY', 'TRUSTED_APPLICATION', 'SYSTEM'];
        for ($n = 0; $n < 150; $n++) {
            $user = $allUsers->random();
            $type = $types[array_rand($types)];
            
            $title = '';
            $message = '';
            $refId = null;
            $refType = null;

            switch ($type) {
                case 'EVENT':
                    $event = Event::all()->random();
                    $refId = $event->id;
                    $refType = 'Event';
                    $eventTitles = [
                        'Pengingat Event Terdekat',
                        'Pendaftaran Event Berhasil',
                        'Perubahan Jadwal Event',
                        'Event Baru Dirilis'
                    ];
                    $title = $eventTitles[array_rand($eventTitles)];
                    $message = "Halo {$user->name}, jangan lewatkan event '{$event->title}' yang diselenggarakan pada {$event->event_date} pukul {$event->event_time}.";
                    break;

                case 'COMMUNITY':
                    $comm = $communities->random();
                    $refId = $comm->id;
                    $refType = 'Community';
                    $title = 'Pengumuman Komunitas';
                    $message = "Komunitas '{$comm->name}' baru saja mempublikasikan info terupdate di forum. Silakan cek detailnya.";
                    break;

                case 'TRUSTED_APPLICATION':
                    $app = TrustedApplication::all()->random();
                    $refId = $app->id;
                    $refType = 'TrustedApplication';
                    
                    if ($app->status === 'APPROVED') {
                        $title = 'Trusted Organizer Disetujui';
                        $message = "Selamat! Pengajuan Anda untuk menjadi Trusted Organizer komunitas '{$app->community_name}' telah disetujui oleh Admin.";
                    } else {
                        $title = 'Pengajuan Trusted Organizer Terkirim';
                        $message = "Pengajuan Anda untuk komunitas '{$app->community_name}' sedang ditinjau oleh tim administrator kami.";
                    }
                    break;

                case 'SYSTEM':
                    $title = 'Selamat Datang!';
                    $message = "Halo {$user->name}, selamat bergabung di platform Community Event Management System. Lengkapi profil Anda untuk pengalaman terbaik.";
                    break;
            }

            $allNotifications[] = [
                'user_id' => $user->id,
                'title' => $title,
                'message' => $message,
                'type' => $type,
                'is_read' => rand(0, 1) ? true : false,
                'reference_id' => $refId,
                'reference_type' => $refType,
                'created_at' => Carbon::now()->subDays(rand(1, 15))->toDateTimeString(),
                'updated_at' => Carbon::now()->subDays(rand(1, 15))->toDateTimeString(),
            ];
        }

        // Bulk insert notifications
        if (!empty($allNotifications)) {
            foreach (array_chunk($allNotifications, 500) as $chunk) {
                DB::table('notifications')->insert($chunk);
            }
        }
    }
}
