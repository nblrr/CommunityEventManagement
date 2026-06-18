<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Category;
use Illuminate\Support\Facades\Cache;

class CategoryController extends Controller
{
    public function index()
    {
        $ttl = config('performance.categories_ttl');

        if (is_null($ttl)) {
            $categories = Cache::rememberForever('categories_list', function () {
                return Category::all();
            });
        } else {
            $categories = Cache::remember('categories_list', $ttl, function () {
                return Category::all();
            });
        }

        // If categories are empty, clear cache to avoid freezing an empty database state forever
        if ($categories->isEmpty()) {
            Cache::forget('categories_list');
        }

        return response()->json($categories);
    }
}

