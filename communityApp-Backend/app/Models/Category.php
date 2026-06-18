<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Cache;

class Category extends Model
{
    protected $fillable = [
        'name',
        'icon'
    ];

    protected static function booted()
    {
        static::saved(function () {
            Cache::forget('categories_list');
        });

        static::deleted(function () {
            Cache::forget('categories_list');
        });
    }

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