<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use App\Models\TrustedApplication;
use Illuminate\Support\Facades\Hash;
use Carbon\Carbon;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Define passwords
        $adminPassword = Hash::make('Admin123!');
        $organizerPassword = Hash::make('Organizer123!');
        $memberPassword = Hash::make('User123!');

        // 1. Seed Super Admin (ID = 1, Email = admin@communityapp.com)
        $superAdmin = User::create([
            'id' => 1,
            'name' => 'Super Admin',
            'email' => 'admin@communityapp.com',
            'password' => $adminPassword,
            'phone_number' => '081122334455',
            'birth_date' => '1985-05-15',
            'gender' => 'MALE',
            'bio' => 'Administrator utama platform Community Event Management System.',
            'avatar_url' => 'https://api.dicebear.com/7.x/adventurer/svg?seed=admin',
            'role' => 'SUPER_ADMIN',
            'is_blocked' => false,
            'is_trusted' => true,
            'email_verified_at' => now(),
        ]);

        if (\Illuminate\Support\Facades\DB::getDriverName() === 'pgsql') {
            \Illuminate\Support\Facades\DB::statement("SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users))");
        }

        // Lists for Indonesian name generation
        $maleFirstNames = ['Budi', 'Joko', 'Andi', 'Ahmad', 'Rian', 'Hendra', 'Eko', 'Agus', 'Taufik', 'Aris', 'Dedi', 'Rudi', 'Fajar', 'Bambang', 'Wawan', 'Surya', 'Hadi', 'Guntur', 'Riki', 'Dian', 'Reza', 'Kevin', 'Aditya', 'Fikri', 'Rahmat', 'Anwar', 'Indra', 'Yudi', 'Ferry', 'Bagus'];
        $femaleFirstNames = ['Siti', 'Dewi', 'Mega', 'Putri', 'Diana', 'Ayu', 'Kartika', 'Wulan', 'Rina', 'Indah', 'Fitri', 'Novi', 'Sari', 'Lestari', 'Desi', 'Sri', 'Anisa', 'Citra', 'Eka', 'Amalia', 'Ria', 'Dina', 'Gita', 'Nisa', 'Siska', 'Tari', 'Yuli', 'Lia', 'Ririn', 'Mila'];
        $lastNames = ['Santoso', 'Wijaya', 'Lestari', 'Hermawan', 'Rahmawati', 'Hidayat', 'Utami', 'Setiawan', 'Puspita', 'Nugraha', 'Budiman', 'Sari', 'Susilo', 'Melati', 'Munandar', 'Prasetyo', 'Kusuma', 'Saputra', 'Pratama', 'Hadi', 'Wibowo', 'Siregar', 'Lubis', 'Nasution', 'Ginting', 'Sitorus', 'Tanjung', 'Simanjuntak', 'Pasaribu', 'Harahap', 'Gozali', 'Halim', 'Subagyo'];

        $indonesianCities = ['Jakarta', 'Bandung', 'Surabaya', 'Yogyakarta', 'Semarang', 'Medan', 'Makassar', 'Malang', 'Bogor', 'Tangerang', 'Denpasar', 'Solo'];

        $usedNames = [];

        // Helper to generate a unique Indonesian name and profile
        $generateProfile = function($gender = null) use (&$usedNames, $maleFirstNames, $femaleFirstNames, $lastNames, $indonesianCities) {
            if (!$gender) {
                $gender = rand(0, 1) ? 'MALE' : 'FEMALE';
            }

            $firstList = ($gender === 'MALE') ? $maleFirstNames : $femaleFirstNames;
            
            do {
                $first = $firstList[array_rand($firstList)];
                $last = $lastNames[array_rand($lastNames)];
                $name = $first . ' ' . $last;
            } while (in_array($name, $usedNames));

            $usedNames[] = $name;
            $city = $indonesianCities[array_rand($indonesianCities)];
            $age = rand(19, 45);
            $birthDate = Carbon::now()->subYears($age)->subDays(rand(1, 365))->format('Y-m-d');
            
            // Generate a valid phone number format: e.g. 081212345678
            $prefixes = ['0812', '0813', '0821', '0852', '0857', '0878', '0896'];
            $phoneNumber = $prefixes[array_rand($prefixes)] . rand(10000000, 99999999);

            $bios = [
                "Pegiat komunitas aktif berbasis di {$city}.",
                "Suka belajar hal-hal baru dan berkontribusi untuk lingkungan sekitar di {$city}.",
                "Senang berkenalan dengan orang baru dan membangun jejaring di {$city}.",
                "Mari berkolaborasi dan membuat dampak positif bersama di {$city}.",
                "Fokus mengembangkan diri dan berbagi ilmu di area {$city}."
            ];
            $bio = $bios[array_rand($bios)];

            return [
                'name' => $name,
                'gender' => $gender,
                'birth_date' => $birthDate,
                'phone_number' => $phoneNumber,
                'bio' => $bio,
            ];
        };

        // 2. Seed 5 Admins
        for ($i = 1; $i <= 5; $i++) {
            $adminProfile = $generateProfile();
            User::create([
                'name' => "Admin " . $i,
                'email' => "admin{$i}@communityapp.com",
                'password' => $adminPassword,
                'phone_number' => $adminProfile['phone_number'],
                'birth_date' => $adminProfile['birth_date'],
                'gender' => $adminProfile['gender'],
                'bio' => 'Administrator platform Community Event Management System.',
                'avatar_url' => "https://api.dicebear.com/7.x/adventurer/svg?seed=admin{$i}",
                'role' => 'ADMIN',
                'is_blocked' => false,
                'is_trusted' => true,
                'email_verified_at' => now(),
            ]);
        }

        // 3. Seed 30 Organizers
        $organizers = [];
        
        // Organizer 1 (Fixed Demo Account)
        $org1Profile = $generateProfile('MALE');
        $organizers[] = User::create([
            'name' => 'Eko Prasetyo',
            'email' => 'organizer1@communityapp.com',
            'password' => $organizerPassword,
            'phone_number' => $org1Profile['phone_number'],
            'birth_date' => $org1Profile['birth_date'],
            'gender' => 'MALE',
            'bio' => 'Organizer berpengalaman di bidang IT dan teknologi digital.',
            'avatar_url' => 'https://api.dicebear.com/7.x/adventurer/svg?seed=organizer1',
            'role' => 'ORGANIZER',
            'is_blocked' => false,
            'is_trusted' => true, // Trusted (Approved)
            'email_verified_at' => now(),
        ]);

        // Organizers 2 to 30
        for ($i = 2; $i <= 30; $i++) {
            $profile = $generateProfile();
            
            // Trusted organizers: 1 to 10
            // Pending organizers: 11 to 20
            // Normal organizers: 21 to 30
            $isTrusted = ($i <= 10); 

            $organizers[] = User::create([
                'name' => $profile['name'],
                'email' => "organizer{$i}@communityapp.com",
                'password' => $organizerPassword,
                'phone_number' => $profile['phone_number'],
                'birth_date' => $profile['birth_date'],
                'gender' => $profile['gender'],
                'bio' => $profile['bio'] . ' Senang memfasilitasi event kreatif.',
                'avatar_url' => "https://api.dicebear.com/7.x/adventurer/svg?seed=organizer{$i}",
                'role' => 'ORGANIZER',
                'is_blocked' => false,
                'is_trusted' => $isTrusted,
                'email_verified_at' => now(),
            ]);
        }

        // Community names for organizer trusted applications (aligned with the 30 communities)
        $communityNames = [
            1 => 'Android Developers Hub',
            2 => 'Web Development Circle',
            3 => 'Cyber Security Society',
            4 => 'Solo Runners Club',
            5 => 'Basketball Community',
            6 => 'Badminton Society',
            7 => 'Indonesia Digital Artists',
            8 => 'UI/UX Jakarta Collective',
            9 => 'Creative Sketchers Club',
            10 => 'Bandung Indie Music',
            11 => 'Jakarta Acoustic Jam',
            12 => 'Solo Classical Symphony',
            13 => 'Indonesia Space Science Community',
            14 => 'Klub Debat Bahasa Inggris',
            15 => 'National Science Society',
            16 => 'Indonesian Startup Founders Hub',
            17 => 'Investor Saham Pemula',
            18 => 'Young Entrepreneurs Circle',
            19 => 'Esports Community',
            20 => 'Valorant Indonesia',
            21 => 'Mobile Legends Community',
            22 => 'Street Photography Solo',
            23 => 'Photography Club',
            24 => 'Cinematography Indonesia',
            25 => 'Green Earth Indonesia',
            26 => 'Zero Waste Jakarta',
            27 => 'Nature Conservation Club',
            28 => 'Yogyakarta Yoga & Mindfulness',
            29 => 'Klub Nutrisi & Hidup Sehat',
            30 => 'Mental Health Alliance'
        ];

        // Seed TrustedApplications for organizers based on role:
        // 10 Trusted: APPROVED status
        // 10 Pending: PENDING status
        // 10 Normal: 5 REJECTED status, 5 No Application
        foreach ($organizers as $index => $org) {
            $num = $index + 1;
            $communityName = $communityNames[$num] ?? 'Komunitas Keren ' . $num;
            
            if ($num <= 10) {
                // Trusted (Approved)
                TrustedApplication::create([
                    'user_id' => $org->id,
                    'community_name' => $communityName,
                    'reason' => 'Saya ingin membangun komunitas ' . $communityName . ' yang profesional untuk bertukar wawasan dan kolaborasi aktif.',
                    'experience' => 'Berpengalaman mengelola event komunitas offline dan online selama lebih dari 5 tahun.',
                    'status' => 'APPROVED',
                    'reviewed_by' => $superAdmin->id,
                    'admin_notes' => 'Disetujui. Pengalaman mumpuni dan visi komunitas sangat terarah.',
                    'applied_at' => Carbon::now()->subDays(30),
                    'reviewed_at' => Carbon::now()->subDays(29),
                ]);
            } elseif ($num <= 20) {
                // Pending application
                TrustedApplication::create([
                    'user_id' => $org->id,
                    'community_name' => $communityName,
                    'reason' => 'Saya ingin merintis komunitas ' . $communityName . ' sebagai wadah diskusi santai bagi para pehobi.',
                    'experience' => 'Menjadi anggota aktif di berbagai komunitas sejenis selama 2 tahun terakhir.',
                    'status' => 'PENDING',
                    'reviewed_by' => null,
                    'admin_notes' => null,
                    'applied_at' => Carbon::now()->subDays(5),
                    'reviewed_at' => null,
                ]);
            } elseif ($num <= 25) {
                // Normal Organizer with a Rejected Application
                TrustedApplication::create([
                    'user_id' => $org->id,
                    'community_name' => $communityName,
                    'reason' => 'Pengajuan komunitas ' . $communityName . ' agar diakui resmi oleh platform.',
                    'experience' => 'Belum memiliki pengalaman mengelola komunitas sebelumnya.',
                    'status' => 'REJECTED',
                    'reviewed_by' => $superAdmin->id,
                    'admin_notes' => 'Ditolak karena belum memiliki pengalaman manajerial komunitas yang cukup.',
                    'applied_at' => Carbon::now()->subDays(15),
                    'reviewed_at' => Carbon::now()->subDays(14),
                ]);
            }
            // Organizers 26 to 30 remain as normal organizers with NO application record.
        }

        // 4. Seed 164 Members (role = USER)
        
        // Member 1 (Fixed Demo Account)
        $mem1Profile = $generateProfile('MALE');
        User::create([
            'name' => 'Budi Santoso',
            'email' => 'user1@communityapp.com',
            'password' => $memberPassword,
            'phone_number' => $mem1Profile['phone_number'],
            'birth_date' => $mem1Profile['birth_date'],
            'gender' => 'MALE',
            'bio' => 'Pencinta teknologi dan olahraga. Senang berkontribusi di komunitas lokal.',
            'avatar_url' => 'https://api.dicebear.com/7.x/adventurer/svg?seed=user1',
            'role' => 'USER',
            'is_blocked' => false,
            'is_trusted' => false,
            'email_verified_at' => now(),
        ]);

        // Members 2 to 164
        for ($i = 2; $i <= 164; $i++) {
            $profile = $generateProfile();
            User::create([
                'name' => $profile['name'],
                'email' => "user{$i}@communityapp.com",
                'password' => $memberPassword,
                'phone_number' => $profile['phone_number'],
                'birth_date' => $profile['birth_date'],
                'gender' => $profile['gender'],
                'bio' => $profile['bio'],
                'avatar_url' => "https://api.dicebear.com/7.x/adventurer/svg?seed=user{$i}",
                'role' => 'USER',
                'is_blocked' => false,
                'is_trusted' => false,
                'email_verified_at' => now(),
            ]);
        }
    }
}
