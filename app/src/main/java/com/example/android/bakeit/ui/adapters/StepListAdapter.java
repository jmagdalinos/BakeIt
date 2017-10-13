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

package com.example.android.bakeit.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeit.R;
import com.example.android.bakeit.data.DbContract.StepsEntry;
import com.example.android.bakeit.data.Step;
import com.example.android.bakeit.utilities.DataUtilities;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Custom RecyclerView Adapter that loads the RecyclerView with a list of steps
 */

public class StepListAdapter extends RecyclerView.Adapter<StepListAdapter.StepViewHolder> {

    /** Cursor with all the steps */
    private Cursor mCursor;

    /** Context */
    private Context mContext;

    /** Views used by the adapter */
    private TextView mShortDescriptionTextView;
    private ImageView mVideoImageView;
    private ImageView mThumbImageView;

    /** Variable storing the previous clicked position */
    private int mOldPosition = -1;

    /** Instance of IngredientClickHandler to handle user clicks on an item in the RecyclerView */
    private StepClickHandler mClickHandler;

    /** Constructor for the adapter */
    public StepListAdapter(Context context, StepClickHandler clickHandler) {
        mClickHandler = clickHandler;
        mContext = context;
    }

    /** Interface to enable click functionality to the recycler view */
    public interface StepClickHandler {
        void onItemClick(ArrayList<Step> steps, int newPosition, int oldPosition);
    }

    /** Creates the custom ViewHolder */
    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Get the context from the viewGroup view
        Context context = viewGroup.getContext();
        // Inflate the view using the step_list_item as a layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.step_list_item, viewGroup, false);

        // Return a new instance of the ViewHolder
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        // Move the cursor to the current position
        mCursor.moveToPosition(position);

        // Get the columnIndexes
        int shortDescColumnIndex = mCursor.getColumnIndex(StepsEntry.COLUMN_SHORT_DESCRIPTION);
        int videoColumnIndex = mCursor.getColumnIndex(StepsEntry.COLUMN_VIDEO);
        int thumbColumnIndex = mCursor.getColumnIndex(StepsEntry.COLUMN_THUMBNAIL);

        // Set the short description
        mShortDescriptionTextView.setText(mCursor.getString(shortDescColumnIndex));

        // If there is a video, show the indicator
        String videoURL = mCursor.getString(videoColumnIndex);
        String thumbURL = mCursor.getString(thumbColumnIndex);

        if (videoURL != null && !TextUtils.isEmpty(videoURL)) {
            mVideoImageView.setVisibility(View.VISIBLE);
        }

        // Check if there is a thumbnail
        if (thumbURL != null && !TextUtils.isEmpty(thumbURL)) {
            mThumbImageView.setVisibility(View.VISIBLE);
            // Use Picasso (http://square.github.io/picasso/) to set the image
            Picasso.with(mContext).load(thumbURL).into(mThumbImageView);
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
    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Constructor for the viewholder
         * @param itemView the view using the step_list_item as a layout
         */
        public StepViewHolder(View itemView) {
            super(itemView);
            // Find the TextView for the short description
            mShortDescriptionTextView = (TextView) itemView.findViewById(R.id.tv_step_list_short_desc);

            // Find the ImageView indicating the existence of a video
            mVideoImageView = (ImageView) itemView.findViewById(R.id.iv_step_list_video);

            // find the ImageView for the thumbnail
            mThumbImageView = (ImageView) itemView.findViewById(R.id.iv_step_list_thumb);

            // Set the OnClickListener to the view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Move cursor to current position
            mCursor.moveToPosition(getAdapterPosition());

            // Pass the list of steps, the new position and the old one to the StepClickHandler
            mClickHandler.onItemClick(DataUtilities.convertCursorToArrayList(mCursor),
                    getAdapterPosition(), mOldPosition);

            // Replace the value for the old position with the new position
            mOldPosition = getAdapterPosition();
        }
    }

    /** Swaps the cursor with one with fresher data */
    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }
}