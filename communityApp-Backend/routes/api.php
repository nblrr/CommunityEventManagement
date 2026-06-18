<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\ProfileController;
use App\Http\Controllers\Api\CategoryController;
use App\Http\Controllers\Api\CommunityController;
use App\Http\Controllers\Api\EventController;
use App\Http\Controllers\Api\EventRatingController;
use App\Http\Controllers\Api\EventImageController;
use App\Http\Controllers\Api\ForumController;
use App\Http\Controllers\Api\NotificationController;
use App\Http\Controllers\Api\TrustedApplicationController;
use App\Http\Controllers\Api\AdminController;
use App\Http\Controllers\Api\SearchController;
use App\Http\Controllers\Api\MediaUploadController;

// ============================================================
// PUBLIC ROUTES (tanpa auth)
// ============================================================

Route::post('/register', [AuthController::class, 'register']);
Route::post('/login', [AuthController::class, 'login']);

// ============================================================
// PROTECTED ROUTES (auth:sanctum)
// ============================================================

Route::middleware(['auth:sanctum', 'block.check'])->group(function () {

    // -------------------- AUTH --------------------
    Route::post('/logout', [AuthController::class, 'logout']);
    Route::get('/user', [AuthController::class, 'me']);
    Route::post('/become-organizer', [AuthController::class, 'becomeOrganizer']);

    // -------------------- PROFILE --------------------
    Route::get('/profile', [ProfileController::class, 'profile']);
    Route::put('/profile', [ProfileController::class, 'updateProfile']);
    Route::post('/profile/avatar', [ProfileController::class, 'updateAvatar']);
    Route::post('/upload', [MediaUploadController::class, 'upload']);

    // -------------------- CATEGORIES --------------------
    Route::get('/categories', [CategoryController::class, 'index']);
    Route::get('/search', [SearchController::class, 'search']);

    // -------------------- COMMUNITIES --------------------
    Route::get('/my-communities', [CommunityController::class, 'myCommunities']);
    Route::get('/communities', [CommunityController::class, 'index']);
    Route::get('/communities/{community}', [CommunityController::class, 'show']);
    Route::post('/communities/{community}/join', [CommunityController::class, 'join']);
    Route::post('/communities/{community}/leave', [CommunityController::class, 'leave']);

    // Community CRUD (organizer/admin only untuk create)
    Route::middleware('organizer')->group(function () {
        Route::post('/communities', [CommunityController::class, 'store']);
        Route::get('/organizer/communities', [CommunityController::class, 'organizerCommunities']);
        Route::get('/organizer/events', [EventController::class, 'organizerEvents']);
    });

    // Update & Delete — authorization check ada di controller
    Route::put('/communities/{community}', [CommunityController::class, 'update']);
    Route::patch('/communities/{community}', [CommunityController::class, 'update']);
    Route::delete('/communities/{community}', [CommunityController::class, 'destroy']);

    // -------------------- EVENTS --------------------
    Route::get('/my-events', [EventController::class, 'myEvents']);
    Route::get('/upcoming-events', [EventController::class, 'upcomingEvents']);
    Route::get('/recommended-events', [EventController::class, 'recommendedEvents']);
    Route::get('/events', [EventController::class, 'index']);
    Route::get('/events/{event}', [EventController::class, 'show']);
    Route::post('/events/{event}/register', [EventController::class, 'register']);
    Route::post('/events/{event}/cancel', [EventController::class, 'cancel']);

    // Event CRUD (organizer/admin only untuk create)
    Route::middleware('organizer')->group(function () {
        Route::post('/events', [EventController::class, 'store']);
        Route::get('/events/{event}/participants', [EventController::class, 'participants']);
    });

    // Update & Delete — authorization check ada di controller
    Route::put('/events/{event}', [EventController::class, 'update']);
    Route::patch('/events/{event}', [EventController::class, 'update']);
    Route::delete('/events/{event}', [EventController::class, 'destroy']);

    // -------------------- EVENT RATINGS --------------------
    Route::get('/events/{event}/ratings', [EventRatingController::class, 'index']);
    Route::post('/events/{event}/ratings', [EventRatingController::class, 'store']);

    // -------------------- EVENT IMAGES --------------------
    Route::get('/events/{event}/images', [EventImageController::class, 'index']);
    Route::post('/events/{event}/images', [EventImageController::class, 'store']);

    // -------------------- FORUM --------------------
    Route::get('/communities/{community}/messages', [ForumController::class, 'index']);
    Route::post('/communities/{community}/messages', [ForumController::class, 'store']);
    Route::delete('/forum-messages/{message}', [ForumController::class, 'destroy']);

    // -------------------- NOTIFICATIONS --------------------
    Route::get('/notifications', [NotificationController::class, 'index']);
    Route::post('/notifications/{id}/read', [NotificationController::class, 'markAsRead']);

    // -------------------- TRUSTED APPLICATIONS (User) --------------------
    Route::post('/trusted-applications', [TrustedApplicationController::class, 'store']);
    Route::get('/trusted-applications/me', [TrustedApplicationController::class, 'myApplication']);

    // ============================================================
    // ADMIN ROUTES
    // ============================================================

    Route::middleware('admin')->prefix('admin')->group(function () {

        // Dashboard
        Route::get('/dashboard', [AdminController::class, 'dashboard']);

        // User Management
        Route::get('/users', [AdminController::class, 'users']);
        Route::post('/users', [AdminController::class, 'createUser']);
        Route::delete('/users/{user}', [AdminController::class, 'deleteUser']);
        Route::post('/users/{user}/role', [AdminController::class, 'updateRole']);
        Route::post('/users/{user}/revoke-trusted', [AdminController::class, 'revokeTrusted']);
        Route::post('/users/{user}/block', [AdminController::class, 'blockUser']);
        Route::post('/users/{user}/unblock', [AdminController::class, 'unblockUser']);

        // Trusted Applications Review
        Route::get('/trusted-applications', [TrustedApplicationController::class, 'index']);
        Route::post('/trusted-applications/{id}/approve', [TrustedApplicationController::class, 'approve']);
        Route::post('/trusted-applications/{id}/reject', [TrustedApplicationController::class, 'reject']);
    });
});