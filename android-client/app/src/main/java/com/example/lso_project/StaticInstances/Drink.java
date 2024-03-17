package com.example.lso_project.StaticInstances;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import java.util.ArrayList;

public class Drink {
    private int Id;
    private Bitmap DecodedDrawable;
    private Drawable Drawable;
    private String Name;
    private String Description;
    private float Price;
    private boolean IsSmoothie;

    private final ArrayList<String> tags = new ArrayList<>();
    private final ArrayList<String> ingredients = new ArrayList<>();

    public Drink(int id, Bitmap drawable,String name, boolean isSmoothie, float price)
    {
        Id = id;
        DecodedDrawable = drawable;
        Name = name;
        IsSmoothie = isSmoothie;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public android.graphics.drawable.Drawable getIconDrawable(Resources res) {
        if(Drawable == null)
            Drawable = new BitmapDrawable(res, DecodedDrawable);
        return Drawable;
    }

    public void setDrawable(Bitmap drawable) {
        // changed drawable
        DecodedDrawable = drawable;
        Drawable = null;
    }

    public boolean getIsSmoothie()
    {
        return IsSmoothie;
    }

    public void setIsSmoothie(boolean value)
    {
        IsSmoothie = value;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void addTag(String tag)
    {
        for (String s:
             tags) {
            if(TextUtils.equals(s, tag))
                return;
        }
        tags.add(tag);
    }

    public void addIngredient(String ingredient)
    {
        for (String s:
                ingredients) {
            if(TextUtils.equals(s, ingredient))
                return;
        }
        ingredients.add(ingredient);
    }
}
