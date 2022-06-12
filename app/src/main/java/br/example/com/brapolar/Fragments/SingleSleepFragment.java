package br.example.com.brapolar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.example.com.brapolar.R;
import br.example.com.brapolar.Activities.SleepActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleSleepFragment extends Fragment {
    public SingleSleepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_sleep, container, false);
        SleepActivity activity = (SleepActivity) getActivity();
        activity.UpdateElements(view);
        return view;
    }
}
