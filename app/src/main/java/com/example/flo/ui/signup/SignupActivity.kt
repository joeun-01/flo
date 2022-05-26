package com.example.flo.ui.signup

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.remote.SignUpView
import com.example.flo.room.SongDatabase
import com.example.flo.data.entities.User
import com.example.flo.databinding.ActivitySignupBinding
import com.example.flo.remote.AuthService

class SignupActivity : AppCompatActivity(), SignUpView {
    lateinit var binding : ActivitySignupBinding
    lateinit var userDB : SongDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = SongDatabase.getInstance(this)!!

        binding.signupSuccessTv.setOnClickListener {  // 회원가입 버튼을 누르면
            signUp()  // 확인 후
        }

    }

    private fun getUser() : User {  // 사용자가 입력한 값 가져오기
        val name : String = binding.signupNameEt.text.toString()
        val email : String = binding.signupIdEt.text.toString() + "@" + binding.signupEmailEt.text.toString()
        val password : String = binding.signupPasswordEt.text.toString()

        userDB.userDao().insert(User(email, password, name, ""))

        return User(email, password, name, "")
    }
//
//    private fun signUp() {  // 회원정보를 제대로 입력했는지 확인 후 회원가입 승인
//        if(binding.signupNameEt.text.toString().isEmpty()){
//            Toast.makeText(this, "이름을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
//        }
//
//        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupEmailEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if(binding.signupPasswordEt.text.toString().isEmpty() != binding.signupPasswordCheckEt.text.toString().isEmpty()) {
//            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        userDB.userDao().insert(getUser())  // 다 괜찮으면 UserTable에 저장
//
//        val user = userDB.userDao().getUsers()
//        Log.d("SIGNUPDACT", user.toString())
//    }

    private fun signUp() {  // 네트워크를 이용하여 회원가입 진행
        if(binding.signupNameEt.text.toString().isEmpty()){
            Toast.makeText(this, "이름을 입력하지 않았습니다.", Toast.LENGTH_SHORT).show()
        }

        if(binding.signupIdEt.text.toString().isEmpty() || binding.signupEmailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if(binding.signupPasswordEt.text.toString().isEmpty() != binding.signupPasswordCheckEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val authService = AuthService()

        authService.setSignUpView(this)
        authService.signUp(getUser())
    }

    override fun onSignUpSuccess() {
        binding.signupEmailErrorTv.visibility = View.GONE
        finish()  // 회원가입 성공
    }

    override fun onSignUpFailure(message: String) {
        // 에러 메세지 띄우기
        binding.signupEmailErrorTv.visibility = View.VISIBLE
        binding.signupEmailErrorTv.text = message
    }
}