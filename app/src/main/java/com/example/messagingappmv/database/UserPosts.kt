package com.example.messagingappmv.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one user contact through id and username.
 */
@Entity(tableName = "user_posts")
data class UserPosts(
    @ColumnInfo(name = "uid")
    var uid: Long = 0L,

    @ColumnInfo(name = "room_id")
    var room_id: String = "",

    @ColumnInfo(name = "post")
    var post: String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}
