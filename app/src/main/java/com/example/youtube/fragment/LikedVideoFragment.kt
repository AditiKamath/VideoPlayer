package com.example.youtube.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.youtube.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_liked_video.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LikedVideoFragment : Fragment() {
    private lateinit var firestoredatabase : FirebaseFirestore
    private lateinit var userHandle : String
    private lateinit var recyclerAdapter : RecyclerAdapter
    val videoCardClickListener : View.OnClickListener = object:View.OnClickListener
    {
        override fun onClick(v: View?)
        {
          //Playing the selected video
           val videoTitle : String = v!!.findViewById<RelativeLayout>(R.id.video_card_rel_layout).findViewById<TextView>(R.id.video_card_title).text.toString()
            (activity as HomeActivity).playVideo(v!!.getTag().toString(), videoTitle)
        }
    }
    private inner class RecyclerAdapter(var videos : ArrayList<LikedVideoItem>) : RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>()
    {
        inner class RecyclerViewHolder(val cardView : View) : RecyclerView.ViewHolder(cardView)

//The view holder for the recycler view

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder
        {
            //Creating a new view
            val cardView : View = LayoutInflater.from(parent.context).inflate(R.layout.video_card, parent, false) as View

            return RecyclerViewHolder(cardView) //Adding the card view to the view holder and returning it
        }

        override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int)
        {
            //Setting the card title
            holder.cardView.findViewById<TextView>(R.id.video_card_title).text = videos.get(position).snippet.title

      //Displaying the video thumbnail
               val imageView : ImageView = holder.cardView.findViewById<ImageView>(R.id.video_card_thumbnail)
            imageView.setImageResource(R.mipmap.ic_launcher) //Default icon
             getThumbnail(imageView).let {it ->
                 it.execute(videos.get(position).snippet.thumbnails.high.url)
             }
            //Setting the video id as tag on the card view
            holder.cardView.setTag(videos.get(position).id)

            //Setting click listener for the card
            holder.cardView.setOnClickListener(videoCardClickListener)
        }

        override fun getItemCount(): Int
        {
            return videos.size
        }

        fun update(newVideos : ArrayList<LikedVideoItem>)
        {
            /**Updates the videos array used for populating the recycler view**/

            Log.d("vc", newVideos.size.toString())
            videos = newVideos
            notifyDataSetChanged()
        }

        fun append(newVideos : ArrayList<LikedVideoItem>)
        {
            /**Adds the new videos to the videos list**/

            videos.addAll(newVideos) //Adds to new videos to the list of videos
            notifyDataSetChanged()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView : View = inflater.inflate(R.layout.fragment_liked_video, container, false)
        recyclerAdapter = RecyclerAdapter(ArrayList<LikedVideoItem>())
        fragmentView.findViewById<RecyclerView>(R.id.liked_video_recyclerView).let{
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = recyclerAdapter
            // setting layout manager and attaching the adapter to the recycle view
        }
        //Initializing firestore
        firestoredatabase = FirebaseFirestore.getInstance()

        //Getting the user handle
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        if(auth.currentUser!!.email == null || auth.currentUser!!.email!!.isBlank())
            userHandle = auth.currentUser!!.phoneNumber!!
        else
            userHandle = auth.currentUser!!.email!!

        //Initializing the recycler adapter


        //Getting the list of favourite videos
        getFavouriteVideos()

        return fragmentView
    }

    private fun getFavouriteVideos()
    {
        //Creating the query to get all the favourite videos of the current user
        val query : Query = firestoredatabase.collection("Fav").whereEqualTo("user",userHandle)

        //Executing the query
        query.get().addOnCompleteListener { task : Task<QuerySnapshot> ->
            if(task.isSuccessful)
                retrieveFavVideosInfo(task.result!!.documents)
            else
                Toast.makeText(context, "Failed to retrieve favourites list", Toast.LENGTH_SHORT).show()
        }
    }

    private fun retrieveFavVideosInfo(docs : List<DocumentSnapshot>)
    {
        /**Retrieves video items for each doc**/

        //Initializing retrofit
        val retrofit : Retrofit = Retrofit.Builder().let {
            it.baseUrl("https://www.googleapis.com/youtube/v3/")
            it.addConverterFactory(GsonConverterFactory.create())
            it.build()
        }

        //Creating the YoutubeApiInterface object
        val apiInterface : jsonPlaceHolderApi = retrofit.create(jsonPlaceHolderApi::class.java)

        //Creating the request query map
        val queryMap : HashMap<String, String> = HashMap<String,String>()
        queryMap.put("part","snippet")
        //Adding the video ids to the query
        var videoIds : String? =""
        for(doc in docs)
        {
            videoIds += "${doc.data!!.get("videoid") as String},"
        }
        queryMap.put("id",videoIds!!)
        queryMap.put("fields","items(snippet,id)")
        queryMap.put("key","AIzaSyB5_5sYECwuDrGkdAsg0isRRvx7HMtStgo")

        //Performing api request
        apiInterface.getVideo(queryMap).enqueue(object: Callback<LikedVideoSearchResult>
        {
            override fun onResponse(call: Call<LikedVideoSearchResult>, response: Response<LikedVideoSearchResult>)
            {
                //Checking if the response was successful
                if(response.isSuccessful)
                {
                    val videoItems : ArrayList<LikedVideoItem> = response.body()!!.items as ArrayList<LikedVideoItem>
                    //Getting the video items list

                    //Updating the recycler adapter
                    recyclerAdapter.update(videoItems)
                }
                else
                    Log.e("FAVS_ERROR_CODE", response.code().toString())
            }

            override fun onFailure(call: Call<LikedVideoSearchResult>, t: Throwable)
            {
                Log.e("FAVS_ERROR", t.message, t)
            }

        })

    }

}