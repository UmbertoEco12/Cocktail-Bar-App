package com.example.lso_project.SocketCommunication.Drinks;

import android.nfc.Tag;
import android.util.Log;

import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.Drink;
import com.example.lso_project.StaticInstances.CurrentDrinks;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketGetDrinkSuggestionsRequest extends BaseSocketCommunication {

    // constructor
    private SocketGetDrinkSuggestionsRequest()
    {
        super();
    }
    // Singleton
    private static SocketGetDrinkSuggestionsRequest instance = null;

    public static SocketGetDrinkSuggestionsRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketGetDrinkSuggestionsRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Get Drinks Suggestions Request";

    public void getSuggestionsRequest()
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
        String s = String.format("Suggest\n%s\n",CurrentUser.getUsername());
        // send
        output.write(writeSocketMessage(s));
        // read drinks count
        int count = readInt();
        Drink[] suggestions = new Drink[count];
        // read all data
        for(int i = 0; i<  count; i++)
        {
            // id message
            String message = new String(readSocketMessage());
            int id = Integer.parseInt(message);
            // add suggestions
            suggestions[i] = CurrentDrinks.getDrinkByID(id);
        }
        CurrentUser.setDrinkSuggestion(suggestions);
    }

}
