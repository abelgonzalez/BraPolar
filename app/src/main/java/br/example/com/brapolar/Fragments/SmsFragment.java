package br.example.com.brapolar.Fragments;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.example.com.brapolar.Entities.EntityCall;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.Utils.UserSingleton;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.text.DateFormat.getDateTimeInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class SmsFragment extends Fragment {
    private ArrayList<EntitySms> smsList;
    private DatabaseReference database;

    public static int smsTotal = 0;
    public static int smsTotalEntr = 0;
    public static int smsTotalSain = 0;

    public SmsFragment() {
        // Required empty public constructor
    }

    public class EntitySmsDateComparator implements Comparator<EntitySms> {
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

        @Override
        public int compare(EntitySms o1, EntitySms o2) {
            // Order ascending.
            try {
                return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        database = FirebaseDatabase.getInstance().getReference();


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_sms, container, false);

        UserSingleton a = UserSingleton.getInstance();
        String user = a.getUserName();

        Query query = database.child("Message")
                .equalTo(user)
                .orderByChild("user");

        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                TextView tx = (TextView) view.findViewById(R.id.social_sms);

                StringBuilder x = new StringBuilder("SMS Registrados\n----------------------\n");

                smsList = new ArrayList<>();
                smsTotalSain = 0;
                smsTotalEntr = 0;
                smsTotal = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntitySms models = snapshot.getValue(EntitySms.class);
                    smsList.add(models);

                    assert models != null;
                    if (models.getType().equals("Sainte")) {
                        smsTotalSain += 1;
                    }
                    if (models.getType().equals("Entrante")) {
                        smsTotalEntr += 1;
                    }
                }
                smsTotal = (int) (long) dataSnapshot.getChildrenCount();

                Collections.sort(smsList, new EntitySmsDateComparator());

                for (int i = smsList.size() - 1; i >= 0; i--) {

                    x.append("Número: ").append(smsList.get(i).getAddress()).append("\n");
                    x.append("Tipo: ").append(smsList.get(i).getType()).append("\n");
                    x.append("Data: ").append(smsList.get(i).getDate()).append("\n");
                    x.append("Quantidade de caracteres: ").append(smsList.get(i).getBody()).append("\n");
                    //x.append("Text: ").append("---").append("\n");
                    x.append("----------------------\n");
                }
                x.append("Total de SMS: ").append(smsTotal).append("\n");
                tx.setText(x.toString());

                // String value = dataSnapshot.getValue(String.class);
                //Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        return view;
    }
}
