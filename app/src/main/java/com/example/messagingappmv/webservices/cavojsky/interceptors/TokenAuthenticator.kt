package com.example.messagingappmv.webservices.cavojsky.interceptors

import android.content.Context
import com.example.messagingappmv.webservices.cavojsky.CavojskyWebService
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(val context: Context) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if(response.code == 401 && response.request.header("ZadanieApiAuth")?.compareTo("accept")==0) {
            val loginData = TokenStorage.load(context)

            if(!response.request.header("Authorization").equals("Bearer ${loginData.accessToken}"))
                return null

            val tokenResponse = CavojskyWebService.refreshTokens(loginData.uid, loginData.refreshToken, context)

            TokenStorage.save(LoginData(loginData.username, tokenResponse.uid, tokenResponse.access, tokenResponse.refresh), context)

            return response.request.newBuilder().header("Authorization", "Bearer ${tokenResponse.access}").build()
        }

        return null
    }
}