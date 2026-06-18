<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Community;
use App\Models\User;
use App\Models\Category;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class CommunitySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $organizers = User::where('role', 'ORGANIZER')->orderBy('id', 'asc')->get();
        $categories = Category::all()->keyBy('name');
        $allUsers = User::all(); // 200 users

        if ($organizers->count() < 30 || $categories->count() < 10) {
            return;
        }

        // 30 communities mapped to 10 categories (exactly 3 per category)
        $communitiesData = [
            // 1. Technology
            [
                'name' => 'Android Developers Hub',
                'description' => 'Wadah berkumpulnya para developer software dan praktisi Android di Surabaya untuk berbagi wawasan, Jetpack Compose, dan peluang karir.',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Web Development Circle',
                'description' => 'Komunitas belajar frontend dan backend development, HTML, CSS, JavaScript, Laravel, React, dan Next.js.',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Cyber Security Society',
                'description' => 'Komunitas pegiat keamanan siber, ethical hacking, penetration testing, dan pertahanan jaringan di Indonesia.',
                'category_name' => 'Technology',
            ],
            // 2. Sports
            [
                'name' => 'Solo Runners Club',
                'description' => 'Klub lari santai untuk warga Solo. Rutin melakukan olahraga lari bersama setiap akhir pekan demi menjaga kebugaran tubuh.',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Basketball Community',
                'description' => 'Komunitas pecinta bola basket. Mengadakan jadwal main bareng secara rutin di lapangan lokal.',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Badminton Society',
                'description' => 'Komunitas pecinta bulutangkis di Indonesia. Mengadakan jadwal main bareng secara rutin dengan sistem sewa lapangan bersama.',
                'category_name' => 'Sports',
            ],
            // 3. Art & Design
            [
                'name' => 'Indonesia Digital Artists',
                'description' => 'Tempat berkumpulnya ilustrator, desainer grafis, dan pembuat konsep visual digital Indonesia untuk berbagi karya.',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'UI/UX Jakarta Collective',
                'description' => 'Wadah diskusi dan bedah portofolio bagi peminat bidang User Interface dan User Experience di sekitar Jabodetabek.',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Creative Sketchers Club',
                'description' => 'Komunitas menggambar sketsa manual menggunakan pensil, cat air, dan media tradisional lainnya.',
                'category_name' => 'Art & Design',
            ],
            // 4. Music
            [
                'name' => 'Bandung Indie Music',
                'description' => 'Menghubungkan grup musik independen lokal untuk berbagi panggung, membagi tips rekaman mandiri, dan berjejaring.',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Jakarta Acoustic Jam',
                'description' => 'Sesi jamming santai bagi para penyanyi dan pemain instrumen akustik di kafe-kafe Jakarta.',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Solo Classical Symphony',
                'description' => 'Wadah apresiasi dan latihan musik klasik bagi pemain biola, cello, piano, dan alat musik orkestra.',
                'category_name' => 'Music',
            ],
            // 5. Education & Science
            [
                'name' => 'Indonesia Space Science Community',
                'description' => 'Komunitas astronom amatir dan peminat sains antariksa yang gemar mengamati bintang dan mendiskusikan kosmologi.',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'Klub Debat Bahasa Inggris',
                'description' => 'Meningkatkan kemampuan public speaking dan penalaran logis anggota lewat latihan debat terstruktur dalam bahasa Inggris.',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'National Science Society',
                'description' => 'Wadah diskusi sains populer, fisika, kimia, biologi, dan matematika untuk pelajar dan umum.',
                'category_name' => 'Education & Science',
            ],
            // 6. Business & Finance
            [
                'name' => 'Indonesian Startup Founders Hub',
                'description' => 'Tempat berjejaring, bertukar ide bisnis, berlatih presentasi pitch deck, dan mencari co-founder bagi pelaku startup.',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Investor Saham Pemula',
                'description' => 'Wadah diskusi mengenai analisis fundamental dan teknikal saham Bursa Efek Indonesia bagi investor pemula.',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Young Entrepreneurs Circle',
                'description' => 'Komunitas wirausaha muda untuk berbagi pengalaman mengelola bisnis UMKM dan strategi pemasaran digital.',
                'category_name' => 'Business & Finance',
            ],
            // 7. Gaming
            [
                'name' => 'Esports Community',
                'description' => 'Komunitas gamer yang berfokus pada pengembangan bakat esports, turnamen kompetitif, dan strategi tim.',
                'category_name' => 'Gaming',
            ],
            [
                'name' => 'Valorant Indonesia',
                'description' => 'Wadah berkumpulnya pemain game Valorant di Indonesia untuk main bareng (mabar) dan turnamen persahabatan.',
                'category_name' => 'Gaming',
            ],
            [
                'name' => 'Mobile Legends Community',
                'description' => 'Komunitas pemain Mobile Legends: Bang Bang untuk mabar, push rank bersama, dan diskusi META terbaru.',
                'category_name' => 'Gaming',
            ],
            // 8. Photography
            [
                'name' => 'Street Photography Solo',
                'description' => 'Melakukan kegiatan berburu foto jalanan (photowalk) bersama di kawasan bersejarah kota Solo dan sekitarnya.',
                'category_name' => 'Photography',
            ],
            [
                'name' => 'Photography Club',
                'description' => 'Komunitas pecinta seni fotografi, belajar teknik pencahayaan, komposisi, dan editing foto menggunakan kamera DSLR/mirrorless.',
                'category_name' => 'Photography',
            ],
            [
                'name' => 'Cinematography Indonesia',
                'description' => 'Wadah bagi pembuat film pendek, videografer, dan pembuat konten visual untuk belajar teknik videografi dan editing.',
                'category_name' => 'Photography',
            ],
            // 9. Environment
            [
                'name' => 'Green Earth Indonesia',
                'description' => 'Gerakan sukarelawan untuk penanaman pohon, pembersihan sampah plastik, dan kampanye kelestarian lingkungan hidup.',
                'category_name' => 'Environment',
            ],
            [
                'name' => 'Zero Waste Jakarta',
                'description' => 'Komunitas gaya hidup minim sampah untuk mengurangi penggunaan plastik sekali pakai dan mempraktikkan daur ulang.',
                'category_name' => 'Environment',
            ],
            [
                'name' => 'Nature Conservation Club',
                'description' => 'Komunitas pecinta alam yang berfokus pada pelestarian hutan, pegunungan, dan satwa liar di Indonesia.',
                'category_name' => 'Environment',
            ],
            // 10. Health & Wellness
            [
                'name' => 'Yogyakarta Yoga & Mindfulness',
                'description' => 'Mengadakan sesi yoga aliran lembut, meditasi kesadaran penuh, dan diskusi gaya hidup sehat di Yogyakarta.',
                'category_name' => 'Health & Wellness',
            ],
            [
                'name' => 'Klub Nutrisi & Hidup Sehat',
                'description' => 'Membagi resep makanan sehat, merancang menu diet seimbang, serta membagikan tips hidup sehat.',
                'category_name' => 'Health & Wellness',
            ],
            [
                'name' => 'Mental Health Alliance',
                'description' => 'Wadah dukungan sebaya untuk berdiskusi seputar kesehatan mental, mengatasi kecemasan, dan self-care.',
                'category_name' => 'Health & Wellness',
            ],
        ];

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

        // Size Distribution Configuration
        // 5 Large (index 0 to 4): 80-120 member
        // 10 Medium (index 5 to 14): 40-80 member
        // 15 Small (index 15 to 29): 15-40 member
        $sizeTargets = [
            // 5 Large
            85, 95, 100, 110, 120,
            // 10 Medium
            45, 48, 52, 55, 60, 65, 70, 72, 75, 80,
            // 15 Small
            15, 17, 18, 20, 22, 24, 25, 27, 29, 30, 32, 34, 36, 38, 40
        ];

        $createdCommunities = [];

        // 1. Create the 30 communities
        foreach ($communitiesData as $index => $data) {
            $organizer = $organizers[$index];
            $category = $categories[$data['category_name']];
            $coverImage = $categoryImageMap[$data['category_name']] ?? 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4';

            $community = Community::create([
                'name' => $data['name'],
                'description' => $data['description'],
                'organizer_id' => $organizer->id,
                'category_id' => $category->id,
                'status' => 'ACTIVE',
                'cover_image_url' => $coverImage,
                'member_count' => 0,
            ]);

            $createdCommunities[] = [
                'model' => $community,
                'target_size' => $sizeTargets[$index]
            ];
        }

        // Initialize user memberships maps
        $userMemberships = [];
        foreach ($allUsers as $user) {
            $userMemberships[$user->id] = [];
        }

        // Initialize community memberships maps
        $communityMemberships = [];
        foreach ($createdCommunities as $item) {
            $community = $item['model'];
            $communityMemberships[$community->id] = [];
            
            // Add the organizer as the OWNER (joins their own community)
            $orgId = $community->organizer_id;
            $communityMemberships[$community->id][$orgId] = 'OWNER';
            $userMemberships[$orgId][] = $community->id;
        }

        // Step A: Ensure every user belongs to at least 1 community
        // (For non-organizers, we register them to a random community first)
        foreach ($allUsers as $user) {
            if ($user->role === 'ORGANIZER') {
                continue; // Organizers are already owners of their respective communities
            }

            $userId = $user->id;
            // Pick a random community
            $randomItem = $createdCommunities[array_rand($createdCommunities)];
            $commId = $randomItem['model']->id;

            $communityMemberships[$commId][$userId] = 'MEMBER';
            $userMemberships[$userId][] = $commId;
        }

        // Step B: Fill community memberships up to their target size
        foreach ($createdCommunities as $item) {
            $community = $item['model'];
            $commId = $community->id;
            $target = $item['target_size'];
            $currentCount = count($communityMemberships[$commId]);

            if ($currentCount < $target) {
                $needed = $target - $currentCount;

                // Find users not yet in this community
                $eligibleUsers = $allUsers->filter(function($user) use ($commId, $userMemberships) {
                    return !in_array($commId, $userMemberships[$user->id]);
                })->shuffle();

                foreach ($eligibleUsers as $user) {
                    if ($needed <= 0) break;

                    $userId = $user->id;
                    $communityMemberships[$commId][$userId] = 'MEMBER';
                    $userMemberships[$userId][] = $commId;
                    $needed--;
                }
            }
        }

        // 2. Insert into database in bulk
        $pivotRecords = [];
        foreach ($communityMemberships as $commId => $members) {
            foreach ($members as $userId => $role) {
                $pivotRecords[] = [
                    'user_id' => $userId,
                    'community_id' => $commId,
                    'role' => $role,
                    'joined_at' => Carbon::now()->subDays(rand(10, 60)),
                    'created_at' => now(),
                    'updated_at' => now(),
                ];
            }
        }

        // Bulk insert community members
        DB::table('community_members')->insert($pivotRecords);

        // 3. Update member_count on communities to maintain cache integrity
        foreach ($createdCommunities as $item) {
            $community = $item['model'];
            $count = count($communityMemberships[$community->id]);
            $community->update(['member_count' => $count]);
        }
    }
}
