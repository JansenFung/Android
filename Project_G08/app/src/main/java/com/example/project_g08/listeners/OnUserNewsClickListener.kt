package com.example.project_g08.listeners

import com.example.project_g08.models.UserArticle

interface OnUserNewsClickListener {
    fun onItemLongClickListener(newArticle: UserArticle)

    fun onItemClickListener(newArticle: UserArticle)
}