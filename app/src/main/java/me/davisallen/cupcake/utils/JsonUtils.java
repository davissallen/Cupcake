package me.davisallen.cupcake.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import me.davisallen.cupcake.pojo.Ingredient;
import me.davisallen.cupcake.pojo.Recipe;
import me.davisallen.cupcake.pojo.Step;

/**
 * Package Name:   me.davisallen.cake.Utils
 * Project:        Cake
 * Created by davis, on 8/2/17
 */

public class JsonUtils {

    private static final String LOG_TAG = JsonUtils.class.getSimpleName();

    public static final String RECIPE_ID = "id";
    public static final String RECIPE_NAME = "name";
    public static final String RECIPE_INGREDIENTS = "ingredients";
    public static final String RECIPE_STEPS = "steps";
    public static final String RECIPE_SERVINGS = "servings";
    public static final String RECIPE_IMAGE = "image";

    public static final String INGREDIENT_QUANTITY = "quantity";
    public static final String INGREDIENT_MEASURE = "measure";
    public static final String INGREDIENT_INGREDIENT = "ingredient";

    public static final String STEP_ID = "id";
    public static final String STEP_SHORT_DESCRIPTION = "shortDescription";
    public static final String STEP_DESCRIPTION = "description";
    public static final String STEP_VIDEO_URL = "videoURL";
    public static final String STEP_THUMBNAIL_URL = "thumbnailURL";

    /*
     *  Takes in a JSON response from website and converts it to a list of recipes
     */
    public static ArrayList<Recipe> getRecipesFromHttpResponse(String httpResponse) {
        ArrayList<Recipe> recipes = new ArrayList<>();

        try {

            // for each recipe in JSONArray
            JSONArray recipeArray = new JSONArray(httpResponse);

            for (int i = 0; i < recipeArray.length(); i++) {
                JSONObject recipeObject = recipeArray.getJSONObject(i);

                int recipeId = recipeObject.getInt(RECIPE_ID);
                String recipeName = recipeObject.getString(RECIPE_NAME);
                ArrayList<Ingredient> recipeIngredients = new ArrayList<>();
                ArrayList<Step> recipeSteps = new ArrayList<>();

                // for each ingredient in ingredients
                JSONArray ingredientsArray = recipeObject.getJSONArray(RECIPE_INGREDIENTS);
                for (int j = 0; j < ingredientsArray.length(); j++) {
                    JSONObject ingredientObject = ingredientsArray.getJSONObject(j);

                    String ingredientQuantity = ingredientObject.getString(INGREDIENT_QUANTITY);
                    String ingredientMeasure = ingredientObject.getString(INGREDIENT_MEASURE);
                    String ingredientIngredient = ingredientObject.getString(INGREDIENT_INGREDIENT);

                    Ingredient ingredient = new Ingredient(
                            ingredientQuantity,
                            ingredientMeasure,
                            ingredientIngredient
                    );

                    recipeIngredients.add(ingredient);
                }

                // for each step in steps
                JSONArray stepsArray = recipeObject.getJSONArray(RECIPE_STEPS);
                for (int j = 0; j < stepsArray.length(); j++) {
                    JSONObject stepObject = stepsArray.getJSONObject(j);

                    int stepId = stepObject.getInt(STEP_ID);
                    String stepShortDescription = stepObject.getString(STEP_SHORT_DESCRIPTION);
                    String stepDescription = stepObject.getString(STEP_DESCRIPTION);
                    String stepVideoURL = stepObject.getString(STEP_VIDEO_URL);
                    String stepThumbnailURL = stepObject.getString(STEP_THUMBNAIL_URL);

                    Step step = new Step(
                            stepId,
                            stepShortDescription,
                            stepDescription,
                            stepVideoURL,
                            stepThumbnailURL
                    );

                    recipeSteps.add(step);
                }

                int recipeServings = recipeObject.getInt(RECIPE_SERVINGS);
                String recipeImage = recipeObject.getString(RECIPE_IMAGE);

                recipes.add(new Recipe(
                        recipeId,
                        recipeName,
                        recipeIngredients,
                        recipeSteps,
                        recipeServings,
                        recipeImage
                ));
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, "Could not parse JSON response from URL");
            e.printStackTrace();
        }

        return recipes;
    }
}
