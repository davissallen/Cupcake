package me.davisallen.cupcake;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import static me.davisallen.cupcake.utils.JsonUtils.RECIPE_STEPS;

public class RecipeDetailActivity extends AppCompatActivity implements
        StepListRecyclerViewAdapter.StepListClickListener,
        StepDetailFragment.NavButtonListener {

    private static final String LOG_TAG = RecipeDetailActivity.class.getSimpleName();
    private String mRecipeName;
    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;
    private FragmentManager mFragmentManager;

    @BindView(R.id.fragment_container_step_list) FrameLayout mFragmentContainerStepList;
    @BindView(R.id.fragment_container_details) FrameLayout mFragmentContainerDetails;

    public static Bundle currentlySelectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get recipe info from received intent
        Intent receivedIntent = getIntent();
        Bundle extraBundle = receivedIntent.getBundleExtra(EXTRA_RECIPE_DETAIL);

        // set the currently selected recipe and update the widget
        currentlySelectedRecipe = extraBundle;
        updateWidget();

        // assign values from recipe info from received intent
        mRecipeName = extraBundle.getString(RECIPE_NAME);
        mSteps = extraBundle.getParcelableArrayList(RECIPE_STEPS);
        mIngredients = extraBundle.getParcelableArrayList(RECIPE_INGREDIENTS);

        // update the action bar with current recipe and provide up navigation
        updateSupportActionBar();

        // set the view to our fragment
        setContentView(R.layout.activity_recipe_detail);

        // bind views with butterknife library
        ButterKnife.bind(this);

        // open the step list fragment
        openFragment(FragmentType.RECIPE_STEP_LIST, 0, TransitionAnimation.ENTER_FROM_RIGHT);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }

        if (clickedItemIndex == 0) {
            // open up ingredient fragment
            openFragment(FragmentType.INGREDIENT_LIST, clickedItemIndex, TransitionAnimation.ENTER_FROM_RIGHT);
        } else {
            // open up detail fragment
            openFragment(FragmentType.RECIPE_DETAIL, clickedItemIndex-1, TransitionAnimation.ENTER_FROM_RIGHT);
        }
    }

    @Override
    public void onButtonClick(int clickedItemId, int position, int numberOfSteps) {
        if (clickedItemId == R.id.button_next) {
            // open up detail fragment
            openFragment(FragmentType.RECIPE_DETAIL, position+1, TransitionAnimation.ENTER_FROM_BOTTOM);
        } else if (clickedItemId == R.id.button_previous) {
            // open up detail fragment
            openFragment(FragmentType.RECIPE_DETAIL, position-1, TransitionAnimation.ENTER_FROM_TOP);
        }
        showDetailsFragment();
    }

    @Override
    public void onBackPressed() {
        if (mFragmentContainerStepList.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            hideDetailsFragment();
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

    private void updateSupportActionBar() {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null && mRecipeName != null) {
            supportActionBar.setTitle(mRecipeName);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showDetailsFragment() {
        mFragmentContainerDetails.setVisibility(View.VISIBLE);
        mFragmentContainerStepList.setVisibility(View.GONE);
    }

    private void hideDetailsFragment() {
        mFragmentContainerDetails.setVisibility(View.GONE);
        mFragmentContainerStepList.setVisibility(View.VISIBLE);
    }

    private void openFragment(FragmentType type, int position, TransitionAnimation anim) {
        if (mFragmentManager == null) {
            mFragmentManager = getSupportFragmentManager();
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (type == FragmentType.RECIPE_STEP_LIST) {
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            transaction.replace(R.id.fragment_container_step_list, StepListFragment.newInstance(mSteps, mIngredients));
            hideDetailsFragment();
        } else if (type == FragmentType.INGREDIENT_LIST) {
            transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            transaction.replace(
                    R.id.fragment_container_details,
                    IngredientsFragment.newInstance(mIngredients)
            );
            showDetailsFragment();
        } else if (type == FragmentType.RECIPE_DETAIL) {
            Step stepSelected = mSteps.get(position);
            switch (anim) {
                case ENTER_FROM_BOTTOM:
                    transaction.setCustomAnimations(R.anim.slide_in_from_bottom, R.anim.slide_out_to_top);
                    break;
                case ENTER_FROM_TOP:
                    transaction.setCustomAnimations(R.anim.slide_in_from_top, R.anim.slide_out_to_bottom);
                    break;
                case ENTER_FROM_RIGHT:
                    transaction.setCustomAnimations(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    break;
                case ENTER_FROM_LEFT:
                    transaction.setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
                    break;
            }
            transaction.replace(
                    R.id.fragment_container_details,
                    StepDetailFragment.newInstance(stepSelected, this, position, mSteps.size())
            );
            showDetailsFragment();
        }

        transaction.commit();
    }

    private enum FragmentType {
        RECIPE_STEP_LIST,
        INGREDIENT_LIST,
        RECIPE_DETAIL
    }

    private enum TransitionAnimation {
        ENTER_FROM_BOTTOM,
        ENTER_FROM_TOP,
        ENTER_FROM_RIGHT,
        ENTER_FROM_LEFT
    }
}
