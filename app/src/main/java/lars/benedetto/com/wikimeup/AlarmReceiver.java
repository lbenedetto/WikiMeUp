package lars.benedetto.com.wikimeup;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class AlarmReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("lars.benedetto.com.wikimeup.alarmTrigger")) {
            Intent newIntent = new Intent(context, ReadArticleActivty.class);
            context.startActivity(newIntent);
        }
    }
}
