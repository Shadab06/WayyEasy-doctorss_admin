package com.wayyeasy.wayyeasydoctors.Listeners;

import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;

public interface UsersListener {
    void initializeVideoMeet(user_booked_response_model user);

    void initializeAudioMeet(user_booked_response_model user);
}
