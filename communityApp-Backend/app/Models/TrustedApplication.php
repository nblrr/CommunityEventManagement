<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class TrustedApplication extends Model
{
    protected $fillable = [
        'user_id',
        'community_name',
        'reason',
        'experience',
        'status',
        'reviewed_by',
        'admin_notes',
        'applied_at',
        'reviewed_at'
    ];

    public function user()
    {
        return $this->belongsTo(
            User::class
        );
    }

    public function reviewer()
    {
        return $this->belongsTo(
            User::class,
            'reviewed_by'
        );
    }
}