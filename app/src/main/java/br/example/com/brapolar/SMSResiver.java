package br.example.com.brapolar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.Utils.UserSingleton;

public class SMSResiver extends BroadcastReceiver {
    private DatabaseReference databaseReference;

    private void inicializateFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            // get sms objects
            Object[] pdus = (Object[]) bundle.get("pdus");
            assert pdus != null;
            if (pdus.length == 0) {
                return;
            }
            // large message might be broken into many
            SmsMessage[] messages = new SmsMessage[pdus.length];
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pdus.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                sb.append(messages[i].getMessageBody());
            }
            String address = messages[0].getOriginatingAddress();
            String message = sb.toString();
            message = String.valueOf(message.length()); // No almacenar el cuerpo del SMS enviado, solo la cant de caracteres

            Log.i("msg", "SMS entrante");
            EntitySms x = new EntitySms(message, address, "Entrante");

            FirebaseSave(x);// salvar en firebase

            MySqliteHandler db = new MySqliteHandler(context);
            db.addSms(x);
            // prevent any other broadcast receivers from receiving broadcast
            // abortBroadcast();
        }
    }

    private void FirebaseSave(EntitySms instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Message").child(String.valueOf(instance.getUid())).setValue(instance);
    }
}
