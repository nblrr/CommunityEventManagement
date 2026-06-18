<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Notification;
use Illuminate\Http\Request;

class NotificationController extends Controller
{
    /**
     * Daftar notifikasi user yang sedang login (paginated).
     * GET /api/notifications
     */
    public function index(Request $request)
    {
        $notifications = $request->user()
            ->notifications()
            ->latest()
            ->paginate(10);

        return response()->json($notifications);
    }

    /**
     * Mark notifikasi sebagai sudah dibaca.
     * POST /api/notifications/{id}/read
     *
     * Hanya pemilik notifikasi yang bisa mark as read.
     */
    public function markAsRead(Request $request, $id)
    {
        $notification = Notification::findOrFail($id);

        if ($notification->user_id !== $request->user()->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk mengakses notifikasi ini.'
            ], 403);
        }

        $notification->update([
            'is_read' => true
        ]);

        return response()->json([
            'message' => 'Read'
        ]);
    }

    /**
     * Update FCM Token for the authenticated user.
     * POST /api/notifications/fcm-token
     */
    public function updateFcmToken(Request $request)
    {
        $request->validate([
            'fcm_token' => 'required|string'
        ]);

        $request->user()->update([
            'fcm_token' => $request->fcm_token
        ]);

        return response()->json([
            'message' => 'FCM Token updated successfully.'
        ]);
    }
}