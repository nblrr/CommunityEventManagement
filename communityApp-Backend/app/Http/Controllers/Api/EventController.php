<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Event;
use App\Models\EventRegistration;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;

class EventController extends Controller
{
    /**
     * Daftar event (paginated).
     * GET /api/events
     */
    public function index(Request $request)
    {
        $query = Event::with([
            'community.organizer',
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
            $now = now();
            $statusFilter = strtoupper($request->status);
            if ($statusFilter === 'UPCOMING') {
                $query->where(function($q) use ($now) {
                    $q->where('event_date', '>', $now->toDateString())
                      ->orWhere(function($q2) use ($now) {
                          $q2->where('event_date', '=', $now->toDateString())
                              ->where('event_time', '>', $now->format('H:i:s'));
                      });
                });
            } elseif ($statusFilter === 'COMPLETED') {
                $query->where(function($q) use ($now) {
                    $q->where('event_date', '<', $now->toDateString())
                      ->orWhere(function($q2) use ($now) {
                          $q2->where('event_date', '=', $now->toDateString())
                              ->whereNotNull('end_time')
                              ->where('end_time', '<', $now->format('H:i:s'));
                      });
                });
            } elseif ($statusFilter === 'ONGOING') {
                $query->where('event_date', '=', $now->toDateString())
                      ->where('event_time', '<=', $now->format('H:i:s'))
                      ->where(function($q) use ($now) {
                          $q->whereNull('end_time')
                            ->orWhere('end_time', '>=', $now->format('H:i:s'));
                      });
            }
        }

        if ($request->has('sort_by') && $request->sort_by != '') {
            $sortBy = $request->sort_by;
            if ($sortBy === 'terbaru') {
                $query->orderBy('event_date', 'desc')->orderBy('event_time', 'desc');
            } elseif ($sortBy === 'terlama') {
                $query->orderBy('event_date', 'asc')->orderBy('event_time', 'asc');
            } elseif ($sortBy === 'peserta_terbanyak') {
                $query->orderBy('attendee_count', 'desc');
            }
        } else {
            $query->orderBy('created_at', 'desc');
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
                'community.organizer',
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
        $ttl = config('performance.upcoming_events_ttl');

        $events = Cache::remember('upcoming_events', $ttl, function () {
            $now = now();

            return Event::where(function($query) use ($now) {
                // Event hasn't started yet (event_date is in the future, OR event_date is today and event_time hasn't passed)
                $query->where('event_date', '>', $now->toDateString())
                      ->orWhere(function($q) use ($now) {
                          $q->where('event_date', '=', $now->toDateString())
                            ->where('event_time', '>', $now->format('H:i:s'));
                      });
            })
            ->with(['community', 'category'])
            ->orderBy('event_date', 'asc')
            ->orderBy('event_time', 'asc')
            ->limit(10)
            ->get();
        });

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

        $now = now();
        $query = Event::where(function($q) use ($now) {
                $q->where('event_date', '>', $now->toDateString())
                  ->orWhere(function($q2) use ($now) {
                      $q2->where('event_date', '=', $now->toDateString())
                          ->where('event_time', '>', $now->format('H:i:s'));
                  });
            });

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

    public function store(Request $request)
    {
        $validated = $request->validate([
            'community_id'    => 'required|integer|exists:communities,id',
            'category_id'     => 'required|integer|exists:categories,id',
            'title'           => 'required|string|max:255',
            'description'     => 'required|string',
            'event_date'      => 'required|date|after_or_equal:today',
            'event_time'      => 'required|date_format:H:i',
            'end_time'        => 'nullable|date_format:H:i',
            'location'        => 'required|string|max:255',
            'is_online'       => 'sometimes|boolean',
            'max_attendees'   => 'required|integer|min:1',
            'cover_image_url' => 'nullable|string',
        ]);

        $user = $request->user();

        // Cek apakah user adalah organizer community tersebut atau admin
        $community = \App\Models\Community::findOrFail($validated['community_id']);

        if (!$user->isAdmin() && $community->organizer_id !== $user->id) {
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
            'end_time'        => $validated['end_time'] ?? null,
            'location'        => $validated['location'],
            'is_online'       => $validated['is_online'] ?? false,
            'max_attendees'   => $validated['max_attendees'],
            'cover_image_url' => $validated['cover_image_url'] ?? null,
        ]);

        Cache::forget('upcoming_events');

        return response()->json($event->fresh(), 201);
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

        if (!$user->isAdmin() && $event->community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk mengubah event ini.'
            ], 403);
        }

        $validated = $request->validate([
            'title'           => 'sometimes|string|max:255',
            'description'     => 'sometimes|string',
            'event_date'      => 'sometimes|date',
            'event_time'      => 'sometimes|date_format:H:i',
            'end_time'        => 'nullable|date_format:H:i',
            'location'        => 'sometimes|string|max:255',
            'is_online'       => 'sometimes|boolean',
            'max_attendees'   => 'sometimes|integer|min:1',
            'cover_image_url' => 'nullable|string',
            'status'          => 'sometimes|in:UPCOMING,ONGOING,COMPLETED',
        ]);

        $event->update($validated);

        Cache::forget('upcoming_events');

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

        if (!$user->isAdmin() && $event->community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk menghapus event ini.'
            ], 403);
        }

        $event->delete();

        Cache::forget('upcoming_events');

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
        $user = auth()->user();

        // Use calculated_status if needed or use the model's logic
        if ($event->calculated_status !== 'UPCOMING') {
            return response()->json([
                'message' => 'Anda tidak dapat mendaftar untuk event yang sedang berlangsung atau sudah selesai.'
            ], 403);
        }

        $userId = $user->id;

        // Check if user is a member of the parent community
        if (!$event->community->members()->where('user_id', $userId)->exists()) {
            return response()->json([
                'message' => 'Anda harus bergabung ke komunitas ini terlebih dahulu sebelum mendaftar event.'
            ], 403);
        }

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
                    'message' => 'Kuota peserta telah penuh'
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
                'message' => 'Kuota peserta telah penuh'
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

    public function organizerEvents(Request $request)
    {
        $user = $request->user();
        $communityIds = \App\Models\Community::where('organizer_id', $user->id)->pluck('id');

        $events = Event::whereIn('community_id', $communityIds)
            ->with(['community', 'category'])
            ->paginate(10);

        return response()->json($events);
    }

    /**
     * GET /api/events/{event}/participants
     */
    public function participants(Request $request, Event $event)
    {
        $user = $request->user();

        if (!$user->isAdmin() && $event->community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk melihat peserta event ini.'
            ], 403);
        }

        $participants = EventRegistration::where('event_id', $event->id)
            ->whereIn('status', ['REGISTERED', 'ATTENDED'])
            ->with('user')
            ->get()
            ->map(function($registration) {
                return [
                    'id' => $registration->user->id,
                    'name' => $registration->user->name,
                    'email' => $registration->user->email,
                    'role' => $registration->user->role,
                    'is_blocked' => (bool)$registration->user->is_blocked,
                    'is_trusted' => (bool)$registration->user->is_trusted,
                    'avatar_url' => $registration->user->avatar_url,
                    'phone_number' => $registration->user->phone_number,
                    'gender' => $registration->user->gender,
                    'bio' => $registration->user->bio,
                    'birth_date' => $registration->user->birth_date,
                ];
            });

        return response()->json($participants);
    }
}
