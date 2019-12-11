package com.example.messagingappmv.screens.room

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.messagingappmv.database.UserPosts

@BindingAdapter("nameString")
fun TextView.setNameString(item: UserPosts?) {
    item?.let {
        text = item.post
    }
}
@BindingAdapter("firstLetterName")
fun TextView.setfirstLetterNameString(item: UserPosts?) {
    item?.let {
        text = item.uid.toString()
    }
}
