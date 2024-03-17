package com.example.lso_project.Activities.LogInActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lso_project.R;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity {
    // which type should handle the activity
    public enum LogInType
    {
        CreateAccount,
        LogIn,
        ChangeUsername,
        ChangePassword
    }
    private static LogInType s_registrationType;
    // views
    private TextView reEnterPasswordTextView;
    private TextView passwordTextView;
    private TextView usernameTextView;
    private EditText reEnterPasswordEditText;
    private EditText passwordEditText;
    private EditText usernameEditText;
    private TextView errorTextView;
    private Button logInBtn;

    // activity managers
    private final Map<LogInType, ILogInUIManager> logInManagers = new HashMap<>();

    public LogInActivity()
    {
        // create ui managers
        logInManagers.put(LogInType.CreateAccount, new CreateAccountUIManager());
        logInManagers.put(LogInType.LogIn, new LogInUIManager());
        logInManagers.put(LogInType.ChangeUsername, new ChangeUsernameUIManager());
        logInManagers.put(LogInType.ChangePassword, new ChangePasswordUIManager());
    }
    // getters and setters
    public EditText getReEnterPasswordEditText() {return reEnterPasswordEditText;}
    public EditText getPasswordEditText() {return passwordEditText;}
    public EditText getUsernameEditText() {return usernameEditText;}

    public static void setRegistrationType(LogInType registrationType) {
        LogInActivity.s_registrationType = registrationType;
    }

    public static LogInType getRegistrationType() {
        return s_registrationType;
    }

    public TextView getReEnterPasswordTextView() {
        return reEnterPasswordTextView;
    }

    public TextView getPasswordTextView() {
        return passwordTextView;
    }

    public TextView getUsernameTextView() {
        return usernameTextView;
    }

    public Button getLogInBtn() {
        return logInBtn;
    }

    // sets the text of the error TextView and sets it visible
    public void showError(String error)
    {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(String.format("Error: %s",error));
    }
    // same but makes sure it runs on ui thread
    public void showErrorPost(String error)
    {
        errorTextView.post(() ->
        {
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(String.format("Error: %s",error));
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        // get views
        logInBtn = findViewById(R.id.logInBtn);
        usernameTextView = findViewById(R.id.usernameTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        reEnterPasswordTextView = findViewById(R.id.reInsertPasswordTextView);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        reEnterPasswordEditText = findViewById(R.id.reInsertPasswordEditText);
        errorTextView = findViewById(R.id.errorTextView);

        // hide error
        errorTextView.setVisibility(View.INVISIBLE);

        // on button click
        logInBtn.setOnClickListener(this::OnLogInButtonClick);

        // setup
        logInManagers.get(s_registrationType).setup(this);
    }

    // calls the manager check function when clicked on the button
    private void OnLogInButtonClick(View btn)
    {
        logInManagers.get(s_registrationType).checkLogin(this);
    }

}