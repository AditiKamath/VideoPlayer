package com.example.youtube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.youtube.fragment.ProfileFragment
import com.example.youtube.fragment.SearchFragment
import com.example.youtube.fragment.VideoFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        getActionBar()?.hide()
        setSupportActionBar(toolbar)
        val videoFragment  = VideoFragment()
        val profileFragment = ProfileFragment()
        val searchFragment = SearchFragment()
        setCurrentFragment(videoFragment)
        // setting correct fragment acc to the id
        bottom_navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.video_icon -> setCurrentFragment(videoFragment)
                R.id.profile_icon -> setCurrentFragment(profileFragment)
                R.id.search_icon-> setCurrentFragment(searchFragment)
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
        menuInflater.inflate(R.menu.option_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.action_logout){
            AlertDialog.Builder(this).apply {
                setTitle("Are you sure?")
                setPositiveButton("Yes"){ _,_ ->
                    FirebaseAuth.getInstance().signOut()
                    logout()
                }
               setNegativeButton("Cancel"){ _,_ ->

                }.create().show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}