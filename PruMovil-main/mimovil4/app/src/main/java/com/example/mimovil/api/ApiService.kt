package com.example.mimovil.api

import com.example.mimovil.model.DataResponse
import retrofit2.Call
import retrofit2.http.GET


interface ApiService {
    @GET("breed/hound/images")
    fun getHoundImages(): Call<DataResponse>

}

