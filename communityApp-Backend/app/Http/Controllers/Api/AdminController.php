<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Community;
use App\Models\Event;
use App\Models\User;
use App\Models\TrustedApplication;
use Illuminate\Http\Request;

class AdminController extends Controller
{
    /**
     * Dashboard statistik untuk admin.
     * GET /api/admin/dashboard
     */
    public function dashboard()
    {
        return response()->json([
            'total_users'        => User::count(),
            'total_communities'  => Community::count(),
            'total_events'       => Event::count(),
            'total_organizers'   => User::where('role', 'ORGANIZER')->count(),
            'trusted_organizers' => User::where('is_trusted', true)->count(),
            'blocked_users'      => User::where('is_blocked', true)->count(),
            'pending_trusted_applications' => TrustedApplication::where('status', 'PENDING')->count(),
        ]);
    }

    /**
     * Daftar seluruh user.
     * GET /api/admin/users
     */
    public function users()
    {
        $users = User::latest()->paginate(10);

        return response()->json($users);
    }

    /**
     * Block user.
     * POST /api/admin/users/{user}/block
     */
    public function blockUser(User $user)
    {
        if ($user->role === 'ADMIN') {
            return response()->json([
                'message' => 'Tidak dapat memblokir admin.'
            ], 403);
        }

        $user->update(['is_blocked' => true]);

        return response()->json([
            'message' => 'User berhasil diblokir.',
            'user'    => $user->fresh(),
        ]);
    }

    /**
     * Unblock user.
     * POST /api/admin/users/{user}/unblock
     */
    public function unblockUser(User $user)
    {
        $user->update(['is_blocked' => false]);

        return response()->json([
            'message' => 'User berhasil di-unblock.',
            'user'    => $user->fresh(),
        ]);
    }
}
