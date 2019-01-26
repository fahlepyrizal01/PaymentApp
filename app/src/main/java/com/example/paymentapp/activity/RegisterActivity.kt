package com.example.paymentapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.grpcservicelib.user_grpc.SendGetRegisterUser
import com.example.modellib.networkConfig.NetworkConfig
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.activity_resgister.*
import com.example.paymentapp.res.AESCrypt

class RegisterActivity : AppCompatActivity(),
    View.OnClickListener,
    SendGetRegisterUser.OnSendGetRegisterUserListener{

    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl("192.168.43.95").setPort(8000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resgister)
        initiationWidget()
    }

    fun initiationWidget(){
        buttonRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            buttonRegister -> {
               registerUser()
            }
        }

        errorsLog = ""
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        textView50.text = if (errorsLog != "") errorsLog else "No Error, data is Ok"
    }

    override fun onErrorGetRegisterUser(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetRegisterUser(idTransaction: Long) {
        textViewLogin.text = idTransaction.toString()
    }

    fun  registerUser(){
        var password = AESCrypt.encrypt("1234567890")
        SendGetRegisterUser.newBuilder()
            .setUserData("Ariana Grande", password,
                "082307701281", "USA", "grandeariana@gmail.com",
                20000000f, "https://15786345")
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetRegisterUserListener(this)
            .send()
    }
}
