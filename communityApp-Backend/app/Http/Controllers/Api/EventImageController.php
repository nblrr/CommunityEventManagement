<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Event;
use App\Models\EventImage;
use Illuminate\Http\Request;

class EventImageController extends Controller
{
    /**
     * Lihat seluruh gambar event.
     * GET /api/events/{event}/images
     */
    public function index(Event $event)
    {
        $images = $event->images()
            ->latest()
            ->get();

        return response()->json($images);
    }

    public function store(Request $request, Event $event)
    {
        $user = $request->user();

        // Check if user is organizer of the event's community or admin
        if (!$user->isAdmin() && $event->community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk mengupload gambar untuk event ini.'
            ], 403);
        }

        $validated = $request->validate([
            'image'     => 'nullable|image|mimes:jpg,jpeg,png,webp|max:2048',
            'image_url' => 'nullable|string',
        ]);

        if (!$request->hasFile('image') && !$request->has('image_url')) {
            return response()->json([
                'message' => 'Harap sertakan file image atau image_url.'
            ], 422);
        }

        $imageUrl = $validated['image_url'] ?? null;

        if ($request->hasFile('image')) {
            $path = $request->file('image')->store('events', 'public');
            $imageUrl = asset('storage/' . $path);
        }

        $image = EventImage::create([
            'event_id'    => $event->id,
            'image_url'   => $imageUrl,
            'uploaded_by' => $user->id,
        ]);

        return response()->json($image, 201);
    }
}
