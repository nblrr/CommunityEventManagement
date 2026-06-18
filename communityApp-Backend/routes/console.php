<?php

use Illuminate\Foundation\Inspiring;
use Illuminate\Support\Facades\Artisan;
use App\Models\Category;
use App\Models\User;
use App\Models\Community;
use App\Models\Event;
use App\Models\EventRegistration;
use App\Models\EventRating;
use App\Models\ForumMessage;
use App\Models\Notification;
use App\Models\TrustedApplication;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

Artisan::command('inspire', function () {
    $this->comment(Inspiring::quote());
})->purpose('Display an inspiring quote');

Artisan::command('db:validate', function () {
    $this->info("==================================================");
    $this->info("POST-SEED DATABASE VALIDATION");
    $this->info("==================================================");

    $errors = [];
    $passed = 0;

    // 1. Categories Validation
    $categories = Category::all();
    $categoryCount = $categories->count();
    $expectedCategories = [
        'Technology', 'Sports', 'Art & Design', 'Music', 'Education & Science',
        'Business & Finance', 'Gaming', 'Photography', 'Environment', 'Health & Wellness'
    ];
    $categoryNames = $categories->pluck('name')->toArray();
    $missingCategories = array_diff($expectedCategories, $categoryNames);

    if ($categoryCount === 10 && empty($missingCategories)) {
        $this->line("✓ Categories Count & Names: Valid (10 categories, no missing)");
        $passed++;
    } else {
        $errors[] = "Categories count is {$categoryCount} (Expected: 10). Missing: " . implode(', ', $missingCategories);
    }

    // 2. Users Validation
    $totalUsers = User::count();
    $superAdminCount = User::where('role', 'SUPER_ADMIN')->count();
    $adminCount = User::where('role', 'ADMIN')->count();
    $organizerCount = User::where('role', 'ORGANIZER')->count();
    $memberCount = User::where('role', 'USER')->count();

    if ($totalUsers === 200) {
        $this->line("✓ Total Users Count: Valid (200 users)");
        $passed++;
    } else {
        $errors[] = "Total users count is {$totalUsers} (Expected: 200)";
    }

    if ($superAdminCount === 1) {
        $this->line("✓ Super Admin Count: Valid (1 super admin)");
        $passed++;
    } else {
        $errors[] = "Super Admin count is {$superAdminCount} (Expected: 1)";
    }

    if ($adminCount === 5) {
        $this->line("✓ Admin Count: Valid (5 admins)");
        $passed++;
    } else {
        $errors[] = "Admin count is {$adminCount} (Expected: 5)";
    }

    if ($organizerCount === 30) {
        $this->line("✓ Organizer Count: Valid (30 organizers)");
        $passed++;
    } else {
        $errors[] = "Organizer count is {$organizerCount} (Expected: 30)";
    }

    if ($memberCount === 164) {
        $this->line("✓ Member Count: Valid (164 members)");
        $passed++;
    } else {
        $errors[] = "Member count is {$memberCount} (Expected: 164)";
    }

    // Fixed Accounts Check
    $fixedAdmin = User::where('email', 'admin@communityapp.com')->first();
    $fixedOrganizer = User::where('email', 'organizer1@communityapp.com')->first();
    $fixedMember = User::where('email', 'user1@communityapp.com')->first();

    if ($fixedAdmin && $fixedAdmin->role === 'SUPER_ADMIN') {
        $this->line("✓ Demo Admin Account: Valid (admin@communityapp.com)");
        $passed++;
    } else {
        $errors[] = "Demo Admin account is missing or has incorrect role (Expected: SUPER_ADMIN)";
    }

    if ($fixedOrganizer && $fixedOrganizer->role === 'ORGANIZER') {
        $this->line("✓ Demo Organizer Account: Valid (organizer1@communityapp.com)");
        $passed++;
    } else {
        $errors[] = "Demo Organizer account is missing or has incorrect role";
    }

    if ($fixedMember && $fixedMember->role === 'USER') {
        $this->line("✓ Demo Member Account: Valid (user1@communityapp.com)");
        $passed++;
    } else {
        $errors[] = "Demo Member account is missing or has incorrect role";
    }

    // Trusted Organizer Distribution (10 Trusted, 10 Pending, 10 Normal)
    $trustedOrganizers = User::where('role', 'ORGANIZER')->where('is_trusted', true)->count();
    $pendingOrganizers = User::where('role', 'ORGANIZER')->where('is_trusted', false)->count();

    $trustedAppsApproved = TrustedApplication::where('status', 'APPROVED')->count();
    $trustedAppsPending = TrustedApplication::where('status', 'PENDING')->count();
    $trustedAppsRejected = TrustedApplication::where('status', 'REJECTED')->count();

    if ($trustedOrganizers === 10 && $pendingOrganizers === 20) {
        $this->line("✓ Organizer Trusted Distribution: Valid (10 Trusted, 20 Pending/Normal)");
        $passed++;
    } else {
        $errors[] = "Organizer trusted distribution is invalid. Trusted: {$trustedOrganizers}, Pending/Normal: {$pendingOrganizers} (Expected: 10 and 20)";
    }

    if ($trustedAppsApproved === 10 && $trustedAppsPending === 10 && $trustedAppsRejected === 5) {
        $this->line("✓ Trusted Applications Statuses: Valid (10 Approved, 10 Pending, 5 Rejected)");
        $passed++;
    } else {
        $errors[] = "Trusted applications count/status is invalid. Approved: {$trustedAppsApproved}, Pending: {$trustedAppsPending}, Rejected: {$trustedAppsRejected} (Expected: 10, 10, 5)";
    }

    // 3. Communities Validation
    $totalCommunities = Community::count();
    if ($totalCommunities === 30) {
        $this->line("✓ Total Communities Count: Valid (30 communities)");
        $passed++;
    } else {
        $errors[] = "Total communities count is {$totalCommunities} (Expected: 30)";
    }

    // Category distribution: exactly 3 per category
    $communitiesPerCategory = Community::select('category_id', DB::raw('count(*) as total'))
        ->groupBy('category_id')
        ->pluck('total', 'category_id');
    
    $categoryBalanced = true;
    foreach ($categories as $cat) {
        $count = $communitiesPerCategory[$cat->id] ?? 0;
        if ($count !== 3) {
            $categoryBalanced = false;
            $errors[] = "Category '{$cat->name}' has {$count} communities (Expected: 3)";
        }
    }
    if ($categoryBalanced) {
        $this->line("✓ Communities Category Distribution: Valid (3 communities per category)");
        $passed++;
    }

    // Membership counts per community (5 Large: 80-120, 10 Medium: 40-80, 15 Small: 15-40)
    $memberCounts = DB::table('community_members')
        ->groupBy('community_id')
        ->select('community_id', DB::raw('count(*) as count'))
        ->pluck('count', 'community_id')
        ->toArray();

    $largeCount = 0;
    $mediumCount = 0;
    $smallCount = 0;
    $invalidCount = 0;

    foreach ($memberCounts as $commId => $count) {
        if ($count >= 80 && $count <= 120) {
            $largeCount++;
        } elseif ($count >= 40 && $count < 80) {
            $mediumCount++;
        } elseif ($count >= 15 && $count < 40) {
            $smallCount++;
        } else {
            $invalidCount++;
            $errors[] = "Community ID {$commId} has invalid member count: {$count} (Expected 15-40, 40-80, or 80-120)";
        }
    }

    if ($largeCount === 5 && $mediumCount === 10 && $smallCount === 15 && $invalidCount === 0) {
        $this->line("✓ Community Memberships Constraints: Valid (5 Large, 10 Medium, 15 Small)");
        $passed++;
    } else {
        $errors[] = "Community size distribution is invalid. Large: {$largeCount} (Exp: 5), Medium: {$mediumCount} (Exp: 10), Small: {$smallCount} (Exp: 15). Invalid: {$invalidCount}";
    }

    // Cached count check - Bulk optimized
    $inconsistentCommunities = Community::leftJoin('community_members', 'communities.id', '=', 'community_members.community_id')
        ->select('communities.id', 'communities.name', 'communities.member_count', DB::raw('count(community_members.id) as actual_count'))
        ->groupBy('communities.id', 'communities.name', 'communities.member_count')
        ->havingRaw('communities.member_count != count(community_members.id)')
        ->get();

    if ($inconsistentCommunities->isEmpty()) {
        $this->line("✓ Community Cached Member Count: Valid");
        $passed++;
    } else {
        foreach ($inconsistentCommunities as $ic) {
            $errors[] = "Community '{$ic->name}' cached member_count ({$ic->member_count}) does not match actual count ({$ic->actual_count})";
        }
    }

    // User membership counts (1-15 communities) - Bulk optimized
    $badUsers = DB::table('community_members')
        ->groupBy('user_id')
        ->select('user_id', DB::raw('count(*) as count'))
        ->havingRaw('count(*) < 1 OR count(*) > 15')
        ->get();

    if ($badUsers->isEmpty()) {
        $this->line("✓ User Memberships Constraints: Valid (All users belong to 1-15 communities)");
        $passed++;
    } else {
        foreach ($badUsers as $bu) {
            $errors[] = "User ID {$bu->user_id} belongs to {$bu->count} communities (Expected: 1-15)";
        }
    }

    // 4. Events Validation
    $totalEvents = Event::count();
    if ($totalEvents === 300) {
        $this->line("✓ Total Events Count: Valid (300 events)");
        $passed++;
    } else {
        $errors[] = "Total events count is {$totalEvents} (Expected: 300)";
    }

    $badEvents = Event::groupBy('community_id')
        ->select('community_id', DB::raw('count(*) as count'))
        ->havingRaw('count(*) != 10')
        ->get();

    if ($badEvents->isEmpty()) {
        $this->line("✓ Event Distribution per Community: Valid (Exactly 10 events per community)");
        $passed++;
    } else {
        foreach ($badEvents as $be) {
            $errors[] = "Community ID {$be->community_id} has {$be->count} events (Expected: 10)";
        }
    }

    // Event Category consistency with parent community
    $inconsistentEventCategories = Event::join('communities', 'events.community_id', '=', 'communities.id')
        ->whereRaw('events.category_id != communities.category_id')
        ->count();

    if ($inconsistentEventCategories === 0) {
        $this->line("✓ Event Category Consistency: Valid (All events match parent community category)");
        $passed++;
    } else {
        $errors[] = "Found {$inconsistentEventCategories} events with category inconsistent with their parent community";
    }

    // Event Timeline: 6 COMPLETED, 3 UPCOMING, 1 ONGOING per community - Bulk optimized
    $timelineIssues = Event::select('community_id', 
            DB::raw("sum(case when status = 'COMPLETED' then 1 else 0 end) as completed_count"), 
            DB::raw("sum(case when status = 'UPCOMING' then 1 else 0 end) as upcoming_count"),
            DB::raw("sum(case when status = 'ONGOING' then 1 else 0 end) as ongoing_count"))
        ->groupBy('community_id')
        ->havingRaw("sum(case when status = 'COMPLETED' then 1 else 0 end) != 6 
                     OR sum(case when status = 'UPCOMING' then 1 else 0 end) != 3
                     OR sum(case when status = 'ONGOING' then 1 else 0 end) != 1")
        ->get();

    if ($timelineIssues->isEmpty()) {
        $this->line("✓ Event Timeline Distribution: Valid (6 COMPLETED, 3 UPCOMING, 1 ONGOING per community)");
        $passed++;
    } else {
        foreach ($timelineIssues as $issue) {
            $errors[] = "Community ID {$issue->community_id} has invalid timeline: {$issue->completed_count} COMPLETED, {$issue->upcoming_count} UPCOMING, {$issue->ongoing_count} ONGOING";
        }
    }

    // Event Date logic
    $completedDateIssues = Event::where('status', 'COMPLETED')->where('event_date', '>', now()->toDateString())->count();
    $upcomingDateIssues = Event::where('status', 'UPCOMING')->where('event_date', '<', now()->toDateString())->count();
    $ongoingDateIssues = Event::where('status', 'ONGOING')->where('event_date', '!=', now()->toDateString())->count();

    if ($completedDateIssues === 0 && $upcomingDateIssues === 0 && $ongoingDateIssues === 0) {
        $this->line("✓ Event Date & Status Consistency: Valid");
        $passed++;
    } else {
        if ($completedDateIssues > 0) $errors[] = "Found {$completedDateIssues} COMPLETED events with dates in the future";
        if ($upcomingDateIssues > 0) $errors[] = "Found {$upcomingDateIssues} UPCOMING events with dates in the past";
        if ($ongoingDateIssues > 0) $errors[] = "Found {$ongoingDateIssues} ONGOING events with dates not equal to today";
    }

    // 5. Registrations Validation
    // Validate registration brackets (10% Empty, 20% 25-50%, 40% 50-80%, 20% 80-100%, 10% Full)
    $emptyCount = 0;
    $bracket1Count = 0; // 25-50%
    $bracket2Count = 0; // 50-80%
    $bracket3Count = 0; // 80-100%
    $fullCount = 0;
    $bracketErrors = 0;

    $eventsWithRegs = Event::leftJoin('event_registrations', function($join) {
            $join->on('events.id', '=', 'event_registrations.event_id')
                 ->whereIn('event_registrations.status', ['REGISTERED', 'ATTENDED']);
        })
        ->select('events.id', 'events.max_attendees', DB::raw('count(event_registrations.id) as actual_count'))
        ->groupBy('events.id', 'events.max_attendees')
        ->get();

    foreach ($eventsWithRegs as $ev) {
        $cap = $ev->max_attendees;
        $count = $ev->actual_count;

        if ($count === 0) {
            $emptyCount++;
        } elseif ($count === $cap) {
            $fullCount++;
        } else {
            $pct = ($count / $cap) * 100;
            if ($pct >= 25 && $pct <= 50) {
                $bracket1Count++;
            } elseif ($pct > 50 && $pct <= 80) {
                $bracket2Count++;
            } elseif ($pct > 80 && $pct < 100) {
                $bracket3Count++;
            } else {
                $bracketErrors++;
                $errors[] = "Event ID {$ev->id} has registration count ({$count}) out of target brackets relative to capacity {$cap} (" . round($pct, 1) . "%)";
            }
        }
    }

    if ($emptyCount === 30 && $bracket1Count === 60 && $bracket2Count === 120 && $bracket3Count === 60 && $fullCount === 30 && $bracketErrors === 0) {
        $this->line("✓ Event Registrations Bracket Distribution: Valid (10% Empty, 20% 25-50%, 40% 50-80%, 20% 80-100%, 10% Full)");
        $passed++;
    } else {
        $errors[] = "Event registrations bracket distribution is invalid. Empty: {$emptyCount} (Exp: 30), 25-50%: {$bracket1Count} (Exp: 60), 50-80%: {$bracket2Count} (Exp: 120), 80-100%: {$bracket3Count} (Exp: 60), Full: {$fullCount} (Exp: 30). Bracket errors: {$bracketErrors}";
    }

    // Cached count check - Bulk optimized
    $inconsistentEvents = Event::leftJoin('event_registrations', function($join) {
            $join->on('events.id', '=', 'event_registrations.event_id')
                 ->whereIn('event_registrations.status', ['REGISTERED', 'ATTENDED']);
        })
        ->select('events.id', 'events.title', 'events.attendee_count', DB::raw('count(event_registrations.id) as actual_count'))
        ->groupBy('events.id', 'events.title', 'events.attendee_count')
        ->havingRaw('events.attendee_count != count(event_registrations.id)')
        ->get();

    if ($inconsistentEvents->isEmpty()) {
        $this->line("✓ Event Cached Attendee Count: Valid");
        $passed++;
    } else {
        foreach ($inconsistentEvents as $ie) {
            $errors[] = "Event '{$ie->title}' cached attendee_count ({$ie->attendee_count}) does not match actual count ({$ie->actual_count})";
        }
    }

    // Validate that each registrant is actually a member of the owning community - Bulk optimized
    $invalidRegMembers = EventRegistration::join('events', 'event_registrations.event_id', '=', 'events.id')
        ->leftJoin('community_members', function($join) {
            $join->on('event_registrations.user_id', '=', 'community_members.user_id')
                 ->on('events.community_id', '=', 'community_members.community_id');
        })
        ->whereNull('community_members.id')
        ->count();

    if ($invalidRegMembers === 0) {
        $this->line("✓ Event Registrations Member Rule: Valid (All attendees are community members)");
        $passed++;
    } else {
        $errors[] = "Found {$invalidRegMembers} event registrations by users who are NOT members of the owning community";
    }

    // 6. Ratings Validation
    $ratingsOnUpcoming = EventRating::join('events', 'event_ratings.event_id', '=', 'events.id')
        ->whereIn('events.status', ['UPCOMING', 'ONGOING'])
        ->count();

    if ($ratingsOnUpcoming === 0) {
        $this->line("✓ Rating Status Restriction: Valid (No ratings on UPCOMING or ONGOING events)");
        $passed++;
    } else {
        $errors[] = "Found {$ratingsOnUpcoming} ratings on UPCOMING or ONGOING events";
    }

    // Ratings by non-attendees - Bulk optimized
    $ratingsByNonAttendees = EventRating::leftJoin('event_registrations', function($join) {
            $join->on('event_ratings.event_id', '=', 'event_registrations.event_id')
                 ->on('event_ratings.user_id', '=', 'event_registrations.user_id')
                 ->where('event_registrations.status', '=', 'ATTENDED');
        })
        ->whereNull('event_registrations.id')
        ->count();

    if ($ratingsByNonAttendees === 0) {
        $this->line("✓ Rating Attendee Restriction: Valid (Only attendees can rate)");
        $passed++;
    } else {
        $errors[] = "Found {$ratingsByNonAttendees} ratings submitted by users who did NOT attend the event";
    }

    // Average rating check (4.1 - 4.6) - Bulk optimized
    $badRatings = EventRating::groupBy('event_id')
        ->select('event_id', DB::raw('avg(rating) as average'))
        ->havingRaw('avg(rating) < 4.1 OR avg(rating) > 4.6')
        ->get();

    if ($badRatings->isEmpty()) {
        $this->line("✓ Average Ratings Range Check: Valid (All event average ratings fall between 4.1 and 4.6)");
        $passed++;
    } else {
        foreach ($badRatings as $br) {
            $errors[] = "Event ID {$br->event_id} has average rating {$br->average} (Expected: 4.1-4.6)";
        }
    }

    // 7. Forum Messages Validation
    // 5 highly active: 50-100, 10 active: 20-50, 15 regular: 5-20 messages
    $forumCounts = ForumMessage::groupBy('community_id')
        ->select('community_id', DB::raw('count(*) as count'))
        ->pluck('count', 'community_id')
        ->toArray();

    $highlyActiveCount = 0;
    $activeCount = 0;
    $regularCount = 0;
    $invalidForumCount = 0;

    foreach ($forumCounts as $commId => $count) {
        if ($count >= 50 && $count <= 100) {
            $highlyActiveCount++;
        } elseif ($count >= 20 && $count < 50) {
            $activeCount++;
        } elseif ($count >= 5 && $count < 20) {
            $regularCount++;
        } else {
            $invalidForumCount++;
            $errors[] = "Community ID {$commId} has invalid forum message count: {$count} (Expected 5-20, 20-50, or 50-100)";
        }
    }

    if ($highlyActiveCount === 5 && $activeCount === 10 && $regularCount === 15 && $invalidForumCount === 0) {
        $this->line("✓ Forum Messages Volume: Valid (5 highly active, 10 active, 15 regular communities)");
        $passed++;
    } else {
        $errors[] = "Forum messages volume distribution is invalid. Highly Active: {$highlyActiveCount} (Expected: 5), Active: {$activeCount} (Expected: 10), Regular: {$regularCount} (Expected: 15). Invalid: {$invalidForumCount}";
    }

    // Verify that all messages are written by community members - Bulk optimized
    $invalidForumSenders = ForumMessage::leftJoin('community_members', function($join) {
            $join->on('forum_messages.community_id', '=', 'community_members.community_id')
                 ->on('forum_messages.sender_id', '=', 'community_members.user_id');
        })
        ->whereNull('community_members.id')
        ->count();

    if ($invalidForumSenders === 0) {
        $this->line("✓ Forum Authors Membership: Valid (All forum authors are community members)");
        $passed++;
    } else {
        $errors[] = "Found {$invalidForumSenders} forum messages sent by users who are NOT community members";
    }

    // 8. Notifications Validation
    $notificationsCount = Notification::count();
    if ($notificationsCount >= 100) {
        $this->line("✓ Notifications Seed Count: Valid ({$notificationsCount} notifications)");
        $passed++;
    } else {
        $errors[] = "Notifications count is {$notificationsCount} (Expected: >= 100)";
    }

    $invalidNotificationUsers = Notification::leftJoin('users', 'notifications.user_id', '=', 'users.id')
        ->whereNull('users.id')
        ->count();

    if ($invalidNotificationUsers === 0) {
        $this->line("✓ Notifications User Integrity: Valid (All notifications belong to valid users)");
        $passed++;
    } else {
        $errors[] = "Found {$invalidNotificationUsers} notifications belonging to non-existent users";
    }

    // ============================================================
    // SUMMARY REPORT
    // ============================================================
    $this->info("==================================================");
    $this->info("VALIDATION SUMMARY");
    $this->info("==================================================");
    $this->line("Passed Checks: {$passed}");
    $this->line("Errors / Violations: " . count($errors));

    if (count($errors) > 0) {
        $this->error("\n❌ DATABASE VALIDATION FAILED:");
        foreach ($errors as $err) {
            $this->error("- " . $err);
        }
    } else {
        $this->info("\n🎉 DATABASE VALIDATION SUCCESSFUL! All constraints and rules are satisfied.");
    }

    $this->info("\n==================================================");
    $this->info("FINAL REPORT METRICS");
    $this->info("==================================================");
    $this->line("Total Categories: " . Category::count());
    $this->line("Total Users: " . User::count());
    $this->line("Total Organizers: " . User::where('role', 'ORGANIZER')->count());
    $this->line("Total Communities: " . Community::count());
    $this->line("Total Events: " . Event::count());
    $this->line("Total Registrations: " . EventRegistration::count());
    $this->line("Total Forum Messages: " . ForumMessage::count());
    $this->line("Total Ratings: " . EventRating::count());
    $this->line("Total Notifications: " . Notification::count());
    $this->info("==================================================");

})->purpose('Validate the seeded database against all system requirements and constraints in bulk');

Artisan::command('event:send-reminders', function () {
    $this->info("==================================================");
    $this->info("SENDING EVENT REMINDERS (H-1)");
    $this->info("==================================================");

    $tomorrow = now()->addDay()->toDateString();
    $this->line("Target Event Date: " . $tomorrow);

    $registrations = \App\Models\EventRegistration::where('status', 'REGISTERED')
        ->whereHas('event', function ($query) use ($tomorrow) {
            $query->where('event_date', $tomorrow);
        })
        ->with(['event', 'user'])
        ->get();

    $this->line("Found registrations count: " . $registrations->count());

    $count = 0;
    foreach ($registrations as $reg) {
        $eventTime = substr($reg->event->event_time, 0, 5);
        \App\Models\Notification::send(
            $reg->user_id,
            'Pengingat Event Besok',
            "Pengingat: Event '{$reg->event->title}' akan dimulai besok jam {$eventTime}.",
            'EVENT_REMINDER',
            $reg->event_id,
            'EVENT'
        );
        $count++;
    }

    $this->info("Successfully sent {$count} event reminders!");
    $this->info("==================================================");
})->purpose('Send event reminders to registered participants 1 day before the event');

// Schedule to run daily at 08:00 AM
\Illuminate\Support\Facades\Schedule::command('event:send-reminders')->dailyAt('08:00');
