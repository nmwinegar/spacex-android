package com.nickwinegar.spacexdemo.ui.launch;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailActivity;
import com.nickwinegar.spacexdemo.util.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of SpaceX Launches. Launches display as either
 * an upcoming launch, or a previous launch. Selecting a launch navigates to detail view
 */
public class LaunchListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.launch_list) RecyclerView launchList;

    private LaunchesAdapter launchListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LaunchListViewModel viewModel = ViewModelProviders.of(this).get(LaunchListViewModel.class);

        setContentView(R.layout.activity_launch_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView(launchList);

        viewModel.getLaunches()
                .observe(this, launches -> launchListAdapter.setLaunches(launches));
        viewModel.getErrorMessage()
                .observe(this, error -> {
                    if (error != null) {
                        Snackbar.make(launchList, error, Snackbar.LENGTH_LONG).show();
                    }
                });
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
            startActivity(intent);
        }
    };
}
