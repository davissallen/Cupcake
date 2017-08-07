package me.davisallen.cupcake;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import me.davisallen.cupcake.pojo.Recipe;
import me.davisallen.cupcake.utils.NetworkUtils;

import static me.davisallen.cupcake.utils.JsonUtils.getRecipesFromHttpResponse;

public class ViewRecipesAsyncTaskLoader extends AsyncTaskLoader<ArrayList<Recipe>> {

    private static final String LOG_TAG = ViewRecipesAsyncTaskLoader.class.getSimpleName();
    Context mContext;

    public ViewRecipesAsyncTaskLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public ArrayList<Recipe> loadInBackground() {
        String httpResponse = NetworkUtils.getResponseFromHttp();
        ArrayList<Recipe> recipes = getRecipesFromHttpResponse(httpResponse);
        return recipes;
    }

    @Override
    public void deliverResult(ArrayList<Recipe> recipes) {
        super.deliverResult(recipes);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
