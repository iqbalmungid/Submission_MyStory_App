package com.iqbalmungid.mystory.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.iqbalmungid.mystory.data.local.datastore.AccountPreferences
import com.iqbalmungid.mystory.ui.maps.MapsViewModel
import com.iqbalmungid.mystory.ui.poststory.PostViewModel
import com.iqbalmungid.mystory.ui.signin.SignInViewModel

class ViewModelFactory (private val pref: AccountPreferences) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SignInViewModel::class.java) -> {
                SignInViewModel(pref) as T
            }
            modelClass.isAssignableFrom(PostViewModel::class.java) -> {
                PostViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}