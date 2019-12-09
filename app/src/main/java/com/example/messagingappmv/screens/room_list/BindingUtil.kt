package com.example.messagingappmv.screens.room_list

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.messagingappmv.database.RoomContact

@BindingAdapter("nameString")
fun TextView.setNameString(item: RoomContact?) {
    item?.let {
        text = item.ssid
    }
}
@BindingAdapter("firstLetterName")
fun TextView.setfirstLetterNameString(item: RoomContact?) {
    item?.let {
        text = item.ssid.get(0).toString()
    }
}
