package com.tmsoft.tm.elinamclient.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;
import com.tmsoft.tm.elinamclient.Urls.InnerNotification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BuyForMeActivity extends AppCompatActivity {

    private EditText productName, productDescription, quantityNeeded;
    private Spinner urgent;
    private TextView deliveryTypeText, deliveryDetails;
    private ImageView productPicture;
    private Button order;
    private ProgressDialog progressDialog;
    private Dialog dialog, deliveryDialog, messageDialog, previousDataDialog, deliveryFormDialog, postBoxDialog;

    private DatabaseReference databaseReference, notificationReference, userReference;
    private String userId;
    private StorageReference storageReference;

    private int gallaryPick;
    private Uri imageUri;
    private String getPictureUri, name, num, emailAddress;
    private String notifySender1, notifySender2, notifySender3;
    private InnerNotification innerNotification;
    private ArrayList<String> notifySenderList;

    private String deliveryType, defaultValue, userTown, userLocation, postRegion, postTown, postDistrict, postBoxNumber;
    private String tempTownName = null, tempLocation = null, tempRegion = null, tempDistrict = null, tempTownNamePost = null,
            tempBoxNumber = null;

    private long number = 0;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_for_me);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.buyForMe_toolbar);
        productName = (EditText) findViewById(R.id.buyForMe_productName);
        productDescription = (EditText) findViewById(R.id.buyForMe_productDescription);
        quantityNeeded = (EditText) findViewById(R.id.buyForMe_qtyNeeded);
        urgent = (Spinner) findViewById(R.id.buyForMe_productUrgency);
        productPicture = (ImageView) findViewById(R.id.buyForMe_productPicture);
        ImageView upload = (ImageView) findViewById(R.id.buyForMe_productUpload);
        order = (Button) findViewById(R.id.buyForMe_order);
        progressDialog = new ProgressDialog(this);
        ImageView info = (ImageView) findViewById(R.id.buyForMe_info);
        deliveryDetails = (TextView) findViewById(R.id.buyForMe_deliveryDetails);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        deliveryDialog = new Dialog(BuyForMeActivity.this, R.style.Theme_CustomDialog);
        messageDialog = new Dialog(BuyForMeActivity.this, R.style.Theme_CustomDialog);
        previousDataDialog = new Dialog(BuyForMeActivity.this, R.style.Theme_CustomDialog);
        deliveryFormDialog = new Dialog(BuyForMeActivity.this, R.style.Theme_CustomDialog);
        postBoxDialog = new Dialog(BuyForMeActivity.this, R.style.Theme_CustomDialog);
        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();
        deliveryTypeText = (TextView) findViewById(R.id.buyForMe_deliveryTypeText);
        notifySenderList = new ArrayList<>();

        deliveryDetails.setPaintFlags(deliveryDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Buy for me");
        databaseReference.keepSynced(true);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        userReference.keepSynced(true);

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_BuyForMe);

        try{
            getNotifySenderDetails();
            getUserInformation();
            getCounter();
            determinePreviousDetail();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "BuyForMeActivity", "main method");
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    gallaryPick = 1;
                    Intent gallaryIntent = new Intent();
                    gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    gallaryIntent.setType("image/*");
                    startActivityForResult(gallaryIntent, gallaryPick);
                } catch (Exception e){
                    Log.i("error", e.getMessage());
                    catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                            "BuyForMeActivity", "main method upload");
                }
            }
        });

        productPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (TextUtils.isEmpty(getPictureUri))
                        Toast.makeText(BuyForMeActivity.this, "Please upload Product Picture", Toast.LENGTH_SHORT).show();
                    else{
                        Intent intent = new Intent(BuyForMeActivity.this, ViewPictureActivity.class);
                        intent.putExtra("image", getPictureUri);
                        intent.putExtra("imageText", productName.getText().toString());
                        startActivity(intent);
                    }
                } catch (Exception e){
                    Log.i("error", e.getMessage());
                    catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                            "BuyForMeActivity", "main method productPicture");
                }
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pName = productName.getText().toString();
                String pDescription = productDescription.getText().toString();
                String urgency = urgent.getSelectedItem().toString();
                String qty = quantityNeeded.getText().toString();

                if (TextUtils.isEmpty(pName))
                    Toast.makeText(BuyForMeActivity.this, "please enter product name", Toast.LENGTH_SHORT).show();
                else if (urgency.equals("Please select"))
                    Toast.makeText(BuyForMeActivity.this, "please select how urgent you need the product", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(qty))
                    Toast.makeText(BuyForMeActivity.this, "please enter quantity needed", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(deliveryType) && TextUtils.isEmpty(userLocation))
                    Toast.makeText(BuyForMeActivity.this, "please complete delivery details", Toast.LENGTH_SHORT).show();
                else
                    saveOrder(pName, pDescription, urgency, qty);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
            }
        });

        deliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDeliveryDialog();
            }
        });
    }

    //Showing delivery dialog
    private void viewDeliveryDialog() {
        final RelativeLayout layoutPersonal, layoutBusTerminal, layoutPoxBox;
        Button dialogClose;

        deliveryDialog.setContentView(R.layout.dialog_delivery);
        layoutPersonal = deliveryDialog.findViewById(R.id.dialog_delivery_relative_personal);
        layoutBusTerminal = deliveryDialog.findViewById(R.id.dialog_delivery_relative_bus_terminal);
        layoutPoxBox = deliveryDialog.findViewById(R.id.dialog_delivery_relative_post_box);
        dialogClose = deliveryDialog.findViewById(R.id.dialog_delivery_close);

        layoutPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageDialog();
                deliveryType = "Personnel Delivery";
            }
        });

        layoutBusTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(BuyForMeActivity.this, "Still working on this delivery option", Toast.LENGTH_LONG).show();
            }
        });

        layoutPoxBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageDialog();
                deliveryType = "Post Box Delivery";
            }
        });

        dialogClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deliveryDialog.dismiss();
            }
        });

        deliveryDialog.show();
    }

    //showing Message to the customer dialog
    private void showMessageDialog() {
        TextView message, closeMsg;
        Button deliveryDetails;

        messageDialog.setContentView(R.layout.dialog_order_message);
        message = messageDialog.findViewById(R.id.dialogOrderMsg_message);
        deliveryDetails = messageDialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);
        closeMsg = messageDialog.findViewById(R.id.dialogOrderMsg_close);

        String msg = "Hello " + name + ", our customer service will contact you in some few minutes to confirm your order.\n"
                + "You can proceed to fill the delivery form";

        message.setText(msg);

        deliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (defaultValue.equals("temp"))
                    showFillDeliveryDetailsForPersonnelDelivery();
                else
                    showPreviousDataDialog();
            messageDialog.dismiss();
            }
        });

        closeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageDialog.dismiss();
            }
        });

        messageDialog.show();
    }

    private void determinePreviousDetail(){
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(userId);
        dRef.child("deliveryDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    defaultValue = "default";
                } else
                    defaultValue = "temp";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showPreviousDataDialog() {
        TextView msg;
        Button yes, no;

        previousDataDialog.setContentView(R.layout.dialog_question);
        msg = previousDataDialog.findViewById(R.id.dialogQuestion_message);
        yes = previousDataDialog.findViewById(R.id.dialogQuestion_yes);
        no = previousDataDialog.findViewById(R.id.dialogQuestion_no);

        String message = "Do you want to load your previous details ?";

        msg.setText(message);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserPreviousDetails();
                previousDataDialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousDataDialog.dismiss();
                showFillDeliveryDetailsForPersonnelDelivery();
            }
        });

        previousDataDialog.show();
    }

    //loading previous data of the user
    private void getUserPreviousDetails() {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(userId);
        dRef.child("deliveryDetails").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("userTown"))
                        tempTownName = dataSnapshot.child("userTown").getValue().toString();

                    if (dataSnapshot.hasChild("userLocation"))
                        tempLocation = dataSnapshot.child("userLocation").getValue().toString();

                    if (dataSnapshot.hasChild("postRegion"))
                        tempRegion = dataSnapshot.child("postRegion").getValue().toString();

                    if (dataSnapshot.hasChild("postTown"))
                        tempTownNamePost = dataSnapshot.child("postTown").getValue().toString();

                    if (dataSnapshot.hasChild("postDistrict"))
                        tempDistrict = dataSnapshot.child("postDistrict").getValue().toString();

                    if (dataSnapshot.hasChild("postBoxNumber"))
                        tempBoxNumber = dataSnapshot.child("postBoxNumber").getValue().toString();

                    dialog.dismiss();
                    showFillDeliveryDetailsForPersonnelDelivery();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //show Fill Delivery Details for personnel delivery
    private void showFillDeliveryDetailsForPersonnelDelivery(){
        try{
            final EditText fullName, phoneNumber, email, townName, deliveryLocation;
            Button makePayment;
            TextView deliveryClose;

            deliveryFormDialog.setContentView(R.layout.dialog_delivery_form_personnel_delivery);
            fullName = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_profileFullName);
            phoneNumber = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_profilePhoneNumber);
            email = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_profileEmailAddress);
            townName = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_townName);
            deliveryLocation = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_profileLocation);
            deliveryClose = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_close);
            makePayment = deliveryFormDialog.findViewById(R.id.dialogDeliveryForm_order);

            fullName.setText(name);
            phoneNumber.setText(num);
            email.setText(emailAddress);

            townName.setText(tempTownName);
            deliveryLocation.setText(tempLocation);

            if (deliveryType.equals("Post Box Delivery"))
                makePayment.setText("Post Box Details");
            else
                makePayment.setText("Done");

            fullName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(BuyForMeActivity.this, "Edit your name in your profile", Toast.LENGTH_LONG).show();
                }
            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(BuyForMeActivity.this, "You cannot change your default email address", Toast.LENGTH_LONG).show();
                }
            });


            makePayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String uFull, uTown, uDelivery;
                    uFull = fullName.getText().toString();
                    uTown = townName.getText().toString();
                    uDelivery = deliveryLocation.getText().toString();

                    if (TextUtils.isEmpty(uFull))
                        Toast.makeText(BuyForMeActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(uTown))
                        Toast.makeText(BuyForMeActivity.this, "Enter your town name", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(uDelivery))
                        Toast.makeText(BuyForMeActivity.this, "Enter your delivery location", Toast.LENGTH_SHORT).show();
                    else {
                        if (deliveryType.equals("Personnel Delivery")){
                            userTown = uTown;
                            userLocation = uDelivery;
                            tempTownName = uTown;
                            tempLocation = uDelivery;
                            deliveryFormDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                            builder.setMessage("You can now proceed to order your product")
                                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            order.setEnabled(true);
                                            dialog.dismiss();
                                            deliveryDialog.dismiss();
                                            deliveryTypeText.setText("Personnel Delivery");
                                            deliveryDetails.setText("Edit Delivery Details");

                                        }
                                    });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            //personalDialogShow(uFull, uPhone, uTown, uEmail, uDelivery, productQuantity);
                        } else if (deliveryType.equals("Post Box Delivery")){
                            postBoxDialogShow();
                        }
                    }
                }
            });

            deliveryClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deliveryFormDialog.dismiss();
                }
            });

            deliveryFormDialog.show();
        } catch (Exception ex){
            Log.i("error", ex.getMessage());
        }
    }

    private void postBoxDialogShow() {
        TextView close;
        final EditText district, townName, bozNumber;
        Button payment;
        final Spinner region;

        postBoxDialog.setContentView(R.layout.dialog_delivery_form_post_box_delivery);
        close = postBoxDialog.findViewById(R.id.dialogPostDeliveryForm_close);
        region = postBoxDialog.findViewById(R.id.dialogPostDeliveryForm_region);
        district = postBoxDialog.findViewById(R.id.dialogPostDeliveryForm_district);
        townName = postBoxDialog.findViewById(R.id.dialogPostDeliveryForm_townName);
        bozNumber = postBoxDialog.findViewById(R.id.dialogPostDeliveryForm_boxNumber);
        payment = postBoxDialog.findViewById(R.id.dialogPostDeliveryForm_payment);

        payment.setText("Done");

        region.setSelection(((ArrayAdapter) region.getAdapter()).getPosition(tempRegion));
        district.setText(tempDistrict);
        townName.setText(tempTownNamePost);
        bozNumber.setText(tempBoxNumber);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postBoxDialog.dismiss();
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getRegion = region.getSelectedItem().toString();
                final String getDistrict = district.getText().toString();
                final String getTown = townName.getText().toString();
                final String getBoxNum = bozNumber.getText().toString();

                if (TextUtils.isEmpty(getRegion))
                    Toast.makeText(BuyForMeActivity.this, "Enter your region", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getTown))
                    Toast.makeText(BuyForMeActivity.this, "Enter your town", Toast.LENGTH_SHORT).show();
                else if (getRegion.equalsIgnoreCase("Select your region"))
                    Toast.makeText(BuyForMeActivity.this, "Select your current region", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getBoxNum))
                    Toast.makeText(BuyForMeActivity.this, "Enter your post box number", Toast.LENGTH_SHORT).show();
                else {
                    postBoxNumber = getBoxNum;
                    postDistrict = getDistrict;
                    postRegion = getRegion;
                    postTown = getTown;
                    tempBoxNumber = getBoxNum;
                    tempDistrict = getDistrict;
                    tempRegion = getRegion;
                    tempTownName = getTown;
                    AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                    builder.setMessage("You can now proceed to order your product")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    order.setEnabled(true);
                                    dialog.dismiss();
                                    deliveryDialog.dismiss();
                                    postBoxDialog.dismiss();
                                    deliveryFormDialog.dismiss();
                                    deliveryTypeText.setText("Post Box Delivery");
                                    deliveryDetails.setText("Edit Delivery Details");
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    postBoxDialog.dismiss();
                }
            }
        });

        postBoxDialog.show();
    }


    private void getInfo(){
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category Details");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Buy For Me")){
                        String gg = dataSnapshot.child("Buy For Me").getValue().toString();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(BuyForMeActivity.this);
                        alertDialog.setMessage(gg)
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //User finish reading
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    } else
                        Toast.makeText(BuyForMeActivity.this, "Error occurred...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNotifySenderDetails() {
        DatabaseReference notifySender = FirebaseDatabase.getInstance().getReference().child("ServerId");
        notifySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("0")) {
                        notifySender1 = dataSnapshot.child("0").getValue().toString();
                        notifySenderList.add(notifySender1);
                    }

                    if (dataSnapshot.hasChild("1")) {
                        notifySender2 = dataSnapshot.child("1").getValue().toString();
                        notifySenderList.add(notifySender2);
                    }

                    if (dataSnapshot.hasChild("2")) {
                        notifySender3 = dataSnapshot.child("2").getValue().toString();
                        notifySenderList.add(notifySender3);
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Show all user information and save it
    private void getUserInformation() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("fullName")){
                        name = dataSnapshot.child("fullName").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("phoneNumber")){
                        num = dataSnapshot.child("phoneNumber").getValue().toString();
                    }

                    emailAddress = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                } else {
                    Toast.makeText(BuyForMeActivity.this, "Profile details not completed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getCounter(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    number = dataSnapshot.getChildrenCount();
                } else
                    number = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveOrder(final String pName, String pDescription, String urgency, String qty) {
        try{
            progressDialog.setMessage("Ordering...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            Calendar postDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
            String saveCurrentDate = currentDateFormat.format(postDate.getTime());

            Calendar postTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
            String saveCurrentTime = currentTimeFormat.format(postTime.getTime());

            Map orderMap = new HashMap();
            orderMap.put("number", number);
            orderMap.put("orderDate", saveCurrentDate);
            orderMap.put("orderTime", saveCurrentTime);
            orderMap.put("userId", userId);
            orderMap.put("productName", pName);
            orderMap.put("productDetails", pDescription);
            orderMap.put("productUrgent", urgency);
            orderMap.put("productQuantity", qty);
            orderMap.put("productImage", getPictureUri);
            orderMap.put("userLocation", userLocation);
            orderMap.put("userTown", userTown);
            orderMap.put("deliveryType", deliveryType);
            orderMap.put("postRegion", postRegion);
            orderMap.put("postTown", postTown);
            orderMap.put("postDistrict", postDistrict);
            orderMap.put("postBoxNumber", postBoxNumber);
            orderMap.put("orderConfirm", "Waiting for confirmation");

            databaseReference.push();
            final String key = databaseReference.getKey();

            databaseReference.setValue(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        //sending notification
                        final HashMap<String, String> notificationData = new HashMap<>();
                        notificationData.put("from", userId);
                        notificationData.put("title", "Product Order");
                        notificationData.put("message", "Buy for me order - " + pName);

                        notificationReference.child(notifySender1).push().setValue(notificationData)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            if (!TextUtils.isEmpty(notifySender2)){
                                                notificationReference.child(notifySender2).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()){
                                                                    String err = task.getException().getMessage();
                                                                    Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }

                                            if (!TextUtils.isEmpty(notifySender3)){
                                                notificationReference.child(notifySender3).push().setValue(notificationData)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (!task.isSuccessful()){
                                                                    String err = task.getException().getMessage();
                                                                    Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }

                                            String[] noti = new String[notifySenderList.size()];
                                            for(int x = 0; x < noti.length; ++x){
                                                noti[x] = notifySenderList.get(x);
                                            }

                                            innerNotification = new InnerNotification(
                                                    "order",
                                                    "no",
                                                    userId,
                                                    "BuyForMeActivity",
                                                    ServerValue.TIMESTAMP.toString(),
                                                    "Buy for me order - " + pName,
                                                    "Product Order in \'Buy for Me\' section",
                                                    notifySenderList,
                                                    key
                                            );

                                            boolean deter = innerNotification.onSaveAll();
                                            if (deter){
                                                progressDialog.dismiss();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                                                builder.setMessage("Product Ordered successfully. Please review product in the order " +
                                                        "history and wait for confirmation to proceed. \nThank you")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                try{
                                                                    deliveryDialog.dismiss();
                                                                    messageDialog.dismiss();
                                                                    previousDataDialog.dismiss();
                                                                    deliveryFormDialog.dismiss();
                                                                    postBoxDialog.dismiss();
                                                                } catch (Exception e){
                                                                    Log.i("error", e.getMessage());
                                                                }
                                                                finish();
                                                            }
                                                        });

                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            } else {
                                               /* progressDialog.dismiss();
                                                AllMessage msg = new AllMessage();
                                                Toast.makeText(BuyForMeActivity.this, msg.getErrorMessage(), Toast.LENGTH_SHORT).show();*/
                                                progressDialog.dismiss();
                                                AlertDialog.Builder builder = new AlertDialog.Builder(BuyForMeActivity.this);
                                                builder.setMessage("Product Ordered successfully. Please review product in the order " +
                                                        "history and wait for confirmation to proceed. \nThank you")
                                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                                try{
                                                                    deliveryDialog.dismiss();
                                                                    messageDialog.dismiss();
                                                                    previousDataDialog.dismiss();
                                                                    deliveryFormDialog.dismiss();
                                                                    postBoxDialog.dismiss();
                                                                } catch (Exception e){
                                                                    Log.i("error", e.getMessage());
                                                                }
                                                                finish();
                                                            }
                                                        });

                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        } else {
                                            String err = task.getException().getMessage();
                                            Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                    } else {
                        String err = task.getException().getMessage();
                        Toast.makeText(BuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "BuyForMeActivity", "saveOrder method");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallaryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            saveProductPictureOneToFirebaseStorage();
        }
    }

    private void saveProductPictureOneToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        if (!TextUtils.isEmpty(getPictureUri)){
            try{
                StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getPictureUri);
                picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        savePic();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        savePic();
                    }
                });
            } catch (Exception ex){
                savePic();
            }
        } else
            savePic();
    }

    private void savePic() {
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTimeFormat.format(postTime.getTime());

        String saveRandomName = saveCurrentDate + userId + saveCurrentTime;

        StorageReference filePath = storageReference.child("Product Pictures").child(userId)
                .child(imageUri.getLastPathSegment() + saveRandomName + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getPictureUri = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(getPictureUri).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getPictureUri).fit()
                                        .placeholder(R.drawable.warning).into(productPicture);
                            }
                        });
                        Toast.makeText(BuyForMeActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        gallaryPick = 0;
                        progressDialog.dismiss();
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(BuyForMeActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
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
}
