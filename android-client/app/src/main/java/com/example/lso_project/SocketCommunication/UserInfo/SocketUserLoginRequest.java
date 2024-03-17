package com.example.lso_project.SocketCommunication.UserInfo;

import android.util.Log;

import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.SocketCommunication.Drinks.SocketGetDrinkSuggestionsRequest;
import com.example.lso_project.SocketCommunication.Drinks.SocketGetDrinksRequest;
import com.example.lso_project.SocketCommunication.UserPayment.SocketGetPaymentMethodsRequest;
import com.example.lso_project.SocketCommunication.UserOrder.SocketGetUserCartRequest;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.IOException;

public class SocketUserLoginRequest extends BaseSocketCommunication {

    // constructor
    private SocketUserLoginRequest()
    {
        super();
    }
    // Singleton
    private static SocketUserLoginRequest instance = null;

    public static SocketUserLoginRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketUserLoginRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Login Request";

    public void sendLoginRequest(String username, String password, IHandleServerResponse response)
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
        // start a new thread
        currentThread = new Thread(() -> threadFunction(username, password, response));
        currentThread.start();
    }

    private void threadFunction( String username, String password, IHandleServerResponse response )
    {
        try {

            // create socket
            socketOpen();
            // create request
            String s = String.format("Login\n%s\n%s\n", username, password);
            // send
            output.write(writeSocketMessage(s));
            // read response
            byte[] data = readSocketMessage();
            String message = new String(data);
            // if is ok
            if( message.equals("OK") )
            {
                Log.d(TAG, "Logged in");
                // login
                CurrentUser.Login(username, password);
                // get drinks
                SocketGetDrinksRequest.getInstance().socketOperation(socket, output, input);
                // get user cart
                SocketGetUserCartRequest.getInstance().socketOperation(socket,output, input);
                // get user suggestions
                SocketGetDrinkSuggestionsRequest.getInstance().socketOperation(socket, output, input);
                // get user payment methods
                SocketGetPaymentMethodsRequest.getInstance().socketOperation(socket,output,input);
                // close
                socketClose();
            }
            // call response
            response.HandleResponse(message);

        } catch (IOException e) {
            Log.d(TAG, "Exception");
            e.printStackTrace();
        }finally {
            currentThread = null;
        }
    }

}
