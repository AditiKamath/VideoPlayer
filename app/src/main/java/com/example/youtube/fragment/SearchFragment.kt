package com.example.youtube.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youtube.*
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters

    lateinit var keyword:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView :View = inflater.inflate(R.layout.fragment_search, container, false)

        var search : Button = fragmentView.findViewById(R.id.button_search)
        search.setOnClickListener{ view->
            getVideo()
        }


        return fragmentView
    }
    fun getVideo(){
        var retrofit : Retrofit = Retrofit.Builder().baseUrl("https://www.googleapis.com/youtube/v3/").
        addConverterFactory(GsonConverterFactory.create()).build()
        keyword = search_edit_text?.text.toString().trim()
        // making the connection between retrofit and the url

        var jsonPlaceHolderApi :jsonPlaceHolderApi = retrofit.create(jsonPlaceHolderApi::class.java) // json api object
        var call : Call<KeywordSearchResult>  = jsonPlaceHolderApi.getSearchResults("snippet","CAoQAA","AIzaSyBx_PnKkAF7qWnVBe2zGNimsNOwi3noAZw ",50,keyword)
            // calling for the data from the api with api key,max items in the result,the part to be retrieved
        call.enqueue(object : Callback<KeywordSearchResult>{
            override fun onFailure(call: Call<KeywordSearchResult>, t: Throwable) {
                /// error code in the logcat for connection or query problems
                Log.e("Error code",t.message!!)
                t.printStackTrace()
            }

            override fun onResponse(
                call: Call<KeywordSearchResult>,
                response: Response<KeywordSearchResult>
            ) {
                if(!response.isSuccessful){
                    // another check for connection or query errors
                    Log.e("Error code",response.message().toString())
                    return
                }
                Log.v("url",response.message().toString())
                // list == result of the type keywordsearch objects
                // response converted into list format
                var itemList:KeywordSearchResult = response.body() as KeywordSearchResult
               // val adapter  = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,itemList)
                println(itemList.items)
                recyclerView.apply {
                    setHasFixedSize(true)
                    // attatching the layout manager and adapter to the reycler view
                    layoutManager = LinearLayoutManager(context)
                    adapter = VideoAdapter(response.body()!!.items)

                }
            }

        })

    }

}
