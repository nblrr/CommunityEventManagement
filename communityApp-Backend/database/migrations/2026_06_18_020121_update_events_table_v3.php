<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('events', function (Blueprint $table) {
            $table->time('end_time')->nullable()->after('event_time');

            // Changing enum values in Laravel/MySQL requires different approaches depending on the DB engine.
            // Since we are moving from PAST to COMPLETED, and potentially adding ONGOING if it wasn't there (it was).
        });

        // Use raw SQL to update the enum to include COMPLETED and remove PAST if needed,
        // but it's safer to just add COMPLETED.
        if (DB::getDriverName() === 'mysql') {
            DB::statement("ALTER TABLE events MODIFY COLUMN status ENUM('UPCOMING', 'ONGOING', 'COMPLETED') DEFAULT 'UPCOMING'");
        } elseif (DB::getDriverName() === 'pgsql') {
            DB::statement('ALTER TABLE events DROP CONSTRAINT IF EXISTS events_status_check');
            DB::statement("ALTER TABLE events ADD CONSTRAINT events_status_check CHECK (status IN ('UPCOMING', 'ONGOING', 'COMPLETED'))");
        }
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('events', function (Blueprint $table) {
            $table->dropColumn('end_time');
        });

        if (DB::getDriverName() === 'mysql') {
            DB::statement("ALTER TABLE events MODIFY COLUMN status ENUM('UPCOMING', 'ONGOING', 'PAST') DEFAULT 'UPCOMING'");
        } elseif (DB::getDriverName() === 'pgsql') {
            DB::statement('ALTER TABLE events DROP CONSTRAINT IF EXISTS events_status_check');
            DB::statement("ALTER TABLE events ADD CONSTRAINT events_status_check CHECK (status IN ('UPCOMING', 'ONGOING', 'PAST'))");
        }
    }
};
