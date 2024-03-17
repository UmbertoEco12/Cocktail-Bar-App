package com.example.lso_project.RecViewAdapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lso_project.StaticInstances.Drink;
import com.example.lso_project.Helpers.IParamEvent;
import com.example.lso_project.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsListAdapter extends RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void Setup(Drink data, IParamEvent<Drink> goToDetails, Resources res)
        {
            // set name
            TextView nameText = view.findViewById(R.id.itemName);
            nameText.setText(data.getName());
            nameText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            // set price
            TextView priceText = view.findViewById(R.id.itemPrice);
            priceText.setText(String.format("%.2f$", data.getPrice()));
            // set img
            ImageView imgView = view.findViewById(R.id.productIcon);
            imgView.setImageDrawable(data.getIconDrawable(res));
            //
            ConstraintLayout layout = view.findViewById(R.id.item);
            layout.setOnClickListener((c) -> goToDetails.run(data));
        }
    }
    // list
    private final ArrayList<Drink> drinkList = new ArrayList<>();
    private IParamEvent<Drink> goToDetails;
    private Resources res;
    public ProductsListAdapter(IParamEvent<Drink> goToDetails, Resources resources)
    {
        this.goToDetails = goToDetails;
        res = resources;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drink_gui, parent, false);

        return new ViewHolder(view);
    }

    // creates items
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Drink thisDrink = drinkList.get(position);
        holder.Setup(thisDrink, goToDetails, res);
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }

    // set items
    public ProductsListAdapter SetDrinkList(List<Drink> drinks)
    {
        drinkList.clear();
        for (Drink drink:
             drinks) {
            drinkList.add(drink);
        }
        //noti
        //notifyDataSetChanged();
        return this;
    }


}
