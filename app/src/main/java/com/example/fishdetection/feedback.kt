package com.example.fishdetection

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.fishdetection.databinding.ActivityFeedbackBinding
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class feedback : AppCompatActivity() {

    private lateinit var feedbakc_binding: ActivityFeedbackBinding

    val api = FeedbackiForm.create()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedbakc_binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(feedbakc_binding.root)

        //비트맵 변환 함수
        fun bitmapToFile(bitmap: Bitmap, fileName: String): File? {
            val file = File(getExternalFilesDir(null), fileName)
            try {
                val stream: OutputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
                stream.close()
                return file
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }


        var name = intent.getStringExtra("name")
        Log.d("로그 feedbakcname 전달 확인", "${name}")
        feedbakc_binding.feedbackname.setText("${name}")

        val result_img = intent.getStringExtra("feedback_img")
        Log.d("로그 feedbakc 확인", "${result_img}")
        val result_img2 = intent.getStringExtra("feedback_img2")
        Log.d("로그 feedbakc2 확인", "${result_img2}")

        var feedbit: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val currentUri: Uri



        if (result_img == "null") { //사진
            feedbit = BitmapFactory.decodeFile(result_img2)
            feedbakc_binding.inputimage.setImageBitmap(feedbit)
        } else if (result_img != "null") { //갤러리

            feedbit = BitmapFactory.decodeFile(result_img)
            feedbakc_binding.inputimage.setImageBitmap(feedbit)
//            currentUri = Uri.parse(result_img)
//            Log.d("로그 uri 확인","${currentUri}")
//
//
//            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,currentUri)
//            feedbakc_binding.inputimage.setImageBitmap(bitmap)

            Log.d("로그 bit 확인", "${feedbit}")

        }


        feedbakc_binding.confirmBtnFeed.setOnClickListener {

            val fish_name = feedbakc_binding.nameEditFeed.text.toString()
            val fish_dis = feedbakc_binding.descriptionEditFeed.text.toString()

            val result_intent_f = Intent(this,Result::class.java)
            val home_intent = Intent(this,MainActivity::class.java)


            var fishname_body = RequestBody.create(MediaType.parse("text/plain"),"${fish_name}")
            var fishdis_body = RequestBody.create(MediaType.parse("text/plain"),"${fish_dis}")

            val namehash = hashMapOf<String, RequestBody>()
            namehash["fish_name"] = fishname_body
            namehash["description"] = fishdis_body

            val image_url = bitmapToFile(feedbit,"image_url")

            Log.d("로그 확인1 f", "${image_url}")

            val file = File("${image_url}")
            Log.d("로그 확인4", "내려가나?")
            val requestfile = RequestBody.create(MediaType.parse("image/png"), file)  //MIME타입

            Log.d("로그 확인4 f", "${requestfile.contentType()}")
            val imgbody = MultipartBody.Part.createFormData("image_url", file.name, requestfile)


            api.feedback_fish(imgbody, namehash).enqueue(object : Callback<JsonObject> {
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ) {
                    Log.d("로그성공f", "${response}")
                    Log.d("로그성공f", response.toString())
                    Log.d("로그성공 중요f", response.body().toString())

                    Toast.makeText(this@feedback, "의견 감사합니다 확인 후 반영하겠습니다.", Toast.LENGTH_SHORT).show()

                    startActivity(home_intent)
                    finish()
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    // 실패
                    var test = call
                    Log.d("로그실패", "${test}")
                    Log.d("로그실패", t.message.toString())
                    Log.d("로그실패", "fail")
                }

            })


        }
        val home_intent = Intent(this,MainActivity::class.java)

        feedbakc_binding.homeBtnFeed.setOnClickListener {
            startActivity(home_intent)
            finish()
        }


    }
}