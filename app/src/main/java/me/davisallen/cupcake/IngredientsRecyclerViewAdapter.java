package me.davisallen.cupcake;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davisallen.cupcake.pojo.Ingredient;

/**
 * Package Name:   me.davisallen.cupcake
 * Project:        Cupcake
 * Created by davis, on 8/3/17
 */

// TODO: Implement listener that crosses the lines out (like shopping list)

public class IngredientsRecyclerViewAdapter extends
        RecyclerView.Adapter<IngredientsRecyclerViewAdapter.IngredientViewHolder> {

    private ArrayList<Ingredient> mIngredients;

    public IngredientsRecyclerViewAdapter(ArrayList<Ingredient> ingredients) {
        mIngredients = ingredients;
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_item_ingredient) TextView listItemIngredientTextView;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        int ingredientListItemId = R.layout.list_item_ingredient;
        View view = inflater.inflate(ingredientListItemId, parent, false);
        IngredientViewHolder viewHolder = new IngredientViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder viewHolder, int position) {
        Ingredient ingredient = mIngredients.get(position);
        String ingredientLabel = ingredient.toString();
        viewHolder.listItemIngredientTextView.setText(ingredientLabel);
    }

    @Override
    public int getItemCount() {
        return mIngredients.size();
    }
}

