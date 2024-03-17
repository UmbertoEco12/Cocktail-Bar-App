package com.example.lso_project.StaticInstances;

import com.example.lso_project.Helpers.Action;
import com.example.lso_project.SocketCommunication.Drinks.SocketGetDrinkDetailsRequest;
import com.example.lso_project.SocketCommunication.Drinks.SocketGetDrinksRequest;

public class CurrentDrinks
{
    private static Drink[] s_drinks;
    private static final Action onDrinksChanged = new Action();

    public static void setDrinks(Drink[] drinks)
    {
        s_drinks = drinks;
        // call listener
        onDrinksChanged.run();
    }

    public static Drink[] getDrinks() {
        return s_drinks;
    }

    public static Action getListener() {return onDrinksChanged;}

    public static Drink getDrinkByID(int id)
    {
        for (Drink d:
             s_drinks) {
            if(d.getId() == id)
                return d;
        }
        return null;
    }

    public static void updateDrinkAt(int id)
    {
        // updates drink only if not already updated
        if(getDrinkByID(id).getDescription() != null)
            return;
        SocketGetDrinkDetailsRequest.getInstance().getDrinkDetailsRequest(id);
    }

    public static void notifyDrinksChanged()
    {
        onDrinksChanged.run();
    }

    public static void updateDrinks()
    {
        SocketGetDrinksRequest.getInstance().getDrinkRequest();
    }
}
