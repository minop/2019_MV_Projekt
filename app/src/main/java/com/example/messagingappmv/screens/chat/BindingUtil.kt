package com.example.messagingappmv.screens.chat

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.messagingappmv.database.UserMessages

@BindingAdapter("nameString")
fun TextView.setNameString(item: UserMessages?) {
    item?.let {
        text = item.user_name
    }
}
@BindingAdapter("firstLetterName")
fun TextView.setfirstLetterNameString(item: UserMessages?) {
    item?.let {
        text = item.user_name.get(0).toString()
    }
}
