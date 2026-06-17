<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Event;
use App\Models\EventRating;
use App\Models\EventRegistration;
use Illuminate\Http\Request;

class EventRatingController extends Controller
{
    /**
     * GET /api/events/{event}/ratings
     */
    public function index(Event $event)
    {
        $ratings = EventRating::where('event_id', $event->id)
            ->with('user')
            ->latest()
            ->get();

        return response()->json($ratings);
    }

    /**
     * User memberikan rating pada event.
     * POST /api/events/{event}/ratings
     *
     * Syarat: user sudah terdaftar (REGISTERED/ATTENDED) di event tersebut.
     * Satu user hanya bisa 1 rating per event.
     */
    public function store(Request $request, Event $event)
    {
        $user = $request->user();

        // Cek apakah user sudah terdaftar di event
        $registration = EventRegistration::where('event_id', $event->id)
            ->where('user_id', $user->id)
            ->whereIn('status', ['REGISTERED', 'ATTENDED'])
            ->first();

        if (!$registration) {
            return response()->json([
                'message' => 'Anda harus terdaftar di event ini untuk memberikan rating.'
            ], 403);
        }

        // Cek apakah sudah pernah rating
        $existing = EventRating::where('event_id', $event->id)
            ->where('user_id', $user->id)
            ->first();

        if ($existing) {
            return response()->json([
                'message' => 'Anda sudah memberikan rating untuk event ini.'
            ], 409);
        }

        $validated = $request->validate([
            'rating'  => 'required|integer|min:1|max:5',
            'comment' => 'nullable|string',
        ]);

        $rating = EventRating::create([
            'event_id' => $event->id,
            'user_id'  => $user->id,
            'rating'   => $validated['rating'],
            'comment'  => $validated['comment'] ?? null,
        ]);

        return response()->json($rating, 201);
    }
}
