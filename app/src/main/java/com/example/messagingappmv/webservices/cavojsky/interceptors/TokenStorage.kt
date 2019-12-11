package com.example.messagingappmv.webservices.cavojsky.interceptors

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.FileNotFoundException

object TokenStorage {

    private const val STORAGE_FILE_NAME = "login_data"

    private val gson = Gson()

    fun save(data: LoginData, context: Context) {
        this.write(gson.toJson(data), context)
    }

    fun load(context: Context): LoginData {
        return safeLoad(context)!!
    }

    fun safeLoad(context: Context): LoginData? {
        return try {
            val stringData = context.openFileInput(this.STORAGE_FILE_NAME).readBytes().toString(Charsets.UTF_8)
            gson.fromJson(stringData, LoginData::class.java)
        } catch (e: FileNotFoundException) {
            null
        } catch (e: JsonSyntaxException) {
            null
        }
    }

    fun delete(context: Context) {
        this.write("", context)
    }

    private fun write(data: String, context: Context) {
        context.openFileOutput(this.STORAGE_FILE_NAME, Context.MODE_PRIVATE).use {
            it.write(data.toByteArray(Charsets.UTF_8))
        }
    }
}