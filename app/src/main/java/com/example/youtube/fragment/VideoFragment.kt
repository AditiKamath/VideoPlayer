package com.example.youtube.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.bumptech.glide.Glide
import com.example.youtube.Communicator
import com.example.youtube.HomeActivity
import com.example.youtube.R
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.custom_controller.*
import kotlinx.android.synthetic.main.custom_controller.view.*
import kotlinx.android.synthetic.main.fragment_liked_video.*
import kotlinx.android.synthetic.main.fragment_video.*


class VideoFragment : Fragment() {
    var download_url: String = ""

    companion object {
        fun newInstance(): VideoFragment {
            return VideoFragment()
        }
    }

    private var isFavourite: Boolean = false
    private var userHandle: String? = null
    var download_id: Long = 0
    private var decodedUrl: String? = null //The decoded dash url
    lateinit var favourite_button: ImageView
    private lateinit var communicator: Communicator
    private var downloading: Boolean = false
    private lateinit var v_title: String
    lateinit var exoPlayer: ExoPlayer
    lateinit var simple_exoplayer_view: PlayerView
    private var playbackPos: Long = 0 //The current position of the playback
    private lateinit var video_id: String
    lateinit var v_channel_title: String
    private lateinit var v_publish_at: String
    private lateinit var v_description: String
    lateinit var firestore: FirebaseFirestore
    private lateinit var thumnail_url: String
    var fullscreen: Boolean = false
    private lateinit var dbkey: String // db record of the update
    val dataSourceFactory: DataSource.Factory by lazy {
        DefaultDataSourceFactory(context!!, "sample")
    }
    private lateinit var like_videoid: String
    private lateinit var like_title: String

    private lateinit var v_url: String
   //// var urlString: String? = null
    var downloadDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //   getSupportActionBar().setTitle("");

        val fragmentView: View = inflater.inflate(R.layout.fragment_video, container, false)
        fragmentView.findViewById<TextView>(R.id.video_title)?.text = (activity as HomeActivity).videoTitle
        v_title = arguments?.getString("v_title").toString()

        /* v_channel_title = (arguments?.get("v_channel_title")).toString()
         v_publish_at = arguments?.get("v_published_at").toString()
         v_description = arguments?.get("v_description").toString()
         thumnail_url = arguments?.get("v_thumbnail_url").toString()*/
         video_id = (arguments?.get("v_id") as String?).toString()
        val item_title: TextView = fragmentView.findViewById(R.id.item_title)
        /*  val vchannel_title: TextView = fragmentView.findViewById(R.id.vchannel_title)
          val published_at: TextView = fragmentView.findViewById(R.id.published_at)
          val description: TextView = fragmentView.findViewById(R.id.description)*/
          item_title.setText(arguments?.getString(v_title))
        /*  vchannel_title.setText(v_channel_title)
          published_at.setText(v_publish_at)
          description.setText(v_description)*/
        firestore = FirebaseFirestore.getInstance()
        simple_exoplayer_view = fragmentView.findViewById(R.id.simple_exoplayer_view)

        //    val v_thumbnail: ImageView = fragmentView.findViewById(R.id.v_thumbnail)
        //    Glide.with(this).load(thumnail_url.toString()).into(v_thumbnail)
        if (savedInstanceState != null) {
            //Setting playback position
            playbackPos = savedInstanceState.getLong("PLAYBACK_POS")
            updateFavButton(fragmentView)
            Log.v("VIDEO ID ", video_id)
            decodedUrl = savedInstanceState.getString("DECODED_URL")
            updateFavButton(fragmentView)
            initializePlayer(savedInstanceState.getString("v_id")!!)
            item_title.setText(null)
            /*  vchannel_title.setText(null)
              published_at.setText(null)
              description.setText()*/
        } else {
            checkIfFavourite(fragmentView)
        }
        var download_button: Button = fragmentView.findViewById(R.id.download_button)
        download_button?.setOnClickListener {
            //   Log.v("inside download button", "in it ")
            downloadFromUrl(download_url)
        }

        favourite_button = fragmentView.findViewById(R.id.favourite_button)
      like_videoid= (activity as HomeActivity).videoId
     //   initializePlayer(video_id)
      if(like_videoid!="0" || like_videoid.isBlank() || like_videoid.isNullOrEmpty())
          initializePlayer(like_videoid)
        else
          initializePlayer(video_id)

        Log.v("Liked",like_videoid)
         like_title = (activity as HomeActivity).videoTitle



        simple_exoplayer_view.keepScreenOn;

        simple_exoplayer_view.btn_fullscreen.setOnClickListener {
            activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            change()
        }
        simple_exoplayer_view.exo_rewind.setOnClickListener {/// rewind by 10 seconds
            exoPlayer.seekTo(exoPlayer.currentPosition - 10000)
        }
        simple_exoplayer_view.exo_fwd.setOnClickListener { ////forward by 10 seconds
            exoPlayer.seekTo(exoPlayer.currentPosition + 10000)
        }
        simple_exoplayer_view.exo_pause.setOnClickListener {
            exoPlayer.playWhenReady = false
            exoPlayer.playbackState
        }
        simple_exoplayer_view!!.controllerHideOnTouch = true
        return fragmentView
    }

    @SuppressLint("ResourceType")
    private fun updateFavButton(v: View) {
        if (favourite_button == null)
            favourite_button = v.findViewById(R.id.favourite_button)!!
        if (isFavourite)
            favourite_button.setImageResource(R.drawable.ic_baseline_favorite_24)
        else
            favourite_button.setImageResource(R.drawable.ic_baseline_notfavorite_border_24)

    }


    @SuppressLint("RestrictedApi")
    fun change() {
        if (fullscreen) {
            println("Fullscreen mode is true");
            btn_fullscreen.setImageDrawable(
                ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.fullscreen
                )
            )
            hideSystemUi()

            activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            val params =
                simple_exoplayer_view.getLayoutParams() as FrameLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height =
                (200 * getApplicationContext().resources.displayMetrics.density).toInt()
            Log.v("HEIGHT", params.height.toString())
            simple_exoplayer_view.setLayoutParams(params)
            fullscreen = false
        } else {
            btn_fullscreen.setImageDrawable(
                ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.exit_fullscreen
                )
            )
            // requireActivity()?.getActionBar()?.hide()
            requireActivity().actionBar?.hide()
            hideSystemUi()

            activity?.window?.decorView?.apply {
                systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
                hideSystemUi()
            }

            activity?.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            val params =
                simple_exoplayer_view.getLayoutParams() as FrameLayout.LayoutParams

            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = activity?.windowManager?.defaultDisplay?.width!!
            Log.v("height", params.height.toString())
            Log.v("LayoutDetails", params.toString())
            simple_exoplayer_view.setLayoutParams(params)
            fullscreen = true
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        //Saving the playback position
        outState.putLong("PLAYBACK_POS", exoPlayer.currentPosition)

        //Saving the video id
        outState.putString("v_id", video_id)
    }

    override fun onStop() {
        super.onStop();

        //Releasing the player
        exoPlayer.release()
    }

    /* override fun onStart() {
         super.onStart()
         initializePlayer(video_id)

     }*/
    private fun getUserHandle() {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        if (auth.currentUser!!.email == null || auth.currentUser!!.email.isNullOrBlank())
            userHandle = auth.currentUser!!.phoneNumber
        else
            userHandle = auth.currentUser!!.email
    }

    private fun checkForFav() {
        if (userHandle == null) getUserHandle()
        if (!isFavourite)
            markFavourite()
        else
            removeFromFavourite()

    }

    private fun markFavourite() {
        val data: HashMap<String, String?> =
            hashMapOf("user" to userHandle, "videoid" to this.video_id)
        firestore.collection("Fav").add(data).addOnCompleteListener {
            if (it.isSuccessful) {
                isFavourite = true
                dbkey = it.result!!.id
                updateFavButton(view!!)
                Toast.makeText(activity, "Added to your favourites", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to mark favourite", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeFromFavourite() {
        firestore.collection("Fav").document(dbkey).delete().addOnCompleteListener {
            if (it.isSuccessful) {
                isFavourite = false
                updateFavButton(view!!)
                Toast.makeText(activity, "Removed from your favourites", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Failed to remove from favourite", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun checkIfFavourite(v: View) {

        val check: Query = firestore.collection("favs").whereEqualTo("user", userHandle)
            .whereEqualTo("videoid", this.video_id)
        check.get().addOnCompleteListener {
            if (it.isSuccessful) {
                if (!it.result!!.isEmpty) {
                    isFavourite = true // set the flag in the db
                    dbkey = it.result!!.documents.get(0).id // save the document id from db
                    updateFavButton(view!!)
                    Log.v("LIKED",like_videoid)
            //        initializePlayer(like_videoid)
                }
                favourite_button.setOnClickListener {
                    checkForFav() /// check for favourite
                }

            }
        }
    }
    private fun initializePlayer(video_id: String) {
        /**Initializes the Exoplayer**/

        //Saving the video id
          this.video_id = video_id


        if (!this::exoPlayer.isInitialized) exoPlayer = SimpleExoPlayer.Builder(context!!).build() //Getting the player
        if (simple_exoplayer_view.player == null) simple_exoplayer_view.player = exoPlayer

        exoPlayer.playWhenReady = true

        //Extracting youtube url
        val youtubeExtractor = @SuppressLint("StaticFieldLeak")
        object : YouTubeExtractor(context!!) {
            override fun onExtractionComplete(
                ytFiles: SparseArray<YtFile>?,
                videoMeta: VideoMeta?
            ) {
                if (ytFiles != null) {
                    var downloadUrl: String = ""
                    var iTag: Int = 0
                    for (i in 0..ytFiles.size()) {

                        iTag = ytFiles.keyAt(i)
                        if (ytFiles.get(iTag) != null) {
                            downloadUrl = ytFiles.get(iTag).url
                            break
                        }
                    }
                    download_url = downloadUrl
                 //   Log.d("Itag", iTag.toString())
                    exoPlayer.prepare(
                        ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(downloadUrl))) //Creating and setting the media source
                    exoPlayer.seekTo(playbackPos)
                }
            }
        }

        //Building the data source
        var videoUrl : String =""
         videoUrl =
            "https://www.youtube.com/watch?v=${video_id}" //Creating the youtube url

        youtubeExtractor.extract(
            videoUrl,
            true,
            true
        ) //Extracting usable url and preparing to play video
        v_url = videoUrl
        Log.v("Url", videoUrl)
        //  Toast.makeText(activity, v_url, Toast.LENGTH_SHORT).show()
        Log.v("Video", v_url)
    }


    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        simple_exoplayer_view.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }

    private fun downloadFromUrl(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setTitle("$v_title")
            request.setDescription("Downloading $v_title")
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                System.currentTimeMillis().toString()
            )
            val downloadManager =
                requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        } catch (exception: Exception) {
            Toast.makeText(activity, exception.toString(), Toast.LENGTH_SHORT).show()

        }
    }


}