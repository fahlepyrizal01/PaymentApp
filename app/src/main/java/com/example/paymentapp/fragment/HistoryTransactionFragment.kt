package com.example.paymentapp.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.grpcservicelib.transaction_grpc.SendGetAllTransaction
import com.example.modellib.StaticVariable
import com.example.modellib.networkConfig.NetworkConfig
import com.example.modellib.transaction.TransactionDataModel
import com.example.paymentapp.R
import com.example.paymentapp.adapter.HistoryTransactionAdapter
import kotlinx.android.synthetic.main.fragment_history_transaction.*
import java.text.DecimalFormat
import java.util.ArrayList

class HistoryTransactionFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
    View.OnClickListener,
    SendGetAllTransaction.OnSendGetAllTransactionListener{

    var errorsLog = ""
    private lateinit var ctx : Context
    private val networkConfig = NetworkConfig.newBuilder().setUrl(StaticVariable.URL)
        .setPort(StaticVariable.PORT)

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
        ctx = activity as Activity
        getAllTransaction()
    }

    override fun onRefresh() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClick(v: View?) {
        when (v){

        }

        errorsLog = ""
    }

    fun showError(errors: MutableList<String>){
        for (error in errors) {
            errorsLog += error + "\n"
        }

        if (errorsLog != ""){
            Toast.makeText(activity,errorsLog, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onGetAllTransaction(transactions: ArrayList<TransactionDataModel>) {
        setAdapter(transactions, ctx, recyclerViewHistoryTransaction)
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

    fun setAdapter (result : ArrayList<TransactionDataModel>, ctx : Context, rvTransaction : RecyclerView){
        val adapter = HistoryTransactionAdapter(ctx, result)
        rvTransaction.layoutManager = LinearLayoutManager(ctx)
        rvTransaction.adapter = adapter
        adapter.notifyDataSetChanged()
    }

}
