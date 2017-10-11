package com.example.android.bakeit;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakeit.UI.MainActivity;
import com.example.android.bakeit.Utilities.DataUtilities;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Used to test the MainActivity's RecyclerView to make sure the correct recipe is passed to the
 * DetailActivity
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentsTest {

    /** The activity to be tested is @MainActivity */
    @Rule
    public IntentsTestRule<MainActivity> mActivityTestRule = new IntentsTestRule<>
            (MainActivity.class);

    /** Checks whether the correct recipe appears is passed to the DetailView after clicking on an
     * item in the RecyclerView*/
    @Test
    public void clickRecyclerViewItem_PassesTitle() {
        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // RecyclerView item and clicks it.
        onView(withId(R.id.rv_recipes_list)).perform(RecyclerViewActions.actionOnItemAtPosition
                        (0, click()));

        // Check that the extra passed to the DetailActivity matches the correct name
        Intents.intended(Matchers.allOf(IntentMatchers.hasExtra(DataUtilities.KEY_RECIPE_NAME, "Nutella Pie")));
    }
}
