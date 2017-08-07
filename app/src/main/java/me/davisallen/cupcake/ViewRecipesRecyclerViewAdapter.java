package me.davisallen.cupcake;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davisallen.cupcake.pojo.Recipe;

/**
 * Package Name:   me.davisallen.cupcake
 * Project:        Cupcake
 * Created by davis, on 8/2/17
 */

public class ViewRecipesRecyclerViewAdapter extends
        RecyclerView.Adapter<ViewRecipesRecyclerViewAdapter.RecipeViewHolder> {

    private final ListItemClickListener mOnClickListener;
    private ArrayList<Recipe> mRecipes;

    public ViewRecipesRecyclerViewAdapter(ListItemClickListener listener, ArrayList<Recipe> recipes) {
        mOnClickListener = listener;
        mRecipes = recipes;
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_title) TextView recipeTitleTextView;
        @BindView(R.id.recipe_image) ImageView recipeImageView;
        @BindView(R.id.servings_text_view) TextView servingsTextView;

        public RecipeViewHolder(View itemView) {
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
    public RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int recipeListItemId = R.layout.list_item_recipe;
        View view = inflater.inflate(recipeListItemId, parent, false);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder viewHolder, final int position) {
        viewHolder.recipeTitleTextView.setText(mRecipes.get(position).getName());
        viewHolder.recipeImageView.setImageResource(R.drawable.birthday);
        String servingsText = "Serves: " + String.valueOf(mRecipes.get(position).getServings());
        viewHolder.servingsTextView.setText(servingsText);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
