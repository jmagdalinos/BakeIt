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

package com.example.android.bakeit.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.android.bakeit.Data.DbContract.IngredientsEntry;
import com.example.android.bakeit.Data.DbContract.RecipesEntry;
import com.example.android.bakeit.Data.DbContract.StepsEntry;

/**
 * Content provider used to access the database
 */

public class RecipeProvider extends ContentProvider {

    /** Member variable for the MovieDbHelper initialized in onCreate */
    private static DbHelper mDbHelper;

    /** Constants used in the URI Matcher */
    private static final int CODE_RECIPES = 200;
    private static final int CODE_RECIPE_ITEM = 201;
    private static final int CODE_INGREDIENTS = 300;
    private static final int CODE_INGREDIENT_ITEM = 301;
    private static final int CODE_STEPS = 400;
    private static final int CODE_STEP_ITEM = 401;

    /** The URI Matcher used by this content provider */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /** Creates the URI Matcher that will match each URI to the appropriate code values */
    private static UriMatcher buildUriMatcher() {
        // Create the URI matcher and get the content authority
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        String contentAuthority = DbContract.CONTENT_AUTHORITY;

        // Add all the possible acceptable URIs with the corresponding codes
        uriMatcher.addURI(contentAuthority, DbContract.PATH_RECIPES, CODE_RECIPES);
        uriMatcher.addURI(contentAuthority, DbContract.PATH_RECIPES + "/#", CODE_RECIPE_ITEM);
        uriMatcher.addURI(contentAuthority, DbContract.PATH_INGREDIENTS, CODE_INGREDIENTS);
        uriMatcher.addURI(contentAuthority, DbContract.PATH_INGREDIENTS + "/#", CODE_INGREDIENT_ITEM);
        uriMatcher.addURI(contentAuthority, DbContract.PATH_STEPS, CODE_STEPS);
        uriMatcher.addURI(contentAuthority, DbContract.PATH_STEPS + "/#", CODE_STEP_ITEM);

        return uriMatcher;
    }

    /** InitializeS the mMovieDbHelper */
    @Override
    public boolean onCreate() {
        mDbHelper = new DbHelper(getContext());
        return true;
    }

    /** Returns a cursor with the queried rows */
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                // Returns a cursor that contains every row of data of the recipes table
                cursor = mDbHelper.getReadableDatabase().query(RecipesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_RECIPE_ITEM:
                // Get the recipe_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Returns a cursor that contains a single row of data of the recipes table
                cursor = mDbHelper.getReadableDatabase().query(RecipesEntry.TABLE_NAME,
                        projection,
                        RecipesEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            case CODE_INGREDIENTS:
                // Returns a cursor that contains every row of data of the ingredients table
                cursor = mDbHelper.getReadableDatabase().query(IngredientsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_INGREDIENT_ITEM:
                // Get the ingredient_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Returns a cursor that contains a single row of data of the ingredients table
                cursor = mDbHelper.getReadableDatabase().query(IngredientsEntry.TABLE_NAME,
                        projection,
                        IngredientsEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STEPS:
                // Returns a cursor that contains every row of data of the steps table
                cursor = mDbHelper.getReadableDatabase().query(StepsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_STEP_ITEM:
                // Get the step_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Returns a cursor that contains a single row of data of the steps table
                cursor = mDbHelper.getReadableDatabase().query(StepsEntry.TABLE_NAME,
                        projection,
                        StepsEntry._ID + "=?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Register to watch a content URI for changes.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    /** Returns the MIME type */
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                return RecipesEntry.CONTENT_LIST_TYPE;
            case CODE_RECIPE_ITEM:
                return RecipesEntry.CONTENT_ITEM_TYPE;
            case CODE_INGREDIENTS:
                return IngredientsEntry.CONTENT_LIST_TYPE;
            case CODE_INGREDIENT_ITEM:
                return IngredientsEntry.CONTENT_ITEM_TYPE;
            case CODE_STEPS:
                return StepsEntry.CONTENT_LIST_TYPE;
            case CODE_STEP_ITEM:
                return StepsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " +
                        sUriMatcher.match(uri));
        }
    }

    /** Inserts a single row and returns the uri of the inserted row */
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id;

        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                // Insert a row into the recipes table
                id = mDbHelper.getWritableDatabase().insert(RecipesEntry.TABLE_NAME,
                        null,
                        values);
                break;
            case CODE_INGREDIENTS:
                // Insert a row into the ingredients table
                id = mDbHelper.getWritableDatabase().insert(IngredientsEntry.TABLE_NAME,
                        null,
                        values);
                break;
            case CODE_STEPS:
                // Insert a row into the steps table
                id = mDbHelper.getWritableDatabase().insert(StepsEntry.TABLE_NAME,
                        null,
                        values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (id > 0) {
            // The operation was successful. Notify all listeners
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return a new URI with the id of the inserted row
        return ContentUris.withAppendedId(uri, id);
    }

    /** Inserts multiple rows and returns the number of inserted rows */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        // Get an instance of a writable database
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();

        int rowsInserted;
        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue : values) {
                        long recipe_id = db.insert(RecipesEntry.TABLE_NAME, null,
                                currentValue);

                        // If the operation was successful, increment the value of rowsInserted
                        if(recipe_id != -1) rowsInserted ++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of inserted rows
                return rowsInserted;
            case CODE_INGREDIENTS:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue : values) {
                        long ingredient_id = db.insert(IngredientsEntry.TABLE_NAME, null,
                                currentValue);

                        // If the operation was successful, increment the value of rowsInserted
                        if(ingredient_id != -1) rowsInserted ++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of inserted rows
                return rowsInserted;
            case CODE_STEPS:
                // Begin transaction with the database
                db.beginTransaction();
                rowsInserted = 0;

                try {
                    // Iterate through all content values and insert them one by one
                    for (ContentValues currentValue : values) {
                        long step_id = db.insert(StepsEntry.TABLE_NAME, null, currentValue);

                        // If the operation was successful, increment the value of rowsInserted
                        if(step_id != -1) rowsInserted ++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    // End the transaction
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    // The operation was successful. Notify all listeners
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                // Return the number of inserted rows
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    /** Deletes one or more rows and returns number of deleted rows */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                // Delete the rows from the recipes table
                rowsDeleted = mDbHelper.getWritableDatabase().delete(RecipesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_RECIPE_ITEM:
                // Get the recipe_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete a single row from the recipes table
                rowsDeleted = mDbHelper.getWritableDatabase().delete(RecipesEntry.TABLE_NAME,
                        RecipesEntry._ID + "=?",
                        selectionArgs);
                break;
            case CODE_INGREDIENTS:
                // Delete the rows from the ingredients table
                rowsDeleted = mDbHelper.getWritableDatabase().delete(IngredientsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_INGREDIENT_ITEM:
                // Get the ingredient_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete a single row from the ingredients table
                rowsDeleted = mDbHelper.getWritableDatabase().delete(IngredientsEntry.TABLE_NAME,
                        IngredientsEntry._ID + "=?",
                        selectionArgs);
                break;
            case CODE_STEPS:
                // Delete the rows from the steps table
                rowsDeleted = mDbHelper.getWritableDatabase().delete(StepsEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_STEP_ITEM:
                // Get the step_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Delete a single row from the steps table
                rowsDeleted = mDbHelper.getWritableDatabase().delete(StepsEntry.TABLE_NAME,
                        StepsEntry._ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted > 0) {
            // The operation was successful. Notify all listeners
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of deleted rows
        return rowsDeleted;
    }

    /** Updates one or more rows and returns number of updated rows */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsUpdated;

        switch (sUriMatcher.match(uri)) {
            case CODE_RECIPES:
                // Update the rows of the recipes table
                rowsUpdated = mDbHelper.getWritableDatabase().update(RecipesEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_RECIPE_ITEM:
                // Get the recipe_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row from the recipes table
                rowsUpdated = mDbHelper.getWritableDatabase().update(RecipesEntry.TABLE_NAME,
                        values,
                        RecipesEntry._ID + "=?",
                        selectionArgs);
                break;
            case CODE_INGREDIENTS:
                // Update the rows of the ingredients table
                rowsUpdated = mDbHelper.getWritableDatabase().update(IngredientsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_INGREDIENT_ITEM:
                // Get the ingredient_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row from the ingredients table
                rowsUpdated = mDbHelper.getWritableDatabase().update(IngredientsEntry.TABLE_NAME,
                        values,
                        IngredientsEntry._ID + "=?",
                        selectionArgs);
                break;
            case CODE_STEPS:
                // Update the rows of the steps table
                rowsUpdated = mDbHelper.getWritableDatabase().update(StepsEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CODE_STEP_ITEM:
                // Get the step_id from the URI
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Update a single row from the steps table
                rowsUpdated = mDbHelper.getWritableDatabase().update(StepsEntry.TABLE_NAME,
                        values,
                        StepsEntry._ID + "=?",
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated > 0) {
            // The operation was successful. Notify all listeners
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of updated rows
        return rowsUpdated;
    }
}
