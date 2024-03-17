package com.example.lso_project.SocketCommunication.UserOrder;

import android.text.TextUtils;
import android.util.Log;

import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.CurrentCart;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.IOException;

public class SocketInsertOrderRequest extends BaseSocketCommunication {
    // constructor
    private SocketInsertOrderRequest()
    {
        super();
    }
    // Singleton
    private static SocketInsertOrderRequest instance = null;

    public static SocketInsertOrderRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketInsertOrderRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Insert Order Request";

    public void insertOrderRequest(IHandleServerResponse response, CreditCardData paymentInfo)
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
        currentThread = new Thread(() -> threadFunction(response, paymentInfo));
        currentThread.start();
    }

    public void stopThread()
    {
        if(currentThread != null)
        {
            currentThread.interrupt();
            Log.d(TAG, "thread operation interrupted");
        }
    }

    private void threadFunction(IHandleServerResponse response, CreditCardData paymentInfo)
    {
        try {
            if(paymentInfo == null)
                return;
            // create socket
            socketOpen();
            // Create request
            StringBuilder s = new StringBuilder(String.format("Order\n%s\n%s\n", CurrentUser.getUsername(), CurrentUser.getPassword()));
            // the client is using a new card
            if(paymentInfo.getCardID() == null)
            {
                s.append(String.format("N\n%s\n%s\n%s\n",paymentInfo.getCardNumber(), paymentInfo.getCardDate(), paymentInfo.getCardCVC()));
            }
            // client is using a saved card
            else
            {
                s.append(String.format("S\n%s\n",paymentInfo.getCardID()));
            }
            // append cart size
            s.append(String.format("%d\n", CurrentCart.getSize()));
            for (CurrentCart.CartDrink c:
                    CurrentCart.getDrinks()) {
                for (int i = 0; i < c.getCount(); i++)
                    s.append(String.format("%d\n", c.getDrink().getId()));
            }
            // send
            output.write(writeSocketMessage(s.toString()));
            // read response
            String msg = new String(readSocketMessage());
            // handle response
            response.HandleResponse(msg);
            // if order went well
            if(TextUtils.equals(msg,"OK"))
            {
                // empty cart
                CurrentCart.clear();
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
