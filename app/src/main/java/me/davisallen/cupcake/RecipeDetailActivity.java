package me.davisallen.cupcake;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davisallen.cupcake.pojo.Ingredient;
import me.davisallen.cupcake.pojo.Step;

import static me.davisallen.cupcake.ViewRecipesActivity.EXTRA_RECIPE_DETAIL;
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_INGREDIENTS;
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_NAME;
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_SERVINGS;
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_STEPS;

public class RecipeDetailActivity extends AppCompatActivity implements
        RecipeDetailRecyclerViewAdapter.ListItemClickListener,
        StepDetailFragment.FragmentNavButtonListener {

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    private String mRecipeName;
    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;
    private int mServings;
    private ActionBar mActionBar;
    private RecipeDetailRecyclerViewAdapter mAdapter;
    private FragmentManager mFragmentManager;
    private boolean mIsFragmentOpen;

    public static Bundle currentlySelectedRecipe;

    @BindView(R.id.recipe_detail_recycler_view) RecyclerView mRecipeDetailRecyclerDetail;
    @BindView(R.id.fragment_container) FrameLayout mFragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        Bundle extraBundle = receivedIntent.getBundleExtra(EXTRA_RECIPE_DETAIL);

        currentlySelectedRecipe = extraBundle;
        updateWidget();

        mIsFragmentOpen = false;

        mRecipeName = extraBundle.getString(RECIPE_NAME);
        mSteps = extraBundle.getParcelableArrayList(RECIPE_STEPS);
        mIngredients = extraBundle.getParcelableArrayList(RECIPE_INGREDIENTS);
        mServings = extraBundle.getInt(RECIPE_SERVINGS, -1);

        mActionBar = getSupportActionBar();
        if (mActionBar != null && mRecipeName != null) {
            mActionBar.setTitle(mRecipeName);
            // TODO: Add proper up navigation?
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        // assign layout manager to recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecipeDetailRecyclerDetail.setLayoutManager(layoutManager);
        // set fixed size for efficiency
        mRecipeDetailRecyclerDetail.setHasFixedSize(true);

        // get adapter reference
        mAdapter = new RecipeDetailRecyclerViewAdapter(this, this, mIngredients, mSteps);
        // set adapter onto RecyclerView
        mRecipeDetailRecyclerDetail.setAdapter(mAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        // TODO
        if (mIsFragmentOpen) {
            onBackPressed();
            return false;
        }

        return super.onSupportNavigateUp();
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (clickedItemIndex == 0) {
            // open up ingredient fragment
            createFragment(
                    mFragmentManager,
                    IngredientsFragment.newInstance(mIngredients),
                    true,
                    R.anim.slide_in_from_right,
                    R.anim.slide_out_to_left
            );
        } else {
            // open up step fragment
            Step stepSelected = mSteps.get(clickedItemIndex-1);
            createFragment(
                    mFragmentManager,
                    StepDetailFragment.newInstance(
                            stepSelected,
                            this,
                            clickedItemIndex-1,
                            mSteps.size()
                    ),
                    true,
                    R.anim.slide_in_from_right,
                    R.anim.slide_out_to_left
            );
        }

        showFragment();
    }

    private static void createFragment(
            FragmentManager fm, Fragment fragmentToStart, boolean addToBackStack, int enterAnim, int exitAnim) {

        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(enterAnim, exitAnim);
        transaction.replace(R.id.fragment_container, fragmentToStart);
        if (addToBackStack) { transaction.addToBackStack(null); }
        transaction.commit();
    }

    private void showFragment() {
        mRecipeDetailRecyclerDetail.setVisibility(View.GONE);
        mFragmentContainer.setVisibility(View.VISIBLE);
        mIsFragmentOpen = true;
    }

    private void hideFragment() {
        mRecipeDetailRecyclerDetail.setVisibility(View.VISIBLE);
        mFragmentContainer.setVisibility(View.GONE);
        mIsFragmentOpen = false;

        // I feel horrible for this, but forcing the view to pause and resume itself so it forces
        // the fragment to pause to stop the mPlayer object, otherwise the simpleexoplayer continues
        // to play the video.
        onPause();
        onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideFragment();
    }

    @Override
    public void onButtonClick(int clickedItemId, int position, int numberOfSteps) {
        if (clickedItemId == R.id.button_next) {
            Step stepSelected = mSteps.get(position+1);
            createFragment(
                    mFragmentManager,
                    StepDetailFragment.newInstance(
                            stepSelected,
                            this,
                            position+1,
                            mSteps.size()
                    ),
                    false,
                    R.anim.slide_in_from_top,
                    R.anim.slide_out_to_bottom
            );
        } else if (clickedItemId == R.id.button_previous) {
            Step stepSelected = mSteps.get(position-1);
            createFragment(
                    mFragmentManager,
                    StepDetailFragment.newInstance(
                            stepSelected,
                            this,
                            position-1,
                            mSteps.size()
                    ),
                    false,
                    R.anim.slide_in_from_bottom,
                    R.anim.slide_out_to_top
            );
        }
    }

    public void updateWidget() {
        Intent intent = new Intent(this, WidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), WidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
        Log.d(LOG_TAG, "all widget updated");
    }
}
