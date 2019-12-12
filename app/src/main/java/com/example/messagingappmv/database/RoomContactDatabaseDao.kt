package com.example.messagingappmv.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.time.LocalDate
import java.time.LocalDateTime

@Dao
interface RoomContactDatabaseDao {

    @Insert
    fun insert(room: RoomContact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll( rooms: List<RoomContact>)

    @Update
    fun update(room: RoomContact)

    @Query("SELECT * from room_contact_list WHERE ssid = :mySsid")
    fun get(mySsid: String): RoomContact

    @Query("SELECT * FROM room_contact_list ORDER BY ssid DESC")
    fun getAllRoomContact(): LiveData<List<RoomContact>>

    /**
     * Selects and returns the latest record.
     */
    @Query("SELECT * FROM room_contact_list ORDER BY ssid DESC LIMIT 1")
    fun getRoomContact(): RoomContact?

    @Query("DELETE FROM room_contact_list")
    fun clear()
}