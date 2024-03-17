package com.example.lso_project.Activities.MainHubActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.lso_project.Helpers.IEvent;
import com.example.lso_project.R;
import com.example.lso_project.StaticInstances.CurrentCart;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainHubActivity extends AppCompatActivity {

    private  static final String TAG = "MainHub";
    // views
    private HomeFragment homeFragment;
    private CartFragment cartFragment;
    private UserMenuFragment userMenuFragment;
    private MenuItem cartMenu;
    private Fragment current;
    private BottomNavigationView bottomNav;
    // event
    private IEvent onCartChanged;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // subscribe to cart listener
        onCartChanged = this::updateText;
        CurrentCart.getListener().addListener(onCartChanged);
        // setup view
        setContentView(R.layout.activity_main_hub);
        bottomNav = findViewById(R.id.bottom_navigation);
        // get cart menu
        cartMenu = bottomNav.getMenu().getItem(2);
        // updates the total price of the cart
        updateText();
        // create fragments
        homeFragment = HomeFragment.newInstance(this);
        cartFragment = new CartFragment();
        userMenuFragment = UserMenuFragment.newInstance(this);
        // setup navbar listeners
        bottomNav.setOnItemSelectedListener((x) ->
        {
            if(x.toString().equals("Home")) // click on home button
            {
                changeFragment(homeFragment);
            }
            else if(x.toString().equals("Account")) // click on account button
            {
                changeFragment(userMenuFragment);
            }
            else // click on Cart button
            {
                changeFragment(cartFragment);
            }
            return true;
        });
        // clear fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(current != null)
            transaction.remove(current);
        transaction.remove(homeFragment).remove(cartFragment).remove(userMenuFragment).commit();
        //select home fragment
        goHome();
    }
    // called when cart changes
    private void updateText()
    {
        this.runOnUiThread(() ->{
            cartMenu.setTitle(String.format("Cart(%.2f)", CurrentCart.getTotal()));
        });
    }

    public void goHome()
    {
        bottomNav.setSelectedItemId(R.id.homeMenuItem);
    }

    public void goCart()
    {
        bottomNav.setSelectedItemId(R.id.cartMenuItem);
    }

    public void goUserMenu()
    {
        bottomNav.setSelectedItemId(R.id.accountMenuItem);
    }

    // change the current fragment
    public void changeFragment(Fragment newFragment)
    {
        if(current == null)
        {
            current = newFragment;
            getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainerView,newFragment).commit();
            return;
        }
        // remove previous fragments
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().remove(current);
        // add new fragment
        transaction.add(R.id.fragmentContainerView,newFragment).commit();
        current = newFragment;
    }

    // remove the back functionality
    @Override
    public void onBackPressed()
    {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unsubscribe from cart listener
        CurrentCart.getListener().removeListener(onCartChanged);
    }
}