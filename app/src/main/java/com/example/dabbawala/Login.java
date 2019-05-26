package com.example.dabbawala;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

public class Login extends AppCompatActivity implements View.OnClickListener {

    Button btnAdmin,btnRestaurent,btnCustomer;
    private static final int RC_SIGN_IN = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnAdmin = (Button)findViewById(R.id.btnAdmin);
        btnRestaurent = (Button)findViewById(R.id.btnRestaurant);
        btnCustomer = (Button)findViewById(R.id.btnCustomer);
        btnCustomer.setOnClickListener(this);
        btnRestaurent.setOnClickListener(this);
        btnAdmin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences sP = getSharedPreferences("Login type",0);
        SharedPreferences.Editor edit = sP.edit();
        if(v.getId() == R.id.btnAdmin)
        {
            edit.putString("Login type","Admin");
        }
        else if(v.getId() == R.id.btnCustomer)
        {
            edit.putString("Login type","Customer");
        }
        else if(v.getId() == R.id.btnRestaurant)
        {
            edit.putString("Login type","Restaurant");
        }
        edit.apply();
        edit.commit();

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTosUrl("https://superapp.example.com/terms-of-service.html")
                        .setPrivacyPolicyUrl("https://superapp.example.com/privacy-policy.html")
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                        new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),

                                        new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()

                                ))
                        .build(),
                RC_SIGN_IN);
        finish();
    }
}
