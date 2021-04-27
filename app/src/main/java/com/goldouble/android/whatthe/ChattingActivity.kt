package com.goldouble.android.whatthe

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.PixelCopy
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.whatthe.adapter.ChattingRecyclerViewAdapter
import com.goldouble.android.whatthe.constants.*
import com.goldouble.android.whatthe.databinding.ActivityChattingBinding
import com.goldouble.android.whatthe.ui.FloatingSurfaceView
import com.goldouble.android.whatthe.ui.FloatingView
import com.goldouble.android.whatthe.ui.SurfaceViewHolder
import com.goldouble.android.whatthe.util.MediaProjectionManagerService
import com.remotemonster.sdk.RemonCall
import com.remotemonster.sdk.data.AudioType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ChattingActivity : AppCompatActivity() {
    lateinit var binding: ActivityChattingBinding

    private val floatingViewLayoutParmas: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY).apply {
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
    }
    private val floatingSurfaceLayoutParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY).apply {
            width = 0
            height = 0
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        }
    }
    private val mWindowManager: WindowManager by lazy {
        getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }
    private val displayInfo by lazy {
        resources.displayMetrics
    }

    private val PERMISSION_REQUEST = 0

    //lateinit var floatingSurfaceView: FloatingSurfaceView
    lateinit var floatingView: FloatingView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kSetActionBar(this)
        checkPermissions()

        val query = kFirestore.collection(Table.CHATTING.id)
            .whereEqualTo("room", intent.getStringExtra("roomNo")).orderBy("date")
        val options = FirestoreRecyclerOptions.Builder<ChattingData>().setQuery(query, ChattingData::class.java).build()
        val chattingAdapter = ChattingRecyclerViewAdapter(options)

        binding.recyclerViewChatting.adapter = chattingAdapter
        binding.recyclerViewChatting.layoutManager = LinearLayoutManager(this)

        if(!kAuth.currentUser!!.isAnonymous) {
            kFirestore.collection(Table.CHATTING.id).add(
                    hashMapOf(
                            "room" to intent.getStringExtra("roomNo"),
                            "date" to Date(),
                            "content" to "안녕하세요. 스마트 도우미 ${kAuth.currentUser!!.displayName}입니다.\n무엇을 도와드릴까요?",
                            "sender" to kAuth.currentUser!!.uid
                    )
            )
        } else {
            checkOverlayPermission()
//            floatingSurfaceView = FloatingSurfaceView(this).also {
//                mWindowManager.addView(it, floatingViewLayoutParmas)
//            }
            floatingView = FloatingView(this, mWindowManager, floatingViewLayoutParmas) {
                screenShot()
            }.also { it.show() }
            startForegroundService(Intent(this, MediaProjectionManagerService::class.java).apply {
                action = ACTION_INIT
            })
        }

        Log.d("ROOM NO", intent.getStringExtra("roomNo").toString())
        RemonCall.builder().context(this)
                .serviceId("ce14cdca-b28d-483a-8146-dc2b7f4d703f")
                .key(getString(R.string.remote_monster_API_key))
                .audioType(AudioType.VOICE)
                .isVideoCall(false)
                .build()
                .connect(intent.getStringExtra("roomNo"))

        chattingAdapter.startListening()
    }

    private fun screenShot() {
        val bitmap = Bitmap.createBitmap(displayInfo.widthPixels, displayInfo.heightPixels, Bitmap.Config.ARGB_8888)
        val listener = PixelCopy.OnPixelCopyFinishedListener { }
        val fileName = UUID.randomUUID().toString()
        PixelCopy.request(floatingView.surfaceView, bitmap, listener, Handler())

        /*val file = File(cacheDir, "screenshots")
        file.delete()
        file.createNewFile()
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)*/

        val file = File(kScreenshotDir(dataDir) + "/screenshot.jpg")
        val fileOutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)

        kStorage.child("screenshots/$fileName").putFile(file.toUri()).addOnSuccessListener {
            kFirestore.collection(Table.CHATTING.id).add(
                hashMapOf(
                    "room" to intent.getStringExtra("roomNo"),
                    "date" to Date(),
                    "image" to fileName
                )
            )
        }

        Log.d("FLOAT", "CAPTURED(width: ${bitmap.width}, height: ${bitmap.height})")
    }

    private fun checkPermissions() {
        //TODO : 카메라 권한 꼭 필요 없으면 지울 것
        val permissionList = arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CHANGE_NETWORK_STATE,
                android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.READ_PHONE_STATE,
        )

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.containsValue(false)) finish()
        }.launch(permissionList)

//        var countGranted = 0
//
//        for(pms: String in permissionList) {
//            val permissionCheck = ContextCompat.checkSelfPermission(this, pms)
//            if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, permissionList, 0)
//            } else countGranted ++
//        }
//
//        return countGranted == permissionList.size
    }

    private fun checkOverlayPermission() {
        if(!Settings.canDrawOverlays(this)) {
            //val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if(!it) finish()
            }.launch(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            //startActivityForResult(intent, PERMISSION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach {
            if(it != PackageManager.PERMISSION_GRANTED) finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PERMISSION_REQUEST -> if (!Settings.canDrawOverlays(this)) finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(kAuth.currentUser!!.isAnonymous) {
            mWindowManager.removeView(floatingView)
        }
    }
}