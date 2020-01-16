package com.carusto.ReactNativePjSip;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import com.carusto.ReactNativePjSip.Custom.PjNotificationsManager;
import com.carusto.ReactNativePjSip.utils.ArgumentUtils;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;

import javax.annotation.Nullable;

public class PjSipBroadcastReceiver extends BroadcastReceiver {

    private static String TAG = "PjSipBroadcastReceiver";

    private int seq = 0;

    private ReactApplicationContext context;

    private HashMap<Integer, Callback> callbacks = new HashMap<>();

    private AudioManager audioManager;


    public PjSipBroadcastReceiver(ReactApplicationContext context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void setContext(ReactApplicationContext context) {
        this.context = context;
    }

    public int register(Callback callback) {
        int id = ++seq;
        callbacks.put(id, callback);
        return id;
    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(PjActions.EVENT_STARTED);
        filter.addAction(PjActions.EVENT_SERVICE_STOPPED);
        filter.addAction(PjActions.EVENT_ACCOUNT_CREATED);
        filter.addAction(PjActions.EVENT_REGISTRATION_CHANGED);
        filter.addAction(PjActions.EVENT_CALL_RECEIVED);
        filter.addAction(PjActions.EVENT_IN_CALL);
        filter.addAction(PjActions.EVENT_CALL_CHANGED);
        filter.addAction(PjActions.EVENT_CALL_TERMINATED);
        filter.addAction(PjActions.EVENT_CALL_SCREEN_LOCKED);
        filter.addAction(PjActions.EVENT_MESSAGE_RECEIVED);
        filter.addAction(PjActions.EVENT_HANDLED);
        Log.d(TAG, "Adding new event filters" );

        filter.addAction(PjActions.EVENT_INCOMING_CALL_DECLINED);
        filter.addAction(PjActions.EVENT_INCOMING_CALL_ANSWERED);

//        filter.addAction("android.intent.action.MEDIA_BUTTON");

        return filter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, "Received \""+ action +"\" response from service (" + ArgumentUtils.dumpIntentExtraParameters(intent) + ")");
        PjNotificationsManager manager = new PjNotificationsManager(this.context);
        switch (action) {
            case PjActions.EVENT_STARTED:
                onCallback(intent);
                break;
            case PjActions.EVENT_SERVICE_STOPPED:
                onServiceStopped(intent);
                break;
            case PjActions.EVENT_ACCOUNT_CREATED:
                onCallback(intent);
                break;
            case PjActions.EVENT_REGISTRATION_CHANGED:
                onRegistrationChanged(intent);
                break;
            case PjActions.EVENT_MESSAGE_RECEIVED:
                onMessageReceived(intent);
                break;
            case PjActions.EVENT_CALL_RECEIVED:
                onCallReceived(intent);
                break;
            case PjActions.EVENT_IN_CALL:
                onInCall(intent);
                break;
            case PjActions.EVENT_CALL_CHANGED:
                onCallChanged(intent);
                break;
            case PjActions.EVENT_CALL_TERMINATED:
                onCallTerminated(intent);
                break;
            case PjActions.EVENT_INCOMING_CALL_ANSWERED:

                Log.d(TAG, "chinga CALL ANSWERED");
                int answeredCallId = intent.getIntExtra("call_id", 0);

                onCallActionAnswer(answeredCallId);
                manager.stopIncomingCallNotification(answeredCallId);
                break;
            case PjActions.EVENT_INCOMING_CALL_DECLINED:
                int declinedCallId = intent.getIntExtra("call_id", 0);

                Log.d(TAG, "CALL DECLINED");
                onCallActionDecline(declinedCallId);
                manager.stopIncomingCallNotification(declinedCallId);
                break;
            default:
                onCallback(intent);
                break;
        }
    }

    private void onRegistrationChanged(Intent intent) {
        String json = intent.getStringExtra("data");
        Object params = ArgumentUtils.fromJson(json);
        emit("pjSipRegistrationChanged", params);
    }

    private void onMessageReceived(Intent intent) {
        String json = intent.getStringExtra("data");
        Object params = ArgumentUtils.fromJson(json);

        emit("pjSipMessageReceived", params);
    }

    private void onCallReceived(Intent intent) {
        Activity mainActivity = context.getCurrentActivity();
        if(mainActivity != null) {
            Log.d(TAG, "onCallReceived");
            mainActivity.setVolumeControlStream(AudioManager.STREAM_RING);
        }
        String json = intent.getStringExtra("data");
        Object params = ArgumentUtils.fromJson(json);
        emit("pjSipCallReceived", params);
    }

    private void onInCall(Intent intent) {

        Activity mainActivity = context.getCurrentActivity();
        if(mainActivity != null) {
            Log.d(TAG, "onInCall");
            mainActivity.setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }
       //We just use this to change the audio stream
    }

    private void onCallChanged(Intent intent) {
        String json = intent.getStringExtra("data");
        Object params = ArgumentUtils.fromJson(json);
        emit("pjSipCallChanged", params);
    }

    private void onCallTerminated(Intent intent) {
        Activity mainActivity = context.getCurrentActivity();
        if(mainActivity != null) {
            Log.d(TAG, "onCallTerminated");
            mainActivity.setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }
        String json = intent.getStringExtra("data");
        Object params = ArgumentUtils.fromJson(json);
        emit("pjSipCallTerminated", params);
    }
    private void onServiceStopped(Intent intent) {

        emit("pjSipCallServiceStopped", null);
    }
    private void onCallActionAnswer(int callId) {

        emit("pjSipCallAnsweredFromAction", callId);

    }

    private void onCallActionDecline(int callId) {

        emit("pjSipCallDeclinedFromAction", callId);

    }
    private void onCallback(Intent intent) {
        // Define callback
        Callback callback = null;

        if (intent.hasExtra("callback_id")) {
            int id = intent.getIntExtra("callback_id", -1);
            if (callbacks.containsKey(id)) {
                callback = callbacks.remove(id);
            } else {
                Log.w(TAG, "Callback with \""+ id +"\" identifier not found (\""+ intent.getAction() +"\")");
            }
        }

        if (callback == null) {
            return;
        }

        // -----
        if (intent.hasExtra("exception")) {
            Log.w(TAG, "Callback executed with exception state: " + intent.getStringExtra("exception"));
            callback.invoke(false, intent.getStringExtra("exception"));
        } else if (intent.hasExtra("data")) {
            Object params = ArgumentUtils.fromJson(intent.getStringExtra("data"));
            callback.invoke(true, params);
        } else {
            callback.invoke(true, true);
        }
    }

    private void emit(String eventName, @Nullable Object data) {
        Log.d(TAG, "emit " + eventName + " / " + data);

        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, data);
    }
}
