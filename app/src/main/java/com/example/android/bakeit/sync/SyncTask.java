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

package com.example.android.bakeit.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.example.android.bakeit.data.DbContract.IngredientsEntry;
import com.example.android.bakeit.data.DbContract.RecipesEntry;
import com.example.android.bakeit.data.DbContract.StepsEntry;
import com.example.android.bakeit.utilities.DataUtilities;
import com.example.android.bakeit.utilities.JSONUtilities;
import com.example.android.bakeit.utilities.NetworkUtilities;

import java.util.ArrayList;

/**
 * Task run every time the app has to sync the data with the server and update the tables
 */

public class SyncTask {

    /** Retrieves the JSON file and inserts the data into the tables */
    public static void syncRecipes(Context context) {

        // Get the recipes
        ArrayList<ArrayList<ContentValues>> data = NetworkUtilities.getData();

        ContentValues[] recipes;
        ContentValues[] ingredients;
        ContentValues[] steps;

        // Check if the ArrayList is empty (e.g. due to internet error)
        if (data != null && data.size() != 0) {
            // The data is in 3 ArrayList<ContentValues>. We have to convert them to ContentValues[]
            // objects so the can be used in the ContentResolver's bulkInsert() method.
            recipes = DataUtilities.convertArrayListToContentValues(data.get(JSONUtilities.ID_RECIPES));
            ingredients = DataUtilities.convertArrayListToContentValues(data.get(JSONUtilities.ID_INGREDIENTS));
            steps = DataUtilities.convertArrayListToContentValues(data.get(JSONUtilities.ID_STEPS));

            // Table Uris for performing the delete/bulkInsert operation
            Uri recipesTableUri = RecipesEntry.CONTENT_URI;
            Uri ingredientsTableUri = IngredientsEntry.CONTENT_URI;
            Uri stepsTableUri = StepsEntry.CONTENT_URI;

            // Delete previous data from all tables and insert new one
            updateTable(context, recipesTableUri, recipes);
            updateTable(context, ingredientsTableUri, ingredients);
            updateTable(context, stepsTableUri, steps);
        }
    }

    /** Deletes the previous data from a table and inserts new one */
    private static void updateTable(Context context, Uri tableUri, ContentValues[] values) {
        // Get an instance of the content resolver
        ContentResolver resolver = context.getContentResolver();

        if (values != null && values.length != 0) {
            // Delete all old data
            resolver.delete(tableUri, null, null);

            // Insert new data
            resolver.bulkInsert(tableUri, values);
        }
    }
}