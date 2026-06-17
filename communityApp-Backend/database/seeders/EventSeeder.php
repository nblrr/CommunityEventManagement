<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\Event;
use App\Models\Community;
use App\Models\Category;
use App\Models\User;
use App\Models\EventRegistration;
use App\Models\EventRating;

class EventSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        $communities = Community::all();
        $categories = Category::all();
        $allRegularUsers = User::where('role', 'USER')->get();

        if ($communities->isEmpty() || $categories->isEmpty()) {
            return;
        }

        $allRegistrations = [];
        $allRatings = [];

        foreach ($communities as $community) {
            // Get community members once
            $members = $community->members()->where('community_members.role', 'MEMBER')->get();
            if ($members->isEmpty()) {
                $members = $allRegularUsers;
            }

            // 1. Seed exactly 10 PAST events
            for ($i = 1; $i <= 10; $i++) {
                $category = $categories->random();
                $eventDate = now()->subDays(rand(2, 60))->format('Y-m-d');
                $eventTime = sprintf('%02d:00:00', rand(9, 21));

                // Determine participants first to set attendee_count when creating the event
                $registeredUsers = collect();
                if ($members->isNotEmpty()) {
                    $sampleSize = rand(3, min(10, $members->count()));
                    $registeredUsers = $members->random($sampleSize);
                }
                $attendeeCount = $registeredUsers->count();

                $event = Event::create([
                    'community_id' => $community->id,
                    'category_id' => $category->id,
                    'title' => "Past Event {$i} - " . $community->name,
                    'description' => "Ini adalah event yang telah selesai diselenggarakan oleh " . $community->name . " mengenai topik " . $category->name . ". Terima kasih kepada seluruh partisipan yang telah bergabung!",
                    'event_date' => $eventDate,
                    'event_time' => $eventTime,
                    'location' => 'Community Hub Hall ' . rand(1, 4),
                    'is_online' => false,
                    'max_attendees' => rand(30, 100),
                    'attendee_count' => $attendeeCount,
                    'cover_image_url' => 'https://images.unsplash.com/photo-1511192336575-5a79af67a629',
                    'status' => 'PAST',
                ]);

                // Queue registrations for bulk insertion
                foreach ($registeredUsers as $user) {
                    $allRegistrations[] = [
                        'user_id' => $user->id,
                        'event_id' => $event->id,
                        'status' => 'ATTENDED',
                        'registered_at' => now()->subDays(rand(1, 4)),
                        'attended_at' => now()->subDays(1),
                        'created_at' => now(),
                        'updated_at' => now(),
                    ];
                }

                // Queue ratings/reviews for bulk insertion
                if ($attendeeCount > 0) {
                    $reviewersCount = rand(max(1, (int)($attendeeCount * 0.3)), $attendeeCount);
                    $reviewers = $registeredUsers->random($reviewersCount);

                    foreach ($reviewers as $user) {
                        $allRatings[] = [
                            'user_id' => $user->id,
                            'event_id' => $event->id,
                            'rating' => rand(4, 5),
                            'comment' => fake()->randomElement([
                                'Sangat seru dan bermanfaat!',
                                'Materi yang disampaikan sangat jelas dan mudah dipahami.',
                                'Speaker-nya luar biasa dan berpengalaman.',
                                'Sangat direkomendasikan untuk diikuti di lain waktu!',
                                'Event terorganisir dengan sangat rapi, terima kasih panitia!',
                                'Banyak insight baru yang saya dapatkan dari diskusi ini.',
                                'Waktu pelaksanaan tepat waktu, sesi tanya jawab juga interaktif.',
                                'Tempat dan suasananya sangat mendukung, mantap!',
                                'Sangat menginspirasi! Ditunggu event selanjutnya.',
                                'Materi sangat relevan dengan kebutuhan industri saat ini.'
                            ]),
                            'created_at' => now(),
                            'updated_at' => now(),
                        ];
                    }
                }
            }

            // 2. Seed 2 to 4 UPCOMING events
            $upcomingCount = rand(2, 4);
            for ($i = 1; $i <= $upcomingCount; $i++) {
                $category = $categories->random();
                $eventDate = now()->addDays(rand(2, 30))->format('Y-m-d');
                $eventTime = sprintf('%02d:00:00', rand(9, 21));

                // Determine participants first to set attendee_count
                $registeredUsers = collect();
                if ($members->isNotEmpty()) {
                    $sampleSize = rand(3, min(10, $members->count()));
                    $registeredUsers = $members->random($sampleSize);
                }
                $attendeeCount = $registeredUsers->count();

                $event = Event::create([
                    'community_id' => $community->id,
                    'category_id' => $category->id,
                    'title' => "Upcoming Event {$i} - " . $community->name,
                    'description' => "Bergabunglah dengan kami di event mendatang untuk " . $community->name . ". Kita akan berkumpul dan membahas berbagai topik menarik tentang " . $category->name . ". Jangan lewatkan!",
                    'event_date' => $eventDate,
                    'event_time' => $eventTime,
                    'location' => rand(0, 1) ? 'Zoom Meeting' : 'Google Meet',
                    'is_online' => true,
                    'max_attendees' => rand(30, 100),
                    'attendee_count' => $attendeeCount,
                    'cover_image_url' => 'https://images.unsplash.com/photo-1531403009284-440f080d1e12',
                    'status' => 'UPCOMING',
                ]);

                // Queue registrations for bulk insertion
                foreach ($registeredUsers as $user) {
                    $allRegistrations[] = [
                        'user_id' => $user->id,
                        'event_id' => $event->id,
                        'status' => 'REGISTERED',
                        'registered_at' => now()->subDays(rand(1, 4)),
                        'attended_at' => null,
                        'created_at' => now(),
                        'updated_at' => now(),
                    ];
                }
            }
        }

        // Bulk insert all registrations in chunks of 500
        if (!empty($allRegistrations)) {
            foreach (array_chunk($allRegistrations, 500) as $chunk) {
                EventRegistration::insert($chunk);
            }
        }

        // Bulk insert all ratings in chunks of 500
        if (!empty($allRatings)) {
            foreach (array_chunk($allRatings, 500) as $chunk) {
                EventRating::insert($chunk);
            }
        }
    }
}
