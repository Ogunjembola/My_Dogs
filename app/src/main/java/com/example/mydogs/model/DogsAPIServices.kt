package com.example.mydogs.model

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
 object DogsAPIServices {
    private val BASE_URL= "https://raw.githubusercontent.com"
    private val api =  Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(DogsApi::class.java)
     fun getDogs():Single<List<DogBreeds>>{
         return api.getDogs()
     }
}