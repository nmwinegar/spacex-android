package com.nickwinegar.spacexdemo.ui.launch.launchDetail;

import android.arch.lifecycle.Lifecycle;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.model.Launchpad;
import com.nickwinegar.spacexdemo.ui.launch.LaunchListActivity;
import com.nickwinegar.spacexdemo.util.GlideApp;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Launch detail screen.
 * This view displays details around the launch as well as
 * provide links for the user to view additional details
 */
public class LaunchDetailActivity extends AppCompatActivity {
    public static final String ARG_ITEM_ID = "item_id";
    @BindView(R.id.launch_detail)
    LinearLayout launchDetailLayout;
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.play_video_fab)
    FloatingActionButton playVideoFab;
    @BindView(R.id.launch_highlight_image)
    ImageView highlightImageView;
    @BindView(R.id.detail_header_textview)
    TextView launchDetailHeader;
    @BindView(R.id.launch_success_header)
    TextView launchSuccessHeader;
    @BindView(R.id.launch_description)
    TextView launchDescription;
    @BindView(R.id.location_layout)
    View locationLayout;
    @BindView(R.id.launch_site_location)
    TextView launchSiteLocation;
    @BindView(R.id.launch_site_full_name)
    TextView launchSiteDescription;

    private LaunchDetailViewModel viewModel;
    private int launchFlightNumber;

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

        launchFlightNumber = getIntent().getIntExtra(ARG_ITEM_ID, 0);
        viewModel.getLaunch(launchFlightNumber)
                .observe(this, launch -> {
                    updateLaunchInformation(launch);
                    if (launch != null) {
                        viewModel.getLaunchpadDetails(launch.launchSite.id)
                                .observe(this, this::updateLaunchpadInformation);
                    }
                });
    }

    private void updateLaunchInformation(Launch launch) {
        if (launch == null) return;
        if (!launch.links.highlightImageUrl.isEmpty()) {
            GlideApp.with(this)
                    .load(launch.links.highlightImageUrl)
                    .centerCrop()
                    .into(highlightImageView);
        }
        Date launchTime = new Date(launch.launchDateTimestamp * 1000);
        launchDetailHeader.setText(new SimpleDateFormat("MMMM d, y, h:mm aaa", Locale.getDefault()).format(launchTime));
        String launchSuccessMessage = launch.launchSuccess ? "Mission Success" : "Mission Failure";
        launchSuccessHeader.setText(launchSuccessMessage);
        launchDescription.setText(launch.details);
    }

    private void updateLaunchpadInformation(Launchpad launchpad) {
        launchSiteLocation.setText(String.format("%s, %s", launchpad.launchpadLocation.name, launchpad.launchpadLocation.region));
        launchSiteDescription.setText(launchpad.fullName);
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
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("geo:%f, %f", viewModel.getLaunchpadLatitude(), viewModel.getLaunchpadLongitude())));
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
