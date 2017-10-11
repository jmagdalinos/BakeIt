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

package com.example.android.bakeit.UI.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakeit.Data.DbContract.IngredientsEntry;
import com.example.android.bakeit.R;
import com.example.android.bakeit.Utilities.DataUtilities;

/**
 * Custom RecyclerView Adapter that loads the RecyclerView with a list of ingredients
 */

public class IngredientListAdapter extends RecyclerView.Adapter<IngredientListAdapter
        .IngredientViewHolder> {

    /** Cursor with all the steps */
    private Cursor mCursor;

    /** Views used by the adapter */
    private TextView mIngredientTextView;

    /** Constructor for the adapter */
    public IngredientListAdapter() {
    }

    /** Creates the custom ViewHolder */
    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the step_list_item as a layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ingredient_list_item, viewGroup, false);

        // Return a new instance of the ViewHolder
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        if (mCursor != null && mCursor.getCount() != 0) {
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

            // Set the ingredient summary
            mIngredientTextView.setText(DataUtilities.createIngredientSummary(name, quantity, measure));
        }
    }

    /** Returns the number of ingredients in the adapter */
    @Override
    public int getItemCount() {
        // Check if the list of steps is null
        if (mCursor == null) return 0;

        return mCursor.getCount();
    }

    /** The following method is overwritten to avoid duplication of views */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /** The following method is overwritten to avoid duplication of views */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /** Custom ViewHolder class */
    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        /**
         * Constructor for the viewholder
         *
         * @param itemView the view using the step_list_item as a layout
         */
        public IngredientViewHolder(View itemView) {
            super(itemView);
            // Find the TextView for the ingredient
            mIngredientTextView = (TextView) itemView.findViewById(R.id
                    .tv_ingredient_list_subTitle);
        }
    }

    /** Swaps the cursor with one with fresher data */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
