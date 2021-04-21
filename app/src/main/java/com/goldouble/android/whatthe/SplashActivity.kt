package com.goldouble.android.whatthe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.goldouble.android.whatthe.util.ForcedTerminationService

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(application, MainActivity::class.java))
            finish()
        }, 1500)
        
        //TODO : 최초 접속 시 권한 설정
    }
}