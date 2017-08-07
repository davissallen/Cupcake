package me.davisallen.cupcake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViewsService;

import static me.davisallen.cupcake.ViewRecipesActivity.EXTRA_RECIPE_DETAIL;

/**
 * Package Name:   me.davisallen.cupcake.Widget
 * Project:        Cupcake
 * Created by davis, on 8/5/17
 */

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle recipeInfo = intent.getBundleExtra(EXTRA_RECIPE_DETAIL);
        return new IngredientsViewsFactory(this.getApplicationContext(), recipeInfo);
    }
}
