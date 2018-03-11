package com.nickwinegar.spacexdemo.ui.launch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.SpaceXDemoApp;
import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailActivity;

import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * An activity representing a list of Launches. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LaunchDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class LaunchListActivity extends AppCompatActivity {

    @Inject
    SpaceXService spaceXService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((SpaceXDemoApp)getApplication()).appComponent.inject(this);

        setContentView(R.layout.activity_launch_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        RecyclerView recyclerView = findViewById(R.id.launch_list);
        assert recyclerView != null;
        setupRecyclerView(recyclerView);

        spaceXService.getLaunches()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(launches -> {
                    // Sort launches from most recent to oldest
                    Collections.sort(launches, (launch1, launch2) -> Long.compare(launch2.launchDateTimestamp, launch1.launchDateTimestamp));
                    LaunchesAdapter adapter = (LaunchesAdapter) recyclerView.getAdapter();
                    adapter.setLaunches(launches);
                });
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new LaunchesAdapter(Glide.with(this)));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }
}
