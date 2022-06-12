package br.example.com.brapolar.Activities;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import br.example.com.brapolar.Fragments.CellFragment;
import br.example.com.brapolar.Fragments.ScreenFragment;
import br.example.com.brapolar.R;
import br.example.com.brapolar.ViewPagerAdapter;

public class PhysicalActivity extends AppCompatActivity {
    private AppBarLayout appBar;
    private TabLayout tabs;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.physical_name));

        //Tags Menu
        tabs = (TabLayout) findViewById(R.id.tabs_physical);
        viewPager = (ViewPager) findViewById(R.id.pager_physical);
        tabs.setupWithViewPager(viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        SetUpViewPager(viewPager, pagerAdapter);
        //end
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SetUpViewPager(ViewPager view, ViewPagerAdapter adapter) {
        adapter.AddItem(new CellFragment(), getString(R.string.app_tower_cell));
        adapter.AddItem(new ScreenFragment(), getString(R.string.app_screen));
        view.setAdapter(adapter);
    }
}
