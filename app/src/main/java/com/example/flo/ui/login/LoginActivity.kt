package com.example.flo.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.remote.LoginView
import com.example.flo.room.SongDatabase
import com.example.flo.data.entities.User
import com.example.flo.databinding.ActivityLoginBinding
import com.example.flo.remote.AuthService
import com.example.flo.remote.Result
import com.example.flo.ui.signup.SignupActivity
import com.example.flo.ui.main.MainActivity

class LoginActivity : AppCompatActivity(), LoginView {
    lateinit var binding : ActivityLoginBinding
    lateinit var userDB : SongDatabase

    lateinit var email : String
    lateinit var password : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = SongDatabase.getInstance(this)!!

        binding.loginSignTv.setOnClickListener {  // 회원가입 창으로 넘어가기
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.loginCloseIv.setOnClickListener {
            finish()
        }

        binding.loginLoginTv.setOnClickListener {
            login()  // 로그인 버튼을 누르면 로그인이 진행되도록
        }

    }

    private fun login() {
        // 아이디, 비밀번호를 제대로 입력했는지 확인
        if(binding.loginIdEt.text.toString().isEmpty() || binding.loginEmailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if(binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

//        val user = songDB.userDao().getUser(email, password)  // 유저가 회원가입이 되어있는 지 확인
//
//        // 유저 값이 비어있지 않으면 로그인 완료
//        user?.let{
//            Log.d("LOGIN_ACT/GET_USER", "userId: ${user.id}, $user")
////            saveJwt(user.id)
//            startMainActivity()  // 로그인이 완료되면 메인엑티비티로 돌아감
//        }

        email = binding.loginIdEt.text.toString() + "@" + binding.loginEmailEt.text.toString()
        password = binding.loginPasswordEt.text.toString()

        val authService = AuthService()

        authService.setLoginView(this)
        authService.login(User(email, password, "", ""))

//        if(user == null){
//            Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//        }
    }

//    private fun saveJwt(jwt : Int) {  // userIdx 저장
//        val sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        editor.putInt("jwt", jwt)
//        editor.apply()
//    }

    private fun saveJwt2(jwt : String) {  // userIdx 저장
        val sharedPreferences = getSharedPreferences("auth2", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("jwt", jwt)
        editor.apply()
    }

    private fun startMainActivity() {  // 로그인이 완료되면 다시 메인으로
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onLoginSuccess(code : Int, result: Result) {
        when(code) {
            1000 -> {  // 로그인 완료
                saveJwt2(result.jwt)
                userDB.userDao().updateJWT(result.jwt, email)
                Log.d("USER", userDB.userDao().getUser(email, password).toString())
                startMainActivity()
            }
        }
    }

    override fun onLoginFailure(message: String) {
        binding.loginEmailErrorTv.visibility = View.VISIBLE
        binding.loginEmailErrorTv.text = message
    }
}