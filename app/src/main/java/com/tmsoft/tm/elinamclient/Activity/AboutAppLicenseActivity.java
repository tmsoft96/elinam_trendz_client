package com.tmsoft.tm.elinamclient.Activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class AboutAppLicenseActivity extends AppCompatActivity {

    private TextView text;

    private DateAndTime dateAndTime;
    private CatchErrors catchErrors;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        Toolbar myToolBar = findViewById(R.id.about_toolbar);
        setSupportActionBar(myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_TermAndPrivacyPolicy);
        dateAndTime = new DateAndTime();
        catchErrors = new CatchErrors();

        text = findViewById(R.id.about_text);

        String path = "Elinam_Trendz.txt";

        try{
            readFile(path);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "AboutAppLicenseActivity", "main method");
        }
    }

    public void readFile(String inFile) {
        String tContents = "";

        try {
            InputStream stream = getAssets().open(inFile);

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            tContents = new String(buffer);
            text.setText(tContents);
        } catch (IOException e) {
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "AboutAppLicenseActivity", "readFile method");
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
