package com.example.howlstagram_f16

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.Login
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


// 위는 gradle에 가서 id 'kotlin-android-extensions' 을 등록하고 sync now후 사용할 수 있다. 안드로이드 4.1로 업데이트 되면서 플러그 인에 서 빠졌기 때문에 자동 import가 안된다.

//string.xml 에 등록한 String 은 R.string.mystring 을 하게 되면 id가 구해지고
//getString(R.string.mystring) 을 하게 되면 그 안에 있는 String 값을 구 할 수가 있다.


class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null

//    googleSignInclient객체를 만든다.
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var callbackManager : CallbackManager? = null
    // onCreate는 LoginActivity가 실행될때 쫙 한번 실행해줘라는 함수임
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        email_login_button.setOnClickListener(){
            signinAndSignup()
        }
        google_sign_in_button.setOnClickListener{
            googleLogin()
        }
        facebook_login_button.setOnClickListener{
            facebookLogin()
        }
//       gso : googleSigninOptional을 만든다 -> 사용자 ID와 프로필 정보를 요청하기 위해서
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // 만약 안된다 시간 세팅을 auto로 해라 로그인 후에는 time zone 때문에 오류가 날 수 있다.
            .requestIdToken("867488562722-0jbi9et7plgjiv0bjckflb7bk9p0bfqa.apps.googleusercontent.com")
            .requestEmail()
            .build()
//        gso를 인자로 전달해서 GoogleSignInClient 객체 생성 !
        googleSignInClient = GoogleSignIn.getClient(this,gso)
//        printHashKey()
        callbackManager = CallbackManager.Factory.create()
    }
    //Xsd1KUAREcaT32Vv5wIVsV+b4CE=
    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey: String = String(Base64.encode(md.digest(), 0))
                Log.i("TAG", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("TAG", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("TAG", "printHashKey()", e)
        }
    }

    fun googleLogin(){

//        gso를 인자로 받아 생성된 googleSignInClient를 signInIntent 메소드를 통해서 signInIntent를 만들고
//        그걸 startActivityForResult에 전달한다 !
        val signInIntent = googleSignInClient?.signInIntent

//        startActivityForResult는 첫 번째 인자로 intent를 두 번째 인자로 결과를 요청할 때 요청 코드를 보낸다 (메소드)

//        startActivity : 새 액티비티를 열어줌 (단방향)
//        startActivityForResult : 새 액티비티를 열어줌 + 결과값 전달 (쌍방향)

//        새로 띄운 activty에서 응답을 받이 위해서 startactivity가 아닌 startactivityforresult를 씀
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
//        registerForActivityResult()
    }

    fun facebookLogin(){
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))

        LoginManager.getInstance()
            .registerCallback(callbackManager, object :FacebookCallback<LoginResult>{
                override fun onSuccess(result: LoginResult?) {
                    handleFacebookAccessToken(result?.accessToken)
                }

                override fun onCancel() {

                }

                override fun onError(error: FacebookException?) {

                }

            })
    }

    fun handleFacebookAccessToken(token: AccessToken?){
        var credential = FacebookAuthProvider.getCredential(token?.token!!)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(){
                    task->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_SHORT).show()
                    //Show the error message
                }
            }
    }

//    바로 onActivitiyResult라는 메소드를 만들게 되는데 이것은 activity A 와 B가 있을 때 A -> B -> A 올 때 사용하는 메소드

//    사용자가 signIn에 성공하면 onActivityResult가 실행된다
//    requestCode는  아까 사용한 GOOGLE_LOGIN_CODE
//
//    1. 구글 로그인 코드 = requestCode 라면
//    2.result에 구글에 로그인했을 때 구글에서 넘겨주는 결괏값을 받아와서 저장
//    3. result가 성공하면 firebaseWithGoogle에 결과 아이디를 넘겨준다 !
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            // 구글에서 넘겨주는 결과값을 받아옴
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            // 성공시 이 값을 firebase에 넘길 수 있겠끔 만듬
            if(result.isSuccess){
                var account = result.signInAccount

                firebaseAuthWithGoogle(account)
            }
        }
    }


//  account에서 id토큰을 가져와서 firebase 사용자 인증 정보로 교환해야 하는데 signInwithCredential로 할 수 있음
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        var credential = GoogleAuthProvider.getCredential(account?.idToken,null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(){
                    task->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_SHORT).show()
                    //Show the error message
                }
            }
    }

    fun signinAndSignup(){
//        이것은 이러한 매개변수에 전달된 인수가 null일 수 있다는 의미입니다.
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener(){
                task->
                if(task.isSuccessful){
                    //creating a user account
                    // task.result?.user
                    moveMainPage(task.result?.user)
                }else if(task.exception?.message.isNullOrEmpty()){
                    //show the error message
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_SHORT).show()
                }else{
                    signinEmail()
                }
            }
    }
    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())
            ?.addOnCompleteListener(){
                    task->
                if(task.isSuccessful){
                    //Login
                    moveMainPage(task.result?.user)
                }else{
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_SHORT).show()
                    //Show the error message
                }
            }

    }

    fun moveMainPage(user:FirebaseUser?){
        if(user != null){ // 유저가 존재할!
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}