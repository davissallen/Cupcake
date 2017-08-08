package me.davisallen.cupcake;

import android.content.Context;
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
import me.davisallen.cupcake.StepListRecyclerViewAdapter.StepListClickListener;
import me.davisallen.cupcake.pojo.Ingredient;
import me.davisallen.cupcake.pojo.Step;


public class StepListFragment extends Fragment {
    private static final String PARAM_STEPS = "steps";
    private static final String PARAM_INGREDIENTS = "ingredients";

    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;

    @BindView(R.id.recipe_steps_list_recycler_view) RecyclerView mStepsRecyclerView;
    private Unbinder unbinder;

    private StepListClickListener mListener;
    private StepListRecyclerViewAdapter mAdapter;

    public StepListFragment() {
        // Required empty public constructor
    }

    public static StepListFragment newInstance(ArrayList<Step> steps,
                                               ArrayList<Ingredient> ingredients) {
        StepListFragment fragment = new StepListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(PARAM_STEPS, steps);
        args.putParcelableArrayList(PARAM_INGREDIENTS, ingredients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSteps = getArguments().getParcelableArrayList(PARAM_STEPS);
            mIngredients = getArguments().getParcelableArrayList(PARAM_INGREDIENTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(
                R.layout.fragment_steps_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // assign layout manager to recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mStepsRecyclerView.setLayoutManager(layoutManager);
        // set fixed size for efficiency
        mStepsRecyclerView.setHasFixedSize(true);

        // get adapter reference
        StepListClickListener listener = (StepListClickListener) getActivity();
        mAdapter = new StepListRecyclerViewAdapter(getContext(), listener, mIngredients, mSteps);
        // set adapter onto RecyclerView
        mStepsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StepListClickListener) {
            mListener = (StepListClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StepListClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
