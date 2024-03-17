package com.example.lso_project.Activities.PaymentActivity;

import android.content.Intent;
import android.view.View;

import com.example.lso_project.Activities.MainHubActivity.MainHubActivity;
import com.example.lso_project.StaticInstances.CurrentUser;

public class AddPaymentManager implements IHandlePaymentManager{
    @Override
    public void setup(PaymentActivity activity) {
        activity.totalText.setVisibility(View.INVISIBLE);
        activity.payButton.setText("Save");
        activity.selectSavedCard.setVisibility(View.GONE);
    }

    @Override
    public void onButtonPressed(PaymentActivity activity) {
        // get card
        CreditCardData data = activity.getCreditCardData();
        // add card
        CurrentUser.addPaymentMethodToServer(data);
        // travel to main hub
        Intent intent = new Intent(activity, MainHubActivity.class);
        activity.startActivity(intent);
    }
}
