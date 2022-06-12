package br.example.com.brapolar.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import br.example.com.brapolar.AlarmReceiverMedication;
import br.example.com.brapolar.Entities.EntityMedication;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReviewMedicationActivity extends AppCompatActivity {
    MySqliteHandler mySqliteHandler;
    EntityMedication curr;
    TextView alarm;
    List<Long> dates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_medication);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.medication_name));
        mySqliteHandler = new MySqliteHandler(this);
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            curr = (EntityMedication) extras.getSerializable("EntityMedication");
        }
        alarm = (TextView) findViewById(R.id.alarm);
        initData();
        TextView name = (TextView) findViewById(R.id.name);
        name.setText(curr.getName() + " " + curr.getUnit() + " " + curr.getDose());
        TextView unit = (TextView) findViewById(R.id.unit);
        unit.setText("Cada " + curr.getFrequency() + "h, " + curr.getQuantity() + " Vezes");
    }

    private void initData() {
        int cant = curr.getQuantity();
        Calendar x = Calendar.getInstance();
        x.setTimeInMillis(curr.getDate());
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < cant; i++) {
            List<EntityMedication> AtTimeI = new ArrayList<>();
            long total = x.getTimeInMillis();
            dates.add(total);
            SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm");
            String dateString = formater.format(total);
            text.append(dateString).append("\n");
            x.add(Calendar.HOUR_OF_DAY, curr.getFrequency());
        }
        alarm.setText(text.toString());
    }

    public void edit(View view) {
        Intent edit = new Intent(this, MedicationActivity.class);
        edit.putExtra("EntityMedication", curr);
        finish();
        startActivity(edit);
    }

    public void MakeAlarm(int id, long time) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent(this, AlarmReceiverMedication.class);
        Bundle b = new Bundle();
        b.putSerializable("EntityMedication", curr);
        b.putLong("time", time);
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("DATA", b);

        PendingIntent broadcast = PendingIntent.getBroadcast(this, id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        if (cal.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
        Log.i("alarma set", cal.get(Calendar.HOUR_OF_DAY) + " " + cal.get(Calendar.MINUTE) + " " + cal.get(Calendar.DAY_OF_MONTH));
    }

    private void MakeAlarms(EntityMedication add) {
        for (int i = 0; i < dates.size(); i++) {
            int id = (add.getId() + 1) * 500 + i;
            MakeAlarm(id, dates.get(i));
        }
    }

    public void ok(View view) {
        mySqliteHandler.addMedication(curr);
        EntityMedication add = mySqliteHandler.getMedication(curr.getDate());
        MakeAlarms(add);
        finish();
        startActivity(new Intent(this, MedicationActivity.class));
        Toast.makeText(this, R.string.medication_add, Toast.LENGTH_SHORT).show();
    }
}
