package com.example.wander

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class StartupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }

    fun onLogin(v: View){
        startActivity(LoginActivity.newIntent(context = this))
    }

    fun onSignup(v:View){
        startActivity(SignupActivity.newIntent(context = this))
    }

    companion object{
        fun newIntent(context: Context?) = Intent(context, StartupActivity::class.java)
    }
}
