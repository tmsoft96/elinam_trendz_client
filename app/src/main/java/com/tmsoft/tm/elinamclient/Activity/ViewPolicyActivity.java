package com.tmsoft.tm.elinamclient.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.tmsoft.tm.elinamclient.Adapters.TabAdapter.PolicyTabsAdapter;
import com.tmsoft.tm.elinamclient.R;

public class ViewPolicyActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_policy);

        myToolBar = findViewById(R.id.viewPolicy_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Our Policy");

        //Tabs in main activity
        myViewPager = findViewById(R.id.viewPolicy_viewPager);
        myViewPager.setAdapter(new PolicyTabsAdapter(getSupportFragmentManager(), this));
        myTabLayout = findViewById(R.id.viewPolicy_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
