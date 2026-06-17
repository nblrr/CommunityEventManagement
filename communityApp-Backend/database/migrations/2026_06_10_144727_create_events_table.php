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
        Schema::create('events', function (Blueprint $table) {
            $table->id();
            $table->foreignId('community_id')->index()->constrained('communities')->onDelete('cascade');
            $table->foreignId('category_id')->index()->constrained('categories')->onDelete('cascade');
            $table->string('title');
            $table->text('description');
            $table->date('event_date')->index();
            $table->time('event_time');
            $table->string('location');
            $table->boolean('is_online')->default(false);
            $table->integer('max_attendees')->default(0);
            $table->integer('attendee_count')->default(0);
            $table->text('cover_image_url')->nullable();
            $table->enum('status', ['UPCOMING', 'ONGOING', 'PAST'])->default('UPCOMING')->index();
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('events');
    }
};
