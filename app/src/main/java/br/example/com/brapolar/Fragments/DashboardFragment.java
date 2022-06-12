package br.example.com.brapolar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import br.example.com.brapolar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment /*implements View.OnClickListener*/ {

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        TextView textDate = (TextView) view.findViewById(R.id.text_date);
        Calendar c = Calendar.getInstance();
        String fecha = c.get(Calendar.DAY_OF_MONTH) + " " + String.valueOf(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH)) + " " + c.get(Calendar.YEAR);
        textDate.setText(fecha);

        /*TextView moodText = (TextView) view.findViewById(R.id.textView3);
        TextView socialText = (TextView) view.findViewById(R.id.textView4);
        TextView physicalText = (TextView) view.findViewById(R.id.textView5);
        TextView psychomotorText = (TextView) view.findViewById(R.id.textView6);
        TextView sleepText = (TextView) view.findViewById(R.id.textView7);
        TextView medicationText = (TextView) view.findViewById(R.id.textView8);
        moodText.setOnClickListener(this);
        socialText.setOnClickListener(this);
        physicalText.setOnClickListener(this);
        psychomotorText.setOnClickListener(this);
        sleepText.setOnClickListener(this);
        medicationText.setOnClickListener(this);
*/
        return view;
    }

//    @Override
//    public void onClick(View v) {
//        TabLayout tabhost = (TabLayout) getActivity().findViewById(R.id.tabs);
//        switch (v.getId()){
//            case R.id.textView3:
//                tabhost.getTabAt(1).select();
//                break;
//            case R.id.textView4:
//                tabhost.getTabAt(2).select();
//                break;
//            case R.id.textView5:
//                tabhost.getTabAt(3).select();
//                break;
//            case R.id.textView6:
//                tabhost.getTabAt(4).select();
//                break;
//            case R.id.textView7:
//                tabhost.getTabAt(5).select();
//                break;
//            case R.id.textView8:
//                tabhost.getTabAt(6).select();
//                break;
//        }
//
//    }
}
