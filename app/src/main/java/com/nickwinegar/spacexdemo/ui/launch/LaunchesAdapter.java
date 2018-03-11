package com.nickwinegar.spacexdemo.ui.launch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.nickwinegar.spacexdemo.R;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.model.Rocket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LaunchesAdapter extends RecyclerView.Adapter<LaunchesAdapter.ViewHolder> {
    private final RequestManager glide;
    private List<Launch> launches;

    LaunchesAdapter(RequestManager glide) {
        this.glide = glide;
    }

    // TODO Diff incoming launches to only update what is needed
    void setLaunches(List<Launch> launches) {
        if (this.launches == null) {
            this.launches = launches;
            notifyItemRangeInserted(0, launches.size());
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

        glide.load(launch.links.patchUrl)
                .into(holder.patchView);
        Date launchTime = new Date(launch.launchDateTimestamp * 1000);
        holder.launchTime.setText(new SimpleDateFormat("MMMM d y, h:mm aaa", Locale.getDefault()).format(launchTime));
        holder.rocketName.setText(launch.rocket.name);
        String payloadDescription = getPayloadDescription(launch);
        holder.payloadDescription.setText(payloadDescription);
    }

    private String getPayloadDescription(Launch launch) {
        StringBuilder builder = new StringBuilder();
        if (launch.rocket.secondStage.payloads.length > 0) {
            for (int i = 0; i < launch.rocket.secondStage.payloads.length; i++) {
                Rocket.SecondStage.Payload payload = launch.rocket.secondStage.payloads[i];
                builder.append(TextUtils.join("/", payload.customers));
                builder.append(" - ");
                builder.append(payload.name);
                if (i < launch.rocket.secondStage.payloads.length - 1) builder.append(", ");
            }
        }

        return builder.toString();
    }

    @Override
    public int getItemCount() {
        return launches == null ? 0 : launches.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.launch_time)
        TextView launchTime;
        @BindView(R.id.rocket_name)
        TextView rocketName;
        @BindView(R.id.payload_description)
        TextView payloadDescription;
        @BindView(R.id.patch_image_view)
        ImageView patchView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
