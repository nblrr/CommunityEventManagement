package com.example.communityeventmanagementsystem.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.communityeventmanagementsystem.data.remote.api.EventApi
import com.example.communityeventmanagementsystem.domain.model.Event
import com.example.communityeventmanagementsystem.data.mapper.toDomain

class EventPagingSource(
    private val api: EventApi,
    private val categoryId: Long? = null,
    private val search: String? = null,
    private val status: String? = null,
    private val sortBy: String? = null
) : PagingSource<Int, Event>() {

    override fun getRefreshKey(state: PagingState<Int, Event>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Event> {
        val page = params.key ?: 1
        return try {
            val response = api.getEvents(page, categoryId, search, status, sortBy)
            LoadResult.Page(
                data = response.data.map { it.toDomain() },
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page == response.last_page) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
