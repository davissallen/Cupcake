package me.davisallen.cupcake;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.davisallen.cupcake.pojo.Step;


public class StepDetailFragment extends Fragment {

    private static final String LOG_TAG = StepDetailFragment.class.getSimpleName();
    private static final String PAUSED_POSITION = "paused_position";
    @BindView(R.id.media_container) FrameLayout mMediaContainer;
    @BindView(R.id.player_view) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.thumbnail_view) ImageView mThumnbailView;
    @BindView(R.id.short_description_text_view) TextView mShortDescriptionTextView;
    @BindView(R.id.description_text_view) TextView mDescriptionTextView;
    @BindView(R.id.button_previous) Button mButtonPrevious;
    @BindView(R.id.button_next) Button mButtonNext;
    private Unbinder unbinder;

    private static final String STEP_PARAM = "step";

    public Step mStep;
    private NavButtonListener mListener;
    private Context mContext;
    private int mCurrentPosition;
    private int mNumberOfSteps;
    private Uri mVideoUri;
    private Uri mThumbnailUri;
    private long mSeekTime;

    public SimpleExoPlayer mPlayer;

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
        // TODO (MAJOR): Refactor fragment to show a thumbnail image if it exists

        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStep = getArguments().getParcelable(STEP_PARAM);
        }

        if (mStep != null) {
            mVideoUri = mStep.getVideoUri();
            mThumbnailUri = mStep.getThumbnailUri();
        } else {
            mVideoUri = null;
            mThumbnailUri = null;
        }

        mSeekTime = 0;
        mContext = getContext();
        setRetainInstance(true);

        initializePlayer();
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

        if (mVideoUri != null && mVideoUri.toString().length() > 0) {
            showVideoHideThumbnail();

            // set player view to player
            mPlayerView.setPlayer(mPlayer);

            // if the device is in landscape and NOT a tablet, show vid full screen
            if (mContext.getResources().getBoolean(R.bool.isLandscape)) {
                if (!mContext.getResources().getBoolean(R.bool.isTablet)) {
                    showMediaFullScreen();
                }
            } else {
                showRegularView();
            }

        } else if (mThumbnailUri != null && mThumbnailUri.toString().length() > 0) {
            hideVideoShowThumbnail();
            Picasso.with(mContext).load(mThumbnailUri).into(mThumnbailView);
        } else {
            hideVideoShowThumbnail();
            showRegularView();
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(PAUSED_POSITION, mSeekTime);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mSeekTime = savedInstanceState.getLong(PAUSED_POSITION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mVideoUri != null) {
            initializePlayer();
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void initializePlayer() {
        if (mPlayer == null && mVideoUri != null) {
            LoadControl loadControl = new DefaultLoadControl();
            TrackSelector trackSelector = new DefaultTrackSelector();
            mPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);

            // dataSourceFactory (produces DataSource instances through which media data is loaded)
            String userAgent = Util.getUserAgent(mContext, getString(R.string.app_name));
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, userAgent);

            // extractorFactory
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // create the MediaSource object
            MediaSource mVideoSource = new ExtractorMediaSource(
                    mVideoUri,
                    dataSourceFactory,
                    extractorsFactory,
                    null,
                    null
            );

            mPlayer.prepare(mVideoSource);
            mPlayer.seekTo(mSeekTime);
        }

        if (mPlayer != null) {
            mPlayer.setPlayWhenReady(true);
        }
    }

    public void setPlayerView() {
        if (mPlayerView != null) {
            mPlayerView.setPlayer(mPlayer);
        }
    }

    public void releasePlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mSeekTime = mPlayer.getCurrentPosition();
            mPlayer.release();
            mPlayer = null;
        }
    }

    private void showMediaFullScreen() {
        mMediaContainer.setVisibility(View.VISIBLE);
        mButtonPrevious.setVisibility(View.GONE);
        mButtonNext.setVisibility(View.GONE);
        mShortDescriptionTextView.setVisibility(View.GONE);
        mDescriptionTextView.setVisibility(View.GONE);
    }

    private void showRegularView()  {
        mMediaContainer.setVisibility(View.VISIBLE);
        mButtonPrevious.setVisibility(View.VISIBLE);
        mButtonNext.setVisibility(View.VISIBLE);
        mShortDescriptionTextView.setVisibility(View.VISIBLE);
        mDescriptionTextView.setVisibility(View.VISIBLE);
    }

    private void showVideoHideThumbnail() {
        mPlayerView.setVisibility(View.VISIBLE);
        mThumnbailView.setVisibility(View.GONE);
    }

    private void hideVideoShowThumbnail() {
        mPlayerView.setVisibility(View.GONE);
        mThumnbailView.setVisibility(View.VISIBLE);
    }
}
