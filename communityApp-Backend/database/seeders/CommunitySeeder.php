<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Community;
use App\Models\User;
use App\Models\Category;
use App\Models\CommunityMember;
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
        $allUsers = User::all(); // 100 users

        if ($organizers->count() < 20 || $categories->count() < 10) {
            return;
        }

        // 20 communities mapped to 10 categories (exactly 2 per category)
        $communitiesData = [
            // 1. Technology
            [
                'name' => 'Surabaya Developer Community',
                'description' => 'Wadah berkumpulnya para developer software dan praktisi teknologi di Surabaya untuk berbagi wawasan dan peluang karir.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'AI Research Indonesia',
                'description' => 'Komunitas riset kecerdasan buatan, machine learning, deep learning, dan LLM untuk memajukan inovasi teknologi di Indonesia.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1677442136019-21780efad99a',
                'category_name' => 'Technology',
            ],
            // 2. Sports
            [
                'name' => 'Bandung Running Club',
                'description' => 'Klub lari santai untuk warga Bandung. Rutin melakukan olahraga lari bersama setiap akhir pekan demi menjaga kebugaran tubuh.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Jakarta Badminton Lovers',
                'description' => 'Komunitas pecinta bulutangkis di area Jakarta. Mengadakan jadwal main bareng secara rutin dengan sistem sewa lapangan bersama.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea',
                'category_name' => 'Sports',
            ],
            // 3. Art & Design
            [
                'name' => 'Indonesia Digital Artists',
                'description' => 'Tempat berkumpulnya ilustrator, desainer grafis, dan pembuat konsep visual digital Indonesia untuk berbagi karya dan tips teknik menggambar.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'UI/UX Jakarta Collective',
                'description' => 'Wadah diskusi dan bedah portofolio bagi peminat bidang User Interface dan User Experience di sekitar Jabodetabek.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1561070791-26c113006238',
                'category_name' => 'Art & Design',
            ],
            // 4. Music
            [
                'name' => 'Bandung Indie Music',
                'description' => 'Menghubungkan grup musik independen lokal untuk berbagi panggung, membagi tips rekaman mandiri, dan berjejaring antar musisi.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Jakarta Acoustic Jam',
                'description' => 'Sesi jamming santai bagi para penyanyi dan pemain instrumen akustik di kafe-kafe Jakarta.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1511192336575-5a79af67a629',
                'category_name' => 'Music',
            ],
            // 5. Education & Science
            [
                'name' => 'Indonesia Space Science Community',
                'description' => 'Komunitas astronom amatir dan peminat sains antariksa yang gemar mengamati bintang dan mendiskusikan astronomi.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1451187580459-43490279c0fa',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'Klub Debat Bahasa Inggris',
                'description' => 'Meningkatkan kemampuan public speaking dan penalaran logis anggota lewat latihan debat terstruktur dalam bahasa Inggris.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1524178232363-1fb2b075b655',
                'category_name' => 'Education & Science',
            ],
            // 6. Business & Finance
            [
                'name' => 'Indonesian Startup Founders Hub',
                'description' => 'Tempat berjejaring, bertukar ide bisnis, berlatih presentasi pitch deck, dan mencari co-founder bagi para pelaku startup.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1515187029135-18ee286d815b',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Investor Saham Pemula',
                'description' => 'Wadah diskusi mengenai analisis fundamental dan teknikal saham Bursa Efek Indonesia bagi investor pemula.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f',
                'category_name' => 'Business & Finance',
            ],
            // 7. Gaming
            [
                'name' => 'Mobile Legends Indonesia Association',
                'description' => 'Mengumpulkan para pemain MLBB di Indonesia untuk bermain bersama, mengadakan turnamen komunitas, dan berbagi strategi permainan.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1542751371-adc38448a05e',
                'category_name' => 'Gaming',
            ],
            [
                'name' => 'Gamer PC Jakarta',
                'description' => 'Klub pehobi rakit PC dan penikmat game PC di Jakarta untuk bertukar informasi seputar spesifikasi perangkat keras dan game terbaru.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1538481199705-c710c4e965fc',
                'category_name' => 'Gaming',
            ],
            // 8. Photography
            [
                'name' => 'Street Photography Jakarta',
                'description' => 'Melakukan kegiatan berburu foto jalanan (photowalk) bersama di kawasan-kawasan bersejarah Jakarta seperti Kota Tua dan Melawai.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1516035069371-29a1b244cc32',
                'category_name' => 'Photography',
            ],
            [
                'name' => 'Mobile Photography Indonesia',
                'description' => 'Komunitas yang berfokus pada teknik pengambilan dan penyuntingan foto berkualitas menggunakan kamera ponsel.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c',
                'category_name' => 'Photography',
            ],
            // 9. Environment
            [
                'name' => 'Green Earth Indonesia',
                'description' => 'Gerakan sukarelawan untuk penanaman pohon, pembersihan sampah plastik, dan kampanye kelestarian lingkungan hidup.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b',
                'category_name' => 'Environment',
            ],
            [
                'name' => 'Zero Waste Jakarta',
                'description' => 'Komunitas gaya hidup minim sampah untuk mengurangi penggunaan plastik sekali pakai dan mempraktikkan pengelolaan limbah rumah tangga.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1531403009284-440f080d1e12',
                'category_name' => 'Environment',
            ],
            // 10. Health & Wellness
            [
                'name' => 'Yogyakarta Yoga & Mindfulness',
                'description' => 'Mengadakan sesi yoga aliran lembut, meditasi kesadaran penuh, dan diskusi gaya hidup sehat di kota Yogyakarta.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b',
                'category_name' => 'Health & Wellness',
            ],
            [
                'name' => 'Klub Nutrisi & Hidup Sehat',
                'description' => 'Membagi resep makanan sehat, merancang menu diet seimbang, serta membagikan tips olahraga kardio sederhana untuk pemula.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4',
                'category_name' => 'Health & Wellness',
            ],
        ];

        $createdCommunities = [];

        // First, create the 20 communities
        foreach ($communitiesData as $index => $data) {
            $organizer = $organizers[$index];
            $category = $categories[$data['category_name']];

            $community = Community::create([
                'name' => $data['name'],
                'description' => $data['description'],
                'organizer_id' => $organizer->id,
                'category_id' => $category->id,
                'status' => 'ACTIVE',
                'cover_image_url' => $data['cover_image_url'],
                'member_count' => 0,
            ]);

            $createdCommunities[] = $community;
        }

        // Initialize user memberships arrays
        // Maps user_id => array of community_ids
        $userMemberships = [];
        foreach ($allUsers as $user) {
            $userMemberships[$user->id] = [];
        }

        // Initialize community memberships arrays
        // Maps community_id => array of user_ids
        $communityMemberships = [];
        foreach ($createdCommunities as $community) {
            $communityMemberships[$community->id] = [];
            
            // Add the organizer as the OWNER (joins their own community)
            $orgId = $community->organizer_id;
            $communityMemberships[$community->id][$orgId] = 'OWNER';
            $userMemberships[$orgId][] = $community->id;
        }

        // Distribute memberships
        // Rule 1: Every user belongs to 1-5 communities
        // Rule 2: Every community has 10-40 members
        
        // Step A: Each user registers to at least 1 community (up to 3)
        foreach ($allUsers as $user) {
            $userId = $user->id;
            
            // Determine how many communities to join: 1 to 3
            // If they are an organizer, they already have 1.
            $currentCount = count($userMemberships[$userId]);
            $targetCommunitiesCount = rand(1, 3);
            
            if ($currentCount >= $targetCommunitiesCount) {
                continue;
            }

            $needed = $targetCommunitiesCount - $currentCount;
            // Get available communities where they are not already joined
            $availableCommunityIds = collect($createdCommunities)
                ->pluck('id')
                ->diff($userMemberships[$userId])
                ->shuffle();

            foreach ($availableCommunityIds as $commId) {
                if ($needed <= 0) break;

                // Check if community has space (< 40 members)
                if (count($communityMemberships[$commId]) < 40) {
                    $communityMemberships[$commId][$userId] = 'MEMBER';
                    $userMemberships[$userId][] = $commId;
                    $needed--;
                }
            }
        }

        // Step B: Ensure every community has at least 10 members
        foreach ($createdCommunities as $community) {
            $commId = $community->id;
            $currentMembersCount = count($communityMemberships[$commId]);

            if ($currentMembersCount < 10) {
                $needed = 10 - $currentMembersCount;

                // Find users that are not in this community AND have joined less than 5 communities
                $eligibleUsers = $allUsers->filter(function($user) use ($commId, $userMemberships) {
                    $userId = $user->id;
                    return !in_array($commId, $userMemberships[$userId]) && count($userMemberships[$userId]) < 5;
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

        // Step C: If any user still has 0 communities (shouldn't happen, but let's be safe), add 1
        foreach ($allUsers as $user) {
            $userId = $user->id;
            if (empty($userMemberships[$userId])) {
                // Find a community with less than 40 members
                $comm = collect($createdCommunities)->first(function($c) use ($communityMemberships) {
                    return count($communityMemberships[$c->id]) < 40;
                });
                if ($comm) {
                    $commId = $comm->id;
                    $communityMemberships[$commId][$userId] = 'MEMBER';
                    $userMemberships[$userId][] = $commId;
                }
            }
        }

        // Insert into database in bulk
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

        // Update member_count on communities
        foreach ($createdCommunities as $community) {
            $count = count($communityMemberships[$community->id]);
            $community->update(['member_count' => $count]);
        }
    }
}
