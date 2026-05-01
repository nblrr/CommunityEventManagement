package com.example.communityeventmanagement.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.communityeventmanagement.data.local.JsonStorage
import com.example.communityeventmanagement.data.model.Community

class CommunityRepository(private val storage: JsonStorage?) {
    val communities = mutableStateListOf<Community>()

    // Load data komunitas
    fun loadCommunities() {
        storage?.let {
            val loadedCommunities = it.loadCommunities().map { comm ->
                Community(
                    id = comm.id, name = comm.name, description = comm.description,
                    category = comm.category, coverImageUri = comm.coverImageUri,
                    organizerId = comm.organizerId, organizerName = comm.organizerName,
                    memberIds = comm.memberIds, events = comm.events,
                    forumMessages = comm.forumMessages
                )
            }
            communities.clear(); communities.addAll(loadedCommunities)
            communities.forEachIndexed { index, community ->
                val messages = it.loadForumMessages(community.id)
                if (messages.isNotEmpty()) communities[index] = community.copy(forumMessages = messages)
            }
        }
    }

    fun saveCommunityData() = storage?.saveCommunities(communities.toList())
    fun saveForumData(communityId: Int) {
        communities.find { it.id == communityId }?.let { storage?.saveForumMessages(communityId, it.forumMessages) }
    }
}
