package com.example.lso_project.Activities.PaymentActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lso_project.R;
import com.example.lso_project.StaticInstances.CurrentUser;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


public class PaymentActivity extends AppCompatActivity {
    // activity manager type
    public enum PaymentType
    {
        PayPayment,
        AddPayment
    }

    private enum SelectedCard
    {
        NewCard,
        SavedCard
    }

    private SelectedCard currentSelection;

    private static PaymentType s_paymentType;
    // managers classes
    private final PaymentManager payManager = new PaymentManager();
    private final AddPaymentManager addPaymentManager = new AddPaymentManager();
    // card data
    private CreditCardData creditCardData;
    // current manager
    private IHandlePaymentManager manager;

    // views
    TextView totalText;
    EditText cardNumber;
    EditText cardExpiration;
    EditText cardCVC;
    TextView errorText;
    Button payButton;
    Chip selectSavedCard;
    Button insertNewCardButton;
    // getters and setters
    public static PaymentType getPaymentType() {
        return s_paymentType;
    }

    public static void setPaymentType(PaymentType paymentType) {
        PaymentActivity.s_paymentType = paymentType;
    }

    public CreditCardData getCreditCardData() {
        return creditCardData;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        // get views
        totalText = findViewById(R.id.totalText);
        cardNumber = findViewById(R.id.cardNumberEditText);
        cardExpiration = findViewById(R.id.cardExpirationDateEditText);
        cardCVC = findViewById(R.id.cardCvcEditText);
        payButton = findViewById(R.id.payBtn);
        errorText = findViewById(R.id.errorText);
        selectSavedCard = findViewById(R.id.chip);
        insertNewCardButton = findViewById(R.id.newCardBtn);
        insertNewCardButton.setOnClickListener(this::clearSelectedSavedCard);
        // setup
        payButton.setOnClickListener(this::HandlePayment);
        errorText.setVisibility(View.INVISIBLE);
        insertNewCardButton.setVisibility(View.INVISIBLE);
        // set the correct manager
        if(s_paymentType == PaymentType.PayPayment)
        {
            manager = payManager;
        }
        else
        {
            manager = addPaymentManager;
        }
        manager.setup(this);
        // create dialog to choose card
        selectSavedCard.setOnClickListener((x) -> onCreateDialog().show());
        currentSelection = SelectedCard.NewCard;
    }
    // checks if inserted card is valid
    // if it is calls the manager method
    private void HandlePayment(View view)
    {
        if(currentSelection == SelectedCard.SavedCard)
        {
            // execute payment
            manager.onButtonPressed(this);
            return;
        }
        //remove spaces from fields
        // check if card is valid
        if(TextUtils.isEmpty(cardNumber.getText()) || TextUtils.isEmpty(cardExpiration.getText()) ||TextUtils.isEmpty(cardCVC.getText()) )
        {
            showError("Fill all the fields to continue.");
            return;
        }
        // remove spaces
        String s = String.valueOf(cardNumber.getText()).replaceAll(" ","");
        // check card number
        if(s.length() != 16)
        {
            showError("Invalid card number.");
            return;
        }
        // check date
        DateFormat formatter = new SimpleDateFormat("MM/yy");
        try {
            Date cardDate = formatter.parse(cardExpiration.getText().toString());
            LocalDate localDate = cardDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if(localDate.isBefore(LocalDate.now()))
            {
                showError("this card is expired.");
                return;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            showError("Invalid date.");
            return;
        }
        // check CVC
        if(cardCVC.length() != 3)
        {
            showError("Invalid card CVC.");
            return;
        }
        // set card info
        creditCardData = new CreditCardData(null,
                cardNumber.getText().toString(),
                cardExpiration.getText().toString(),
                cardCVC.getText().toString());
        // execute
        manager.onButtonPressed(this);
    }
    // sets the text of the error TextView and sets it visible
    public void showError(String error)
    {
        errorText.setVisibility(View.VISIBLE);
        errorText.setText(String.format("Error: %s",error));
    }
    // same but runs on ui thread
    public void showErrorPost(String error)
    {
        errorText.post(() ->
        {
            errorText.setVisibility(View.VISIBLE);
            errorText.setText(String.format("Error: %s",error));
        });
    }

    private void clearSelectedSavedCard(View ignored)
    {
        currentSelection = SelectedCard.NewCard;
        cardNumber.setText("");
        cardExpiration.setText("");
        cardCVC.setText("");
        cardNumber.setEnabled(true);
        cardExpiration.setEnabled(true);
        cardCVC.setEnabled(true);
        insertNewCardButton.setVisibility(View.INVISIBLE);
    }

    // creates a dialog that shows the user saved cards
    private AlertDialog onCreateDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // create items
        CharSequence[] items = new CharSequence[CurrentUser.getPaymentMethods().size()];
        // set items
        List<CreditCardData> paymentMethods = CurrentUser.getPaymentMethods();
        for (int i = 0, paymentMethodsSize = paymentMethods.size(); i < paymentMethodsSize; i++) {
            CreditCardData d = paymentMethods.get(i);
            items[i] = String.format("%s %s", d.getCardNumber(),d.getCardDate());
        }
        // setup on click listener
        builder.setTitle("Select card").setItems(items, (dialog, which) -> {
            // fill the fields with the selected card
            currentSelection = SelectedCard.SavedCard;
            creditCardData = CurrentUser.getPaymentMethods().get(which);
            cardNumber.setEnabled(false);
            cardExpiration.setEnabled(false);
            cardCVC.setEnabled(false);
            insertNewCardButton.setVisibility(View.VISIBLE);
            cardNumber.setText(CurrentUser.getPaymentMethods().get(which).getCardNumber());
            cardExpiration.setText(CurrentUser.getPaymentMethods().get(which).getCardDate());
            cardCVC.setText(CurrentUser.getPaymentMethods().get(which).getCardCVC());
        });
        return builder.create();
    }
}