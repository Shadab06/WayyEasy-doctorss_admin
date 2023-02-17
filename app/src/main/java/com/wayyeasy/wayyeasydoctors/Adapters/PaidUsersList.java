package com.wayyeasy.wayyeasydoctors.Adapters;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.url;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.wayyeasy.wayyeasydoctors.Listeners.UsersListener;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.R;

import java.util.List;

public class PaidUsersList  extends RecyclerView.Adapter<PaidUsersList.holder>{

    private static final String TAG = "Close Data";
    List<user_booked_response_model> data;
    private UsersListener usersListener;
    private String token;

    public PaidUsersList(List<user_booked_response_model> data, UsersListener usersListener, String token) {
        this.data = data;
        this.usersListener = usersListener;
        this.token = token;
    }

    @NonNull
    @Override
    public holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_paid_user_design, parent, false);
        return new holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull holder holder, int position) {
        holder.userName.setText(data.get(position).getName());
        holder.userAge.setText(data.get(position).getAge());

        String imgUrl = url + data.get(position).getProfileImage();

        GlideUrl glideUrl = new GlideUrl(imgUrl,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build());

        Glide.with(holder.userName.getContext()).load(glideUrl).into(holder.userProfileImage);

        holder.audioCall.setOnClickListener(view -> initializeAudioCall(data.get(position)));
        holder.videoCall.setOnClickListener(view -> initializeVideoCall(data.get(position)));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        ImageView userProfileImage, audioCall, videoCall;
        TextView userAge, userName;
        public holder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            audioCall = itemView.findViewById(R.id.internet_audio_call);
            videoCall = itemView.findViewById(R.id.internet_video_call);
            userAge = itemView.findViewById(R.id.user_age);
            userName = itemView.findViewById(R.id.user_name);
        }
    }

    private void initializeVideoCall(user_booked_response_model user) {
        usersListener.initializeVideoMeet(user);
    }

    private void initializeAudioCall(user_booked_response_model user) {
        usersListener.initializeAudioMeet(user);
    }
}
