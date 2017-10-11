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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bakeit.Data.DbContract.IngredientsEntry;
import com.example.android.bakeit.Data.DbContract.RecipesEntry;
import com.example.android.bakeit.Data.DbContract.StepsEntry;

/**
 * Helper class that creates the database and its tables
 */

public class DbHelper extends SQLiteOpenHelper {
    /** The name of the database */
    private static final String DATABASE_NAME = "recipes.db";

    /** The version of the database */
    private static final int DATABASE_VERSION = 1;

    /** Constructor for the helper object */
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement that creates the recipes table
        final String SQL_CREATE_RECIPES_TABLE = "CREATE TABLE " +
                RecipesEntry.TABLE_NAME + " (" +
                RecipesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RecipesEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RecipesEntry.COLUMN_SERVINGS + " TEXT NOT NULL, " +
                RecipesEntry.COLUMN_IMAGE + " TEXT);";

        // SQL statement that creates the recipes table
        final String SQL_CREATE_INGREDIENTS_TABLE = "CREATE TABLE " +
                IngredientsEntry.TABLE_NAME + " (" +
                IngredientsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IngredientsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                IngredientsEntry.COLUMN_QUANTITY + " TEXT NOT NULL, " +
                IngredientsEntry.COLUMN_MEASURE + " TEXT, " +
                IngredientsEntry.COLUMN_RECIPE + " TEXT NOT NULL);";

        // SQL statement that creates the recipes table
        final String SQL_CREATE_STEPS_TABLE = "CREATE TABLE " +
                StepsEntry.TABLE_NAME + " (" +
                StepsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StepsEntry.COLUMN_SHORT_DESCRIPTION + " TEXT NOT NULL, " +
                StepsEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                StepsEntry.COLUMN_VIDEO + " TEXT, " +
                StepsEntry.COLUMN_THUMBNAIL + " TEXT, " +
                StepsEntry.COLUMN_RECIPE + " TEXT NOT NULL);";

        // Create the tables
        db.execSQL(SQL_CREATE_RECIPES_TABLE);
        db.execSQL(SQL_CREATE_INGREDIENTS_TABLE);
        db.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    /** Called when upgrading the version */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +
                RecipesEntry.TABLE_NAME + ", " +
                IngredientsEntry.TABLE_NAME + ", " +
                StepsEntry.TABLE_NAME);
            onCreate(db);
        }
    }
}
