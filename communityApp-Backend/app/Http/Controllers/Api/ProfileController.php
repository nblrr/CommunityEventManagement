<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;

class ProfileController extends Controller
{
    /**
     * Get current user profile.
     * GET /api/profile
     */
    public function profile(Request $request)
    {
        $user = $request->user();
        $user->append(['communities_count', 'events_count']);
        return response()->json($user);
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

        $freshUser = $user->fresh();
        $freshUser->append(['communities_count', 'events_count']);
        return response()->json($freshUser);
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

        // Store avatar file to Supabase via S3 driver
        $file = $request->file('avatar');
        $extension = $file->getClientOriginalExtension();
        if (empty($extension)) {
            $extension = 'jpg';
        }
        $filename = Str::uuid() . '.' . $extension;
        $path = Storage::disk('s3')->putFileAs('avatars', $file, $filename);

        if ($path === false) {
            return response()->json([
                'message' => 'Gagal mengunggah berkas ke penyimpanan cloud.'
            ], 500);
        }

        // Construct standard public Supabase Storage URL
        $supabaseUrl = rtrim(env('SUPABASE_URL', 'https://bxdvutvfrbmfcixpuefm.supabase.co'), '/');
        $bucket = env('AWS_BUCKET', 'community-images');
        $publicUrl = "{$supabaseUrl}/storage/v1/object/public/{$bucket}/{$path}";

        $user->update([
            'avatar_url' => $publicUrl
        ]);

        $freshUser = $user->fresh();
        $freshUser->append(['communities_count', 'events_count']);
        return response()->json($freshUser);
    }
}