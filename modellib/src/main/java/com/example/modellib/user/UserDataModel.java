package com.example.modellib.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDataModel {

    @SerializedName("id_pengguna")
    @Expose
    public Long IdPengguna = 0l;

    @SerializedName("nama_pengguna")
    @Expose
    public String NamaPengguna = "";

    @SerializedName("password")
    @Expose
    public String Password = "";

    @SerializedName("nomor_telepon")
    @Expose
    public String NomorTelepon = "";

    @SerializedName("alamat")
    @Expose
    public String Alamat = "";

    @SerializedName("email")
    @Expose
    public String Email = "";

    @SerializedName("balance")
    @Expose
    public Float Balance = 0f;

    @SerializedName("url_foto_profil")
    @Expose
    public String UrlFotoProfil = "";

    public UserDataModel() {
        super();
    }
}
