package com.example.messagingappmv.webservices.cavojsky

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object CavojskyWebService {

    private const val BASE_URL = "zadanie.mpage.sk"

    private var httpClient: OkHttpClient
    private var retrofit: Retrofit
    private var webservice: CavojskyWebServiceInterface

    // "constructor"
    init {
        this.httpClient = OkHttpClient.Builder()
                            .build()

        this.retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(this.httpClient)
                        .build()

        this.webservice = this.retrofit.create(CavojskyWebServiceInterface::class.java)
    }
}