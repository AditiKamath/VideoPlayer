package com.example.youtube.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youtube.*
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchFragment : Fragment(), onVideoItemClickListener {
    // TODO: Rename and change types of parameters
    private lateinit var communicator: Communicator
    private lateinit var recyclerView : RecyclerView

    lateinit var keyword: String
    lateinit var itemList: KeywordSearchResult



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        /*     video_item  = VideoItem.VideoId()*/
        val fragmentView: View = inflater.inflate(R.layout.fragment_search, container, false)

        var search: Button = fragmentView.findViewById(R.id.button_search)
        search.setOnClickListener { view ->
            getVideo()
        }
        recyclerView = fragmentView.findViewById(R.id.recyclerView_videos)



        return fragmentView
    }

    fun getVideo() {
        var retrofit: Retrofit =
            Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/")
                .addConverterFactory(GsonConverterFactory.create()).build()
        keyword = search_edit_text?.text.toString().trim()
        // making the connection between retrofit and the url

        var jsonPlaceHolderApi: jsonPlaceHolderApi =
            retrofit.create(jsonPlaceHolderApi::class.java) // json api object
        var call: Call<KeywordSearchResult> = jsonPlaceHolderApi.getSearchResults(
            "snippet",
            "CAoQAA",
            "AIzaSyALml_gq_OKFNSl1jsoS9VGDf9Vq8iIbAY",
            50,
            keyword
        )
        // calling for the data from the api with api key,max items in the result,the part to be retrieved
        call.enqueue(object : Callback<KeywordSearchResult> {
            override fun onFailure(call: Call<KeywordSearchResult>, t: Throwable) {
                /// error code in the logcat for connection or query problems
                Log.e("Error code", t.message!!)
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<KeywordSearchResult>,
                response: Response<KeywordSearchResult>
            ) {
                if (!response.isSuccessful) {
                    // another check for connection or query errors
                    Log.e("Error code", response.message().toString())
                    return
                }
                Log.v("url", response.message().toString())

                // list == result of the type keywordsearch objects
                // response converted into list format
                itemList = response.body() as KeywordSearchResult

                println(itemList.items)
                recyclerView?.apply {
                    setHasFixedSize(true)
                    // attaching the layout manager and adapter to the recycler view
                    layoutManager = LinearLayoutManager(context)
                    adapter = VideoAdapter(response.body()!!.items, this@SearchFragment)

                }
            }

        })

    }

    override fun onVideoClick(item: VideoItem, position: Int) {
     //   val snippet = VideoItem.VideoItemSnippet()

        Log.v("videoId", item.id.videoId)
        communicator = activity as Communicator
        communicator.passData(item,item.snippet.title,item.snippet.channelTitle,item.snippet.publishedAt,item.snippet.description,item.id.videoId)
       // Toast.makeText(activity, item.snippet.channelTitle, Toast.LENGTH_SHORT).show()
    }


}
