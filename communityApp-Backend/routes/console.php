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
    $adminCount = User::where('role', 'ADMIN')->count();
    $organizerCount = User::where('role', 'ORGANIZER')->count();
    $memberCount = User::where('role', 'USER')->count();

    if ($totalUsers === 100) {
        $this->line("✓ Total Users Count: Valid (100 users)");
        $passed++;
    } else {
        $errors[] = "Total users count is {$totalUsers} (Expected: 100)";
    }

    if ($adminCount === 1) {
        $this->line("✓ Admin Count: Valid (1 admin)");
        $passed++;
    } else {
        $errors[] = "Admin count is {$adminCount} (Expected: 1)";
    }

    if ($organizerCount === 20) {
        $this->line("✓ Organizer Count: Valid (20 organizers)");
        $passed++;
    } else {
        $errors[] = "Organizer count is {$organizerCount} (Expected: 20)";
    }

    if ($memberCount === 79) {
        $this->line("✓ Member Count: Valid (79 members)");
        $passed++;
    } else {
        $errors[] = "Member count is {$memberCount} (Expected: 79)";
    }

    // Fixed Accounts Check
    $fixedAdmin = User::where('email', 'admin@communityapp.com')->first();
    $fixedOrganizer = User::where('email', 'organizer1@communityapp.com')->first();
    $fixedMember = User::where('email', 'user1@communityapp.com')->first();

    if ($fixedAdmin && $fixedAdmin->role === 'ADMIN') {
        $this->line("✓ Demo Admin Account: Valid (admin@communityapp.com)");
        $passed++;
    } else {
        $errors[] = "Demo Admin account is missing or has incorrect role";
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

    // Trusted Organizer Distribution (40% trusted, 60% pending)
    $trustedOrganizers = User::where('role', 'ORGANIZER')->where('is_trusted', true)->count();
    $pendingOrganizers = User::where('role', 'ORGANIZER')->where('is_trusted', false)->count();

    $trustedAppsApproved = TrustedApplication::where('status', 'APPROVED')->count();
    $trustedAppsPending = TrustedApplication::where('status', 'PENDING')->count();

    if ($trustedOrganizers === 8 && $pendingOrganizers === 12) {
        $this->line("✓ Organizer Trusted Distribution: Valid (8 Trusted / 40%, 12 Pending / 60%)");
        $passed++;
    } else {
        $errors[] = "Organizer trusted distribution is invalid. Trusted: {$trustedOrganizers}, Pending: {$pendingOrganizers} (Expected: 8 and 12)";
    }

    if ($trustedAppsApproved === 8 && $trustedAppsPending === 12) {
        $this->line("✓ Trusted Applications Statuses: Valid (8 Approved, 12 Pending)");
        $passed++;
    } else {
        $errors[] = "Trusted applications count/status is invalid. Approved: {$trustedAppsApproved}, Pending: {$trustedAppsPending} (Expected: 8 and 12)";
    }

    // 3. Communities Validation
    $totalCommunities = Community::count();
    if ($totalCommunities === 20) {
        $this->line("✓ Total Communities Count: Valid (20 communities)");
        $passed++;
    } else {
        $errors[] = "Total communities count is {$totalCommunities} (Expected: 20)";
    }

    // Category distribution: exactly 2 per category
    $communitiesPerCategory = Community::select('category_id', DB::raw('count(*) as total'))
        ->groupBy('category_id')
        ->pluck('total', 'category_id');
    
    $categoryBalanced = true;
    foreach ($categories as $cat) {
        $count = $communitiesPerCategory[$cat->id] ?? 0;
        if ($count !== 2) {
            $categoryBalanced = false;
            $errors[] = "Category '{$cat->name}' has {$count} communities (Expected: 2)";
        }
    }
    if ($categoryBalanced) {
        $this->line("✓ Communities Category Distribution: Valid (2 communities per category)");
        $passed++;
    }

    // Membership counts per community (10-40 members) - Bulk optimized
    $badCommunities = DB::table('community_members')
        ->groupBy('community_id')
        ->select('community_id', DB::raw('count(*) as count'))
        ->havingRaw('count(*) < 10 OR count(*) > 40')
        ->get();

    if ($badCommunities->isEmpty()) {
        $this->line("✓ Community Memberships Constraints: Valid (All communities have 10-40 members)");
        $passed++;
    } else {
        foreach ($badCommunities as $bc) {
            $errors[] = "Community ID {$bc->community_id} has invalid members count: {$bc->count} (Expected: 10-40)";
        }
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

    // User membership counts (1-5 communities) - Bulk optimized
    $badUsers = DB::table('community_members')
        ->groupBy('user_id')
        ->select('user_id', DB::raw('count(*) as count'))
        ->havingRaw('count(*) < 1 OR count(*) > 5')
        ->get();

    if ($badUsers->isEmpty()) {
        $this->line("✓ User Memberships Constraints: Valid (All users belong to 1-5 communities)");
        $passed++;
    } else {
        foreach ($badUsers as $bu) {
            $errors[] = "User ID {$bu->user_id} belongs to {$bu->count} communities (Expected: 1-5)";
        }
    }

    // 4. Events Validation
    $totalEvents = Event::count();
    if ($totalEvents === 200) {
        $this->line("✓ Total Events Count: Valid (200 events)");
        $passed++;
    } else {
        $errors[] = "Total events count is {$totalEvents} (Expected: 200)";
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

    // Event Timeline: 6 PAST, 4 UPCOMING per community - Bulk optimized
    $timelineIssues = Event::select('community_id', 
            DB::raw("sum(case when status = 'PAST' then 1 else 0 end) as past_count"), 
            DB::raw("sum(case when status = 'UPCOMING' then 1 else 0 end) as upcoming_count"))
        ->groupBy('community_id')
        ->havingRaw("sum(case when status = 'PAST' then 1 else 0 end) != 6 OR sum(case when status = 'UPCOMING' then 1 else 0 end) != 4")
        ->get();

    if ($timelineIssues->isEmpty()) {
        $this->line("✓ Event Timeline Distribution: Valid (6 PAST, 4 UPCOMING per community)");
        $passed++;
    } else {
        foreach ($timelineIssues as $issue) {
            $errors[] = "Community ID {$issue->community_id} has invalid timeline: {$issue->past_count} PAST, {$issue->upcoming_count} UPCOMING";
        }
    }

    // Event Date logic
    $pastDateIssues = Event::where('status', 'PAST')->where('event_date', '>=', now()->toDateString())->count();
    $upcomingDateIssues = Event::where('status', 'UPCOMING')->where('event_date', '<', now()->toDateString())->count();

    if ($pastDateIssues === 0 && $upcomingDateIssues === 0) {
        $this->line("✓ Event Date & Status Consistency: Valid");
        $passed++;
    } else {
        if ($pastDateIssues > 0) $errors[] = "Found {$pastDateIssues} PAST events with dates in the future or today";
        if ($upcomingDateIssues > 0) $errors[] = "Found {$upcomingDateIssues} UPCOMING events with dates in the past";
    }

    // 5. Registrations Validation
    // Registration counts per event (5-30) - Bulk optimized
    $badRegs = EventRegistration::whereIn('status', ['REGISTERED', 'ATTENDED'])
        ->groupBy('event_id')
        ->select('event_id', DB::raw('count(*) as count'))
        ->havingRaw('count(*) < 5 OR count(*) > 30')
        ->get();

    if ($badRegs->isEmpty()) {
        $this->line("✓ Event Registrations Count: Valid (All events have 5-30 attendees)");
        $passed++;
    } else {
        foreach ($badRegs as $br) {
            $errors[] = "Event ID {$br->event_id} has {$br->count} registrations (Expected: 5-30)";
        }
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
        ->where('events.status', 'UPCOMING')
        ->count();

    if ($ratingsOnUpcoming === 0) {
        $this->line("✓ Rating Status Restriction: Valid (No ratings on UPCOMING events)");
        $passed++;
    } else {
        $errors[] = "Found {$ratingsOnUpcoming} ratings on UPCOMING events";
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

    // Average rating check (3.5 - 4.9) - Bulk optimized
    $badRatings = EventRating::groupBy('event_id')
        ->select('event_id', DB::raw('avg(rating) as average'))
        ->havingRaw('avg(rating) < 3.5 OR avg(rating) > 4.9')
        ->get();

    if ($badRatings->isEmpty()) {
        $this->line("✓ Average Ratings Range Check: Valid (All event average ratings fall between 3.5 and 4.9)");
        $passed++;
    } else {
        foreach ($badRatings as $br) {
            $errors[] = "Event ID {$br->event_id} has average rating {$br->average} (Expected: 3.5-4.9)";
        }
    }

    // 7. Forum Messages Validation
    // Messages per community (20-50) - Bulk optimized
    $badForumCount = ForumMessage::groupBy('community_id')
        ->select('community_id', DB::raw('count(*) as count'))
        ->havingRaw('count(*) < 20 OR count(*) > 50')
        ->get();

    if ($badForumCount->isEmpty()) {
        $this->line("✓ Forum Messages Volume: Valid (All communities have 20-50 messages)");
        $passed++;
    } else {
        foreach ($badForumCount as $bfc) {
            $errors[] = "Community ID {$bfc->community_id} has {$bfc->count} messages (Expected: 20-50)";
        }
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
