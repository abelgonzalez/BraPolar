package br.example.com.brapolar.Activities;

import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import br.example.com.brapolar.Entities.EntityCalibration;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.TimeCount;
import br.example.com.brapolar.Utils.UserSingleton;

import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.UUID;

public class CalibrationActivity extends AppCompatActivity {
    int count = 0;
    TextView t;
    String contenido;
    String formatOK = "<font color='#006400'>";
    String formatNext = "<font color='#00BFFF'>";
    String formatWrong = "<font color='#EE0000'>";
    String format2 = "</font>";
    Date end, start;
    MySqliteHandler mySqliteHandler;
    EntityCalibration current;
    private DatabaseReference databaseReference; // Firebase

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        t = (TextView) findViewById(R.id.calibration_text);
        TextView value = (TextView) findViewById(R.id.calibration_value);
        mySqliteHandler = new MySqliteHandler(this);
        contenido = this.getString(R.string.keyboard_text);
        current = mySqliteHandler.getCalibration(1);
        value.setText(this.getString(R.string.typing_text) + ": " + TimeCount.toShortTime(current.getTime() / 1000));
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (count == 0) {
            start = new Date();
        }
        if (count < contenido.length() - 1) {
            char at = contenido.charAt(count);
            char next = contenido.charAt(count + 1);
            char pressedKey = (char) event.getUnicodeChar();
            if (at == ' ') {
                at = '_';
            }
            if (contenido.charAt(count) == pressedKey) {
                if (count == 0) {
                    String cx = formatOK + at + format2 + formatNext + next + format2 + contenido.substring(2, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                } else {
                    String cx = formatOK + contenido.substring(0, count) + at + format2 + formatNext + next + format2 + contenido.substring(count + 2, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                }
                count++;
                return true;
            } else {
                if (count == 0) {
                    String cx = formatWrong + at + format2 + contenido.substring(1, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                } else {
                    String cx = contenido.substring(0, count) + formatWrong + at + format2 + contenido.substring(count + 1, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                }
                return true;
            }
        } else if (count < contenido.length()) {
            char at = contenido.charAt(count);
            char pressedKey = (char) event.getUnicodeChar();
            if (at == ' ') {
                at = '_';
            }
            if (contenido.charAt(count) == pressedKey) {
                if (count == 0) {
                    String cx = formatOK + at + format2 + contenido.substring(1, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }

                } else {
                    String cx = formatOK + contenido.substring(0, count) + at + format2 + contenido.substring(count + 1, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                }
                count++;
                end = new Date();
                int time = (int) (end.getTime() - start.getTime());
                EntityCalibration calibration = new EntityCalibration(1, time);
                mySqliteHandler.UpdateCalibration(calibration);

                FirebaseSave(calibration); // Salvar en Firebase

                Toast.makeText(this, "Calibração terminada", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            } else {
                if (count == 0) {
                    String cx = formatWrong + at + format2 + contenido.substring(1, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                } else {

                    String cx = contenido.substring(0, count) + formatWrong + at + format2 + contenido.substring(count + 1, contenido.length());
                    if (Build.VERSION.SDK_INT >= 24) {
                        t.setText(Html.fromHtml(cx, 0));
                    } else {
                        t.setText(Html.fromHtml(cx));
                    }
                }
                return true;
            }

        } else {
            return true;
        }
    }

    void FirebaseSave(EntityCalibration instance) {
        UserSingleton a = UserSingleton.getInstance();
        String userN = a.getUserName();

        instance.setUser(userN);
        instance.setUid((UUID.randomUUID().toString()));

        databaseReference.child("Calibration").child(String.valueOf(instance.getUid())).setValue(instance);
    }
}
