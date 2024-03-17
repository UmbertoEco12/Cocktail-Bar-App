package com.example.lso_project.Activities.PaymentActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.lso_project.Activities.MainHubActivity.MainHubActivity;
import com.example.lso_project.R;
import com.example.lso_project.StaticInstances.CurrentCart;

public class PaymentDoneActivity extends AppCompatActivity {

    private TextView loadingText;
    private boolean orderSucceeded;

    private static CreditCardData paymentInfo;

    public static void setPaymentInfo(CreditCardData data)
    {
        paymentInfo = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_done);
        // set order as failed
        orderSucceeded = false;
        // place order
        CurrentCart.sendOrder(this::orderResponse, paymentInfo);
        // get fields
        loadingText = findViewById(R.id.loadingText);
        // set text
        loadingText.setText("Loading ...");
        // create and start handler
        Handler loadingHandler = new Handler(Looper.getMainLooper());
        loadingHandler.postDelayed(this::goToMainHub
        , 3000);   //5 seconds
    }

    private void goToMainHub()
    {
        // create and start handler
        Handler doneHandler = new Handler(Looper.getMainLooper());
        doneHandler.postDelayed( () ->
        {
            if(orderSucceeded)
            {
                // set text as done
                loadingText.setText("Done.");
            }
            else
            {
                // set text as failed
                loadingText.setText("Failed.");
                // stops the thread
                CurrentCart.stopOrder();
            }

            // go to main hub activity
            Intent intent = new Intent(this, MainHubActivity.class);
            startActivity(intent);
        }, 1000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void orderResponse(String msg)
    {
        // true if msg is OK
        orderSucceeded = TextUtils.equals(msg, "OK");
    }

    @Override
    public void onBackPressed() {

    }
}