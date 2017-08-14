package me.davisallen.cupcake;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_NAME;

/**
 * Package Name:   me.davisallen.cupcake
 * Project:        Cupcake
 * Created by davis, on 8/5/17
 */

public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {

            RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            if (RecipeDetailActivity.currentlySelectedRecipe != null) {
                showIngredients(widget, context);
            } else {
                hideIngredients(widget, context);
            }
            appWidgetManager.updateAppWidget(appWidgetId, widget);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_ingredients_list_view);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    private void showIngredients(RemoteViews remoteViews, Context context) {
        // Create service intent to open to recipe selected
        Intent svcIntent = new Intent(context, WidgetService.class);
//        svcIntent.putExtra(EXTRA_RECIPE_DETAIL, RecipeDetailActivity.currentlySelectedRecipe);
//        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // update view with title and ingredients list
        String recipeName = RecipeDetailActivity.currentlySelectedRecipe.getString(RECIPE_NAME);
        remoteViews.setTextViewText(R.id.widget_title_text_view, recipeName);
        remoteViews.setViewVisibility(R.id.widget_ingredients_list_view, View.VISIBLE);
        remoteViews.setViewVisibility(R.id.widget_no_recipe_text_view, View.GONE);
        remoteViews.setRemoteAdapter(R.id.widget_ingredients_list_view, svcIntent);
    }

    private void hideIngredients(RemoteViews remoteViews, Context context) {
        // create intent to open main activity
        Intent clickIntent = new Intent(context, ViewRecipesActivity.class);
        PendingIntent clickPI = PendingIntent.getActivity(
                context,
                0,
                clickIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        // update view with a textview that tells user to select a recipe
        remoteViews.setTextViewText(R.id.widget_title_text_view, context.getString(R.string.app_name));
        remoteViews.setViewVisibility(R.id.widget_ingredients_list_view, View.GONE);
        remoteViews.setViewVisibility(R.id.widget_no_recipe_text_view, View.VISIBLE);
        remoteViews.setOnClickPendingIntent(R.id.widget_no_recipe_text_view, clickPI);
    }
}
