package com.nickwinegar.spacexdemo.ui.launch.launchDetail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.ui.launch.LaunchListActivity;
import com.nickwinegar.spacexdemo.util.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a single Launch detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link LaunchListActivity}.
 */
public class LaunchDetailActivity extends AppCompatActivity {
    public static final String ARG_ITEM_ID = "item_id";
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.launch_highlight_image)
    ImageView highlightImageView;

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
        }

        launchFlightNumber = getIntent().getIntExtra(ARG_ITEM_ID, 0);
        setupUi();
    }

    private void setupUi() {
        viewModel.getLaunch(launchFlightNumber)
                .observe(this, launch -> {
                    if (launch == null) return;
                    collapsingToolbar.setTitle(launch.rocket.name);
                    if (!launch.links.highlightImageUrl.isEmpty()) {
                        GlideApp.with(this)
                                .load(launch.links.highlightImageUrl)
                                .centerCrop()
                                .into(highlightImageView);
                    }
                });
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
