package com.goldouble.android.whatthe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.goldouble.android.whatthe.constants.*
import com.goldouble.android.whatthe.databinding.ActivityWaitingBinding
import com.goldouble.android.whatthe.util.ForcedTerminationService

class WaitingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWaitingBinding
    private lateinit var forcedTerminationService: Intent

    enum class Connection(val title: String, val info: String) {
        WAITING("도우미를\n기다리고 있습니다", "현재 대기 인원"),
        CONNECTED("도우미와\n연결되었습니다", "대화방으로 이동합니다")
    }

    private var state = Connection.WAITING
    set(value) {
        field = value
        binding.textWaitingStatus.text = value.title
        binding.textWaitingPerson.text = value.info
    }

    private var timerCount = 4
        set(value) {
            field = value
            binding.textWaitingCount.text = value.toString()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaitingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kSetActionBar(this)

        forcedTerminationService = Intent(this, ForcedTerminationService::class.java)
        startService(forcedTerminationService
            .putExtra("requestId", intent.getStringExtra("requestId"))
            .putExtra("recordId", intent.getStringExtra("recordId")))

        val countDownTimer = object: CountDownTimer((timerCount - 1) * 1000L, 1000) {
            override fun onTick(p0: Long) {
                timerCount--
            }

            override fun onFinish() {
                startActivity(Intent(baseContext, ChattingActivity::class.java)
                    .putExtra("roomNo", intent.getStringExtra("requestId")))
                finish()
            }
        }

        kFirestore.collection(Table.REQUEST.id).document(intent.getStringExtra("requestId")!!).addSnapshotListener { snapshot, error ->
            if(error != null) {
                Log.e("FIRESTORE", error.localizedMessage ?: "")
            }

            if (snapshot != null && snapshot.exists()) {
                if(snapshot.getBoolean("response") == true) {
                    state = Connection.CONNECTED
                    binding.buttonWaitingRetry.visibility = View.GONE
                    countDownTimer.start()
                }
                Log.d("FIRESTORE", snapshot.data.toString())
            }
        }

        binding.buttonWaitingCancel.setOnClickListener {
            countDownTimer.cancel()
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("WAITING", "DESTROYED")
        Log.d("ANONYMOUS USER", kAnonymousUser?.uid.toString())

        kFirestore.collection(Table.REQUEST.id).document(intent.getStringExtra("requestId")!!).delete()
        kStorage.child("records/${intent.getStringExtra("recordId")!!}.aac").delete()
        kAnonymousUser?.delete()
    }
}