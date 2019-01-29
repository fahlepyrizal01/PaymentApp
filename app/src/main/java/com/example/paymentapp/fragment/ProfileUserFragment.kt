package com.example.paymentapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.example.paymentapp.activity.LoginActivity
import com.example.paymentapp.res.SharedPrefManager
import java.text.DecimalFormat
import android.app.AlertDialog
import android.widget.ImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.dialog_qrcode.view.*
import kotlinx.android.synthetic.main.dialog_top_up.view.*


class ProfileUserFragment : Fragment(),
    View.OnClickListener,
    SendGetOneUser.OnSendGetOneUserListener{

    var errorsLog = ""
    lateinit var dialog: AlertDialog
    lateinit var dialogView: View
    lateinit var inflater: LayoutInflater
    lateinit var ctx : Context
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
        return inflater.inflate(com.example.paymentapp.R.layout.fragment_profile_user, container, false)
    }

    private fun initiationWidget(v: View) {
        imageButtonSetting.setOnClickListener(this)
        buttonLogout.setOnClickListener(this)
        imageButtonTopUp.setOnClickListener(this)
        textViewTopUp.setOnClickListener(this)
        imageButtonQRCode.setOnClickListener(this)
        textViewQRCode.setOnClickListener(this)
        ctx = activity as Activity
        getOneUser(SharedPrefManager.getIdUser(ctx))
    }

    override fun onClick(v: View?) {
        when (v){
            imageButtonSetting -> {

            }
            buttonLogout -> {
                SharedPrefManager.setLoggedIn(ctx, false)
                startActivity(Intent(context, LoginActivity::class.java))
                activity!!.finish()
            }
            imageButtonTopUp -> {
                showDialogTopUp(ctx)
            }
            textViewTopUp -> {
                showDialogTopUp(ctx)
            }
            imageButtonQRCode -> {
                showDialogQRCode(SharedPrefManager.getIdUser(ctx), ctx)

            }
            textViewQRCode -> {
                showDialogQRCode(SharedPrefManager.getIdUser(ctx), ctx)
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

    private fun getOneUser(idUser : Long){
        SendGetOneUser.newBuilder()
            .setIdPengguna(idUser)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetOneUserListener(this)
            .send()
    }

    private fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != null){
            Toast.makeText(activity,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setimageProfile(path : String) {
        Picasso.get()
            .load(path)
            .error(R.drawable.user)
            .placeholder(R.drawable.user)
            .into(circleImageViewProfileImage)
    }


    private fun showDialogTopUp(context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_top_up, null)
        dialog.setView(dialogView)
        dialogView.buttonCloseTopUp.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDialogQRCode(idUser: Long, context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_qrcode, null)
        dialog.setView(dialogView)
        showQRCode(idUser,dialogView.imageViewQRCode)
        dialogView.textViewIDUser.text = idUser.toString()
        dialogView.buttonCloseQRCode.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showQRCode(idUser: Long, qRCode : ImageView){

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(idUser.toString(),
                BarcodeFormat.QR_CODE,200,200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qRCode.setImageBitmap(bitmap)
        } catch (e : WriterException) {
            e.printStackTrace()
        }
    }
}
