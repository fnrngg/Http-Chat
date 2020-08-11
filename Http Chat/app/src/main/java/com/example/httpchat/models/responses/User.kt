package com.example.httpchat.models.responses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Long,
    val name: String,
    val profession: String,
    val picture: String?
) : Parcelable