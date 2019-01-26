package com.example.paymentapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.grpcservicelib.user_grpc.SendGetOneUser
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.user.UserDataModel
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.fragment_profile_user.*
import java.text.DecimalFormat

class ProfileUserFragment : Fragment(),
    View.OnClickListener,
    SendGetOneUser.OnSendGetOneUserListener{

    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl("192.168.43.95").setPort(8000)

    companion object {
        fun newInstance() = ProfileUserFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initiationWidget(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_user, container, false)
    }

    private fun initiationWidget(v: View) {
        imageButtonSetting.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v){
            imageButtonSetting -> {
                getOneUser()
            }
        }
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        textViewFullName.text = if (errorsLog != "") errorsLog else "No Error, data is Ok"
    }

    override fun onErrorGetOneUser(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetOneUser(user: UserDataModel) {
        var datas = "User Data\n"
        datas += "${user.IdPengguna}\n"
        datas += "${user.NamaPengguna}\n"
        datas += "${user.NomorTelepon}\n"
        datas += "${user.Alamat}\n"
        datas += "${user.Email}\n"
        val formatter = DecimalFormat("#,###,###")
        val balance = formatter.format(user.Balance)
        datas += "${balance}\n"
        datas += "${user.UrlFotoProfil}\n"
        datas += "\n"
        textViewBalance.text =  datas
    }

    fun getOneUser(){

        SendGetOneUser.newBuilder()
            .setIdPengguna(148591304110702511)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetOneUserListener(this)
            .send()
    }
}
