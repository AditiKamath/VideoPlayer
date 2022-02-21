package com.example.youtube

interface Communicator {
    fun passData(videoItem: VideoItem,title:String,channeltitle:String,published_at:String,description:String,videoId:String/* like_videoid:String,*/
                 /*like_videotitle:String*/)
   // fun passUrl(videoId: String,video_title:String)

}