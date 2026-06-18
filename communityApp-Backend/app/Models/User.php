<?php

namespace App\Models;

use Illuminate\Foundation\Auth\User as Authenticatable;
use Laravel\Sanctum\HasApiTokens;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Notifications\Notifiable;
use App\Models\Event;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, Notifiable;

    public const ROLE_SUPER_ADMIN = 'SUPER_ADMIN';
    public const ROLE_ADMIN = 'ADMIN';
    public const ROLE_ORGANIZER = 'ORGANIZER';
    public const ROLE_USER = 'USER';

    public function isSuperAdmin(): bool
    {
        return $this->role === self::ROLE_SUPER_ADMIN;
    }

    public function isAdmin(): bool
    {
        return $this->role === self::ROLE_ADMIN || $this->role === self::ROLE_SUPER_ADMIN;
    }

    public function isOrganizer(): bool
    {
        return $this->role === self::ROLE_ORGANIZER || $this->role === self::ROLE_ADMIN || $this->role === self::ROLE_SUPER_ADMIN;
    }

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

    protected $appends = [
    ];

    public function getCommunitiesCountAttribute()
    {
        if ($this->isOrganizer()) {
            return $this->organizedCommunities()->count();
        }
        return $this->communities()->count();
    }

    public function getEventsCountAttribute()
    {
        if ($this->isOrganizer()) {
            $communityIds = $this->organizedCommunities()->pluck('id');
            return Event::whereIn('community_id', $communityIds)->count();
        }
        return $this->eventRegistrations()
            ->whereIn('status', ['REGISTERED', 'ATTENDED'])
            ->count();
    }

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
