<?php

namespace Tests\Feature;

use App\Models\Category;
use App\Models\Community;
use App\Models\Event;
use App\Models\User;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class CategorySystemTest extends TestCase
{
    use RefreshDatabase;

    protected User $user;
    protected Category $categoryTech;
    protected Category $categorySports;

    protected function setUp(): void
    {
        parent::setUp();

        $this->user = User::factory()->create([
            'role' => 'ORGANIZER'
        ]);

        $this->categoryTech = Category::create([
            'name' => 'Technology',
            'icon' => 'code'
        ]);

        $this->categorySports = Category::create([
            'name' => 'Sports',
            'icon' => 'sports_soccer'
        ]);
    }

    /**
     * Test getting categories list.
     */
    public function test_get_categories(): void
    {
        $response = $this->actingAs($this->user)
            ->getJson('/api/categories');

        $response->assertStatus(200)
            ->assertJsonCount(2)
            ->assertJsonFragment(['name' => 'Technology'])
            ->assertJsonFragment(['name' => 'Sports']);
    }

    /**
     * Test creating a community validation and storage.
     */
    public function test_create_community_stores_category_id(): void
    {
        $response = $this->actingAs($this->user)
            ->postJson('/api/communities', [
                'name' => 'GDG Solo',
                'description' => 'Google Developer Group Solo',
                'category_id' => $this->categoryTech->id,
                'cover_image_url' => 'http://example.com/cover.png'
            ]);

        $response->assertStatus(201)
            ->assertJsonFragment([
                'name' => 'GDG Solo',
                'category_id' => $this->categoryTech->id
            ]);

        $this->assertDatabaseHas('communities', [
            'name' => 'GDG Solo',
            'category_id' => $this->categoryTech->id
        ]);
    }

    /**
     * Test creating an event validation and storage.
     */
    public function test_create_event_stores_category_id(): void
    {
        $community = Community::create([
            'name' => 'GDG Solo',
            'description' => 'Google Developer Group Solo',
            'organizer_id' => $this->user->id,
            'category_id' => $this->categoryTech->id
        ]);

        $response = $this->actingAs($this->user)
            ->postJson('/api/events', [
                'community_id' => $community->id,
                'category_id' => $this->categoryTech->id,
                'title' => 'Flutter Meetup',
                'description' => 'A meetup about Flutter',
                'event_date' => '2026-07-01',
                'event_time' => '14:00',
                'location' => 'Solo Grand Mall',
                'max_attendees' => 50,
                'is_online' => false
            ]);

        $response->assertStatus(201)
            ->assertJsonFragment([
                'title' => 'Flutter Meetup',
                'category_id' => $this->categoryTech->id
            ]);

        $this->assertDatabaseHas('events', [
            'title' => 'Flutter Meetup',
            'category_id' => $this->categoryTech->id
        ]);
    }

    /**
     * Test community and event list filtering by category_id.
     */
    public function test_list_filtering_by_category(): void
    {
        $communityTech = Community::create([
            'name' => 'GDG Solo',
            'description' => 'Google Developer Group Solo',
            'organizer_id' => $this->user->id,
            'category_id' => $this->categoryTech->id
        ]);

        $communitySports = Community::create([
            'name' => 'Running Solo',
            'description' => 'Running community in Solo',
            'organizer_id' => $this->user->id,
            'category_id' => $this->categorySports->id
        ]);

        // Filter communities
        $response = $this->actingAs($this->user)
            ->getJson('/api/communities?category_id=' . $this->categoryTech->id);

        $response->assertStatus(200)
            ->assertJsonFragment(['name' => 'GDG Solo'])
            ->assertJsonMissing(['name' => 'Running Solo']);
    }

    /**
     * Test unified search with query and category_id.
     */
    public function test_unified_search(): void
    {
        $community = Community::create([
            'name' => 'GDG Solo Tech',
            'description' => 'Google Developer Group Solo',
            'organizer_id' => $this->user->id,
            'category_id' => $this->categoryTech->id
        ]);

        $event = Event::create([
            'community_id' => $community->id,
            'category_id' => $this->categoryTech->id,
            'title' => 'GDG Tech Talk',
            'description' => 'A talk about developer technologies',
            'event_date' => '2026-07-01',
            'event_time' => '14:00',
            'location' => 'Solo Grand Mall',
            'max_attendees' => 50,
            'is_online' => false
        ]);

        $response = $this->actingAs($this->user)
            ->getJson('/api/search?query=GDG&category_id=' . $this->categoryTech->id);

        $response->assertStatus(200)
            ->assertJsonFragment(['name' => 'GDG Solo Tech'])
            ->assertJsonFragment(['title' => 'GDG Tech Talk']);
    }
}
