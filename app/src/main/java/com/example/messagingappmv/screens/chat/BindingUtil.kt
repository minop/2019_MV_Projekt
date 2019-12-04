package com.example.messagingappmv.screens.chat

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.messagingappmv.R
import com.example.messagingappmv.database.UserMessages

@BindingAdapter("nameString")
fun TextView.setNameString(item: UserMessages?) {
    item?.let {
        text = item.message
    }
}

//@BindingAdapter("showGif")
//fun ImageView.set(item: UserMessages?) {
//    item?.let {
//        text = item.message
//    }
//}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("showGif")
fun bindImage(imgView: ImageView, gifId: String?) {
    Log.d("Image id", gifId)
//    val imgUrl = "https://media1.giphy.com/media/" + (gifId?.substring(4) ?: null) + "/100_s.gif?cid=55406586ba2b8638b929a9c0b7ed3dd63afa0990e0e02221&rid=100_s.gif"
    val imgUrl =  "https://media3.giphy.com/media/" + (gifId?.substring(4) ?: null) + "/200.gif?cid=55406586d919cb8ee36800dd3d476776b7489557b600d9ca&rid=200.gif"
    Log.d("Image url", imgUrl)

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

@BindingAdapter("firstLetterName")
fun TextView.setfirstLetterNameString(item: UserMessages?) {
    item?.let {
        text = item.uid.toString()
    }
}
