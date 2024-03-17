package com.example.lso_project.SocketCommunication.UserPayment;

import android.util.Log;

import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.IOException;

public class SocketRemovePaymentMethodRequest extends BaseSocketCommunication {

    // constructor
    private SocketRemovePaymentMethodRequest()
    {
        super();
    }
    // Singleton
    private static SocketRemovePaymentMethodRequest instance = null;

    public static SocketRemovePaymentMethodRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketRemovePaymentMethodRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Remove payment Request";

    public void removePaymentMethodRequest(String cardID)
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
        currentThread = new Thread(() -> threadFunction(cardID));
        currentThread.start();
    }

    private void threadFunction(String cardID)
    {
        try {
            // create socket
            socketOpen();
            // Create request
            String s = String.format("RemovePayment\n%s\n%s\n%s\n", CurrentUser.getUsername(),CurrentUser.getPassword(),
                    cardID);
            // send
            output.write(writeSocketMessage(s));
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
