package com.example.youtube

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


interface jsonPlaceHolderApi {
@GET("search")
    // adding the multiple queries according to our specifications
    fun getSearchResults(@Query("part")part:String,@Query("pageToken")nextPageToken:String,
        @Query("key")key:String,@Query("maxResults")max:Int,
                         @Query("q")search:String) : Call<KeywordSearchResult>


    @GET("videos")
    fun getVideo(@QueryMap queryMap: Map<String, String>) : Call<LikedVideoSearchResult>

}