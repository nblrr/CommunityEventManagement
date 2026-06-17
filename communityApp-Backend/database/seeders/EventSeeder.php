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
        $allForumMessages = [];
        $allNotifications = [];

        // Predefined realistic event titles and descriptions per community
        $eventsDataMap = [
            'Surabaya Developer Community' => [
                ['title' => 'Vite + React Best Practices', 'desc' => 'Sesi berbagi mengenai optimasi build react menggunakan Vite dan struktur project berskala besar.'],
                ['title' => 'Git Version Control Seminar', 'desc' => 'Menguasai workflow git, branch management, dan cara mengatasi conflict di tim.'],
                ['title' => 'React & Vite Performance', 'desc' => 'Teknik profiling, lazy loading, dan code splitting untuk mereduksi load time aplikasi web React.'],
                ['title' => 'Backend API Security Workshop', 'desc' => 'Praktik terbaik mengamankan API backend dari celah keamanan umum menggunakan OWASP standard.'],
                ['title' => 'Kotlin Jetpack Compose Bootcamp', 'desc' => 'Bootcamp intensif membangun UI deklaratif Android modern menggunakan Jetpack Compose.'],
                ['title' => 'Tech Meetup: Dockerizing Laravel', 'desc' => 'Belajar dasar containerization menggunakan Docker untuk aplikasi PHP Laravel.'],
                // Upcoming
                ['title' => 'Intro to Next.js App Router', 'desc' => 'Eksplorasi routing sistem baru di Next.js untuk optimasi rendering di sisi server.'],
                ['title' => 'Clean Architecture in Go', 'desc' => 'Bagaimana mengimplementasikan clean architecture di Go untuk backend microservices.'],
                ['title' => 'Flutter State Management Showdown', 'desc' => 'Membandingkan Bloc, Provider, dan Riverpod di project Flutter skala industri.'],
                ['title' => 'Web Performance Optimization 101', 'desc' => 'Langkah-langkah taktis memperbaiki Core Web Vitals pada website modern.'],
            ],
            'AI Research Indonesia' => [
                ['title' => 'AI & Deep Learning Seminar', 'desc' => 'Pembahasan dasar-dasar Neural Network dan pengenalan Deep Learning untuk klasifikasi gambar.'],
                ['title' => 'NLP with HuggingFace Workshop', 'desc' => 'Praktik implementasi model NLP seperti BERT dan RoBERTa menggunakan library HuggingFace.'],
                ['title' => 'Intro to LLMs & Prompt Engineering', 'desc' => 'Memahami dasar LLM, tokenisasi, dan cara merancang prompt yang efektif.'],
                ['title' => 'Computer Vision Workshop', 'desc' => 'Implementasi deteksi objek real-time menggunakan YOLOv8 dan OpenCV.'],
                ['title' => 'Neural Network Architectures Talk', 'desc' => 'Diskusi mendalam mengenai evolusi arsitektur CNN, RNN, hingga Transformer.'],
                ['title' => 'AI Ethics & Applications Panel', 'desc' => 'Diskusi panel mengenai bias data, hak cipta model AI, dan regulasi AI di Indonesia.'],
                // Upcoming
                ['title' => 'Fine-tuning Llama-3 locally', 'desc' => 'Tutorial langkah demi langkah mem-fine-tune model Llama-3 pada dataset lokal.'],
                ['title' => 'RAG System Implementation', 'desc' => 'Membangun sistem tanya jawab dokumen menggunakan Vector Database dan LangChain.'],
                ['title' => 'AI Agents with Autogen', 'desc' => 'Konsep dan cara kerja Multi-Agent system untuk otomatisasi tugas kompleks.'],
                ['title' => 'Deploying ML Models to Production', 'desc' => 'Belajar Docker, FastAPI, dan Triton Inference Server untuk model deployment.'],
            ],
            'Bandung Running Club' => [
                ['title' => 'Weekly Sprint Session Dago', 'desc' => 'Latihan sprint interval di track Dago untuk melatih kecepatan dan kapasitas paru-paru.'],
                ['title' => 'Marathon Prep Talk & Nutrition', 'desc' => 'Diskusi bersama pelatih mengenai persiapan fisik dan pemenuhan nutrisi sebelum lari marathon.'],
                ['title' => 'Weekly Run Gasibu', 'desc' => 'Lari sore bersama mengelilingi lapangan Gasibu dilanjutkan dengan pendinginan santai.'],
                ['title' => 'Cardio Endurance Training Guide', 'desc' => 'Tips meningkatkan daya tahan jantung bagi pemula agar tidak mudah lelah.'],
                ['title' => 'Sunday Morning Run Dago', 'desc' => 'Rutinitas lari pagi sejauh 5K menyusuri jalan Dago yang segar di hari Minggu.'],
                ['title' => 'Night Run Bandung Heritage', 'desc' => 'Lari malam menyusuri jalan-jalan bersejarah di kota Bandung dengan rute santai.'],
                // Upcoming
                ['title' => 'Lembang Trail Run 10K', 'desc' => 'Petualangan lari lintas alam menyusuri perbukitan hijau di kawasan Lembang.'],
                ['title' => 'Pace Management Workshop', 'desc' => 'Belajar mengatur tempo lari (pace) agar energi tidak cepat habis di jarak jauh.'],
                ['title' => 'Running Gear and Shoes Review', 'desc' => 'Bedah jenis sepatu lari (daily trainer vs carbon plate) sesuai gaya lari Anda.'],
                ['title' => 'Bandung Half Marathon Practice', 'desc' => 'Latihan bersama dengan target jarak 15K sebagai persiapan Half Marathon.'],
            ],
            'Jakarta Badminton Lovers' => [
                ['title' => 'Weekly Double Play Senayan', 'desc' => 'Latihan tanding ganda badminton di Hall Senayan dengan fokus rotasi pemain.'],
                ['title' => 'Badminton Technique Class', 'desc' => 'Pelatihan teknik dasar grip raket, footwork, dan cara melakukan lob yang benar.'],
                ['title' => 'Mixed Doubles Session Pluit', 'desc' => 'Tanding persahabatan ganda campuran untuk menguji kekompakan komunikasi partner.'],
                ['title' => 'Singles Drill Session Palmerah', 'desc' => 'Latihan fisik khusus pemain tunggal yang menuntut kecepatan dan stamina tinggi.'],
                ['title' => 'Friendly Match Senayan GBK', 'desc' => 'Tanding santai antar anggota komunitas di lapangan badminton GBK.'],
                ['title' => 'Badminton Smash & Drop Shot Drill', 'desc' => 'Fokus latihan teknik melakukan smash tajam dan drop shot tipis di depan net.'],
                // Upcoming
                ['title' => 'Fun Tournament BSD Cup 2026', 'desc' => 'Turnamen badminton internal komunitas dengan hadiah menarik untuk kategori ganda.'],
                ['title' => 'Footwork Mastery Clinic', 'desc' => 'Sesi intensif melatih kelincahan kaki agar menjangkau semua sudut lapangan.'],
                ['title' => 'Serving and Netting Workshop', 'desc' => 'Meningkatkan akurasi servis pendek dan tipisnya netting untuk mengontrol permainan.'],
                ['title' => 'Badminton Physics & Strategy', 'desc' => 'Diskusi santai mengenai arah angin lapangan, ketegangan senar raket, dan strategi bertahan.'],
            ],
            'Indonesia Digital Artists' => [
                ['title' => 'Digital Painting Demo', 'desc' => 'Demonstrasi melukis lanskap secara digital menggunakan Photoshop dari nol.'],
                ['title' => 'Blender 3D for Beginners', 'desc' => 'Pengenalan antarmuka Blender, teknik modeling dasar, dan pemberian material.'],
                ['title' => 'Character Design Critique', 'desc' => 'Sesi review dan feedback portofolio desain karakter buatan anggota komunitas.'],
                ['title' => 'Webtoon Drawing Class', 'desc' => 'Langkah-langkah merancang storyboard, paneling, dan pewarnaan komik web.'],
                ['title' => 'Watercolor Digital Techniques', 'desc' => 'Meniru efek cat air tradisional menggunakan brush digital di Clip Studio Paint.'],
                ['title' => 'Sketching Kota Tua Together', 'desc' => 'Menggambar arsitektur bangunan kolonial Kota Tua secara langsung di kertas sketsa.'],
                // Upcoming
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
                // Upcoming
                ['title' => 'Figma Prototyping: Interactive Components', 'desc' => 'Mengeksplorasi pembuatan animasi mikro dan prototipe interaktif di Figma.'],
                ['title' => 'UX Research Methods Deep Dive', 'desc' => 'Belajar metode survei, card sorting, dan user interview untuk riset produk.'],
                ['title' => 'Designing for Accessibility (a11y)', 'desc' => 'Bagaimana membuat produk digital yang ramah bagi pengguna disabilitas.'],
                ['title' => 'UX Writing: Crafting Clear Copy', 'desc' => 'Memahami peran mikro-kop (microcopy) dalam memandu user menggunakan aplikasi.'],
            ],
            'Bandung Indie Music' => [
                ['title' => 'Home Studio Setup on Budget', 'desc' => 'Tips memilih audio interface, mikrofon, dan DAW murah untuk rekaman di rumah.'],
                ['title' => 'Songwriting Workshop', 'desc' => 'Menemukan progresi akord, menulis lirik puitis, dan menyusun struktur lagu yang enak.'],
                ['title' => 'Indie Band Jam Session', 'desc' => 'Sesi jamming antar grup musik band lokal di studio musik ternama Bandung.'],
                ['title' => 'Acoustic Open Mic Night', 'desc' => 'Wadah bagi musisi solo dan duo menampilkan lagu karya mereka secara akustik.'],
                ['title' => 'Mixing & Mastering Basics', 'desc' => 'Pengenalan EQ, kompresor, reverb, dan limiter dalam tahap pasca-produksi lagu.'],
                ['title' => 'Indie Stage Showcase Bandung', 'desc' => 'Konser mini menampilkan band-band baru berbakat dari komunitas Bandung.'],
                // Upcoming
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
                // Upcoming
                ['title' => 'Percussive Fingerstyle Clinic', 'desc' => 'Teknik memainkan gitar akustik sekaligus sebagai alat perkusi (ketukan).'],
                ['title' => 'Sing-Along Gathering: Pop Indo 90s', 'desc' => 'Bernostalgia menyanyikan lagu-lagu pop legendaris Indonesia era 90-an bersama.'],
                ['title' => 'Songwriting Acoustic Challenge', 'desc' => 'Tantangan menulis dan merekam lagu akustik pendek dalam waktu 24 jam.'],
                ['title' => 'Choosing the Right Acoustic Guitar', 'desc' => 'Panduan memilih kayu gitar, bentuk bodi (dreadnought vs concert), dan preamp.'],
            ],
            'Indonesia Space Science Community' => [
                ['title' => 'Solar System Explorer Lecture', 'desc' => 'Kuliah umum virtual membahas formasi planet dan karakteristik bulan-bulan di tata surya.'],
                ['title' => 'Astrophotography Basics', 'desc' => 'Cara memotret galaksi Bima Sakti (Milky Way) menggunakan kamera DSLR sederhana.'],
                ['title' => 'Space Exploration Talk', 'desc' => 'Diskusi tentang misi Mars Rover terbaru dan rencana kolonisasi planet merah.'],
                ['title' => 'Telescope Setup Guide', 'desc' => 'Panduan merakit, kalibrasi finder-scope, dan merawat lensa teropong bintang.'],
                ['title' => 'Rocket Propulsion Seminar', 'desc' => 'Mempelajari prinsip kerja mesin roket kimiawi dan roket ion masa depan.'],
                ['title' => 'Bosscha Stargazing Night', 'desc' => 'Kunjungan langsung ke Observatorium Bosscha Lembang untuk meneropong bintang.'],
                // Upcoming
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
                // Upcoming
                ['title' => 'British Parliamentary Format Class', 'desc' => 'Pengenalan format parlemen Inggris (BP) yang membagi tim menjadi 4 fraksi.'],
                ['title' => 'Motion Analysis & Case Building', 'desc' => 'Cara membedah mosi debat rumit dan merancang argumen utama yang solid.'],
                ['title' => 'Rebuttal Mastery Workshop', 'desc' => 'Teknik menyerang argumen lawan secara tajam, logis, dan persuasif.'],
                ['title' => 'Adjudication Core Principles', 'desc' => 'Belajar menjadi juri debat (adjudicator) yang adil dan objektif.'],
            ],
            'Indonesian Startup Founders Hub' => [
                ['title' => 'Startup Valuation Talk', 'desc' => 'Memahami cara menghitung valuasi startup di tahap awal sebelum pendanaan.'],
                ['title' => 'Fundraising Strategies Panel', 'desc' => 'Diskusi panel dengan modal ventura lokal mengenai kriteria startup yang layak didanai.'],
                ['title' => 'Lean Canvas Workshop', 'desc' => 'Praktik merangkum model bisnis startup dalam satu lembar Lean Canvas.'],
                ['title' => 'Co-founder Dating Night', 'desc' => 'Wadah mempertemukan founder teknis (CTO) dengan founder bisnis (CEO).'],
                ['title' => 'Growth Hacking Seminar', 'desc' => 'Taktik pemasaran organik bernilai tinggi untuk menaikkan retensi pengguna secara eksponensial.'],
                ['title' => 'Startup Pitching Day Jakarta', 'desc' => 'Kesempatan bagi founder mempresentasikan ide startup di depan investor.'],
                // Upcoming
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
                // Upcoming
                ['title' => 'Macroeconomics & Stock Market', 'desc' => 'Pengaruh suku bunga BI, inflasi, dan nilai tukar rupiah terhadap indeks saham.'],
                ['title' => 'Value Investing Strategy ala Buffet', 'desc' => 'Mencari emiten salah harga yang memiliki moat bisnis yang kuat.'],
                ['title' => 'Avoiding Pump and Dump Stocks', 'desc' => 'Tips mengenali saham gorengan bernilai transaksi janggal agar modal tetap aman.'],
                ['title' => 'IDX Stock Screener Tutorial', 'desc' => 'Menggunakan fitur filter emiten untuk mempermudah proses seleksi saham harian.'],
            ],
            'Mobile Legends Indonesia Association' => [
                ['title' => 'Draft Pick Analysis', 'desc' => 'Bedah hero counter, meta ban-pick terbaru, dan strategi komposisi tim MLBB.'],
                ['title' => 'Midlaner Rotation Class', 'desc' => 'Tips rotasi efektif bagi role midlaner untuk memenangkan lane lain.'],
                ['title' => 'Esports Career Seminar', 'desc' => 'Peluang karir di industri esports sebagai pro-player, pelatih, caster, atau manajer.'],
                ['title' => 'Regular Fun Match Komunitas', 'desc' => 'Mabar santai antar anggota komunitas sekaligus ajang silaturahmi offline.'],
                ['title' => 'Ranked Push Together Event', 'desc' => 'Berkumpul bersama untuk push rank tier Mythic secara berkelompok (party).'],
                ['title' => 'MLBB Fun Tournament Season 12', 'desc' => 'Turnamen 5v5 persahabatan komunitas berhadiah ribuan diamond.'],
                // Upcoming
                ['title' => 'Goldlaner Positioning Guide', 'desc' => 'Bagaimana menjaga posisi saat war agar tidak gampang tereliminasi.'],
                ['title' => 'Roamer and Shotcaller Mastery', 'desc' => 'Melatih kemampuan inisiator war dan memandu keputusan tim secara real-time.'],
                ['title' => 'MLBB Coach Analysis Session', 'desc' => 'Review gameplay rekaman pertandingan anggota oleh mantan analis tim pro.'],
                ['title' => 'Esports Mental Health Talk', 'desc' => 'Menjaga kesehatan mental dan fisik dari kelelahan akibat bermain game terlalu lama.'],
            ],
            'Gamer PC Jakarta' => [
                ['title' => 'Liquid Cooling Setup Seminar', 'desc' => 'Panduan merakit custom loop liquid cooling untuk menjaga suhu PC tetap dingin.'],
                ['title' => 'Cyberpunk Graphics Benchmarks', 'desc' => 'Melihat perbandingan performa kartu grafis terbaru pada game dengan grafis berat.'],
                ['title' => 'Steam Summer Sale Gathering', 'desc' => 'Diskusi merekomendasikan game-game diskon terbaik di Steam Summer Sale.'],
                ['title' => 'LAN Party Retro Games', 'desc' => 'Bernostalgia main game multiplayer jadul seperti Counter-Strike 1.6 di jaringan lokal.'],
                ['title' => 'Keyboard Modding Workshop', 'desc' => 'Cara me-lube switch, memasang foam mod, dan kustomisasi keycaps keyboard mekanik.'],
                ['title' => 'PC Building Workshop & Showcase', 'desc' => 'Demonstrasi langsung merakit PC gaming berkinerja tinggi dari awal.'],
                // Upcoming
                ['title' => 'Mini-ITX Small Form Factor PC Guide', 'desc' => 'Tips merakit PC gaming berukuran kompak tapi bertenaga tinggi.'],
                ['title' => 'Custom Cables & PC Aesthetics', 'desc' => 'Mempelajari kerapian kabel manajemen dan pemasangan lampu RGB yang estetik.'],
                ['title' => 'PC Undervolting & Overclocking', 'desc' => 'Meningkatkan performa CPU/GPU atau menghemat daya tanpa menurunkan kinerja.'],
                ['title' => 'Gamer PC Meetup & Setup Exhibition', 'desc' => 'Pameran rig PC gaming kustom milik anggota komunitas di Jakarta.'],
            ],
            'Street Photography Jakarta' => [
                ['title' => 'Night Street Photography', 'desc' => 'Tips menangkap ekspresi orang dan bayangan estetis di bawah penerangan lampu jalanan.'],
                ['title' => 'Architectural Hunt Meetup', 'desc' => 'Berburu foto geometri dan simetri pada gedung-gedung pencakar langit di Sudirman.'],
                ['title' => 'Visual Storytelling Talk', 'desc' => 'Bagaimana membuat sebuah foto tunggal menceritakan emosi dan cerita yang mendalam.'],
                ['title' => 'Street Portraits Workshop', 'desc' => 'Teknik mendekati orang asing di jalanan untuk meminta izin memotret potret mereka.'],
                ['title' => 'Black & White Editing Clinic', 'desc' => 'Mengolah foto street agar berkarakter kuat menggunakan kontras hitam putih.'],
                ['title' => 'Street Photowalk Kota Tua Jakarta', 'desc' => 'Kegiatan hunting foto bersama menyusuri gang-gang antik di Kota Tua Jakarta.'],
                // Upcoming
                ['title' => 'Jakarta MRT Station Photowalk', 'desc' => 'Mencari sudut estetik arsitektur stasiun dan mobilitas penumpang MRT Jakarta.'],
                ['title' => 'Candid Street Photography Tips', 'desc' => 'Trik memotret momen candid secara natural tanpa membuat subjek terganggu.'],
                ['title' => 'Street Photography Book Project', 'desc' => 'Diskusi mengurasi foto terbaik anggota untuk diterbitkan menjadi buku kolektif.'],
                ['title' => 'Decisive Moment Mastery', 'desc' => 'Melatih refleks mata dan jari untuk menekan tombol shutter di waktu paling tepat.'],
            ],
            'Mobile Photography Indonesia' => [
                ['title' => 'Mobile Camera Settings Seminar', 'desc' => 'Memahami mode profesional (ISO, Shutter Speed, Focus) di kamera bawaan HP.'],
                ['title' => 'Macro Photo Walk Indonesia', 'desc' => 'Hunting foto serangga dan detail bunga kecil menggunakan lensa makro eksternal ponsel.'],
                ['title' => 'Product Photography Phone', 'desc' => 'Trik memotret produk UMKM secara menarik hanya bermodalkan smartphone dan lampu meja.'],
                ['title' => 'Composition Techniques Guide', 'desc' => 'Menguasai Rule of Thirds, Leading Lines, dan Framing untuk komposisi foto HP yang matang.'],
                ['title' => 'Mobile Video Editing Workshop', 'desc' => 'Langkah mengedit video cinematic pendek untuk reels menggunakan aplikasi CapCut.'],
                ['title' => 'Street Photowalk: Lightroom Mobile', 'desc' => 'Workshop memotret dan menyunting warna foto secara instan lewat aplikasi Lightroom HP.'],
                // Upcoming
                ['title' => 'Smartphone Night Photography', 'desc' => 'Tips menghasilkan foto malam hari yang minim noise menggunakan mode malam (Night Mode).'],
                ['title' => 'Flatlay Photography for Instagram', 'desc' => 'Teknik menyusun barang di atas meja secara estetik untuk konten media sosial.'],
                ['title' => 'Mobile Photo Editing: Snapseed', 'desc' => 'Menggunakan brush tool dan double exposure di Snapseed untuk edit foto kreatif.'],
                ['title' => 'Mobile Photography Exhibition', 'desc' => 'Pameran karya foto terbaik yang diambil menggunakan kamera smartphone.'],
            ],
            'Green Earth Indonesia' => [
                ['title' => 'Mangrove Clean Up Event', 'desc' => 'Aksi sukarela membersihkan sampah plastik di kawasan hutan mangrove Pantai Indah Kapuk.'],
                ['title' => 'Reforestation Seminar Indo', 'desc' => 'Edukasi tentang pentingnya penanaman kembali pohon endemik untuk mencegah tanah longsor.'],
                ['title' => 'Plastic Recycling Workshop', 'desc' => 'Mengolah sampah botol plastik bekas menjadi barang berguna seperti pot tanaman.'],
                ['title' => 'Eco Brick Crafting Session', 'desc' => 'Membuat ecobrick dari sampah plastik kemasan untuk digunakan sebagai bahan bangunan alternatif.'],
                ['title' => 'Volunteer Gathering Bogor', 'desc' => 'Temu akrab para relawan lingkungan guna mendiskusikan rencana kerja semester depan.'],
                ['title' => 'Tree Planting Day Bogor', 'desc' => 'Menanam ratusan bibit pohon mahoni di lahan kritis kaki gunung Salak Bogor.'],
                // Upcoming
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
                // Upcoming
                ['title' => 'Decluttering & Preloved Bazaar', 'desc' => 'Kegiatan membersihkan barang tak terpakai di rumah dan menjualnya sebagai barang preloved.'],
                ['title' => 'Making Eco-Enzyme at Home', 'desc' => 'Memanfaatkan kulit buah sisa menjadi cairan pembersih alami serbaguna.'],
                ['title' => 'Upcycling Old Clothes Workshop', 'desc' => 'Mengubah kaos atau pakaian bekas menjadi tas belanja (tote bag) modis.'],
                ['title' => 'Zero Waste Home audit guide', 'desc' => 'Langkah-langkah mengaudit jumlah produksi sampah di rumah masing-masing.'],
            ],
            'Yogyakarta Yoga & Mindfulness' => [
                ['title' => 'Mindfulness Meditation Session', 'desc' => 'Sesi meditasi hening terpandu untuk menenangkan pikiran dan meredakan stres harian.'],
                ['title' => 'Yin Yoga for Relaxation', 'desc' => 'Latihan yoga gerakan perlahan dengan menahan pose lebih lama untuk fleksibilitas sendi.'],
                ['title' => 'Breathwork (Pranayama) Workshop', 'desc' => 'Teknik melatih pernapasan dalam guna meningkatkan pasokan oksigen dan ketenangan jiwa.'],
                ['title' => 'Sound Healing Session Yogyakarta', 'desc' => 'Relaksasi mendalam menggunakan getaran frekuensi suara dari singing bowl.'],
                ['title' => 'Restorative Yoga Meetup', 'desc' => 'Yoga menggunakan alat bantu bantal/balok untuk merilekskan otot yang tegang.'],
                ['title' => 'Gentle Flow Yoga Prambanan', 'desc' => 'Latihan yoga mengalir lembut dengan latar belakang pemandangan candi Prambanan.'],
                // Upcoming
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
                // Upcoming
                ['title' => 'Fat Loss vs Weight Loss Seminar', 'desc' => 'Memahami perbedaan susut lemak tubuh dan turunnya berat badan secara keseluruhan.'],
                ['title' => 'Intermittent Fasting Guide', 'desc' => 'Cara aman menerapkan metode puasa berkala bagi kesehatan metabolisme.'],
                ['title' => 'Smoothies and Healthy Juices Demo', 'desc' => 'Demonstrasi membuat jus sayur buah padat nutrisi untuk detoksifikasi alami.'],
                ['title' => 'HIIT Workout Challenge 15 Mins', 'desc' => 'Latihan interval intensitas tinggi selama 15 menit bersama pelatih kebugaran.'],
            ]
        ];

        // Consistent category image map from Unsplash
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
                    'Sharing dong framework CSS favorit kalian buat project cepat.'
                ],
                'reply' => [
                    'Bisa coba lirik ThinkPad bekas seri L atau Asus Vivobook gan, ramah di dompet.',
                    'Coba cek state management Anda, biasanya karena lupa dispose listener.',
                    'Untuk proyek skala besar, RESTful yang terstruktur sudah cukup, GraphQL punya setup cost besar.',
                    'Saya sedang pelajari! Memang konsep Server Component-nya agak membingungkan di awal.',
                    'TailwindCSS masih jadi andalan saya karena utility-first nya cepat dipelajari.'
                ]
            ],
            'Sports' => [
                'post' => [
                    'Adakah tips mencegah cedera lutut saat latihan lari jarak jauh?',
                    'Rekomendasi raket bulutangkis untuk pemula tipe kontrol?',
                    'Berapa kali idealnya latihan kardio dalam seminggu bagi pemula?',
                    'Bagaimana cara konsisten bangun jam 5 pagi buat olahraga?',
                    'Sharing rute lari pagi yang teduh di sekitar Bandung dong.'
                ],
                'reply' => [
                    'Jangan lupa pemanasan dinamis dan penguatan otot paha (quadriceps).',
                    'Coba pakai Yonex Nanoray atau Li-Ning Windstorm, ringan dan cocok buat pemula.',
                    'Idealnya 2-3 kali seminggu dengan durasi 30 menit per sesi, jangan langsung diforsir.',
                    'Tidur lebih awal sebelum jam 10 malam dan taruh alarm jauh dari tempat tidur.',
                    'Daerah Dago Atas atau keliling komplek ITB masih lumayan sejuk dan rimbun.'
                ]
            ],
            'Art & Design' => [
                'post' => [
                    'Ada yang tahu tempat download brush gratis buat Photoshop yang bagus?',
                    'Bagaimana cara menentukan palet warna yang harmonis untuk UI aplikasi?',
                    'Butuh waktu berapa lama bagi kalian untuk menguasai anatomi dasar?',
                    'Software alternatif gratis pengganti Adobe Illustrator apa ya?',
                    'Tips mengatasi creative block saat kehabisan ide menggambar.'
                ],
                'reply' => [
                    'Coba cari di BrushLovers atau langsung ke DeviantArt, banyak creator bagi gratis.',
                    'Gunakan tools seperti Coolors.co atau Adobe Color untuk generate palet berbasis harmoni.',
                    'Konsisten latihan sketsa 30 menit sehari, butuh waktu sekitar 3-6 bulan untuk mulai lancar.',
                    'Inkscape atau Figma sebenarnya sangat mumpuni untuk membuat grafis vektor gratis.',
                    'Coba istirahat sejenak, jalan-jalan keluar, atau cari inspirasi di Pinterest/Behance.'
                ]
            ],
            'Music' => [
                'post' => [
                    'Bagaimana cara melatih jangkauan vokal (vocal range) secara mandiri?',
                    'Rekomendasi audio interface terbaik di kisaran harga di bawah 1.5 juta?',
                    'Bagaimana menyusun struktur lagu pop agar tidak terdengar membosankan?',
                    'Ada yang tahu chord progresi yang paling sering dipakai lagu hits?',
                    'Tips meredam gema di kamar tidur untuk rekaman vokal rumahan.'
                ],
                'reply' => [
                    'Lakukan pemanasan vokal lip trill dan humming setiap pagi secara teratur.',
                    'Focusrite Scarlett Solo Gen 3 atau Behringer UMC202HD sangat bagus di kelas harga itu.',
                    'Coba tambahkan variasi dinamika di bagian bridge atau ganti instrumen pengiring.',
                    'Progresi chord I - V - vi - IV paling sering dipakai, dijamin terdengar familiar.',
                    'Gunakan karpet tebal, pasang tirai jendela, dan taruh gantungan baju di sudut kamar.'
                ]
            ],
            'Education & Science' => [
                'post' => [
                    'Ada rekomendasi buku astronomi populer untuk pemula?',
                    'Bagaimana format menyusun argumen debat bahasa Inggris yang kuat?',
                    'Apakah teori Big Bang adalah satu-satunya penjelasan asal mula alam semesta?',
                    'Tips belajar bahasa Inggris cepat lewat film atau podcast.',
                    'Bagaimana cara kerja teleskop pantul (reflektor) dibanding teropong biasa?'
                ],
                'reply' => [
                    'Buku "Cosmos" karya Carl Sagan wajib dibaca, bahasanya sangat indah dan puitis.',
                    'Gunakan struktur A-R-E-L: Assertion, Reasoning, Evidence, Link-back.',
                    'Saat ini Big Bang adalah model paling didukung bukti ilmiah, seperti radiasi latar kosmik.',
                    'Tonton tanpa subtitle bahasa Indonesia, gunakan subtitle bahasa Inggris untuk melatih listening.',
                    'Teleskop reflektor menggunakan cermin cekung untuk mengumpulkan cahaya, bebas dari cacat warna.'
                ]
            ],
            'Business & Finance' => [
                'post' => [
                    'Bagaimana cara membedakan saham yang under-valued dengan yang jebakan (value trap)?',
                    'Lebih baik fokus bangun produk dulu atau cari pendanaan di tahap startup awal?',
                    'Tips mengelola dana darurat bagi pekerja lepas (freelancer).',
                    'Berapa persen alokasi investasi saham yang ideal untuk anak muda?',
                    'Bagaimana cara mendaftarkan merek dagang secara legal di Indonesia?'
                ],
                'reply' => [
                    'Periksa apakah labanya konsisten bertumbuh atau utangnya terlalu menumpuk.',
                    'Fokus ke Product-Market Fit dulu. Investor tidak akan mendanai ide tanpa bukti traksi.',
                    'Freelancer sebaiknya menyiapkan dana darurat minimal 6-9 bulan rata-rata pengeluaran bulanan.',
                    'Bisa gunakan rumus 100 dikurangi umur Anda untuk porsi aset berisiko seperti saham.',
                    'Bisa diajukan online lewat portal resmi DJKI Kemenkumham, biayanya relatif terjangkau.'
                ]
            ],
            'Gaming' => [
                'post' => [
                    'Adakah tips rotasi lane yang baik buat roamer di META Mobile Legends sekarang?',
                    'PC gaming budget 8 juta sekarang sudah bisa dapet spek apa aja ya?',
                    'Siapa hero counter terbaik untuk melawan Harith di gold lane?',
                    'Apakah upgrade RAM dari 8GB ke 16GB sangat terasa untuk gaming?',
                    'Turnamen MLBB komunitas berikutnya kapan diadakan lagi?'
                ],
                'reply' => [
                    'Selalu berikan vision di area turtle/lord dan bantu midlaner clear wave lebih dulu.',
                    'Bisa dapet Intel Core i3 gen 12/Ryzen 5 dengan VGA GTX 1650 atau RX 6600 bekas.',
                    'Gunakan Minsitthar untuk membatasi dash-nya atau pakai hero burst seperti Brody/Popol.',
                    'Sangat terasa! Stuttering di game berat open world akan berkurang jauh.',
                    'Turnamen cup komunitas akan segera diselenggarakan di akhir bulan ini, pantau terus info event!'
                ]
            ],
            'Photography' => [
                'post' => [
                    'Adakah yang punya tips memotret street photo candid tanpa terlihat mencurigakan?',
                    'Lebih baik beli lensa prime 50mm f/1.8 atau lensa zoom bawaan kit dulu?',
                    'Bagaimana cara menjaga kestabilan tangan saat memotret dengan HP tanpa tripod?',
                    'Aplikasi edit foto HP apa yang paling komplit fiturnya selain Lightroom?',
                    'Sharing dong spot foto street paling estetik di daerah Jakarta.'
                ],
                'reply' => [
                    'Gunakan lensa yang tidak terlalu besar, bertingkahlah seperti turis biasa dan tersenyumlah.',
                    'Lensa 50mm f/1.8 sangat bagus untuk melatih komposisi dan menghasilkan efek bokeh indah.',
                    'Posisikan siku menempel di dada untuk menopang HP dan tahan napas sesaat saat menekan shutter.',
                    'Snapseed sangat bagus untuk editing lokal, atau VSCO jika menyukai filter warna film.',
                    'Kawasan Glodok Pancoran, jembatan pinisi Sudirman, dan sekitar stasiun MRT Blok M.'
                ]
            ],
            'Environment' => [
                'post' => [
                    'Di mana kita bisa menyalurkan sampah plastik yang sudah terpilah di Jakarta?',
                    'Bagaimana cara memulai membuat ecobrick di rumah dari sampah kemasan?',
                    'Ada tips membuat pupuk kompos dari sampah dapur agar tidak bau menyengat?',
                    'Apakah penggunaan tas belanja spunbond benar-benar ramah lingkungan?',
                    'Bagaimana cara bergabung menjadi relawan penanaman pohon minggu depan?'
                ],
                'reply' => [
                    'Bisa disalurkan ke Bank Sampah terdekat atau lewat layanan dropbox Waste4Change.',
                    'Bersihkan dan keringkan sampah plastik, gunting kecil-kecil, lalu padatkan ke dalam botol bekas.',
                    'Pastikan kelembapan terjaga dan imbangi sampah basah (hijau) dengan sampah kering (cokelat/daun).',
                    'Spunbond baru ramah lingkungan jika dipakai berulang-ulang ratusan kali, bukan sekali pakai.',
                    'Cukup klik daftar di halaman detail event Penanaman Pohon di aplikasi ini!'
                ]
            ],
            'Health & Wellness' => [
                'post' => [
                    'Bagaimana mengatasi insomnia dan pikiran cemas sebelum tidur?',
                    'Ada rekomendasi gerakan yoga sederhana untuk peregangan setelah seharian duduk bekerja?',
                    'Tips merancang menu makanan diet sehat yang kenyang tahan lama.',
                    'Apakah meditasi 10 menit sehari sudah cukup untuk melatih fokus?',
                    'Olahraga apa yang paling cocok untuk membakar lemak perut dengan cepat?'
                ],
                'reply' => [
                    'Matikan layar HP minimal 1 jam sebelum tidur dan lakukan latihan pernapasan 4-7-8.',
                    'Gerakan Child’s Pose, Cat-Cow stretch, dan Cobra Pose sangat baik meredakan kaku punggung.',
                    'Perbanyak porsi serat dari sayuran dan protein seperti dada ayam atau tahu tempe.',
                    'Sangat cukup! Konsistensi jauh lebih penting daripada durasi yang lama tapi jarang.',
                    'Kombinasi latihan kekuatan (strength training) dan kardio HIIT, diiringi defisit kalori.'
                ]
            ]
        ];

        // Process each community to generate its 10 events
        foreach ($communities as $community) {
            $category = $community->category;
            $categoryName = $category->name;
            $commName = $community->name;

            // Get events data for this community
            $eventsPool = $eventsDataMap[$commName] ?? [
                ['title' => 'Event Keren ' . $commName, 'desc' => 'Deskripsi event komunitas.'],
            ];

            // Get community members once to ensure registration compliance
            // We load user IDs of members in this community
            $memberUserIds = DB::table('community_members')
                ->where('community_id', $community->id)
                ->pluck('user_id')
                ->toArray();

            if (empty($memberUserIds)) {
                // Fallback to all users if somehow empty
                $memberUserIds = $allUsers->pluck('id')->toArray();
            }

            // Shuffle members for random selection later
            $membersCount = count($memberUserIds);

            // Generate 10 events (6 PAST, 4 UPCOMING)
            for ($i = 0; $i < 10; $i++) {
                $isPast = ($i < 6); // First 6 are PAST, next 4 are UPCOMING
                $eventInfo = $eventsPool[$i] ?? ['title' => "Event {$i} - {$commName}", 'desc' => "Deskripsi untuk event {$i}"];
                
                if ($isPast) {
                    $eventDate = Carbon::now()->subDays(rand(7, 90))->format('Y-m-d');
                    $status = 'PAST';
                } else {
                    $eventDate = Carbon::now()->addDays(rand(1, 60))->format('Y-m-d');
                    $status = 'UPCOMING';
                }

                $eventTime = sprintf('%02d:00', rand(8, 20));
                
                // Determine number of attendees (5 to 30)
                // Since community has at least 10 members (up to 40), K can be rand(5, min(30, membersCount))
                $sampleSize = rand(5, min(30, $membersCount));
                
                // Randomly select K members
                $attendeeIds = array_rand(array_flip($memberUserIds), $sampleSize);
                if (!is_array($attendeeIds)) {
                    $attendeeIds = [$attendeeIds];
                }

                // Choose a cover image matching the category
                $coverImage = $categoryImageMap[$categoryName] ?? 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4';

                // Create the Event record
                $event = Event::create([
                    'community_id' => $community->id,
                    'category_id' => $category->id,
                    'title' => $eventInfo['title'],
                    'description' => $eventInfo['desc'],
                    'event_date' => $eventDate,
                    'event_time' => $eventTime,
                    'location' => rand(0, 1) ? 'Zoom Meeting' : 'Gedung Serbaguna ' . $categoryName . ' Hall ' . rand(1, 3),
                    'is_online' => rand(0, 1) ? true : false,
                    'max_attendees' => rand(30, 100),
                    'attendee_count' => count($attendeeIds),
                    'cover_image_url' => $coverImage,
                    'status' => $status,
                ]);

                // Create registrations for selected attendees
                foreach ($attendeeIds as $userId) {
                    $allRegistrations[] = [
                        'user_id' => $userId,
                        'event_id' => $event->id,
                        'status' => $isPast ? 'ATTENDED' : 'REGISTERED',
                        'registered_at' => Carbon::parse($eventDate)->subDays(rand(1, 5))->toDateTimeString(),
                        'attended_at' => $isPast ? Carbon::parse($eventDate)->toDateTimeString() : null,
                        'created_at' => now(),
                        'updated_at' => now(),
                    ];
                }

                // Create ratings for PAST events only
                if ($isPast && count($attendeeIds) > 0) {
                    // Let a random subset (50% to 100%) of attendees leave ratings
                    $ratersCount = rand(max(1, (int)(count($attendeeIds) * 0.5)), count($attendeeIds));
                    $raters = array_rand(array_flip($attendeeIds), $ratersCount);
                    if (!is_array($raters)) {
                        $raters = [$raters];
                    }

                    $eventRatings = [];
                    foreach ($raters as $userId) {
                        // Weighted probability
                        $rand = rand(1, 100);
                        if ($rand <= 60) {
                            $ratingValue = 5;
                        } elseif ($rand <= 90) {
                            $ratingValue = 4;
                        } else {
                            $ratingValue = 3;
                        }
                        $eventRatings[$userId] = $ratingValue;
                    }

                    // Enforce average between 3.5 and 4.9
                    if (count($eventRatings) > 0) {
                        $sum = array_sum($eventRatings);
                        $avg = $sum / count($eventRatings);
                        if ($avg > 4.9) {
                            // Too high: reduce some 5s to 4s
                            foreach ($eventRatings as $userId => $val) {
                                if ($val === 5) {
                                    $eventRatings[$userId] = 4;
                                    $sum = array_sum($eventRatings);
                                    $avg = $sum / count($eventRatings);
                                    if ($avg <= 4.9) {
                                        break;
                                    }
                                }
                            }
                        } elseif ($avg < 3.5) {
                            // Too low: increase some 3s to 4s or 5s
                            foreach ($eventRatings as $userId => $val) {
                                if ($val === 3) {
                                    $eventRatings[$userId] = 4;
                                    $sum = array_sum($eventRatings);
                                    $avg = $sum / count($eventRatings);
                                    if ($avg >= 3.5) {
                                        break;
                                    }
                                }
                            }
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

            // 4. Forum Messages (Posts and Replies)
            // We want 20-50 messages per community.
            // We will seed 8 thread posts and each post has 2 to 4 replies, totaling 24-40 messages.
            $patterns = $forumPatterns[$categoryName] ?? [
                'post' => ['Halo semuanya!', 'Ada event baru?'],
                'reply' => ['Halo juga!', 'Ya, silakan cek halaman event.']
            ];

            for ($p = 0; $p < 8; $p++) {
                // Select a random member as sender of the post
                $postSenderId = $memberUserIds[array_rand($memberUserIds)];
                
                $postMessage = $patterns['post'][$p % count($patterns['post'])];
                
                // Add unique number or phrase if necessary, but keep realistic
                $postTime = Carbon::now()->subDays(rand(5, 30))->subMinutes(rand(1, 1440));

                $post = ForumMessage::create([
                    'community_id' => $community->id,
                    'sender_id' => $postSenderId,
                    'message' => $postMessage,
                    'created_at' => $postTime,
                    'updated_at' => $postTime,
                ]);

                $allForumMessages[] = $post; // Track

                // Create 2 to 4 replies
                $repliesCount = rand(2, 4);
                $lastReplyTime = $postTime;
                for ($r = 0; $r < $repliesCount; $r++) {
                    // Sender of reply must be a community member (preferably not the post author, if possible)
                    $eligibleSenders = array_diff($memberUserIds, [$postSenderId]);
                    if (empty($eligibleSenders)) {
                        $eligibleSenders = $memberUserIds;
                    }
                    $replySenderId = $eligibleSenders[array_rand($eligibleSenders)];

                    $replyMessage = $patterns['reply'][rand(0, count($patterns['reply']) - 1)];
                    
                    // Reply time is after the post time
                    $lastReplyTime = Carbon::parse($lastReplyTime)->addMinutes(rand(5, 120));

                    $reply = ForumMessage::create([
                        'community_id' => $community->id,
                        'sender_id' => $replySenderId,
                        'message' => $replyMessage,
                        'created_at' => $lastReplyTime,
                        'updated_at' => $lastReplyTime,
                    ]);

                    $allForumMessages[] = $reply;
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

        // 5. Notifications
        // Let's generate a list of realistic notifications for various users.
        // We will seed 150 notifications in total, spread across random users.
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
