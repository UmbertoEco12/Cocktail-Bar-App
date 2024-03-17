package com.example.lso_project.Activities.PaymentActivity;

import android.content.Intent;

import com.example.lso_project.StaticInstances.CurrentCart;

public class PaymentManager implements IHandlePaymentManager{

    @Override
    public void setup(PaymentActivity activity) {
        activity.totalText.setText(String.format("Total: %.2f", CurrentCart.getTotal()));
    }

    @Override
    public void onButtonPressed(PaymentActivity activity) {

        // set payment info
        PaymentDoneActivity.setPaymentInfo(activity.getCreditCardData());

        // travel to payment done
        Intent intent = new Intent(activity, PaymentDoneActivity.class);
        activity.startActivity(intent);
    }
}
