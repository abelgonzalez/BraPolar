package br.example.com.brapolar.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.example.com.brapolar.Entities.EntitySetting;
import br.example.com.brapolar.Fragments.TimePickerFragment;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    MySqliteHandler mySqliteHandler;
    Switch sTrackCall;
    Switch sTrackMsg;
    Switch sTrackSig;
    Switch sTrackApp;
    Switch sTrackAccelerometer;
    Switch sTrackLight;
    Button calibrateBtn;
    List<EntitySetting> settings;
    String pick = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);
        sTrackCall = (Switch) findViewById(R.id.track_call);
        sTrackMsg = (Switch) findViewById(R.id.track_msg);
        sTrackSig = (Switch) findViewById(R.id.track_sig);
        sTrackApp = (Switch) findViewById(R.id.track_app);
        sTrackAccelerometer = (Switch) findViewById(R.id.track_accelerometer);
        sTrackLight = (Switch) findViewById(R.id.track_light);

        sTrackCall.setOnCheckedChangeListener(this);
        sTrackMsg.setOnCheckedChangeListener(this);
        sTrackSig.setOnCheckedChangeListener(this);
        sTrackApp.setOnCheckedChangeListener(this);
        sTrackAccelerometer.setOnCheckedChangeListener(this);
        sTrackLight.setOnCheckedChangeListener(this);

        calibrateBtn = (Button) findViewById(R.id.calibrate_keyboard_btn);
        calibrateBtn.setOnClickListener(this);

        mySqliteHandler = new MySqliteHandler(this);
        settings = mySqliteHandler.getAllElementsSetting();
        UpdateElemntsFromBd();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdateElemntsFromBd() {
        settings = mySqliteHandler.getAllElementsSetting();
        sTrackCall.setChecked(settings.get(0).isEnable());
        sTrackMsg.setChecked(settings.get(1).isEnable());
        sTrackSig.setChecked(settings.get(2).isEnable());
        sTrackApp.setChecked(settings.get(3).isEnable());
        sTrackAccelerometer.setChecked(settings.get(4).isEnable());
        sTrackLight.setChecked(settings.get(5).isEnable());
    }

    private void UpdateElementFromView(int id, boolean value) {
        EntitySetting update = settings.get(id);
        update.setValue(value ? 1 : 0);
        mySqliteHandler.UpdateSetting(update);
        settings = mySqliteHandler.getAllElementsSetting();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.track_call:
                UpdateElementFromView(0, isChecked);
                break;
            case R.id.track_msg:
                UpdateElementFromView(1, isChecked);
                break;
            case R.id.track_sig:
                UpdateElementFromView(2, isChecked);
                break;
            case R.id.track_app:
                UpdateElementFromView(3, isChecked);
                break;
            case R.id.track_accelerometer:
                UpdateElementFromView(4, isChecked);
                break;
            case R.id.track_light:
                UpdateElementFromView(5, isChecked);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.calibrate_keyboard_btn) {
            Log.i("msg", "enter to Calibration Activity");
            startActivity(new Intent(this, CalibrationActivity.class));
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (pick.equals("mood")) {
            mySqliteHandler.UpdateSetting(new EntitySetting(7, "time_alarm", hourOfDay * 60 + minute));
            MakeAlarm();
            Toast.makeText(this, getString(R.string.option_alarmMood), Toast.LENGTH_LONG).show();
        } else if (pick.equals("sleep")) {
            mySqliteHandler.UpdateSetting(new EntitySetting(8, "time_alarm_sleep", hourOfDay * 60 + minute));
            MakeSleepAlarm();
            Toast.makeText(this, getString(R.string.option_alarmSleep), Toast.LENGTH_LONG).show();
        }
        pick = "";
    }

    public void MakeAlarm() {
        ArrayList<EntitySetting> settings = mySqliteHandler.getAllElementsSetting();
        int valAlarm = settings.get(6).getValue();
        int h = valAlarm / 60;
        int m = valAlarm % 60;
        int s = 0;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("type", "mood");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        cal.set(Calendar.SECOND, s);
        if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24 * 60 * 60 * 1000, broadcast);
        Log.i("alarma set", cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.DAY_OF_MONTH));
    }

    public void MakeSleepAlarm() {
        ArrayList<EntitySetting> settings = mySqliteHandler.getAllElementsSetting();
        int valAlarm = settings.get(7).getValue();
        int h = valAlarm / 60;
        int m = valAlarm % 60;
        int s = 0;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("type", "sleep");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        cal.set(Calendar.SECOND, s);
        if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 24 * 60 * 60 * 1000, broadcast);
        Log.i("alarma set", cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.DAY_OF_MONTH));
    }

    public void TimePicker(View view) {
        pick = "mood";
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }

    public void TimePickerSleep(View view) {
        pick = "sleep";
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
    }
}
