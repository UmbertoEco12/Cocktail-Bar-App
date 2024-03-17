package com.example.lso_project.SocketCommunication.UserPayment;

import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;

import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.IOException;

public class SocketAddPaymentMethodRequest extends BaseSocketCommunication {
    // constructor
    private SocketAddPaymentMethodRequest()
    {
        super();
    }
    // Singleton
    private static SocketAddPaymentMethodRequest instance = null;

    public static SocketAddPaymentMethodRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketAddPaymentMethodRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Add Payment Request";

    public void addPaymentRequest(CreditCardData card)
    {
        if(currentThread != null)
        {
            Log.d(TAG, "thread operation still running");

            if(canInterrupt)
            {
                // tries to interrupt the thread
                Log.d(TAG, "interrupting thread");
                currentThread.interrupt();
            }
            else
            {
                return;
            }
        }
        // start a timer that sets canInterrupt
        startTimerThread();
        currentThread = new Thread(() -> threadFunction(card));
        currentThread.start();
    }

    private void threadFunction(CreditCardData cardData)
    {
        try {
            // create socket
            socketOpen();
            // Create request
            String s = String.format("AddPayment\n%s\n%s\n%s\n%s\n%s\n", CurrentUser.getUsername(),CurrentUser.getPassword(),
                    cardData.getCardNumber(), cardData.getCardDate(), cardData.getCardCVC());
            // send
            output.write(writeSocketMessage(s));
            // read id
            String id = new String(readSocketMessage());
            if(TextUtils.equals(id, "NULL"))
            {
                // this card is already saved
                Log.d(TAG, "Card already saved");
            }
            else
            {
                String c = cardData.getCardNumber().replaceAll(" ", "");
                String lastFourDigits = c.substring(c.length() - 4);
                String cardNumber =  "**** **** **** " + lastFourDigits;
                Log.d(TAG, "CardNumber: " + cardNumber);
                CurrentUser.addPaymentMethodFromServer(new CreditCardData(id, cardNumber, cardData.getCardDate(), "***"));
            }
            // close
            socketClose();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"Error");
        }finally {
            currentThread = null;
        }
    }
}
