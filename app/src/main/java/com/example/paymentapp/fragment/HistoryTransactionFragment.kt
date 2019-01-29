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
import com.example.paymentapp.res.SharedPrefManager
import kotlinx.android.synthetic.main.fragment_history_transaction.*
import java.util.ArrayList

class HistoryTransactionFragment : Fragment(),
    SwipeRefreshLayout.OnRefreshListener,
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
        ctx = activity as Activity
        getAllTransaction(SharedPrefManager.getIdUser(ctx))
    }

    override fun onRefresh() {
        getAllTransaction(SharedPrefManager.getIdUser(ctx))
        swipeRefreshLayoutHistoryTransaction.isRefreshing = !swipeRefreshLayoutHistoryTransaction.isRefreshing
    }

    private fun showError(errors: MutableList<String>){
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

    private fun getAllTransaction(idUser : Long){
        SendGetAllTransaction.newBuilder()
            .setIdPengguna(idUser)
            .setNetworkConfig(networkConfig)
            .setKey("x9O1LkXjyxpRiyhNRX8T", "auth5cur3")
            .setOnSendGetAllTransactionListener(this)
            .send()
    }

    private fun setAdapter (result : ArrayList<TransactionDataModel>, ctx : Context, rvTransaction : RecyclerView){
        val adapter = HistoryTransactionAdapter(ctx, result)
        rvTransaction.layoutManager = LinearLayoutManager(ctx)
        rvTransaction.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}
