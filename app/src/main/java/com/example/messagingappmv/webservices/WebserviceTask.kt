package com.example.messagingappmv.webservices

import android.os.AsyncTask
import android.util.Log
import retrofit2.Call

class WebserviceTask<T>(private val callback : (T?) -> Unit, private val callbackError: () -> Unit = {}) : AsyncTask<Call<T>, Void, T?>() {

    override fun doInBackground(vararg request: Call<T>?): T? {
        val response = request[0]!!.execute()

        if(response.isSuccessful)
            return response.body()

        Log.e("Webservice call failed", String.format("%d %s %s",response.raw().code, response.raw().message, response.raw().request.toString()))
        this.cancel(true)
        return null
    }

    override fun onPostExecute(result: T?) {
        callback.invoke(result)
    }

    override fun onCancelled() {
        callbackError.invoke()
    }
}