package com.example.paymentapp.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.grpcservicelib.transaction_grpc.SendGetAddTransaction
import com.example.grpcservicelib.user_grpc.SendGetOneUser
import com.example.modellib.StaticVariable
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.user.UserDataModel
import com.example.paymentapp.R
import com.example.paymentapp.activity.ScanActivity
import com.example.paymentapp.res.SharedPrefManager
import kotlinx.android.synthetic.main.dialog_transaction_failed.view.*
import kotlinx.android.synthetic.main.dialog_transaction_success.view.*
import kotlinx.android.synthetic.main.fragment_transaction.*
import java.text.SimpleDateFormat
import java.util.*
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionRequest
import kotlinx.android.synthetic.main.dialog_detail_transaction.view.*
import java.text.DecimalFormat


class TransactionFragment : Fragment(),
    View.OnClickListener,
    SendGetOneUser.OnSendGetOneUserListener,
    SendGetAddTransaction.OnSendGetAddTransactionListener{

    private var errorsLog = ""
    private lateinit var dialog: AlertDialog
    private lateinit var dialogView: View
    private lateinit var inflater: LayoutInflater
    private lateinit var ctx : Context
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

    companion object {
        fun newInstance() = TransactionFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initiationWidget()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    private fun initiationWidget() {
        buttonScanQRCode.setOnClickListener(this)
        buttonPay.setOnClickListener(this)
        ctx = activity as Activity
    }

    override fun onClick(v: View?) {
        when (v){
            buttonScanQRCode -> {
                requestCameraPermission()
            }
            buttonPay -> {
                getOneUser(java.lang.Long.parseLong(editTextIDReceiver.text.toString()))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            editTextIDReceiver.setText(data!!.getStringExtra("IDUser"))
        }
    }

    private fun showError(errors: MutableList<String>){
        for (error in errors) {
            if (error != ""){
                errorsLog += error + "\n"
            }
        }

        if (errorsLog != ""){
            Toast.makeText(ctx,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErrorGetOneUser(errors: MutableList<String>) {
        showError(errors)
    }

    @SuppressLint("SetTextI18n")
    override fun onGetOneUser(user: UserDataModel) {
        showDialogDetailTransactionTemporary(user.IdPengguna, user.NamaPengguna,
            java.lang.Float.parseFloat(editTextNominal.text.toString()))
    }

    private fun getOneUser(idUser : Long){
        SendGetOneUser.newBuilder()
            .setIdPengguna(idUser)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetOneUserListener(this)
            .send()
    }

    override fun onErrorGetAddTransaction(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetAddTransaction(idTransaction: Long) {
        if (idTransaction != 0L){
            showDialogSuccess(ctx)
        }else {
            showDialogFailed(ctx)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun  addTransaction(idUser : Long){
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd MM yyyy kk:mm:ss")
        SendGetAddTransaction.newBuilder()
            .setTransactionData(idUser,
                java.lang.Long.parseLong(editTextIDReceiver.text.toString()),
                java.lang.Float.parseFloat(editTextNominal.text.toString()),
                sdf.format(cal.time))
            .setNetworkConfig(networkConfig)
            .setKey("ABCDE12345", "p4ym3nt")
            .setOnSendGetAddTransactionListener(this)
            .send()
    }

    @SuppressLint("InflateParams")
    private fun showDialogSuccess(context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_transaction_success, null)
        dialog.setView(dialogView)
        dialogView.buttonOkeSuccess.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showDialogFailed(context : Context){

        dialog = AlertDialog.Builder(context).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_transaction_failed, null)
        dialog.setView(dialogView)
        dialogView.buttonOkeFailed.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun requestCameraPermission() {
        Dexter.withActivity(ctx as Activity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    startActivityForResult(Intent(ctx, ScanActivity::class.java), 123)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    if (response.isPermanentlyDenied) {
                        showSettingsDialogAccess()
                    }else {
                        showSettingsDialogWarning()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    private fun showSettingsDialogWarning() {
        val builder = AlertDialog.Builder(ctx)
        builder.setMessage("Aplikasi ini membutuhkan akses camera untuk menjalankan fitur ini.")
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun showSettingsDialogAccess() {
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle("Butuh Hak Akses")
        builder.setMessage("Aplikasi ini membutuhkan akses camera untuk menjalankan fitur ini. Kamu bisa memberikan " +
                "aplikasi ini akses di pengaturan aplikasi.")
        builder.setPositiveButton("PENGATURAN APLIKASI") { dialog, _ ->
            dialog.cancel()
            openSettings()
        }
        builder.setNegativeButton(
            "TUTUP"
        ) { dialog, _ -> dialog.cancel() }
        builder.show()

    }

    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", (ctx as Activity).packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    @SuppressLint("SetTextI18n", "InflateParams")
    private fun showDialogDetailTransactionTemporary(idReceiver: Long, nameReceiver: String, total: Float){
        val dialog = AlertDialog.Builder(ctx).create()
        inflater = layoutInflater
        dialogView = inflater.inflate(R.layout.dialog_detail_transaction, null)
        dialog.setView(dialogView)
        dialogView.textViewIDReceiverDetail.text = idReceiver.toString()
        dialogView.textViewFullNameReceiverDetail.text = nameReceiver
        val formatter = DecimalFormat("#,###,###")
        val totalPay = formatter.format(total)
        dialogView.textViewTotalPayDetail.text = "Rp. $totalPay"
        dialogView.buttonPayDetail.setOnClickListener{
            dialog.dismiss()
            addTransaction(SharedPrefManager.getIdUser(ctx))
        }
        dialogView.buttonCancelDetail.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
