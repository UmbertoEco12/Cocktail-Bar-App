package com.example.lso_project.SocketCommunication.UserInfo;

import android.util.Log;

import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.SocketCommunication.BaseSocketCommunication;

import java.io.IOException;

public class SocketUserRegistrationRequest extends BaseSocketCommunication {

    // constructor
    private SocketUserRegistrationRequest()
    {
        super();
    }
    // Singleton
    private static SocketUserRegistrationRequest instance = null;

    public static SocketUserRegistrationRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketUserRegistrationRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Registration Request";

    public void sendRegistrationRequest( String username, String password, IHandleServerResponse response )
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
        currentThread = new Thread(() -> threadFunction(username, password, response));
        currentThread.start();
    }

    private void threadFunction( String username, String password, IHandleServerResponse response )
    {
        try {
            // create socket
            socketOpen();
            // create request
            String s = String.format("Registration\n%s\n%s\n", username, password);
            // send
            output.write(writeSocketMessage(s));
            // read response
            byte[] data = readSocketMessage();
            String message = new String(data);
            // call handler
            response.HandleResponse(message);
            // close
            socketClose();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            currentThread = null;
        }
    }
}
