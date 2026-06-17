<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Community;
use App\Models\Event;
use Illuminate\Http\Request;

class SearchController extends Controller
{
    /**
     * Unified search for communities and events.
     * GET /api/search
     */
    public function search(Request $request)
    {
        $request->validate([
            'query' => 'nullable|string|max:255',
            'category_id' => 'nullable|integer|exists:categories,id',
        ]);

        $searchQuery = $request->input('query', '');
        $categoryId = $request->input('category_id');

        // Search communities
        $communitiesQuery = Community::with(['organizer', 'category'])->withCount('events');
        if ($categoryId) {
            $communitiesQuery->where('category_id', $categoryId);
        }
        if ($searchQuery !== '') {
            $communitiesQuery->where(function($q) use ($searchQuery) {
                $q->where('name', 'like', "%{$searchQuery}%")
                  ->orWhere('description', 'like', "%{$searchQuery}%");
            });
        }
        $communities = $communitiesQuery->limit(10)->get();

        // Search events
        $eventsQuery = Event::with(['community', 'category']);
        if ($categoryId) {
            $eventsQuery->where('category_id', $categoryId);
        }
        if ($searchQuery !== '') {
            $eventsQuery->where(function($q) use ($searchQuery) {
                $q->where('title', 'like', "%{$searchQuery}%")
                  ->orWhere('description', 'like', "%{$searchQuery}%")
                  ->orWhere('location', 'like', "%{$searchQuery}%");
            });
        }
        $events = $eventsQuery->limit(10)->get();

        return response()->json([
            'communities' => $communities,
            'events' => $events
        ]);
    }
}
