package com.example.communityeventmanagement.domain.entities

import com.example.communityeventmanagement.R

data class Category(val id: String, val resId: Int)

const val CATEGORY_ALL = "ALL"

val AppCategories = listOf(
    Category("ART", R.string.cat_art),
    Category("TECHNOLOGY", R.string.cat_technology),
    Category("SPORTS", R.string.cat_sports),
    Category("FOOD", R.string.cat_food),
    Category("BUSINESS", R.string.cat_business),
    Category("MUSIC", R.string.cat_music),
    Category("EDUCATION", R.string.cat_education),
    Category("TRAVEL", R.string.cat_travel),
    Category("HOBBIES", R.string.cat_hobbies),
    Category("ENVIRONMENT", R.string.cat_environment),
    Category("COMEDY", R.string.cat_comedy),
    Category("HEALTH", R.string.cat_health),
    Category("SOCIAL", R.string.cat_social)
)

fun List<Category>.findDisplayRes(id: String): Int? = this.find { it.id == id }?.resId
fun List<Category>.findCategory(id: String): Category? = this.find { it.id == id }
