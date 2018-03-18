package com.nickwinegar.spacexdemo.ui.launch;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailActivity;
import com.nickwinegar.spacexdemo.util.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of SpaceX Launches. Launches display as either
 * an upcoming launch, or a previous launch. Selecting a launch navigates to detail view
 */
public class LaunchListActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.launch_list) RecyclerView launchList;
    @BindView(R.id.error_layout) LinearLayout errorLayout;
    @BindView(R.id.retry_button) Button retryButton;

    private LaunchesAdapter launchListAdapter;
    private LaunchListViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(LaunchListViewModel.class);

        setContentView(R.layout.activity_launch_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        setupTabs();
        toolbar.setTitle(getTitle());
        setupRecyclerView(launchList);
        retryButton.setOnClickListener(arg -> {
            loadLaunches(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
        });

        viewModel.getLaunches()
                .observe(this, launches -> {
                    launchListAdapter.setLaunches(launches);
                    launchList.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                });
        viewModel.getErrorMessage()
                .observe(this, error -> {
                    if (error != null) {
                        Snackbar.make(launchList, error, Snackbar.LENGTH_LONG).show();
                        launchList.setVisibility(View.GONE);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                });

        loadLaunches(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText(R.string.previous_launches));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.upcoming_launches));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addOnTabSelectedListener(this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        launchListAdapter = new LaunchesAdapter(GlideApp.with(this), callback);
        recyclerView.setAdapter(launchListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private final LaunchSelectedCallback callback = launch -> {
        if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            Intent intent = new Intent(this, LaunchDetailActivity.class);
            intent.putExtra(LaunchDetailActivity.FLIGHT_NUMBER, launch.flightNumber);
            boolean isUpcomingLaunch = tabLayout.getTabAt(tabLayout.getSelectedTabPosition()).getText() == getResources().getString(R.string.upcoming_launches);
            intent.putExtra(LaunchDetailActivity.IS_UPCOMING, isUpcomingLaunch);
            startActivity(intent);
        }
    };

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        loadLaunches(tab);
    }

    private void loadLaunches(TabLayout.Tab tab) {
        if (tab.getText().equals(getResources().getText(R.string.upcoming_launches))) {
            viewModel.loadUpcomingLaunches();
        } else if (tab.getText().equals(getResources().getText(R.string.previous_launches))) {
            viewModel.loadPreviousLaunches();
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
