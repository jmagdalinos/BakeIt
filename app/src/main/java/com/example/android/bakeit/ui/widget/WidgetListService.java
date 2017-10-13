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

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakeit.data.DbContract.IngredientsEntry;
import com.example.android.bakeit.R;
import com.example.android.bakeit.utilities.DataUtilities;

/**
 * Used to populate the ListView of a widget with a list of ingredients
 */

public class WidgetListService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetListRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

/**
 * Similar to a ListAdapter, it creates a cursor and populates the ListView with a list of
 * ingredients
 */
class WidgetListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    /** Member Variables */
    private Context mContext;
    private Cursor mCursor;
    private String mCurrentRecipeName;

    /**
     * Constructor for the factory
     */
    public WidgetListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mCurrentRecipeName = intent.getDataString();
    }

    @Override
    public void onCreate() {

    }

    /** Runs a query for the ingredients using the recipe name as a selection argument */
    @Override
    public void onDataSetChanged() {
        // Get the uri from the ingredients table
        Uri ingredientsUri = IngredientsEntry.CONTENT_URI;

        // If the cursor exists, close it
        if (mCursor != null) mCursor.close();

        // Create a cursor with ingredient entries
        mCursor = mContext.getContentResolver().query(
                ingredientsUri,
                null,
                IngredientsEntry.COLUMN_RECIPE + "=?",
                new String[] {mCurrentRecipeName},
                null
        );
    }

    /** Closes the cursor onDestroy */
    @Override
    public void onDestroy() {
        if (mCursor != null) mCursor.close();
    }

    /** Returns the size of the list */
    @Override
    public int getCount() {
        if (mCursor == null) return 0;
        return mCursor.getCount();
    }

    /** Creates a single item in the list */
    @Override
    public RemoteViews getViewAt(int position) {
        // If the cursor is empty, return early
        if (mCursor == null || mCursor.getCount() == 0) return null;

        // Move the cursor to the current position
        mCursor.moveToPosition(position);

        // Get the columnIndexes
        int quantityColumnIndex = mCursor.getColumnIndex(IngredientsEntry.COLUMN_QUANTITY);
        int measureColumnIndex = mCursor.getColumnIndex(IngredientsEntry.COLUMN_MEASURE);
        int nameColumnIndex = mCursor.getColumnIndex(IngredientsEntry.COLUMN_NAME);

        // Get the name, quantity and measure for the current ingredient
        String name = mCursor.getString(nameColumnIndex);
        String quantity = mCursor.getString(quantityColumnIndex);
        String measure = mCursor.getString(measureColumnIndex);

        // Create a RemoteView using the ingredient_list_item as a template
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout
                .ingredient_list_item);

        // Set the ingredient summary
        views.setTextViewText(R.id.tv_ingredient_list_subTitle, DataUtilities.createIngredientSummary(name, quantity, measure));
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the ListView the same
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
