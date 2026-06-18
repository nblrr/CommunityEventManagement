<?php

return [
    /*
    |--------------------------------------------------------------------------
    | Performance and Caching Configuration
    |--------------------------------------------------------------------------
    |
    | These values control the TTL (Time To Live) for various cached data
    | to improve application performance.
    |
    */

    'admin_stats_ttl' => env('ADMIN_STATS_CACHE_TTL', 300),
    'upcoming_events_ttl' => env('UPCOMING_EVENTS_CACHE_TTL', 600),
    'categories_ttl' => null, // null means Cache::rememberForever
];
