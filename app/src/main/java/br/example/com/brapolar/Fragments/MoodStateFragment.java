package br.example.com.brapolar.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Comparator;

import br.example.com.brapolar.Entities.EntitySms;
import br.example.com.brapolar.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoodStateFragment extends Fragment {

    public MoodStateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mood_state, container, false);
    }
}
