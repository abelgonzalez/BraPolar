package br.example.com.brapolar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    List<String> titles = new ArrayList<>();
    List<Fragment> tabs = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return tabs.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    public void AddItem(Fragment f, String title) {
        titles.add(title);
        tabs.add(f);
    }

    @Override
    public int getCount() {
        return tabs.size();
    }
}
