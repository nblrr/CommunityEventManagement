package com.example.communityeventmanagementsystem.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.communityeventmanagementsystem.data.remote.api.CommunityApi
import com.example.communityeventmanagementsystem.domain.model.Community
import com.example.communityeventmanagementsystem.data.mapper.toDomain

class CommunityPagingSource(
    private val api: CommunityApi,
    private val categoryId: Long? = null,
    private val search: String? = null,
    private val sortBy: String? = null
) : PagingSource<Int, Community>() {

    override fun getRefreshKey(state: PagingState<Int, Community>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Community> {
        val page = params.key ?: 1
        return try {
            val response = api.getCommunities(page, categoryId, search, sortBy)
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
