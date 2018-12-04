package com.example.logan.github_test;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;

public class Tools {
    static String getUserPrivateKey(Context c){
        Encryption encryption = new Encryption(c);
        return encryption.decryptString("privateKeyWif");
    }

    static String getAccountName(Context c){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPref.getString("username",null);
    }

    static void saveUserCredentials(String username, String privateKey, Context c){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        sharedPref.edit().putString("username", username).apply();

        Encryption encryption = new Encryption(c);
        encryption.encryptString("privateKeyWif", privateKey);
    }

    static Account loginIfPossible(String userName, String password, Context c) throws InvalidKeyException {
        SteemJConfig myConfig = SteemJConfig.getInstance();
        myConfig.setResponseTimeout(100000);
        myConfig.setDefaultAccount(new AccountName(userName));
        List<ImmutablePair<PrivateKeyType, String>> privateKeys = new ArrayList<>();
        privateKeys.add(new ImmutablePair<>(PrivateKeyType.POSTING, password));

        try {
            myConfig.getPrivateKeyStorage().addAccount(myConfig.getDefaultAccount(), privateKeys);


            Account a = new Account();
            a.setUserName(getAccountName(c));
            return a;
        }catch (Exception e){
            throw new InvalidKeyException();
        }
    }

    static Account loginIfPossible(Context c) throws InvalidKeyException {
        if (getAccountName(c)!=null && getUserPrivateKey(c)!=null){
            return loginIfPossible(getAccountName(c),getUserPrivateKey(c),c);
        }
        return null;
    }

    static void forgetLogin(Context c){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        sharedPref.edit().remove("username").apply();
    }
}
