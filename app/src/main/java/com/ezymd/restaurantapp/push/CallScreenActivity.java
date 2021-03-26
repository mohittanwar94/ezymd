package com.ezymd.restaurantapp.push;

import android.media.AudioManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ezymd.restaurantapp.BaseActivity;
import com.ezymd.restaurantapp.R;
import com.ezymd.restaurantapp.customviews.SnapButton;
import com.ezymd.restaurantapp.customviews.SnapTextView;
import com.ezymd.restaurantapp.utils.GlideApp;
import com.ezymd.restaurantapp.utils.JSONKeys;
import com.ezymd.restaurantapp.utils.UIUtil;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallListener;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class CallScreenActivity extends BaseActivity {

    static final String TAG = CallScreenActivity.class.getSimpleName();

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private ImageView speaker, mute;
    private SnapTextView mCallDuration;
    private SnapTextView mCallState;
    private SnapTextView mCallerName;
    private boolean isEnableSpeaker, isEnableMute = false;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            CallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callscreen);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = findViewById(R.id.callDuration);

        speaker = findViewById(R.id.speaker);
        mute = findViewById(R.id.mute);
        mCallerName = findViewById(R.id.remoteUser);
        mCallState = findViewById(R.id.callState);
        SnapButton endCallButton = findViewById(R.id.hangupButton);

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);

        loadUserImage();

        speaker.setOnClickListener(v -> {
            UIUtil.clickAlpha(v);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            if (audioController != null && isEnableSpeaker) {
                audioController.disableSpeaker();
                isEnableSpeaker = false;
            } else if (audioController != null && !isEnableSpeaker) {
                isEnableSpeaker = true;
                audioController.enableSpeaker();
            }
            setSpeakerState(isEnableSpeaker);
        });

        mute.setOnClickListener(v -> {
            UIUtil.clickAlpha(v);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            if (audioController != null && isEnableMute) {
                audioController.unmute();
                isEnableMute = false;
            } else if (audioController != null && !isEnableMute) {
                isEnableMute = true;
                audioController.mute();
            }
            setMuteState(isEnableMute);
        });
    }

    private void setSpeakerState(boolean isEnableSpeaker) {
        if (!isEnableSpeaker)
            speaker.setImageResource(R.drawable.ic_speaker);
        else
            speaker.setImageResource(R.drawable.ic_speaker_yellow);

    }

    private void setMuteState(boolean isEnableSpeaker) {
        if (!isEnableSpeaker)
            mute.setImageResource(R.drawable.ic_microphone);
        else
            mute.setImageResource(R.drawable.ic_microphone_yellow);

    }

    private void loadUserImage() {
        String avatar = getIntent().getStringExtra(JSONKeys.AVATAR);
        ImageView avatarUser = findViewById(R.id.avatarUser);
        if (!TextUtils.isEmpty(avatar)) {
            GlideApp.with(this.getApplicationContext())
                    .load(avatar).fitCenter().dontAnimate()
                    .dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL).into(avatarUser);
        }
    }


    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.addCallListener(new SinchCallListener());
            mCallerName.setText(getIntent().getStringExtra(JSONKeys.NAME));
            mCallState.setText(call.getState().toString());
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mDurationTask.cancel();
        mTimer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
    }

    @Override
    public void onBackPressed() {

    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(int totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallDuration.setText(formatTimespan(call.getDetails().getDuration()));
        }
    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            //Toast.makeText(CallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();
            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            speaker.setEnabled(true);
            mute.setEnabled(true);
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.disableSpeaker();
        }

        @Override
        public void onCallProgressing(Call call) {
            speaker.setEnabled(false);
            mute.setEnabled(false);
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

    }
}
