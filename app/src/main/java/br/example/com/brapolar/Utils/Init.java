package br.example.com.brapolar.Utils;

import com.google.firebase.database.FirebaseDatabase;

public class Init extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        // Singleton de Usuario
        UserSingleton a = UserSingleton.getInstance();
        //a.setUserName("Paciente10");
        //a.setEmail("paciente1@brapolar.com.br");
    }
}
