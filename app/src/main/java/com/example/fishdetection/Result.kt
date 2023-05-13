package com.example.fishdetection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Binder
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

        var result = intent.getStringExtra("result")
        Log.d("로그 전달result 전달 확인","${result}")
        var result_img = intent.getStringExtra("result_img")
        Log.d("로그 result_img 전달 확인","${result_img}")

        val result2 = intent.getStringExtra("result2")
        Log.d("로그 전달result2 전달 확인","${result2}")
        val result_img2 = intent.getStringExtra("result_img2")
        Log.d("로그 result_img2 전달 확인","${result_img2}")

        var photobit: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

        if(result == "null"){
            photobit = BitmapFactory.decodeFile(result_img2)
            result_binding.inputimage.setImageBitmap(photobit)
            result = result2
        }
        else if(result != "null"){
            currentUri = Uri.parse(result_img)

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentUri)
            result_binding.inputimage.setImageBitmap(bitmap)
        }



        val posibleS = "포획 가능"
        val ispossible = result.toString().contains(posibleS)
        Log.d("로그 포획가능","${ispossible}")

        if(ispossible==true){
            result_binding.possible.setText("채집 가능합니다.")
        }
        else{
            result_binding.possible.setText("채집 불가능합니다.")
        }

        //이름 추출
        var name = result.toString().split(":")
        //Log.d("로그 이름","${name[1]}")
        var name1 = name[1].split(",")
        Log.d("로그 이름1","${name1[0]}")
        var name2 = name1[0].split("'")
        Log.d("로그 이름2","${name2[1]}")
        if(name2[1] != null){
            result_binding.fname.setText(name2[1])
        }

        //금어기 추출
        var date = result.toString().split(":")
        Log.d("로그 날짜","${date[1]}")
        var date1 = date[1].split("(")
        Log.d("로그 날짜1","${date1[2]}") //시작날짜
        Log.d("로그 날짜1","${date1[3]}") //끝날짜
        var startDate = date1[2].split(")")
        Log.d("로그 시작 날짜 최종 ","${startDate[0]}") //시작날짜 최종
        var finshtDate = date1[3].split(")")
        Log.d("로그 끝 날짜 최종 ","${finshtDate[0]}") //끝날짜 최종

        if(startDate[0]!=null && finshtDate[0]!=null){
            var dateText = "${startDate[0]}" + " " + "~" + " " + "${finshtDate[0]}"
            Log.d("로그 날짜 최종 ","${dateText}")
            result_binding.regiondate.setText(dateText)
        }

        //금지체장 추출
        var len = result.toString().split(":")
        var len1 = len[1].split(",")
        Log.d("로그 길이","${len1[8]}")
        if(len1[8] != null){
            result_binding.regionlen.setText(len1[8]+"  " + "cm")
        }



//        result_binding.retry.setOnClickListener {
//            startActivity(input_intent)
//            finish()
//        }

        //피드백 페이지 이동
        val feedbakc_intent = Intent(this,feedback::class.java)

        result_binding.feedbackBtn.setOnClickListener {
            startActivity(feedbakc_intent)
            finish()
        }

        //하단 버튼 이동
        val home_intent = Intent(this,MainActivity::class.java)
        val input_intent = Intent(this,Input_info::class.java)

        //홈 화면 이동
        result_binding.homeBtnResult.setOnClickListener {
            startActivity(home_intent)
            finish()
        }
        //도감 페이지 이동
        result_binding.collectionBtn.setOnClickListener {

        }
        // 입력 페이지 이동
        result_binding.inputBtn.setOnClickListener {
            startActivity(input_intent)
            finish()
        }
    }
}