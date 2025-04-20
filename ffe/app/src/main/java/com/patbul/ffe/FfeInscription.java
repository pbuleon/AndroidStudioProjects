package com.patbul.ffe;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;
import android.content.Context;

public class FfeInscription{

    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    Activity activity;

    String masterKey;
    SharedPreferences sharedPreferences;

    public FfeInscription(Activity act) {
        activity = act;

            // masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            //sharedPreferences = EncryptedSharedPreferences.create("FFE",masterKey,act, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
            sharedPreferences = act.getSharedPreferences("FFE",Context.MODE_PRIVATE);

    }

    boolean testCompte(String login, String password){

          return true;
    }

    public void saveCompte(String login, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGIN,login);
        editor.putString(PASSWORD,password);
        editor.apply();
        String lll = sharedPreferences.getString(LOGIN,"");


    }

    public String getLogin() {
        String login = sharedPreferences.getString(LOGIN,"");
        return login;
    }
}
