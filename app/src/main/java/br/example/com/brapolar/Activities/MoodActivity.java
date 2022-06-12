package br.example.com.brapolar.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.MainActivity;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.UserSingleton;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.UUID;

public class MoodActivity extends AppCompatActivity {
    private TextView dateView;
    MySqliteHandler mySqliteHandler;
    private int year, month, day;
    Boolean face = true;

    private DatabaseReference databaseReference; // Firebase

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mood);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.mood_name));

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
                moodPercentNumber[0] = arrLst.get(arrLst.size() - 1).getValue();
                UpdateFace(moodPercentNumber[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                //Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mySqliteHandler = new MySqliteHandler(this);

        dateView = (TextView) findViewById(R.id.text_date);
        Calendar calendar = Calendar.getInstance();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            year = extras.getInt("year");
            month = extras.getInt("month");
            day = extras.getInt("day");
        } else {
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        showDate(year, month, day);
        inicializateSeekBar();

        inicializateFirebase(); // Inicializar Firebase
    }

    void inicializateFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

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
                }
            };

    private void showDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        StringBuilder date = new StringBuilder().append(day).append("/").append(month).append("/").append(year);
        dateView.setText(date);
        if (mySqliteHandler != null) {
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
            int val = mySqliteHandler.getValueMoodStateByDate(date.toString());

            if (val == -1) {
                val = 100;
                face = false;
            }
            seekBar.setProgress(val);
            UpdateFace(val);
            face = true;
        }
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

    public void ProceessMood(View view) {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        StringBuilder date = new StringBuilder().append(day).append("/").append(month).append("/").append(year);
        EntityMoodState mood = new EntityMoodState(seekBar.getProgress(), date.toString());
        mySqliteHandler.addOrUpdateMoodState(mood);

        FirebaseSave(mood); // Salvar en Firebase

        Toast.makeText(this, getString(R.string.save_mood_state), Toast.LENGTH_LONG).show();
    }

    void FirebaseSave(EntityMoodState instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Mood").child(String.valueOf(instance.getUid())).setValue(instance);
    }
}
