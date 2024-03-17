package com.example.lso_project.SocketCommunication.Drinks;

import android.util.Log;

import com.example.lso_project.SocketCommunication.BaseSocketCommunication;
import com.example.lso_project.StaticInstances.Drink;
import com.example.lso_project.StaticInstances.CurrentDrinks;

import java.io.IOException;

public class SocketGetDrinkDetailsRequest extends BaseSocketCommunication {

    // constructor
    private SocketGetDrinkDetailsRequest()
    {
        super();
    }
    // Singleton
    private static SocketGetDrinkDetailsRequest instance = null;

    public static SocketGetDrinkDetailsRequest getInstance()
    {
        if(instance == null)
        {
            instance = new SocketGetDrinkDetailsRequest();
        }
        return instance;
    }
    // used to debug
    private static final String TAG = "Get Drink Details Request";

    public void getDrinkDetailsRequest(int drinkId)
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
        currentThread = new Thread(() -> threadFunction(drinkId));
        currentThread.start();
    }

    private void threadFunction(int id)
    {
        try {
            // create socket
            socketOpen();
            // Create request
            String s = String.format("DrinkDetails\n%d\n",id);
            // send
            output.write(writeSocketMessage(s));
            // read drinks count
            int count = readInt();
            Drink currentDrink = CurrentDrinks.getDrinkByID(id);
            // read all data
            for(int i = 0; i<  count; i++)
            {
                if(i == 0)
                {
                    // get description
                    byte[] desc = readSocketMessage();
                    String description = new String(desc);
                    // set description
                    currentDrink.setDescription(description);
                }
                // read tag
                byte[] tagData = readSocketMessage();
                // add tag
                currentDrink.addTag(new String(tagData));

                // read ingredient
                byte[] ingredientData = readSocketMessage();
                // add ingredient
                currentDrink.addIngredient(new String(ingredientData));
            }
            // calls action on current drinks
            CurrentDrinks.notifyDrinksChanged();
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
