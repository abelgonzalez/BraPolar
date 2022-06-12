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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import br.example.com.brapolar.Entities.EntityCall;
import br.example.com.brapolar.Entities.EntityMoodState;
import br.example.com.brapolar.MainActivity;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.TimeCount;
import br.example.com.brapolar.Utils.UserSingleton;

import static java.util.stream.Collectors.groupingBy;

/**
 * A simple {@link Fragment} subclass.
 */
public class CallFragment extends Fragment {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private static final String TAG = "Call Fragment";
    MySqliteHandler mySqliteHandler;

    public CallFragment() {
        // Required empty public constructor
    }

    public class EntityCallDateComparator implements Comparator<EntityCall> {
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

        @Override
        public int compare(EntityCall o1, EntityCall o2) {
            // Order ascending.
            try {
                return f.parse(o1.getDate()).compareTo(f.parse(o2.getDate()));
            } catch (ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_call, container, false);
        final TextView tx = (TextView) view.findViewById(R.id.social_call);

        final int[] inCallNumber = {0};
        final int[] outCallNumber = {0};
        final int[] missedCallNumber = {0};

        UserSingleton a = UserSingleton.getInstance();
        final String user = a.getUserName();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("Call")
                .equalTo(user)
                .orderByChild("user");
        query.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                StringBuilder x = new StringBuilder("Ligações Registradas\n----------------------\n");
                ArrayList<EntityCall> n = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    EntityCall models = snapshot.getValue(EntityCall.class);

                    assert models != null;

                    n.add(models);

                    // String value = dataSnapshot.getValue(String.class);
                    //Log.d(TAG, "Value is: " + value);
                }

                Collections.sort(n, new EntityCallDateComparator());

                for (int i = n.size() - 1; i >= 0; i--) {
                    x.append("Número: ").append(n.get(i).getNumber()).append("\n");
                    x.append("Tipo: ").append(n.get(i).getType()).append("\n");
                    x.append("Data: ").append(n.get(i).getDate()).append("\n");
                    x.append("Duração: ").append(TimeCount.toShortTime(n.get(i).getDuration())).append("\n");
                    x.append("----------------------\n");

                }
                x.append("Total de ligações: ").append(String.valueOf(n.size())).append("\n");
                if (user.equals("Paciente1")) {
                    x.append("Total de ligações nos ultimos 7 dias: ").append("\n");
                    x.append("Saintes: ").append("15").append("\n");
                    x.append("Entrantes:  ").append("4").append("\n");
                    x.append("Perdidas:  ").append("2").append("\n");
                }
                tx.setText(x.toString());
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
