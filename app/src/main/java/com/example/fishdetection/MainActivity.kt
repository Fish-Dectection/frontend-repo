package com.example.fishdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fishdetection.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var main_binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main_binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(main_binding.root)

        main_binding.buttno1.setOnClickListener{
            val input_intent = Intent(this,Input_info::class.java)
            startActivity(input_intent)

        }
    }
}