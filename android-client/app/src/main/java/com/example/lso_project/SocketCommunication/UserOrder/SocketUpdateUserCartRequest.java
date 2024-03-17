package com.example.lso_project.SocketCommunication.UserOrder;

import android.util.Log;

import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.CurrentCart;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.IOException;

public class SocketUpdateUserCartRequest extends BaseSocketCommunication {

    // constructor
    private SocketUpdateUserCartRequest()
    {
        super();
    }
    // Singleton
    private static SocketUpdateUserCartRequest instance = null;

    public static SocketUpdateUserCartRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketUpdateUserCartRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Update Cart Request";

    public void updateCartRequest()
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
        currentThread = new Thread(this::threadFunction);
        currentThread.start();
    }

    private void threadFunction()
    {
        try {
            // create socket
            socketOpen();
            // Create request
            StringBuilder s = new StringBuilder(String.format("UpdateCart\n%s\n%s\n%d\n", CurrentUser.getUsername(), CurrentUser.getPassword(), CurrentCart.getSize()));

            for (CurrentCart.CartDrink c:
                    CurrentCart.getDrinks()) {
                for (int i = 0; i < c.getCount(); i++)
                    s.append(String.format("%d\n", c.getDrink().getId()));
            }
            // send
            output.write(writeSocketMessage(s.toString()));
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
