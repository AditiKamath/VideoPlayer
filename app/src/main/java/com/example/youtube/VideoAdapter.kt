package com.example.youtube

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class VideoAdapter(val list: List<VideoItem>,var clickListener: onVideoItemClickListener): RecyclerView.Adapter<VideoAdapter.VideoViewHolder>(){
   // lateinit var video_item : VideoItem.VideoId
   val videoCardClickListener : View.OnClickListener = object:View.OnClickListener
   {
       override fun onClick(v: View?)
       {
           //Playing the selected video
           val videoTitle : String = v!!.findViewById<RelativeLayout>(R.id.video_card_rel_layout).findViewById<TextView>(R.id.video_card_title).text.toString()
           (this@VideoAdapter as HomeActivity).playVideo(v!!.getTag().toString(), videoTitle)
       }
   }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        // attaching the new custom made recycler view item
        val cardView = LayoutInflater.from(parent.context).inflate(R.layout.video_card,parent, false)
        return VideoViewHolder(cardView)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
       /* val imageView : ImageView = holder.itemView.findViewById<ImageView>(R.id.video_card_thumbnail)
        imageView.setImageResource(R.mipmap.ic_launcher) //Default icon
        getThumbnail(imageView).let {it ->
            it.execute(list.get(position).snippet.thumbnails.high.url)
        }*/
        return holder.bind(list[position],clickListener)
    }
    class VideoViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById(R.id.video_card_thumbnail)

  //  val cardView : View = itemView.findViewById(R.id.video_card_rel_layout)
        private val title : TextView = itemView.findViewById(R.id.video_card_title)
    //    private val channel_title : TextView = itemView.findViewById(R.id.channel_title);
        //     private val description : TextView = itemView.findViewById(R.id.description);
   //     private val published_time : TextView = itemView.findViewById(R.id.published_time);
    //    private val imageView: ImageView = itemView.findViewById(R.id.thumbnail);
        fun bind(video: VideoItem, action:onVideoItemClickListener){
//            Log.v("videoId",video.id.videoId.toString())
            // binding all the fetched data to the views in the view holder
            title.text = video.snippet.title
         //   channel_title.text = video.snippet.channelTitle
        //    published_time.text =video.snippet.publishTime
            //      description.text = video.snippet.description
            val url  = video.snippet.thumbnails.medium.url
           Glide.with(itemView.context).load(url).into(imageView)
            //Displaying the video thumbnail

            itemView.setOnClickListener{
                action.onVideoClick(video,adapterPosition)
            }
        }
    }
}
interface onVideoItemClickListener{
    fun onVideoClick(item:VideoItem,position: Int)
  //  fun onLikedVideoClick(item:LikedVideoItem,position: Int)
}