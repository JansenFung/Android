package com.example.project_g08.listeners

import com.example.project_g08.models.FavouriteNews

interface OnNewsClickListener {
    fun onItemClickListener(link: String)

    fun onItemLongClickListener(news: FavouriteNews)
}