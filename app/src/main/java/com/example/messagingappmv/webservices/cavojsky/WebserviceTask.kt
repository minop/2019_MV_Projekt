package com.example.messagingappmv.webservices.cavojsky

import android.os.AsyncTask
import android.util.Log
import retrofit2.Call
import java.lang.IllegalStateException

class WebserviceTask<T>(private val callback : (T?) -> Any) : AsyncTask<Call<T>, Void, T?>() {

    override fun doInBackground(vararg request: Call<T>?): T? {
        val response = request[0]!!.execute()

        if(response.isSuccessful)
            return response.body()

        Log.e("Webservice call failed", response.errorBody()!!.string())
        throw IllegalStateException("Webservice call failed")
    }

    override fun onPostExecute(result: T?) {
        callback.invoke(result)
    }
}