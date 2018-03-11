package com.nickwinegar.spacexdemo.ui.launch;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An activity representing a list of Launches. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LaunchDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class LaunchListActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.launch_list)
    RecyclerView launchList;

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
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        launchListAdapter = new LaunchesAdapter(Glide.with(this));
        recyclerView.setAdapter(launchListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
