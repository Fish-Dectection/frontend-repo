package com.example.fishdetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fishdetection.databinding.ActivityFeedbackBinding
import com.example.fishdetection.databinding.ActivityInputInfoBinding

class feedback : AppCompatActivity() {

    private lateinit var feedbakc_binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        feedbakc_binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(feedbakc_binding.root)


    }
}