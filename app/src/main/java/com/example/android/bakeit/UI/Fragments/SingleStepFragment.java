/*
* Copyright (C) 2017 John Magdalinos
* Copyright 2017 The Android Open Source Project, Inc.
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

package com.example.android.bakeit.UI.Fragments;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.bakeit.Data.Step;
import com.example.android.bakeit.R;
import com.example.android.bakeit.Utilities.DataUtilities;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Uses @ExoPlayer (https://github.com/google/ExoPlayer) to stream video files
 * It also switches to Immersive mode if there is a video and the device is a phone in landscape
 * orientation.
 */

public class SingleStepFragment extends Fragment implements
        ExoPlayer.EventListener {

    /** Member Variables */
    @BindView(R.id.player_view) SimpleExoPlayerView mExoPlayerView;
    @BindView(R.id.tv_single_step_short_desc) TextView mShortDescTextView;
    @BindView(R.id.tv_single_step_desc) TextView mDescTextView;
    @BindView(R.id.iv_previous) ImageView mPreviousBtn;
    @BindView(R.id.iv_next) ImageView mNextBtn;
    private SimpleExoPlayer mExoPlayer;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private ArrayList<Step> mSteps;
    private int mCurrentPosition;
    private String mDesc, mShortDesc, mVideoUrlString;
    private int mScreenOrientation;
    private boolean mIsTwoPane;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    /** String for logging errors */
    private static final String LOG_TAG = SingleStepFragment.class.getSimpleName();

    /** Resource ids for the two fragments */
    private static final String STATE_CURRENT_POSITION = "current_position";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the root view by using the appropriate xml
        View view = inflater.inflate(R.layout.fragment_single_step, container, false);

        // Get the orientation of the screen
        mScreenOrientation = getResources().getConfiguration().orientation;

        // Uses @ButterKnife (http://jakewharton.github.io/butterknife/)
        ButterKnife.bind(this, view);

        // Get the typeface and set it on the ShortDescTextView
        Typeface font = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/courgette_regular.ttf");
        mShortDescTextView.setTypeface(font);

        // Get the bundle from the fragment arguments
        Bundle bundle = getArguments();

        // Get the ArrayList of Steps from the bundle
        mSteps = bundle.getParcelableArrayList(DataUtilities.KEY_STEPS);

        // Check if this is a two-pane layout
        mIsTwoPane = bundle.getBoolean(DataUtilities.KEY_IS_TWO_PANE);

        // Get the current position
        if (savedInstanceState == null) {
            // Retrieve the current position from the bundle
            mCurrentPosition = bundle.getInt(DataUtilities.KEY_STEP_ID);
        } else {
            // Retrieve the current position from the saved instance
            mCurrentPosition = savedInstanceState.getInt(STATE_CURRENT_POSITION);
        }

        // If there is a step selected, continue with the setup
        if (mCurrentPosition != -1 && mSteps != null && mSteps.size() != 0) {
            // Get the data from the ArrayList
            getStepData();

            // Show all data
            refreshView();

            // Initialize Media session
            initializeMediaSession();
        } else {
            // No step selected, show message
            mShortDescTextView.setText(getString(R.string.please_select_step));
            mShortDescTextView.setVisibility(View.VISIBLE);
            mExoPlayerView.setVisibility(View.GONE);
        }

        // Return the root view
        return view;
    }

    /** Saves the current position */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_POSITION, mCurrentPosition);
        super.onSaveInstanceState(outState);
    }

    /** Code for toggling Immersive mode on/off from Google Samples
     * (https://github.com/googlesamples/android-BasicImmersiveMode) */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final View decorView = getActivity().getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int i) {
                        int height = decorView.getHeight();
                    }
                });
    }

    /** Release the ExoPlayer and deactivate the media session when the activity finishes */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Show the toolbar
        getActivity().findViewById(R.id.appBar).setVisibility(View.VISIBLE);

        releasePlayer();
        if (mMediaSession != null) mMediaSession.setActive(false);
    }

    /** Get the description short description and video URL from the list of Steps */
    private void getStepData() {
        // Get the current Step and retrieve its data
        Step currentStep = mSteps.get(mCurrentPosition);
        // Get the description and remove its numbering if present
        mDesc = DataUtilities.removeNumbering(currentStep.getmDescription());
        // Get the short description
        mShortDesc = currentStep.getmShortDescription();
        // Get the video Url
        mVideoUrlString = currentStep.getmVideoURL();
    }

    /** Displays all data */
    private void refreshView() {
        // If this is position 0, don't display the description as it is the same with the short one
        if (mCurrentPosition != 0) {
            // Set the description on the TextView
            mDescTextView.setText(mDesc);
        }

        // Set the short description on the TextView
        mShortDescTextView.setText(mShortDesc);

        // If there is no video, don't display the Player View
        if (mVideoUrlString == null || TextUtils.isEmpty(mVideoUrlString)) {
            mExoPlayerView.setVisibility(View.GONE);
            // Display the descriptions
            mShortDescTextView.setVisibility(View.VISIBLE);
            mDescTextView.setVisibility(View.VISIBLE);

        } else {
            mExoPlayerView.setVisibility(View.VISIBLE);
            if (mScreenOrientation == Configuration.ORIENTATION_LANDSCAPE && !mIsTwoPane) {
                // If the phone's rotation is landscape, don't display the descriptions
                mDescTextView.setVisibility(View.INVISIBLE);
                mShortDescTextView.setVisibility(View.INVISIBLE);

                toggleFullscreen();
                // Hide the toolbar
                getActivity().findViewById(R.id.appBar).setVisibility(View.GONE);
            }

            // Initialize player
            initializeExoPlayer();
        }

        // Check if the step is the first or last
        if (mCurrentPosition == 0) {
            // First step; disable navigation to previous step
            mPreviousBtn.setVisibility(View.INVISIBLE);
            mPreviousBtn.setEnabled(false);

            // Enable navigation to next step
            mNextBtn.setVisibility(View.VISIBLE);
            mNextBtn.setEnabled(true);
        } else if (mCurrentPosition == (mSteps.size() - 1)) {
            // Last step; disable navigation to next step
            mNextBtn.setVisibility(View.INVISIBLE);
            mNextBtn.setEnabled(false);

            // Enable navigation to previous step
            mPreviousBtn.setVisibility(View.VISIBLE);
            mPreviousBtn.setEnabled(true);
        } else {
            // Step between first and last; enable all navigation
            mPreviousBtn.setVisibility(View.VISIBLE);
            mPreviousBtn.setEnabled(true);
            mNextBtn.setVisibility(View.VISIBLE);
            mNextBtn.setEnabled(true);
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        // as well as the available actions.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_STOP |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);

        // Set the state on the session
        mMediaSession.setPlaybackState(mStateBuilder.build());

        // Use class MySessionCallback with methods that handle callbacks from a media
        // controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);
    }

    /** Initializes the ExoPlayer */
    private void initializeExoPlayer() {
        // If there is no video, return early
        if (mVideoUrlString == null || TextUtils.isEmpty(mVideoUrlString)) return;

        // Parse the String to a uri
        Uri mediaUri = Uri.parse(mVideoUrlString);

        // Check if there is an instance of ExoPlayer
        if (mExoPlayer == null) {
            // Create default track selector and load control to be used in the player
            TrackSelection.Factory adaptiveTrackSelectionFactory = new
                    AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER);
            TrackSelector trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();

            // Create an instance of the ExoPlayer
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector,
                    loadControl);

            // Set the player to the player view
            mExoPlayerView.setPlayer(mExoPlayer);

            // Set the event listener
            mExoPlayer.addListener(this);

            // Prepare the media source
            String userAgent = Util.getUserAgent(getContext(), "BakeIt");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new
                    DefaultDataSourceFactory(getContext(), userAgent), new
                    DefaultExtractorsFactory(),null, null);

            // Set the media source to the player
            mExoPlayer.prepare(mediaSource);

            // Use setPlayWhenReady to set playback
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    /** Releases the ExoPlayer */
    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        mExoPlayer = null;
    }

    /** Implemented to enable previous button clicking */
    @OnClick(R.id.iv_previous)
    public void goToPreviousStep() {
        mCurrentPosition--;
        releasePlayer();
        getStepData();
        refreshView();
    }

    /** Implemented to enable next button clicking */
    @OnClick(R.id.iv_next)
    public void goToNextStep() {
        mCurrentPosition++;
        releasePlayer();
        getStepData();
        refreshView();
    }

    /**
     * ExoPlayer event Listeners
     */
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync, and post the media notification.
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     * STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_READY && playWhenReady) {
            // The player is playing
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING, mExoPlayer
                    .getCurrentPosition(), 1f);
        } else if (playbackState == ExoPlayer.STATE_READY) {
            // The player is paused
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED, mExoPlayer
                    .getCurrentPosition(), 1f);
        }

        // Set the state on the session
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /** Media Session Callbacks, where all external clients control the player. */
    private class MySessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onStop() {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.seekTo(0);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /** Toggles immersive mode on/off */
    public void toggleFullscreen() {
        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int uiOptions = getActivity().getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        // END_INCLUDE (get_current_ui_flags)

        // BEGIN_INCLUDE (toggle_ui_flags)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            boolean isImmersiveModeEnabled =
                    ((uiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == uiOptions);
        }

        // Immersive mode: Backward compatible to KitKat (API 19).
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // This sample uses the "sticky" form of immersive mode, which will let the user swipe
        // the bars back in again, but will automatically make them disappear a few seconds later.
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        }
        getActivity().getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }
}
