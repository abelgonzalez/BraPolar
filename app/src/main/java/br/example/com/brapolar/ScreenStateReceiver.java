package br.example.com.brapolar;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import androidx.annotation.RequiresApi;
import br.example.com.brapolar.Entities.EntityScreen;
import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.Utils.UserSingleton;

public class ScreenStateReceiver extends BroadcastReceiver {
    private DatabaseReference databaseReference;

    private void inicializateFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onReceive(Context context, Intent intent) {
        inicializateFirebase();

        long date = System.currentTimeMillis();
        String strAction = intent.getAction();
        MySqliteHandler bd = new MySqliteHandler(context);
        KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        assert strAction != null;
        if (strAction.equals(Intent.ACTION_USER_PRESENT) || strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON))

            if (strAction.equals(Intent.ACTION_SCREEN_OFF)) {
                System.out.println("EntityScreen off");
                EntityScreen screen = new EntityScreen(date, "off", 1);

                FirebaseSave(screen);// salvar en Firebase

                bd.addScreen(screen);
            } else if (strAction.equals(Intent.ACTION_SCREEN_ON)) {
                assert myKM != null;
                if (myKM.isDeviceLocked()) {//.inKeyguardRestrictedInputMode()) {
                    System.out.println("EntityScreen on " + "LOCKED");
                    EntityScreen screen = new EntityScreen(date, "on", 1);

                    FirebaseSave(screen);// salvar en Firebase

                    bd.addScreen(screen);
                } else {
                    System.out.println("EntityScreen on " + "UNLOCKED");
                    EntityScreen screen = new EntityScreen(date, "on", 0);

                    FirebaseSave(screen);// salvar en Firebase

                    bd.addScreen(screen);
                }

            } else {
                System.out.println("EntityScreen on " + "UNLOCKED");
                EntityScreen screen = new EntityScreen(date, "on", 0);

                FirebaseSave(screen);// salvar en Firebase

                bd.addScreen(screen);
            }
    }

    private void FirebaseSave(EntityScreen instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Screen").child(String.valueOf(instance.getUid())).setValue(instance);
    }
}
