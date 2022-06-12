package br.example.com.brapolar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.example.com.brapolar.Entities.EntityCall;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.Utils.UserSingleton;

public class CallReceiver extends PhonecallReceiver {
    DatabaseReference databaseReference;

    void inicializateFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void FireSaveCall(EntityCall call) {
        call.setUser("pepe");
        databaseReference.child("Call").child(String.valueOf(call.getId())).setValue(call); //  Create Call hieracy,
    }

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, R.string.call_incoming, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start) {
        Toast.makeText(ctx, R.string.call_outgoing, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end) {
        inicializateFirebase();

        Toast.makeText(ctx, R.string.call_incoming + " de: " + number, Toast.LENGTH_SHORT).show();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm");
        String dateString = formater.format(start);
        EntityCall add = new EntityCall(number, "Entrante", dateString, (int) (end.getTime() - start.getTime()) / 1000);
        MySqliteHandler bd = new MySqliteHandler(ctx);
        bd.addCall(add);

        FirebaseSave(add); // Salvar en Firebase

    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end) {
        inicializateFirebase();

        Toast.makeText(ctx, R.string.call_outgoing + " de:" + number, Toast.LENGTH_SHORT).show();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm");
        String dateString = formater.format(start);
        EntityCall add = new EntityCall(number, "Sainte", dateString, (int) (end.getTime() - start.getTime()) / 1000);
        MySqliteHandler bd = new MySqliteHandler(ctx);
        bd.addCall(add);

        FirebaseSave(add); // Salvar en Firebase

    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start) {
        inicializateFirebase();

        Toast.makeText(ctx, R.string.social_lost_call + number, Toast.LENGTH_SHORT).show();
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy HH:mm");
        String dateString = formater.format(start);
        EntityCall add = new EntityCall(number, "Perdida", dateString, 0);
        MySqliteHandler bd = new MySqliteHandler(ctx);
        bd.addCall(add);

        FirebaseSave(add); // Salvar en Firebase

    }

    void FirebaseSave(EntityCall instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Call").child(String.valueOf(instance.getUid())).setValue(instance);
    }
}
