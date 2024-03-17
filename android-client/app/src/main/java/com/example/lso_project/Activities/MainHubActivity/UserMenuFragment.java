package com.example.lso_project.Activities.MainHubActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lso_project.R;
import com.example.lso_project.StaticInstances.CurrentUser;
import com.example.lso_project.Activities.LogInActivity.LogInActivity;

public class UserMenuFragment extends Fragment {

    // main activity
    public MainHubActivity MainActivity;
    // fragment
    private PaymentOptionsFragment paymentOptionsFragment;

    public UserMenuFragment() {
        // Required empty public constructor
    }
    // returns a new instance of UserMenuFragment
    public static UserMenuFragment newInstance(MainHubActivity mainHubActivity) {
        UserMenuFragment fragment = new UserMenuFragment();
        fragment.MainActivity = mainHubActivity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paymentOptionsFragment = PaymentOptionsFragment.newInstance(MainActivity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_user_menu, container, false);
        // get views
        TextView welcomeText = thisView.findViewById(R.id.helloUserText);
        Button changeUsernameButton = thisView.findViewById(R.id.changeUsernameBtn);
        Button changePasswordButton = thisView.findViewById(R.id.changePasswordBtn);
        Button paymentMethodBtn = thisView.findViewById(R.id.paymentMethodBtn);
        Button logOutButton = thisView.findViewById(R.id.logOutBtn);
        Button biometricBtn = thisView.findViewById(R.id.biometricAuthBtn);
        // init
        // welcome text
        welcomeText.setText(String.format("Hello %s,", CurrentUser.getUsername()));
        // change username on click
        changeUsernameButton.setOnClickListener((x) ->
        {
            LogInActivity.setRegistrationType(LogInActivity.LogInType.ChangeUsername);
            Intent myIntent = new Intent(MainActivity, LogInActivity.class);
            MainActivity.startActivity(myIntent);
        });
        // change password on click
        changePasswordButton.setOnClickListener((x) ->
        {
            LogInActivity.setRegistrationType(LogInActivity.LogInType.ChangePassword);
            Intent myIntent = new Intent(MainActivity, LogInActivity.class);
            MainActivity.startActivity(myIntent);
        });
        // change payment methods on click
        paymentMethodBtn.setOnClickListener((x)->
        {
            MainActivity.changeFragment(paymentOptionsFragment);
        });
        // log out on click
        logOutButton.setOnClickListener(this::logOut);


        //biometricAuth();
        biometricBtn.setVisibility(View.GONE);

        return thisView;
    }

//    private Executor executor;
//    private BiometricPrompt biometricPrompt;
//    private BiometricPrompt.PromptInfo promptInfo;
//
//    private void biometricAuth()
//    {
//        executor = ContextCompat.getMainExecutor(getContext());
//        biometricPrompt = new BiometricPrompt(this,
//                executor, new BiometricPrompt.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode,
//                                              @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                Toast.makeText(getContext(),
//                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
//                        .show();
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(
//                    @NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//                Toast.makeText(getContext(),
//                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
//                BiometricAuthHelper.insertBiometric(result, (res) ->
//                {
//                    String s;
//                    if(TextUtils.equals(res, "OK"))
//                    {
//                        s = "Succeeded";
//                    }
//                    else
//                    {
//                        s = "Failed";
//                    }
//                    getActivity().runOnUiThread(() ->
//                    {
//                        // show toast
//                        Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
//                    });
//                });
//
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                Toast.makeText(getContext(), "Authentication failed",
//                                Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//
//        promptInfo = new BiometricPrompt.PromptInfo.Builder()
//                .setTitle("Biometric login for my app")
//                .setSubtitle("Log in using your biometric credential")
//                .setNegativeButtonText("Use account password")
//                .build();
//
//        // Prompt appears when user clicks "Log in".
//        // Consider integrating with the keystore to unlock cryptographic operations,
//        // if needed by your app.
//        biometricBtn.setOnClickListener(view -> {
//            try{
//                Cipher cipher = BiometricAuthHelper.getCipher();
//                SecretKey secretKey = BiometricAuthHelper.getSecretKey();
//                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//                biometricPrompt.authenticate(promptInfo,
//                        new BiometricPrompt.CryptoObject(cipher));
//                //promptInfo.
//                biometricPrompt.authenticate(promptInfo);
//            }
//            catch (Exception e)
//            {
//                e.printStackTrace();
//            }
//        });
//    }

    // called when log out button is clicked
    private void logOut(View btn)
    {
        // create alert dialog with Yes and No options
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to log out?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialogInterface, i) -> goToMainMenu());
        builder.setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // if logout is accepted goes to the start menu
    private void goToMainMenu()
    {
        // logout from user
        CurrentUser.Logout();
        // travel to main hub with new account
        Intent myIntent = new Intent(getContext(), com.example.lso_project.Activities.MainActivity.class);
        startActivity(myIntent);
    }
}