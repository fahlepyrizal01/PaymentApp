package com.example.grpcservicelib.castingModel;

import com.example.modellib.transaction.TransactionDataModel;
import transaksi.Transaksi;

public class TransactionDataModelCasting {

    public static TransactionDataModel toTransactionDataModel(Transaksi.transaksiData transactionData){
        TransactionDataModel model = new TransactionDataModel();
        try {

            model.IdTransaksi = transactionData.getIdTransaksi();
            model.IdPengirim = transactionData.getIdPengirim();
            model.IdPenerima = transactionData.getIdPenerima();
            model.Nominal = transactionData.getNominal();
            model.WaktuTransaksi = transactionData.getWaktuTransaksi();
            model.NamaPengirim = transactionData.getNamaPengirim();
            model.NamaPenerima = transactionData.getNamaPenerima();

        }catch (NullPointerException e){
            return model;
        }

        return model;
    }

    public static Transaksi.transaksiData toTransactionDataModelGRPC(TransactionDataModel trasactionData){
        return Transaksi.transaksiData
                .newBuilder()
                .setIdTransaksi(trasactionData.IdTransaksi)
                .setIdPengirim(trasactionData.IdPengirim)
                .setIdPenerima(trasactionData.IdPenerima)
                .setNominal(trasactionData.Nominal)
                .setWaktuTransaksi(trasactionData.WaktuTransaksi)
                .setNamaPengirim(trasactionData.NamaPengirim)
                .setNamaPenerima(trasactionData.NamaPenerima)
                .build();
    }
}
