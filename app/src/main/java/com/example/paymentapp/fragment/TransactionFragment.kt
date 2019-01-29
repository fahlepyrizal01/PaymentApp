package com.example.paymentapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.grpcservicelib.transaction_grpc.SendGetAddTransaction
import com.example.modellib.StaticVariable
import com.example.modellib.networkConfig.NetworkConfig
import com.example.paymentapp.R
import com.example.paymentapp.activity.ScanActivity
import com.example.paymentapp.res.SharedPrefManager
import kotlinx.android.synthetic.main.dialog_transaction_failed.view.*
import kotlinx.android.synthetic.main.dialog_transaction_success.view.*
import kotlinx.android.synthetic.main.fragment_transaction.*
import java.text.SimpleDateFormat
import java.util.*


class TransactionFragment : Fragment(),
    View.OnClickListener,
    SendGetAddTransaction.OnSendGetAddTransactionListener{

    var errorsLog = ""
    lateinit var dialog: AlertDialog
    lateinit var dialogView: View
    lateinit var inflater: LayoutInflater
    lateinit var ctx : Context
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

    companion object {
        fun newInstance() = TransactionFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initiationWidget(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    private fun initiationWidget(v: View) {
        buttonScanQRCode.setOnClickListener(this)
        buttonPay.setOnClickListener(this)
        ctx = activity as Activity
    }

    override fun onClick(v: View?) {
        when (v){
            buttonScanQRCode -> {
                startActivityForResult(Intent(ctx, ScanActivity::class.java), 123)
            }
            buttonPay -> {
                addTransaction(SharedPrefManager.getIdUser(ctx))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123 && resultCode == Activity.RESULT_OK){
            editTextIDReceiver.setText(data!!.getStringExtra("IDUser"))
        }
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != ""){
            Toast.makeText(activity,errorsLog, Toast.LENGTH_SHORT).show()
        }
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
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetAddTransactionListener(this)
            .send()
    }

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

}
