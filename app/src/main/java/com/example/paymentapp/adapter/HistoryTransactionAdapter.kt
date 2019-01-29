package com.example.paymentapp.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import com.example.modellib.transaction.TransactionDataModel
import com.example.paymentapp.R
import com.squareup.picasso.Picasso
import java.text.DecimalFormat
import java.util.*

class HistoryTransactionAdapter(private val context: Context, var listTransactionData : ArrayList<TransactionDataModel>):
    RecyclerView.Adapter<HistoryTransactionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.list_history_transaction, parent, false)
        return HistoryTransactionAdapter.ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listTransactionData.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTimeTransaction.text = listTransactionData[position].WaktuTransaksi
        holder.textViewIDTransaction.text = listTransactionData[position].IdTransaksi.toString()
        holder.textViewIDSender.text = listTransactionData[position].IdPengirim.toString()
        holder.textViewFullNameSender.text = listTransactionData[position].NamaPengirim
        holder.textViewIDReceiver.text = listTransactionData[position].IdPenerima.toString()
        holder.textViewFullNameReceiver.text = listTransactionData[position].NamaPenerima
        val formatter = DecimalFormat("#,###,###")
        val nominal = formatter.format(listTransactionData[position].Nominal)
        holder.textViewNominal.text = "Rp. $nominal"
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewTimeTransaction = itemView.findViewById(R.id.textViewTimeTransaction) as TextView
        val textViewIDTransaction = itemView.findViewById(R.id.textViewIDTransaction) as TextView
        val textViewIDSender = itemView.findViewById(R.id.textViewIDSender) as TextView
        val textViewFullNameSender = itemView.findViewById(R.id.textViewFullNameSender) as TextView
        val textViewIDReceiver = itemView.findViewById(R.id.textViewIDReceiver) as TextView
        val textViewFullNameReceiver = itemView.findViewById(R.id.textViewFullNameReceiver) as TextView
        val textViewNominal = itemView.findViewById(R.id.textViewNominal) as TextView
    }
}