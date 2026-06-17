<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class ForumMessage extends Model
{
    use SoftDeletes;

    protected $fillable = [
        'community_id',
        'sender_id',
        'message'
    ];

    public function community()
    {
        return $this->belongsTo(
            Community::class
        );
    }

    public function sender()
    {
        return $this->belongsTo(
            User::class,
            'sender_id'
        );
    }
}