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

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one user contact through id and username.
 */
@Entity(tableName = "user_messages")
data class UserMessages(

    @ColumnInfo(name = "uid")
    var uid: Long = 0L,

    @ColumnInfo(name = "contact")
    var contact_id: Long = 0L,

    @ColumnInfo(name = "message")
    var message: String = "",

    @ColumnInfo(name = "time")
    var time: String = "",

    @ColumnInfo(name = "uid_name")
    var uid_name: String = "",

    @ColumnInfo(name = "contact_name")
    var contact_name: String = "",

    @ColumnInfo(name = "uid_fid")
    var uid_fid: String = "",

    @ColumnInfo(name = "contact_fid")
    var contact_fid: String = ""
){
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L

}
