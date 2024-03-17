package com.example.lso_project.Activities.MainHubActivity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lso_project.StaticInstances.Drink;
import com.example.lso_project.Helpers.IEvent;
import com.example.lso_project.Helpers.IParamEvent;
import com.example.lso_project.R;
import com.example.lso_project.RecViewAdapters.ProductsListAdapter;
import com.example.lso_project.StaticInstances.CurrentDrinks;
import com.example.lso_project.StaticInstances.CurrentUser;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    // list adapters
    private ProductsListAdapter cocktailsAdapter;
    private ProductsListAdapter smoothiesAdapter;
    private ProductsListAdapter suggestionsAdapter;

    // main activity
    private MainHubActivity mainHubActivity;
    // fragment
    private DrinkDetailsFragment drinkDetailsFragment;

    // events
    private IEvent getSuggestions;
    private IEvent onDrinkChanged;

    // views
    private TabLayout tabLayout;

    public enum DrinkTab
    {
        Suggested,
        Cocktails,
        Smoothies
    }

    public static boolean ignoreTabSelection = false;
    public static DrinkTab s_currentTab = DrinkTab.Cocktails;

    public HomeFragment() {
        // Required empty public constructor
    }
    // returns a new instance of the home fragment
    public static HomeFragment newInstance(MainHubActivity mainHubActivity) {
        HomeFragment fragment = new HomeFragment();
        fragment.mainHubActivity = mainHubActivity;
        return fragment;
    }

    private void resetCurrentTab()
    {
        s_currentTab = DrinkTab.Cocktails;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // create details fragment
        drinkDetailsFragment = DrinkDetailsFragment.newInstance(mainHubActivity);
        // create event that goes to details fragment
        // called when the user clicks on a drink list item
        IParamEvent<Drink> goToDetails = (drink) -> {
            DrinkDetailsFragment.setSelectedDrink(drink);
            mainHubActivity.changeFragment(drinkDetailsFragment);
        };
        // create list adapters
        cocktailsAdapter = new ProductsListAdapter(goToDetails, getResources());
        smoothiesAdapter = new ProductsListAdapter(goToDetails, getResources());
        suggestionsAdapter = new ProductsListAdapter(goToDetails, getResources());
        // events
        // on drink changed event
        onDrinkChanged = this::onDrinkChanged;
        CurrentDrinks.getListener().addListener(onDrinkChanged);
        // on suggestion changed
        getSuggestions = this::updateSuggestions;
        CurrentUser.getOnSuggestionsChanged().addListener(getSuggestions);
        // on login
        CurrentUser.getOnLoginAction().addListener(this::resetCurrentTab);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // get fields
        RecyclerView recView = view.findViewById(R.id.homeRecView);
        tabLayout = view.findViewById(R.id.drinkTabs);

        // rec view setup
        recView.setAdapter(cocktailsAdapter);
        recView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // tab listener
        // change rec view adapter based on the tab pressed
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getText().equals("Cocktail"))
                {
                    recView.setAdapter(cocktailsAdapter);
                    if(ignoreTabSelection)return;
                    s_currentTab = DrinkTab.Cocktails;
                }
                else if(tab.getText().equals("Smoothies"))
                {
                    recView.setAdapter(smoothiesAdapter);
                    if(ignoreTabSelection)return;
                    s_currentTab = DrinkTab.Smoothies;
                }
                else if(tab.getText().equals("For You"))
                {
                    recView.setAdapter(suggestionsAdapter);
                    if(ignoreTabSelection)return;
                    s_currentTab = DrinkTab.Suggested;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        // update ui
        onDrinkChanged();
        // create tabs
        updateSuggestions();
        // select last tab
        selectSavedTab();
        return view;
    }

    private void selectSavedTab()
    {
        String tabText = "";
        switch (s_currentTab){

            case Suggested:
                tabText = "For You";
                break;
            case Cocktails:
                tabText = "Cocktail";
                break;
            case Smoothies:
                tabText = "Smoothies";
                break;
        }
        for (int i = 0; i< tabLayout.getTabCount(); i++)
        {
            if(tabLayout.getTabAt(i).getText().equals(tabText))
                tabLayout.getTabAt(i).select();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CurrentDrinks.getListener().removeListener(onDrinkChanged);
        CurrentUser.getOnSuggestionsChanged().removeListener(getSuggestions);
        CurrentUser.getOnLoginAction().removeListener(this::resetCurrentTab);
    }
    // called when the drinks changes
    private void onDrinkChanged()
    {
        // updates the ui
        Drink[] drinks = CurrentDrinks.getDrinks();
        // not yet received
        if(drinks == null) return;
        ArrayList<Drink> smoothies = new ArrayList<>();
        ArrayList<Drink> cocktails = new ArrayList<>();
        for (Drink drink:
                drinks) {
            // add to list
            if(drink.getIsSmoothie())
            {
                smoothies.add(drink);
            }
            else
            {
                cocktails.add(drink);
            }
        }
        // update adapter list
        cocktailsAdapter.SetDrinkList(cocktails);
        smoothiesAdapter.SetDrinkList(smoothies);
        // update ui on ui thread
        this.getActivity().runOnUiThread(() ->
        {
            cocktailsAdapter.notifyDataSetChanged();
            smoothiesAdapter.notifyDataSetChanged();
        });
    }

    // called when user suggestion changes
    private void updateSuggestions()
    {
        // update adapter list
        suggestionsAdapter.SetDrinkList(CurrentUser.getDrinkSuggestion());
        // update ui
        this.getActivity().runOnUiThread(() ->
        {
            ignoreTabSelection = true;
            tabLayout.removeAllTabs();
            // tab layout
            tabLayout.addTab(tabLayout.newTab().setText("Cocktail"));
            tabLayout.addTab(tabLayout.newTab().setText("Smoothies"));
            // add suggestions if there are any
            if(CurrentUser.getDrinkSuggestion().size() > 0)
            {
                tabLayout.addTab(tabLayout.newTab().setText(("For You")),0);
            }
            suggestionsAdapter.notifyDataSetChanged();
            selectSavedTab();
            ignoreTabSelection = false;
        });
    }
}