<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Community;
use Illuminate\Http\Request;

class CommunityController extends Controller
{
    /**
     * Daftar community (paginated).
     * GET /api/communities
     */
    public function index(Request $request)
    {
        $query = Community::with([
            'organizer',
            'category'
        ])->withCount('events');

        if ($request->has('search') && $request->search != '') {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('description', 'like', "%{$search}%");
            });
        }

        if ($request->has('category_id') && $request->category_id != '') {
            $query->where('category_id', $request->category_id);
        }

        if ($request->has('sort_by') && $request->sort_by != '') {
            $sortBy = $request->sort_by;
            if ($sortBy === 'terbaru') {
                $query->orderBy('created_at', 'desc');
            } elseif ($sortBy === 'terlama') {
                $query->orderBy('created_at', 'asc');
            } elseif ($sortBy === 'peserta_terbanyak') {
                $query->orderBy('member_count', 'desc');
            }
        } else {
            $query->orderBy('created_at', 'desc');
        }

        $communities = $query->paginate(10);

        return response()->json($communities);
    }

    /**
     * Daftar community yang diikuti/diikuti user.
     * GET /api/my-communities
     */
    public function myCommunities(Request $request)
    {
        $user = $request->user();

        $communities = $user->communities()
            ->with(['organizer', 'category'])
            ->withCount('events')
            ->paginate(10);

        return response()->json($communities);
    }

    /**
     * Detail community.
     * GET /api/communities/{community}
     */
    public function show(Community $community)
    {
        return response()->json(
            $community->load([
                'organizer',
                'category',
                'members',
                'events'
            ])
        );
    }

    /**
     * Buat community baru (organizer/admin only — dijaga middleware).
     * POST /api/communities
     */
    public function store(Request $request)
    {
        $validated = $request->validate([
            'name'            => 'required|string|max:255',
            'description'     => 'required|string',
            'category_id'     => 'required|integer|exists:categories,id',
            'cover_image_url' => 'nullable|string',
        ]);

        $community = Community::create([
            'name'            => $validated['name'],
            'description'     => $validated['description'],
            'organizer_id'    => auth()->id(),
            'category_id'     => $validated['category_id'],
            'status'          => 'ACTIVE',
            'cover_image_url' => $validated['cover_image_url'] ?? null,
        ]);

        return response()->json($community, 201);
    }

    /**
     * Update community.
     * PUT /api/communities/{community}
     *
     * Hanya organizer pembuat atau admin.
     */
    public function update(Request $request, Community $community)
    {
        $user = $request->user();

        if ($user->role !== 'ADMIN' && $community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk mengubah community ini.'
            ], 403);
        }

        $validated = $request->validate([
            'name'            => 'sometimes|string|max:255',
            'description'     => 'sometimes|string',
            'category_id'     => 'sometimes|integer|exists:categories,id',
            'status'          => 'sometimes|in:ACTIVE,INACTIVE',
            'cover_image_url' => 'nullable|string',
        ]);

        $community->update($validated);

        return response()->json($community->fresh());
    }

    /**
     * Hapus community.
     * DELETE /api/communities/{community}
     *
     * Hanya organizer pembuat atau admin.
     */
    public function destroy(Request $request, Community $community)
    {
        $user = $request->user();

        if ($user->role !== 'ADMIN' && $community->organizer_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk menghapus community ini.'
            ], 403);
        }

        $community->delete();

        return response()->json([
            'message' => 'Community deleted'
        ]);
    }

    /**
     * Join community.
     * POST /api/communities/{community}/join
     */
    public function join(Community $community)
    {
        $userId = auth()->id();

        if ($community->members()->where('user_id', $userId)->exists()) {
            return response()->json([
                'message' => 'Anda sudah bergabung dengan community ini.'
            ], 400);
        }

        $community->members()->attach($userId);
        $community->increment('member_count');

        return response()->json([
            'message' => 'Joined',
            'community' => $community->fresh()
        ]);
    }

    /**
     * Leave community.
     * POST /api/communities/{community}/leave
     */
    public function leave(Community $community)
    {
        $userId = auth()->id();

        if (!$community->members()->where('user_id', $userId)->exists()) {
            return response()->json([
                'message' => 'Anda belum bergabung dengan community ini.'
            ], 400);
        }

        $community->members()->detach($userId);
        $community->decrement('member_count');

        return response()->json([
            'message' => 'Left',
            'community' => $community->fresh()
        ]);
    }

    /**
     * GET /api/organizer/communities
     */
    public function organizerCommunities(Request $request)
    {
        $user = $request->user();

        $communities = Community::where('organizer_id', $user->id)
            ->with(['organizer', 'category'])
            ->withCount('events')
            ->paginate(10);

        return response()->json($communities);
    }
}