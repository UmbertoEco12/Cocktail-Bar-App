package com.example.lso_project.RecViewAdapters;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lso_project.R;
import com.example.lso_project.StaticInstances.CurrentCart;

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        View view;

        TextView nameText;
        ImageView imgView;
        TextView countText;
        TextView total;
        ImageButton delButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        public void Setup(CurrentCart.CartDrink data, Resources res)
        {
            // set name
            nameText = view.findViewById(R.id.itemName);
            nameText.setText(data.getDrink().getName());
            // set img
            imgView = view.findViewById(R.id.productIcon);
            imgView.setImageDrawable(data.getDrink().getIconDrawable(res));
            //set count
            countText = view.findViewById(R.id.itemCountText);
            total = view.findViewById(R.id.itemPrice);
            updateUI(data);

            delButton = view.findViewById(R.id.deleteBtn);
            delButton.setOnClickListener((x) ->
            {
                CurrentCart.removeFromCart(data.getDrink());
                updateUI(data);
            });
        }

        private void updateUI(CurrentCart.CartDrink data)
        {
            // set count
            countText.setText(String.format("x%d",data.getCount()));
            // set total
            total.setText(String.format("%.2f$",data.getTotal()));
        }
    }

    private Resources res;
    public CartListAdapter(Resources resources)
    {
        res = resources;
    }

    @NonNull
    @Override
    public CartListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_drink_gui, parent, false);

        return new CartListAdapter.ViewHolder(view);
    }

    // creates items
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.Setup(CurrentCart.getDrinks().get(position), res);
    }

    @Override
    public int getItemCount() {
        return CurrentCart.getDrinks().size();
    }

}
