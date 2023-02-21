package com.wayyeasy.wayyeasydoctors.Adapters;

import static com.wayyeasy.wayyeasydoctors.ComponentFiles.ApiHandlers.ApiControllers.url;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.wayyeasy.wayyeasydoctors.Activities.SingleConsultationActivity;
import com.wayyeasy.wayyeasydoctors.Listeners.UsersListener;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.R;

import java.util.List;

public class PaidUsersList  extends RecyclerView.Adapter<PaidUsersList.holder>{

    private static final String TAG = "Close Data";
    List<user_booked_response_model> data;
    private String token;

    public PaidUsersList(List<user_booked_response_model> data, String token) {
        this.data = data;
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
        holder.consultation.setText(data.get(position).getConsultation());

        if (data.get(position).getConsultation().equals("pending"))
            holder.consultation.setTextColor(holder.consultation.getContext().getResources().getColor(R.color.theme_color_orange));
        else
            holder.consultation.setTextColor(holder.consultation.getContext().getResources().getColor(R.color.theme_color));

        String imgUrl = url + data.get(position).getProfileImage();

        GlideUrl glideUrl = new GlideUrl(imgUrl,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build());

        Glide.with(holder.userName.getContext()).load(glideUrl).into(holder.userProfileImage);

        holder.singleItem.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), SingleConsultationActivity.class);
            intent.putExtra("user", data.get(position));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class holder extends RecyclerView.ViewHolder {
        ImageView userProfileImage;
        TextView userAge, userName, consultation;
        CardView singleItem;
        public holder(@NonNull View itemView) {
            super(itemView);
            userProfileImage = itemView.findViewById(R.id.user_profile_image);
            userAge = itemView.findViewById(R.id.user_age);
            userName = itemView.findViewById(R.id.user_name);
            consultation = itemView.findViewById(R.id.consultation);
            singleItem = itemView.findViewById(R.id.single_item);
        }
    }
}
