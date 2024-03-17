package com.example.lso_project.Activities.LogInActivity;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.hardware.biometrics.BiometricManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.lso_project.Activities.LogInActivity.LogInActivity;
import com.example.lso_project.Activities.MainActivity;
import com.example.lso_project.Activities.MainHubActivity.MainHubActivity;
import com.example.lso_project.Helpers.BiometricAuthHelper;
import com.example.lso_project.Helpers.IEvent;
import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class BiometricLoginManager {

    private static class AuthBiometricManager extends BiometricPrompt.AuthenticationCallback
    {
        private MainActivity mainActivity;
        private IEvent onSucceeded;
        public AuthBiometricManager(MainActivity mainActivity, IEvent onSucceeded)
        {
            this.mainActivity = mainActivity;
            this.onSucceeded = onSucceeded;
        }

        @Override
        public void onAuthenticationError(int errorCode,
                                          @NonNull CharSequence errString) {
            super.onAuthenticationError(errorCode, errString);
            Toast.makeText(mainActivity.getApplicationContext(),
                            "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
            Toast.makeText(mainActivity.getApplicationContext(),
                    "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            onSucceeded.run();
        }

        @Override
        public void onAuthenticationFailed() {
            super.onAuthenticationFailed();
            Toast.makeText(mainActivity.getApplicationContext(), "Authentication failed",
                            Toast.LENGTH_SHORT).show();
        }
    }

    private MainActivity mainActivity;
    private static final String TAG = "BiometricLoginManager";
    public BiometricLoginManager(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }

    public void Check(String[] savedUser)
    {
        // get biometric manager
        BiometricManager biometricManager = mainActivity.getApplicationContext().getSystemService(BiometricManager.class);
        // if the device has the necessary hardware
        if(biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL) ==  BiometricManager.BIOMETRIC_SUCCESS)
        {
            // check saved users
            if(savedUser != null)
            {
                Log.d(TAG, savedUser[0] + " " + savedUser[1]);
                // can login with biometrics
                BiometricAuth(savedUser);
                mainActivity.biometricLoginButton.setVisibility(View.VISIBLE);
            }
        }
    }


    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private void BiometricAuth(String[] savedUser)
    {
        BiometricAuthHelper.generateKey();
        Executor executor = ContextCompat.getMainExecutor(mainActivity);
        // prompt setup
        biometricPrompt = new BiometricPrompt(mainActivity,
                executor, new AuthBiometricManager(mainActivity,
                () -> loginOnSucceed(savedUser)));

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Biometric".
        mainActivity.biometricLoginButton.setOnClickListener(view -> {
            try{
                Cipher cipher = BiometricAuthHelper.getCipher();
                SecretKey secretKey = BiometricAuthHelper.getSecretKey();
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                biometricPrompt.authenticate(promptInfo,
                        new BiometricPrompt.CryptoObject(cipher));
                //promptInfo.
                biometricPrompt.authenticate(promptInfo);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        });
    }

    private void loginOnSucceed(String[] savedUser)
    {
        // if auth succeeds check if previous user is registered in the server
        CurrentUser.checkLoginHashed(savedUser[0],savedUser[1],(r) -> handleLogin(r));
    }
    // checks login with biometric auth
    private void handleLogin(String response)
    {
        mainActivity.runOnUiThread(() ->
        {
            // if there is an error a toast appears and the activity gets changed to the login activity
            if(response.equals("WRONG_USERNAME"))
            {
                Toast.makeText(mainActivity.getApplicationContext(), "Error: Couldn't find your username",
                                Toast.LENGTH_SHORT)
                        .show();
                goToLoginActivity();
                return;
            }
            else if(response.equals("WRONG_PASSWORD"))
            {
                Toast.makeText(mainActivity.getApplicationContext(), "Error: wrong password.",
                                Toast.LENGTH_SHORT)
                        .show();
                goToLoginActivity();
                return;
            }
            // no error travel to main hub
            goToMainHubActivity();
        });
    }

    private void goToMainHubActivity()
    {
        // travel to main hub
        Intent myIntent = new Intent(mainActivity, MainHubActivity.class);
        mainActivity.startActivity(myIntent);
    }

    private void goToLoginActivity()
    {
        // travels to the log in activity
        LogInActivity.setRegistrationType(LogInActivity.LogInType.LogIn);
        Intent goToCreateAccountActivity = new Intent(mainActivity, LogInActivity.class);
        mainActivity.startActivity(goToCreateAccountActivity);
    }
}
