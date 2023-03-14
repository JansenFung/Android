package com.example.project_g08.models

import java.util.*

class UserArticle(var id: String = UUID.randomUUID().toString(),
                  var title: String = "",
                  var detail: String = "",
                  var author: String = "",
                  var publishedDate: String = "") {

    init {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        cal.set(year, month, day)

        publishedDate = cal.time.toString()
    }

    override fun toString(): String {
        return "UserArticle(id='$id', title='$title', detail='$detail', author='$author', publishedDate='$publishedDate')"
    }

}