<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Support\Str;
use Exception;

class MediaUploadController extends Controller
{
    /**
     * Upload generic file to Supabase storage.
     * POST /api/upload
     */
    public function upload(Request $request)
    {
        try {
            $validated = $request->validate([
                'image' => 'required|image|mimes:jpeg,jpg,png,webp|max:5120',
                'type' => 'required|string|in:avatar,community-cover,event-banner,event-gallery',
            ]);

            $file = $request->file('image');
            $type = $validated['type'];

            // Map image type to bucket subfolder
            $folderMap = [
                'avatar' => 'avatars',
                'community-cover' => 'community-covers',
                'event-banner' => 'event-banners',
                'event-gallery' => 'event-gallery',
            ];

            $folder = $folderMap[$type];

            // Generate UUID filename
            $extension = $file->getClientOriginalExtension();
            if (empty($extension)) {
                $extension = 'jpg';
            }
            $filename = Str::uuid() . '.' . $extension;

            // Upload using the s3 disk
            $path = Storage::disk('s3')->putFileAs($folder, $file, $filename);

            // Construct standard public Supabase Storage URL manually to ensure CDN route format
            $supabaseUrl = rtrim(env('SUPABASE_URL', 'https://bxdvutvfrbmfcixpuefm.supabase.co'), '/');
            $bucket = env('AWS_BUCKET', 'community-images');
            $publicUrl = "{$supabaseUrl}/storage/v1/object/public/{$bucket}/{$path}";

            return response()->json([
                'url' => $publicUrl,
                'path' => $path,
                'bucket' => $bucket,
                'type' => $type
            ], 201);

        } catch (Exception $e) {
            // Return user-friendly error, no stack traces
            return response()->json([
                'message' => 'Gagal mengunggah berkas. Silakan coba lagi.'
            ], 500);
        }
    }
}
