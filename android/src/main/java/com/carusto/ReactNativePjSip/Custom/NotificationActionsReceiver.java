package com.carusto.ReactNativePjSip.Custom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.carusto.ReactNativePjSip.PjSipBroadcastEmiter;
import com.carusto.ReactNativePjSip.utils.ArgumentUtils;

public class NotificationActionsReceiver extends BroadcastReceiver {
    private final String TAG = "NotificationActReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        PjSipBroadcastEmiter emitter = new PjSipBroadcastEmiter(context);
        String action = intent.getAction();

        Log.d(TAG, "Received \""+ action +"\" response from notification (" + ArgumentUtils.dumpIntentExtraParameters(intent) + ")");

        switch (action) {
            case Actions.ANSWER_CALL:
                emitter.fireCallAnsweredEvent( intent.getIntExtra("call_id", 0));
                break;
            case Actions.DECLINE_CALL:
                emitter.fireCallDeclinedEvent( intent.getIntExtra("call_id", 0));
                break;
        }
    }

    public  class Actions {
        public static final String ANSWER_CALL = "com.moises.pjsip.notification.call.answer";
        public static final String DECLINE_CALL = "com.moises.pjsip.notification.call.decline";
    }
}


