package com.example.messagingappmv.screens.room

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.messagingappmv.R
import com.example.messagingappmv.database.UserPosts

@BindingAdapter("nameString")
fun TextView.setNameString(item: UserPosts?) {
    item?.let {
        text = item.post
    }
}

@BindingAdapter("userNameString")
fun TextView.setuserNameString(item: UserPosts?) {
    item?.let {
        text = item.userName
    }
}