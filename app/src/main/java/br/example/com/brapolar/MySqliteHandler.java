package br.example.com.brapolar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import br.example.com.brapolar.Entities.EntityCalibration;
import br.example.com.brapolar.Entities.EntityCall;
import br.example.com.brapolar.Entities.EntityInput;
import br.example.com.brapolar.Entities.EntityMedication;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.Entities.EntityScreen;
import br.example.com.brapolar.Entities.EntitySetting;
import br.example.com.brapolar.Entities.EntitySleep;
import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.Fragments.SmsFragment;
import br.example.com.brapolar.Utils.UserSingleton;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MySqliteHandler extends SQLiteOpenHelper {
    private static String DB_NAME = "app.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 6;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    private DatabaseReference databaseReference;

    private void inicializateFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }
    // ToDo revisar si dejarlo todo aca

    public MySqliteHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
        Log.i("update", "db");
    }

    private void FirebaseSave(EntityCall instance) {

        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Call").child(String.valueOf(instance.getUid())).setValue(instance);
    }


    public void addCall(EntityCall call) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        inicializateFirebase(); // TODO retirar esto de acá
        FirebaseSave(call);

        values.put("id", call.getId());
        values.put("number", call.getNumber());
        values.put("type", call.getType());
        values.put("date", call.getDate().toString());
        values.put("duration", call.getDuration());
        database.insert("call", null, values);
        database.close();
        Log.i("msg", "add call");
    }

    public EntityCall getCall(int id) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("call", new String[]{"id", "number", "type", "date", "duration"}, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return new EntityCall();
        }
        EntityCall x = new EntityCall(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)));
        return x;
    }

    public boolean ExistCall(EntityCall call) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("call", new String[]{"id", "number", "type", "date", "duration"}, "id=?", new String[]{String.valueOf(call.getId())}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void CheckToAddCall(EntityCall call) {
        if (!ExistCall(call)) {
            addCall(call);
        }
    }

    public int getCountCall(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from call where type='" + type + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
    }

    public ArrayList<EntityCall> getAllElements() {

        ArrayList<EntityCall> list = new ArrayList<EntityCall>();

        // Select All Query
        String selectQuery = "SELECT  * FROM call";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityCall x = new EntityCall(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), Integer.parseInt(cursor.getString(4)));
            //only one column
            list.add(x);
        }
        cursor.close();
        db.close();

        return list;
    }

    public int getCountSMS(final String type) {
        final int[] count = {0};

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        /*
        Query query = databaseReference.child("Message")
                .orderByChild("type")
                .startAt(type)
                .endAt(type);

                */

        Query query = databaseReference.child("Message")
                .equalTo(user)
                .orderByChild("user");

        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntitySms models = snapshot.getValue(EntitySms.class);

                    assert models != null;
                    if (models.getType().equals(type)) {
                        count[0] += 1;
                    }
                }
                // String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

/*
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount = db.rawQuery("select count(*) from sms where type='" + type + "'", null);
        mCount.moveToFirst();
        int count = mCount.getInt(0);
        mCount.close();
        return count;
        */

        return count[0];
    }

    private void FirebaseSave(EntitySms instance) {

        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Message").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void addSms(EntitySms sms) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //inicializateFirebase(); // TODO retirar esto de acá
        //FirebaseSave(sms); // Si dejo para salvasr los SMS acá, no se guardan en Firebase. Solo funciona cuando está en modo Debug, no se por qué, pero acontence así

        values.put("body", sms.getBody());
        values.put("address", sms.getAddress());
        values.put("type", sms.getType());
        values.put("date", sms.getDate());
        database.insert("sms", null, values);
        database.close();
        Log.i("msg", "add sms");
    }

    public ArrayList<EntitySms> getAllElementsSms() {
        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();

        final ArrayList<EntitySms> lista = new ArrayList<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Query query = database.child("Message").orderByChild("user").equalTo(user);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntitySms models = snapshot.getValue(EntitySms.class);
                    assert models != null;
                    lista.add(models);
                    //String latitude=models.getUser();
                    //String longitude=models.getBody();

                    ///Here you can get all data
                }
                // String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
        return lista;
    }

    public ArrayList<EntitySms> getAllE33lementsSms() {
        ArrayList<EntitySms> list = new ArrayList<EntitySms>();

        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();
        //list = FirebaseGetAllSMSFromUser(userN);

        /*
        // Select All Query
        String selectQuery = "SELECT  * FROM sms";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntitySms x = new EntitySms(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            //only one column
            list.add(x);
        }
        cursor.close();
        db.close();
        */

        return list;
    }

    public void addScreen(EntityScreen screen) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("date", screen.getDate());
        values.put("type", screen.getType());
        values.put("islock", screen.getIslock());
        database.insert("screen", null, values);
        database.close();
        Log.i("msg", "add screen");

    }

    public ArrayList<EntityScreen> getAllElementsScreen() {
        ArrayList<EntityScreen> list = new ArrayList<EntityScreen>();
        String selectQuery = "SELECT  * FROM screen";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityScreen x = new EntityScreen(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }


    private void FirebaseSave(EntityTower instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Tower").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void addTower(EntityTower tower) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        inicializateFirebase();
        FirebaseSave(tower);

        values.put("date", tower.getDate());
        values.put("cid", tower.getCid());
        values.put("lac", tower.getLac());
        database.insert("tower", null, values);
        database.close();
        Log.i("msg", "add tower");
    }

    public ArrayList<EntityTower> getAllElementsTower() {
        ArrayList<EntityTower> list = new ArrayList<EntityTower>();
        String selectQuery = "SELECT  * FROM tower";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityTower x = new EntityTower(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<EntityTower> getAllUniqueTower() {
        ArrayList<EntityTower> list = new ArrayList<EntityTower>();
        String selectQuery = "SELECT * FROM tower GROUP BY cid";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityTower x = new EntityTower(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    public Map<Integer, Long> getAllTimeTower() {
        DateFormat format = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Map<Integer, Long> TowersDuration = new HashMap<Integer, Long>();
        ArrayList<EntityTower> list = new ArrayList<EntityTower>();
        String selectQuery = "SELECT  * FROM tower";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean last = false;
        EntityTower curr = new EntityTower(0, "0", 0, 0);
        try {
            while (cursor.moveToNext()) {
                EntityTower x = new EntityTower(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)), Integer.parseInt(cursor.getString(3)));
                if (!last) {
                    curr = x;
                    TowersDuration.put(x.cid, 0l);
                    last = true;
                } else {
                    Date lx = format.parse(curr.getDate());
                    Date fina = format.parse(x.getDate());
                    long time = (fina.getTime() - lx.getTime());
                    time += TowersDuration.get(curr.getCid());
                    TowersDuration.put(curr.getCid(), time);
                    curr = x;
                    if (!TowersDuration.containsKey(x.cid)) {
                        TowersDuration.put(x.cid, 0l);
                    }
                }
            }
        } catch (Exception ignored) {

        }
        cursor.close();
        db.close();
        return TowersDuration;
    }


    private void FirebaseSave(EntityInput instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Input").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void addInput(EntityInput input) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        inicializateFirebase();
        FirebaseSave(input);

        values.put("date", input.getDate());
        values.put("type", input.getType());
        values.put("app", input.getApp());
        database.insert("input", null, values);
        database.close();
        Log.i("msg", "add input");
    }

    public ArrayList<EntityInput> getAllElementsInput() {

        ArrayList<EntityInput> list = new ArrayList<EntityInput>();

        // Select All Query
        String selectQuery = "SELECT  * FROM input";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityInput x = new EntityInput(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    private void FirebaseSave(EntitySetting instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Settings").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void UpdateSetting(EntitySetting setting) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();

        inicializateFirebase();
        FirebaseSave(setting);

        ContentValues cv = new ContentValues();
        cv.put("value", setting.getValue());
        database.update("setting", cv, "id=" + setting.getId(), null);
        database.close();
        Log.i("msg", "update setting");
    }

    public ArrayList<EntitySetting> getAllElementsSetting() {
        ArrayList<EntitySetting> list = new ArrayList<EntitySetting>();
        String selectQuery = "SELECT  * FROM setting";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntitySetting x = new EntitySetting(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    private void FirebaseSave(EntityCalibration instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Calibration").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void UpdateCalibration(EntityCalibration calibration) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();

        inicializateFirebase();
        FirebaseSave(calibration);

        ContentValues cv = new ContentValues();
        cv.put("time", calibration.getTime());
        database.update("calibration", cv, "id=" + calibration.getId(), null);
        database.close();
        Log.i("msg", "update calibration to:" + calibration.getTime());
    }

    public EntityCalibration getCalibration(int id) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("calibration", new String[]{"id", "time"}, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return new EntityCalibration(1, 0);
        }
        EntityCalibration x = new EntityCalibration(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)));
        return x;
    }

    public void addOrUpdateMoodState(EntityMoodState mood) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int id = ExistMoodState(mood);
        if (id == -1) {//add
            values.put("value", mood.getValue());
            String aa = mood.getDate().toString();
            values.put("date", mood.getDate().toString());
            database.insert("mood", null, values);
            database.close();
            Log.i("msg", "add mood");
        } else {//update
            ContentValues cv = new ContentValues();
            cv.put("date", mood.getDate().toString());
            cv.put("value", mood.getValue());
            database.update("mood", cv, "id=" + id, null);
            database.close();
            Log.i("msg", "update mood to value:" + mood.getValue());
        }
    }

    public int ExistMoodState(EntityMoodState mood) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("mood", new String[]{"id", "value", "date"}, "date=?", new String[]{String.valueOf(mood.getDate())}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id = Integer.parseInt(cursor.getString(0));
                cursor.close();
                return id;
            }
            cursor.close();
        }
        return -1;
    }

    public int getValueMoodStateByDate(String date) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("mood", new String[]{"id", "value", "date"}, "date=?", new String[]{date}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int val = Integer.parseInt(cursor.getString(1));
                cursor.close();
                return val;
            }
            cursor.close();
        }
        return -1;
    }

    public EntityScreen getFirstScreenByDate(long time) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("screen", new String[]{"id", "date", "type", "islock"}, "date>=? AND type='on' AND islock='0' ", new String[]{String.valueOf(time)}, null, null, "date ASC", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                EntityScreen ret = new EntityScreen(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
                cursor.close();
                return ret;
            }
            cursor.close();
        }
        return new EntityScreen(-1, 0, "m", 0);
    }

    public EntityScreen getLastScreenByDate(long time) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("screen", new String[]{"id", "date", "type", "islock"}, "date>? AND type='on' AND islock='0' ", new String[]{String.valueOf(time)}, null, null, "date ASC", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                EntityScreen ret = new EntityScreen(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)), cursor.getString(2), Integer.parseInt(cursor.getString(3)));
                cursor.close();
                return ret;
            }
            cursor.close();
        }
        return new EntityScreen(-1, 0, "m", 0);
    }

    private void FirebaseSave(EntitySleep instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Sleep").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void addOrUpdateSleep(EntitySleep sleep) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();

        inicializateFirebase();
        FirebaseSave(sleep);

        ContentValues values = new ContentValues();
        int id = ExistSleep(sleep);
        if (id == -1) {
            values.put("date", sleep.getDate());
            values.put("sleep", sleep.getSleep());
            values.put("awake", sleep.getAwake());
            values.put("value", sleep.getValue());
            database.insert("sleep", null, values);
            database.close();
            Log.i("msg", "add sleep");
        } else {//update
            ContentValues cv = new ContentValues();
            cv.put("sleep", sleep.getSleep());
            cv.put("awake", sleep.getAwake());
            cv.put("value", sleep.getValue());
            database.update("sleep", cv, "id=" + id, null);
            database.close();
            Log.i("msg", "update sleep");
        }
    }

    public EntitySleep getSleepByDate(long time) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("sleep", new String[]{"id", "date", "sleep", "awake", "value"}, "date=?", new String[]{String.valueOf(time)}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                EntitySleep ret = new EntitySleep(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)));
                cursor.close();
                return ret;
            }
            cursor.close();
        }
        return new EntitySleep(-1, -1, -1, -1, -1);
    }

    public int ExistSleep(EntitySleep sleep) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("sleep", new String[]{"id", "date", "sleep", "awake", "value"}, "date=?", new String[]{String.valueOf(sleep.getDate())}, null, null, null, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int id = Integer.parseInt(cursor.getString(0));
                cursor.close();
                return id;
            }
            cursor.close();
        }
        return -1;
    }

    public ArrayList<EntitySleep> getAllElementsSleep() {

        ArrayList<EntitySleep> list = new ArrayList<EntitySleep>();

        // Select All Query
        String selectQuery = "SELECT  * FROM sleep";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntitySleep x = new EntitySleep(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)));
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    public EntitySleep getSleep(int id) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("sleep", new String[]{"id", "date", "sleep", "awake", "value"}, "id=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        EntitySleep x = new EntitySleep(Integer.parseInt(cursor.getString(0)), Long.parseLong(cursor.getString(1)), Long.parseLong(cursor.getString(2)), Long.parseLong(cursor.getString(3)), Long.parseLong(cursor.getString(4)));
        return x;
    }

    private void FirebaseSave(EntityMedication instance) {

        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Medication").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    public void addMedication(EntityMedication medication) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();

        inicializateFirebase();
        FirebaseSave(medication);

        values.put("name", medication.getName());
        values.put("unit", medication.getUnit());
        values.put("dose", medication.getDose());
        values.put("quantity", medication.getQuantity());
        values.put("frequency", medication.getFrequency());
        values.put("enable", 1);
        values.put("date", medication.getDate());
        database.insert("medication", null, values);
        database.close();
        Log.i("msg", "add medication");
        Log.i("msg", String.valueOf(medication.getDate()));
    }

    public ArrayList<EntityMedication> getAllElementsMedication() {
        ArrayList<EntityMedication> list = new ArrayList<EntityMedication>();

        // Select All Query
        String selectQuery = "SELECT  * FROM medication WHERE enable = 1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityMedication x = new EntityMedication(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),
                    Integer.parseInt(cursor.getString(5)),
                    Integer.parseInt(cursor.getString(6)),
                    Long.parseLong(cursor.getString(7))
            );
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    public ArrayList<EntityMedication> getAllElementsMedicationByDateRange(long a, long b) {

        ArrayList<EntityMedication> list = new ArrayList<EntityMedication>();

        // Select All int range [a,b]
        String selectQuery = "SELECT  * FROM medication WHERE enable = 1 AND date>=" + a + " AND date<=" + b;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        while (cursor.moveToNext()) {
            EntityMedication x = new EntityMedication(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    Integer.parseInt(cursor.getString(4)),
                    Integer.parseInt(cursor.getString(5)),
                    Integer.parseInt(cursor.getString(6)),
                    Long.parseLong(cursor.getString(7))
            );
            list.add(x);
        }
        cursor.close();
        db.close();
        return list;
    }

    //to update pass id , 1
    //to delete pass id , 0
    public void UpdateMedication(EntityMedication m, int enable) {
        int id = m.getId();
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", m.getName());
        cv.put("unit", m.getUnit());
        cv.put("dose", m.getDose());
        cv.put("quantity", m.getQuantity());
        cv.put("frequency", m.getFrequency());
        cv.put("enable", enable);
        cv.put("date", m.getDate());
        database.update("medication", cv, "id=" + id, null);
        database.close();
        if (enable == 0) {
            Log.i("msg", "delete medication");
        } else {
            Log.i("msg", "update medication");
        }
    }

    public EntityMedication getMedication(long date) {
        SQLiteDatabase database = MySqliteHandler.this.getReadableDatabase();
        Cursor cursor = database.query("medication", new String[]{"id", "name", "unit", "dose", "quantity", "frequency", "enable", "date"}, "date=?", new String[]{String.valueOf(date)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        } else {
            return new EntityMedication();
        }
        EntityMedication x = new EntityMedication(
                Integer.parseInt(cursor.getString(0)),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                Integer.parseInt(cursor.getString(4)),
                Integer.parseInt(cursor.getString(5)),
                Integer.parseInt(cursor.getString(6)),
                Long.parseLong(cursor.getString(7))
        );
        cursor.close();
        database.close();
        return x;
    }
}
