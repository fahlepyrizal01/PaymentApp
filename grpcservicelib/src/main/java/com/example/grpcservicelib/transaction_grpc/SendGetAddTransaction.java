package com.example.grpcservicelib.transaction_grpc;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.example.grpcservicelib.castingModel.TransactionDataModelCasting;
import com.example.modellib.networkConfig.NetworkConfig;
import com.example.modellib.transaction.TransactionDataModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import transaksi.Transaksi;
import transaksi.transaksiServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.modellib.StaticVariable.Authorization;
import static com.example.modellib.StaticVariable.PORT;
import static com.example.modellib.StaticVariable.URL;

public class SendGetAddTransaction extends AsyncTask<Void, Void, Transaksi.transaksiData> {

    private static SendGetAddTransaction _instance;
    private String ClientKey,ClientToken;
    private NetworkConfig networkConfig;
    private Metadata header = new Metadata();
    private TransactionDataModel transactionDataModel = new TransactionDataModel();
    private ManagedChannel channel;
    private OnSendGetAddTransactionListener listener;
    private ArrayList<String> Errors = new ArrayList<>();

    public static SendGetAddTransaction newBuilder(){
        _instance = new SendGetAddTransaction();
        return _instance;
    }

    public SendGetAddTransaction setKey(String ClientKey, String ClientToken) {
        _instance.ClientToken = ClientToken;
        _instance.ClientKey = ClientKey;
        return _instance;
    }

    public SendGetAddTransaction setNetworkConfig(NetworkConfig networkConfig) {
        _instance.networkConfig = networkConfig;
        return _instance;
    }

    public SendGetAddTransaction setTransactionData(Long IdPengirim, Long IdPenerima,
                                                float Nominal, String WaktuTransaksi) {
        _instance.transactionDataModel.IdPengirim = IdPengirim;
        _instance.transactionDataModel.IdPenerima = IdPenerima;
        _instance.transactionDataModel.Nominal = Nominal;
        _instance.transactionDataModel.WaktuTransaksi = WaktuTransaksi;
        return _instance;
    }

    public SendGetAddTransaction setOnSendGetAddTransactionListener(OnSendGetAddTransactionListener listener) {
        _instance.listener = listener;
        return _instance;
    }

    public void send(){
        if (_instance != null){
            _instance.execute();
        }
    }

    private SendGetAddTransaction() { }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Metadata.Key<String> key = Metadata.Key.of(Authorization, Metadata.ASCII_STRING_MARSHALLER);
        _instance.header.put(key, _instance.ClientToken == null ? "" : _instance.ClientToken);
        _instance.header.put(key, _instance.ClientKey== null ? "" : _instance.ClientKey);
        _instance.channel = ManagedChannelBuilder
                .forAddress(
                        _instance.networkConfig != null ? _instance.networkConfig.getUrl() : URL,
                        _instance.networkConfig != null ? _instance.networkConfig.getPort() : PORT
                )
                .usePlaintext(true)
                .build();
    }

    @Override
    protected Transaksi.transaksiData doInBackground(Void... voids) {

        Transaksi.transaksiData response = null;
        try {
            transaksiServiceGrpc.transaksiServiceBlockingStub stub =
                    transaksiServiceGrpc.newBlockingStub(_instance.channel);

            stub = MetadataUtils.attachHeaders(stub,_instance.header);

            response = stub.addTransaksi(TransactionDataModelCasting
                    .toTransactionDataModelGRPC(_instance.transactionDataModel));
        }catch (Exception e) {
            Errors.add(e.getMessage());
        }

        Errors.add(_instance.ClientKey == null && _instance.ClientToken == null ?
                "All key not set!" : _instance.ClientKey == null ?
                "Client key not set!" :
                _instance.ClientToken == null ?
                        "Token key not set!" :
                        "");

        return response;
    }



    @Override
    protected void onPostExecute(Transaksi.transaksiData transactionData) {
        super.onPostExecute(transactionData);

        if (transactionData!=null && transactionData.getErrorsMessageList() != null && transactionData.getErrorsMessageCount() > 0){
            Errors.addAll(transactionData.getErrorsMessageList());
        }

        try {
            _instance.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

            _instance.Errors.add(e.getMessage());

            if (_instance.listener != null) {
                _instance.listener.onErrorGetAddTransaction(Errors);
            }
        }

        if (_instance.Errors.size() > 0 && _instance.listener != null){
            _instance.listener.onErrorGetAddTransaction(_instance.Errors);
        }

        if (_instance.listener != null){
            _instance.listener.onGetAddTransaction(transactionData == null ? 0 : transactionData.getIdTransaksi());
        }
    }

    public interface OnSendGetAddTransactionListener {
        void onErrorGetAddTransaction(@NonNull List<String> errors);
        void onGetAddTransaction(@NonNull Long idTransaction);
    }
}
