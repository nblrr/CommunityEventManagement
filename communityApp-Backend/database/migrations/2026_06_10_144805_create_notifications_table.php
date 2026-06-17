<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('notifications', function (Blueprint $table) {

            $table->id();

            $table->foreignId('user_id')
                ->index()
                ->constrained('users')
                ->cascadeOnDelete();

            $table->string('title');

            $table->text('message');

            $table->enum('type', [
                'EVENT',
                'COMMUNITY',
                'TRUSTED_APPLICATION',
                'SYSTEM'
            ]);

            $table->boolean('is_read')
                ->default(false)
                ->index();

            $table->string('reference_type')
                ->nullable();

            $table->unsignedBigInteger('reference_id')
                ->nullable();

            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('notifications');
    }
};