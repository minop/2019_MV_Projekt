package com.example.messagingappmv.webservices.cavojsky.interceptors

import android.content.Context
import com.google.gson.Gson

object TokenStorage {

    private const val STORAGE_FILE_NAME = "login_data"

    private val gson = Gson()

    fun save(data: LoginData, context: Context) {
        val stringData = gson.toJson(data)
        context.openFileOutput(this.STORAGE_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(stringData.toByteArray(Charsets.UTF_8))
        }
    }

    fun load(context: Context): LoginData {
        val stringData = context.openFileInput(this.STORAGE_FILE_NAME).readBytes().toString(Charsets.UTF_8)
        return gson.fromJson(stringData, LoginData::class.java)
    }
}