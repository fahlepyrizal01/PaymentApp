package com.example.paymentapp.res;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPrefManager {

    public static final String SP_ID = "spID";

    public static final String SP_LOGGED_IN_PREF = "spLoggedinStatus";

    static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(SP_LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static void setID(Context context, Long idUser) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putLong(SP_ID, idUser);
        editor.apply();
    }

    public static long getIdUser(Context context) {
        return getPreferences(context).getLong(SP_ID,0);
    }

    public static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(SP_LOGGED_IN_PREF, false);
    }
}
