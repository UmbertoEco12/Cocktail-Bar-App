package com.example.lso_project.Activities.LogInActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.example.lso_project.StaticInstances.CurrentUser;
import com.example.lso_project.Activities.MainActivity;


public class ChangeUsernameUIManager implements ILogInUIManager{
    LogInActivity activity;

    @Override
    public void setup(LogInActivity activity) {
        this.activity = activity;
        // change username text
        activity.getUsernameTextView().setText("New Username");
        // hide password fields
        activity.getPasswordTextView().setVisibility(View.GONE);
        activity.getPasswordEditText().setVisibility(View.GONE);
        activity.getReEnterPasswordTextView().setVisibility(View.GONE);
        activity.getReEnterPasswordEditText().setVisibility(View.GONE);

        activity.getLogInBtn().setText("Change Username");
    }

    @Override
    public boolean checkLogin(LogInActivity activity) {
        // check if is empty
        if( TextUtils.isEmpty(activity.getUsernameEditText().getText()) )
        {
            activity.showError("Enter a new username");
            return false;
        }
        // check if it is equal to the old
        if( TextUtils.equals(activity.getUsernameEditText().getText(), CurrentUser.getUsername()) )
        {
            activity.showError("Enter a new username");
            return false;
        }
        // check availability in db
        CurrentUser.checkUsernameUpdate(
                activity.getUsernameEditText().getText().toString()
                ,this::handleResponse);
        return true;
    }

    private void handleResponse(String response)
    {
        if(!response.equals("OK"))
        {
            activity.showErrorPost("This username is already taken");
            return;
        }
        // logout
        CurrentUser.Logout();
        // travel to main activity to try and log in
        Intent myIntent = new Intent(activity, MainActivity.class);
        activity.startActivity(myIntent);
    }
}
