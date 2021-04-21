package com.goldouble.android.whatthe

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.whatthe.constants.kAuth
import com.goldouble.android.whatthe.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO : ActionBar 색상 수정
        //TODO : EditText textColor 수정
        supportActionBar?.apply {
            title = "로그인"
            setDisplayHomeAsUpEnabled(true)
        }

        binding.buttonLogin.setOnClickListener {
            if(binding.editTextEmail.text.isEmpty() || binding.editTextPassword.text.isEmpty()) { //이메일, 패스워드 입력 확인
                Toast.makeText(it.context, "이메일과 비밀번호를 입력해주세요", Toast.LENGTH_LONG).show()
            } else {
                try {
                    //로딩창 표시, 터치  잠금
                    binding.loadingLayout.root.visibility = View.VISIBLE
                    window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                    //로그인
                    kAuth.signInWithEmailAndPassword(
                        binding.editTextEmail.text.toString(),
                        binding.editTextPassword.text.toString()
                    ).addOnCompleteListener { //완료되면
                        //로딩창 제거, 터치 잠금 해제
                        binding.loadingLayout.root.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }.addOnSuccessListener { //성공하면
                        //preference에 로그인 정보 저장
//                        val prefs = getSharedPreferences("login", Context.MODE_PRIVATE)
//                        prefs.edit().putString("email", binding.editTextEmail.text.toString())
//                            .putString("password", binding.editTextPassword.text.toString())
//                            .apply()

                        startActivity(Intent(this, RequestActivity::class.java))
                        finish()
                    }.addOnFailureListener { e -> //실패하면
                        //에러메시지 표시
                        Toast.makeText(it.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: FirebaseException) {
                    e.printStackTrace()
                }
            }
        }

        binding.buttonToRegistration.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}