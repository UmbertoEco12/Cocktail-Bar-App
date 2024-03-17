package com.example.lso_project.StaticInstances;

import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.Helpers.Action;
import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.SocketCommunication.UserOrder.SocketGetUserCartRequest;
import com.example.lso_project.SocketCommunication.UserOrder.SocketInsertOrderRequest;
import com.example.lso_project.SocketCommunication.UserOrder.SocketUpdateUserCartRequest;

import java.util.ArrayList;

public class CurrentCart {

    public static class CartDrink
    {
        private Drink drink;
        private int count;


        public CartDrink(Drink drink) {
            this.drink = drink;
            this.count = 1;
        }

        public Drink getDrink() {
            return drink;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void addItem()
        {
            this.count++;
        }
        // returns true if item should be removed
        public boolean removeItem()
        {
            this.count --;
            if(count <= 0)
                return true;
            return false;
        }

        public float getTotal()
        {
            return drink.getPrice() * count;
        }
    }

    private static final ArrayList<CartDrink> s_drinks = new ArrayList<>();

    private static final Action s_onCartChanged = new Action();

    private static final Action s_onItemsChanged = new Action();

    public static void addToCart(Drink drink)
    {
        for (int i = 0, s_drinksSize = s_drinks.size(); i < s_drinksSize; i++) {
            CartDrink cartDrink = s_drinks.get(i);
            if (cartDrink.getDrink().getId() == drink.getId()) {
                cartDrink.addItem();
                s_onCartChanged.run();
                sendCart();
                return;
            }
        }
        CartDrink newDrink = new CartDrink(drink);
        s_drinks.add(newDrink);
        s_onCartChanged.run();
        sendCart();
        s_onItemsChanged.run();
    }
    // called from socket
    public static void addToCartNoListener(Drink drink)
    {
        for (int i = 0, s_drinksSize = s_drinks.size(); i < s_drinksSize; i++) {
            CartDrink cartDrink = s_drinks.get(i);
            if (cartDrink.getDrink().getId() == drink.getId()) {
                cartDrink.addItem();
                return;
            }
        }
        CartDrink newDrink = new CartDrink(drink);
        s_drinks.add(newDrink);
    }

    public static void clearNoListener()
    {
        s_drinks.clear();
    }

    public static void callListeners()
    {
        s_onCartChanged.run();
        s_onItemsChanged.run();
    }

    public static void removeFromCart(Drink drink)
    {
        for (int i = 0, s_drinksSize = s_drinks.size(); i < s_drinksSize; i++) {
            CartDrink cartDrink = s_drinks.get(i);
            if (cartDrink.getDrink().getId() == drink.getId()) {
                if(cartDrink.removeItem())
                {
                    s_drinks.remove(cartDrink);
                    s_onItemsChanged.run();
                }
                s_onCartChanged.run();
                sendCart();
                return;
            }
        }
    }

    public static Action getListener()
    {
        return s_onCartChanged;
    }

    public static Action getOnItemRemovedListener()
    {
        return s_onItemsChanged;
    }

    public static int getDrinkCount(Drink drink)
    {
        for (int i = 0, s_drinksSize = s_drinks.size(); i < s_drinksSize; i++) {
            CartDrink cartDrink = s_drinks.get(i);
            if (cartDrink.getDrink().getId() == drink.getId()) {

                return cartDrink.getCount();
            }
        }
        return 0;
    }

    public static ArrayList<CartDrink> getDrinks()
    {
        return s_drinks;
    }

    public static float getTotal()
    {
        float total = 0;
        for (int i = 0, s_drinksSize = s_drinks.size(); i < s_drinksSize; i++) {
            CartDrink cartDrink = s_drinks.get(i);
            total += cartDrink.getTotal();
        }
        return total;
    }

    public static void clear()
    {
        s_drinks.clear();
        s_onCartChanged.run();
        sendCart();
        s_onItemsChanged.run();
    }

    public static int getSize()
    {
        int total = 0;
        for (int i = 0, s_drinksSize = s_drinks.size(); i < s_drinksSize; i++) {
            CartDrink cartDrink = s_drinks.get(i);
            total += cartDrink.getCount();
        }
        return total;
    }

    public static void updateCart()
    {
        SocketGetUserCartRequest.getInstance().getCartRequest();
    }

    public static void sendCart()
    {
        SocketUpdateUserCartRequest.getInstance().updateCartRequest();
    }

    public static void sendOrder(IHandleServerResponse response, CreditCardData paymentInfo)
    {
        // clears the cart and send the items as an order
        SocketInsertOrderRequest.getInstance().insertOrderRequest(response, paymentInfo);
    }

    public static void stopOrder()
    {
        // stops the thread, timed out
        SocketInsertOrderRequest.getInstance().stopThread();
    }


}
