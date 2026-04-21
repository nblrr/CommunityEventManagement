package com.example.communityeventmanagement.navigation

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.snapshots.SnapshotStateList

val LocalBackStack = compositionLocalOf<SnapshotStateList<Route>> {
    error(
        "LocalBackStack tidak ditemukan! " +
                "Pastikan NavDisplay sudah membungkus composable yang mengakses LocalBackStack."
    )
}