package br.example.com.brapolar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.example.com.brapolar.Entities.EntityScreen;
import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.LongConvert;
import br.example.com.brapolar.Utils.TimeCount;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScreenFragment extends Fragment {

    public ScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_screen, container, false);
        TextView tx = (TextView) view.findViewById(R.id.physical_screen);
        MySqliteHandler mySqliteHandler = new MySqliteHandler(getContext());
        int on = 0, off = 0, lock = 0;
        StringBuilder x = new StringBuilder();
        List<EntityScreen> screens = mySqliteHandler.getAllElementsScreen();
        if (screens.size() > 0) {
            ///Obs esta imprimiendo numero convertir a  fecha hacer despues
            for (int i = screens.size() - 1; i >= 0; i--) {
                //Date date = new Date(screens.get(i).getDate());
                //DateFormat format = new SimpleDateFormat(getString(R.string.format));
                //String dateString = format.format(date);
                String dateString = LongConvert.FromLongToDateFormatString(screens.get(i).getDate());
                if (screens.get(i).getType().equals("off")) {
                    x.append(dateString).append(" Tela off\n");
                } else {
                    if (screens.get(i).getIslock() == 1) {
                        x.append(dateString).append(" Tela On (Bloqueada)\n");
                    } else {
                        x.append(dateString).append(" Tela On (Desbloqueada)\n");
                    }
                }
            }
            String current = screens.get(0).getType();
            EntityScreen last = screens.get(0);
            // Date date = format.parse(string);
            try {
                for (int i = 1; i < screens.size(); i++) {
                    if (current.equals("on") && last.getIslock() == 1) {
                        if (screens.get(i).getType().equals("off")) {//onLock para off
                            Date lx = new Date(last.getDate());
                            Date fina = new Date(screens.get(i).getDate());
                            long time = (fina.getTime() - lx.getTime());
                            Log.i("t", String.valueOf(time));
                            on += time;
                            lock += time;
                        } else if (screens.get(i).getType().equals("on") && screens.get(i).getIslock() == 0) {//onLock para onunlock
                            Date lx = new Date(last.getDate());
                            Date fina = new Date(screens.get(i).getDate());
                            long time = (fina.getTime() - lx.getTime());
                            on += time;
                            lock += time;
                            Log.i("t", String.valueOf(time));
                        }
                    } else if (current.equals("on") && last.getIslock() == 0) {
                        if (screens.get(i).getType().equals("off")) {//onLock para off
                            Date lx = new Date(last.getDate());
                            Date fina = new Date(screens.get(i).getDate());
                            long time = (fina.getTime() - lx.getTime());
                            on += time;
                            Log.i("t", String.valueOf(time));
                        }
                    } else if (current.equals("off")) {//off para on
                        if (screens.get(i).getType().equals("on")) {//off++
                            Date lx = new Date(last.getDate());
                            Date fina = new Date(screens.get(i).getDate());
                            long time = (fina.getTime() - lx.getTime());
                            off += time;
                            Log.i("t", String.valueOf(time));
                        }
                    }
                    last = screens.get(i);
                    current = screens.get(i).getType();
                }
            } catch (Exception e) {
                Log.i("errror", "error parse date");
                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
            }
        }
        String inicio = "Tela On: " + TimeCount.toLongTime(on / 1000) + "\n" +
                "Tela Off (Bloqueada): " + TimeCount.toLongTime(lock / 1000) + "\n" +
                "Tela Off: " + TimeCount.toLongTime(off / 1000) + "\n-----------------------\n";
        tx.setText(String.format("%s%s", inicio, x.toString()));

        return view;
    }
}
