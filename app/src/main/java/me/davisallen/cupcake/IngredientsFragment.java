package me.davisallen.cupcake;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.davisallen.cupcake.pojo.Ingredient;


public class IngredientsFragment extends Fragment {
    private static final String INGREDIENTS_ARRAYLIST = "ingredients_array_list";

    @BindView(R.id.fragment_ingredients_recycler_view) RecyclerView mIngredientsFragmentRecyclerView;
    private Unbinder unbinder;

    private ArrayList<Ingredient> mIngredients;
    private IngredientsRecyclerViewAdapter mAdapter;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    public static IngredientsFragment newInstance(ArrayList<Ingredient> ingredients) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(INGREDIENTS_ARRAYLIST, ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIngredients = getArguments().getParcelableArrayList(INGREDIENTS_ARRAYLIST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_ingredients, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mIngredientsFragmentRecyclerView.setLayoutManager(layoutManager);
        // set fixed size for efficiency
        mIngredientsFragmentRecyclerView.setHasFixedSize(true);

        // get adapter reference
        mAdapter = new IngredientsRecyclerViewAdapter(mIngredients);
        // set adapter onto RecyclerView
        mIngredientsFragmentRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
