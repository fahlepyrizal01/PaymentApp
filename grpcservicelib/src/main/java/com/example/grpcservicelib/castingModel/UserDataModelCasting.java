package com.example.grpcservicelib.castingModel;

import com.example.modellib.user.UserDataModel;
import pengguna.Pengguna;

public class UserDataModelCasting {

    public static UserDataModel toUserDataModel(Pengguna.penggunaData userData){
        UserDataModel model = new UserDataModel();
        try {

            model.IdPengguna = userData.getIdPengguna();
            model.NamaPengguna = userData.getNamaPengguna();
            model.Password = userData.getPassword();
            model.NomorTelepon = userData.getNomorTelepon();
            model.Alamat = userData.getAlamat();
            model.Email = userData.getEmail();
            model.Balance = userData.getBalance();
            model.JenisKelamin = userData.getJenisKelamin();
            model.TanggalLahir = userData.getTanggalLahir();


        }catch (NullPointerException e){
            return model;
        }

        return model;
    }

    public static Pengguna.penggunaData toUserDataModelGRPC(UserDataModel userData){
        return Pengguna.penggunaData
                .newBuilder()
                .setIdPengguna(userData.IdPengguna)
                .setNamaPengguna(userData.NamaPengguna)
                .setPassword(userData.Password)
                .setNomorTelepon(userData.NomorTelepon)
                .setAlamat(userData.Alamat)
                .setEmail(userData.Email)
                .setBalance(userData.Balance)
                .setJenisKelamin(userData.JenisKelamin)
                .setTanggalLahir(userData.TanggalLahir)
                .build();
    }
}
