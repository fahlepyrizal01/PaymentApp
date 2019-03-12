package com.example.grpcservicelib.user_grpc;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import com.example.grpcservicelib.castingModel.UserDataModelCasting;
import com.example.modellib.networkConfig.NetworkConfig;
import com.example.modellib.user.UserDataModel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
import pengguna.Pengguna;
import pengguna.penggunaServiceGrpc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.modellib.StaticVariable.Authorization;
import static com.example.modellib.StaticVariable.PORT;
import static com.example.modellib.StaticVariable.URL;

public class SendGetRegisterUser extends AsyncTask<Void, Void, Pengguna.penggunaData> {

    private static SendGetRegisterUser _instance;
    private String ClientKey, ClientToken;
    private NetworkConfig networkConfig;
    private Metadata header = new Metadata();
    private UserDataModel userDataModel = new UserDataModel();
    private ManagedChannel channel;
    private SendGetRegisterUser.OnSendGetRegisterUserListener listener;
    private ArrayList<String> Errors = new ArrayList<>();

    public static SendGetRegisterUser newBuilder() {
        _instance = new SendGetRegisterUser();
        return _instance;
    }

    public SendGetRegisterUser setKey(String ClientKey, String ClientToken) {
        _instance.ClientToken = ClientToken;
        _instance.ClientKey = ClientKey;
        return _instance;
    }

    public SendGetRegisterUser setNetworkConfig(NetworkConfig networkConfig) {
        _instance.networkConfig = networkConfig;
        return _instance;
    }

    public SendGetRegisterUser setUserData(String NameUser, String Password, String PhoneNumber, String Address,
                                           String Email, Float Balance, String Gender, String BirthDay) {
        _instance.userDataModel.NamaPengguna = NameUser;
        _instance.userDataModel.Password = Password;
        _instance.userDataModel.NomorTelepon = PhoneNumber;
        _instance.userDataModel.Alamat = Address;
        _instance.userDataModel.Email = Email;
        _instance.userDataModel.Balance = Balance;
        _instance.userDataModel.JenisKelamin = Gender;
        _instance.userDataModel.TanggalLahir = BirthDay;
        return _instance;
    }

    public SendGetRegisterUser setOnSendGetRegisterUserListener(SendGetRegisterUser.OnSendGetRegisterUserListener listener) {
        _instance.listener = listener;
        return _instance;
    }

    public void send() {
        if (_instance != null) {
            _instance.execute();
        }
    }

    private SendGetRegisterUser() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Metadata.Key<String> key = Metadata.Key.of(Authorization, Metadata.ASCII_STRING_MARSHALLER);
        _instance.header.put(key, _instance.ClientToken == null ? "" : _instance.ClientToken);
        _instance.header.put(key, _instance.ClientKey == null ? "" : _instance.ClientKey);
        _instance.channel = ManagedChannelBuilder
                .forAddress(
                        _instance.networkConfig != null ? _instance.networkConfig.getUrl() : URL,
                        _instance.networkConfig != null ? _instance.networkConfig.getPort() : PORT
                )
                .usePlaintext(true)
                .build();
    }

    @Override
    protected Pengguna.penggunaData doInBackground(Void... voids) {

        Pengguna.penggunaData response = null;
        try {
            penggunaServiceGrpc.penggunaServiceBlockingStub stub =
                    penggunaServiceGrpc.newBlockingStub(_instance.channel);

            stub = MetadataUtils.attachHeaders(stub, _instance.header);

            response = stub.registerPengguna(UserDataModelCasting.toUserDataModelGRPC(
                    _instance.userDataModel));
        } catch (Exception e) {
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
    protected void onPostExecute(Pengguna.penggunaData userData) {
        super.onPostExecute(userData);

        if (userData != null && userData.getErrorsMessageList() != null && userData.getErrorsMessageCount() > 0) {
            Errors.addAll(userData.getErrorsMessageList());
        }

        try {
            _instance.channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {

            _instance.Errors.add(e.getMessage());

            if (_instance.listener != null) {
                _instance.listener.onErrorGetRegisterUser(Errors);
            }
        }

        if (_instance.Errors.size() > 0 && _instance.listener != null) {
            _instance.listener.onErrorGetRegisterUser(_instance.Errors);
        }

        if (_instance.listener != null) {
            _instance.listener.onGetRegisterUser(userData == null ? "" : userData.getEmail());
        }
    }

    public interface OnSendGetRegisterUserListener {
        void onErrorGetRegisterUser(@NonNull List<String> errors);

        void onGetRegisterUser(@NonNull String email);
    }
}