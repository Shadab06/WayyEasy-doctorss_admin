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
import com.wayyeasy.wayyeasydoctors.R;
import com.wayyeasy.wayyeasydoctors.databinding.ActivityIncomingInvitationBinding;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingInvitation extends AppCompatActivity {

    ActivityIncomingInvitationBinding incomingInvitationBinding;
    private String meetingType = null;
    private MediaPlayer mediaPlayer;
    private boolean callResponded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        incomingInvitationBinding = ActivityIncomingInvitationBinding.inflate(getLayoutInflater());
        setContentView(incomingInvitationBinding.getRoot());

        callResponded = false;

        mediaPlayer = MediaPlayer.create(this, R.raw.incoming_call_ring);
        mediaPlayer.start();

        new Handler().postDelayed(() -> {
            if (!callResponded)
                mediaPlayer.start();
        }, 28000);

        Intent intent = getIntent();
        meetingType = intent.getStringExtra(Constants.REMOTE_MESSAGE_MEETING_TYPE);
        String bookedUserName = intent.getStringExtra(Constants.name);
        if (meetingType != null) {
            if (meetingType.equals("video")) {
                incomingInvitationBinding.callType.setImageResource(R.drawable.ic_video);
            } else
                incomingInvitationBinding.callType.setImageResource(R.drawable.ic_call);
        }

        if (bookedUserName != null) {
            incomingInvitationBinding.userIcon.setText(bookedUserName.substring(0, 2));
            incomingInvitationBinding.userName.setText(bookedUserName);
        }

        incomingInvitationBinding.receiveCall.setOnClickListener(view -> sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                getIntent().getStringExtra(Constants.REMOTE_MESSAGE_INVITER_TOKEN)));

        incomingInvitationBinding.rejectCall.setOnClickListener(view -> sendInvitationResponse(Constants.REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(Constants.REMOTE_MESSAGE_INVITER_TOKEN)));
    }

    public void sendInvitationResponse(String type, String receiverToken) {
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MESSAGE_DATA, data);
            body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);
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

                            callResponded = true;
                            new Handler().post(() -> mediaPlayer.stop());

                        if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)) {
                                try {

                                    URL serverURL = new URL("https://meet.jit.si");

                                    JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder();
                                    builder.setServerURL(serverURL);
                                    builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));

                                    if (meetingType.equals("audio")) {
                                        builder.setVideoMuted(true);
                                    }

                                    JitsiMeetActivity.launch(IncomingInvitation.this, builder.build());
                                    finish();

                                } catch (Exception e) {
                                    Log.d(TAG, "onResponse: 109 " + e.getMessage());
                                    Toast.makeText(IncomingInvitation.this, "Meeting Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(IncomingInvitation.this, "Invitation Rejected", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {

                            callResponded = true;
                            new Handler().post(() -> mediaPlayer.stop());

                            Log.d(TAG, "onResponse: " + response);
                            Toast.makeText(IncomingInvitation.this, "Response: " + response.message(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {

                        callResponded = true;
                        new Handler().post(() -> mediaPlayer.stop());

                        Log.d(TAG, "Error: 105 " + t.getMessage());
                        Toast.makeText(IncomingInvitation.this, "Error: 127 " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type != null) {
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {

                    callResponded = true;
                    new Handler().post(() -> mediaPlayer.stop());

                    Toast.makeText(context, "Invitation Cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: 162 " + "sdlkfjlsjfdlk");
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(invitationResponseReceiver, new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStart: 168 " + "sdlkfjlsjfdlk");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(invitationResponseReceiver);
    }
}