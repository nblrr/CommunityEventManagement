<?php

namespace App\Services;

use Google\Auth\Credentials\ServiceAccountCredentials;
use Google\Auth\HttpHandler\HttpHandlerFactory;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Log;

class FcmService
{
    protected $credentialsPath;

    public function __construct()
    {
        $this->credentialsPath = base_path('firebase-service-account.json');
    }

    /**
     * Get OAuth2 Access Token using Google Auth Admin SDK json credentials.
     */
    public function getAccessToken(): ?string
    {
        if (!file_exists($this->credentialsPath)) {
            Log::error("FCM: firebase-service-account.json not found at " . $this->credentialsPath);
            return null;
        }

        try {
            $scopes = ['https://www.googleapis.com/auth/firebase.messaging'];
            $credentials = new ServiceAccountCredentials($scopes, $this->credentialsPath);
            
            // Fetch the token
            $token = $credentials->fetchAuthToken(HttpHandlerFactory::build());
            
            return $token['access_token'] ?? null;
        } catch (\Exception $e) {
            Log::error("FCM: Failed to fetch Access Token: " . $e->getMessage());
            return null;
        }
    }

    /**
     * Send Push Notification to a specific FCM token.
     */
    public function sendNotification(string $fcmToken, string $title, string $body, array $data = []): bool
    {
        $accessToken = $this->getAccessToken();
        if (!$accessToken) {
            Log::error("FCM: Cannot send notification, Access Token is null.");
            return false;
        }

        // Parse project_id from service account json
        $credentialsJson = json_decode(file_get_contents($this->credentialsPath), true);
        $projectId = $credentialsJson['project_id'] ?? null;

        if (!$projectId) {
            Log::error("FCM: Project ID not found in firebase-service-account.json.");
            return false;
        }

        $url = "https://fcm.googleapis.com/v1/projects/{$projectId}/messages:send";

        try {
            // Merge custom payload data into FCM message structure
            $payload = [
                'message' => [
                    'token' => $fcmToken,
                    'notification' => [
                        'title' => $title,
                        'body' => $body,
                    ],
                ]
            ];

            if (!empty($data)) {
                // FCM data payload values must all be strings
                $payload['message']['data'] = array_map('strval', $data);
            }

            $response = Http::withToken($accessToken)->post($url, $payload);

            if ($response->successful()) {
                Log::info("FCM: Notification sent successfully to token: " . substr($fcmToken, 0, 15) . "...");
                return true;
            } else {
                Log::error("FCM: Failed to send notification. Response: " . $response->body());
                return false;
            }
        } catch (\Exception $e) {
            Log::error("FCM: Exception while sending notification: " . $e->getMessage());
            return false;
        }
    }
}
