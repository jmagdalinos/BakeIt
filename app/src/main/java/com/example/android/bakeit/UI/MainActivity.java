/*
* Copyright (C) 2017 John Magdalinos
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

package com.example.android.bakeit.UI;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakeit.Data.DbContract.RecipesEntry;
import com.example.android.bakeit.R;
import com.example.android.bakeit.Sync.SyncService;
import com.example.android.bakeit.UI.Adapters.RecipeListAdapter;
import com.example.android.bakeit.Utilities.DataUtilities;
import com.example.android.bakeit.Utilities.SimpleIdlingResource;


/**
 * The master activity
 * Checks if the device is a tablet.
 * If so, it loads all the recipes into a grid..
 * If not, it loads all the recipes into a list
 * It then launches the DetailActivity
 */
public class MainActivity extends AppCompatActivity implements
        RecipeListAdapter.RecipeClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Id of the cursor loader */
    private static final int RECIPE_LOADER_ID = 52;

    /** Member Variables */
    private RecyclerView mRecyclerView;
    private Button mTryAgainButton;
    private LinearLayout mNoRecipesLayout;
    private RecipeListAdapter mListAdapter;
    private boolean mIsTablet;

    /** Key for the RecyclerView layout list in saveInstanceState */
    private static final String STATE_LINEAR_LAYOUT_MANAGER = "linear_layout_manager";
    private static final String STATE_GRID_LAYOUT_MANAGER = "grid_layout_manager";

    /** Parcelable to be used when saving the RecyclerView state */
    private Parcelable mLinearLayoutManagerSavedState = null;
    private Parcelable mGridLayoutManagerSavedState = null;

    /** SimpleIdlingResource used for testing */
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/courgette_regular.ttf");

        // Get the toolbar and its TextView
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        // Set the text and typeface of the toolbar
        textView.setTypeface(font);

        // Get the boolean value of mIsTablet to distinguish between Phone and Tablet
        mIsTablet = getResources().getBoolean(R.bool.is_tablet);

        // Find the LinearLayout and button
        mNoRecipesLayout = (LinearLayout) findViewById(R.id.ll_no_internet);
        mTryAgainButton = (Button) findViewById(R.id.btn_try_again);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_recipes_list);

        // Set onClickListener on Button
        mTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sync the data from the server
                initializeSync();
            }
        });

        // Hide the TextView & Button for no recipes
        showNoRecipesMessage(false);

        // Sync the data from the server
        initializeSync();

        // Check if this is a tablet
        if (mIsTablet) {
            // Create the layout for tablets
            createTabletLayout();
        } else {
            // Create the layout for phones
            createPhoneLayout();
        }

        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mRecyclerView.setHasFixedSize(true);

        // Check if this is the the first creation of the fragment or if it has been restored.
        if (savedInstanceState != null) {
            // Get the recycler view state
            if (mIsTablet) {
                mGridLayoutManagerSavedState = savedInstanceState.getParcelable(STATE_GRID_LAYOUT_MANAGER);
                // This is a restored fragment, therefore restore the RecyclerView
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mGridLayoutManagerSavedState);
            } else {
                mLinearLayoutManagerSavedState = savedInstanceState.getParcelable(STATE_LINEAR_LAYOUT_MANAGER);
                // This is a restored fragment, therefore restore the RecyclerView
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mLinearLayoutManagerSavedState);
            }
        }

        // Create a new instance of a RecipeListAdapter
        mListAdapter = new RecipeListAdapter(this, this);

        // Set the adapter on the RecyclerView
        mRecyclerView.setAdapter(mListAdapter);

        // Initialize the loader
        getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);
    }

    /** Saves the current state of the RecyclerView */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the RecyclerView state
        if (mIsTablet) {
            // Save the state for tablets
            mGridLayoutManagerSavedState = mRecyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(STATE_GRID_LAYOUT_MANAGER, mGridLayoutManagerSavedState);
        } else {
            mLinearLayoutManagerSavedState = mRecyclerView.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(STATE_LINEAR_LAYOUT_MANAGER, mLinearLayoutManagerSavedState);
        }
        super.onSaveInstanceState(outState);
    }

    /** Creates a LinearLayout for the RecyclerView to be used on Phones */
    private void createPhoneLayout() {
        // Create a Linear layout manager and assign it to the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
    }

    /** Creates a GridLayout for the RecyclerView to be used on Tablets */
    private void createTabletLayout() {
        // Create a Grid layout manager and assign it to the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
    }

    /** Implementation of interface to enable click handling */
    @Override
    public void onItemClick(String recipeName, String servings, String image) {
        // Create a new intent to open the detail activity
        Intent detailIntent = new Intent(this, DetailActivity.class);
        // Pass the recipe name as an extra
        detailIntent.putExtra(DataUtilities.KEY_RECIPE_NAME, recipeName);
        detailIntent.putExtra(DataUtilities.KEY_RECIPE_SERVINGS, servings);
        detailIntent.putExtra(DataUtilities.KEY_RECIPE_IMAGE, image);
        // Start the intent
        startActivity(detailIntent);
    }

    /** Toggles visibility of TextView & Button when there is nothing to show */
    private void showNoRecipesMessage(boolean showMessage) {
        if (showMessage) {
            mNoRecipesLayout.setVisibility(View.VISIBLE);
            mTryAgainButton.setEnabled(true);
        } else {
            mNoRecipesLayout.setVisibility(View.GONE);
            mTryAgainButton.setEnabled(false);
        }
    }

    /** Starts the intent service to sync the recipes */
    private void initializeSync() {
        Intent syncIntent = new Intent(this, SyncService.class);
        startService(syncIntent);
    }

    /** Instantiates and returns a loader querying for the recipe names */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case RECIPE_LOADER_ID:
                return new CursorLoader(this,
                        RecipesEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader " + loaderId + " not implemented");
        }
    }

    /** The loader has finished retrieving the data. Pass the cursor to the adapter */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case RECIPE_LOADER_ID:
                if (data != null && data.getCount() != 0) {
                    // Hide the TextView & Button for no recipes
                    showNoRecipesMessage(false);
                    // Pass the cursor to the adapter
                    mListAdapter.swapCursor(data);
                } else {
                    // Show the TextView & Button for no recipes
                    showNoRecipesMessage(true);

                    // The cursor is empty. Clear the adapter
                    mListAdapter.swapCursor(null);
                }
                break;
            default:
                throw new RuntimeException("Loader " + loader.getId() + " not implemented");
        }
    }

    /** The loader is has been reset therefore swap the cursor for a null one */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // The cursor is now empty. Clear the adapter
        mListAdapter.swapCursor(null);
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }
}
