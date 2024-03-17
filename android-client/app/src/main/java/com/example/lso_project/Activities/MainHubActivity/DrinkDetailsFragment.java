package com.example.lso_project.Activities.MainHubActivity;

import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lso_project.StaticInstances.Drink;
import com.example.lso_project.Helpers.IEvent;
import com.example.lso_project.R;
import com.example.lso_project.StaticInstances.CurrentCart;
import com.example.lso_project.StaticInstances.CurrentDrinks;

import java.util.ArrayList;

public class DrinkDetailsFragment extends Fragment {

    // selects the drink from a different class before traveling to this fragment
    private static Drink s_drinkSelected;
    // views
    private TextView drinkDescription;
    private TextView cartNumber;
    private IEvent onCartChangedEvent;
    private LinearLayout tagLayout;
    private TextView ingredientsText;
    // activity
    private MainHubActivity activity;

    public static void setSelectedDrink(Drink drink)
    {
        s_drinkSelected = drink;
    }

    public DrinkDetailsFragment() {
        // Required empty public constructor
    }
    // returns a new instance of DrinkDetailsFragment
    public static DrinkDetailsFragment newInstance(MainHubActivity activity) {
        DrinkDetailsFragment fragment = new DrinkDetailsFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCartChangedEvent = this::updateCartNumber;
        // subscribe to events
        CurrentCart.getListener().addListener(onCartChangedEvent);
        CurrentDrinks.getListener().addListener(this::updateDrinks);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View thisView = inflater.inflate(R.layout.fragment_drink_details, container, false);
        // update selected drink
        CurrentDrinks.updateDrinkAt(s_drinkSelected.getId());
        // get views
        ImageButton backButton = thisView.findViewById(R.id.backButton);
        ImageView drinkIcon = thisView.findViewById(R.id.drinkIcon);
        TextView name = thisView.findViewById(R.id.drinkNameTextView);
        drinkDescription = thisView.findViewById(R.id.drinkDescription);
        TextView priceTag = thisView.findViewById(R.id.priceTag);
        cartNumber = thisView.findViewById(R.id.cartCountTextView);
        Button addToCart = thisView.findViewById(R.id.addToCartButton);
        tagLayout = thisView.findViewById(R.id.tagsLayout);
        ingredientsText = thisView.findViewById(R.id.ingredientsText);
        // setup
        backButton.setOnClickListener(this::onBackPressed);
        // set icon
        drinkIcon.setImageDrawable(s_drinkSelected.getIconDrawable(getResources()));
        // set text
        name.setText(s_drinkSelected.getName());
        // reset description
        drinkDescription.setText("");

        priceTag.setText(String.format("%.2f$",s_drinkSelected.getPrice()));
        // should update each time the cart gets updated
        updateCartNumber();
        // update ui
        updateDrinks();
        // add to cart button
        addToCart.setOnClickListener((x)-> CurrentCart.addToCart(s_drinkSelected));
        return thisView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unsubscribe to events
        CurrentCart.getListener().removeListener(onCartChangedEvent);
        CurrentDrinks.getListener().removeListener(this::updateDrinks);
    }
    // updates the text for how many of this drink are already in the cart
    private void updateCartNumber()
    {
        if(cartNumber == null) return;
        cartNumber.post(() ->
        {
            int currentCount = CurrentCart.getDrinkCount(s_drinkSelected);
            if(currentCount > 0)
                cartNumber.setText(String.format("Already %dx in cart",currentCount));
            else
                cartNumber.setText("");
        });
    }
    // called when drinks change
    private void updateDrinks()
    {
        drinkDescription.post(() ->
        {
            tagLayout.removeAllViews();
            for (String tag :
                    s_drinkSelected.getTags()) {
                TextView v = new TextView(getContext());
                v.setText(tag);
                v.setBackground(AppCompatResources.getDrawable(getContext(),R.drawable.rounded_corner));
                LinearLayout.LayoutParams params = new
                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(8,8,8,8);
                v.setLayoutParams(params);
                tagLayout.addView(v);
            }
            StringBuilder s = new StringBuilder();
            ArrayList<String> ingredients = s_drinkSelected.getIngredients();
            for (int i = 0, ingredientsSize = ingredients.size(); i < ingredientsSize; i++) {
                String ingredient = ingredients.get(i);
                if(i == ingredientsSize - 1)
                {
                    // last
                    s.append(String.format("-%s.\n", ingredient));
                }
                else
                {
                    s.append(String.format("-%s,\n", ingredient));
                }
            }
            ingredientsText.setText(s.toString());
            drinkDescription.setText(s_drinkSelected.getDescription());
        });
    }

    // called when the back button is pressed
    private void onBackPressed(View x)
    {
        activity.goHome();
    }

}