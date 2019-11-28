package com.example.messagingappmv.screens.contact_list

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.messagingappmv.database.UserContact

@BindingAdapter("nameString")
fun TextView.setNameString(item: UserContact?) {
    item?.let {
        text = item.user_name
    }
}
@BindingAdapter("firstLetterName")
fun TextView.setfirstLetterNameString(item: UserContact?) {
    item?.let {
        text = item.user_name.get(0).toString()
    }
}
