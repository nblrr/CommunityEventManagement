<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;

class ProfileController extends Controller
{
    /**
     * Get current user profile.
     * GET /api/profile
     */
    public function profile(Request $request)
    {
        return response()->json(
            $request->user()
        );
    }

    /**
     * Update profile data.
     * PUT /api/profile
     */
    public function updateProfile(Request $request)
    {
        $user = $request->user();

        $validated = $request->validate([
            'name'         => 'sometimes|string|max:255',
            'phone_number' => 'nullable|string|max:30',
            'gender'       => 'nullable|string|max:20',
            'bio'          => 'nullable|string',
            'birth_date'   => 'nullable|date',
        ]);

        $user->update($validated);

        return response()->json($user->fresh());
    }

    /**
     * Update avatar URL.
     * POST /api/profile/avatar
     */
    public function updateAvatar(Request $request)
    {
        $request->validate([
            'avatar' => 'required|image|mimes:jpg,jpeg,png,webp|max:2048'
        ]);

        $user = $request->user();

        // Store avatar file
        $path = $request->file('avatar')->store('avatars', 'public');

        $user->update([
            'avatar_url' => asset('storage/' . $path)
        ]);

        return response()->json($user->fresh());
    }
}