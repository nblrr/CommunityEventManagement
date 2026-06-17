<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $passwordHash = Hash::make('password');
        $existingEmails = User::pluck('email')->flip()->toArray();
        $usersToInsert = [];

        // 1. Seed Super Admin
        $email = 'admin@community.com';
        $adminData = [
            'name' => 'Super Admin',
            'password' => $passwordHash,
            'phone_number' => '081234567890',
            'birth_date' => '1990-01-01',
            'gender' => 'MALE',
            'bio' => 'Platform Super Admin',
            'avatar_url' => 'https://api.dicebear.com/7.x/adventurer/svg?seed=admin',
            'role' => 'ADMIN',
            'is_blocked' => false,
            'is_trusted' => true,
            'email_verified_at' => now(),
            'created_at' => now(),
            'updated_at' => now(),
        ];
        if (isset($existingEmails[$email])) {
            User::where('email', $email)->update(array_diff_key($adminData, array_flip(['email', 'created_at'])));
        } else {
            $usersToInsert[] = array_merge(['email' => $email], $adminData);
        }

        // 2. Seed 25 Organizers (Non-trusted)
        for ($i = 1; $i <= 25; $i++) {
            $email = "organizer{$i}@community.com";
            $organizerData = [
                'name' => fake()->name(),
                'password' => $passwordHash,
                'phone_number' => fake()->phoneNumber(),
                'birth_date' => fake()->date('Y-m-d', '-20 years'),
                'gender' => fake()->randomElement(['MALE', 'FEMALE']),
                'bio' => fake()->sentence(),
                'avatar_url' => "https://api.dicebear.com/7.x/adventurer/svg?seed=organizer{$i}",
                'role' => 'ORGANIZER',
                'is_blocked' => false,
                'is_trusted' => false,
                'email_verified_at' => now(),
                'created_at' => now(),
                'updated_at' => now(),
            ];
            if (isset($existingEmails[$email])) {
                User::where('email', $email)->update(array_diff_key($organizerData, array_flip(['email', 'created_at'])));
            } else {
                $usersToInsert[] = array_merge(['email' => $email], $organizerData);
            }
        }

        // 3. Seed 10 Trusted Organizers
        for ($i = 1; $i <= 10; $i++) {
            $email = "trusted_organizer{$i}@community.com";
            $organizerData = [
                'name' => fake()->name() . " (Trusted)",
                'password' => $passwordHash,
                'phone_number' => fake()->phoneNumber(),
                'birth_date' => fake()->date('Y-m-d', '-20 years'),
                'gender' => fake()->randomElement(['MALE', 'FEMALE']),
                'bio' => fake()->sentence(),
                'avatar_url' => "https://api.dicebear.com/7.x/adventurer/svg?seed=trusted_organizer{$i}",
                'role' => 'ORGANIZER',
                'is_blocked' => false,
                'is_trusted' => true,
                'email_verified_at' => now(),
                'created_at' => now(),
                'updated_at' => now(),
            ];
            if (isset($existingEmails[$email])) {
                User::where('email', $email)->update(array_diff_key($organizerData, array_flip(['email', 'created_at'])));
            } else {
                $usersToInsert[] = array_merge(['email' => $email], $organizerData);
            }
        }

        // 4. Seed 100 Regular Users
        for ($i = 1; $i <= 100; $i++) {
            $email = "user{$i}@community.com";
            $userData = [
                'name' => fake()->name(),
                'password' => $passwordHash,
                'phone_number' => fake()->phoneNumber(),
                'birth_date' => fake()->date('Y-m-d', '-18 years'),
                'gender' => fake()->randomElement(['MALE', 'FEMALE']),
                'bio' => fake()->sentence(),
                'avatar_url' => "https://api.dicebear.com/7.x/adventurer/svg?seed=user{$i}",
                'role' => 'USER',
                'is_blocked' => false,
                'is_trusted' => false,
                'email_verified_at' => now(),
                'created_at' => now(),
                'updated_at' => now(),
            ];
            if (isset($existingEmails[$email])) {
                User::where('email', $email)->update(array_diff_key($userData, array_flip(['email', 'created_at'])));
            } else {
                $usersToInsert[] = array_merge(['email' => $email], $userData);
            }
        }

        if (!empty($usersToInsert)) {
            User::insert($usersToInsert);
        }
    }
}
