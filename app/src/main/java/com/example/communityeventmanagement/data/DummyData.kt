package com.example.communityeventmanagement.data

// Models
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val isOrganizer: Boolean = false,
    val organizerProfile: OrganizerProfile? = null
)

data class OrganizerProfile(
    val communityName: String,
    val picName: String,
    val description: String,
    val phone: String
)

data class Community(
    val id: Int,
    val name: String,
    val description: String,
    val category: String,
    val emoji: String,
    val organizerId: String,
    val organizerName: String,
    val memberCount: Int,
    val events: List<Event> = emptyList(),
    val forumMessages: List<ForumMessage> = emptyList()
)

data class Event(
    val id: Int,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String,
    val attendeeCount: Int,
    val emoji: String,
    val communityId: Int = 0
)

data class ForumMessage(
    val sender: String,
    val message: String,
    val time: String,
    val avatarInitials: String
)

object AppState {
    var currentUser: UserProfile? = null
    val joinedCommunityIds = mutableSetOf<Int>()
    val communities = mutableListOf<Community>().also { it.addAll(DummyData.communities) }
}

// Dummy Data
object DummyData {
    val communities = mutableListOf(
        Community(
            id = 1,
            name = "Jogja Developer Community",
            description = "Komunitas developer Yogyakarta yang aktif membahas teknologi terkini, berbagi ilmu, dan networking bersama.",
            category = "Technology",
            emoji = "💻",
            organizerId = "org_1",
            organizerName = "Rizal Pratama",
            memberCount = 312,
            events = listOf(
                Event(
                    id = 1,
                    title = "Tech Meetup Jogja 2025",
                    description = "Kumpul komunitas developer membahas tren teknologi terbaru: AI, Kotlin Multiplatform, dan lebih banyak lagi.",
                    date = "28 Apr 2025",
                    location = "Kampus UGM, Yogyakarta",
                    category = "Technology",
                    attendeeCount = 128,
                    emoji = "🎯",
                    communityId = 1
                ),
                Event(
                    id = 2,
                    title = "Community Clean Code Day",
                    description = "Workshop hands-on refactoring kode, clean architecture, dan best practices bersama senior engineers.",
                    date = "05 Jun 2025",
                    location = "Online via Zoom",
                    category = "Technology",
                    attendeeCount = 310,
                    emoji = "🧹",
                    communityId = 1
                )
            ),
            forumMessages = mutableListOf(
                ForumMessage("Admin", "Selamat datang di Jogja Developer Community! 🎉", "09:00", "AD"),
                ForumMessage("Budi S.", "Halo semuanya! Ada yang ikut Tech Meetup minggu depan?", "09:15", "BS"),
                ForumMessage("Siti R.", "Hadir dong! Udah gak sabar buat networking 🔥", "09:22", "SR"),
                ForumMessage("Andi P.", "Gue juga dateng. Ada info parkir?", "09:45", "AP"),
                ForumMessage("Budi S.", "Parkir di kantong sebelah timur, ~5 menit jalan kaki.", "09:50", "BS"),
                ForumMessage("Admin", "Info parkir sudah diupdate di deskripsi event. Thanks Budi 👍", "10:03", "AD")
            )
        ),
        Community(
            id = 2,
            name = "UI/UX Indonesia",
            description = "Komunitas desainer UI/UX Indonesia. Belajar, berbagi karya, dan diskusi seputar desain produk digital.",
            category = "Design",
            emoji = "🎨",
            organizerId = "org_2",
            organizerName = "Maya Sari",
            memberCount = 185,
            events = listOf(
                Event(
                    id = 3,
                    title = "Workshop UI/UX Design",
                    description = "Belajar desain antarmuka langsung dari para ahli. Dari Figma hingga prototyping, semua dibahas tuntas.",
                    date = "10 Mei 2025",
                    location = "Co-working Space Kota Baru",
                    category = "Design",
                    attendeeCount = 64,
                    emoji = "✏️",
                    communityId = 2
                )
            ),
            forumMessages = mutableListOf(
                ForumMessage("Admin", "Selamat datang di komunitas UI/UX Indonesia! 🎨", "08:00", "AD"),
                ForumMessage("Maya S.", "Jangan lupa share portofolio kalian di sini ya!", "08:30", "MS"),
                ForumMessage("Dina R.", "Halo! Saya baru belajar Figma, ada tips untuk pemula?", "09:10", "DR"),
                ForumMessage("Maya S.", "Coba mulai dari Auto Layout dulu, itu fundamental banget 🙌", "09:20", "MS")
            )
        ),
        Community(
            id = 3,
            name = "Startup Founders Jogja",
            description = "Tempat berkumpulnya para founder startup Yogyakarta. Sharing knowledge, pitching ideas, dan cari co-founder.",
            category = "Business",
            emoji = "🚀",
            organizerId = "org_3",
            organizerName = "Bagas Wicaksono",
            memberCount = 97,
            events = listOf(
                Event(
                    id = 4,
                    title = "Startup Pitching Night",
                    description = "Kesempatan emas mempresentasikan ide startup ke investor dan mentor berpengalaman.",
                    date = "20 Mei 2025",
                    location = "Hotel Tentrem, Yogyakarta",
                    category = "Business",
                    attendeeCount = 200,
                    emoji = "💡",
                    communityId = 3
                )
            ),
            forumMessages = mutableListOf(
                ForumMessage("Admin", "Selamat datang para founder! 🚀 Yuk kenalan dulu.", "07:00", "AD"),
                ForumMessage("Bagas W.", "Ada yang butuh co-founder di bidang tech? DM saya!", "07:45", "BW"),
                ForumMessage("Rina K.", "Saya lagi cari mentor untuk product-market fit, ada yang bisa bantu?", "08:10", "RK")
            )
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