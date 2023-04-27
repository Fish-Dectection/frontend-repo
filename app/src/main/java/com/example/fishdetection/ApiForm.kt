package com.example.fishdetection

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File

interface ApiForm {
    @Multipart
    @POST("/caught_fish/")
    fun post_fish(
        @Part image : MultipartBody.Part?,
        @PartMap data: HashMap<String, RequestBody>
        //@Body fish: String
    ): Call<JsonObject>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "http://192.168.204.187:8000/" // 주소 "127.0.0.1:8000/caught_fish/"  local =http://10.0.2.2:8000/ mac 주소 192.168.207.39:8000

        fun create(): ApiForm {


            val gson :Gson =   GsonBuilder().setLenient().create();

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
//                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiForm::class.java)
        }
    }
}


