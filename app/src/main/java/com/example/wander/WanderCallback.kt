package com.example.wander

import com.google.firebase.database.DatabaseReference

interface WanderCallback {
    fun onSignout()
    fun onGetUserId(): String
    fun getUserDatabase(): DatabaseReference
    fun profileComplete()
    fun startPhotoActivity()
}