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

package com.example.android.bakeit.ui.fragments;

import android.content.Context;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakeit.data.DbContract.StepsEntry;
import com.example.android.bakeit.data.Step;
import com.example.android.bakeit.R;
import com.example.android.bakeit.ui.adapters.StepListAdapter;
import com.example.android.bakeit.utilities.DataUtilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Retrieves all the steps for a specific recipe using a CursorLoader. It then populates a
 * RecyclerView with the cursor data.
 */
public class StepsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, StepListAdapter.StepClickHandler {

    /** Member Variables */
    @BindView(R.id.rv_fragment_list_bottom) RecyclerView mStepsRecyclerView;
    @BindView(R.id.tv_fragment_list_title_bottom) TextView mStepsTextView;
    private StepListAdapter mStepsListAdapter;
    private String mRecipeName;
    private Cursor mCursor;
    private Boolean mIsTwoPane;

    /** Keys for the saveInstanceState */
    private static final String STATE_STEPS_LAYOUT_MANAGER = "steps_layout_manager";

    /** Variables to be used when saving states */
    private Parcelable mStepsLayoutManagerSavedState = null;

    /** Ids of the cursor loaders */
    private static final int STEPS_LOADER_ID = 72;

    /** Empty constructor required to create a fragment */
    public StepsFragment() {}

    /** Member instance of OnFragmentClickListener interface */
    private OnFragmentClickListener mCallback;

    /** Interface used to implement on click functionality to an activity */
    public interface OnFragmentClickListener {
        void onClick(ArrayList<Step> steps, int position);
    }

    /**
     * This makes sure that the host activity has implemented the callback interface
     * If not, it throws an exception
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (OnFragmentClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentClickListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the root view by using the appropriate xml
        View view = inflater.inflate(R.layout.fragment_steps_list, container, false);

        // Uses @ButterKnife (http://jakewharton.github.io/butterknife/)
        ButterKnife.bind(this, view);

        ButterKnife.setDebug(true);

        // Get the bundle from the fragment arguments
        Bundle bundle = getArguments();

        // Get the typeface
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/courgette_regular.ttf");

        // Get the current recipe name
        mRecipeName = bundle.getString(DataUtilities.KEY_RECIPE_NAME);
        mIsTwoPane = bundle.getBoolean(DataUtilities.KEY_IS_TWO_PANE);


        // Set the text in the TextView
        mStepsTextView.setText(getString(R.string.steps_title));
        mStepsTextView.setTypeface(font);

        // Create a Linear layout manager and assign them to the recycler view
        LinearLayoutManager stepsLayoutManager = new LinearLayoutManager(this.getContext());
        mStepsRecyclerView.setLayoutManager(stepsLayoutManager);
        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mStepsRecyclerView.setHasFixedSize(true);

        // Check if this is the the first creation of the fragment or if it has been restored.
        if (savedInstanceState != null) {
            // Get the recycler view state
            mStepsLayoutManagerSavedState = savedInstanceState.getParcelable(STATE_STEPS_LAYOUT_MANAGER);

            // This is a restored fragment, therefore restore the RecyclerViews
            mStepsRecyclerView.getLayoutManager().onRestoreInstanceState(mStepsLayoutManagerSavedState);
        }

        // Create a new IngredientListAdapter and a new StepListAdapter
        mStepsListAdapter = new StepListAdapter(getContext(), this);

        // Create a new instance of both adapters and set them to the RecyclerViews
        mStepsRecyclerView.setAdapter(mStepsListAdapter);

        // Initialize the loaders
        getLoaderManager().initLoader(STEPS_LOADER_ID, null, this);

        // Return the root view
        return view;
    }

    /** Saves the current state of the RecyclerView */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the current state
        mStepsLayoutManagerSavedState = mStepsRecyclerView.getLayoutManager()
                .onSaveInstanceState();

        outState.putParcelable(STATE_STEPS_LAYOUT_MANAGER, mStepsLayoutManagerSavedState);
        super.onSaveInstanceState(outState);
    }

    /** Implementation of interface to enable click handling */
    @Override
    public void onItemClick(ArrayList<Step> steps, int newPosition, int oldPosition) {
        // Send the clicked step to the click handler
        mCallback.onClick(steps, newPosition);

        // Highlight the clicked item
        highlightStep(newPosition, oldPosition);
    }

    /** Highlights the currently selected step and un-highlights the previous one */
    private void highlightStep(int newPosition, int oldPosition) {
        // Check if the user clicked the same item as before
        if (newPosition != oldPosition && mIsTwoPane) {
            // Highlight the item
            mStepsRecyclerView.findViewHolderForAdapterPosition(newPosition).itemView.setSelected
                    (true);
            // Check if the old position is -1, meaning a first run
            if (oldPosition != -1 && mCursor.getCount() != 0 && mCursor != null) {
                // Remove highlight from the previous item
                mStepsRecyclerView.findViewHolderForAdapterPosition(oldPosition).itemView.setSelected(false);
            }
        }
    }

    /** Instantiates and returns a loader querying for the recipe ingredients & steps */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case STEPS_LOADER_ID:
                // Get the Content Uri for the steps table
                Uri stepsUri = StepsEntry.CONTENT_URI;

                // Return all rows with the current recipe's name in the appropriate column
                return new CursorLoader(getContext(),
                        stepsUri,
                        null,
                        StepsEntry.COLUMN_RECIPE + "=?",
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
            case STEPS_LOADER_ID:
                // Check if the cursor is null or empty
                if (data != null && data.getCount() != 0) {
                    // Pass the cursor to the adapter
                    mStepsListAdapter.swapCursor(data);
                    mCursor = data;
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
            case STEPS_LOADER_ID:
                mStepsListAdapter.swapCursor(null);
                break;
        }
    }
}