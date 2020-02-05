package com.tmsoft.tm.elinamclient.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.R;

public class UserSettingsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private LinearLayout relAboutUs, relLogOut, relOurPolicy, relFeedback, relShare;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;

    private String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("AppLink")){
                        link = dataSnapshot.child("AppLink").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        myToolBar = findViewById(R.id.userSettings_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");

        relAboutUs = findViewById(R.id.userSettings_relAboutUs);
        relLogOut = findViewById(R.id.userSettings_relLogOut);
        relFeedback = findViewById(R.id.userSettings_relFeedback);
        relOurPolicy = findViewById(R.id.userSettings_relOurPolicy);
        relShare = findViewById(R.id.userSettings_relShare);

        relAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToAboutUsActivity();
            }
        });

        relLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendUserToLogInActivity();
            }
        });

        relFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSettingsActivity.this, FeedBackActivity.class);
                intent.putExtra("userId", getCurrentUserId);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        relShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = link;
                String shareSub = "Elinam Trendz";
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Share using ..."));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        relOurPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToOurPolicyActivity();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToLogInActivity(){
        Intent intent = new Intent(UserSettingsActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendUserToOurPolicyActivity(){
        Intent intent = new Intent(UserSettingsActivity.this, ViewPolicyActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToAboutUsActivity(){
        Intent intent = new Intent(UserSettingsActivity.this, AboutUsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
