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

package com.example.android.bakeit.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contains the definitions for all tables
 */

public class DbContract {

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.bakeit";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider for Sunshine.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible paths that can be appended to BASE_CONTENT_URI to form valid URI's that the app
     * can handle.
     */
    public static final String PATH_RECIPES = "recipes";
    public static final String PATH_INGREDIENTS = "ingredients";
    public static final String PATH_STEPS = "steps";

    /**
     * Inner class that defines the table contents of the RecipesEntry table
     */
    public static class RecipesEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the RecipesEntry table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_RECIPES)
                .build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of recipes. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single recipe. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/";

        /** Name of the table */
        public static final String TABLE_NAME = "recipes";

        /** Column containing recipe name as TEXT */
        public static final String COLUMN_NAME = "name";

        /** Column containing recipe servings as TEXT */
        public static final String COLUMN_SERVINGS = "servings";

        /** Column containing recipe image as TEXT */
        public static final String COLUMN_IMAGE = "image";
    }

    /**
     * Inner class that defines the table contents of the IngredientsEntry table
     */
    public static class IngredientsEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the RecipesEntry table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_INGREDIENTS)
                .build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of ingredients. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single ingredient. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/";

        /** Name of the table */
        public static final String TABLE_NAME = "ingredients";

        /** Column containing the recipe the ingredient belongs to as TEXT */
        public static final String COLUMN_RECIPE = "recipe";

        /** Column containing ingredient name as TEXT */
        public static final String COLUMN_NAME = "name";

        /** Column containing ingredient quantity as TEXT */
        public static final String COLUMN_QUANTITY = "quantity";

        /** Column containing ingredient measure type as TEXT */
        public static final String COLUMN_MEASURE = "measure";
    }

    /**
     * Inner class that defines the table contents of the StepsEntry table
     */
    public static class StepsEntry implements BaseColumns {
        /** The base CONTENT_URI used to query the RecipesEntry table from the content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STEPS)
                .build();

        /** The MIME type of the {@link #CONTENT_URI} for a list of steps. */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/";

        /** The MIME type of the {@link #CONTENT_URI} for a single step. */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/";

        /** Name of the table */
        public static final String TABLE_NAME = "steps";

        /** Column containing the recipe the step belongs to as TEXT */
        public static final String COLUMN_RECIPE = "recipe";

        /** Column containing step's short description as TEXT */
        public static final String COLUMN_SHORT_DESCRIPTION = "short_description";

        /** Column containing step's description as TEXT */
        public static final String COLUMN_DESCRIPTION = "description";

        /** Column containing step's video URL as TEXT */
        public static final String COLUMN_VIDEO = "video_url";

        /** Column containing step's thumbnail's URL as TEXT */
        public static final String COLUMN_THUMBNAIL = "thumbnail_url";
    }

}
