package com.example.android.bakeit;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakeit.UI.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Used to test the DetailsActivity's RecyclerView to make sure the correct short description
 * appears in the SingleStepFragment
 */

@RunWith(AndroidJUnit4.class)
public class DetailsActivityShortDescriptionTest {

    /** The activity to be tested is @MainActivity */
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new
            ActivityTestRule<>(MainActivity.class);

    /** Checks whether the correct short description appears in the SingleStepFragment after
     * clicking on an item in the RecyclerView*/
    @Test
    public void clickRecyclerViewItem() {
        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to the a specific
        // RecyclerView item in MainActivity and clicks it.
        onView(withId(R.id.rv_recipes_list)).perform(RecyclerViewActions.actionOnItemAtPosition
                (0, click()));

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to the a specific
        // RecyclerView item in DetailActivity and clicks it.
        onView(withId(R.id.rv_fragment_list_bottom)).perform(RecyclerViewActions.actionOnItemAtPosition
                        (0, click()));

        // Checks whether the "Recipe Introduction" is displayed
        onView(withId(R.id.tv_single_step_short_desc)).check(ViewAssertions.matches(withText
                ("Recipe Introduction")));

    }
}
