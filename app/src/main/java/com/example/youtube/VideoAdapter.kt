package com.example.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(val list: List<VideoItem>): RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        // attatching the new custom made recycler view item
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item,parent, false)
        return VideoViewHolder(view)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        return holder.bind(list[position])
    }
    class VideoViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        private val title : TextView = itemView.findViewById(R.id.video_title)
        private val channel_title : TextView = itemView.findViewById(R.id.channel_title);
   //     private val description : TextView = itemView.findViewById(R.id.description);
        private val published_time : TextView = itemView.findViewById(R.id.published_time);
        private val imageView: ImageView = itemView.findViewById(R.id.thumbnail);
        fun bind(video: VideoItem){
            // binding all the fetched data to the views in the view holder
             title.text = video.snippet.title
             channel_title.text = video.snippet.channelTitle
           published_time.text =video.snippet.publishTime
       //      description.text = video.snippet.description
            val url  = video.snippet.thumbnails.medium.url
            Glide.with(itemView.context).load(url).into(imageView)

        }
    }
}
