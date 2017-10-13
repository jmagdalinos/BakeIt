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

package com.example.android.bakeit.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bakeit.R;
import com.example.android.bakeit.data.Step;
import com.example.android.bakeit.ui.fragments.SingleStepFragment;
import com.example.android.bakeit.utilities.DataUtilities;

import java.util.ArrayList;

/**
 * Loads the fragment showing the selected step (Video and text)
 */
public class PhoneStepActivity extends AppCompatActivity {

    /** Member variables */
    private FragmentManager mFragmentManager;
    private SingleStepFragment mSingleStepFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonestep);

        // Get the extras from the intent
        String recipeName = getIntent().getStringExtra(DataUtilities.KEY_RECIPE_NAME);
        int position = getIntent().getIntExtra(DataUtilities.KEY_STEP_ID, -1);
        ArrayList<Step> steps = getIntent().getParcelableArrayListExtra(DataUtilities.KEY_STEPS);
        boolean isTwoPane = getIntent().getBooleanExtra(DataUtilities.KEY_IS_TWO_PANE, false);

        // Get the custom font
        Typeface mFont = Typeface.createFromAsset(getAssets(), "fonts/courgette_regular.ttf");

        // Get the toolbar and its TextView
        Toolbar toolbar = (Toolbar) findViewById(R.id.appBar);
        TextView textView = (TextView) toolbar.findViewById(R.id.tv_toolbar);
        // Setup back navigation
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set the text and typeface of the toolbar
        textView.setText(recipeName);
        textView.setTypeface(mFont);

        // Get the support fragment manager
        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState != null) {
            // If this is a saved instance, retrieve the previous fragment
            mSingleStepFragment = (SingleStepFragment) mFragmentManager.getFragment(savedInstanceState,
                    DataUtilities.KEY_FRAGMENT);
        } else {
            // Create a new instance of the StepDetailsFragment
            mSingleStepFragment = new SingleStepFragment();

            // Create a Bundle and put inside the List of Steps and the current position
            Bundle stepsBundle = new Bundle();
            stepsBundle.putParcelableArrayList(DataUtilities.KEY_STEPS, steps);
            stepsBundle.putInt(DataUtilities.KEY_STEP_ID, position);
            stepsBundle.putBoolean(DataUtilities.KEY_IS_TWO_PANE, isTwoPane);

            // Set the arguments of the Fragment using the Bundle
            mSingleStepFragment.setArguments(stepsBundle);

            // Associate the Fragment with the frame Layout
            mFragmentManager.beginTransaction()
                    .replace(R.id.fl_fragment_main, mSingleStepFragment)
                    // Don't add the fragment to the back stash
                    .commit();
        }
    }

    /** Save an instance of the current fragment */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the fragment instance
        mFragmentManager.putFragment(outState, DataUtilities.KEY_FRAGMENT, mSingleStepFragment);
    }

    /** Setup action bar back button navigation */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}