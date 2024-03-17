package com.example.lso_project.SocketCommunication.Drinks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DebugUtils;
import android.util.Log;

import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.Drink;
import com.example.lso_project.StaticInstances.CurrentDrinks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Pattern;

public class SocketGetDrinksRequest extends BaseSocketCommunication {

    // constructor
    private SocketGetDrinksRequest()
    {
        super();
    }
    // Singleton
    private static SocketGetDrinksRequest instance = null;

    public static SocketGetDrinksRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketGetDrinksRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Get Drinks Request";

    public void getDrinkRequest()
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
        String s = "Drink\n";
        // send
        output.write(writeSocketMessage(s));
        // read drinks count
        int count = readInt();
        Log.d(TAG, Integer.valueOf(count).toString());
        Drink[] drinks = new Drink[count];
        // read all data
        for(int i = 0; i<  count; i++)
        {
            int id = Integer.parseInt(new String(readSocketMessage()));
            String name = new String(readSocketMessage());
            boolean isSmoothie = Integer.parseInt(new String(readSocketMessage())) == 1;
            float price = Float.parseFloat(new String(readSocketMessage()));
            byte[] imgData = readSocketMessage();
            Bitmap decodedIcon = BitmapFactory.decodeByteArray(imgData,0,imgData.length);
            drinks[i] = new Drink(id, decodedIcon,name, isSmoothie, price);
        }
        CurrentDrinks.setDrinks(drinks);
    }

}
