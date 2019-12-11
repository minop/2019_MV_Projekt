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

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("showGif")
fun bindImage(imgView: ImageView, gifId: String?) {
    val cId = "55406586d919cb8ee36800dd3d476776b7489557b600d9ca"
    val imgUrl =  "https://media3.giphy.com/media/" + (gifId?.substring(4) ?: null) + "/100.gif?cid=" + cId + "&rid=100.gif"

    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .into(imgView)
    }
}
