package com.example.youtube

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class VideoItem {

    class VideoId {

        lateinit var videoId : String
    }
    @SerializedName("id")
     lateinit var id:VideoId

        class VideoItemSnippet {
            class VideoThumbnail {
                class ThumbnailRes {
                    lateinit var url: String
                    var width: Int = 0
                    var height: Int = 0
                }

                lateinit var default: ThumbnailRes
                lateinit var medium: ThumbnailRes
                lateinit var high: ThumbnailRes
            }

            lateinit var publishedAt: String
            lateinit var channelId: String
            lateinit var title: String
            lateinit var description: String
            lateinit var thumbnails: VideoThumbnail
            lateinit var channelTitle: String
            lateinit var liveBroadcastContent: String
            lateinit var publishTime: String
        }

        @SerializedName("snippet")
        lateinit var snippet: VideoItemSnippet

}

