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

package com.example.android.bakeit.ui.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.android.bakeit.data.DbContract.RecipesEntry;
import com.example.android.bakeit.R;
import com.example.android.bakeit.ui.adapters.RecipeListAdapter;

/**
 * The configuration screen for the {@link RecipeIngredientsWidget RecipeIngredientsWidget} AppWidget.
 */
public class RecipeIngredientsWidgetConfigureActivity extends AppCompatActivity implements
        RecipeListAdapter.RecipeClickHandler,
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Preference file name and key */
    private static final String PREFS_NAME = "com.example.android.bakeit.UI.Widget.RecipeIngredientsWidget";
    private static final String PREF_NAME_PREFIX_KEY = "name_appwidget_";
    private static final String PREF_SERVINGS_PREFIX_KEY = "servings_appwidget_";
    private static final String PREF_IMAGE_PREFIX_KEY = "image_appwidget_";

    /** The Id of the widget */
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    /** Id of the cursor loader */
    private static final int RECIPE_LOADER_ID = 32;

    /** Member variables */
    private RecipeListAdapter mListAdapter;
    private TextView mMessageTextView;


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        // Set the content view using the recipe_ingredients_widget_configure.xml
        setContentView(R.layout.recipe_ingredients_widget_configure);

        // Find the RecyclerView & TextView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_appwidget_recipes_list);
        mMessageTextView = (TextView) findViewById(R.id.tv_appwidget_message);

        // Create a Linear layout manager and assign it to the recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // To improve performance, we make it so the child layout size in the RecyclerView does
        // not change
        mRecyclerView.setHasFixedSize(true);

        // Create a new instance of a RecipeListAdapter
        mListAdapter = new RecipeListAdapter(getApplicationContext(), this);

        // Set the adapter on the RecyclerView
        mRecyclerView.setAdapter(mListAdapter);

        // Initialize the loader
        getSupportLoaderManager().initLoader(RECIPE_LOADER_ID, null, this);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    /** Empty constructor */
    public RecipeIngredientsWidgetConfigureActivity() {
        super();
    }

    /** Write the recipe name to the SharedPreferences object for this widget */
    static void saveRecipePref(Context context, int appWidgetId, String name, String servings,
                               String image) {
        // Get an instance of the shared preferences editor to edit the file PREFS_NAME
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        // Store the recipe name in the shared preferences. The key includes the widget Id
        prefs.putString(PREF_NAME_PREFIX_KEY + appWidgetId, name);
        prefs.putString(PREF_SERVINGS_PREFIX_KEY + appWidgetId, servings);
        prefs.putString(PREF_IMAGE_PREFIX_KEY + appWidgetId, image);
        prefs.apply();
    }

    /**
     * Retrieve the recipe from the SharedPreferences object for this widget.
     * If there is no preference saved, get the default from a resource
     */
    static String loadRecipePref(Context context, int appWidgetId) {
        // Get an instance of the shared preferences for the file PREFS_NAME
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Get the recipe name
        String titleValue = prefs.getString(PREF_NAME_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_recipe_name);
        }
    }

    /**
     * Retrieve the servings from the SharedPreferences object for this widget.
     * If there is no preference saved, get the default from a resource
     */
    static String loadServingsPref(Context context, int appWidgetId) {
        // Get an instance of the shared preferences for the file PREFS_NAME
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Get the recipe name
        String titleValue = prefs.getString(PREF_SERVINGS_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_recipe_servings);
        }
    }

    /**
     * Retrieve the image url from the SharedPreferences object for this widget.
     * If there is no preference saved, get the default from a resource
     */
    static String loadImagePref(Context context, int appWidgetId) {
        // Get an instance of the shared preferences for the file PREFS_NAME
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        // Get the recipe name
        String titleValue = prefs.getString(PREF_IMAGE_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_recipe_image);
        }
    }

    /** Delete the recipe from the preferences */
    static void deleteRecipePref(Context context, int appWidgetId) {
        // Get an instance of the shared preferences editor to edit the file PREFS_NAME
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        // Delete the recipe name in the shared preferences
        prefs.remove(PREF_NAME_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_SERVINGS_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_IMAGE_PREFIX_KEY + appWidgetId);
        prefs.apply();
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

    /** The loader has finished retrieving the recipes. Pass the cursor to the adapter */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case RECIPE_LOADER_ID:
                if (data != null && data.getCount() != 0) {
                    // Display the correct message
                    mMessageTextView.setText(getApplicationContext().getString(R.string.appwidget_select_recipe));
                    // Pass the cursor to the adapter
                    mListAdapter.swapCursor(data);
                } else {
                    // Display the correct message
                    mMessageTextView.setText(getApplicationContext().getString(R.string.appwidget_no_recipes));
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
        switch (loader.getId()) {
            case RECIPE_LOADER_ID:
                // The cursor is now empty. Clear the adapter
                mListAdapter.swapCursor(null);
                break;
            default:
                break;
        }
    }

    /** Implementation of method to enable click functionality on an item */
    @Override
    public void onItemClick(String recipeName, String servings, String image) {
        // Save the context
        final Context context = RecipeIngredientsWidgetConfigureActivity.this;

        // Store the recipe name locally
        saveRecipePref(context, mAppWidgetId, recipeName, servings, image);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RecipeIngredientsWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}

