package com.example.wander

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import fragments.MatchesFragment
import fragments.ProfileFragment
import fragments.SwipeFragment
import kotlinx.android.synthetic.main.activity_main.*
import util.DATA_USERS

class WanderActivity : AppCompatActivity(), WanderCallback{

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userId = firebaseAuth.currentUser?.uid
    private lateinit var userDatabase: DatabaseReference

    private var profileFragment: ProfileFragment? = null
    private var swipeFragment: SwipeFragment? = null
    private var matchesFragment: MatchesFragment? = null

    private var profileTab: TabLayout.Tab? = null
    private var swipeTab: TabLayout.Tab? = null
    private var matchesTab: TabLayout.Tab? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(userId.isNullOrEmpty()){
            onSignout()
        }

        userDatabase = FirebaseDatabase.getInstance().reference.child(DATA_USERS)

        profileTab = navigation_tabs.newTab()
        swipeTab = navigation_tabs.newTab()
        matchesTab = navigation_tabs.newTab()

        profileTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_profile)
        swipeTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_swipe)
        matchesTab?.icon = ContextCompat.getDrawable(this,R.drawable.tab_matches)

        navigation_tabs.addTab(profileTab!!)
        navigation_tabs.addTab(swipeTab!!)
        navigation_tabs.addTab(matchesTab!!)

        navigation_tabs.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab){
                    profileTab->{
                        if(profileFragment == null){
                            profileFragment = ProfileFragment()
                            profileFragment!!.setCallback(this@WanderActivity)
                        }
                        replaceFragment(profileFragment!!)
                    }
                    swipeTab->{
                        if(swipeFragment == null){
                            swipeFragment = SwipeFragment()
                            swipeFragment!!.setCallback(this@WanderActivity)
                        }
                        replaceFragment(swipeFragment!!)
                    }
                    matchesTab->{
                        if(matchesFragment == null){
                            matchesFragment = MatchesFragment()
                            matchesFragment!!.setCallback(this@WanderActivity)
                        }
                        replaceFragment(matchesFragment!!)
                    }
                }
            }
        })

        profileTab?.select()

    }

    fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
        .replace(R.id.fragmentContainer, fragment)
        .commit()
    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, WanderActivity::class.java)
    }

    override fun onSignout() {
        firebaseAuth.signOut()
        startActivity(StartupActivity.newIntent(this))
        finish()
    }

    override fun onGetUserId(): String = userId!!

    override fun profileComplete() {
        swipeTab?.select()
    }

    override fun getUserDatabase(): DatabaseReference = userDatabase
}


