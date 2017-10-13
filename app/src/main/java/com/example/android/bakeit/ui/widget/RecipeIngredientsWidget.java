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

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.bakeit.R;
import com.example.android.bakeit.ui.DetailActivity;
import com.example.android.bakeit.utilities.DataUtilities;
import com.squareup.picasso.Picasso;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in
 * {@link RecipeIngredientsWidgetConfigureActivity RecipeIngredientsWidgetConfigureActivity}
 */
public class RecipeIngredientsWidget extends AppWidgetProvider {

    /** Updates a single widget */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Get the recipe data from the preferences
        String widgetRecipeName = RecipeIngredientsWidgetConfigureActivity.loadRecipePref(context,
                appWidgetId);
        String widgetRecipeServings = RecipeIngredientsWidgetConfigureActivity.loadServingsPref(context,
                appWidgetId);
        String widgetRecipeImage = RecipeIngredientsWidgetConfigureActivity.loadImagePref(context,
                appWidgetId);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);

        // Set the recipe name on the TextView
        views.setTextViewText(R.id.tv_appwidget_recipe_name, widgetRecipeName);

        // Set the recipe image on the ImageView using Picasso (http://square.github.io/picasso/)
        int[] appWidgetIds = new int[] {appWidgetId};
        Picasso.with(context).load(widgetRecipeImage).into(views,R.id.iv_appwidget_recipe_image,
                appWidgetIds);

        // Create an intent to the {@WidgetListService} to setup the list adapter
        Intent listIntent = new Intent(context, WidgetListService.class);
        // Set the data on the intent. If we use setExtra instead of setData, the intent will be
        // run only once. By using setData, we differentiate between the various intents, making
        // sure they all launch
        listIntent.setData(Uri.parse(String.valueOf(widgetRecipeName)));
        // Set the remote adapter on the ListView
        views.setRemoteAdapter(R.id.lv_appwidget_ingredients, listIntent);

        // Set the intent to open the current recipe in the DetailActivity
        Intent recipeIntent = new Intent(context, DetailActivity.class);
        // Put the recipe name to the intent as an extra
        recipeIntent.putExtra(DataUtilities.KEY_RECIPE_NAME, widgetRecipeName);
        recipeIntent.putExtra(DataUtilities.KEY_RECIPE_SERVINGS, widgetRecipeServings);
        recipeIntent.putExtra(DataUtilities.KEY_RECIPE_IMAGE, widgetRecipeImage);

        // Create the pending Intent
        PendingIntent recipePendingIntent = PendingIntent.getActivity(context, 0, recipeIntent, PendingIntent
                .FLAG_UPDATE_CURRENT);

        // Set the pending intent on the recipe image
        views.setOnClickPendingIntent(R.id.iv_appwidget_recipe_image, recipePendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /** Updates all widgets */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    /** Deletes the preferences along with the widgets */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            RecipeIngredientsWidgetConfigureActivity.deleteRecipePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
