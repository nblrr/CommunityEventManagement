<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Category;

class CategorySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $categories = [
            ['name' => 'Technology', 'icon' => 'code'],
            ['name' => 'Sports', 'icon' => 'sports_soccer'],
            ['name' => 'Art & Design', 'icon' => 'palette'],
            ['name' => 'Music', 'icon' => 'music_note'],
            ['name' => 'Education & Science', 'icon' => 'school'],
            ['name' => 'Business & Finance', 'icon' => 'trending_up'],
            ['name' => 'Gaming', 'icon' => 'sports_esports'],
            ['name' => 'Photography', 'icon' => 'photo_camera'],
            ['name' => 'Environment', 'icon' => 'eco'],
            ['name' => 'Health & Wellness', 'icon' => 'favorite'],
        ];

        foreach ($categories as $category) {
            Category::updateOrCreate(['name' => $category['name']], $category);
        }
    }
}
