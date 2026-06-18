<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Community;
use App\Models\Event;
use App\Models\User;
use App\Models\TrustedApplication;
use App\Models\EventRegistration;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;

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
            'total_registrations' => EventRegistration::count(),
        ]);
    }

    /**
     * Daftar seluruh user dengan search dan filter.
     * GET /api/admin/users
     */
    public function users(Request $request)
    {
        $query = User::query();

        if ($request->has('search') && $request->search != '') {
            $search = $request->search;
            $query->where(function($q) use ($search) {
                $q->where('name', 'like', "%{$search}%")
                  ->orWhere('email', 'like', "%{$search}%");
            });
        }

        if ($request->has('role') && $request->role != '') {
            $query->where('role', $request->role);
        }

        if ($request->has('status') && $request->status != '') {
            $status = $request->status;
            if ($status === 'blocked') {
                $query->where('is_blocked', true);
            } elseif ($status === 'active') {
                $query->where('is_blocked', false);
            }
        }

        $users = $query->latest()->paginate(10);

        return response()->json($users);
    }

    /**
     * Create user/admin/organizer.
     * POST /api/admin/users
     */
    public function createUser(Request $request)
    {
        $creator = $request->user();

        $validated = $request->validate([
            'name'     => 'required|string|max:255',
            'email'    => 'required|email|unique:users,email',
            'password' => 'required|string|min:8',
            'role'     => 'required|string|in:SUPER_ADMIN,ADMIN,ORGANIZER,USER',
        ]);

        // 1. ADMIN cannot create ADMIN or SUPER_ADMIN
        if ($creator->role === 'ADMIN') {
            if (in_array($validated['role'], ['SUPER_ADMIN', 'ADMIN'])) {
                return response()->json([
                    'message' => 'Admin tidak dapat membuat Admin atau Super Admin.'
                ], 403);
            }
        }

        // 2. Cannot create another SUPER_ADMIN
        if ($validated['role'] === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Tidak dapat membuat Super Admin baru.'
            ], 403);
        }

        $user = User::create([
            'name'       => $validated['name'],
            'email'      => $validated['email'],
            'password'   => Hash::make($validated['password']),
            'role'       => $validated['role'],
            'is_blocked' => false,
            'is_trusted' => false,
        ]);

        return response()->json([
            'message' => 'User berhasil dibuat.',
            'user'    => $user->fresh(),
        ], 201);
    }

    /**
     * Delete user/admin/organizer.
     * DELETE /api/admin/users/{user}
     */
    public function deleteUser(Request $request, User $user)
    {
        $creator = $request->user();

        // 1. SUPER_ADMIN protection: cannot be deleted
        if ($user->id === 1 || $user->role === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Super Admin tidak dapat dihapus.'
            ], 403);
        }

        // 2. Only SUPER_ADMIN can delete users
        if ($creator->role !== 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Hanya Super Admin yang dapat menghapus pengguna.'
            ], 403);
        }

        // Set reviewed_by references to null to avoid constraint violation
        TrustedApplication::where('reviewed_by', $user->id)->update(['reviewed_by' => null]);

        $user->delete();

        return response()->json([
            'message' => 'User berhasil dihapus.'
        ]);
    }

    /**
     * Update user role.
     * POST /api/admin/users/{user}/role
     */
    public function updateRole(Request $request, User $user)
    {
        $creator = $request->user();

        // 1. SUPER_ADMIN protection: cannot be demoted/altered
        if ($user->id === 1 || $user->role === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Role Super Admin tidak dapat diubah.'
            ], 403);
        }

        $validated = $request->validate([
            'role' => 'required|string|in:SUPER_ADMIN,ADMIN,ORGANIZER,USER'
        ]);

        // 2. Only SUPER_ADMIN can change roles
        if ($creator->role !== 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Hanya Super Admin yang dapat mengubah role pengguna.'
            ], 403);
        }

        if ($validated['role'] === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Tidak dapat mengubah role menjadi Super Admin.'
            ], 403);
        }

        $user->update(['role' => $validated['role']]);

        return response()->json([
            'message' => 'Role user berhasil diubah.',
            'user'    => $user->fresh(),
        ]);
    }

    /**
     * Revoke Trusted Organizer status.
     * POST /api/admin/users/{user}/revoke-trusted
     */
    public function revokeTrusted(Request $request, User $user)
    {
        $creator = $request->user();

        // 1. Cannot alter SUPER_ADMIN status
        if ($user->id === 1 || $user->role === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Tidak dapat mengubah status Super Admin.'
            ], 403);
        }

        // 2. ADMIN cannot alter other ADMIN's status (just in case)
        if ($user->role === 'ADMIN' && $creator->role !== 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Hanya Super Admin yang dapat mengubah status Admin.'
            ], 403);
        }

        $user->update(['is_trusted' => false]);

        return response()->json([
            'message' => 'Status trusted berhasil dicabut.',
            'user'    => $user->fresh(),
        ]);
    }

    /**
     * Block user.
     * POST /api/admin/users/{user}/block
     */
    public function blockUser(Request $request, User $user)
    {
        $creator = $request->user();

        // 1. SUPER_ADMIN protection: cannot be blocked
        if ($user->id === 1 || $user->role === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Super Admin tidak dapat diblokir.'
            ], 403);
        }

        // 2. ADMIN cannot block other ADMIN
        if ($user->role === 'ADMIN' && $creator->role !== 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Admin tidak dapat memblokir Admin lain.'
            ], 403);
        }

        $user->update(['is_blocked' => true]);

        // Revoke all Sanctum tokens
        $user->tokens()->delete();

        return response()->json([
            'message' => 'User berhasil diblokir.',
            'user'    => $user->fresh(),
        ]);
    }

    /**
     * Unblock user.
     * POST /api/admin/users/{user}/unblock
     */
    public function unblockUser(Request $request, User $user)
    {
        $creator = $request->user();

        // 1. SUPER_ADMIN protection
        if ($user->id === 1 || $user->role === 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Super Admin tidak dapat diblokir.'
            ], 403);
        }

        // 2. ADMIN cannot unblock other ADMIN (just in case)
        if ($user->role === 'ADMIN' && $creator->role !== 'SUPER_ADMIN') {
            return response()->json([
                'message' => 'Admin tidak dapat mengelola Admin lain.'
            ], 403);
        }

        $user->update(['is_blocked' => false]);

        return response()->json([
            'message' => 'User berhasil di-unblock.',
            'user'    => $user->fresh(),
        ]);
    }
}
