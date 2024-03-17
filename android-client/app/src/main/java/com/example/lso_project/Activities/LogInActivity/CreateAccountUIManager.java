package com.example.lso_project.Activities.LogInActivity;

import android.content.Intent;
import android.text.TextUtils;

import com.example.lso_project.StaticInstances.CurrentUser;
import com.example.lso_project.Activities.MainHubActivity.MainHubActivity;


public class CreateAccountUIManager implements ILogInUIManager{
    // log in activity
    private LogInActivity activity;
    @Override
    public void setup(LogInActivity activity) {
        this.activity = activity;
        activity.getLogInBtn().setText("Create Account");
    }

    @Override
    public boolean checkLogin(LogInActivity activity) {
        // check empty fields
        if( TextUtils.isEmpty(activity.getUsernameEditText().getText()) || TextUtils.isEmpty(activity.getPasswordEditText().getText()) )
        {
            // show error
            activity.showError("Fill all the fields to continue.");
            return false;
        }
        // check if passwords match

        if( TextUtils.equals(activity.getPasswordEditText().getText(),activity.getReEnterPasswordEditText().getText()) )
        {
            //ok can proceed
            //check username availability
            CurrentUser.checkRegistration(activity.getUsernameEditText().getText().toString(),
                    activity.getPasswordEditText().getText().toString(), this::handleResponse);
            return true;
        }
        else
        {
            // show error
            activity.showError("Those passwords didn't match. Try again.");
            return false;
        }
    }

    public void handleResponse(String response)
    {
        if(!response.equals("OK"))
        {
            activity.showErrorPost("This username is already taken");
            return;
        }
        // travel to login activity
        LogInActivity.setRegistrationType(LogInActivity.LogInType.LogIn);
        Intent myIntent = new Intent(activity, LogInActivity.class);
        activity.startActivity(myIntent);
    }
}
