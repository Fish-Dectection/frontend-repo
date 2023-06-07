package com.example.fishdetection

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fishdetection.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import android.Manifest
import android.widget.Toast
import androidx.core.content.FileProvider


class Input_info : AppCompatActivity() {

    private val OPEN_GALLERY = 1
    private var checknum = 0

    private lateinit var input_binding: ActivityInputInfoBinding
    val api = ApiForm.create()

    private var postimg = ""
    private var postimg2: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    private fun openGallery(){
        checknum = 0
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }

    // 카메라 앱 열기
    private fun openCamera() {
        checknum = 1
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }


    //카메라 관련
    //private lateinit var binding: ActivityMainBinding
    private val REQUEST_IMAGE_CAPTURE = 1

    // 카메라 권한 체크
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // 카메라 권한 요청
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_IMAGE_CAPTURE
        )
    }

    // 권한 요청 결과 처리 함수
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    Toast.makeText(this, "카메라 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        input_binding = ActivityInputInfoBinding.inflate(layoutInflater)
        setContentView(input_binding.root)

        input_binding.downloadBtn.setOnClickListener {
            openGallery()
        }

        input_binding.cameraBtn.setOnClickListener{
            if (checkCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("로그 처음","${data?.data}")
        //Log.d("로그 처음2","${data?.extras}")


        //카메라 이미지 등록 (한개로 못 합티나?)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            input_binding.inputimage.setImageBitmap(imageBitmap)
//        }

        if(resultCode == Activity.RESULT_OK){

            //갤러리 열었을때
            if(requestCode == OPEN_GALLERY && checknum == 0){
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

            //카메라 열었을때
            else if(requestCode == OPEN_GALLERY && checknum == 1){
                Log.d("로그 사진 확인","들어왔나?")
                val imageBitmap = data?.extras?.get("data") as Bitmap
                Log.d("로그 사진 확인","${data?.extras}")
                Log.d("로그 사진 확인","${imageBitmap}")

                input_binding.inputimage.setImageBitmap(imageBitmap)
            }

            else{
                Log.d("로그ActivityResult","someting wrong")
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

            var bitmap: Bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

            //갤러리 일때
            if(checknum==0){
                val currentImg : Uri? = data?.data
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver,currentImg)
                Log.d("비트맵 확인2","${bitmap.toString()}")
            }

            //카메라 일때
            else if(checknum==1){
                bitmap = data?.extras?.get("data") as Bitmap
                postimg2 = bitmap
            }


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
                    Log.d("로그성공 중요", response.body().toString())


                    val result = response.body().toString()
                    val context = applicationContext

                    //인식 못햇을경우
                    if(result.toString() == "{\"response\":\"None\"}"){
                        Log.d("로그인식못함", "인식목함 경우")
                        val fail_intent = Intent(context,Fail::class.java)
                        startActivity(fail_intent)
                        finish()
                    }
                    //정확도 50이하


                    //두마리 이상일 경우
                    else if(result.toString() == "{\"response\":\"한 마리 사진만 입력하세요\"}"){
                        Log.d("로그 여러마리", "여러개 인식 경우")
                        //일단 실패 페이지로
                        val fail_intent = Intent(context,Fail::class.java)
                        startActivity(fail_intent)
                        finish()
                    }

                    //제대로 인식
                    else{
                        //갤러리
                        if(checknum==0){
                            result_intent.putExtra("result","${result}")
                             Log.d("로그 result_img 전달 전 확인","${postimg}")
                            result_intent.putExtra("result_img","${postimg}")
                            result_intent.putExtra("result2","null")
                            result_intent.putExtra("result_img2","null")
                            startActivity(result_intent)
                            finish()
                        }
                        //카메라
                        else if(checknum==1){

                            result_intent.putExtra("result","null")
                            result_intent.putExtra("result_img","null")

                            result_intent.putExtra("result2","${result}")
                            Log.d("로그 postimg2","${postimg2}")

                            val result_file= bitmapToFile(postimg2,"result_file")
                            Log.d("로그 result_img 전달 전 확인","${result_file}")

                            result_intent.putExtra("result_img2","${result_file}")



                            startActivity(result_intent)
                            finish()
                        }
                    }





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


        //하단 버튼 이동
        val home_intent = Intent(this,MainActivity::class.java)

        input_binding.homeBtnInput.setOnClickListener {
            startActivity(home_intent)
            finish()
        }

    }

}