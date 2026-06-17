<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Community extends Model
{
    protected $fillable = [
        'name',
        'description',
        'organizer_id',
        'category_id',
        'status',
        'cover_image_url',
        'member_count'
    ];

    public function organizer()
    {
        return $this->belongsTo(
            User::class,
            'organizer_id'
        );
    }

    public function category()
    {
        return $this->belongsTo(
            Category::class
        );
    }

    public function members()
    {
        return $this->belongsToMany(
            User::class,
            'community_members'
        )->withPivot('role', 'joined_at');
    }

    public function events()
    {
        return $this->hasMany(
            Event::class
        );
    }

    public function forumMessages()
    {
        return $this->hasMany(
            ForumMessage::class
        );
    }
}