package br.example.com.brapolar.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import br.example.com.brapolar.MySqliteHandler;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.TimeCount;
import br.example.com.brapolar.EntityTower;

/**
 * A simple {@link Fragment} subclass.
 */
public class CellFragment extends Fragment {

    public CellFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cell, container, false);
        TextView tx = (TextView) view.findViewById(R.id.physical_cell);
        MySqliteHandler mySqliteHandler = new MySqliteHandler(getContext());
        StringBuilder x = new StringBuilder("Celda atual:\n");
        int cid = 0;
        int lac = 0;
        final TelephonyManager telephony = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        assert telephony != null;
        if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
            final GsmCellLocation location = (GsmCellLocation) telephony.getCellLocation();
            if (location != null) {
                cid = location.getCid();
                lac = location.getLac();
            }
        } else if (telephony.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            final CdmaCellLocation location = (CdmaCellLocation) telephony.getCellLocation();
            if (location != null) {
                cid = location.getBaseStationId();
                lac = location.getSystemId();
            }
        }
        x.append("Celda ID: ").append(cid).append(" Celda LAC: ").append(lac).append("\n");
        x.append("------------------------------------------------------------------\n");
        x.append("Celdas usadas\n");
        x.append("------------------------------------------------------------------\n");
        List<EntityTower> uniqueTower = mySqliteHandler.getAllUniqueTower();
        List<EntityTower> allTower = mySqliteHandler.getAllElementsTower();
        Map<Integer, Long> durations = mySqliteHandler.getAllTimeTower();
        for (int i = 0; i < uniqueTower.size(); i++) {
            long time = 0;
            if (durations.get(uniqueTower.get(i).getCid()) != null) {
                time = durations.get(uniqueTower.get(i).getCid());
            }

            if (uniqueTower.get(i).getCid() == 0) {
                x.append("Celda ID: Sem sinal" + "Celda LAC: Sem sinal\n");
                x.append("Último uso: ").append(uniqueTower.get(i).getDate()).append("\n");
                x.append("Data: ").append(TimeCount.toShortTime(time / 1000)).append("\n");
                x.append("------------------------------------------------------------------\n");
            } else {
                x.append("Celda ID: ").append(uniqueTower.get(i).getCid()).append("  ").append("Celda LAC: ").append(uniqueTower.get(i).getLac()).append("\n");
                x.append("Último uso: ").append(uniqueTower.get(i).getDate()).append("\n");
                x.append("Tempo usado: ").append(TimeCount.toShortTime(time / 1000)).append("\n");
                x.append("------------------------------------------------------------------\n");
            }
        }
        x.append("Registro das torres\n");

        for (int i = 0; i < allTower.size(); i++) {
            if (allTower.get(i).getCid() != 0) {
                x.append("------------------------------------------------------------------\n");
                x.append("Celda ID: ").append(allTower.get(i).getCid()).append("  ").append("Celda LAC: ").append(allTower.get(i).getLac()).append("\n");
                x.append("Último uso: ").append(allTower.get(i).getDate()).append("\n");
            }
        }
        x.append("------------------------------------------------------------------\n");
        tx.setText(x.toString());

        return view;
    }
}
