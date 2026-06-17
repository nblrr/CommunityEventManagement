<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Event;
use App\Models\EventRegistration;
use Illuminate\Http\Request;

class EventController extends Controller
{
    /**
     * Daftar event (paginated).
     * GET /api/events
     */
    public function index(Request $request)
    {
        $query = Event::with([
            'community',
            'category'
        ]);

        if ($request->has('search') && $request->search != '') {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('title', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%")
                  ->orWhere('location', 'like', "%{$search}%");
            });
        }

        if ($request->has('category_id') && $request->category_id != '') {
            $query->where('category_id', $request->category_id);
        }

        if ($request->has('status') && $request->status != '') {
            $query->where('status', $request->status);
        }

        $events = $query->paginate(10);

        return response()->json($events);
    }

    /**
     * Detail event.
     * GET /api/events/{event}
     */
    public function show(Event $event)
    {
        return response()->json(
            $event->load([
                'community',
                'category',
                'ratings.user',
                'images'
            ])
        );
    }

    /**
     * GET /api/my-events
     */
    public function myEvents(Request $request)
    {
        $user = $request->user();
        $eventIds = EventRegistration::where('user_id', $user->id)
            ->whereIn('status', ['REGISTERED', 'ATTENDED'])
            ->pluck('event_id');

        $events = Event::whereIn('id', $eventIds)
            ->with(['community', 'category'])
            ->paginate(10);

        return response()->json($events);
    }

    /**
     * GET /api/upcoming-events
     */
    public function upcomingEvents(Request $request)
    {
        $events = Event::where('status', 'UPCOMING')
            ->orWhere('event_date', '>=', now()->toDateString())
            ->with(['community', 'category'])
            ->orderBy('event_date', 'asc')
            ->limit(10)
            ->get();

        return response()->json($events);
    }

    /**
     * GET /api/recommended-events
     */
    public function recommendedEvents(Request $request)
    {
        $user = $request->user();
        
        $categoryIds = EventRegistration::where('user_id', $user->id)
            ->join('events', 'event_registrations.event_id', '=', 'events.id')
            ->pluck('events.category_id')
            ->unique();

        $communityIds = $user->communities()->pluck('communities.id');

        $query = Event::where('status', 'UPCOMING')
            ->where('event_date', '>=', now()->toDateString());

        if ($categoryIds->isNotEmpty() || $communityIds->isNotEmpty()) {
            $query->where(function($q) use ($categoryIds, $communityIds) {
                if ($categoryIds->isNotEmpty()) {
                    $q->whereIn('category_id', $categoryIds);
                }
                if ($communityIds->isNotEmpty()) {
                    $q->orWhereIn('community_id', $communityIds);
                }
            });
        }

        $registeredEventIds = EventRegistration::where('user_id', $user->id)
            ->whereIn('status', ['REGISTERED', 'ATTENDED'])
            ->pluck('event_id');

        $query->whereNotIn('id', $registeredEventIds);

        $events = $query->with(['community', 'category'])
            ->orderBy('attendee_count', 'desc')
            ->limit(10)
            ->get();

        return response()->json($events);
    }

    /**
     * Buat event baru.
     * POST /api/events
     *
     * Hanya organizer pemilik community atau admin.
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'community_id'    => 'required|integer|exists:communities,id',
            'category_id'     => 'required|integer|exists:categories,id',
            'title'           => 'required|string|max:255',
            'description'     => 'required|string',
            'event_date'      => 'required|date',
            'event_time'      => 'required|date_format:H:i',
            'location'        => 'required|string|max:255',
            'is_online'       => 'sometimes|boolean',
            'max_attendees'   => 'required|integer|min:1',
            'cover_image_url' => 'nullable|string',
        ]);

        $user = $request->user();

        // Cek apakah user adalah organizer community tersebut atau admin
        $community = \App\Models\Community::findOrFail($validated['community_id']);

        if ($user->role !== 'ADMIN' && $community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda harus menjadi organizer community ini untuk membuat event.'
            ], 403);
        }

        $event = Event::create([
            'community_id'    => $validated['community_id'],
            'category_id'     => $validated['category_id'],
            'title'           => $validated['title'],
            'description'     => $validated['description'],
            'event_date'      => $validated['event_date'],
            'event_time'      => $validated['event_time'],
            'location'        => $validated['location'],
            'is_online'       => $validated['is_online'] ?? false,
            'max_attendees'   => $validated['max_attendees'],
            'cover_image_url' => $validated['cover_image_url'] ?? null,
        ]);

        return response()->json($event, 201);
    }

    /**
     * Update event.
     * PUT /api/events/{event}
     *
     * Hanya organizer pemilik community event atau admin.
     */
    public function update(Request $request, Event $event)
    {
        $user = $request->user();

        if ($user->role !== 'ADMIN' && $event->community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk mengubah event ini.'
            ], 403);
        }

        $validated = $request->validate([
            'title'           => 'sometimes|string|max:255',
            'description'     => 'sometimes|string',
            'event_date'      => 'sometimes|date',
            'event_time'      => 'sometimes|date_format:H:i',
            'location'        => 'sometimes|string|max:255',
            'is_online'       => 'sometimes|boolean',
            'max_attendees'   => 'sometimes|integer|min:1',
            'cover_image_url' => 'nullable|string',
            'status'          => 'sometimes|in:UPCOMING,ONGOING,PAST',
        ]);

        $event->update($validated);

        return response()->json($event->fresh());
    }

    /**
     * Hapus event.
     * DELETE /api/events/{event}
     *
     * Hanya organizer pemilik community event atau admin.
     */
    public function destroy(Request $request, Event $event)
    {
        $user = $request->user();

        if ($user->role !== 'ADMIN' && $event->community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk menghapus event ini.'
            ], 403);
        }

        $event->delete();

        return response()->json([
            'message' => 'Event deleted'
        ]);
    }


    /**
     * Register ke event.
     * POST /api/events/{event}/register
     */
    public function register(Event $event)
    {
        $userId = auth()->id();

        // Check if registration exists
        $existing = EventRegistration::where('event_id', $event->id)
            ->where('user_id', $userId)
            ->first();

        if ($existing) {
            if ($existing->status !== 'CANCELLED') {
                return response()->json([
                    'message' => 'Anda sudah terdaftar di event ini.'
                ], 409);
            }

            // If it was cancelled, they can re-register if there is capacity
            $count = EventRegistration::where('event_id', $event->id)
                ->whereIn('status', ['REGISTERED', 'ATTENDED'])
                ->count();

            if ($event->max_attendees > 0 && $count >= $event->max_attendees) {
                return response()->json([
                    'message' => 'Event penuh'
                ], 400);
            }

            $existing->update(['status' => 'REGISTERED']);

            $event->update([
                'attendee_count' => EventRegistration::where('event_id', $event->id)
                    ->whereIn('status', ['REGISTERED', 'ATTENDED'])
                    ->count()
            ]);

            return response()->json([
                'message' => 'Registered',
                'event'   => $event->fresh()
            ]);
        }

        $count = EventRegistration::where('event_id', $event->id)
            ->whereIn('status', ['REGISTERED', 'ATTENDED'])
            ->count();

        if ($event->max_attendees > 0 && $count >= $event->max_attendees) {
            return response()->json([
                'message' => 'Event penuh'
            ], 400);
        }

        EventRegistration::create([
            'event_id' => $event->id,
            'user_id'  => $userId,
            'status'   => 'REGISTERED',
        ]);

        $event->update([
            'attendee_count' => EventRegistration::where('event_id', $event->id)
                ->whereIn('status', ['REGISTERED', 'ATTENDED'])
                ->count()
        ]);

        return response()->json([
            'message' => 'Registered',
            'event'   => $event->fresh()
        ]);
    }

    /**
     * Cancel registrasi event.
     * POST /api/events/{event}/cancel
     */
    public function cancel(Event $event)
    {
        $userId = auth()->id();

        $registration = EventRegistration::where('event_id', $event->id)
            ->where('user_id', $userId)
            ->first();

        if (!$registration) {
            return response()->json([
                'message' => 'Anda belum terdaftar di event ini.'
            ], 400);
        }

        if ($registration->status === 'CANCELLED') {
            return response()->json([
                'message' => 'Pendaftaran Anda sudah dibatalkan sebelumnya.'
            ], 400);
        }

        $registration->update(['status' => 'CANCELLED']);

        $event->update([
            'attendee_count' => EventRegistration::where('event_id', $event->id)
                ->whereIn('status', ['REGISTERED', 'ATTENDED'])
                ->count()
        ]);

        return response()->json([
            'message' => 'Cancelled',
            'event'   => $event->fresh()
        ]);
    }

    /**
     * GET /api/organizer/events
     */
    public function organizerEvents(Request $request)
    {
        $user = $request->user();
        $communityIds = \App\Models\Community::where('organizer_id', $user->id)->pluck('id');

        $events = Event::whereIn('community_id', $communityIds)
            ->with(['community', 'category'])
            ->paginate(10);

        return response()->json($events);
    }
}