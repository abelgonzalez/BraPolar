package br.example.com.brapolar.Fragments;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.example.com.brapolar.Entities.EntityInput;
import br.example.com.brapolar.Entities.EntityInputText;
import br.example.com.brapolar.R;
import br.example.com.brapolar.Utils.TimeCount;

/**
 * A simple {@link Fragment} subclass.
 */
public class InputFragment extends Fragment {

    public InputFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        TextView tx = (TextView) view.findViewById(R.id.social_input);
        List<EntityInputText> texts = EntityInput.Fragmentado(getContext());
        double totalTime = 0;
        long cantWord = 0;
        long errors = 0;
        Map<String, Long> mapa = new HashMap<String, Long>();
        for (int i = 0; i < texts.size(); i++) {
            totalTime = texts.get(i).getTime(getContext());
            cantWord += texts.get(i).getCount();
            errors += texts.get(i).getError();

            if (!mapa.containsKey(texts.get(i).getApp())) {
                mapa.put(texts.get(i).getApp(), (long) totalTime);
            } else {
                long duration = mapa.get(texts.get(i).getApp()) + (long) totalTime;
                mapa.put(texts.get(i).getApp(), duration);
            }
        }
        double speed = EntityInputText.UtilToWPM(totalTime, cantWord / 5);
        final PackageManager pm = getContext().getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        StringBuilder x = new StringBuilder("Velocidade de Escrita: " + Math.floor(speed) + getContext().getString(R.string.input_speed_unit) + "\n");
        x.append("Quantidade de Palavras: " + String.valueOf(cantWord / 5 + (cantWord % 5 == 0 ? 0 : 1)) + "\n");
        x.append("Quantidade de Erros: " + String.valueOf(errors) + "\n");
        x.append("------------------------------------------------\n");
        for (Map.Entry<String, Long> entry : mapa.entrySet()) {
            x.append("app: " + entry.getKey() + " time: " + TimeCount.toShortTime(entry.getValue() / 1000) + "\n");
        }
        /*for(int i=0;i<texts.size();i++){
            x.append("entrada "+String.valueOf(i+1) + "\n");
            for(int j=0;j<texts.get(i).getKeys().size();j++){
               EntityInput a = texts.get(i).getKeys().get(j);
                if(a.getType().equals(getContext().getString(R.string.input_erase))){
                    x.append("erase key\n");
                }
                else{
                    x.append("regular key\n");
                }
            }
        }*/
        tx.setText(x);
        return view;
    }
}
