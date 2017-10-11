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
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeit.Data.DbContract.RecipesEntry;
import com.example.android.bakeit.R;
import com.squareup.picasso.Picasso;

/**
 * Custom RecyclerView Adapter that loads the RecyclerView with a list of recipes
 */

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.RecipeViewholder> {

    /** Member variables */
    private Cursor mCursor;
    private final Context mContext;
    private TextView mNameTextView;
    private ImageView mImageView;
    private static Typeface mFont;

    /** Instance of RecipeClickHandler to handle user clicks on an item in the RecyclerView */
    private final RecipeClickHandler mClickHandler;

    /** Constructor for the adapter */
    public RecipeListAdapter(Context context, RecipeClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        // Get the typeface
        mFont = Typeface.createFromAsset(mContext.getAssets(), "fonts/courgette_regular.ttf");
    }

    /** Interface to enable click functionality to the recycler view */
    public interface RecipeClickHandler {
        void onItemClick(String recipeName, String servings, String image);
    }

    /** Creates the custom ViewHolder */
    @Override
    public RecipeViewholder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the recipe_list_item as a layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_list_item, viewGroup, false);

        return new RecipeViewholder(view);
    }

    /** Sets the TextViews of the recipe */
    @Override
    public void onBindViewHolder(RecipeViewholder holder, int position) {
        // Move the cursor to the current position
        mCursor.moveToPosition(position);

        // Set the recipe name
        mNameTextView.setTypeface(mFont);
        mNameTextView.setText(mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_NAME)));

        // Get the image
        String image = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_IMAGE));

        // Use Picasso (http://square.github.io/picasso/) to set the image
        Picasso.with(mContext).load(image).into (mImageView);
    }

    /** Returns the number of recipes in the adapter */
    @Override
    public int getItemCount() {
        // Check if the list of recipes is null
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
    public class RecipeViewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Constructor for the viewholder
         * @param itemView the view using the recipe_list_item.xml as a layout
         */
        public RecipeViewholder(View itemView) {
            super(itemView);
            // Find the TextView for the recipe name
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_list_recipe_name);

            // Find the ImageView for the recipe image
            mImageView = (ImageView) itemView.findViewById(R.id.iv_list_recipe_image);

            // Set the OnClickListener to the view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Move the cursor to the current position and get all data
            mCursor.moveToPosition(getAdapterPosition());
            String name =  mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_NAME));
            String servings = mCursor.getString(mCursor.getColumnIndex(RecipesEntry
                    .COLUMN_SERVINGS));
            String image = mCursor.getString(mCursor.getColumnIndex(RecipesEntry.COLUMN_IMAGE));

            // Send all recipe data to the click handler
            mClickHandler.onItemClick(name, servings, image);
        }
    }

    /** Swaps the cursor with one with fresher data */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}
