package com.example.messagingappmv.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import com.example.messagingappmv.webservices.cavojsky.responsebodies.RoomListItem
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "room_contact_list")
data class RoomContact(
    @PrimaryKey(autoGenerate = true)
    var room_id: Long = 0L,

    @ColumnInfo(name = "ssid")
    var ssid: String = ""
){
    constructor(roomResponse: RoomListItem) : this(){
        this.ssid = roomResponse.roomid
    }

    constructor(ssid: String) : this(){
        this.ssid = ssid
    }
}