package com.iqbalmungid.mystory.data.local.datastore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Account(
    val name: String,
    val email: String,
    val password: String,
    val userId: String,
    val token: String,
    val isLogin: Boolean
): Parcelable
