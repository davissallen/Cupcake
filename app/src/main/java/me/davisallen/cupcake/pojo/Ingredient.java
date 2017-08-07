package me.davisallen.cupcake.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Package Name:   me.davisallen.cake.POJO
 * Project:        Cake
 * Created by davis, on 8/2/17
 */

public class Ingredient implements Parcelable{

    /* EXAMPLE INGREDIENT
        "quantity": 2,
        "measure": "CUP",
        "ingredient": "Graham Cracker crumbs"
     */

    private String quantity;
    private String measure;
    private String ingredient;

    public Ingredient(String quantity, String measure, String ingredient) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        String measure = getMeasure();
        if (measure.equals("UNIT")) {
            // dont include measurement
            return String.format(
                    Locale.US,
                    "%s %s",
                    getQuantity(),
                    getIngredient()
            );
        }
        return String.format(
                Locale.US,
                "%s %s %s",
                getQuantity(),
                measure,
                getIngredient()
        );
    }

    protected Ingredient(Parcel in) {
        quantity = in.readString();
        measure = in.readString();
        ingredient = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    public String getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(quantity);
        parcel.writeString(measure);
        parcel.writeString(ingredient);
    }
}

