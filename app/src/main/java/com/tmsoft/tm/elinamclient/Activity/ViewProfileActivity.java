package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private TextView fullName, phoneNumber, address, email;
    private Button editProfile, viewOrders, chatUs, favoriteList;
    private CircleImageView profilePicture;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String getCurrentUserId, pProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fullName = findViewById(R.id.viewProfile_fullName);
        phoneNumber = findViewById(R.id.viewProfile_phoneNumber);
        address = findViewById(R.id.viewProfile_address);
        editProfile = findViewById(R.id.viewProfile_editProfile);
        viewOrders = findViewById(R.id.viewProfile_viewOrders);
        chatUs = findViewById(R.id.viewProfile_chatUs);
        profilePicture = findViewById(R.id.viewProfile_profilePicture);
        email = findViewById(R.id.viewProfile_email);
        favoriteList = findViewById(R.id.viewProfile_favoriteList);

        myToolBar = findViewById(R.id.viewProfile_navToolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        getAllUserInformation();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToProfileActivity();
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPicture(pProfilePicture);
            }
        });


        favoriteList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToFavoriteActivity();
            }
        });

        viewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewOrderActivity();
            }
        });

        chatUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewProfileActivity.this, ChatActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }



    private void sendUserToFavoriteActivity() {
        Intent intent = new Intent(ViewProfileActivity.this, FavoriteActivity.class);
        startActivity(intent);
    }


    private void openPicture(final String pProfilePicture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewProfileActivity.this);

        final ImageView imageView = new ImageView(ViewProfileActivity.this);
        //loading picture offline
        Picasso.get().load(pProfilePicture).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(pProfilePicture)
                        .placeholder(R.drawable.no_image).into(imageView);
            }
        });
        builder.setView(imageView);
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }


    private void sendUserToProfileActivity() {
        Intent intent = new Intent(ViewProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

    private void sendUserToViewOrderActivity(){
        Intent intent = new Intent(ViewProfileActivity.this, ViewProductOrderActivity.class);
        startActivity(intent);
    }



    private void getAllUserInformation() {
        databaseReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("fullName")){
                        String pFullName = dataSnapshot.child("fullName").getValue().toString();
                        fullName.setText(pFullName);
                    }
                    if(dataSnapshot.hasChild("phoneNumber")){
                        String pPhoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                        phoneNumber.setText(pPhoneNumber);
                    }
                    if(dataSnapshot.hasChild("location")){
                        String pLocation = dataSnapshot.child("location").getValue().toString();
                        address.setText(pLocation);
                    }
                    if(dataSnapshot.hasChild("profilePicture")){
                        pProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                        //loading picture offline
                        Picasso.get().load(pProfilePicture).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(pProfilePicture)
                                        .placeholder(R.drawable.profile_image).into(profilePicture);
                            }
                        });
                    }
                    String pEmail = mAuth.getCurrentUser().getEmail();
                    email.setText(pEmail);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
