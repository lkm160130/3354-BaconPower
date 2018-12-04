package com.example.logan.github_test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

import eu.bittrade.libs.steemj.base.models.AccountName;
import eu.bittrade.libs.steemj.configuration.SteemJConfig;
import eu.bittrade.libs.steemj.enums.PrivateKeyType;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText passwordEditText;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.Username);
        passwordEditText = findViewById(R.id.PostingKey);
    }

    public void loginButtonPressed(View v){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();


        try {
            Tools.loginIfPossible(this);
            Tools.saveUserCredentials(username,password,this);
            finish();
            Toast.makeText(this,R.string.login_succeeded,Toast.LENGTH_SHORT).show();
        }catch (InvalidKeyException e){
            e.printStackTrace();
            Toast.makeText(this,R.string.login_failed,Toast.LENGTH_LONG).show();
        }

    }


}
