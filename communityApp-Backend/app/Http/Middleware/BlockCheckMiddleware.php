<?php
 
namespace App\Http\Middleware;
 
use Closure;
use Illuminate\Http\Request;
use Symfony\Component\HttpFoundation\Response;
 
class BlockCheckMiddleware
{
    /**
     * Handle an incoming request.
     *
     * @param  \Closure(\Illuminate\Http\Request): (\Symfony\Component\HttpFoundation\Response)  $next
     */
    public function handle(Request $request, Closure $next): Response
    {
        $user = $request->user();
 
        if ($user && $user->is_blocked) {
            // Revoke current access token and potentially others
            $user->tokens()->delete();
 
            return response()->json([
                'message' => 'Akun Anda telah dinonaktifkan. Hubungi administrator.'
            ], 403);
        }
 
        return $next($request);
    }
}
