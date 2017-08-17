package lars.benedetto.com.wikimeup;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    public static final int SUNDAY = 0;
    public static final int MONDAY = 1;
    public static final int TUESDAY = 2;
    public static final int WEDNESDAY = 3;
    public static final int THURSDAY = 4;
    public static final int FRIDAY = 5;
    public static final int SATURDAY = 6;

    private int[] daysOfWeek = {
            Calendar.SUNDAY,
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY,
            Calendar.SATURDAY
    };
    public static PendingIntent[] pendingIntents = new PendingIntent[7];
    private TextView textViewTime;
    private Activity context = this;
    private ImageCheckBox[] boxes;
    private boolean[] repeatDay = new boolean[7];
    private AlarmManager alarmManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boxes = new ImageCheckBox[]{
                (ImageCheckBox) findViewById(R.id.checkBoxSunday),
                (ImageCheckBox) findViewById(R.id.checkBoxMonday),
                (ImageCheckBox) findViewById(R.id.checkBoxTuesday),
                (ImageCheckBox) findViewById(R.id.checkBoxWednesday),
                (ImageCheckBox) findViewById(R.id.checkBoxThursday),
                (ImageCheckBox) findViewById(R.id.checkBoxFriday),
                (ImageCheckBox) findViewById(R.id.checkBoxSaturday)
        };
        sharedPreferences = getSharedPreferences("alarm_data", MODE_PRIVATE);
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        updateRepeatDays();

        setBoxClickListeners();

        final Switch switchOnOff = (Switch) findViewById(R.id.switchEnable);
        textViewTime = (TextView) findViewById(R.id.textViewTime);
        setTextViewTime();
        textViewTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog dialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("hour", hour);
                        editor.putInt("minute", minute);
                        editor.apply();
                        setTextViewTime(hour, minute);
                        if (switchOnOff.isChecked())
                            setAlarm(hour, minute);
                    }
                }, hour, minute, DateFormat.is24HourFormat(context));
                dialog.setTitle("Select Alarm Time");
                dialog.show();
            }
        });


        switchOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchOnOff.isChecked()) {
                    int hour = sharedPreferences.getInt("hour", 9);
                    int minute = sharedPreferences.getInt("minute", 0);
                    setAlarm(hour, minute);
                } else {
                    cancelAlarm();
                }
            }
        });

        Button doNow = (Button) findViewById(R.id.button);
        doNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis() + 2000);
                setAlarm(calendar, 0);
                Toast.makeText(context, "Loading...", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTextViewTime() {
        int hour = sharedPreferences.getInt("hour", 9);
        int minute = sharedPreferences.getInt("minute", 0);
        setTextViewTime(hour, minute);
    }

    private void setTextViewTime(int hour, int minute) {
        boolean is24Hour = DateFormat.is24HourFormat(context);
        String time;
        if (is24Hour) {
            time = String.format(Locale.ENGLISH, "%d:%02d", hour, minute);
        } else {
            time = String.format(Locale.ENGLISH, "%d:%02d %s", hour % 12, minute, hour > 12 ? "PM" : "AM");
        }
        textViewTime.setText(time);
    }

    private void setBoxClickListeners() {
        for (int i = 0; i < 7; i++) {
            setBoxClickListener(i);
        }
    }

    private void setBoxClickListener(final int i) {
        boxes[i].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = boxes[i].isChecked();
                repeatDay[i] = isChecked;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("Day" + i, isChecked);
                editor.apply();
            }
        });
    }

    private void updateRepeatDays() {
        for (int i = 0; i < 7; i++) {
            boolean isRepeatDay = sharedPreferences.getBoolean("Day" + i, false);
            repeatDay[i] = isRepeatDay;
            boxes[i].setChecked(isRepeatDay);
        }
    }

    private long setAlarm(Calendar calendar, int alarmCode) {
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("lars.benedetto.com.wikimeup.alarmTrigger");
        pendingIntents[alarmCode] = PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntents[alarmCode]);
        return (calendar.getTimeInMillis() - System.currentTimeMillis()) / 1000;//seconds

    }

    private void setAlarm(int hour, int minute) {
        enableBootReceiver();
        Calendar calendar = Calendar.getInstance();
        long seconds = Long.MAX_VALUE;
        for (int alarmCode = 0; alarmCode < 7; alarmCode++) {
            if (repeatDay[alarmCode]) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.DAY_OF_WEEK, daysOfWeek[alarmCode]);
                // Check we aren't setting it in the past which would trigger it to fire instantly
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_YEAR, 7);
                }
                long sec = setAlarm(calendar, alarmCode);
                if (sec < seconds) seconds = sec;
            }
        }
        long minutes = seconds / 60;
        seconds = seconds % 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        long days = hours / 24;
        hours = hours % 24;
        Toast.makeText(this, String.format(Locale.ENGLISH, "Alarm set to go off in T-%02d:%02d:%02d:%02ds",days,hours,minutes,seconds), Toast.LENGTH_LONG).show();

    }

    private void cancelAlarm() {
        disableBootReceiver();
        if (alarmManager == null) return;
        for (int i = 0; i < 7; i++) {
            PendingIntent intent = pendingIntents[i];
            if (intent == null) return;
            alarmManager.cancel(pendingIntents[i]);
        }
    }

    private void disableBootReceiver() {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void enableBootReceiver() {
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

}
