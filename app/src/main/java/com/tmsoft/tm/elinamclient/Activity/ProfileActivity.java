package com.tmsoft.tm.elinamclient.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private CircleImageView profilePicture;
    private EditText fullName, phoneNumber, townName, location;
    private Button saveButton;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference userStorageRef;
    private String currentUserId;

    private int gallaryPick = 1;
    private String getPictureUri;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;

    private CropImage.ActivityResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = findViewById(R.id.profile_navToobar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        profilePicture = findViewById(R.id.profileProfilePicture);
        fullName = findViewById(R.id.profileFullname);
        phoneNumber = findViewById(R.id.profilePhoneNumber);
        townName = findViewById(R.id.townName);
        location = findViewById(R.id.profileLocation);
        saveButton = findViewById(R.id.btn_saveProfile);
        progressDialog = new ProgressDialog(this);
        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        databaseReference.keepSynced(true);
        userStorageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUserProfile();
            }
        });

        viewProfile();
    }



    private void viewProfile() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profilePicture")) {
                        getPictureUri = dataSnapshot.child("profilePicture").getValue().toString();
                        //loading picture offline
                        Picasso.get().load(getPictureUri).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(getPictureUri)
                                        .placeholder(R.drawable.profile_image)
                                        .into(profilePicture);
                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "please select profile picture first", Toast.LENGTH_SHORT).show();
                    }

                    if (dataSnapshot.hasChild("fullName")){
                        String name = dataSnapshot.child("fullName").getValue().toString();
                        fullName.setText(name);
                    }
                    if (dataSnapshot.hasChild("phoneNumber")){
                        String number = dataSnapshot.child("phoneNumber").getValue().toString();
                        phoneNumber.setText(number);
                    }
                    if (dataSnapshot.hasChild("location")){
                        String loc = dataSnapshot.child("location").getValue().toString();
                        location.setText(loc);
                    }

                    if (dataSnapshot.hasChild("townName")){
                        String town = dataSnapshot.child("townName").getValue().toString();
                        townName.setText(town);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    private void registerUserProfile() {
        try{
            String name = fullName.getText().toString();
            String phoneNum = phoneNumber.getText().toString();
            String town = townName.getText().toString();
            String loc = location.getText().toString();


            if(TextUtils.isEmpty(name))
                Toast.makeText(this, "Enter your full name", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(phoneNum))
                Toast.makeText(this, "Enter your phone number", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(town))
                Toast.makeText(this, "Enter your town name", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(loc))
                Toast.makeText(this, "Describe your location", Toast.LENGTH_SHORT).show();
            else{
                progressDialog.setTitle("Saving Profile");
                progressDialog.setMessage("Please wait patiently for your profile to be saved");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                String email = mAuth.getCurrentUser().getEmail();

                HashMap userMap = new HashMap();
                userMap.put("userId", currentUserId);
                userMap.put("fullName", name);
                userMap.put("phoneNumber", phoneNum);
                userMap.put("townName", town);
                userMap.put("location", loc);
                userMap.put("email", email);
                userMap.put("profilePicture", getPictureUri);
                databaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            sendUserToMainActivity();
                            Toast.makeText(ProfileActivity.this, "saved successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else{
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        } catch (Exception e){
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(), "ProfileActivity", "registerUserProfile");
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallaryPick && resultCode == RESULT_OK && data != null){
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            result = CropImage.getActivityResult(data);


            if (resultCode == RESULT_OK){

                progressDialog.setTitle("Uploading Profile Picture");
                progressDialog.setMessage("Please wait patiently while uploading your profile picture");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();

                if (!TextUtils.isEmpty(getPictureUri)){
                    try{
                        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri);
                        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                saveProfile();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                saveProfile();
                            }
                        });
                    } catch (Exception ex){
                        saveProfile();
                    }
                } else
                    saveProfile();
            } else {
                Toast.makeText(this, "Failed to upload image try again...", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }
    }

    private void saveProfile(){
        Uri resultUri = result.getUri();

        StorageReference filePath = userStorageRef.child(currentUserId + ".jpg");
        filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri = uri.toString();
                        //loading picture offline
                        Picasso.get().load(getPictureUri).networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(getPictureUri).placeholder(R.drawable.profile_image)
                                        .into(profilePicture);
                            }
                        });
                        Toast.makeText(ProfileActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ProfileActivity.this, "Error : " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        try{
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } catch (Exception e){
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(), "ProfileActivity", "sendUserToMainActivity method");
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

}
