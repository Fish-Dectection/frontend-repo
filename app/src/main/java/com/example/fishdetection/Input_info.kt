package com.example.fishdetection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import com.example.fishdetection.databinding.ActivityInputInfoBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


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

        Log.d("로그 처음","${data?.data}")

        if(resultCode == Activity.RESULT_OK){
            if(requestCode == OPEN_GALLERY){
                val currentImg : Uri? = data?.data

                postimg = currentImg.toString()

                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImg)
                    input_binding.inputimage.setImageBitmap(bitmap)
                    Log.d("비트맵 확인","${bitmap.toString()}")
                }
                catch (e:Exception){
                    e.printStackTrace()
                }
            }
            else{
                Log.d("ActivityResult","someting wrong")
            }
        }

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


        input_binding.confirmBtn.setOnClickListener{
            val currentImg : Uri? = data?.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImg)
            Log.d("비트맵 확인2","${bitmap.toString()}")

            val image_url = bitmapToFile(bitmap,"image_url")

            //val image_url=input_binding.inputimage
            val fish_len=input_binding.lengthEdit.text.toString()
            val fish_region=input_binding.regionEdit.text.toString()

            Log.d("로그 확인1","${image_url}")
            Log.d("로그 확인2","${fish_len}")
            Log.d("로그 확인3","${fish_region}")

            //절대경로 변환 함수
            fun getRealPathFromURI(uri: Uri?): String? {
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = contentResolver.query(uri!!, projection, null, null, null)
                val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                val s: String = cursor.getString(columnIndex)
                cursor.close()
                return s
            }



            //uri 만드는 함수 필요?

            //image request 형식으로 변형
            val file = File("${image_url}")
            //val file = File("/Users/dongwonchu/Desktop/fish-detection/frontend-repo/app/src/main/res/drawable/download.png")
            //val file = File(absolutelyPath("url", this)
            //val file = File(getRealPathFromURI(currentImg))
            Log.d("로그 확인4","내려가나?")
            val requestfile = RequestBody.create(MediaType.parse("image/png"), file)  //MIME타입

            Log.d("로그 확인4","${requestfile.contentType()}")
            val imgbody = MultipartBody.Part.createFormData("image", file.name, requestfile)


            //나머지 데이터 request로 바꾼후 form 형식으로
            var fishlen_body = RequestBody.create(MediaType.parse("text/plain"),"${fish_len}")
            var fishre_body = RequestBody.create(MediaType.parse("text/plain"),"${fish_region}")

            val infohash = hashMapOf<String, RequestBody>()
            infohash["location"] = fishre_body
            infohash["length"] = fishlen_body




            val result_intent = Intent(this,Result::class.java)

            api.post_fish(imgbody,infohash).enqueue(object : Callback<JsonObject>{
                override fun onResponse(
                    call: Call<JsonObject>,
                    response: Response<JsonObject>
                ){
                    Log.d("로그성공", "${response}")
                    Log.d("로그성공",response.toString())
                    Log.d("로그성공", response.body().toString())


                    val result = response.body().toString()

                    result_intent.putExtra("result","${result}")
                    Log.d("로그 result_img 전달 전 확인","${postimg}")
                    result_intent.putExtra("result_img","${postimg}")
                    startActivity(result_intent)
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

    }

}