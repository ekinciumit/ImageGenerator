package com.example.imagegenerator

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DetailAPI {

    @POST("/v1/Task/Detail")
    fun getDetailData(
        @Header("Content-Type") type:String,
        @Header("x-api-key") key : String,
        @Header("x-nonce") nonce:String,
        @Header("x-signature") signature:String,
        @Body detailRequest: DetailRequest
    ):Call<TaskListResponse>
}