package br.example.com.brapolar.Activities;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import br.example.com.brapolar.AddMedicationDialog;
import br.example.com.brapolar.Entities.EntityMedication;
import br.example.com.brapolar.ExpandableListAdapter;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;

import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;
import android.widget.ExpandableListView;

/*import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class MedicationActivity extends AppCompatActivity {

    MySqliteHandler mySqliteHandler;
    EntityMedication curr;

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private List<Integer> listDataHeader;
    private HashMap<Integer, List<EntityMedication>> listHash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.medication_name));
        mySqliteHandler = new MySqliteHandler(this);

        //sample
        listView = (ExpandableListView) findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);


        for (int i = 0; i < listAdapter.getGroupCount(); i++)
            listView.expandGroup(i);


//        listView = (ListView)findViewById(R.id.list);

//        listView.setAdapter(new AdapterMedication(mySqliteHandler.getAllElementsMedication(),this));
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMedicationDialog dialog = new AddMedicationDialog();

                dialog.show(getSupportFragmentManager(), "example dialog");
            }
        });

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            curr = (EntityMedication) extras.getSerializable("EntityMedication");
            AddMedicationDialog dialog = new AddMedicationDialog();
            dialog.setMedication(curr);
            dialog.show(getSupportFragmentManager(), "example dialog");
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        listView = (ExpandableListView) findViewById(R.id.lvExp);
        initData();
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listHash);
        listView.setAdapter(listAdapter);

        for (int i = 0; i < listAdapter.getGroupCount(); i++)
            listView.expandGroup(i);

    }


    private void initData() {
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long a = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, 1);
        long b = c.getTimeInMillis() - 1;

        ArrayList<EntityMedication> all = mySqliteHandler.getAllElementsMedication();
        ArrayList<EntityMedication> today = new ArrayList<>();
        for (int i = 0; i < all.size(); i++) {
            EntityMedication EM = all.get(i);
            long time = EM.getDate();
            for (int j = 0; j < EM.getQuantity(); j++) {
                if (time >= a && time <= b) {
                    today.add(EM);
                    break;
                }
                time += EM.getFrequency() * 60 * 60 * 1000;
            }
        }

        for (int i = 0; i < 24 * 60; i++) {
            List<EntityMedication> AtTimeI = new ArrayList<>();
            for (int j = 0; j < today.size(); j++) {
                EntityMedication EM = today.get(j);
                Calendar x = Calendar.getInstance();
                long C = EM.getDate();
                int cant = 0;
                while (C <= b && cant < EM.getQuantity()) {
                    x.setTimeInMillis(C);
                    int Sum = x.get(Calendar.HOUR_OF_DAY) * 60 + x.get(Calendar.MINUTE);
                    if (Sum == i) {
                        AtTimeI.add(EM);
                        break;
                    }
                    C += EM.getFrequency() * 1000 * 60 * 60;
                    cant++;
                }

            }
            if (!AtTimeI.isEmpty()) {
                listDataHeader.add(i);
                listHash.put(i, AtTimeI);
            }
        }
    }

    public void NoyifyChange() {
        listView.deferNotifyDataSetChanged();
    }
    /*

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPerson.clear(); // limpiar por si tiene algo en caché
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()){
                    Persona p = objSnaptshot.getValue(Persona.class);
                    listPerson.add(p); // la instancia de persona en ese ciclo

                    arrayAdapterPersona = new ArrayAdapter<Persona>(MedicationActivity.this, android.R.layout.simple_list_item_1, listPerson);
                    listV_personas.setAdapter(arrayAdapterPersona);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true); // persistencia de datos... caso el usuario
        // inserte un dato y no tenga conexion a internet, se almacena la instancia de forma local
        // y cuando se conecte, la instancia se envía
        // OJO: --->ES EJECUTADO SOLO 1 VEZ!! Solo al inicio de la app
        databaseReference = firebaseDatabase.getReference();
    }
    */
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
/*
        String nombre = nomP.getText().toString();
        String app = appP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();


        switch (item.getItemId()){
            case R.id.icon_add:{

                if (nombre.equals("") ||app.equals("")||correo.equals("")||password.equals("") ){
                    validacion();
                }
                else {
                    //crear una instancia de persona
                    Persona p = new Persona();
                    p.setUid((UUID.randomUUID().toString()));
                    p.setNombres((nombre));
                    p.setApellidos(app);
                    p.setCorreo(correo);
                    p.setPassword(password);

                    databaseReference.child("Persona").child(p.getUid()).setValue(p); //  crea la jerarquí para Persona,
                    // de ahora en adelante todos los nuevos objetos, se almacenarán en la clase PErsona,
                    // tendiendo el ID como llave primaria
                    Toast.makeText(this,"Agregar", Toast.LENGTH_LONG).show();
                    limpiarcajas();
                }
                break;

            }
            case R.id.icon_save:{

                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                p.setNombres(nomP.getText().toString().trim());
                p.setApellidos(appP.getText().toString().trim());
                p.setCorreo(correoP.getText().toString().trim());
                p.setPassword(passwordP.getText().toString().trim());

                databaseReference.child("Persona").child(p.getUid()).setValue(p);// obteniendo la persona y estableciendo con el SetValue, el nuevo objeto p

                Toast.makeText(this,"Guardar", Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.icon_delete:{

                Persona p = new Persona();
                p.setUid(personaSelected.getUid());
                databaseReference.child("Persona").child(p.getUid()).removeValue();
                Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
                limpiarcajas();

                Toast.makeText(this,"Eliminar", Toast.LENGTH_LONG).show();
                break;
            }
            default:break;
        }
        return true;
    }

    private void limpiarcajas() {
        nomP.setText("");
        appP.setText("");
        correoP.setText("");
        passwordP.setText("");
    }

    private void validacion(){ // para saber si está vacio
        String nombre = nomP.getText().toString();
        String correo = correoP.getText().toString();
        String password = passwordP.getText().toString();
        String app = appP.getText().toString();

        if (nombre.equals("")){
            nomP.setError("Required");
        }
        if (correo.equals("")){
            nomP.setError("Required");
        }
        if (password.equals("")){
            nomP.setError("Required");
        }
        if (app.equals("")){
            nomP.setError("Required");
        }
    }
    */
}
