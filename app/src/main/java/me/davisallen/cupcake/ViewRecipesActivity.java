package me.davisallen.cupcake;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davisallen.cupcake.pojo.Recipe;
import me.davisallen.cupcake.utils.NetworkUtils;
import me.davisallen.cupcake.utils.ToastUtils;

import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_INGREDIENTS;
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_NAME;
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_STEPS;

public class ViewRecipesActivity extends AppCompatActivity implements
        ViewRecipesRecyclerViewAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<ArrayList<Recipe>> {

    public static final String EXTRA_RECIPE_DETAIL = "recipe_detail";

    private static final int LOADER_ID = 1;
    private static final String LOG_TAG = ViewRecipesActivity.class.getSimpleName();
    private ArrayList<Recipe> mRecipes;
    private LoaderManager mLoaderManager;
    private ViewRecipesRecyclerViewAdapter mAdapter;
    private ActionBar mActionBar;
    private boolean mIsTablet;

    // get reference to recyclerview and splash screen
    @BindView(R.id.recipe_recycler_view) RecyclerView mRecipeRecyclerView;
    @BindView(R.id.splash_screen_layout) FrameLayout mSplashScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        // hide action bar and show splash screen
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.hide();
        }
        setContentView(R.layout.activity_view_recipes);

        // bind ButterKnife views
        ButterKnife.bind(this);

        // load recipes in background
        mLoaderManager = getSupportLoaderManager();

        mLoaderManager.initLoader(LOADER_ID, null, this);

        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        RecyclerView.LayoutManager layoutManager;
        if (mIsTablet) {
            layoutManager = new GridLayoutManager(this, 2);
        } else {
            layoutManager = new LinearLayoutManager(this);
        }

        // assign layout manager to recycler view
        mRecipeRecyclerView.setLayoutManager(layoutManager);

        // set fixed size for efficiency
        mRecipeRecyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Recipe clickedRecipe = mRecipes.get(clickedItemIndex);

        ToastUtils.makeCustomToast(this, "Tonight, I'm makin " + clickedRecipe.getName());

        Bundle recipeDetail = new Bundle();
        recipeDetail.putString(RECIPE_NAME, clickedRecipe.getName());
        recipeDetail.putParcelableArrayList(RECIPE_STEPS, clickedRecipe.getSteps());
        recipeDetail.putParcelableArrayList(RECIPE_INGREDIENTS, clickedRecipe.getIngredients());

        Intent openRecipeDetailIntent = new Intent(this, RecipeDetailActivity.class);
        openRecipeDetailIntent.putExtra(EXTRA_RECIPE_DETAIL, recipeDetail);

        startActivity(openRecipeDetailIntent);
    }

    @Override
    public Loader<ArrayList<Recipe>> onCreateLoader(int id, Bundle args) {
        if (NetworkUtils.isNetworkAvailable(this)) {
            return new ViewRecipesAsyncTaskLoader(this);
        } else {
            ToastUtils.makeCustomToast(this, "Please connect to the Internet to view recipes.");
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Recipe>> loader, ArrayList<Recipe> data) {
        if (loader == null || loader.getId() != LOADER_ID) {
            // loader not received, some error occurred
            Log.e(LOG_TAG, "Recipes could not be retrieved from the server :(");
            return;
        } else {
            mRecipes = data;
        }

        // get adapter reference
        mAdapter = new ViewRecipesRecyclerViewAdapter(this, mRecipes, this);

        // set adapter onto RecyclerView
        mRecipeRecyclerView.setAdapter(mAdapter);

        // hide splash screen and show recyclerview with action bar
        mSplashScreen.setVisibility(View.GONE);
        if (mActionBar != null) {
            mActionBar.show();
        }
        mRecipeRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
