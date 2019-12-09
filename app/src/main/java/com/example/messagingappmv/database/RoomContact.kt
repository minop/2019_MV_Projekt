package com.example.messagingappmv.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded

@Entity(tableName = "room_contact_list")
data class RoomContact(
    @PrimaryKey(autoGenerate = true)
    var room_id: Long = 0L,

    @ColumnInfo(name = "ssid")
    var ssid: String = "",

    @ColumnInfo(name = "bssid")
    var bssid: String = ""
)