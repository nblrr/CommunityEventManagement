<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('event_images', function (Blueprint $table) {

            $table->id();

            $table->foreignId('event_id')
                ->index()
                ->constrained('events')
                ->cascadeOnDelete();

            $table->foreignId('uploaded_by')
                ->nullable()
                ->index()
                ->constrained('users')
                ->nullOnDelete();

            $table->text('image_url');

            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('event_images');
    }
};