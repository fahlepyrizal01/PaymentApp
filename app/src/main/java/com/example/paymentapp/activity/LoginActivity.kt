package com.example.paymentapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.grpcservicelib.user_grpc.SendGetLoginUser
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.user.UserDataModel
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.activity_login.*
import com.example.paymentapp.res.AESCrypt

class LoginActivity : AppCompatActivity(),
    View.OnClickListener,
    SendGetLoginUser.OnSendGetLoginUserListener{

    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl("192.168.43.95").setPort(8000)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initiationWidget()
    }

    fun initiationWidget(){
        buttonLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            buttonLogin -> {
                loginUser()
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

    override fun onErrorGetLoginUser(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetLoginUser(user: UserDataModel) {
        var datas = "User data\n"
        datas += "${user.IdPengguna}\n"
        datas += "${user.NamaPengguna}\n"
        datas += "\n"
        textViewRegister.text =  datas
    }

    fun loginUser(){
        var password = AESCrypt.encrypt("1234567890")
        SendGetLoginUser.newBuilder()
            .setEmailPassword("grandeariana@gmail.com", password)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetLoginUserListener(this)
            .send()
    }
}
