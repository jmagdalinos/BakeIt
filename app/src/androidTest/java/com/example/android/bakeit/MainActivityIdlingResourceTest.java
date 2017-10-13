package com.example.android.bakeit;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakeit.ui.MainActivity;
import com.example.android.bakeit.utilities.DataUtilities;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Uses an Idling Resource to simulate an operation that would happen on a different thread
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityIdlingResourceTest {
    private IdlingResource mIdlingResource;

    /** The activity to be tested is @MainActivity */
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>
            (MainActivity.class);

    /** Register the idling resource before the test is run */
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        // To prove that the test fails, omit this call:
        Espresso.registerIdlingResources(mIdlingResource);
    }

    /**
     * Run the test
     */
    @Test
    public void clickRecyclerViewItem_PassesTitle() {
        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // RecyclerView item and clicks it.
        onView(withId(R.id.rv_recipes_list)).perform(RecyclerViewActions.actionOnItemAtPosition
                (0, click()));

        // Check that the extra passed to the DetailActivity matches the correct name
        Intents.intended(Matchers.allOf(IntentMatchers.hasExtra(DataUtilities.KEY_RECIPE_NAME, "Nutella Pie")));
    }

    /**
     * Unregister the idling resource after the test is run
     */
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
