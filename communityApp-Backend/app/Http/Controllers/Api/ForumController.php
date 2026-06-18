<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Community;
use App\Models\ForumMessage;
use Illuminate\Http\Request;

class ForumController extends Controller
{
    public function index(Community $community)
    {
        $user = auth()->user();

        // Check if user is member, organizer or admin
        $isMember = $community->members()->where('user_id', $user->id)->exists();
        if (!$user->isAdmin() && $community->organizer_id !== $user->id && !$isMember) {
            return response()->json([
                'message' => 'Anda harus bergabung dengan community ini untuk melihat forum.'
            ], 403);
        }

        $messages = $community->forumMessages()
            ->with('sender')
            ->latest()
            ->paginate(30);

        return response()->json($messages);
    }

    public function store(Request $request, Community $community)
    {
        $user = $request->user();

        // Check if user is member, organizer or admin
        $isMember = $community->members()->where('user_id', $user->id)->exists();
        if (!$user->isAdmin() && $community->organizer_id !== $user->id && !$isMember) {
            return response()->json([
                'message' => 'Anda harus bergabung dengan community ini untuk mengirim pesan.'
            ], 403);
        }

        $validated = $request->validate([
            'message' => 'required|string|max:1000'
        ]);

        $message = ForumMessage::create([
            'community_id' => $community->id,
            'sender_id'    => $user->id,
            'message'      => $validated['message']
        ]);

        // Notify other community members
        $members = $community->members()->where('users.id', '!=', $user->id)->pluck('users.id')->toArray();
        if ($community->organizer_id !== $user->id && !in_array($community->organizer_id, $members)) {
            $members[] = $community->organizer_id;
        }

        $preview = strlen($message->message) > 60 ? substr($message->message, 0, 60) . '...' : $message->message;
        foreach ($members as $memberId) {
            \App\Models\Notification::send(
                $memberId,
                $community->name,
                "{$user->name}: {$preview}",
                'FORUM_MESSAGE',
                $community->id,
                'COMMUNITY'
            );
        }

        return response()->json($message->load('sender'), 201);
    }

    public function destroy(ForumMessage $message)
    {
        $user = auth()->user();

        if (!$user->isAdmin() && $message->sender_id !== $user->id) {
            return response()->json([
                'message' => 'Anda tidak memiliki izin untuk menghapus pesan ini.'
            ], 403);
        }

        $message->delete();

        return response()->json([
            'message' => 'Pesan berhasil dihapus'
        ]);
    }
}