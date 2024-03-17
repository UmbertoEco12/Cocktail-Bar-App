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
import android.widget.TextView;

import com.example.lso_project.Helpers.IEvent;
import com.example.lso_project.Activities.PaymentActivity.PaymentActivity;
import com.example.lso_project.R;
import com.example.lso_project.RecViewAdapters.CartListAdapter;
import com.example.lso_project.StaticInstances.CurrentCart;


public class CartFragment extends Fragment {

    // views
    private TextView noOrderText;
    private TextView totalText;
    // adapter
    private CartListAdapter cartListAdapter;
    // events
    private IEvent onItemsChangedEvent;
    private IEvent onCartChangedEvent;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create adapter
        cartListAdapter = new CartListAdapter(getResources());
        // setup events
        onCartChangedEvent = this::onCartChanged;
        onItemsChangedEvent = this::onItemsChanged;
        // set listeners
        CurrentCart.getListener().addListener(onCartChangedEvent);
        CurrentCart.getOnItemRemovedListener().addListener(onItemsChangedEvent);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_cart, container, false);

        // get views
        noOrderText = thisView.findViewById(R.id.noOrderText);
        RecyclerView itemsList = thisView.findViewById(R.id.ordersRecView);
        totalText = thisView.findViewById(R.id.priceTextView);
        Button payButton = thisView.findViewById(R.id.payButton);

        // setup recView
        itemsList.setAdapter(cartListAdapter);
        itemsList.setLayoutManager(new LinearLayoutManager(getContext()));
        // update ui
        onCartChanged();
        // setup click listener
        payButton.setOnClickListener((x) ->
        {
            // if there are items in the cart
            if(CurrentCart.getDrinks().size() > 0)
            {
                // go to payment activity
                PaymentActivity.setPaymentType(PaymentActivity.PaymentType.PayPayment);
                Intent myIntent = new Intent(getContext(), PaymentActivity.class);
                startActivity(myIntent);
            }
        });

        return thisView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // remove listeners
        CurrentCart.getListener().removeListener(onCartChangedEvent);
        CurrentCart.getOnItemRemovedListener().removeListener(onItemsChangedEvent);
    }

    // called when the cart changes
    private void onCartChanged()
    {
        totalText.post(() ->
        {
            if(CurrentCart.getDrinks().size() == 0)
            {
                totalText.setText("");
                noOrderText.setVisibility(View.VISIBLE);
                return;
            }
            noOrderText.setVisibility(View.INVISIBLE);
            // update total text
            if(CurrentCart.getTotal() <= 0)
            {
                totalText.setText("");
            }
            else
                totalText.setText(String.format("%.2f$",CurrentCart.getTotal()));
        });
    }

    // called when an item is removed or added from the cart
    private void onItemsChanged()
    {
        // cart adapter uses the cart list directly
        this.getActivity().runOnUiThread(() -> cartListAdapter.notifyDataSetChanged());
    }
}