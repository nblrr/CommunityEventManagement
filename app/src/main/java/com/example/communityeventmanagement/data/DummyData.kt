package com.example.communityeventmanagement.data

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String,
    val attendeeCount: Int,
    val emoji: String
)

data class ForumMessage(
    val sender: String,
    val message: String,
    val time: String,
    val avatarInitials: String
)

object DummyData {
    val events = listOf(
        Event(
            id = 1,
            title = "Tech Meetup Jogja 2025",
            description = "Kumpul komunitas developer membahas tren teknologi terbaru: AI, Kotlin Multiplatform, dan lebih banyak lagi.",
            date = "28 Apr 2025",
            location = "Kampus UGM, Yogyakarta",
            category = "Technology",
            attendeeCount = 128,
            emoji = "💻"
        ),
        Event(
            id = 2,
            title = "Workshop UI/UX Design",
            description = "Belajar desain antarmuka langsung dari para ahli. Dari Figma hingga prototyping, semua dibahas tuntas.",
            date = "10 Mei 2025",
            location = "Co-working Space Kota Baru",
            category = "Design",
            attendeeCount = 64,
            emoji = "🎨"
        ),
        Event(
            id = 3,
            title = "Startup Pitching Night",
            description = "Kesempatan emas mempresentasikan ide startup ke investor dan mentor berpengalaman.",
            date = "20 Mei 2025",
            location = "Hotel Tentrem, Yogyakarta",
            category = "Business",
            attendeeCount = 200,
            emoji = "🚀"
        ),
        Event(
            id = 4,
            title = "Community Clean Code Day",
            description = "Workshop hands-on refactoring kode, clean architecture, dan best practices bersama senior engineers.",
            date = "05 Jun 2025",
            location = "Online via Zoom",
            category = "Technology",
            attendeeCount = 310,
            emoji = "🧹"
        )
    )

    val forumDiscussions = listOf(
        ForumMessage("Admin", "Selamat datang di grup diskusi komunitas! 🎉 Silakan kenalan dan mulai ngobrol.", "09:00", "AD"),
        ForumMessage("Budi S.", "Halo semuanya! Ada yang ikut Tech Meetup minggu depan?", "09:15", "BS"),
        ForumMessage("Siti R.", "Hadir dong! Udah gak sabar buat networking dan dapet insight baru 🔥", "09:22", "SR"),
        ForumMessage("Andi P.", "Gue juga dateng. Btw, ada yang bisa kasih info parkir di sana?", "09:45", "AP"),
        ForumMessage("Budi S.", "Biasanya parkir di kantong parkir sebelah timur gedung, bisa jalan kaki ~5 menit.", "09:50", "BS"),
        ForumMessage("Admin", "Info parkir sudah diupdate di deskripsi event ya! Thanks Budi 👍", "10:03", "AD")
    )
}