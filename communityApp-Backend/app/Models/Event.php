<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Event extends Model
{
    protected $fillable = [
        'community_id',
        'category_id',
        'title',
        'description',
        'event_date',
        'event_time',
        'location',
        'is_online',
        'max_attendees',
        'attendee_count',
        'cover_image_url',
        'status'
    ];

    public function community()
    {
        return $this->belongsTo(
            Community::class
        );
    }

    public function category()
    {
        return $this->belongsTo(
            Category::class
        );
    }

    public function registrations()
    {
        return $this->hasMany(
            EventRegistration::class
        );
    }

    public function images()
    {
        return $this->hasMany(
            EventImage::class
        );
    }

    public function ratings()
    {
        return $this->hasMany(
            EventRating::class
        );
    }
}