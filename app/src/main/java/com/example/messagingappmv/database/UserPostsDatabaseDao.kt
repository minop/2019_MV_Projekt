/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.messagingappmv.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.messagingappmv.database.UserContact


/**
 * Defines methods for using the UserContactList class with Room.
 */
@Dao
interface UserPostsDatabaseDao {

    @Insert
    fun insert(post: UserPosts)

    /**
     * When updating a row with a value already set in a column,
     * replaces the old value with the new one.
     *
     * @param post new value to write
     */
    @Update
    fun update(post: UserPosts)

    /**
     * Selects and returns the row that matches the supplied start time, which is our key.
     *
     * @param key startTimeMilli to match
     */
    @Query("SELECT * from user_posts WHERE uid = :key")
    fun get(key: Long): UserPosts

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM user_posts")
    fun clear()

    /**
     * Selects and returns all rows in the table,
     *
     * sorted by start time in descending order.
     */
    @Query("SELECT * FROM user_posts WHERE (uid = :uid AND room_id = :room_id) ORDER BY id DESC")
    fun getAllUserPosts(uid: Long, room_id: Long): LiveData<List<UserPosts>>

    /**
     * Selects and returns the latest record.
     */
    @Query("SELECT * FROM user_posts ORDER BY id DESC LIMIT 1")
    fun getUserPost(): UserPosts?

    /**
     * Selects and returns the night with given Id.
     */
    @Query("SELECT * from user_posts WHERE uid = :key")
    fun getUserPostById(key: Long): LiveData<UserPosts>


}
