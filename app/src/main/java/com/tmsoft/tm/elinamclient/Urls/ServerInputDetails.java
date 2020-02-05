package com.tmsoft.tm.elinamclient.Urls;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ServerInputDetails {
    private String phoneNumber, location, emailAddress;

    public ServerInputDetails() {
        DatabaseReference contactReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        contactReference.keepSynced(true);

        contactReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("PhoneNumber")){
                        phoneNumber = dataSnapshot.child("PhoneNumber").getValue().toString();
                        Log.i("info", phoneNumber);
                    }
                    if (dataSnapshot.hasChild("EmailAddress")){
                        emailAddress = dataSnapshot.child("EmailAddress").getValue().toString();
                        Log.i("info", emailAddress);
                    }
                    if (dataSnapshot.hasChild("Location")){
                        location = dataSnapshot.child("Location").getValue().toString();
                        Log.i("info", location);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
