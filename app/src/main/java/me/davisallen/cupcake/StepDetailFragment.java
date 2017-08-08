package me.davisallen.cupcake;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.davisallen.cupcake.pojo.Step;


public class StepDetailFragment extends Fragment {

    private static final String LOG_TAG = StepDetailFragment.class.getSimpleName();
    @BindView(R.id.player_view) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.short_description_text_view) TextView mShortDescriptionTextView;
    @BindView(R.id.description_text_view) TextView mDescriptionTextView;
    @BindView(R.id.button_previous) Button mButtonPrevious;
    @BindView(R.id.button_next) Button mButtonNext;
    private Unbinder unbinder;

    private static final String STEP_PARAM = "step";

    private Step mStep;
    private NavButtonListener mListener;
    private Context mContext;
    private int mCurrentPosition;
    private int mNumberOfSteps;

    private SimpleExoPlayer mPlayer;
    private MediaSource mVideoSource;

    public interface NavButtonListener {
        void onButtonClick(int clickedItemId, int position, int numberOfSteps);
    }

    public StepDetailFragment() {
        // Required empty public constructor
    }

    public static StepDetailFragment newInstance(Step step,
                                                 NavButtonListener listener,
                                                 int position,
                                                 int numberOfSteps) {
        StepDetailFragment fragment = new StepDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(STEP_PARAM, step);
        fragment.setArguments(args, listener, position, numberOfSteps);
        return fragment;
    }

    public void setArguments(Bundle args,
                             NavButtonListener listener,
                             int position,
                             int numberOfSteps) {
        setArguments(args);
        mListener = listener;
        mCurrentPosition = position;
        mNumberOfSteps = numberOfSteps;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStep = getArguments().getParcelable(STEP_PARAM);
        }
        mContext = getContext();
        setRetainInstance(true);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Video Uri
        // We have to look at videoURL and thumbnailURL because the JSON entries are nonuniform
        // and some have only a videoURL and others have only a thumbnailURL (exclusive) which both
        // URLs point to videos.

        Uri videoUri = mStep.getVideoOrThumbnailUri();
        if (videoUri != null) {
            initializePlayer();                 // initialize SimpleExoPlayer
            initializeMediaSource(videoUri);    // initialize MediaSource
            startVideo();                       // start playing video

            if (mContext.getResources().getBoolean(R.bool.isLandscape)) {
                showLandscapeView();
            } else {
                showPortraitView();
            }

        } else {
            mPlayerView.setVisibility(View.INVISIBLE);
        }

        mShortDescriptionTextView.setText(mStep.getShortDescription());
        mDescriptionTextView.setText(mStep.getDescription());

        // Add listeners for buttons
        if (mCurrentPosition < mNumberOfSteps-1) {
            mButtonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int buttonId = mButtonNext.getId();
                    mListener.onButtonClick(buttonId, mCurrentPosition, mNumberOfSteps);
                }
            });
        } else {
            mButtonNext.setVisibility(View.INVISIBLE);
        }

        if (mCurrentPosition >= 1) {
            mButtonPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int buttonId = mButtonPrevious.getId();
                    mListener.onButtonClick(buttonId, mCurrentPosition, mNumberOfSteps);
                }
            });
        } else {
            mButtonPrevious.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean getAllowEnterTransitionOverlap() {
        return super.getAllowEnterTransitionOverlap();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlayer();
    }

    public void stopPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NavButtonListener) {
            mListener = (NavButtonListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mPlayer != null) {
            mPlayer.release();
            Log.d(LOG_TAG, "releasing mPlayer");
            mPlayer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initializePlayer() {
        if (mPlayer == null) {
            LoadControl loadControl = new DefaultLoadControl();
            TrackSelector trackSelector = new DefaultTrackSelector();
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        }
    }

    private void initializeMediaSource(Uri videoUri) {

        // dataSourceFactory (produces DataSource instances through which media data is loaded)
        String userAgent = Util.getUserAgent(mContext, getString(R.string.app_name));
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, userAgent);

        // extractorFactory
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // create the MediaSource object
        mVideoSource = new ExtractorMediaSource(
                videoUri,
                dataSourceFactory,
                extractorsFactory,
                null,
                null
        );
    }

    private void startVideo() {
        mPlayerView.setPlayer(mPlayer);
        mPlayer.prepare(mVideoSource);
        mPlayer.setPlayWhenReady(true);
    }

    private void showLandscapeView() {
        mButtonPrevious.setVisibility(View.GONE);
        mButtonNext.setVisibility(View.GONE);
        mShortDescriptionTextView.setVisibility(View.GONE);
        mDescriptionTextView.setVisibility(View.GONE);
    }

    private void showPortraitView() {
        mButtonPrevious.setVisibility(View.VISIBLE);
        mButtonNext.setVisibility(View.VISIBLE);
        mShortDescriptionTextView.setVisibility(View.VISIBLE);
        mDescriptionTextView.setVisibility(View.VISIBLE);
    }

}
