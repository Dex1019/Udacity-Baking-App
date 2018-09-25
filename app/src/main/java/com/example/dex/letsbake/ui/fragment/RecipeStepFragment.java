package com.example.dex.letsbake.ui.fragment;

import android.app.Dialog;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dex.letsbake.R;
import com.example.dex.letsbake.ui.adapter.RecipeStepPagerAdapter;
import com.example.dex.letsbake.utils.GlideApp;
import com.example.dex.letsbake.utils.NetworkUtils;
import com.example.dex.letsbake.utils.Utils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeStepFragment extends Fragment implements ExoPlayer.EventListener {

    public static final String KEY_PLAY_WHEN_READY = "playWhenReady";
    private static final String TAG = RecipeStepFragment.class.getSimpleName();
    private final String STATE_RESUME_WINDOW = "resumeWindow";
    private final String STATE_RESUME_POSITION = "resumePosition";
    private final String STATE_PLAYER_FULLSCREEN = "playerFullscreen";
    private final String STATE_IS_VISIBLE = "isVisible";
    @BindView(R.id.tv_recipe_step_desc)
    TextView desc;
    @BindView(R.id.media_frame)
    FrameLayout mMediaFrame;
    @BindView(R.id.ep_recipe_video)
    SimpleExoPlayerView mExoPlayerView;
    @BindView(R.id.iv_recipe_video_thumbnail)
    ImageView mImageView;
    @BindView(R.id.exo_fullscreen_button)
    ImageButton mFullScreenButton;

    private boolean isMediaAvailable = false;
    private SimpleExoPlayer mExoPlayer;
    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;
    private String videoUrl;
    private String videoThumbnail;
    private String description;
    private MediaSource mVideoSource;
    private int mResumeWindow;
    private long mResumePosition;
    private boolean fragmentIsVisible = false;
    private boolean playWhenReady;


    public RecipeStepFragment() {

    }

    public static RecipeStepFragment newInstance(Bundle args) {
        RecipeStepFragment f = new RecipeStepFragment();
        f.setArguments(args);
        f.onHiddenChanged(true);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        Bundle args = getArguments();
        if (args.containsKey(RecipeStepPagerAdapter.RECIPE_VIDEO_URL)) {
            videoUrl = args.getString(RecipeStepPagerAdapter.RECIPE_VIDEO_URL);
        } else if (args.containsKey(RecipeStepPagerAdapter.RECIPE_VIDEO_THUMBNAIL)) {
            videoThumbnail = args.getString(RecipeStepPagerAdapter.RECIPE_VIDEO_THUMBNAIL);
        }

        description = args.getString(RecipeStepPagerAdapter.RECIPE_DESCRIPTION);

        if (savedInstanceState != null) {
            mResumeWindow = savedInstanceState.getInt(STATE_RESUME_WINDOW);
            mResumePosition = savedInstanceState.getLong(STATE_RESUME_POSITION);
            mExoPlayerFullscreen = savedInstanceState.getBoolean(STATE_PLAYER_FULLSCREEN);
            fragmentIsVisible = savedInstanceState.getBoolean(STATE_IS_VISIBLE);
            playWhenReady = savedInstanceState.getBoolean(KEY_PLAY_WHEN_READY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe_step, container, false);
        ButterKnife.bind(this, rootView);

        if (videoUrl != null || videoThumbnail != null) {
            isMediaAvailable = true;
        }
        desc.setText(description);
        desc.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isMediaAvailable) {

            if (videoUrl != null) {
                if (Utils.isVideo(videoUrl)) {
                    prepareVideo();
                } else if (Utils.isImage(videoUrl)) {
                    videoThumbnail = videoUrl;
                    prepareImage();
                }

            } else if (videoThumbnail != null) {
                if (Utils.isImage(videoThumbnail)) {
                    prepareImage();
                } else if (Utils.isVideo(videoThumbnail)) {
                    videoUrl = videoThumbnail;
                    prepareVideo();
                }
            }
            mMediaFrame.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putInt(STATE_RESUME_WINDOW, mResumeWindow);
        outState.putLong(STATE_RESUME_POSITION, mResumePosition);
        outState.putBoolean(STATE_PLAYER_FULLSCREEN, mExoPlayerFullscreen);
        outState.putBoolean(STATE_IS_VISIBLE, fragmentIsVisible);
        outState.putBoolean(KEY_PLAY_WHEN_READY, playWhenReady);

        super.onSaveInstanceState(outState);
    }

    private void prepareImage() {
        mImageView.setVisibility(View.VISIBLE);

        if (NetworkUtils.getNetworkInfo(getContext()) != NetworkUtils.TYPE_WIFI)
            videoThumbnail = null;

        GlideApp.with(this)
                .load(videoThumbnail)
                .fitCenter()
                .centerCrop()
                .placeholder(R.drawable.logo)
                .fallback(R.drawable.logo)
                .into(mImageView);
    }

    private void prepareVideo() {
        mExoPlayerView.setVisibility(View.VISIBLE);

        initFullscreenDialog();
        initFullscreenButton();

        Uri mp4VideoUri = Uri.parse(videoUrl);
        String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
        DefaultDataSourceFactory dataSourceFactory =
                new DefaultDataSourceFactory(getContext(), userAgent);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        mVideoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory,
                extractorsFactory, null, null);

        initializePlayer();

        if (mExoPlayerFullscreen) {
            ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
            mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mFullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_exit));
            mFullScreenDialog.show();
        }

        Resources res = getActivity().getResources();
        if (res.getConfiguration().orientation == 2 && !res.getBoolean(R.bool.is_tablet) && fragmentIsVisible) {
            openFullscreenDialog();
        } else {
            closeFullscreenDialog();
        }

    }

    private void initializePlayer() {

        if (mExoPlayer == null) {
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(getContext()), trackSelector, loadControl);
            mExoPlayerView.setPlayer(mExoPlayer);

            mExoPlayer.addListener(this);

            if (NetworkUtils.getNetworkInfo(getContext()) == NetworkUtils.TYPE_WIFI)
                mExoPlayer.prepare(mVideoSource);
            mExoPlayer.setPlayWhenReady(false);

            boolean haveResumePosition = mResumeWindow != C.INDEX_UNSET;

            if (haveResumePosition && mResumePosition > 0) {
                mExoPlayer.seekTo(mResumeWindow, mResumePosition);
                mExoPlayer.setPlayWhenReady(false);
            }
        }
    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    private void initFullscreenDialog() {
        mFullScreenDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            @Override
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        Log.i(TAG, "openFullscreenDialog called");
        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mFullScreenDialog.addContentView(mExoPlayerView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mFullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen_exit));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) mExoPlayerView.getParent()).removeView(mExoPlayerView);
        mMediaFrame.addView(mExoPlayerView);

        mFullScreenButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_fullscreen));
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();

    }

    private void initFullscreenButton() {
        mFullScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayerView != null && mExoPlayer != null) {
            mResumeWindow = mExoPlayer.getCurrentWindowIndex();
            mResumePosition = Math.max(0, mExoPlayer.getCurrentPosition());
            playWhenReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }

        if (mFullScreenDialog != null)
            mFullScreenDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            mExoPlayer.seekTo(0);
            mExoPlayer.setPlayWhenReady(false);
        }

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    public void fragmentIsVisible(boolean isVisible) {
        if (!isVisible && mExoPlayer != null) {
            fragmentIsVisible = false;

            mExoPlayer.seekTo(0);
            mExoPlayer.setPlayWhenReady(false);
        } else if (isVisible) {
            fragmentIsVisible = true;
        }

    }

}