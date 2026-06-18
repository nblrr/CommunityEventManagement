<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        $driver = \Illuminate\Support\Facades\DB::getDriverName();
        if ($driver === 'pgsql') {
            \Illuminate\Support\Facades\DB::statement('ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check');
            \Illuminate\Support\Facades\DB::statement("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'ORGANIZER', 'USER'))");
        } elseif ($driver === 'mysql') {
            \Illuminate\Support\Facades\DB::statement("ALTER TABLE users MODIFY COLUMN role ENUM('SUPER_ADMIN', 'ADMIN', 'ORGANIZER', 'USER') DEFAULT 'USER'");
        }
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        $driver = \Illuminate\Support\Facades\DB::getDriverName();
        if ($driver === 'pgsql') {
            \Illuminate\Support\Facades\DB::statement('ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check');
            \Illuminate\Support\Facades\DB::statement("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('ADMIN', 'ORGANIZER', 'USER'))");
        } elseif ($driver === 'mysql') {
            \Illuminate\Support\Facades\DB::statement("ALTER TABLE users MODIFY COLUMN role ENUM('ADMIN', 'ORGANIZER', 'USER') DEFAULT 'USER'");
        }
    }
};
