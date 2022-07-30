package com.iqbalmungid.mystory.ui.splashscreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.iqbalmungid.mystory.R
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.ui.main.MainActivity
import com.iqbalmungid.mystory.ui.main.ViewModelFactory
import com.iqbalmungid.mystory.ui.poststory.PostViewModel
import com.iqbalmungid.mystory.ui.signup.SignUpActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")
class SplashScreen : AppCompatActivity() {
    private lateinit var viewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val time = 1500

        Handler(Looper.getMainLooper()).postDelayed({
            statusLogin()
            finish()
        }, time.toLong())
    }

    private fun statusLogin() {
    val pref = AccountPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ViewModelFactory(pref))[PostViewModel::class.java]

        viewModel.getUser().observe(this) {
            if (it.isLogin) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, SignUpActivity::class.java))
                finish()
            }
        }
    }
}