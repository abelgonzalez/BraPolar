package br.example.com.brapolar;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.NonNull;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.cardview.widget.CardView;

import android.util.Log;

import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import br.example.com.brapolar.Activities.AboutActivity;
import br.example.com.brapolar.Activities.AcousticActivity;
import br.example.com.brapolar.Activities.ContactActivity;
import br.example.com.brapolar.Activities.MedicationActivity;
import br.example.com.brapolar.Activities.MoodActivity;
import br.example.com.brapolar.Activities.PhysicalActivity;
import br.example.com.brapolar.Activities.PsychomotorActivity;
import br.example.com.brapolar.Activities.SendActivity;
import br.example.com.brapolar.Activities.SettingsActivity;
import br.example.com.brapolar.Activities.SleepActivity;
import br.example.com.brapolar.Activities.SocialActivity;
import br.example.com.brapolar.Entities.EntityCall;
import br.example.com.brapolar.Entities.EntityInput;
import br.example.com.brapolar.Entities.EntityInputText;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.Entities.EntityScreen;
import br.example.com.brapolar.Entities.EntitySetting;
import br.example.com.brapolar.Entities.EntitySleep;

import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.Fragments.SmsFragment;
import br.example.com.brapolar.Utils.TimeCount;
import br.example.com.brapolar.Utils.UserSingleton;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    ScreenStateReceiver mScreenStateReceiver;
    private AppBarLayout appBar;
    private TabLayout tabs;
    ViewPager viewPager;
    private TextView dateView;
    private int year, month, day;
    AlarmManager alarmManager;
    Intent alarmIntent;
    PendingIntent pendingIntent;
    Boolean face = true;

    MySqliteHandler mySqliteHandler;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    private void inicializateFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inicializateFirebase(); // TODO: Valorar si vale la pena dejar esto acá  para usarlo al capturar los datos o bien pasarlo para la clase MyFirebaseApp
        mAuth = FirebaseAuth.getInstance();

        String a = getString(R.string.username);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (UStats.getUsageStatsList(this).isEmpty()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        getSupportActionBar().setTitle(getString(R.string.app_name));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ContentResolver contentResolver = getContentResolver();
        contentResolver.registerContentObserver(Uri.parse("content://sms"), true, new SMSObserver(new Handler(), this));
        mySqliteHandler = new MySqliteHandler(MainActivity.this);

        //code screen resiver registry
        mScreenStateReceiver = new ScreenStateReceiver();
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        screenStateFilter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
        //code EntityTower timer
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            TaskCelda();
        }
        dateView = (TextView) findViewById(R.id.text_date);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month, day);

        TextView socialText = (TextView) findViewById(R.id.textView4);
        TextView physicalText = (TextView) findViewById(R.id.textView5);
        TextView psychomotorText = (TextView) findViewById(R.id.textView6);
        TextView sleepText = (TextView) findViewById(R.id.textView7);
        TextView medicationText = (TextView) findViewById(R.id.textView8);
        TextView acousticText = (TextView) findViewById(R.id.textView9);
        CardView Cardsocial = (CardView) findViewById(R.id.CardSocial);
        CardView Cardphysical = (CardView) findViewById(R.id.CardPhysical);
        CardView Cardpsychomotor = (CardView) findViewById(R.id.CardPsychomotor);
        CardView Cardacustic = (CardView) findViewById(R.id.CardAcustic);
        CardView Cardmedication = (CardView) findViewById(R.id.CardMedication);
        CardView Cardsleep = (CardView) findViewById(R.id.CardSleep);
        socialText.setOnClickListener(this);
        physicalText.setOnClickListener(this);
        psychomotorText.setOnClickListener(this);
        sleepText.setOnClickListener(this);
        medicationText.setOnClickListener(this);
        acousticText.setOnClickListener(this);
        Cardsocial.setOnClickListener(this);
        Cardphysical.setOnClickListener(this);
        Cardpsychomotor.setOnClickListener(this);
        Cardacustic.setOnClickListener(this);
        Cardmedication.setOnClickListener(this);
        Cardsleep.setOnClickListener(this);

        RefreshCall();
        RefreshSMS();
        RefreshTyping();
        RefreshTower();
        RefreshScreen();
        UpdateQuality();
        MakeSleepAlarm();
        UpdateFace();
        //inicializateSeekBar();

        MakeAlarm();
        EntityScreen aa = mySqliteHandler.getFirstScreenByDate(System.currentTimeMillis() - 1000 * 60 * 60 * 5);
        Date dt = new Date(aa.getDate());
        //int x = 10;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //showDate(year, month, day);
        RefreshCall();
        RefreshSMS();
        RefreshTyping();
        RefreshTower();
        RefreshScreen();
        UpdateQuality();
        UpdateFace();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.i("msg", "enter to settings");
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_about) {
            Log.i("msg", "enter to about");
            startActivity(new Intent(this, AboutActivity.class));
        } else if (id == R.id.action_contact) {
            Log.i("msg", "enter to contact");
            startActivity(new Intent(this, ContactActivity.class));
        } else if (id == R.id.action_logout) {
            mAuth.signOut(); // Cerrar sesión
            Log.i("msg", "logout");
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        TextView tx = (TextView) findViewById(R.id.textView);

        if (id == R.id.mood) {
            Log.i("msg", "enter to Mood State Activity");
            Intent intent = new Intent(this, MoodActivity.class);
            intent.putExtra("year", year);
            intent.putExtra("month", month);
            intent.putExtra("day", day);
            startActivity(intent);
        } else if (id == R.id.social) {
            Log.i("msg", "enter to Social Activity");
            startActivity(new Intent(this, SocialActivity.class));
        } else if (id == R.id.physical) {
            Log.i("msg", "enter to Physical Activity");
            startActivity(new Intent(this, PhysicalActivity.class));
        } else if (id == R.id.psychomotor) {
            Log.i("msg", "enter to Pshychomotor Activity");
            startActivity(new Intent(this, PsychomotorActivity.class));
        } else if (id == R.id.sleep) {
            ShowSleepActivity();
        } else if (id == R.id.medication) {
            Log.i("msg", "enter to Medication Activity");
            startActivity(new Intent(this, MedicationActivity.class));
        } else if (id == R.id.acustic) {
            Log.i("msg", "enter to Acustic Activity");
            startActivity(new Intent(this, AcousticActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        TaskCelda();
                    }
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                startService(new Intent(getApplicationContext(), CallLogService.class));
                            }
                        };
                        thread.start();
                    }
                }
        }
    }

    private void TaskCelda() {
        Timer timer = new Timer();
        final Handler handler = new Handler();
        final CellResiver cellResiver = new CellResiver(this);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                AsyncTask<Object, Void, String> mytask = new AsyncTask<Object, Void, String>() {
                    @Override
                    protected String doInBackground(Object[] objects) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                cellResiver.Task();
                            }
                        });
                        return null;
                    }
                };
                mytask.execute();
            }
        };
        timer.schedule(task, 0, 3000);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.textView4 || id == R.id.CardSocial) {
            Log.i("msg", "enter to Social Activity");
            startActivity(new Intent(this, SocialActivity.class));
        } else if (id == R.id.textView5 || id == R.id.CardPhysical) {
            Log.i("msg", "enter to Physical Activity");
            startActivity(new Intent(this, PhysicalActivity.class));
        } else if (id == R.id.textView6 || id == R.id.CardPsychomotor) {
            Log.i("msg", "enter to Pshychomotor Activity");
            startActivity(new Intent(this, PsychomotorActivity.class));
        } else if (id == R.id.textView7 || id == R.id.CardSleep) {
            ShowSleepActivity();
        } else if (id == R.id.textView8 || id == R.id.CardMedication) {
            Log.i("msg", "enter to Medication Activity");
            startActivity(new Intent(this, MedicationActivity.class));
        } else if (id == R.id.textView9 || id == R.id.CardAcustic) {
            Log.i("msg", "enter to Acoustic Activity");
            startActivity(new Intent(this, AcousticActivity.class));
        }
    }

    private void RefreshCall() {
        final TextView inCall = (TextView) findViewById(R.id.total_in);
        final TextView outCall = (TextView) findViewById(R.id.total_out);
        final TextView missedCall = (TextView) findViewById(R.id.total_call_missed);

        final int[] inCallNumber = {0};
        final int[] outCallNumber = {0};
        final int[] missedCallNumber = {0};

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("Call")
                .equalTo(user)
                .orderByChild("user");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                inCallNumber[0] = 0;
                outCallNumber[0] = 0;
                missedCallNumber[0] = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntityCall models = snapshot.getValue(EntityCall.class);
                    assert models != null;

                    if (models.getType() != null) {

                        if (models.getType().equals("Entrante")) {
                            inCallNumber[0] += 1;
                        }
                        if (models.getType().equals("Sainte")) {
                            outCallNumber[0] += 1;
                        }

                        if (models.getType().equals("Perdida")) {
                            missedCallNumber[0] += 1;
                        }
                    }
                    // String value = dataSnapshot.getValue(String.class);
                    //Log.d(TAG, "Value is: " + value);
                }
                inCall.setText(String.valueOf(inCallNumber[0]));
                outCall.setText(String.valueOf(outCallNumber[0]));
                missedCall.setText(String.valueOf(missedCallNumber[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        /*
        inCall.setText(String.valueOf(mySqliteHandler.getCountCall(getString(R.string.call_incoming_sing))));
        outCall.setText(String.valueOf(mySqliteHandler.getCountCall(getString(R.string.call_outgoing_sing))));
        missedCall.setText(String.valueOf(mySqliteHandler.getCountCall(getString(R.string.call_missed_sing))));

        */

    }

    private void RefreshSMS() {
        final TextView inSms = (TextView) findViewById(R.id.total_in_sms);
        final TextView outSms = (TextView) findViewById(R.id.total_out_sms);

        final int[] inSMSNumber = {0};
        final int[] outSMSNumber = {0};

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("Message")
                .equalTo(user)
                .orderByChild("user");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                inSMSNumber[0] = 0;
                outSMSNumber[0] = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntitySms models = snapshot.getValue(EntitySms.class);

                    assert models != null;
                    if (models.getType().equals("Entrante")) {
                        inSMSNumber[0] += 1;
                    }
                    if (models.getType().equals("Sainte")) {
                        outSMSNumber[0] += 1;
                    }
                    // String value = dataSnapshot.getValue(String.class);
                    //Log.d(TAG, "Value is: " + value);
                }
                inSms.setText(String.valueOf(inSMSNumber[0]));
                outSms.setText(String.valueOf(outSMSNumber[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //inSms.setText(String.valueOf(mySqliteHandler.getCountSMS(getString(R.string.call_incoming_sing))));
        //outSms.setText(String.valueOf(mySqliteHandler.getCountSMS(getString(R.string.call_outgoing_sing))));

    }

    private void RefreshTower() {
        final TextView cellTower = (TextView) findViewById(R.id.cell_tower);
        final int[] cellTowerNumber = {0};

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("Tower")
                .equalTo(user)
                .orderByChild("user");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                cellTowerNumber[0] = (int) (long) dataSnapshot.getChildrenCount(); // todas las torres

                cellTower.setText(String.valueOf(cellTowerNumber[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        //cellTower.setText(String.valueOf(mySqliteHandler.getAllUniqueTower().size()));
    }

    private void RefreshScreen() {
        final TextView screenOn = (TextView) findViewById(R.id.screen_on);
        final TextView screenOff = (TextView) findViewById(R.id.screen_off);

        final int[] screenOnNumber = {0};
        final int[] screenOffNumber = {0};

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("Screen")
                .equalTo(user)
                .orderByChild("user");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                screenOnNumber[0] = 0;
                screenOffNumber[0] = 0;

                List<EntityScreen> screens = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EntityScreen models = snapshot.getValue(EntityScreen.class);
                    screens.add(models);
                }

                // old one but functional :/
                int on = 0, off = 0, lock = 0;
                if (screens.size() > 0) {
                    String current = screens.get(0).getType();
                    EntityScreen last = screens.get(0);
                    try {
                        for (int i = 1; i < screens.size(); i++) {
                            if (current.equals("on") && last.getIslock() == 1) {
                                if (screens.get(i).getType().equals("off")) {//onLock para off
                                    Date lx = new Date(last.getDate());
                                    Date fina = new Date(screens.get(i).getDate());
                                    long time = (fina.getTime() - lx.getTime());
                                    Log.i("t", String.valueOf(time));
                                    on += time;
                                    lock += time;
                                } else if (screens.get(i).getType().equals("on") && screens.get(i).getIslock() == 0) {//onLock para onunlock
                                    Date lx = new Date(last.getDate());
                                    Date fina = new Date(screens.get(i).getDate());
                                    long time = (fina.getTime() - lx.getTime());
                                    on += time;
                                    lock += time;
                                    Log.i("t", String.valueOf(time));
                                }
                            } else if (current.equals("on") && last.getIslock() == 0) {
                                if (screens.get(i).getType().equals("off")) {//onLock para off
                                    Date lx = new Date(last.getDate());
                                    Date fina = new Date(screens.get(i).getDate());
                                    long time = (fina.getTime() - lx.getTime());
                                    on += time;
                                    Log.i("t", String.valueOf(time));
                                }
                            } else if (current.equals("off")) {//off para on
                                if (screens.get(i).getType().equals("on")) {//off++
                                    Date lx = new Date(last.getDate());
                                    Date fina = new Date(screens.get(i).getDate());
                                    long time = (fina.getTime() - lx.getTime());
                                    off += time;
                                    Log.i("t", String.valueOf(time));
                                }
                            }
                            last = screens.get(i);
                            current = screens.get(i).getType();
                        }
                    } catch (Exception e) {
                        Log.i("errror", "error parse date");
                        //Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
                    }
                }
                screenOnNumber[0] = on;
                screenOffNumber[0] = off;

                screenOn.setText(TimeCount.toShortTime(screenOnNumber[0] / 1000));
                screenOff.setText(TimeCount.toShortTime(screenOffNumber[0] / 1000));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void inicializateSeekBar() {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                UpdateFace(progress);

            }
        });
    }

    private void RefreshTyping() {
        TextView speed = (TextView) findViewById(R.id.speed);
        TextView word = (TextView) findViewById(R.id.word);
        TextView wrong = (TextView) findViewById(R.id.wrong);
        List<EntityInputText> texts = EntityInput.Fragmentado(this);
        double totalTime = 0;
        long cantWord = 0;
        long errors = 0;
        for (int i = 0; i < texts.size(); i++) {
            totalTime += texts.get(i).getTime(this);
            cantWord += texts.get(i).getCount();
            errors += texts.get(i).getError();
        }
        double sx = EntityInputText.UtilToWPM(totalTime, cantWord / 5);
        String speedText = (int) Math.floor(sx) + getString(R.string.input_speed_unit);
        String words = String.valueOf(cantWord / 5 + (cantWord % 5 == 0 ? 0 : 1));
        speed.setText(speedText);
        word.setText(words);
        wrong.setText(String.valueOf(errors));
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
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
                    showDate(arg1, arg2 + 1, arg3);
                    UpdateQuality();
                }
            };

    private void showDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        StringBuilder date = new StringBuilder().append(day).append("/").append(month).append("/").append(year);
        dateView.setText(date);
        if (mySqliteHandler != null) {
//            SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar);
            int val = mySqliteHandler.getValueMoodStateByDate(date.toString());

            if (val == -1) {
                val = 100;
                face = false;
            }
//                seekBar.setProgress(val);
            //UpdateFace(val); TODO: Aca era para actualizar la cara en funcion de la fecha seleccionada
            face = true;
        }
    }

    public class EntityMoodDateComparator implements Comparator<EntityMoodState> {
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

        @Override
        public int compare(EntityMoodState o1, EntityMoodState o2) {
            // Order ascending.
            try {
                return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    private void UpdateFace() {

        final int[] moodPercentNumber = {0};

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        final ArrayList<EntityMoodState> arrLst = new ArrayList<>();

        Query query = databaseReference.child("Mood")
                .equalTo(user)
                .orderByChild("user");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntityMoodState models = snapshot.getValue(EntityMoodState.class);
                    assert models != null;
                    arrLst.add(models);

                    // String value = dataSnapshot.getValue(String.class);
                    //Log.d(TAG, "Value is: " + value);
                }

                Collections.sort(arrLst, new EntityMoodDateComparator());
                if (arrLst.size() >= 1) {
                    moodPercentNumber[0] = arrLst.get(arrLst.size() - 1).getValue();
                }
                UpdateFace(moodPercentNumber[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void UpdateFace(int progress) {
        ImageView p1 = (ImageView) findViewById(R.id.imageView1);
        ImageView p2 = (ImageView) findViewById(R.id.imageView2);
        ImageView p3 = (ImageView) findViewById(R.id.imageView3);
        ImageView p4 = (ImageView) findViewById(R.id.imageView4);
        ImageView p5 = (ImageView) findViewById(R.id.imageView5);
        p1.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_1));
        p2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_2));
        p3.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_3));
        p4.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_4));
        p5.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_5));
        if (face) {
            if (progress >= 0 && progress < 20) {
                p1.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_1_negative));
                p2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_2));
                p3.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_3));
                p4.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_4));
                p5.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_5));
            }
            if (progress >= 20 && progress < 40) {
                p1.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_1));
                p2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_2_negative));
                p3.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_3));
                p4.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_4));
                p5.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_5));
            }
            if (progress >= 40 && progress < 60) {
                p1.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_1));
                p2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_2));
                p3.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_3_negative));
                p4.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_4));
                p5.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_5));
            }
            if (progress >= 60 && progress < 80) {
                p1.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_1));
                p2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_2));
                p3.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_3));
                p4.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_4_negative));
                p5.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_5));
            }
            if (progress >= 80 && progress <= 100) {
                p1.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_1));
                p2.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_2));
                p3.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_3));
                p4.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_4));
                p5.setImageDrawable(getResources().getDrawable(R.drawable.ic_mood_5_negative));
            }
        }
    }

    public void UpdateQuality() {
        DateFormat format = new SimpleDateFormat(getString(R.string.format));
//        TextView textSleep = (TextView)findViewById(R.id.textSleep);
//        TextView textAwake = (TextView)findViewById(R.id.textAwake);
        TextView quantity = (TextView) findViewById(R.id.textQuantity);
        TextView quality = (TextView) findViewById(R.id.textQualidade);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.MILLISECOND, 0);

        EntitySleep a = mySqliteHandler.getSleepByDate(c.getTimeInMillis());
        if (a.getId() != -1) {
//        textSleep.setText(String.valueOf(format.format(new Date(a.getSleep()))));
//        textAwake.setText(String.valueOf(format.format(new Date(a.getAwake()))));
            quantity.setText(TimeCount.toShortTime(a.TimeSleep() / 1000));
            quality.setText(String.valueOf((int) a.getValue()));
        }
        Log.i("fecha query", String.valueOf(c.getTimeInMillis()));
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

    public void ShowMoodActivity(View view) {
        Log.i("msg", "enter to Mood State Activity");
        Intent intent = new Intent(this, MoodActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        startActivity(intent);
    }

    public void ShowSleepActivity() {
        Log.i("msg", "enter to Sleep State Activity");
        Intent intent = new Intent(this, SleepActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("month", month - 1);
        intent.putExtra("day", day);
        startActivity(intent);
    }

    public void MakeSleepAlarm() {
        ArrayList<EntitySetting> settings = mySqliteHandler.getAllElementsSetting();
        int valAlarm = settings.get(7).getValue();
        int h = valAlarm / 60;
        int m = valAlarm % 60;
        int s = 0;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.putExtra("type", "sleep");
        notificationIntent.addCategory("android.intent.category.DEFAULT");

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

    public void SendActivity(MenuItem item) {
        startActivity(new Intent(MainActivity.this, SendActivity.class));
    }
}
