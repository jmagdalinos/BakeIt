/*
* Copyright (C) 2017 John Magdalinos
* Copyright 2017 The Android Open Source Project, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*  	http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.bakeit.UI.Fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeit.Data.DbContract.IngredientsEntry;
import com.example.android.bakeit.R;
import com.example.android.bakeit.UI.Adapters.IngredientListAdapter;
import com.example.android.bakeit.Utilities.DataUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Retrieves all the steps for a specific recipe using a CursorLoader. It then populates a
 * RecyclerView with the cursor data.
 */

public class IngredientsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Member Variables */
    @BindView(R.id.tv_fragment_list_servings_title) TextView mServingsTextView;
    @BindView(R.id.rv_fragment_list_top) RecyclerView mIngredientsRecyclerView;
    @BindView(R.id.tv_fragment_list_title_top) TextView mIngredientsTextView;
    private IngredientListAdapter mIngredientsAdapter;
    private String mRecipeName;
    private int mIngredientsMinHeight;
    private int mIngredientsMaxHeight;
    private ImageView mCollapsingArrow;

    /** Keys for the saveInstanceState */
    private static final String STATE_INGREDIENTS_LAYOUT_MANAGER = "ingredients_layout_manager";
    private static final String STATE_MAX_HEIGHT = "max_height";
    private static final String STATE_MIN_HEIGHT = "min_height";

    /** Variables to be used when saving states */
    private Parcelable mIngredientsLayoutManagerSavedState = null;

    /** Ids of the cursor loaders */
    private static final int INGREDIENT_LOADER_ID = 62;

    /** Empty constructor required to create a fragment */
    public IngredientsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the root view by using the appropriate xml
        View view = inflater.inflate(R.layout.fragment_ingredients_list, container, false);

        // Uses @ButterKnife (http://jakewharton.github.io/butterknife/)
        ButterKnife.bind(this, view);

        // Get the CardView and the rotating arrow
        CardView cardView = (CardView) view.findViewById(R.id.fragment_card_top);
        mCollapsingArrow = (ImageView) view.findViewById(R.id.iv_fragment_list_collapse_arrow);

        // Get the CardView and the rotating arrow
        mCollapsingArrow = (ImageView) view.findViewById(R.id.iv_fragment_list_collapse_arrow);

        // Find the max height of the ingredients
        mIngredientsRecyclerView.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mIngredientsMaxHeight = mIngredientsRecyclerView.getMeasuredHeight();

        // Setup the CardView's collapse/expansion and get the ingredients' min height
        mIngredientsRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mIngredientsRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                mIngredientsMinHeight = mIngredientsRecyclerView.getHeight();
                ViewGroup.LayoutParams layoutParams = mIngredientsRecyclerView.getLayoutParams();
                layoutParams.height = mIngredientsMinHeight;
                mIngredientsRecyclerView.setLayoutParams(layoutParams);
                return true;
            }
        });

        // Set an on click listener on the card view to trigger its collapse/expansion
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCardViewHeight(mIngredientsMaxHeight);
            }
        });

        // Get the bundle from the fragment arguments
        Bundle bundle = getArguments();

        // Get the typeface
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/courgette_regular.ttf");

        // Get the current recipe name
        mRecipeName = bundle.getString(DataUtilities.KEY_RECIPE_NAME);

        // Get the current recipe servings
        String recipeServings = bundle.getString(DataUtilities.KEY_RECIPE_SERVINGS);

        // Set the texts in the TextViews
        mServingsTextView.setText("(for " + recipeServings + " servings)");
        mServingsTextView.setTypeface(font);
        mIngredientsTextView.setText(getString(R.string.ingredients_title));
        mIngredientsTextView.setTypeface(font);

        // Create a Linear layout manager and assign it to the recycler view
        LinearLayoutManager ingredientLayoutManager = new LinearLayoutManager(this.getContext());
        mIngredientsRecyclerView.setLayoutManager(ingredientLayoutManager);
        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mIngredientsRecyclerView.setHasFixedSize(true);

        // Check if this is the the first creation of the fragment or if it has been restored.
        if (savedInstanceState != null) {
            // Get the recycler view state
            mIngredientsLayoutManagerSavedState = savedInstanceState.getParcelable(STATE_INGREDIENTS_LAYOUT_MANAGER);

            // This is a restored fragment, therefore restore the RecyclerViews
            mIngredientsRecyclerView.getLayoutManager().onRestoreInstanceState(mIngredientsLayoutManagerSavedState);
        }

        // Create a new IngredientListAdapter and a new StepListAdapter
        mIngredientsAdapter = new IngredientListAdapter();

        // Create a new instance of both adapters and set them to the RecyclerViews
        mIngredientsRecyclerView.setAdapter(mIngredientsAdapter);

        // Initialize the loaders
        getLoaderManager().initLoader(INGREDIENT_LOADER_ID, null, this);

        // Return the root view
        return view;
    }

    /** Saves the current state of the RecyclerView */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the current state
        mIngredientsLayoutManagerSavedState = mIngredientsRecyclerView.getLayoutManager()
                .onSaveInstanceState();

        outState.putParcelable(STATE_INGREDIENTS_LAYOUT_MANAGER, mIngredientsLayoutManagerSavedState);
        outState.putInt(STATE_MAX_HEIGHT, mIngredientsMaxHeight);
        outState.putInt(STATE_MIN_HEIGHT, mIngredientsMinHeight);
        super.onSaveInstanceState(outState);
    }

    /** Checks if the CardView is expanded or collapsed and calls the appropriate method */
    private void toggleCardViewHeight(int height) {
        if (mIngredientsRecyclerView.getHeight() == mIngredientsMinHeight) {
            // Expand the CardView
            expandView(height);
        } else {
            // Collapse the CardView
            collapseView();
        }
    }

    /** Expands the CardView to the given height */
    private void expandView(int height) {
        ValueAnimator animator = ValueAnimator.ofInt(mIngredientsRecyclerView.getMeasuredHeightAndState(), height);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mIngredientsRecyclerView.getLayoutParams();
                layoutParams.height = val;
                mIngredientsRecyclerView.setLayoutParams(layoutParams);
            }
        });

        animator.start();

        // Rotate the arrow
        float rotation = mCollapsingArrow.getRotation();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mCollapsingArrow, "rotation", rotation,
                rotation + 180);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    /** Collapses the CardView to the min height */
    private void collapseView() {
        ValueAnimator animator = ValueAnimator.ofInt(mIngredientsRecyclerView
                .getMeasuredHeightAndState(), mIngredientsMinHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int val = (Integer) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = mIngredientsRecyclerView.getLayoutParams();
                layoutParams.height = val;
                mIngredientsRecyclerView.setLayoutParams(layoutParams);
            }
        });

        animator.start();

        // Rotate the arrow
        float rotation = mCollapsingArrow.getRotation();
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mCollapsingArrow, "rotation",
                rotation, rotation + 180);
        objectAnimator.setDuration(500);
        objectAnimator.start();
    }

    /** Instantiates and returns a loader querying for the recipe ingredients & steps */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case INGREDIENT_LOADER_ID:
                // Get the Content Uri for the ingredients table
                Uri ingredientsUri = IngredientsEntry.CONTENT_URI;

                // Return all rows with the current recipe's name in the appropriate column
                return new CursorLoader(getContext(),
                        ingredientsUri,
                        null,
                        IngredientsEntry.COLUMN_RECIPE + "=?",
                        new String[]{mRecipeName},
                        null);

            default:
                throw new RuntimeException("Loader " + loaderId + " not implemented");
        }
    }

    /** The loader has finished retrieving the data. Pass the cursor to the adapter */
    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        switch (loader.getId()) {
            case INGREDIENT_LOADER_ID:
                // Check if the cursor is null or empty
                if (data != null && data.getCount() != 0) {
                    // Pass the cursor to the adapter
                    mIngredientsAdapter.swapCursor(data);
                }
                break;
            default:
                throw new RuntimeException("Loader " + loader.getId() + " not implemented");
        }
    }

    /** The loader is has been reset therefore swap the cursor for a null one */
    @Override
    public void onLoaderReset(Loader loader) {
        // The cursor is now empty. Clear the adapter
        switch (loader.getId()) {
            case INGREDIENT_LOADER_ID:
                mIngredientsAdapter.swapCursor(null);
                break;
        }
    }
}