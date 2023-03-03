package com.wayyeasy.wayyeasydoctors.Activities;

import static com.wayyeasy.wayyeasydoctors.Activities.DashboardActivity.TAG;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.CallingApiHandlers.CallingApiControllers;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.CallingApiHandlers.CallingApiSet;
import com.wayyeasy.wayyeasydoctors.ComponentFiles.Constants.Constants;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.single_user_booked;
import com.wayyeasy.wayyeasydoctors.Models.RealtimeCalling.user_booked_response_model;
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.Utils.SharedPreferenceManager;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityOutgoingInvitationBinding;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.util.UUID;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitation extends AppCompatActivity {

    ActivityOutgoingInvitationBinding outgoingInvitationBinding;
    single_user_booked bookedUser;
    SharedPreferenceManager preferenceManager;
    private String inviterToken = null;
    private String meetingRoom = null;
    private String meetingType = null;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outgoingInvitationBinding = ActivityOutgoingInvitationBinding.inflate(getLayoutInflater());
        setContentView(outgoingInvitationBinding.getRoot());

        preferenceManager = new SharedPreferenceManager(getApplicationContext());

        mediaPlayer = MediaPlayer.create(this, R.raw.incoming_call_ring);
        mediaPlayer.start();

        Intent intent = getIntent();
        meetingType = intent.getStringExtra("type");
        bookedUser = intent.getParcelableExtra("user");
        if (meetingType != null) {
            if (meetingType.equals("video")) {
                outgoingInvitationBinding.callType.setImageResource(R.drawable.ic_video);
            } else {
                outgoingInvitationBinding.callType.setImageResource(R.drawable.ic_call);
            }
        }

        if (bookedUser != null) {
            outgoingInvitationBinding.userIcon.setText(bookedUser.getName().substring(0, 2));
            outgoingInvitationBinding.userName.setText(bookedUser.getName());
        }

        outgoingInvitationBinding.rejectCall.setOnClickListener(view -> {
            if (bookedUser != null) {
                cancelInvitation(bookedUser.getFcmToken());
            }
        });

        if (meetingType != null && bookedUser != null) {
            initiateMeeting(meetingType, bookedUser.getFcmToken());
        }

    }

    private void initiateMeeting(String meetingType, String receiverToken) {

        try {

            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MESSAGE_MEETING_TYPE, meetingType);
            data.put(Constants.name, preferenceManager.getString(Constants.name));
            data.put(Constants.REMOTE_MESSAGE_INVITER_TOKEN, preferenceManager.getString(Constants.KEY_FCM_TOKEN));

            meetingRoom = preferenceManager.getString(Constants.mongoId) + "_" + UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.REMOTE_MSG_MEETING_ROOM, meetingRoom);
            body.put(Constants.REMOTE_MESSAGE_DATA, data);
            body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void sendRemoteMessage(String remoteBodyMessage, String type) {

        CallingApiControllers.getClient().create(CallingApiSet.class).sendRemoteMessage(
                        Constants.getRemoteMessageHeaders(), remoteBodyMessage)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response != null && response.isSuccessful()) {
                            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {

                                new Handler().post(() -> mediaPlayer.start());

                            } else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)) {

                                Log.d(TAG, "onResponse: 133 " + response);

                                new Handler().post(() -> mediaPlayer.stop());

                                finish();

                            }

                        } else {

                            Log.d(TAG, "onResponse: " + response);

                            new Handler().post(() -> mediaPlayer.stop());

                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                        new Handler().post(() -> mediaPlayer.stop());

                        Log.d(TAG, "Error: 105 " + t.getMessage());
                        Toast.makeText(OutgoingInvitation.this, "Error: 127 " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();

                    }
                });
    }

    public void cancelInvitation(String receiverToken) {
        try {

            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MESSAGE_DATA, data);
            body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);

        } catch (Exception e) {
            Log.d(TAG, "sendInvitationResponse: 65 " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {

                    new Handler().post(() -> mediaPlayer.stop());

                    try {

                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverURL);
                        builder.setRoom(meetingRoom);

                        if (meetingType.equals("audio")) {
                            builder.setVideoMuted(true);
                        }

                        JitsiMeetActivity.launch(OutgoingInvitation.this, builder.build());
                        finish();

                    } catch (Exception e) {
                        Log.d(TAG, "onResponse: 109 " + e.getMessage());
                        Toast.makeText(OutgoingInvitation.this, "Meeting Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)) {

                    new Handler().post(() -> mediaPlayer.stop());

                    Toast.makeText(context, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(invitationResponseReceiver, new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReceiver);
    }
}