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
        'end_time',
        'location',
        'is_online',
        'max_attendees',
        'attendee_count',
        'cover_image_url',
        'status'
    ];

    protected $appends = [
        'calculated_status',
        'average_rating',
        'review_count',
        'category_name',
        'organizer_name',
        'organizer_id',
        'is_organizer_trusted',
        'organizer_image_url',
        'rating'
    ];

    /**
     * Override the 'status' attribute getter so that whenever the model
     * is serialized to JSON (or accessed via $event->status), it returns
     * the time-based calculated status instead of the raw DB column.
     */
    public function getStatusAttribute($value)
    {
        return $this->getCalculatedStatusAttribute();
    }

    public function getCalculatedStatusAttribute()
    {
        $now = now();
        $start = \Carbon\Carbon::parse($this->event_date . ' ' . $this->event_time);

        if ($this->end_time) {
            $end = \Carbon\Carbon::parse($this->event_date . ' ' . $this->end_time);
            if ($end->lt($start)) {
                $end->addDay();
            }
        } else {
            $end = (clone $start)->addHours(2);
        }

        if ($now->lt($start)) {
            return 'UPCOMING';
        } elseif ($now->between($start, $end)) {
            return 'ONGOING';
        } else {
            return 'COMPLETED';
        }
    }

    public function getAverageRatingAttribute()
    {
        if ($this->relationLoaded('ratings')) {
            return round($this->ratings->avg('rating') ?: 0, 1);
        }
        return 0.0;
    }

    public function getReviewCountAttribute()
    {
        if ($this->relationLoaded('ratings')) {
            return $this->ratings->count();
        }
        return 0;
    }

    public function getCategoryNameAttribute()
    {
        if ($this->relationLoaded('category') && $this->category) {
            return $this->category->name;
        }
        return null;
    }

    public function getOrganizerNameAttribute()
    {
        if ($this->relationLoaded('community') && $this->community && $this->community->relationLoaded('organizer') && $this->community->organizer) {
            return $this->community->organizer->name;
        }
        return null;
    }

    public function getOrganizerIdAttribute()
    {
        if ($this->relationLoaded('community') && $this->community && $this->community->relationLoaded('organizer') && $this->community->organizer) {
            return $this->community->organizer->id;
        }
        return 0;
    }

    public function getIsOrganizerTrustedAttribute()
    {
        if ($this->relationLoaded('community') && $this->community && $this->community->relationLoaded('organizer') && $this->community->organizer) {
            return (bool)$this->community->organizer->is_trusted;
        }
        return false;
    }

    public function getOrganizerImageUrlAttribute()
    {
        if ($this->relationLoaded('community') && $this->community && $this->community->relationLoaded('organizer') && $this->community->organizer) {
            return $this->community->organizer->avatar_url;
        }
        return null;
    }

    public function getRatingAttribute()
    {
        return $this->getAverageRatingAttribute();
    }

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
