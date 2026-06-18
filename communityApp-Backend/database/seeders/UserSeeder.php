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

        // 1. Seed Admin
        $admin = User::create([
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

        // 2. Seed 20 Organizers
        $organizers = [];
        
        // Organizer 1 (Fixed)
        $org1Profile = $generateProfile('MALE');
        $organizers[] = User::create([
            'name' => 'Eko Prasetyo', // Clean Indonesian name
            'email' => 'organizer1@communityapp.com',
            'password' => $organizerPassword,
            'phone_number' => $org1Profile['phone_number'],
            'birth_date' => $org1Profile['birth_date'],
            'gender' => 'MALE',
            'bio' => 'Organizer berpengalaman di bidang IT dan teknologi digital.',
            'avatar_url' => 'https://api.dicebear.com/7.x/adventurer/svg?seed=organizer1',
            'role' => 'ORGANIZER',
            'is_blocked' => false,
            'is_trusted' => true, // Trusted
            'email_verified_at' => now(),
        ]);

        // Organizers 2 to 20
        for ($i = 2; $i <= 20; $i++) {
            $profile = $generateProfile();
            
            // 40% Trusted, 60% Pending. Out of 20: 8 Trusted, 12 Pending.
            // Organizer 1 to 8 are Trusted (is_trusted = true)
            // Organizer 9 to 20 are Pending (is_trusted = false)
            $isTrusted = ($i <= 8); 

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

        // Seed TrustedApplications for each organizer
        foreach ($organizers as $index => $org) {
            $num = $index + 1;
            $isTrusted = ($num <= 8);
            
            $communityNames = [
                1 => 'Surabaya Developer Community',
                2 => 'AI Research Indonesia',
                3 => 'Bandung Running Club',
                4 => 'Jakarta Badminton Lovers',
                5 => 'Indonesia Digital Artists',
                6 => 'UI/UX Jakarta Collective',
                7 => 'Bandung Indie Music',
                8 => 'Jakarta Acoustic Jam',
                9 => 'Indonesia Space Science Community',
                10 => 'Klub Debat Bahasa Inggris',
                11 => 'Indonesian Startup Founders Hub',
                12 => 'Investor Saham Pemula',
                13 => 'Mobile Legends Indonesia Association',
                14 => 'Gamer PC Jakarta',
                15 => 'Street Photography Jakarta',
                16 => 'Mobile Photography Indonesia',
                17 => 'Green Earth Indonesia',
                18 => 'Zero Waste Jakarta',
                19 => 'Yogyakarta Yoga & Mindfulness',
                20 => 'Klub Nutrisi & Hidup Sehat'
            ];

            $communityName = $communityNames[$num] ?? 'Komunitas Keren ' . $num;

            if ($isTrusted) {
                TrustedApplication::create([
                    'user_id' => $org->id,
                    'community_name' => $communityName,
                    'reason' => 'Saya ingin membangun komunitas ' . $communityName . ' yang profesional untuk bertukar wawasan dan kolaborasi aktif.',
                    'experience' => 'Berpengalaman mengelola event komunitas offline dan online selama lebih dari 5 tahun.',
                    'status' => 'APPROVED',
                    'reviewed_by' => $admin->id,
                    'admin_notes' => 'Disetujui. Pengalaman mumpuni dan visi komunitas sangat terarah.',
                    'applied_at' => Carbon::now()->subDays(30),
                    'reviewed_at' => Carbon::now()->subDays(29),
                ]);
            } else {
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
            }
        }

        // 3. Seed 79 Members
        
        // Member 1 (Fixed)
        $mem1Profile = $generateProfile('MALE');
        User::create([
            'name' => 'Budi Santoso', // Clean Indonesian name
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

        // Members 2 to 79
        for ($i = 2; $i <= 79; $i++) {
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
