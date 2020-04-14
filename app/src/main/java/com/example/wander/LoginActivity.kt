package com.example.wander

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun onLogin(v: View){
        startActivity(MainActivity.newIntent(context = this))
    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, LoginActivity::class.java)
    }

}
