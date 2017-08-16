package lars.benedetto.com.wikimeup;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            SharedPreferences sharedPreferences = context.getSharedPreferences("alarm_data", MODE_PRIVATE);
            sharedPreferences.getInt("hour", 9);
            sharedPreferences.getInt("minute", 0);


        }
    }
}
