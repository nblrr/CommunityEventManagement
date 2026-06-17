<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;
use Laravel\Sanctum\HasApiTokens;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Notifications\Notifiable;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    protected $fillable = [
        'name',
        'email',
        'password',
        'phone_number',
        'birth_date',
        'gender',
        'bio',
        'avatar_url',
        'role',
        'is_blocked',
        'is_trusted'
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected $casts = [
        'email_verified_at' => 'datetime',
        'birth_date' => 'date',
        'is_blocked' => 'boolean',
        'is_trusted' => 'boolean',
    ];

    public function organizedCommunities()
    {
        return $this->hasMany(
            Community::class,
            'organizer_id'
        );
    }

    public function communities()
    {
        return $this->belongsToMany(
            Community::class,
            'community_members'
        )->withPivot('role', 'joined_at');
    }

    public function eventRegistrations()
    {
        return $this->hasMany(
            EventRegistration::class
        );
    }

    public function eventRatings()
    {
        return $this->hasMany(
            EventRating::class
        );
    }

    public function forumMessages()
    {
        return $this->hasMany(
            ForumMessage::class,
            'sender_id'
        );
    }

    public function trustedApplication()
    {
        return $this->hasOne(
            TrustedApplication::class
        );
    }

    public function notifications()
    {
        return $this->hasMany(
            Notification::class
        );
    }
}