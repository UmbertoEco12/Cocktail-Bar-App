package com.example.lso_project.StaticInstances;

import android.text.TextUtils;
import android.util.Log;

import com.example.lso_project.Helpers.Action;
import com.example.lso_project.Helpers.Hashing;
import com.example.lso_project.Helpers.IHandleServerResponse;
import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.SocketCommunication.UserPayment.SocketAddPaymentMethodRequest;
import com.example.lso_project.SocketCommunication.Drinks.SocketGetDrinkSuggestionsRequest;
import com.example.lso_project.SocketCommunication.UserPayment.SocketGetPaymentMethodsRequest;
import com.example.lso_project.SocketCommunication.UserPayment.SocketRemovePaymentMethodRequest;
import com.example.lso_project.SocketCommunication.UserInfo.SocketUserLoginRequest;
import com.example.lso_project.SocketCommunication.UserInfo.SocketUserRegistrationRequest;
import com.example.lso_project.SocketCommunication.UserInfo.SocketUserUpdateRequest;
import com.example.lso_project.Helpers.UserDatabaseHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CurrentUser {
    private static String username;
    private static String password;

    private static ArrayList<CreditCardData> paymentMethods = new ArrayList<>();

    private static Action onLoginAction = new Action();

    private static ArrayList<Drink> suggestions = new ArrayList<>();

    private static Action onPaymentMethodsChanged = new Action();

    private static Action onSuggestionsChanged = new Action();

    public static String getUsername() {
        return username;
    }

    private static void setUsername(String username) {
        CurrentUser.username = username;
    }

    public static String getPassword() {
        return password;
    }

    private static void setPassword(String password) {
        CurrentUser.password = password;
    }

    public static List<CreditCardData> getPaymentMethods() {
        return paymentMethods;
    }

    public static void setPaymentMethods(CreditCardData[] paymentMethods) {
        CurrentUser.paymentMethods.clear();
        CurrentUser.paymentMethods.addAll(Arrays.asList(paymentMethods));
        onPaymentMethodsChanged.run();
    }

    public static List<Drink> getDrinkSuggestion() {
        return suggestions;
    }

    public static void setDrinkSuggestion(Drink[] suggestions) {
        CurrentUser.suggestions.clear();
        CurrentUser.suggestions.addAll(Arrays.asList(suggestions));
        onSuggestionsChanged.run();
    }

    public static void addPaymentMethodToServer(CreditCardData data)
    {
        SocketAddPaymentMethodRequest.getInstance().addPaymentRequest(data);
    }

    public static void addPaymentMethodFromServer(CreditCardData data)
    {
        paymentMethods.add(data);
        onPaymentMethodsChanged.run();
    }

    public static void removePaymentMethod(CreditCardData data)
    {
        for (int i = 0, paymentMethodsLength = paymentMethods.size(); i < paymentMethodsLength; i++) {
            CreditCardData d = paymentMethods.get(i);
            if (TextUtils.equals(d.getCardNumber(), data.getCardNumber())) {
                // remove locally
                paymentMethods.remove(i);
                // remove on server
                SocketRemovePaymentMethodRequest.getInstance().removePaymentMethodRequest(data.getCardID());
                return;
            }
        }
    }

    public static void Login(String username, String password)
    {
        // set values
        setUsername(username);setPassword(password);
        // save on local db
        UserDatabaseHelper.getInstance(null).setUser(username,password);
        // call on login events
        onLoginAction.run();
    }

    public static void Logout()
    {
        Log.d("User","Logout");
        // reset values
        setUsername(null); setPassword(null);
        // remove from local db
        UserDatabaseHelper.getInstance(null).deleteUser();
    }

    public static Action getOnLoginAction() {
        return onLoginAction;
    }

    public static void updateSuggestions()
    {
        SocketGetDrinkSuggestionsRequest.getInstance().getSuggestionsRequest();
    }

    public static void updatePaymentOptions()
    {
        SocketGetPaymentMethodsRequest.getInstance().getPaymentMethods();
    }

    public static Action getOnSuggestionsChanged() {
        return onSuggestionsChanged;
    }

    public static Action getOnPaymentMethodsChanged() {
        return onPaymentMethodsChanged;
    }

    public static void checkLogin(String username, String password, IHandleServerResponse response)
    {
        // hash password
        String passwordHash = Hashing.getMd5Hash(password);
        checkLoginHashed(username,passwordHash, response);
    }

    public static void checkLoginHashed(String username, String password, IHandleServerResponse response)
    {
        SocketUserLoginRequest.getInstance().sendLoginRequest(username,password,response);
    }

    public static void checkPasswordUpdate(String newPassword, IHandleServerResponse response)
    {
        // hash password
        String passwordHash = Hashing.getMd5Hash(newPassword);
        SocketUserUpdateRequest.getInstance().sendUpdateRequest(getUsername(),passwordHash,response);
    }

    public static void checkUsernameUpdate(String newUsername, IHandleServerResponse response)
    {
        SocketUserUpdateRequest.getInstance().sendUpdateRequest(newUsername,getPassword(),response);
    }

    public static void checkRegistration(String username, String password, IHandleServerResponse response)
    {
        // hash password
        String passwordHash = Hashing.getMd5Hash(password);
        SocketUserRegistrationRequest.getInstance().sendRegistrationRequest(username,passwordHash,response);
    }
}
