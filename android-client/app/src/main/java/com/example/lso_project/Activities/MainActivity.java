package com.example.lso_project.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lso_project.Activities.LogInActivity.BiometricLoginManager;
import com.example.lso_project.R;
import com.example.lso_project.Activities.LogInActivity.LogInActivity;
import com.example.lso_project.Helpers.UserDatabaseHelper;
import com.example.lso_project.SocketCommunication.BaseSocketCommunication;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // A tag for logging
    public Button biometricLoginButton;
    private final BiometricLoginManager biometricLoginManager = new BiometricLoginManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create local db and get saved user
        String[] savedUser = UserDatabaseHelper.getInstance(this).getUser();
        // set view
        setContentView(R.layout.activity_main);
        // create account button and listener
        Button createAccountBtn = findViewById(R.id.createAccountBtn);
        createAccountBtn.setOnClickListener((x)->
        {
            // setting create account
            LogInActivity.setRegistrationType(LogInActivity.LogInType.CreateAccount);
            Intent goToCreateAccountActivity = new Intent(this, LogInActivity.class);
            startActivity(goToCreateAccountActivity);
        });
        // log in account button and listener
        Button logInAccountBtn = findViewById(R.id.logInAccountBtn);
        logInAccountBtn.setOnClickListener((x)->
        {
            // setting log in
            LogInActivity.setRegistrationType(LogInActivity.LogInType.LogIn);
            Intent goToCreateAccountActivity = new Intent(this, LogInActivity.class);
            startActivity(goToCreateAccountActivity);
        });

        // get biometric account button
        // invisible by default
        biometricLoginButton = findViewById(R.id.biometricAuth);
        biometricLoginButton.setVisibility(View.GONE);
        // check biometric
        biometricLoginManager.Check(savedUser);
        // connection views
        // tls/tcp switch
        Switch tcpTlsSwitch;
        tcpTlsSwitch = findViewById(R.id.tcpTlsSwitch);
        tcpTlsSwitch.setChecked(BaseSocketCommunication.TLS);
        updateTcpTlsSwitch(tcpTlsSwitch);
        tcpTlsSwitch.setOnClickListener((i) ->
        {
            BaseSocketCommunication.TLS = tcpTlsSwitch.isChecked();
            updateTcpTlsSwitch(tcpTlsSwitch);
        });
        // address/ port
        EditText addressText = findViewById(R.id.editTextAddress);
        EditText portText = findViewById(R.id.editTextPort);
        // get values
        addressText.setText(BaseSocketCommunication.SERVER_ADDRESS);
        portText.setText(Integer.valueOf(BaseSocketCommunication.SERVER_PORT).toString());
        // set listeners
        TextWatcher addressTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                // change text
                BaseSocketCommunication.SERVER_ADDRESS = editable.toString();
            }

        };

        TextWatcher portTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                // change text
                BaseSocketCommunication.SERVER_PORT = Integer.parseInt(editable.toString());
            }
        };

        addressText.addTextChangedListener(addressTextWatcher);
        portText.addTextChangedListener(portTextWatcher);

        // set icon
        ImageView icon = findViewById(R.id.iconImg);
        Drawable res = getResources().getDrawable(R.drawable.appicon);
        icon.setImageDrawable(res);
    }

    private  void updateTcpTlsSwitch(Switch s)
    {
        if(s.isChecked())
            s.setText("TLS");
        else
            s.setText("TCP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}