package com.tmsoft.tm.elinamclient.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tmsoft.tm.elinamclient.Activity.FavoriteActivity;
import com.tmsoft.tm.elinamclient.Activity.ProfileActivity;
import com.tmsoft.tm.elinamclient.Activity.UserSettingsActivity;
import com.tmsoft.tm.elinamclient.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeProfileFragment extends Fragment {

    private View view;
    private TextView fullName, phoneNumber, address, email;
    private CircleImageView profilePicture;
    private ImageView edit, setting, cart;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String getCurrentUserId, pProfilePicture;

    public HomeProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_b_home_profile, container, false);

        fullName = view.findViewById(R.id.fragmentProfile_fullName);
        phoneNumber = view.findViewById(R.id.fragmentProfile_phoneNumber);
        address = view.findViewById(R.id.fragmentProfile_address);
        profilePicture = view.findViewById(R.id.fragmentProfile_profilePicture);
        email = view.findViewById(R.id.fragmentProfile_email);
        edit = view.findViewById(R.id.fragmentProfile_edit);
        setting = view.findViewById(R.id.fragmentProfile_setting);
        cart = view.findViewById(R.id.fragmentProfile_cart);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);

        getAllUserInformation();

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPicture(pProfilePicture);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToProfileActivity();
            }
        });

        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToFavoriteActivity();
            }
        });

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToUserSetting();
            }
        });

        return view;
    }


    private void openPicture(String pProfilePicture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final ImageView imageView = new ImageView(getContext());
        Picasso.get().load(pProfilePicture).into(imageView);
        builder.setView(imageView);
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
                        Picasso.get()
                                .load(pProfilePicture)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProfilePicture)
                                        .resize(50,50)
                                        .centerCrop()
                                        .placeholder(R.drawable.profile_image)
                                        .into(profilePicture);
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

    private void sendUserToProfileActivity() {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        startActivity(intent);
    }

    private void sendUserToFavoriteActivity() {
        Intent intent = new Intent(getContext(), FavoriteActivity.class);
        startActivity(intent);
    }

    private void sendUserToUserSetting(){
        Intent intent = new Intent(getContext(), UserSettingsActivity.class);
        startActivity(intent);
    }
}
