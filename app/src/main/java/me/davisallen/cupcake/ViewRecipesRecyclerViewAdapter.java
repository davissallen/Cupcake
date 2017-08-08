package me.davisallen.cupcake;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
    private Context mContext;

    public ViewRecipesRecyclerViewAdapter(ListItemClickListener listener,
                                          ArrayList<Recipe> recipes,
                                          Context context) {
        mOnClickListener = listener;
        mRecipes = recipes;
        mContext = context;
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
        Recipe recipe = mRecipes.get(position);

        viewHolder.recipeTitleTextView.setText(recipe.getName());

        // set to image resource if exists, otherwise default to the cupcake logo
        String imageUrl = recipe.getImagePath();
        if (imageUrl != null && imageUrl.length() > 0) {
            Picasso.with(mContext).load(imageUrl).into(viewHolder.recipeImageView);
        } else {
            viewHolder.recipeImageView.setImageResource(R.drawable.birthday);
        }

        String servingsText = "Serves: " + String.valueOf(mRecipes.get(position).getServings());
        viewHolder.servingsTextView.setText(servingsText);
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }
}
