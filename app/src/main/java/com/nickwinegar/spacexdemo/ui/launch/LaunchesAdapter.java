package com.nickwinegar.spacexdemo.ui.launch;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.model.Rocket;
import com.nickwinegar.spacexdemo.util.GlideRequests;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LaunchesAdapter extends RecyclerView.Adapter<LaunchesAdapter.ViewHolder> {
    private final GlideRequests glide;
    private LaunchSelectedCallback launchSelectedCallback;
    private List<Launch> launches;

    private View.OnClickListener launchSelectedListener = view -> {
        if (launchSelectedCallback != null) {
            Launch launch = (Launch) view.getTag();
            launchSelectedCallback.onLaunchSelected(launch);
        }
    };

    LaunchesAdapter(GlideRequests glide, LaunchSelectedCallback launchSelectedCallback) {
        this.glide = glide;
        this.launchSelectedCallback = launchSelectedCallback;
    }

    void setLaunches(List<Launch> newLaunches) {
        if (launches == null) {
            launches = newLaunches;
            notifyItemRangeInserted(0, newLaunches.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return launches.size();
                }

                @Override
                public int getNewListSize() {
                    return newLaunches.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return launches.get(oldItemPosition).getFlightNumber() ==
                            newLaunches.get(newItemPosition).getFlightNumber();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Launch newLaunch = newLaunches.get(newItemPosition);
                    Launch oldLaunch = launches.get(oldItemPosition);
                    return newLaunch.getFlightNumber() == oldLaunch.getFlightNumber();
                }
            });
            launches = newLaunches;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.launch_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Launch launch = launches.get(position);

        holder.itemView.setTag(launch);
        holder.itemView.setOnClickListener(launchSelectedListener);
        glide.load(launch.getLinks().getPatchUrl())
                .placeholder(R.drawable.ic_rocket)
                .into(holder.patchView);
        Date launchTime = new Date(launch.getLaunchDateTimestamp() * 1000);
        holder.launchTime.setText(new SimpleDateFormat("MMMM d, y, h:mm aaa", Locale.getDefault()).format(launchTime));
        holder.rocketName.setText(launch.getRocket().getName());
        String payloadDescription = getPayloadDescription(launch);
        holder.payloadDescription.setText(payloadDescription);
    }

    private String getPayloadDescription(Launch launch) {
        StringBuilder builder = new StringBuilder();
        if (launch.getRocket().getSecondStage().getPayloads().size() > 0) {
            for (int i = 0; i < launch.getRocket().getSecondStage().getPayloads().size(); i++) {
                Rocket.SecondStage.Payload payload = launch.getRocket().getSecondStage().getPayloads().get(i);
                builder.append(TextUtils.join("/", payload.getCustomers()));
                builder.append(" - ");
                builder.append(payload.getName());
                if (i < launch.getRocket().getSecondStage().getPayloads().size() - 1) builder.append(", ");
            }
        }

        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return launches == null ? 0 : launches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.launch_time) TextView launchTime;
        @BindView(R.id.rocket_name) TextView rocketName;
        @BindView(R.id.payload_description) TextView payloadDescription;
        @BindView(R.id.patch_image_view) ImageView patchView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
