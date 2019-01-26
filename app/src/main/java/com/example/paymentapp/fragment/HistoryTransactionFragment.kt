package com.example.paymentapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.grpcservicelib.transaction_grpc.SendGetAllTransaction
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.transaction.TransactionDataModel
import com.example.paymentapp.R
import kotlinx.android.synthetic.main.fragment_history_transaction.*
import java.text.DecimalFormat
import java.util.ArrayList

class HistoryTransactionFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener,
    SendGetAllTransaction.OnSendGetAllTransactionListener{

    var errorsLog = ""
    private val networkConfig = NetworkConfig.newBuilder().setUrl("192.168.43.95").setPort(8000)

    companion object {
        fun newInstance() = HistoryTransactionFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initiationWidget(view)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history_transaction, container, false)
    }

    private fun initiationWidget(v: View) {
//        button.setOnClickListener(this)
    }

    override fun onRefresh() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when (v){
//            button -> {
//                getAllTransaction()
//            }
        }

        errorsLog = ""
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

//        textView20.text = if (errorsLog != "") errorsLog else "No Error, data is Ok"
    }

    override fun onGetAllTransaction(transactions: ArrayList<TransactionDataModel>) {
        var datas = "Transactions List\n"
        for (data in transactions){
            datas += "${data.IdTransaksi}\n"
            datas += "${data.IdPengirim}\n"
            datas += "${data.IdPenerima}\n"
            val formatter = DecimalFormat("#,###,###")
            val nominal = formatter.format(data.Nominal)
            datas += "${nominal}\n"
            datas += "${data.WaktuTransaksi}\n"
            datas += "${data.NamaPengirim}\n"
            datas += "${data.NamaPenerima}\n"
            datas += "\n"
        }
//        textView17.text =  datas
    }

    override fun onErrorGetAllTransaction(errors: MutableList<String>) {
        showError(errors)
    }

    fun getAllTransaction(){

        SendGetAllTransaction.newBuilder()
            .setIdPengguna(148591304110702511)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetAllTransactionListener(this)
            .send()
    }

}
