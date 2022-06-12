package br.example.com.brapolar.Activities;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import br.example.com.brapolar.Fragments.CallFragment;
import br.example.com.brapolar.Fragments.InputFragment;
import br.example.com.brapolar.Fragments.SmsFragment;
import br.example.com.brapolar.R;
import br.example.com.brapolar.ViewPagerAdapter;

public class SocialActivity extends AppCompatActivity {
    private AppBarLayout appBar;
    private TabLayout tabs;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.social_name));

        //Tags Menu
        tabs = (TabLayout) findViewById(R.id.tabs_social);
        viewPager = (ViewPager) findViewById(R.id.pager_social);
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
        adapter.AddItem(new CallFragment(), getString(R.string.social_call));
        adapter.AddItem(new SmsFragment(), getString(R.string.social_sms));
        adapter.AddItem(new InputFragment(), getString(R.string.input_name));

        view.setAdapter(adapter);
    }
}
