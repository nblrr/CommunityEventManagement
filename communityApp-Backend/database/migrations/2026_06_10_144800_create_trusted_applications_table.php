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
        Schema::create('trusted_applications', function (Blueprint $table) {
            $table->id();
            $table->foreignId('user_id')->constrained('users')->onDelete('cascade');
            $table->string('community_name');
            $table->text('reason');
            $table->text('experience')->nullable();
            $table->enum('status', ['PENDING','APPROVED','REJECTED'])->default('PENDING')->index();
            $table->foreignId('reviewed_by')->nullable()->index()->constrained('users');
            $table->text('admin_notes')->nullable();
            $table->timestamp("applied_at")->useCurrent();
            $table->timestamp('reviewed_at')->nullable();
            $table->timestamps();
            $table->unique('user_id');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('trusted_applications');
    }
};
