package com.example.paymentapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.grpcservicelib.transaction_grpc.SendGetAddTransaction
import com.example.modellib.networkConfig.NetworkConfig
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.fragment_transaction.*

class TransactionFragment : Fragment(),
    View.OnClickListener,
    SendGetAddTransaction.OnSendGetAddTransactionListener{

    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl("192.168.43.95").setPort(8000)

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
    }

    override fun onClick(v: View?) {
        when (v){
            buttonScanQRCode -> {
                addTransaksi()
            }
        }
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        textView4.text = if (errorsLog != "") errorsLog else "No Error, data is Ok"
    }

    override fun onErrorGetAddTransaction(errors: MutableList<String>) {
        showError(errors)
    }

    override fun onGetAddTransaction(idTransaction: Long) {
        textView5.text = idTransaction.toString()
    }

    fun  addTransaksi(){
        SendGetAddTransaction.newBuilder()
            .setTransactionData(148591304110702511,148591304110702512,
                1000f,"31 Januari 2019")
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetAddTransactionListener(this)
            .send()
    }
}
