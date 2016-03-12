package com.cs160.joleary.catnip;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, Path: " + messageEvent.getPath());
        Log.d("T", "in WatchListenerService, Data: " + messageEvent.getData());
        //use the 'path' field in sendmessage to differentiate use cases
        //(here, fred vs lexy)

        String path = messageEvent.getPath();
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        Log.d("T", value);

        MainActivity.p = Person.parseMessage(value);
        MainActivity.v = Vote.parseMessage(value);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(getPackageName() + ".data", "sup");
        startActivity(intent);


    }
}