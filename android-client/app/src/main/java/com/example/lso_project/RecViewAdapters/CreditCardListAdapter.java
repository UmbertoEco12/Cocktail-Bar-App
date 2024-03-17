package com.example.lso_project.RecViewAdapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lso_project.Helpers.IParamEvent;
import com.example.lso_project.Activities.PaymentActivity.CreditCardData;
import com.example.lso_project.R;

import java.util.ArrayList;
import java.util.List;

public class CreditCardListAdapter extends RecyclerView.Adapter<CreditCardListAdapter.ViewHolder>  {

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void Setup(CreditCardData data, IParamEvent<CreditCardData> onDelete)
        {
            Button cardBtn = view.findViewById(R.id.creditCardButton);
            // should obscure the card number first
            cardBtn.setText(String.format("%s %s",data.getCardNumber(),data.getCardDate()));
            Button deleteBtn = view.findViewById(R.id.deleteCardBtn);
            // hide del btn
            deleteBtn.setVisibility(View.GONE);
            // add listener
            cardBtn.setOnClickListener((x) ->
            {
                if(deleteBtn.getVisibility() == View.VISIBLE)
                    deleteBtn.setVisibility(View.GONE);
                else
                    deleteBtn.setVisibility(View.VISIBLE);
            });

            deleteBtn.setOnClickListener((x) -> onDelete.run(data));
        }
    }

    IParamEvent<CreditCardData> onDeleteItem;

    // list
    private final ArrayList<CreditCardData> cardsData = new ArrayList<>();

    public CreditCardListAdapter(IParamEvent<CreditCardData> onDelete)
    {
        onDeleteItem = onDelete;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.credit_card_item_gui, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.Setup(cardsData.get(position), onDeleteItem);
    }

    @Override
    public int getItemCount() {
        return cardsData.size();
    }

    // set items
    public CreditCardListAdapter SetList(List<CreditCardData> list)
    {
        cardsData.clear();
        cardsData.addAll(list);
        return this;
    }

}
