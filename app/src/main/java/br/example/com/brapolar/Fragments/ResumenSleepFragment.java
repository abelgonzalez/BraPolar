package br.example.com.brapolar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.example.com.brapolar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResumenSleepFragment extends Fragment {

    public ResumenSleepFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resumen_sleep, container, false);
    }
}
