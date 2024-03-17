package com.example.lso_project.SocketCommunication.UserPayment;

import android.util.Log;

import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.CurrentUser;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SocketGetPaymentMethodsRequest extends BaseSocketCommunication {

    // constructor
    private SocketGetPaymentMethodsRequest()
    {
        super();
    }
    // Singleton
    private static SocketGetPaymentMethodsRequest instance = null;

    public static SocketGetPaymentMethodsRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketGetPaymentMethodsRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Get Payment methods Request";

    public void getPaymentMethods()
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
        new Thread(() -> {
            try {
                // create socket
                socketOpen();
                // create operation
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
        ).start();
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
        String s = String.format("GetPayment\n%s\n%s\n",CurrentUser.getUsername(),CurrentUser.getPassword());
        // send
        output.write(writeSocketMessage(s));
        // read elem count
        int count = readInt();
        Log.d(TAG, "Count: " + Integer.valueOf(count).toString());
        // create card array
        CreditCardData[] cards = new CreditCardData[count];
        String[] cardsData = new String[3];
        // read all data
        for(int i = 0; i<  count; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                cardsData[j] = new String(readSocketMessage());
            }
            // 0: id, 1: number, 2: date,
            cards[i] = new CreditCardData(cardsData[0], cardsData[1], cardsData[2], "***");
        }
        // set cards
        CurrentUser.setPaymentMethods(cards);
    }

}
