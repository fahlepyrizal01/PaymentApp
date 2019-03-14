package com.example.paymentapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.grpcservicelib.user_grpc.SendGetLoginUser
import com.example.modellib.StaticVariable
import com.example.modellib.networkConfig.NetworkConfig
import com.example.paymentapp.MainActivity
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.activity_login.*
import com.example.paymentapp.res.AESCrypt
import com.example.paymentapp.res.SharedPrefManager

class LoginActivity : AppCompatActivity(),
    View.OnClickListener,
    SendGetLoginUser.OnSendGetLoginUserListener{

    lateinit var context : Context
    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initiationWidget()
    }

    private fun initiationWidget(){
        context = this@LoginActivity
        buttonLogin.setOnClickListener(this)
        textViewRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            buttonLogin -> {
                loginUser()
            }
            textViewRegister -> {
                startActivityForResult(Intent(this, RegisterActivity::class.java), 123)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            editTextEmailProfile.setText(data!!.getStringExtra("Email"))
        }
    }

    private fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != ""){
            Toast.makeText(this,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErrorGetLoginUser(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetLoginUser(idUser : Long) {
        if (idUser != 0L){
            saveSession(idUser)
        }
    }

    private fun loginUser(){
        SendGetLoginUser.newBuilder()
            .setEmailPassword(editTextEmailProfile.text.toString(),
                AESCrypt.encrypt(editTextPasswordRegister.text.toString()))
            .setNetworkConfig(networkConfig)
            .setKey("ABCDE12345", "p4ym3nt")
            .setOnSendGetLoginUserListener(this)
            .send()
    }

    private fun saveSession(idUser: Long){

        SharedPrefManager.setLoggedIn(context, true)
        SharedPrefManager.setID(context, idUser)
        startActivity(Intent(context, MainActivity::class.java))
        finish()
    }
}
