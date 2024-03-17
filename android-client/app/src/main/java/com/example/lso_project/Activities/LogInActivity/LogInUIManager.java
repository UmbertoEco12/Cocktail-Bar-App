package com.example.lso_project.Activities.LogInActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.example.lso_project.StaticInstances.CurrentUser;
import com.example.lso_project.Activities.MainHubActivity.MainHubActivity;

public class LogInUIManager implements ILogInUIManager {
    // log in activity
    private LogInActivity activity;
    @Override
    public void setup(LogInActivity activity) {
        this.activity = activity;
        // hide password confirmation field
        activity.getReEnterPasswordEditText().setVisibility(View.GONE);
        activity.getReEnterPasswordTextView().setVisibility(View.GONE);
        // change button text
        activity.getLogInBtn().setText("Log in");
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
        // check username and password
        CurrentUser.checkLogin(activity.getUsernameEditText().getText().toString(),
                activity.getPasswordEditText().getText().toString(), this::handleResponse);
        return true;
    }
    // handle server response
    private void handleResponse(String response)
    {
        // if response is negative show error
        if(response.equals("WRONG_USERNAME"))
        {
            activity.showErrorPost("Couldn't find your username");
            return;
        }
        else if(response.equals("WRONG_PASSWORD"))
        {
            activity.showErrorPost("Wrong password");
            return;
        }
        // response is positive
        // travel to main hub with new account
        Intent myIntent = new Intent(activity, MainHubActivity.class);
        activity.startActivity(myIntent);
    }
}
