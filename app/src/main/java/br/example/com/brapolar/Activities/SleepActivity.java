package br.example.com.brapolar.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.example.com.brapolar.Entities.EntityScreen;
import br.example.com.brapolar.Entities.EntitySleep;
import br.example.com.brapolar.Fragments.ResumenSleepFragment;
import br.example.com.brapolar.Fragments.SingleSleepFragment;
import br.example.com.brapolar.Fragments.TimePickerFragment;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.TimeCount;
import br.example.com.brapolar.ViewPagerAdapter;

public class SleepActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    String pick = "";
    String pickDate = "";
    MySqliteHandler mySqliteHandler;
    Date sleep, awake;
    int SH, SM, AH, AM;
    TextView Tsleep, Tawake, Total;
    int Syear, Smonth, Sday, Ayear, Amonth, Aday;
    int year, month, day;
    DateFormat format;
    SeekBar seekBar;
    private TextView dateView;

    private AppBarLayout appBar;
    private TabLayout tabs;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.sleep_name));
        Bundle extras = getIntent().getExtras();
        Calendar calendar = Calendar.getInstance();
        if (extras != null) {
            year = extras.getInt("year");
            month = extras.getInt("month");
            day = extras.getInt("day");
        } else {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        //Tags Menu
        tabs = (TabLayout) findViewById(R.id.tabs_sleep);
        viewPager = (ViewPager) findViewById(R.id.pager_sleep);
        tabs.setupWithViewPager(viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        SetUpViewPager(viewPager, pagerAdapter);
        //end
        mySqliteHandler = new MySqliteHandler(this);
        format = new SimpleDateFormat(getString(R.string.format2));

    }

    private void Create() {
        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.YEAR, year);
        hoy.set(Calendar.MONTH, month);
        hoy.set(Calendar.DAY_OF_MONTH, day);
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.MILLISECOND, 0);
        EntitySleep ver = new EntitySleep(hoy.getTimeInMillis(), 0, 0, 0);
        int id = mySqliteHandler.ExistSleep(ver);

        StringBuilder a = new StringBuilder();
        if (id == -1) {
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(hoy.getTimeInMillis());
            Syear = c1.get(Calendar.YEAR);
            Smonth = c1.get(Calendar.MONTH);
            Sday = c1.get(Calendar.DAY_OF_MONTH);
            c1.add(Calendar.DAY_OF_MONTH, -1);
            c1.set(Calendar.HOUR_OF_DAY, 21);
            c1.set(Calendar.MINUTE, 0);
            c1.set(Calendar.SECOND, 0);
            c1.set(Calendar.MILLISECOND, 0);
            EntityScreen a1 = mySqliteHandler.getFirstScreenByDate(c1.getTimeInMillis());

            if (a1.getId() != -1) {
                Calendar sC = Calendar.getInstance();
                sC.setTimeInMillis(a1.getDate());
                int day = sC.get(Calendar.DAY_OF_MONTH);
                int hoyDay = hoy.get(Calendar.DAY_OF_MONTH);
                if (day == hoyDay || day - 1 == hoyDay) {
                    sleep = new Date(a1.getDate());
                    a.append(format.format(new Date(a1.getDate())));
                    c1.setTimeInMillis(a1.getDate());
                    Syear = c1.get(Calendar.YEAR);
                    Smonth = c1.get(Calendar.MONTH);
                    Sday = c1.get(Calendar.DAY_OF_MONTH);
                } else {
                    sleep = new Date(hoy.getTimeInMillis());
                    a.append("Não definido");
                }
            } else {
                sleep = new Date();
                a.append("Não definido");
            }
            Tsleep.setText(a);
            a = new StringBuilder();
            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(hoy.getTimeInMillis());
            Ayear = c2.get(Calendar.YEAR);
            Amonth = c2.get(Calendar.MONTH);
            Aday = c2.get(Calendar.DAY_OF_MONTH);
            c2.set(Calendar.HOUR_OF_DAY, 9);
            c2.set(Calendar.MINUTE, 0);
            c2.set(Calendar.SECOND, 0);
            c2.set(Calendar.MILLISECOND, 0);

            EntityScreen a2 = mySqliteHandler.getLastScreenByDate(c2.getTimeInMillis());
            if (a2.getId() != -1) {
                Calendar sC = Calendar.getInstance();
                sC.setTimeInMillis(a1.getDate());
                int day = sC.get(Calendar.DAY_OF_MONTH);
                int hoyDay = hoy.get(Calendar.DAY_OF_MONTH);
                if (day == hoyDay || day - 1 == hoyDay) {
                    awake = new Date(a2.getDate());
                    a.append(format.format(new Date(a2.getDate())));
                    c2.setTimeInMillis(a2.getDate());
                    Ayear = c2.get(Calendar.YEAR);
                    Amonth = c2.get(Calendar.MONTH);
                    Aday = c2.get(Calendar.DAY_OF_MONTH);
                } else {
                    awake = new Date(hoy.getTimeInMillis());
                    a.append("Não definido");
                }
            } else {
                awake = new Date();
                a.append("Não definido");
            }
            Tawake.setText(a);
            a = new StringBuilder();
            if (a1.getId() != -1 && a2.getId() != -1) {
                long total = Math.abs(a2.getDate() - a1.getDate());
                a.append(TimeCount.toLongTime(total / 1000));
            } else {
                a.append("Não definido");
            }
            Total.setText(a);
        } else {
            EntitySleep newSleep = mySqliteHandler.getSleep(id);
            sleep = new Date(newSleep.getSleep());
            awake = new Date(newSleep.getAwake());
            Tsleep.setText(format.format(sleep.getTime()));
            Tawake.setText(format.format(awake.getTime()));
            Total.setText(TimeCount.toLongTime(newSleep.TimeSleep() / 1000));
            seekBar.setProgress((int) (newSleep.getValue()));
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(sleep.getTime());
            Syear = c1.get(Calendar.YEAR);
            Smonth = c1.get(Calendar.MONTH);
            Sday = c1.get(Calendar.DAY_OF_MONTH);
            c1.setTimeInMillis(awake.getTime());
            Ayear = c1.get(Calendar.YEAR);
            Amonth = c1.get(Calendar.MONTH);
            Aday = c1.get(Calendar.DAY_OF_MONTH);
        }

        Calendar sx = Calendar.getInstance();
        sx.setTimeInMillis(sleep.getTime());
        SH = sx.get(Calendar.HOUR_OF_DAY);
        SM = sx.get(Calendar.MINUTE);

        sx.setTimeInMillis(awake.getTime());
        AH = sx.get(Calendar.HOUR_OF_DAY);
        AM = sx.get(Calendar.MINUTE);

//        update();
/*
        a = new StringBuilder();
        a.append("Registro de horas sonho:\n");
        ArrayList<EntitySleep>lista = mySqliteHandler.getAllElementsSleep();
        for(int i=0;i<lista.size();i++){
            a.append(lista.get(i).Date() + " Quantidade de horas de sonho: \n" + TimeCount.toLongTime(lista.get(i).TimeSleep()/1000)+"\n");
            a.append("Qualidade de sonho: "+lista.get(i).getValue()+"\n");
            a.append("-------------------------------------------------------\n");
        }
        list.setText(a);*/
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (pick.equals("sleep")) {
            SH = hourOfDay;
            SM = minute;
        } else if (pick.equals("awake")) {
            AH = hourOfDay;
            AM = minute;
        }
        update();
        pick = "";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void SetAwake(View view) {
        pick = "awake";
        Bundle args = new Bundle();
        Calendar c = Calendar.getInstance();
        if (awake != null)
            c.setTimeInMillis(awake.getTime());
        args.putInt("hour", c.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", c.get(Calendar.MINUTE));
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "time picker");
    }

    public void SetSleep(View view) {
        pick = "sleep";
        Bundle args = new Bundle();
        Calendar c = Calendar.getInstance();
        if (awake != null)
            c.setTimeInMillis(sleep.getTime());
        args.putInt("hour", c.get(Calendar.HOUR_OF_DAY));
        args.putInt("minute", c.get(Calendar.MINUTE));
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), "time picker");
    }

    public void SaveButton(View view) {
        Calendar aw = Calendar.getInstance();
        aw.set(Calendar.DAY_OF_MONTH, Aday);
        aw.set(Calendar.MONTH, Amonth);
        aw.set(Calendar.YEAR, Ayear);
        aw.set(Calendar.HOUR_OF_DAY, AH);
        aw.set(Calendar.MINUTE, AM);
        aw.set(Calendar.SECOND, 0);
        aw.set(Calendar.MILLISECOND, 0);

        Calendar sl = Calendar.getInstance();
        sl.set(Calendar.DAY_OF_MONTH, Sday);
        sl.set(Calendar.MONTH, Smonth);
        sl.set(Calendar.YEAR, Syear);
        sl.set(Calendar.HOUR_OF_DAY, SH);
        sl.set(Calendar.MINUTE, SM);
        sl.set(Calendar.SECOND, 0);
        sl.set(Calendar.MILLISECOND, 0);

        long diff = Math.abs(sl.getTimeInMillis() - aw.getTimeInMillis());

        if (sl.getTimeInMillis() < aw.getTimeInMillis() && diff < 86400000) {
            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.YEAR, year);
            hoy.set(Calendar.MONTH, month);
            hoy.set(Calendar.DAY_OF_MONTH, day);
            hoy.set(Calendar.HOUR_OF_DAY, 0);
            hoy.set(Calendar.SECOND, 0);
            hoy.set(Calendar.MINUTE, 0);
            hoy.set(Calendar.MILLISECOND, 0);
            EntitySleep sleepE = new EntitySleep(hoy.getTimeInMillis(), sl.getTimeInMillis(), aw.getTimeInMillis(), (long) seekBar.getProgress());
            mySqliteHandler.addOrUpdateSleep(sleepE);
            Log.i("fecha add", String.valueOf(hoy.getTimeInMillis()));
            Toast.makeText(this, "Registro salvado", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Datas erradas", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("deprecation")
    public void setDateSleep(View view) {
        pickDate = "sleep";
        showDialog(1);
    }

    @SuppressWarnings("deprecation")
    public void setDateAwake(View view) {
        pickDate = "awake";
        showDialog(2);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        Calendar hoy = Calendar.getInstance();
        hoy.set(Calendar.YEAR, year);
        hoy.set(Calendar.MONTH, month);
        hoy.set(Calendar.DAY_OF_MONTH, day);
        hoy.set(Calendar.HOUR_OF_DAY, 0);
        hoy.set(Calendar.SECOND, 0);
        hoy.set(Calendar.MINUTE, 0);
        hoy.set(Calendar.MILLISECOND, 0);
        if (id == 1) {//sleep
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, Syear, Smonth, Sday);
            datePickerDialog.getDatePicker().setMaxDate(hoy.getTimeInMillis());
            datePickerDialog.getDatePicker().setMinDate(hoy.getTimeInMillis() - 86400000);
            return datePickerDialog;
        }
        if (id == 2) {//awake
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, Ayear, Amonth, Aday);
            datePickerDialog.getDatePicker().setMaxDate(hoy.getTimeInMillis());
            datePickerDialog.getDatePicker().setMinDate(hoy.getTimeInMillis() - 86400000);
            return datePickerDialog;
        }
        if (id == 999) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    if (pickDate.equals("sleep")) {
                        Syear = arg1;
                        Smonth = arg2;
                        Sday = arg3;
                    } else if (pickDate.equals("awake")) {
                        Ayear = arg1;
                        Amonth = arg2;
                        Aday = arg3;
                    } else if (pickDate.equals("day")) {
                        showDate(arg1, arg2, arg3);
                        Create();
                    }
                    pickDate = "";
                    update();
                }
            };

    private void update() {
        Calendar aw = Calendar.getInstance();
        aw.set(Calendar.DAY_OF_MONTH, Aday);
        aw.set(Calendar.MONTH, Amonth);
        aw.set(Calendar.YEAR, Ayear);
        aw.set(Calendar.HOUR_OF_DAY, AH);
        aw.set(Calendar.MINUTE, AM);
        aw.set(Calendar.SECOND, 0);
        aw.set(Calendar.MILLISECOND, 0);

        Calendar sl = Calendar.getInstance();
        sl.set(Calendar.DAY_OF_MONTH, Sday);
        sl.set(Calendar.MONTH, Smonth);
        sl.set(Calendar.YEAR, Syear);
        sl.set(Calendar.HOUR_OF_DAY, SH);
        sl.set(Calendar.MINUTE, SM);
        sl.set(Calendar.SECOND, 0);
        sl.set(Calendar.MILLISECOND, 0);

        long diff = Math.abs(sl.getTimeInMillis() - aw.getTimeInMillis());
       /* if(sl.getTimeInMillis()>aw.getTimeInMillis() || diff>=86400000){
            Toast.makeText(this,"Datas erradas",Toast.LENGTH_LONG).show();
            return;
        }*/
        StringBuilder a = new StringBuilder();
        a.append(format.format(new Date(sl.getTimeInMillis())));
        Tsleep.setText(a.toString());

        a = new StringBuilder();
        a.append(format.format(new Date(aw.getTimeInMillis())));
        Tawake.setText(a.toString());

        Total.setText(TimeCount.toShortTime(diff / 1000));
    }

    private void showDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        StringBuilder date = new StringBuilder().append(day).append("/").append(month + 1).append("/").append(year);
        dateView.setText(date);
    }

    @SuppressWarnings("deprecation")
    public void SetDate(View view) {
        pickDate = "day";
        showDialog(999);
    }

    private void SetUpViewPager(ViewPager view, ViewPagerAdapter adapter) {
        adapter.AddItem(new SingleSleepFragment(), "Single");
        adapter.AddItem(new ResumenSleepFragment(), "Resumen");
        view.setAdapter(adapter);
    }

    public void UpdateElements(View v) {
        Tsleep = (TextView) v.findViewById(R.id.sleepTime);
        Tawake = (TextView) v.findViewById(R.id.awakeTime);
        Total = (TextView) v.findViewById(R.id.TotalTime);
        seekBar = (SeekBar) v.findViewById(R.id.seekBarSleep);
        dateView = (TextView) v.findViewById(R.id.text_date);
        showDate(year, month, day);
        Create();
    }
}
