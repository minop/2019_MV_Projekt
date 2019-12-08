package com.example.messagingappmv.webservices.cavojsky

import android.os.AsyncTask
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.lang.IllegalStateException
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.example.messagingappmv.webservices.cavojsky.WebserviceTask

class WebserviceTask<T>(private val callback : (T?) -> Any) : AsyncTask<Call<T>, Void, T?>() {

    override fun doInBackground(vararg request: Call<T>?): T? {
        val response = request[0]!!.execute()

        if(response.isSuccessful)
            return response.body()

    }

    override fun onPostExecute(result: T?) {
        callback.invoke(result)
    }
}