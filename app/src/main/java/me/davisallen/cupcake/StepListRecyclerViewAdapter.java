package me.davisallen.cupcake;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davisallen.cupcake.pojo.Ingredient;
import me.davisallen.cupcake.pojo.Step;

/**
 * Package Name:   me.davisallen.cupcake
 * Project:        Cupcake
 * Created by davis, on 8/2/17
 */

public class StepListRecyclerViewAdapter extends
        RecyclerView.Adapter<StepListRecyclerViewAdapter.StepListItemViewHolder> {

    private final StepListClickListener mOnClickListener;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;
    private Context mContext;

    public StepListRecyclerViewAdapter(
            Context context, StepListClickListener listener,
            ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        mContext = context;
        mOnClickListener = listener;
        mIngredients = ingredients;
        mSteps = steps;
    }

    public class StepListItemViewHolder extends
            RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.step_ingredient_text_view) TextView stepIngredientTextView;

        public StepListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }

    public interface StepListClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public StepListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int recipeDetailListItemId = R.layout.list_item_recipe_detail;
        View view = inflater.inflate(recipeDetailListItemId, parent, false);
        StepListItemViewHolder viewHolder = new StepListItemViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StepListItemViewHolder viewHolder, final int position) {
        if (position == 0) {
            // Ingredients list item
            viewHolder.stepIngredientTextView.setTextSize(24);
            viewHolder.stepIngredientTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            viewHolder.stepIngredientTextView.setText(R.string.ingredients_title);
        } else if (position == 1) {
            // Recipe Introduction list item
            viewHolder.stepIngredientTextView.setTextSize(24);
            viewHolder.stepIngredientTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            viewHolder.stepIngredientTextView.setText(mSteps.get(position-1).getShortDescription());
        }
        else {
            // Step list item
            String stepListItemText = String.format(
                    Locale.US,
                    "%d. %s", position-1, mSteps.get(position-1).getShortDescription()
            );
            viewHolder.stepIngredientTextView.setTextSize(20);
            viewHolder.stepIngredientTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            viewHolder.stepIngredientTextView.setText(stepListItemText);
        }
    }

    @Override
    public int getItemCount() {
        // returns one extra to include Ingredients list item
        return mSteps.size() + 1;
    }
}
