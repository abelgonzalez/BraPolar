package br.example.com.brapolar;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.ViewDebug;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.Utils.UserSingleton;

public class SMSObserver extends ContentObserver {
    private String lastSmsId;
    private Context ctx;
    private DatabaseReference databaseReference;

    private void inicializateFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public SMSObserver(Handler handler, Context ctx) {
        super(handler);
        this.ctx = ctx;
    }

    @Override
    public void onChange(boolean selfChange) {
        inicializateFirebase();

        super.onChange(selfChange);
        Uri uriSMSURI = Uri.parse("content://sms/sent");
        Cursor cur = ctx.getContentResolver().query(uriSMSURI, null, null, null, null);
        assert cur != null;
        cur.moveToNext();
        String id = cur.getString(cur.getColumnIndex("_id"));
        if (smsChecker(id)) {
            String address = cur.getString(cur.getColumnIndex("address"));
            if (address == null)
                address = "";
            String message = cur.getString(cur.getColumnIndex("body"));
            message = String.valueOf(message.length()); // No almacenar el cuerpo del SMS enviado, solo la cant de caracteres

            EntitySms x = new EntitySms(message, address, "Sainte");

            FirebaseSave(x);// salvar en firebase

            MySqliteHandler db = new MySqliteHandler(ctx);
            db.addSms(x);
        }
    }

    private void FirebaseSave(EntitySms instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Message").child(String.valueOf(instance.getUid())).setValue(instance);
    }

    // Prevent duplicate results without overlooking legitimate duplicates
    public boolean smsChecker(String smsId) {
        boolean flagSMS = true;

        if (smsId.equals(lastSmsId)) {
            flagSMS = false;
        } else {
            lastSmsId = smsId;
        }
        return flagSMS;
    }
}
