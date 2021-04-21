package com.goldouble.android.whatthe

import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.goldouble.android.whatthe.constants.*
import com.goldouble.android.whatthe.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    enum class HelpeeButton(val color: Int, val icon: Int, val title: String, val subTitle: String, val subButton: SubHelpeeButton) {
        DEFAULT(R.color.button_helpee, R.drawable.ic_outline_live_help_50, "도움받기", "도움을 받으시려면 누르세요", SubHelpeeButton.HELPER),
        WAIT(R.color.button_wait, R.drawable.ic_outline_mic_50, "", "상단의 버튼을 누르고\n말씀해주세요", SubHelpeeButton.BACK),
        RECORDING(R.color.button_recording, R.drawable.ic_outline_mic_50, "녹음중입니다", "도움이 필요한 내용을\n말씀해주세요", SubHelpeeButton.FINISH),
        PLAYING(R.color.button_playing, R.drawable.ic_baseline_play_arrow_50, "녹음된 음성을\n재생중입니다", "요청하기를 누르시면 등록됩니다", SubHelpeeButton.RETRY)
    }

    enum class SubHelpeeButton(val color: Int, val title: String) {
        HELPER(R.color.sub_button_helper, "도움주기"),
        BACK(R.color.sub_button_retry, "돌아가기"),
        FINISH(R.color.sub_button_next, "녹음완료"),
        RETRY(R.color.sub_button_retry, "다시녹음")
    }

    private lateinit var binding: ActivityMainBinding

    private var state = HelpeeButton.DEFAULT
    set(value) {
        field = value
        binding.layoutMainHelpee.setBackgroundColor(ActivityCompat.getColor(this, value.color))
        binding.iconMainHelpee.setImageResource(value.icon)
        binding.labelMainTitle.text = value.title
        binding.labelMainSubtitle.text = value.subTitle

        binding.layoutMainHelper.setBackgroundColor(ActivityCompat.getColor(this, value.subButton.color))
        binding.labelMainHelper.text = value.subButton.title
    }

    private var recordId: String? = null
    private var recordFilePath: String? = null
    private var mediaRecorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kSetActionBar(this)

        val mediaPlayer = MediaPlayer()

        binding.buttonMainHelpee.setOnClickListener {
            when (state) {
                HelpeeButton.DEFAULT -> {
                    binding.imageMainIconBackground.setImageResource(R.drawable.at_field_oval)
                    state = HelpeeButton.WAIT
                }
                HelpeeButton.WAIT -> {
                    if (checkPermission()) {
                        setMediaRecorder()
                        mediaRecorder?.start()
                        state = HelpeeButton.RECORDING
                    }
                }
                HelpeeButton.RECORDING -> Unit
                HelpeeButton.PLAYING -> {
                    mediaPlayer.apply {
                        reset()
                        setDataSource(recordFilePath)
                        prepare()
                    }.start()
                }
            }
        }

        binding.buttonMainHelper.setOnClickListener {
            when (state) {
                HelpeeButton.DEFAULT -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                HelpeeButton.WAIT -> {
                    binding.imageMainIconBackground.setImageResource(R.drawable.at_field_rectangle)
                    state = HelpeeButton.DEFAULT
                }
                HelpeeButton.RECORDING -> {
                    mediaRecorder?.stop()
                    mediaRecorder?.release()
                    mediaRecorder = null
                    state = HelpeeButton.PLAYING
                    binding.buttonMainRequest.visibility = View.VISIBLE
                }
                HelpeeButton.PLAYING -> {
                    mediaPlayer.reset()
                    state = HelpeeButton.WAIT
                    binding.buttonMainRequest.visibility = View.GONE
                }
            }
        }

        binding.buttonMainRequest.setOnClickListener {
            when (state) {
                HelpeeButton.PLAYING -> {
                    binding.loadingLayout.root.visibility = View.VISIBLE
                    window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    kAuth.signInAnonymously()
                            .addOnSuccessListener { user ->
                                kAnonymousUser = user.user
                                kStorage.child("records/$recordId.aac").putFile(Uri.parse("file://$recordFilePath"))
                                        .addOnSuccessListener {
                                            kFirestore.collection(Table.REQUEST.id).add(
                                                    hashMapOf(
                                                            "date" to Date(),
                                                            "record" to recordId
                                                    )
                                            ).addOnCompleteListener {
                                                binding.loadingLayout.root.visibility = View.GONE
                                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                            }
                                            .addOnSuccessListener {
                                                startActivity(Intent(this, WaitingActivity::class.java)
                                                        .putExtra("requestId", it.id)
                                                        .putExtra("recordId", recordId))
                                                state = HelpeeButton.DEFAULT
                                                binding.buttonMainRequest.visibility = View.GONE
                                            }.addOnFailureListener {
                                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            binding.loadingLayout.root.visibility = View.GONE
                                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                                        }
                            }.addOnFailureListener {
                                binding.loadingLayout.root.visibility = View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                }
                else -> Unit
            }
        }
    }

    override fun onResume() {
        super.onResume()
        kAuth.signOut()
    }

    private fun checkPermission():Boolean {
        val permissionList = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.RECORD_AUDIO
        )

        var countGranted = 0

        for(pms: String in permissionList) {
            val permissionCheck = ContextCompat.checkSelfPermission(this, pms)
            if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissionList, 0)
            } else countGranted++
        }

        return countGranted == permissionList.size
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        grantResults.forEach {
            if(it != PackageManager.PERMISSION_GRANTED) finish()
        }
    }

    private fun setMediaRecorder() {
        recordId = UUID.randomUUID().toString()
        recordFilePath = kRecordDir(dataDir) + "/$recordId.aac"
        if(mediaRecorder == null) {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT)
                setOutputFile(recordFilePath)
                prepare()
            }
        }
    }
}