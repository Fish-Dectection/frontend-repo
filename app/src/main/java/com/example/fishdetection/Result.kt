package com.example.fishdetection

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import com.example.fishdetection.databinding.ActivityResultBinding

class Result : AppCompatActivity() {

    private lateinit var result_binding: ActivityResultBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        result_binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(result_binding.root)

        val currentUri : Uri

        val result = intent.getStringExtra("result")
        val result_img = intent.getStringExtra("result_img")
        Log.d("result_img 전달 확인","${result_img}")
        Log.d("result 전달 확인","${result}")
        currentUri = Uri.parse(result_img)

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentUri)
        result_binding.inputimage.setImageBitmap(bitmap)
    }
}