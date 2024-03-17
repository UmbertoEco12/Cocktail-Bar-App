package com.example.lso_project.Activities.LogInActivity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.example.lso_project.StaticInstances.CurrentUser;
import com.example.lso_project.Activities.MainActivity;

public class ChangePasswordUIManager implements ILogInUIManager{
    LogInActivity activity;
    @Override
    public void setup(LogInActivity activity) {
        this.activity = activity;
        // hide username field
        activity.getUsernameTextView().setVisibility(View.GONE);
        activity.getUsernameEditText().setVisibility(View.GONE);
        // change password text
        activity.getPasswordTextView().setText("New Password");

        activity.getLogInBtn().setText("Change Password");
    }

    @Override
    public boolean checkLogin(LogInActivity activity) {
        // check empty fields
        if( TextUtils.isEmpty(activity.getPasswordEditText().getText()) )
        {
            // show error
            activity.showError("Enter a new password first");
            return false;
        }
        // check if password is the same

        if( TextUtils.equals(activity.getPasswordEditText().getText(),activity.getReEnterPasswordEditText().getText()) )
        {
            // check password
            CurrentUser.checkPasswordUpdate(
                    activity.getPasswordEditText().getText().toString(),
                    this::handleResponse);

            return true;
        }
        else
        {
            // show error
            activity.showError("Those passwords didn't match. Try again.");
            return false;
        }
    }
    // handle server response
    private void handleResponse(String response)
    {
        // logout
        CurrentUser.Logout();
        // travel to main activity to try and log in
        Intent myIntent = new Intent(activity, MainActivity.class);
        activity.startActivity(myIntent);
    }
}
