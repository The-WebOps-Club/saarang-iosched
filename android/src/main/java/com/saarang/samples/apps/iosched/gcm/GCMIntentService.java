/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.saarang.samples.apps.iosched.gcm;

import com.saarang.samples.apps.iosched.Config;
import com.google.android.gcm.GCMBaseIntentService;
import com.saarang.samples.apps.iosched.gcm.command.AnnouncementCommand;
import com.saarang.samples.apps.iosched.gcm.command.SyncCommand;
import com.saarang.samples.apps.iosched.util.AccountUtils;
import com.saarang.samples.apps.iosched.gcm.command.NotificationCommand;
import com.saarang.samples.apps.iosched.gcm.command.SyncUserCommand;
import com.saarang.samples.apps.iosched.gcm.command.TestCommand;
import com.saarang.samples.apps.iosched.util.LogUtils;

import android.content.Context;
import android.content.Intent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.saarang.samples.apps.iosched.util.LogUtils.LOGD;
import static com.saarang.samples.apps.iosched.util.LogUtils.LOGE;
import static com.saarang.samples.apps.iosched.util.LogUtils.LOGI;
import static com.saarang.samples.apps.iosched.util.LogUtils.LOGW;
import static com.saarang.samples.apps.iosched.util.LogUtils.makeLogTag;

/**
 * {@link android.app.IntentService} responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

    private static final String TAG = LogUtils.makeLogTag("GCM");

    private static final Map<String, GCMCommand> MESSAGE_RECEIVERS;
    static {
        // Known messages and their GCM message receivers
        Map <String, GCMCommand> receivers = new HashMap<String, GCMCommand>();
        receivers.put("test", new TestCommand());
        receivers.put("announcement", new AnnouncementCommand());
        receivers.put("sync_schedule", new SyncCommand());
        receivers.put("sync_user", new SyncUserCommand());
        receivers.put("notification", new NotificationCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    public GCMIntentService() {
        super(Config.GCM_SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String regId) {
        LogUtils.LOGI(TAG, "Device registered: regId=" + regId);
        ServerUtilities.register(context, regId, AccountUtils.getPlusProfileId(this));
    }

    @Override
    protected void onUnregistered(Context context, String regId) {
        LogUtils.LOGI(TAG, "Device unregistered");
        if (ServerUtilities.isRegisteredOnServer(context, AccountUtils.getPlusProfileId(this))) {
            ServerUtilities.unregister(context, regId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            LogUtils.LOGD(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        String extraData = intent.getStringExtra("extraData");
        LogUtils.LOGD(TAG, "Got GCM message, action=" + action + ", extraData=" + extraData);

        if (action == null) {
            LogUtils.LOGE(TAG, "Message received without command action");
            return;
        }

        action = action.toLowerCase();
        GCMCommand command = MESSAGE_RECEIVERS.get(action);
        if (command == null) {
            LogUtils.LOGE(TAG, "Unknown command received: " + action);
        } else {
            command.execute(this, action, extraData);
        }

    }

    @Override
    public void onError(Context context, String errorId) {
        LogUtils.LOGE(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        LogUtils.LOGW(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }
}
