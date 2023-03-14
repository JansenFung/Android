package com.example.project_g08.models

class Results(var title:String, var image_url : String?= null, var link: String, var pubDate: String){
    override fun toString(): String {
        return "Results(title='$title', image_url=$image_url, link='$link', pubDate='$pubDate')"
    }
}

class News(var status:String,var totalResults:Int,var results : List<Results>) {
    override fun toString(): String {
        return "News(status='$status', totalResults=$totalResults, results=$results)"
    }
}