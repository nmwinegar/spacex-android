package com.nickwinegar.spacexdemo.ui.launch.launchDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.model.Launchpad;
import com.nickwinegar.spacexdemo.model.Rocket;
import com.nickwinegar.spacexdemo.ui.launch.LaunchListActivity;
import com.nickwinegar.spacexdemo.util.GlideApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Launch detail screen.
 * This view displays details around the launch as well as
 * provide links for the user to access additional functionality
 * like YouTube highlight video and launch location
 */
public class LaunchDetailActivity extends AppCompatActivity {
    public static final String FLIGHT_NUMBER = "flight_number";
    public static final String IS_UPCOMING = "isUpcoming";
    @BindView(R.id.launch_detail_container) View launchDetailLayout;
    @BindView(R.id.launch_detail_progressbar) ProgressBar launchDetailProgressBar;
    @BindView(R.id.detail_toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_layout) CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.play_video_fab) FloatingActionButton playVideoFab;
    @BindView(R.id.launch_highlight_image) ImageView highlightImageView;
    @BindView(R.id.detail_header_textview) TextView launchDetailHeader;
    @BindView(R.id.launch_success_header) TextView launchSuccessHeader;
    @BindView(R.id.launch_description) TextView launchDescription;
    @BindView(R.id.location_layout) View locationLayout;
    @BindView(R.id.launch_site_location) TextView launchSiteLocation;
    @BindView(R.id.launch_site_full_name) TextView launchSiteDescription;
    @BindView(R.id.launch_rocket_name) TextView launchRocketName;
    @BindView(R.id.first_stage_cores) LinearLayout launchFirstStageCores;
    @BindView(R.id.second_stage_payloads) LinearLayout secondStagePayloads;

    private LaunchDetailViewModel viewModel;
    private boolean launchIsUpcoming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(LaunchDetailViewModel.class);

        setContentView(R.layout.activity_launch_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        playVideoFab.setOnClickListener(arg -> playHighlightVideo());
        locationLayout.setOnClickListener(arg -> viewLaunchsiteLocation());

        launchDetailProgressBar.setVisibility(View.VISIBLE);
        launchDetailLayout.setVisibility(View.INVISIBLE);

        int launchFlightNumber = getIntent().getIntExtra(FLIGHT_NUMBER, 0);
        launchIsUpcoming = getIntent().getBooleanExtra(IS_UPCOMING, false);
        viewModel.getLaunch()
                .observe(this, launch -> {
                    updateLaunchInformation(launch);
                    if (launch != null) {
                        viewModel.getLaunchpadDetails(launch.launchSite.id)
                                .observe(this, this::updateLaunchpadInformation);
                    }
                });
        viewModel.getErrorMessage()
                .observe(this, error -> {
                    if (error != null) {
                        Snackbar.make(launchDetailLayout, error, Snackbar.LENGTH_SHORT);
                    }
                });

        if (launchIsUpcoming) viewModel.loadUpcomingLaunch(launchFlightNumber);
        else viewModel.loadPreviousLaunch(launchFlightNumber);
    }

    private void updateLaunchInformation(Launch launch) {
        if (launch == null) return;
        if (launch.links.highlightImageUrl != null && !launch.links.highlightImageUrl.isEmpty()) {
            GlideApp.with(this)
                    .load(launch.links.highlightImageUrl)
                    .placeholder(R.drawable.ic_rocket)
                    .centerCrop()
                    .into(highlightImageView);
        } else {
            GlideApp.with(this)
                    .load("")
                    .placeholder(R.drawable.ic_rocket)
                    .centerCrop()
                    .into(highlightImageView);
            playVideoFab.setVisibility(View.GONE);
        }
        Date launchTime = new Date(launch.launchDateTimestamp * 1000);
        launchDetailHeader.setText(new SimpleDateFormat("MMMM d, y, h:mm aaa", Locale.getDefault()).format(launchTime));
        if (!launchIsUpcoming) {
            String launchSuccessMessage = launch.launchSuccess ? "Mission Success" : "Mission Failure";
            launchSuccessHeader.setText(launchSuccessMessage);
        } else launchSuccessHeader.setVisibility(View.GONE);
        launchDescription.setText(launch.details);
        launchRocketName.setText(launch.rocket.name);
        addCoreViews(launch.rocket.firstStage.cores);
        addPayloadViews(launch.rocket.secondStage.payloads);

        launchDetailProgressBar.setVisibility(View.GONE);
        launchDetailLayout.setVisibility(View.VISIBLE);
    }

    private void updateLaunchpadInformation(Launchpad launchpad) {
        launchSiteLocation.setText(String.format("%s, %s", launchpad.launchpadLocation.name, launchpad.launchpadLocation.region));
        launchSiteDescription.setText(launchpad.fullName);
    }

    private void addCoreViews(List<Rocket.FirstStage.Core> cores) {
        launchFirstStageCores.removeAllViews();
        for (Rocket.FirstStage.Core core : cores) {
            View coreItem = LayoutInflater.from(this).inflate(R.layout.core_detail, null);

            TextView coreSerial = coreItem.findViewById(R.id.core_serial);
            TextView coreFlightCount = coreItem.findViewById(R.id.core_flight_count);
            TextView coreLandingSuccess = coreItem.findViewById(R.id.core_landing_success);

            coreSerial.setText(String.format("Serial: %s", core.serial));
            coreFlightCount.setText(String.format(Locale.getDefault(), "Flight #%d", core.flightCount));
            if (core.landingSuccess) {
                coreLandingSuccess.setText(R.string.landing_success);
                coreLandingSuccess.setVisibility(View.VISIBLE);
            } else coreLandingSuccess.setVisibility(View.GONE);

            launchFirstStageCores.addView(coreItem);
        }
    }

    private void addPayloadViews(List<Rocket.SecondStage.Payload> payloads) {
        secondStagePayloads.removeAllViews();
        for (Rocket.SecondStage.Payload payload : payloads) {
            View payloadItem = LayoutInflater.from(this).inflate(R.layout.payload_detail, null);

            TextView payloadName = payloadItem.findViewById(R.id.payload_name);
            TextView payloadCustomers = payloadItem.findViewById(R.id.payload_customers);
            TextView payloadOrbit = payloadItem.findViewById(R.id.payload_orbit);

            payloadName.setText(String.format("%s - %s", payload.payloadType, payload.name));
            payloadCustomers.setText(TextUtils.join("/", payload.customers));
            payloadOrbit.setText(viewModel.getOrbitDescription(payload.orbit));

            secondStagePayloads.addView(payloadItem);
        }
    }

    private void playHighlightVideo() {
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, viewModel.getVideoAppUri());
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, viewModel.getVideoWebUri());
            startActivity(webIntent);
        }
    }

    private void viewLaunchsiteLocation() {
        try {
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(Locale.getDefault(), "geo:%f, %f", viewModel.getLaunchpadLatitude(), viewModel.getLaunchpadLongitude())));
            startActivity(mapsIntent);
        } catch (Exception e) {
            Snackbar.make(launchDetailLayout, "Unable to open launchsite location", Snackbar.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, LaunchListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
