package com.example.leon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentOfLongRunningService = new Intent(context, LongRunningService.class);
        context.startService(intentOfLongRunningService);
    }
}
