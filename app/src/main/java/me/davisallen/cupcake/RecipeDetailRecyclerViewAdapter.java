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

public class RecipeDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecipeDetailRecyclerViewAdapter.RecipeDetailViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private ArrayList<Ingredient> mIngredients;
    private ArrayList<Step> mSteps;
    private Context mContext;

    public RecipeDetailRecyclerViewAdapter (Context context, ListItemClickListener listener, ArrayList<Ingredient> ingredients, ArrayList<Step> steps) {
        mContext = context;
        mOnClickListener = listener;
        mIngredients = ingredients;
        mSteps = steps;
    }

    public class RecipeDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.step_ingredient_text_view) TextView stepIngredientTextView;

        public RecipeDetailViewHolder(View itemView) {
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

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    @Override
    public RecipeDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int recipeDetailListItemId = R.layout.list_item_recipe_detail;
        View view = inflater.inflate(recipeDetailListItemId, parent, false);
        RecipeDetailViewHolder viewHolder = new RecipeDetailViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeDetailViewHolder viewHolder, final int position) {
        if (position == 0) {
            // if is ingredient, set style to ingredientListItem
            viewHolder.stepIngredientTextView.setTextSize(24);
            viewHolder.stepIngredientTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            viewHolder.stepIngredientTextView.setText(R.string.ingredients_title);
        } else if (position == 1) {
            viewHolder.stepIngredientTextView.setTextSize(24);
            viewHolder.stepIngredientTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            viewHolder.stepIngredientTextView.setText(mSteps.get(position-1).getShortDescription());
        }
        else {
            // else, set style to stepListItem
            viewHolder.stepIngredientTextView.setTextSize(20);
            viewHolder.stepIngredientTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryText));
            viewHolder.stepIngredientTextView.setText(
                    String.format(
                            Locale.US,
                            "%d. %s", position-1, mSteps.get(position-1).getShortDescription()
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return mSteps.size() + 1;
    }
}
