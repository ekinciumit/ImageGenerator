package com.example.imagegenerator

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PromptAPI {

    @POST("/v1/Run/CompVis/stable-diffusion-v1-4")
    fun getPromptData(
        @Header("Content-Type") type:String,
        @Header("x-api-key") key : String,
        @Header("x-nonce") nonce:String,
        @Header("x-signature") signature:String,
        @Body promptRequest: PromptRequest
    ): Call<PromptResponse>
}