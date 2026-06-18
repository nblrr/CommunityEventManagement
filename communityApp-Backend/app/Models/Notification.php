<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Notification extends Model
{
    protected $fillable = [
        'user_id',
        'title',
        'message',
        'type',
        'is_read',
        'reference_id',
        'reference_type'
    ];

    protected $casts = [
        'is_read' => 'boolean',
    ];

    public function user()
    {
        return $this->belongsTo(
            User::class
        );
    }

    /**
     * Helper to create a database notification and dispatch FCM push notification.
     */
    public static function send(int $userId, string $title, string $message, string $type, ?int $referenceId = null, ?string $referenceType = null)
    {
        $notification = self::create([
            'user_id'        => $userId,
            'title'          => $title,
            'message'        => $message,
            'type'           => $type,
            'is_read'        => false,
            'reference_id'   => $referenceId,
            'reference_type' => $referenceType
        ]);

        $user = User::find($userId);
        if ($user && !empty($user->fcm_token)) {
            \App\Jobs\SendFcmNotificationJob::dispatch(
                $user->fcm_token,
                $title,
                $message,
                [
                    'notification_id' => (string) $notification->id,
                    'type'            => $type,
                    'reference_id'    => (string) $referenceId,
                    'reference_type'  => (string) $referenceType,
                ]
            );
        }

        return $notification;
    }
}