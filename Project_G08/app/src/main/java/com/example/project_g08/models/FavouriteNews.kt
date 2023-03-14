package com.example.project_g08.models

import java.util.*

class FavouriteNews( var title:String="",
                     var pic: String="",
                     var id: String = UUID.randomUUID().toString(),
                     var link: String="") {

    override fun toString(): String {
        return "FavouriteNews(title='$title', pic='$pic', id='$id', link='$link')"
    }
}