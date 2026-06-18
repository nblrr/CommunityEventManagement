<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\User;
use Illuminate\Support\Facades\Hash;

class AuthController extends Controller
{
    /**
     * Register user baru.
     * POST /api/register
     */
    public function register(Request $request)
    {
        $validated = $request->validate([
            'name'     => 'required|string|max:255',
            'email'    => 'required|email|unique:users',
            'password' => 'required|min:8'
        ]);

        $user = User::create([
            'name'     => $validated['name'],
            'email'    => $validated['email'],
            'password' => Hash::make($validated['password'])
        ]);

        $user = $user->fresh();
        $user->append(['communities_count', 'events_count']);
        $token = $user->createToken('android')->plainTextToken;

        return response()->json([
            'user'  => $user,
            'token' => $token
        ], 201);
    }

    /**
     * Login user.
     * POST /api/login
     */
    public function login(Request $request)
    {
        $validated = $request->validate([
            'email'    => 'required|email',
            'password' => 'required'
        ]);

        $user = User::where(
            'email',
            $validated['email']
        )->first();

        if (
            !$user ||
            !Hash::check(
                $validated['password'],
                $user->password
            )
        ) {
            return response()->json([
                'message' => 'Email atau password salah'
            ], 401);
        }

        if ($user->is_blocked) {
            return response()->json([
                'message' => 'Akun Anda telah diblokir.'
            ], 403);
        }

        $user->append(['communities_count', 'events_count']);
        $token = $user->createToken('android')->plainTextToken;

        return response()->json([
            'user'  => $user,
            'token' => $token
        ]);
    }

    /**
     * Logout user (hapus current token).
     * POST /api/logout
     */
    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();

        return response()->json([
            'message' => 'Logout berhasil'
        ]);
    }

    /**
     * Get current user data.
     * GET /api/user
     */
    public function me(Request $request)
    {
        $user = $request->user();
        $user->append(['communities_count', 'events_count']);
        return response()->json($user);
    }

    /**
     * User menjadi organizer.
     * POST /api/become-organizer
     *
     * Mengubah role user dari USER menjadi ORGANIZER.
     */
    public function becomeOrganizer(Request $request)
    {
        $user = $request->user();

        if ($user->role === 'ORGANIZER') {
            return response()->json([
                'message' => 'Anda sudah menjadi organizer.'
            ], 409);
        }

        if ($user->isAdmin()) {
            return response()->json([
                'message' => 'Admin tidak perlu menjadi organizer.'
            ], 409);
        }

        $user->update(['role' => 'ORGANIZER']);

        $freshUser = $user->fresh();
        $freshUser->append(['communities_count', 'events_count']);
        return response()->json([
            'message' => 'Anda sekarang menjadi organizer.',
            'user'    => $freshUser,
        ]);
    }
}