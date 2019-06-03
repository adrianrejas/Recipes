package org.udacity.android.arejas.recipes.presentation.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
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
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.databinding.FragmentRecipeStepBinding;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipeDetailsViewModel;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.factories.ViewModelFactory;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailsListActivity;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailInfoActivity;
import org.udacity.android.arejas.recipes.presentation.ui.interfaces.NavigationInterface;
import org.udacity.android.arejas.recipes.utils.Utils;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javax.inject.Inject;

/**
 * A fragment representing a single RecipeDb detail screen.
 * This fragment is either contained in a {@link RecipeDetailsListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailInfoActivity}
 * on handsets.
 */
public class RecipeStepFragment extends Fragment implements NavigationInterface, ExoPlayer.EventListener {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_STEP_POSITION_ID = "step_id";

    private static final String TAG = RecipeStepFragment.class.getSimpleName();

    @Inject
    public ViewModelFactory viewModelFactory;

    private RecipeDetailsViewModel recipeDetailsViewModel;

    private FragmentRecipeStepBinding uiBinding;

    private Integer stepPositionId = null;

    private static MediaSessionCompat mMediaSession;
    private SimpleExoPlayer mExoPlayer;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the step ID to show
        if (getArguments().containsKey(ARG_STEP_POSITION_ID)) {
            stepPositionId = getArguments().getInt(ARG_STEP_POSITION_ID);
        }
        // Get the recipe details activity view model and observe the changes in the recipe
        recipeDetailsViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity()), viewModelFactory).get(RecipeDetailsViewModel.class);
        recipeDetailsViewModel.getRecipeDetails().observe(this, new Observer<Resource<Recipe>>() {
            @Override
            public void onChanged(@Nullable Resource<Recipe> recipeResource) {
                if (recipeResource == null) {
                    showError(new NullPointerException("No data was loaded."));
                } else {
                    if (recipeResource.getStatus() == Resource.Status.ERROR) {
                        showError(recipeResource.getError());
                    } else if (recipeResource.getStatus() == Resource.Status.LOADING) {
                        showLoading();
                    } else {
                        updateData(recipeResource.getData());
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Initialize media session
        initializeMediaSession();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // If orientation is landscape and layout is the small one, set full screen
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;
        float dpWidth  = outMetrics.widthPixels / density;
        if ((getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) &&
                (dpWidth < 720)) {
            getActivity().getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE
                            // Set the content to appear under the system bars so that the
                            // content doesn't resize when the system bars hide and show.
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            // Hide the nav bar and status bar
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        // Inflate the layout corresponding to the ingredients
        uiBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_step,
                container, false);
        // Set activity title accordingly JUST IF WE'RE in a one-pane view (in a two-pane view the
        // activity decides the title)
        if (getActivity() instanceof RecipeDetailInfoActivity)
            getActivity().setTitle(Utils.getFontedString(getString(R.string.title_recipe_step), R.font.tillana_bold));
        // Set navigator listener
        uiBinding.setNavigator(this);
        return uiBinding.getRoot();
    }

    /**
     * Update the UI with the recipe got from repository
     *
     * @param recipe Recipe obtained from repository.
     */
    private void updateData(Recipe recipe) {
        if ((recipe != null) && (stepPositionId != null) &&
                (recipe.getSteps() != null) &&
                (recipe.getSteps().get(stepPositionId) != null)) {
            uiBinding.stepLayout.setVisibility(View.VISIBLE);
            uiBinding.stepLoadingLayout.loadingLayout.setVisibility(View.GONE);
            uiBinding.stepErrorLayout.errorLayout.setVisibility(View.GONE);
            uiBinding.setRecipeStep(recipe.getSteps().get(stepPositionId));
            if ((stepPositionId + 1) >= recipe.getSteps().size()) {
                if (uiBinding.btStepNext != null)
                    uiBinding.btStepNext.setVisibility(View.INVISIBLE);
            } else {
                uiBinding.setNextDetailId((stepPositionId + 1));
            }
            if (((stepPositionId - 1) >= 0)) {
                uiBinding.setPrevDetailId((stepPositionId - 1));
            } else if ((recipe.getIngredients() == null) ||
                            (recipe.getIngredients().isEmpty())){
                if (uiBinding.btStepPrevious != null)
                    uiBinding.btStepPrevious.setVisibility(View.INVISIBLE);
            }
            initializePlayer(recipe.getSteps().get(stepPositionId).getVideoURL(),
                    recipe.getSteps().get(stepPositionId).getThumbnailURL());
        } else {
            showError(new NullPointerException("No valid data."));
        }
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {
        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(false);

    }

    /**
     * Initialize ExoPlayer with requested video, or thumbnail or no preview in case of not having video
     * @param mediaUrl The URI of the sample to play.
     * @param thumbnailUrl The URI of the sample to play.
     */
    private void initializePlayer(String mediaUrl, String thumbnailUrl) {
        if ((mediaUrl != null) && (!mediaUrl.isEmpty())) {
            if (mExoPlayer != null) {
                releasePlayer();
            }
            // Hide the thumbnail (leave the nopreview without text view because it's also serving as background)
            uiBinding.stepThumbnail.setVisibility(View.GONE);
            uiBinding.stepPlayer.setVisibility(View.VISIBLE);
            uiBinding.stepNopreview.setVisibility(View.VISIBLE);
            // If present and there is no problem, load the thumbnail url as default artwork.
            try {
                if ((thumbnailUrl != null) && (!thumbnailUrl.isEmpty()))
                    uiBinding.stepPlayer.setDefaultArtwork(BitmapFactory.decodeStream(
                            (new URL(thumbnailUrl)).openStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            uiBinding.stepPlayer.setPlayer(mExoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            mExoPlayer.addListener(this);

            // Prepare the MediaSource and set ready to be played when the user wants to.
            String userAgent = Util.getUserAgent(getContext(), "StepRecipe");
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(mediaUrl), new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(false);
        } else if ((thumbnailUrl != null) && (!thumbnailUrl.isEmpty())){
            // Hide the player and set the nopreview text empty, leaving only the thumbnail and the nopreview as backgrond
            uiBinding.stepThumbnail.setVisibility(View.VISIBLE);
            uiBinding.stepPlayer.setVisibility(View.GONE);
            uiBinding.stepNopreview.setVisibility(View.VISIBLE);
            uiBinding.stepNopreview.setText("");
            // Use Glide library to load the thumbnail (using Glide instead of the same exoplayer artwork
            // for not showing the controls and control in a deeper way the loading of an image
            Glide.with(getContext())
                    .load(thumbnailUrl)
                    .error(R.drawable.step)
                    .placeholder(R.drawable.loading)
                    .into(uiBinding.stepThumbnail);
        } else {
            // Hide the player (leave the nopreview without text view because it's also serving as background)
            uiBinding.stepThumbnail.setVisibility(View.GONE);
            uiBinding.stepPlayer.setVisibility(View.GONE);
            uiBinding.stepNopreview.setVisibility(View.VISIBLE);
            uiBinding.stepNopreview.setText(getString(R.string.no_preview));
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    /**
     * Show an error on the activity. The idea has been to escalate errors to the UI classes by the
     * exception mechanisms and let these classes decide how to show the error.
     *
     * @param error Exception with the error appeared.
     */
    private void showError(Throwable error) {
        uiBinding.stepLayout.setVisibility(View.GONE);
        uiBinding.stepLoadingLayout.loadingLayout.setVisibility(View.GONE);
        uiBinding.stepErrorLayout.errorLayout.setVisibility(View.VISIBLE);
        if (error instanceof NullPointerException)
            uiBinding.stepErrorLayout.tvError.setText(getString(R.string.error_data));
        if (error instanceof IOException)
            uiBinding.stepErrorLayout.tvError.setText(getString(R.string.error_connection));
        else
            uiBinding.stepErrorLayout.tvError.setText(getString(R.string.error_ui));
    }

    /**
     * Show loading data on the activity. If Swipe refresh layout is yet refreshing no actions have
     * to be taken.
     *
     */
    private void showLoading() {
        uiBinding.stepLayout.setVisibility(View.GONE);
        uiBinding.stepLoadingLayout.loadingLayout.setVisibility(View.VISIBLE);
        uiBinding.stepErrorLayout.errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFragment(Integer fragmentId) {
        if (fragmentId == null) {
            RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
        } else {
            Bundle arguments = new Bundle();
            arguments.putInt(RecipeStepFragment.ARG_STEP_POSITION_ID, fragmentId.intValue());
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(arguments);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
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

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        // Setting the nopreview view
        uiBinding.stepThumbnail.setVisibility(View.GONE);
        uiBinding.stepPlayer.setVisibility(View.GONE);
        uiBinding.stepNopreview.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPositionDiscontinuity() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
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
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    /**
     * Broadcast Receiver registered to receive the MEDIA_BUTTON intent coming from clients.
     */
    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }

}
