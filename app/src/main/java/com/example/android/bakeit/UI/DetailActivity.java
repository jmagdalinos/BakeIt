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
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeit.Data.Step;
import com.example.android.bakeit.R;
import com.example.android.bakeit.UI.Fragments.IngredientsFragment;
import com.example.android.bakeit.UI.Fragments.SingleStepFragment;
import com.example.android.bakeit.UI.Fragments.StepsFragment;
import com.example.android.bakeit.Utilities.DataUtilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Checks if the device is able to show two panes.
 * If so, it loads all the data into one screen using fragments.
 * If not, it loads the ingredients and steps into one screen and launches the
 * PhoneStepActivity to show the selected step
 */
public class DetailActivity extends AppCompatActivity implements StepsFragment
        .OnFragmentClickListener {

    /** Member variables */
    private String mRecipeName, mRecipeServings;
    private FragmentManager mFragmentManager;
    private static ArrayList<Step> mSteps;
    private int mPosition = -1;
    private boolean mIsSavedState = false;
    private boolean mIsTwoPane;

    /** Resource ids for the two fragments */
    private static final int ID_INGREDIENTS_FRAGMENT = R.id.fl_fragment_ingredients;
    private static final int ID_STEPS_FRAGMENT = R.id.fl_fragment_steps;
    private static final int ID_SINGLE_FRAGMENT = R.id.fl_fragment_single;

    /** Keys for the ArrayList of Steps and the position in saveInstanceState */
    private static final String STATE_STEPS = "steps";
    private static final String STATE_POSITION = "position";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Get the extras from the intent
        mRecipeName = getIntent().getStringExtra(DataUtilities.KEY_RECIPE_NAME);
        mRecipeServings = getIntent().getStringExtra(DataUtilities.KEY_RECIPE_SERVINGS);
        String mRecipeImage = getIntent().getStringExtra(DataUtilities.KEY_RECIPE_IMAGE);

        // Get the custom font
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/courgette_regular.ttf");

        // Check if the layout contains the detail fragment
        if (findViewById(ID_SINGLE_FRAGMENT) != null) {
            // There is a detail fragment; This is a tablet
            mIsTwoPane = true;
            // Use Picasso (http://square.github.io/picasso/) to set the image
            Picasso.with(this).load(mRecipeImage).into ((ImageView) findViewById(R.id.im_fragment_image));
        } else {
            // There is no detail fragment; This is a phone
            mIsTwoPane = false;
        }

        // Get the toolbar and its TextView
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        // Set the text and typeface of the toolbar
        textView.setText(mRecipeName);
        textView.setTypeface(mFont);

        // Check if this a restored instance of the Activity
        if (savedInstanceState == null) {
            // This is a new instance
            mIsSavedState = false;
        } else {
            // This is a restored instance; get the stored variables
            mSteps = savedInstanceState.getParcelableArrayList(STATE_STEPS);
            mPosition = savedInstanceState.getInt(STATE_POSITION);
            mIsSavedState = true;

        }

        // Get the support fragment manager
        mFragmentManager = getSupportFragmentManager();

        if (mIsTwoPane) {
            createTabletLayout();
        } else {
            createPhoneLayout();
        }
    }

    /** Saves the current state of the ArrayList, the position and the visibility of the
     * ingredients*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(STATE_STEPS, mSteps);
        outState.putInt(STATE_POSITION, mPosition);
        super.onSaveInstanceState(outState);
    }

    /** Creates a LinearLayout for the RecyclerView to be used on Phones */
    private void createPhoneLayout() {
        // Display the list fragment
        setupListFragment();
    }

    /** Creates a LinearLayout for the RecyclerView to be used on Tablets */
    private void createTabletLayout() {
        // Display the list fragment
        setupListFragment();

        // Display the step fragment
        setupStepFragment( mSteps, mPosition);
    }

    /** Setups fragments to show a summary of the ingredients and steps */
    private void setupListFragment() {
        // Get an instance of the two fragments
        IngredientsFragment mIngredientsFragment;
        StepsFragment mStepsFragment;

        // If this a new instance, create a new List Fragment
        if (!mIsSavedState) {
            // Create a new instance of the IngredientsFragment to display the
            // list of ingredients a new instance of the StepsFragment to display the steps
            mIngredientsFragment = new IngredientsFragment();
            mStepsFragment = new StepsFragment();

            // Create a Bundle and put inside the current recipe
            Bundle detailsBundle = new Bundle();
            detailsBundle.putString(DataUtilities.KEY_RECIPE_NAME, mRecipeName);
            detailsBundle.putString(DataUtilities.KEY_RECIPE_SERVINGS, mRecipeServings);
            detailsBundle.putBoolean(DataUtilities.KEY_IS_TWO_PANE, mIsTwoPane);

            // Set the arguments of the Fragment using the Bundle
            mIngredientsFragment.setArguments(detailsBundle);
            mStepsFragment.setArguments(detailsBundle);

            // Associate the Fragment with the frame Layout
            mFragmentManager.beginTransaction()
                    .replace(ID_INGREDIENTS_FRAGMENT, mIngredientsFragment)
                    .addToBackStack(null)
                    .commit();

            mFragmentManager.beginTransaction()
                    .replace(ID_STEPS_FRAGMENT, mStepsFragment)
                    .commit();
        }
    }

    /** Setups fragments to show a single step and its video */
    private void setupStepFragment(ArrayList<Step> steps, int position) {
        // Create a new instance of the StepDetailsFragment
        SingleStepFragment mSingleStepFragment = new SingleStepFragment();

        // Create a Bundle and put inside the List of Steps and the current position
        Bundle stepsBundle = new Bundle();
        stepsBundle.putParcelableArrayList(DataUtilities.KEY_STEPS, steps);
        stepsBundle.putInt(DataUtilities.KEY_STEP_ID, position);
        stepsBundle.putBoolean(DataUtilities.KEY_IS_TWO_PANE, mIsTwoPane);

        // Set the arguments of the Fragment using the Bundle
        mSingleStepFragment.setArguments(stepsBundle);

        // Associate the Fragment with the frame Layout
        mFragmentManager.beginTransaction()
                .replace(ID_SINGLE_FRAGMENT, mSingleStepFragment)
                // Don't add the fragment to the back stash
                .commit();
    }

    /** Implementation of interface to enable click handling */
    @Override
    public void onClick(ArrayList<Step> steps, int position) {
        // Store the ArrayList and the position
        mSteps = steps;
        mPosition = position;

        if (mIsTwoPane) {
            // Replace the step fragment with the one showing the step details
            setupStepFragment(steps, position);
        } else {
            Intent intent = new Intent(getApplicationContext(), PhoneStepActivity.class);
            intent.putExtra(DataUtilities.KEY_RECIPE_NAME, mRecipeName);
            intent.putExtra(DataUtilities.KEY_STEP_ID, mPosition);
            intent.putParcelableArrayListExtra(DataUtilities.KEY_STEPS, mSteps);
            intent.putExtra(DataUtilities.KEY_IS_TWO_PANE, mIsTwoPane);

            startActivity(intent);
        }
    }

    /** Setup Back Button functionality */
    @Override
    public void onBackPressed() {
        // Check the number of entries in the Back Stack
        if (mFragmentManager.getBackStackEntryCount() > 1) {
            // The Single Step Fragment is displayed; navigate to the List Fragment
            super.onBackPressed();

        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }
}