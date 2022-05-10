package com.example.flo

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var binding : ActivitySignupBinding
    lateinit var userDB : SongDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = SongDatabase.getInstance(this)!!

        binding.sighupSuccessTv.setOnClickListener {  // 회원가입 버튼을 누르면
            signUp()  // 확인 후
            finish()  // 회원가입 창 종료
        }

    }

    private fun getUser() : User {  // 사용자가 입력한 값 가져오기
        val email : String = binding.signupIdEt.text.toString() + "@" + binding.signupEmailEt.text.toString()
        val password : String = binding.signupPasswordEt.text.toString()

        return User("joeun", email, password)
    }

    private fun signUp() {  // 회원정보를 제대로 입력했는지 확인 후 회원가입 승인
        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupEmailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.signupPasswordEt.text.toString().isEmpty() != binding.signupPasswordCheckEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        userDB.userDao().insert(getUser())  // 다 괜찮으면 UserTable에 저장

        val user = userDB.userDao().getUsers()
        Log.d("SIGNUPDACT", user.toString())
    }
}