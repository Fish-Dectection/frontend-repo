package com.example.fishdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fishdetection.databinding.ActivityFailBinding
import com.example.fishdetection.databinding.ActivityFeedbackBinding

class Fail : AppCompatActivity() {

    private lateinit var fail_binding: ActivityFailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fail_binding = ActivityFailBinding.inflate(layoutInflater)
        setContentView(fail_binding.root)

        //하단 버튼 이동
        val home_intent = Intent(this,MainActivity::class.java)
        val input_intent = Intent(this,Input_info::class.java)

        //홈 화면 이동
        fail_binding.homeBtnFail.setOnClickListener {
            startActivity(home_intent)
            finish()
        }
        //도감 페이지 이동
        fail_binding.collectionBtnFail.setOnClickListener {

        }
        // 입력 페이지 이동
        fail_binding.inputBtnFail.setOnClickListener {
            startActivity(input_intent)
            finish()
        }
    }
}