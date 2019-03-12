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
import kotlinx.android.synthetic.main.fragment_profile_user.*
import com.example.modellib.StaticVariable
import com.example.paymentapp.activity.LoginActivity
import com.example.paymentapp.res.SharedPrefManager
import java.text.DecimalFormat
import android.app.AlertDialog
import android.graphics.Color
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import com.amulyakhare.textdrawable.TextDrawable
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.dialog_qrcode.view.*


class ProfileUserFragment : Fragment(),
    View.OnClickListener,
    SendGetOneUser.OnSendGetOneUserListener{

    var errorsLog = ""
    var bulan = ""
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
        circleImageViewProfileImage.setOnClickListener(this)
        ctx = activity as Activity
        getOneUser(SharedPrefManager.getIdUser(ctx))
    }

    override fun onClick(v: View?) {
        when (v){
            imageButtonSetting -> {
                showOverFlowMenu()
            }

            buttonLogout -> {
                SharedPrefManager.setLoggedIn(ctx, false)
                startActivity(Intent(context, LoginActivity::class.java))
                activity!!.finish()
            }
            imageButtonTopUp -> {
                showDialogTopUpTemporary()
            }
            textViewTopUp -> {
                showDialogTopUpTemporary()
            }
            imageButtonQRCode -> {
                showDialogQRCodeTemporary(SharedPrefManager.getIdUser(ctx))

            }
            textViewQRCode -> {
                showDialogQRCodeTemporary(SharedPrefManager.getIdUser(ctx))
            }
        }
    }

    override fun onErrorGetOneUser(errors: MutableList<String>) {
        showError(errors)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetOneUser(user: UserDataModel) {
        textViewFullNameProfile.text = user.NamaPengguna
        val formatter = DecimalFormat("#,###,###")
        val balance = formatter.format(user.Balance)
        textViewBalanceProfile.text = "Rp. $balance"
        editTextGenderProfile.setText(user.JenisKelamin)
        editTextDateofBirthProfile.setText(user.TanggalLahir)
        editTextEmailProfile.setText(user.Email)
        editTextPhoneNumberProfile.setText(user.NomorTelepon)
        editTextAddressProfile.setText(user.Alamat)

        var drawable = TextDrawable.builder()
            .beginConfig()
            .withBorder(4)
            .bold()
            .endConfig()
            .buildRoundRect(textViewFullNameProfile.text.substring(0,1), Color.rgb(0,150,136), 180)
        circleImageViewProfileImage.setImageDrawable(drawable)
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

    @SuppressLint("InflateParams")
    private fun showDialogQRCodeTemporary(idUser: Long){
        val builder = AlertDialog.Builder(ctx)
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_qrcode, null)
        builder.setView(dialogView)
        showQRCode(idUser, dialogView.imageViewQRCode)
        dialogView.textViewIDUser.text = idUser.toString()
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
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

    private fun showOverFlowMenu(){
        val popup = PopupMenu(ctx, imageButtonSetting)
        popup.menuInflater.inflate(R.menu.overflow_menu, popup.menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->

            when (item!!.itemId) {
                R.id.termAndCondition -> {
                    showDialogTemporary()
                }
                R.id.help -> {
                    showDialogTemporary()
                }
            }
            true
        }
        popup.show()
    }

    private fun showDialogTemporary(){
        val builder = AlertDialog.Builder(ctx)
        builder.setMessage("Ini adalah contoh penerapan GRPC pada aplikasi payment berbasis android.")
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun showDialogTopUpTemporary(){
        val builder = AlertDialog.Builder(ctx)
        builder.setMessage("Ini adalah contoh aplikasi penerapan teknologi GRPC. Untuk melakukan pengisian saldo " +
                "pengguna harus meminta kepada admin aplikasi saat ini.")
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, which -> dialog.cancel() }
        builder.show()
    }
}
