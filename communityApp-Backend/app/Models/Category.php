<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Category extends Model
{
    protected $fillable = [
        'name',
        'icon'
    ];

    public function communities()
    {
        return $this->hasMany(
            Community::class
        );
    }

    public function events()
    {
        return $this->hasMany(
            Event::class
        );
    }
}