package com.tmsoft.tm.elinamclient.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;

import java.util.Objects;

public class AppInfoActivity extends AppCompatActivity {

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);

        RelativeLayout term = findViewById(R.id.app_term_and_condition);
        RelativeLayout license = findViewById(R.id.app_license);

        Toolbar myToolBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_AppInfo);

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();

        term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToTermActivity();
            }
        });

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToLicenseActivity();
            }
        });
    }

    private void sendUserToLicenseActivity() {
        try{
            Intent intent = new Intent(AppInfoActivity.this, LicenseActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "AppInfoActivity", "sendUserToLicenseActivity method");
        }
    }

    private void sendUserToTermActivity() {
        try{
            Intent intent = new Intent(AppInfoActivity.this, AboutAppLicenseActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "AppInfoActivity", "sendUserToTermActivity method");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
