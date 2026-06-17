<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Community;
use App\Models\User;
use App\Models\Category;

class CommunitySeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $organizers = User::where('role', 'ORGANIZER')->get();
        $categories = Category::all();
        $regularUsers = User::where('role', 'USER')->get();

        if ($organizers->isEmpty() || $categories->isEmpty()) {
            return;
        }

        $communitiesData = [
            [
                'name' => 'Laravel Devs Indonesia',
                'description' => 'A community for Laravel PHP developers in Indonesia to share knowledge, jobs, and tips.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'UI/UX Design ID',
                'description' => 'Discussions, reviews, and design portfolio reviews for UI/UX enthusiasts in Indonesia.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1561070791-26c113006238',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Indo Runner Community',
                'description' => 'Run together, stay healthy! Weekly running meets in major cities.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1476480862126-209bfaa8edc8',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Acoustic Jam Session',
                'description' => 'For musicians and music lovers who enjoy jamming out with acoustic instruments.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1511192336575-5a79af67a629',
                'category_name' => 'Music',
            ],
            [
                'name' => 'AI & Machine Learning Club',
                'description' => 'Exploring the latest in AI, Deep Learning, LLMs, and neural networks.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1677442136019-21780efad99a',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Python Indonesia',
                'description' => 'Indonesian community of Python programmers. Sharing scripts, web dev, and data science.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Flutter Devs Jakarta',
                'description' => 'Meetups and discussions for Google Flutter mobile developers based in Jakarta.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1512941937669-90a1b58e7e9c',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Cyber Security Forum',
                'description' => 'Focusing on ethical hacking, pen-testing, defense systems, and network security.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1550751827-4bd374c3f58b',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Web Development Bootcamp',
                'description' => 'Learn HTML, CSS, JS, React, and Laravel from scratch with peer support.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1531403009284-440f080d1e12',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'Android Kotlin Club',
                'description' => 'Native Android developer group focused on Kotlin, Jetpack Compose, and modern architecture.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1607252631355-89dddb30e7bd',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Jakarta Football Meetup',
                'description' => 'Playing football and futsal regularly every week in central Jakarta.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1508098682722-e99c43a406b2',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Badminton Fun Club',
                'description' => 'Friendly badminton games for all skill levels. Courts booked every weekend.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Cycling Tour Indonesia',
                'description' => 'Road bike and mountain bike tours around beautiful scenic tracks in Indonesia.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1485965120184-e220f721d03e',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Yoga and Mindfulness',
                'description' => 'Gentle flow yoga, meditation, and healthy lifestyle community.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1544367567-0f2fcb009e0b',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Photography and Cinematography',
                'description' => 'For visual storytellers. Photowalks, camera gear discussions, and editing workshops.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1452780212940-6f5c0d14d848',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Digital Painting Society',
                'description' => 'Concept art, illustrations, character design, and digital coloring tips.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Sketching and Doodling',
                'description' => 'Pen and paper enthusiasts. Monthly offline sketching meetups in museums/parks.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1513364776144-60967b0f800f',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Indie Rock Band Union',
                'description' => 'Connecting independent rock bands, scheduling joint gigs, and sharing recording setups.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Classical Piano Lovers',
                'description' => 'Appreciating Chopin, Bach, and Mozart. Recitals and piano practice sharing.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1520523839897-bd0b52f945a0',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Jazz & Blues Society',
                'description' => 'Late night jazz jam sessions and appreciation of classic blues music.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Startup Founders Hub',
                'description' => 'Pitch deck practice, fundraising strategies, co-founder dating, and networking.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1515187029135-18ee286d815b',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Stock Market & Trading',
                'description' => 'Fundamental and technical analysis discussions for IDX and global stocks.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Crypto & Blockchain Indo',
                'description' => 'DeFi, Web3, Smart Contracts, and crypto investment strategies.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1621761191319-c6fb62004040',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Astronomers Community',
                'description' => 'Stargazing, astrophotography, telescopes, and discussing space science.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1451187580459-43490279c0fa',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'English Debate Club',
                'description' => 'Enhance public speaking and logical reasoning through structured debate practice.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1524178232363-1fb2b075b655',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'DevOps & Cloud Indonesia',
                'description' => 'A community for DevOps engineers and Cloud professionals to discuss Kubernetes, Docker, AWS, GCP, and CI/CD pipelines.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1618401471353-b98aedd07871',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Mobile Gaming Association',
                'description' => 'Gathering mobile gamers and e-sports enthusiasts for regular tournaments and discussions.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1542751371-adc38448a05e',
                'category_name' => 'Technology',
            ],
            [
                'name' => 'Basketball Fellowship',
                'description' => 'Fun basketball matches, training tips, and regular court play every Wednesday and Saturday.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1546519638-68e109498ffc',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Tennis Club Indonesia',
                'description' => 'For tennis enthusiasts. Connect with players of your skill level and book court sessions.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1595435934249-5df7ed86e1c0',
                'category_name' => 'Sports',
            ],
            [
                'name' => 'Creative Writing Workshop',
                'description' => 'Discuss storytelling techniques, character development, and get reviews on your poetry or novel drafts.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1455390582262-044cdead277a',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Pottery & Sculpting Club',
                'description' => 'Hands-on pottery workshops, clay sculpting tutorials, and ceramic art exhibitions.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1565192647048-f997ded87ab7',
                'category_name' => 'Art & Design',
            ],
            [
                'name' => 'Vocal Training & Choir',
                'description' => 'Improve your vocal range, learn harmony, and sing with a passionate community choir.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1516280440614-37939bbacd6a',
                'category_name' => 'Music',
            ],
            [
                'name' => 'Data Science Academy',
                'description' => 'Learn data analysis, machine learning models, visualization, and SQL through peer projects.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1551288049-bebda4e38f71',
                'category_name' => 'Education & Science',
            ],
            [
                'name' => 'Real Estate Investing',
                'description' => 'Discussions on property investment, rental yields, flipping houses, and mortgage options.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1560518883-ce09059eeffa',
                'category_name' => 'Business & Finance',
            ],
            [
                'name' => 'Personal Finance Indo',
                'description' => 'Learn budgeting, retirement planning, insurance, and compound interest to achieve financial freedom.',
                'cover_image_url' => 'https://images.unsplash.com/photo-1559526324-4b87b5e36e44',
                'category_name' => 'Business & Finance',
            ]
        ];

        foreach ($organizers as $index => $organizer) {
            $data = $communitiesData[$index] ?? null;

            if (!$data) {
                // Fallback in case we have more organizers than predefined communities
                $category = $categories->random();
                $data = [
                    'name' => fake()->unique()->company() . ' Club',
                    'description' => fake()->paragraph(),
                    'cover_image_url' => 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4',
                    'category_name' => $category->name,
                ];
            }

            $category = Category::where('name', $data['category_name'])->first() ?: $categories->random();

            $community = Community::updateOrCreate(
                ['name' => $data['name']],
                [
                    'description' => $data['description'],
                    'organizer_id' => $organizer->id,
                    'category_id' => $category->id,
                    'status' => 'ACTIVE',
                    'cover_image_url' => $data['cover_image_url'],
                    'member_count' => 0,
                ]
            );

            // Assign organizer as OWNER in community_members pivot table
            $community->members()->syncWithoutDetaching([
                $organizer->id => ['role' => 'OWNER', 'joined_at' => now()]
            ]);

            // Assign some random members in bulk
            if ($regularUsers->isNotEmpty()) {
                $joinedUsers = $regularUsers->random(rand(5, min(15, $regularUsers->count())));
                $syncData = [];
                foreach ($joinedUsers as $user) {
                    $syncData[$user->id] = [
                        'role' => 'MEMBER',
                        'joined_at' => now()->subDays(rand(1, 30))
                    ];
                }
                $community->members()->syncWithoutDetaching($syncData);
            }

            // Sync total members count
            $community->update(['member_count' => $community->members()->count()]);
        }
    }
}
