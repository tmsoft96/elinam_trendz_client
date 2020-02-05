package com.tmsoft.tm.elinamclient.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.tmsoft.tm.elinamclient.R;

import java.io.IOException;
import java.io.InputStream;

public class LicenseActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        myToolBar = findViewById(R.id.license_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("App License");

        text = findViewById(R.id.license_text);

        String path = "app_license.txt";
        readFile(path);

    }

    public String readFile(String inFile) {
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
            Log.i("Error", e.getMessage());
        }

        return tContents;

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
