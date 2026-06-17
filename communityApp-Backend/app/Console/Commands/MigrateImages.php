<?php

namespace App\Console\Commands;

use Illuminate\Console\Command;
use App\Models\User;
use App\Models\Community;
use App\Models\Event;
use Illuminate\Support\Facades\Storage;
use Exception;

class MigrateImages extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'db:migrate-images';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Migrate local file system images to Supabase and sanitize content:// URIs';

    /**
     * Execute the console command.
     */
    public function handle()
    {
        $this->info("Starting image storage migration...");

        // 1. Migrate user avatars pointing to localhost/storage
        $users = User::whereNotNull('avatar_url')->get();
        $this->info("Scanning " . $users->count() . " users for avatar migrations...");

        foreach ($users as $user) {
            $url = $user->avatar_url;
            if (str_contains($url, 'localhost') && str_contains($url, 'storage/avatars')) {
                // Get filename from local disk path
                $filename = basename($url);
                $localPath = 'avatars/' . $filename;

                if (Storage::disk('public')->exists($localPath)) {
                    try {
                        $fileContents = Storage::disk('public')->get($localPath);
                        Storage::disk('s3')->put($localPath, $fileContents);

                        $supabaseUrl = rtrim(env('SUPABASE_URL', 'https://bxdvutvfrbmfcixpuefm.supabase.co'), '/');
                        $bucket = env('AWS_BUCKET', 'community-images');
                        $newUrl = "{$supabaseUrl}/storage/v1/object/public/{$bucket}/{$localPath}";

                        $user->update(['avatar_url' => $newUrl]);
                        $this->info("Migrated avatar for user: {$user->name} to Supabase.");
                        
                        // Clean up local file
                        Storage::disk('public')->delete($localPath);
                    } catch (Exception $e) {
                        $this->error("Failed to migrate avatar for user ID {$user->id}: " . $e->getMessage());
                    }
                } else {
                    // Local file missing, substitute with public fallback dicebear
                    $newUrl = "https://api.dicebear.com/7.x/adventurer/svg?seed=" . urlencode($user->name);
                    $user->update(['avatar_url' => $newUrl]);
                    $this->warn("Local avatar file missing. Reset to public placeholder for user: {$user->name}");
                }
            }
        }

        // 2. Sanitize Communities cover URLs starting with content://
        $communities = Community::all();
        $this->info("Scanning " . $communities->count() . " communities for invalid URIs...");
        foreach ($communities as $community) {
            $url = $community->cover_image_url;
            if ($url && (str_starts_with($url, 'content://') || str_starts_with($url, 'file://'))) {
                // Update to default unsplash fallback image
                $community->update([
                    'cover_image_url' => 'https://images.unsplash.com/photo-1517245386807-bb43f82c33c4'
                ]);
                $this->info("Sanitized cover image for community: {$community->name}");
            }
        }

        // 3. Sanitize Events cover URLs starting with content://
        $events = Event::all();
        $this->info("Scanning " . $events->count() . " events for invalid URIs...");
        foreach ($events as $event) {
            $url = $event->cover_image_url;
            if ($url && (str_starts_with($url, 'content://') || str_starts_with($url, 'file://'))) {
                // Update to default unsplash fallback image
                $event->update([
                    'cover_image_url' => 'https://images.unsplash.com/photo-1517694712202-14dd9538aa97'
                ]);
                $this->info("Sanitized cover image for event: {$event->title}");
            }
        }

        $this->info("Image storage migration completed successfully!");
    }
}
