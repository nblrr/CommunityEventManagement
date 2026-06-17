<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\TrustedApplication;
use App\Models\User;
use Illuminate\Http\Request;

class TrustedApplicationController extends Controller
{
    /**
     * User mengajukan permohonan menjadi Trusted Organizer.
     * POST /api/trusted-applications
     */
    public function store(Request $request)
    {
        $user = $request->user();

        if ($user->role !== 'ORGANIZER') {
            return response()->json([
                'message' => 'Hanya organizer yang dapat mengajukan trusted application.'
            ], 403);
        }

        $existing = TrustedApplication::where('user_id', $user->id)->first();
        if ($existing) {
            return response()->json([
                'message' => 'Anda sudah memiliki pengajuan.',
                'application' => $existing
            ], 409);
        }

        $validated = $request->validate([
            'community_name' => 'required|string|max:255',
            'reason'         => 'required|string',
            'experience'     => 'nullable|string',
        ]);

        $application = TrustedApplication::create([
            'user_id'        => $user->id,
            'community_name' => $validated['community_name'],
            'reason'         => $validated['reason'],
            'experience'     => $validated['experience'] ?? null,
            'status'         => 'PENDING',
        ]);

        return response()->json($application, 201);
    }

    /**
     * User melihat status pengajuannya sendiri.
     * GET /api/trusted-applications/me
     */
    public function myApplication(Request $request)
    {
        $application = TrustedApplication::where(
            'user_id', $request->user()->id
        )->first();

        if (!$application) {
            return response()->json([
                'message' => 'Belum ada pengajuan.'
            ], 404);
        }

        return response()->json($application);
    }

    /**
     * Admin melihat seluruh pengajuan.
     * GET /api/admin/trusted-applications
     */
    public function index()
    {
        $applications = TrustedApplication::with('user')
            ->latest('applied_at')
            ->paginate(10);

        return response()->json($applications);
    }

    /**
     * Admin approve pengajuan.
     * POST /api/admin/trusted-applications/{id}/approve
     */
    public function approve(Request $request, $id)
    {
        $trustedApplication = TrustedApplication::findOrFail($id);

        if ($trustedApplication->status !== 'PENDING') {
            return response()->json([
                'message' => 'Pengajuan sudah diproses sebelumnya.'
            ], 409);
        }

        $validated = $request->validate([
            'admin_notes' => 'nullable|string',
        ]);

        $trustedApplication->update([
            'status'      => 'APPROVED',
            'reviewed_by' => $request->user()->id,
            'admin_notes' => $validated['admin_notes'] ?? null,
            'reviewed_at' => now(),
        ]);

        // Set user sebagai trusted organizer
        $trustedApplication->user->update([
            'is_trusted' => true,
        ]);

        return response()->json([
            'message'     => 'Pengajuan disetujui.',
            'application' => $trustedApplication->fresh()->load('user'),
        ]);
    }

    /**
     * Admin reject pengajuan.
     * POST /api/admin/trusted-applications/{id}/reject
     */
    public function reject(Request $request, $id)
    {
        $trustedApplication = TrustedApplication::findOrFail($id);

        if ($trustedApplication->status !== 'PENDING') {
            return response()->json([
                'message' => 'Pengajuan sudah diproses sebelumnya.'
            ], 409);
        }

        $validated = $request->validate([
            'admin_notes' => 'nullable|string',
            'notes'       => 'nullable|string',
        ]);

        $adminNotes = $validated['admin_notes'] ?? $validated['notes'] ?? $request->input('notes') ?? $request->input('admin_notes');

        $trustedApplication->update([
            'status'      => 'REJECTED',
            'reviewed_by' => $request->user()->id,
            'admin_notes' => $adminNotes,
            'reviewed_at' => now(),
        ]);

        return response()->json([
            'message'     => 'Pengajuan ditolak.',
            'application' => $trustedApplication->fresh()->load('user'),
        ]);
    }
}
