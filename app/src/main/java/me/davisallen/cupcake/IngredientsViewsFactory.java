package me.davisallen.cupcake;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import me.davisallen.cupcake.pojo.Ingredient;

import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_INGREDIENTS;

/**
 * Package Name:   me.davisallen.cupcake.Widget
 * Project:        Cupcake
 * Created by davis, on 8/5/17
 */

public class IngredientsViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = IngredientsViewsFactory.class.getSimpleName();

    private Context mContext;
    private ArrayList<Ingredient> mIngredients;

    public IngredientsViewsFactory(Context context) {
        mContext = context;
        getCurrentRecipeInfo();
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        getCurrentRecipeInfo();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mIngredients != null) {
            return mIngredients.size();
        } else {
            return 0;
        }
    }

    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews row = new RemoteViews(
                mContext.getPackageName(),
                R.layout.row
        );

        row.setTextViewText(R.id.widget_list_item_text_view, mIngredients.get(i).toString());

        return(row);
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void getCurrentRecipeInfo() {
        if (RecipeDetailActivity.currentlySelectedRecipe != null) {
            mIngredients = RecipeDetailActivity.currentlySelectedRecipe.getParcelableArrayList(RECIPE_INGREDIENTS);
        } else {
            mIngredients = null;
        }
    }
}
