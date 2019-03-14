package com.example.modellib.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDataModel {

    @SerializedName("id_transaksi")
    @Expose
    public Long IdTransaksi = 0L;

    @SerializedName("id_pengirim")
    @Expose
    public Long IdPengirim = 0L;

    @SerializedName("id_penerima")
    @Expose
    public Long IdPenerima = 0L;

    @SerializedName("nominal")
    @Expose
    public Float Nominal = 0f;

    @SerializedName("waktu_transaksi")
    @Expose
    public String WaktuTransaksi = "";

    @SerializedName("nama_pengirim")
    @Expose
    public String NamaPengirim = "";

    @SerializedName("nama_penerima")
    @Expose
    public String NamaPenerima = "";

    public TransactionDataModel() {
        super();
    }
}
