package com.example.messagingappmv.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one user contact through id and username.
 */
@Entity(tableName = "user_posts")
data class UserPosts(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "uid")
    var uid: Long = 0L,

    @ColumnInfo(name = "ssid")
    var room_ssid: String = "",

    @ColumnInfo(name = "bssid")
    var room_bssid: String = "",

    @ColumnInfo(name = "message")
    var message: String = "",

    @ColumnInfo(name = "time")
    var time: String = ""
)
