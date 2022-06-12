package br.example.com.brapolar.Activities;

import androidx.appcompat.app.AppCompatActivity;
import br.example.com.brapolar.R;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Informações");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void YesAction(View view) {
        Toast.makeText(this, getString(R.string.feedback_yes), Toast.LENGTH_LONG).show();
        finish();
    }

    public void NoAction(View view) {
        Toast.makeText(this, getString(R.string.feedback_no), Toast.LENGTH_LONG).show();
        finish();
    }
}
