package com.valbonet.wakeuplyapp.framework

import com.valbonet.wakeuplyapp.data.SWService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Singleton pattern in Kotlin: https://kotlinlang.org/docs/reference/object-declarations.html#object-declarations
object TikTokApiAccess {
    val serviceApi : SWService by lazy {
        val okHttpClient = OkHttpClient().newBuilder()
        okHttpClient.connectTimeout(60, TimeUnit.SECONDS)
        okHttpClient.readTimeout(60, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(60, TimeUnit.SECONDS)
        val client = okHttpClient.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://t.tiktok.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        retrofit.client(client)

        // Create Retrofit client
        return@lazy retrofit.build().create(SWService::class.java)
    }

}