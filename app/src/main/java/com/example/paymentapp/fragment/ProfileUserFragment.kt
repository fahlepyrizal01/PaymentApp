package com.example.paymentapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.grpcservicelib.user_grpc.SendGetOneUser
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.user.UserDataModel
import com.example.paymentapp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_user.*
import  com.example.modellib.StaticVariable
import java.text.DecimalFormat

class ProfileUserFragment : Fragment(),
    View.OnClickListener,
    SendGetOneUser.OnSendGetOneUserListener{

    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

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
        getOneUser()
    }

    override fun onClick(v: View?) {
        when (v){
            imageButtonSetting -> {

            }
        }
    }


    override fun onErrorGetOneUser(errors: MutableList<String>) {
        showError(errors)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetOneUser(user: UserDataModel) {
        setimageProfile(user.UrlFotoProfil)
        textViewFullNameProfile.text = user.NamaPengguna
        val formatter = DecimalFormat("#,###,###")
        val balance = formatter.format(user.Balance)
        textViewBalanceProfile.text = "Rp. $balance"
        editTextEmailProfile.setText(user.Email)
        editTextPhoneNumberProfile.setText(user.NomorTelepon)
        editTextAddressProfile.setText(user.Alamat)
    }

    fun getOneUser(){

        SendGetOneUser.newBuilder()
            .setIdPengguna(420888191099371521)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetOneUserListener(this)
            .send()
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != ""){
            Toast.makeText(activity,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    fun setimageProfile(path : String) {
        Picasso.get()
            .load(path)
            .error(R.drawable.user)
            .placeholder(R.drawable.user)
            .into(circleImageViewProfileImage)
    }
}
