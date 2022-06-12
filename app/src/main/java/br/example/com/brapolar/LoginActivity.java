package br.example.com.brapolar;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.FontRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import br.example.com.brapolar.Activities.MoodActivity;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.UserSingleton;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail, mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Button btnSignIn = findViewById(R.id.email_sign_in_button);
        mAuth = FirebaseAuth.getInstance();

        grantPermissions();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                UserSingleton userSingleton = UserSingleton.getInstance();

                if (user != null) {
                    // User is signed in

                    String Uid = user.getUid();
                    String email = user.getEmail();

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + Uid);
                    toastMessage("Corretamente autenticado como: " + email);

                    assert email != null;
                    userSingleton.setUserName(getUserFromEmail(email));
                    userSingleton.setEmail(email);

                    if (userSingleton.getUserName().equals("Especialista")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivityEspecialista.class);
                        startActivity(intent);
                    } else {

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }

                } else {
                    // User is signed out
                    userSingleton = null;
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Sessão fechada com sucesso");
                }
                // ...
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pass = mPassword.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    mAuth.signInWithEmailAndPassword(email, pass)

                    ;
                } else {
                    toastMessage("Você não preencheu todos os campos");
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onBackPressed() {
        if (mAuth.getCurrentUser() == null) { // si no está nadie logado, entonces bloqua el back
            boolean a = true;
        } else {
            super.onBackPressed();
        }
    }

    private String getUserFromEmail(String email) {
        String[] parts = email.split("\\@");
        return parts[0];

    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void grantPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.READ_PHONE_STATE,
                android.Manifest.permission.PROCESS_OUTGOING_CALLS,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            startService(new Intent(this, CallLogService.class));
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
