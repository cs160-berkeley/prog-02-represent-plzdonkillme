package com.cs160.joleary.catnip;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Created by joleary and noon on 2/19/16 at very late in the night. (early in the morning?)
 */
public class PhoneListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        String path = messageEvent.getPath();
        String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

        Log.d("T", "in PhoneListenerService, got: " + path);
        Log.d("T", "in PhoneListenerService, got: " + value);

        if (path.equals("/data")) {
            Log.d("T", "STARTING ACTIVITY 3");
            Intent intent = new Intent(this, Main3Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(getPackageName() + ".json", value);
            startActivity(intent);

        } else if (path.equals("/shake")) {
            Log.d("T", "STARTING ACTIVITY 2");
            Intent intent = new Intent(this, Main2Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(getPackageName() + ".input", 1);
            Random r = new Random();
            int i1 = r.nextInt(90000) + 10000;
            intent.putExtra(getPackageName() + ".zip", i1 + "");
            startActivity(intent);
        }
    }
}
