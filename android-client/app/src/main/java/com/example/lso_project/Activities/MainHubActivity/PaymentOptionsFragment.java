package com.example.lso_project.Activities.MainHubActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.Activities.PaymentActivity.PaymentActivity;
import com.example.lso_project.R;
import com.example.lso_project.RecViewAdapters.CreditCardListAdapter;
import com.example.lso_project.StaticInstances.CurrentUser;

public class PaymentOptionsFragment extends Fragment {

    // main activity
    private MainHubActivity activity;
    // list adapter
    private CreditCardListAdapter adapter;
    // views
    private TextView noPaymentOptionsTextView;
    private RecyclerView paymentOptionsRecView;

    public PaymentOptionsFragment() {
        // Required empty public constructor
    }

    // returns a new instance of the PaymentOptionsFragment
    public static PaymentOptionsFragment newInstance(MainHubActivity activity) {
        PaymentOptionsFragment fragment = new PaymentOptionsFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create adapter
        adapter = new CreditCardListAdapter(this::onDeleteItem);
        // subscribe to on payment methods changed
        CurrentUser.getOnPaymentMethodsChanged().addListener(this::updateUI);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_payment_options, container, false);

        // get views
        ImageButton backButton = thisView.findViewById(R.id.backButton);
        noPaymentOptionsTextView = thisView.findViewById(R.id.noPaymentOptionsText);
        paymentOptionsRecView = thisView.findViewById(R.id.paymentRecView);
        Button addPaymentOptionBtn = thisView.findViewById(R.id.addPaymentOptionBtn);

        // setup
        backButton.setOnClickListener(this::onBackPressed);
        addPaymentOptionBtn.setOnClickListener(this::addPaymentOptionBtn);

        // set rec view
        paymentOptionsRecView.setAdapter(adapter);
        paymentOptionsRecView.setLayoutManager(new LinearLayoutManager(getContext()));

        // update ui
        updateUI();
        return thisView;
    }

    // goes to the activity to add a new payment method
    private void addPaymentOptionBtn(View x)
    {
        // set payment activity type add
        PaymentActivity.setPaymentType(PaymentActivity.PaymentType.AddPayment);
        // travel to payment activity
        Intent intent = new Intent(getContext(), PaymentActivity.class);
        startActivity(intent);
    }

    // goes back to the user menu
    private void onBackPressed(View x)
    {
        activity.goUserMenu();
    }

    private void onDeleteItem(CreditCardData data)
    {
        // remove card
        CurrentUser.removePaymentMethod(data);
        // update ui
        updateUI();
    }

    // called when user payment methods change
    private void updateUI()
    {
        // set list
        adapter.SetList(CurrentUser.getPaymentMethods());
        // set data changed
        activity.runOnUiThread(() ->
        {
            // if there are some cards saved hide the text
            if(CurrentUser.getPaymentMethods().size() > 0)
            {
                noPaymentOptionsTextView.setVisibility(View.GONE);
                paymentOptionsRecView.setVisibility(View.VISIBLE);
            }
            else // show the text saying "no card saved"
            {
                noPaymentOptionsTextView.setVisibility(View.VISIBLE);
                paymentOptionsRecView.setVisibility(View.GONE);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unsubscribe from event
        CurrentUser.getOnPaymentMethodsChanged().removeListener(this::updateUI);
    }
}