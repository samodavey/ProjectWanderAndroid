package com.example.wander

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import fragments.MatchesFragment
import fragments.ProfileFragment
import fragments.SwipeFragment
import kotlinx.android.synthetic.main.activity_main.*
import util.DATA_USERS
import java.io.ByteArrayOutputStream
import java.io.IOException

const val REQUEST_CODE_PHOTO = 1234

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

    private var resultImageUrl: Uri? = null

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            resultImageUrl = data?.data
            storeImage()
        }
    }

    fun storeImage() {

        //This is all to compress the image into an array of bytes
        //So therefore we can store it on my database

        if(resultImageUrl != null && userId != null){
            val filePath = FirebaseStorage.getInstance().reference.child("profileImage").child(userId)
            var bitmap : Bitmap? = null
            try{
                bitmap = MediaStore.Images.Media.getBitmap(application.contentResolver,resultImageUrl)
            }catch (e:IOException){
                e.printStackTrace()
            }

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG,20,baos)
            val data = baos.toByteArray()


            //Get the download uri if successful upload to firestore
            //Also updates profile image on profile fragment
            //Look at firestore documentation for further detail
            val uploadTask = filePath.putBytes(data)
            uploadTask.addOnFailureListener{e -> e.printStackTrace()}
            uploadTask.addOnSuccessListener { taskSnapshot ->
                filePath.downloadUrl.addOnSuccessListener { uri ->
                    profileFragment?.updateImageUri(uri.toString())
                }.addOnFailureListener{e -> e.printStackTrace()}
            }

        }
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

    override fun startPhotoActivity() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }

    override fun getUserDatabase(): DatabaseReference = userDatabase
}


