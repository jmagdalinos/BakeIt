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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Java object representing a single recipe step.
 */

public class Step implements Parcelable {

    /** Member variables for each step */
    private final String mShortDescription;
    private final String mDescription;
    private final String mVideoURL;
    private final String mThumbnailURL;

    /**
     * Public constructor for the object
     * @param shortDescription the short description of the step
     * @param description the full description of the step
     * @param videoURL the video URL for the step
     * @param thumbnailURL the thumbnail for the step
     */
    public Step(String shortDescription, String description, String videoURL,
                String thumbnailURL) {
        mShortDescription = shortDescription;
        mDescription = description;
        mVideoURL = videoURL;
        mThumbnailURL = thumbnailURL;
    }

    /**
     * Constructor for the object using a parcel
     */
    public Step(Parcel parcel) {
        mShortDescription = parcel.readString();
        mDescription = parcel.readString();
        mVideoURL = parcel.readString();
        mThumbnailURL = parcel.readString();
    }

    /** Getter methods */
    public String getmShortDescription() {
        return mShortDescription;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmVideoURL() {
        return mVideoURL;
    }

    public String getmThumbnailURL() {
        return mThumbnailURL;
    }


    /** Required by the implementation of Parcelable */
    @Override
    public int describeContents() {
        return 0;
    }

    /** Write all object properties to the parcel */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mShortDescription);
        dest.writeString(mDescription);
        dest.writeString(mVideoURL);
        dest.writeString(mThumbnailURL);
    }

    /** Required by the implementation of Parcelable */
    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel source) {
            return new Step(source);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
