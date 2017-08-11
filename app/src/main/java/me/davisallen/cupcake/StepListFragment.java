package me.davisallen.cupcake;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
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
    public static final String BUNDLE_RECYCLER_LAYOUT = "StepListFragment.recycler.layout";
    private static final String LOG_TAG = StepListFragment.class.getSimpleName();

    private ArrayList<Step> mSteps;
    private ArrayList<Ingredient> mIngredients;

    @BindView(R.id.recipe_steps_list_recycler_view) RecyclerView mStepsRecyclerView;
    private Unbinder unbinder;

    private StepListClickListener mListener;
    private StepListRecyclerViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mCurrentPosition;

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
        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.scrollToPosition(mCurrentPosition);
        mStepsRecyclerView.setLayoutManager(mLayoutManager);

        // set fixed size for efficiency
        mStepsRecyclerView.setHasFixedSize(true);

        // get adapter reference
        StepListClickListener listener = (StepListClickListener) getActivity();
        mAdapter = new StepListRecyclerViewAdapter(getContext(), listener, mIngredients, mSteps);
        // set adapter onto RecyclerView
        mStepsRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mStepsRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mStepsRecyclerView.getLayoutManager().onSaveInstanceState());
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
