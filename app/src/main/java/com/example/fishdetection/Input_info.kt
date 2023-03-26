package com.example.fishdetection

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.fishdetection.databinding.ActivityInputInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

class Input_info : AppCompatActivity() {

    private val OPEN_GALLERY = 1

    private lateinit var input_binding: ActivityInputInfoBinding
    val api = ApiForm.create()

    private var postimg = ""

    private fun openGallery(){
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        input_binding = ActivityInputInfoBinding.inflate(layoutInflater)
        setContentView(input_binding.root)

        input_binding.downloadBtn.setOnClickListener {
            openGallery()
        }

    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                val currentImg : Uri? = data?.data

                postimg = currentImg.toString()

                try{
                  val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImg)
                    input_binding.inputimage.setImageBitmap(bitmap)
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }
            else{
                Log.d("ActivityResult","someting wrong")
            }
        }




        input_binding.confirmBtn.setOnClickListener{

            val image_url=input_binding.inputimage
            val fish_len=input_binding.lengthEdit.text.toString()
            val fish_region=input_binding.regionEdit.text.toString()

            Log.d("확인1","${image_url}")
            Log.d("확인2","${fish_len}")
            Log.d("확인3","${fish_region}")

            val result_intent = Intent(this,Result::class.java)

            api.post_fish("{'fish_length': ['${fish_len}'], 'user_region': ['\"${fish_region}\"'], 'image_url': ['\"된건가 \"']}").enqueue(object : Callback<PostResult>{
                override fun onResponse(
                    call: Call<PostResult>,
                    response: Response<PostResult>
                ){
                    Log.d("로그실패", "${response}")
                    Log.d("로그성공",response.toString())
                    Log.d("로그성공", response.body().toString())


                    val result = response.body().toString()

                    result_intent.putExtra("result","${result}")
                    Log.d("result_img 전달 전 확인","${postimg}")
                    result_intent.putExtra("result_img","${postimg}")
                    startActivity(result_intent)
                    finish()

                }
                override fun onFailure(call: Call<PostResult>, t: Throwable) {
                    // 실패
                    var test = call
                    Log.d("로그실패", "${test}")
                    Log.d("로그실패", t.message.toString())
                    Log.d("로그실패", "fail")
                }
            })

        }

    }
}