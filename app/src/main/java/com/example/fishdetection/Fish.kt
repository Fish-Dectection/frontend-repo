package com.example.fishdetection

data class PostModel (
    //val data = listOf
    val fish_length: Double? = null,
    val user_region: String? = null,
    val img_url: String? = null
)

data class PostResult (
//    val task_id: Int?=null,
    val response: String?=null

)