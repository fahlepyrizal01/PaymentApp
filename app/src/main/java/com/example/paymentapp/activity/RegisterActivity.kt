package com.example.paymentapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.grpcservicelib.user_grpc.SendGetRegisterUser
import com.example.modellib.StaticVariable
import com.example.modellib.networkConfig.NetworkConfig
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.activity_resgister.*
import com.example.paymentapp.res.AESCrypt

class RegisterActivity : AppCompatActivity(),
    View.OnClickListener,
    SendGetRegisterUser.OnSendGetRegisterUserListener{

    lateinit var context : Context
    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resgister)
        initiationWidget()
    }

    private fun initiationWidget(){
        context = this@RegisterActivity
        buttonRegister.setOnClickListener(this)
        textViewLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            buttonRegister -> {
               registerUser()
            }
            textViewLogin -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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

    override fun onErrorGetRegisterUser(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetRegisterUser(email : String) {
        val resultIntent = Intent()
        resultIntent.putExtra("Email", email)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun  registerUser(){
        SendGetRegisterUser.newBuilder()
            .setUserData(editTextFullNameRegister.text.toString(),
                AESCrypt.encrypt(editTextPasswordRegister.text.toString()),
                editTextPhoneNumberRegister.text.toString(), editTextAddressRegister.text.toString(),
                editTextEmailRegister.text.toString(),
                800000000f,
                "https://i.pinimg.com/originals/f7/dc/81/f7dc818b5e1a008483c286b18308d03b.jpg")
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetRegisterUserListener(this)
            .send()
    }
}
