package br.example.com.brapolar.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        Calendar c = Calendar.getInstance();
        int m, h;
        if (args != null) {
            c.set(Calendar.HOUR_OF_DAY, args.getInt("hour"));
            c.set(Calendar.MINUTE, args.getInt("minute"));
        }
        m = c.get(Calendar.MINUTE);
        h = c.get(Calendar.HOUR_OF_DAY);
        return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), h, m, android.text.format.DateFormat.is24HourFormat(getActivity()));
    }
}
