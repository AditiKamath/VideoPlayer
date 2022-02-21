package com.example.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.youtube.fragment.LikedVideoFragment
import com.example.youtube.fragment.ProfileFragment
import com.example.youtube.fragment.SearchFragment
import com.example.youtube.fragment.VideoFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.recycler_card_view.*

class HomeActivity : AppCompatActivity(), Communicator {
    var videoId : String = "0" //The id of the video being
    var videoTitle : String = "" //The title of the video being played
    val videoItem = VideoItem()
    val liked_video = LikedVideoItem()
    val snippet = VideoItem.VideoItemSnippet()
    var searchFragment = SearchFragment()
    val videoFragment = VideoFragment()
    val likedVideoFragment =LikedVideoFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getActionBar()?.hide()
        setSupportActionBar(toolbar)
        val profileFragment = ProfileFragment()
        setCurrentFragment(searchFragment)
        // setting correct fragment acc to the id
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.video_icon -> setCurrentFragment(videoFragment)
                R.id.profile_icon -> setCurrentFragment(profileFragment)
                R.id.search_icon -> setCurrentFragment(searchFragment)
                R.id.liked_icon -> setCurrentFragment(likedVideoFragment)
            }
            true
        }
    }


    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()
                    logout()
                }
                setNegativeButton("Cancel") { _, _ ->

                }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    fun playVideo(videoId : String, videoTitle : String)
    {
        /**Plays the video with the given id, in video fragment*/

        //Setting the video id
        this.videoId = videoId
        this.videoTitle = videoTitle

        //Changing to video fragment
        setCurrentFragment(videoFragment)
      //  bottom_navigation?.selectedItemId =R.id.video_fragment
    }

 /*   override fun passUrl(videoId: String, video_title: String) {
        this.videoId = videoId
        this.videoTitle = video_title
        val like_bundle = Bundle()
        like_bundle.putString("like_videoid",videoId)
        like_bundle.putString("like_vtitle",videoTitle)
        val transaction  = this.supportFragmentManager.beginTransaction()
        videoFragment.arguments = like_bundle
        transaction.replace(R.id.frame_layout,videoFragment)
        transaction.commit()
    }*/

    override fun passData(
        videoItem: VideoItem,
        title: String,
        channeltitle: String,
        published_at: String,
        description: String,
        videoId: String
      /*  like_videoid:String,
        like_videotitle:String*/
    ) {
        val bundle = Bundle()
      /*  this.videoId = like_videoid
        this.videoTitle = like_videotitle*/
        bundle.putString("v_title", videoItem.snippet.title)
        bundle.putString("v_channel_title", videoItem.snippet.channelTitle)
        bundle.putString("v_published_at", videoItem.snippet.publishTime)
        bundle.putString("v_description", videoItem.snippet.description)
        bundle.putString("v_thumbnail_url", videoItem.snippet.thumbnails.medium.url)
        bundle.putString("v_id", videoItem.id.videoId)
      /*  bundle.putString("like_videoid",like_videoid)
        bundle.putString("like_videotitle",videoTitle*/
        val transaction = this.supportFragmentManager.beginTransaction()

        videoFragment.arguments = bundle
        transaction.replace(R.id.frame_layout, videoFragment)
        transaction.commit()
    }

   /* override fun passLikedData(
        likedVideo: LikedVideoItem,
        title: String,
        channeltitle: String,
        published_at: String,
        description: String,
        videoId: String
    ){
        val bundle = Bundle()
        bundle.putString("v_title", likedVideo.snippet.title)
        bundle.putString("v_channel_title",  likedVideo.snippet.channelTitle)
        bundle.putString("v_published_at",  likedVideo.snippet.publishTime)
        bundle.putString("v_description", likedVideo.snippet.description)
        bundle.putString("v_thumbnail_url", likedVideo.snippet.thumbnails.medium.url)
        bundle.putString("v_id", likedVideo.id.videoId)
        val transaction = this.supportFragmentManager.beginTransaction()

        videoFragment.arguments = bundle
        transaction.replace(R.id.frame_layout, videoFragment)
        transaction.commit()
    }*/
}