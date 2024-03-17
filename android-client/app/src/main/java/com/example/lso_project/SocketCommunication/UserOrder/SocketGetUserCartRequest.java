package com.example.lso_project.SocketCommunication.UserOrder;

import android.text.TextUtils;
import android.util.Log;

import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.CurrentCart;
import com.example.lso_project.StaticInstances.CurrentDrinks;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketGetUserCartRequest extends BaseSocketCommunication {

    // constructor
    private SocketGetUserCartRequest()
    {
        super();
    }
    // Singleton
    private static SocketGetUserCartRequest instance = null;

    public static SocketGetUserCartRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketGetUserCartRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Get Cart Request";

    public void getCartRequest()
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
            // operation
            socketOp();
            // close
            socketClose();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"Error");
        }finally {
            currentThread = null;
        }
    }

    public void socketOperation(Socket socket, DataOutputStream outputStream, DataInputStream inputStream) throws IOException
    {
        // set socket
        this.socket = socket;
        this.output = outputStream;
        this.input = inputStream;
        // do operation
        socketOp();
    }

    private void socketOp() throws IOException
    {
        // Create request
        String s = String.format("GetCart\n%s\n%s\n",CurrentUser.getUsername(), CurrentUser.getPassword());
        // send
        output.write(writeSocketMessage(s));
        // read result
        String check = new String(readSocketMessage());
        if(TextUtils.equals(check,"NO"))
        {
            return;
        }
        // read drinks count
        int count = readInt();
        // empty cart
        CurrentCart.clearNoListener();
        // read all data
        for(int i = 0; i<  count; i++)
        {
            // get id
            String message = new String(readSocketMessage());
            int id = Integer.parseInt(message);
            // add drink to cart
            CurrentCart.addToCartNoListener(CurrentDrinks.getDrinkByID(id));
        }
        // call listeners when all cart is being added
        CurrentCart.callListeners();
    }

}
