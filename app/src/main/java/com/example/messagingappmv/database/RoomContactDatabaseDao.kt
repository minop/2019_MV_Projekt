package com.example.messagingappmv.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomContactDatabaseDao {

    @Insert
    fun insert(room: RoomContact)

    @Update
    fun update(room: RoomContact)

    @Query("SELECT * from room_contact_list WHERE ssid = :mySsid AND bssid = :myBssid")
    fun get(mySsid: String, myBssid: String): RoomContact

    @Query("SELECT * FROM room_contact_list ORDER BY ssid DESC")
    fun getAllRoomContact(): LiveData<List<RoomContact>>

    /**
     * Selects and returns the latest record.
     */
    @Query("SELECT * FROM room_contact_list ORDER BY room_id DESC LIMIT 1")
    fun getRoomContact(): RoomContact?

    @Query("DELETE FROM room_contact_list")
    fun clear()
}