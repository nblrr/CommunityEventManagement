<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;

class OrganizerMiddleware
{
    public function handle(Request $request, Closure $next): Response
    {
        $role = $request->user()?->role;

        if (!in_array($role, ['ORGANIZER', 'ADMIN'])) {
            return response()->json([
                'message' => 'Akses ditolak. Hanya organizer atau admin yang diizinkan.'
            ], 403);
        }

        return $next($request);
    }
}
