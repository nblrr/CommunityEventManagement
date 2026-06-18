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
        Schema::table('communities', function (Blueprint $table) {
            $table->index('name');
            $table->index('status');
        });

        Schema::table('events', function (Blueprint $table) {
            $table->index('title');
            $table->index('location');
        });

        Schema::table('users', function (Blueprint $table) {
            $table->index('name');
            $table->index('role');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('communities', function (Blueprint $table) {
            $table->dropIndex(['name']);
            $table->dropIndex(['status']);
        });

        Schema::table('events', function (Blueprint $table) {
            $table->dropIndex(['title']);
            $table->dropIndex(['location']);
        });

        Schema::table('users', function (Blueprint $table) {
            $table->dropIndex(['name']);
            $table->dropIndex(['role']);
        });
    }
};
