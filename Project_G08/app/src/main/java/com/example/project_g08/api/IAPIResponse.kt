package com.example.project_g08.api

import com.example.project_g08.models.News
import retrofit2.http.GET
import retrofit2.http.Query

//URL endpoint for retrieve the news for API
interface IAPIResponse {
    @GET("/api/1/news?apikey=pub_147176234acaf8e1af12416f79b0763bd067f&country=ca&language=en")
    suspend fun getAllNews(): News

    @GET("/api/1/news?apikey=pub_147176234acaf8e1af12416f79b0763bd067f&category=sports&language=en")
    suspend fun getAllSports() : News

    @GET("/api/1/news?apikey=pub_147176234acaf8e1af12416f79b0763bd067f&category=world&language=en")
    suspend fun getWorld() : News

    @GET("/api/1/news?apikey=pub_147176234acaf8e1af12416f79b0763bd067f&language=en")
    suspend fun getByCategory(@Query("category") categoryToRetrieve:String): News
}