package me.davisallen.cupcake.pojo;

import java.net.URL;
import java.util.ArrayList;

import me.davisallen.cupcake.utils.UrlUtils;

/**
 * Package name: me.davisallen.cake
 * Project: Cake
 * Created by davis, on 8/1/17
 */

public class Recipe {

    private int id;
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Step> steps;
    private int servings;
    private String image;

    public Recipe(int id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps, int servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public URL getImage() {
        return UrlUtils.convertStringToURL(image);
    }

}
