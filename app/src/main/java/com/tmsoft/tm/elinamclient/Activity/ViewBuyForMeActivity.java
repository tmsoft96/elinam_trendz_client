package com.tmsoft.tm.elinamclient.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class ViewBuyForMeActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private ImageView productImage, transactionPicture;
    private TextView productName, productDescription, quantityOrdered, urgency,
            userName, userLocation, userTown, userPhoneNumber, userEmailAddress, deliveryType, postBoxDetails,
            paymentType, sendFullName, amountSend, transactionId, senderNetwork, deliveryDate, deliveryFee,
            confirmDelivery, productConfirmation, paymentConfirmation, orderDate, totalAmount, productFee;
    private Button payment;
    private LinearLayout paymentDetails, deliveryDetails, senderNameLinear, amountPaidLinear, transactionLinear, senderNetworkLinear;
    private Dialog dialog, instantPaymentDialog;
    private ProgressDialog progressDialog;

    private String postKey, userId;
    private DatabaseReference databaseReference, notificationReference, userReference;
    private StorageReference storageReference;

    private String pTransactionPicture, pDeliveryDate, pDeliveryFee, pDeliverySuccess,
            pOrderConfirm, pProductFee, pProductImage, pProductName, pProductQuantity;
    private String zPostRegion, zPostTown, zPostDistrict, zPostNumber;
    private int gallaryPick = 1;
    private double pTotalAmount;
    private Uri imageUri;

    private String notifySender1, notifySender2, notifySender3;
    private String tempSenderName, tempAmountPaid, tempTransId, tempTransPicture;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;
    private InnerNotification innerNotification;
    private ArrayList<String> notifySenderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_buy_for_me);

        myToolbar = (Toolbar) findViewById(R.id.viewBuyForMe_toolbar);
        productImage = (ImageView) findViewById(R.id.viewBuyForMe_productPicture);
        transactionPicture = (ImageView) findViewById(R.id.viewBuyForMe_transactionPicture);
        productName = (TextView) findViewById(R.id.viewBuyForMe_productName);
        productDescription = (TextView) findViewById(R.id.viewBuyForMe_productDescription);
        quantityOrdered = (TextView) findViewById(R.id.viewBuyForMe_productQtyOrdered);
        urgency = (TextView) findViewById(R.id.viewBuyForMe_urgent);
        userName = (TextView) findViewById(R.id.viewBuyForMe_userFullName);
        userLocation = (TextView) findViewById(R.id.viewBuyForMe_userLocation);
        userTown = (TextView) findViewById(R.id.viewBuyForMe_userTown);
        userPhoneNumber = (TextView) findViewById(R.id.viewBuyForMe_userPhoneNumber);
        userEmailAddress = (TextView) findViewById(R.id.viewBuyForMe_userEmail);
        productName = (TextView) findViewById(R.id.viewBuyForMe_productName);
        deliveryType = (TextView) findViewById(R.id.viewBuyForMe_deliveryType);
        paymentType = (TextView) findViewById(R.id.viewBuyForMe_paymentType);
        sendFullName = (TextView) findViewById(R.id.viewBuyForMe_senderFullName);
        amountSend = (TextView) findViewById(R.id.viewBuyForMe_amountPaid);
        transactionId = (TextView) findViewById(R.id.viewBuyForMe_transactionId);
        senderNetwork = (TextView) findViewById(R.id.viewBuyForMe_senderNetwork);
        deliveryDate = (TextView) findViewById(R.id.viewBuyForMe_deliveryDate);
        postBoxDetails = (TextView) findViewById(R.id.viewBuyForMe_postDelivery);
        deliveryFee = (TextView) findViewById(R.id.viewBuyForMe_deliveryFee);
        productFee = (TextView) findViewById(R.id.viewBuyForMe_productFee);
        payment = (Button) findViewById(R.id.viewBuyForMe_payment);
        confirmDelivery = (TextView) findViewById(R.id.viewBuyForMe_deliveryConfirm);
        paymentDetails = (LinearLayout) findViewById(R.id.viewBuyForMe_paymentDetails);
        deliveryDetails = (LinearLayout) findViewById(R.id.viewBuyForMe_deliveryDetails);
        productConfirmation = (TextView) findViewById(R.id.viewBuyForMe_productConfirmation);
        orderDate = (TextView) findViewById(R.id.viewBuyForMe_orderDate);
        paymentConfirmation = (TextView) findViewById(R.id.viewBuyForMe_paymentConfirmation);
        totalAmount = (TextView) findViewById(R.id.viewBuyForMe_totalAmount);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        progressDialog = new ProgressDialog(this);
        senderNameLinear = (LinearLayout) findViewById(R.id.viewBuyForMe_linearSenderFullName);
        amountPaidLinear = (LinearLayout) findViewById(R.id.viewBuyForMe_linearAmountPaid);
        transactionLinear = (LinearLayout) findViewById(R.id.viewBuyForMe_linearTransactionId);
        senderNetworkLinear = (LinearLayout) findViewById(R.id.viewBuyForMe_linearSenderNetwork);
        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();
        notifySenderList = new ArrayList<>();


        postKey = getIntent().getExtras().get("postKey").toString();

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buy for me");

        paymentDetails.setVisibility(View.GONE);
        deliveryDetails.setVisibility(View.GONE);
        postBoxDetails.setVisibility(View.GONE);
        payment.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Buy for me");
        databaseReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child("Transaction Picture");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        userReference.keepSynced(true);

        try{
            getUserPrivateData();
            getAllInfo();
            getNotifySenderDetails();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ViewBuyForMeActivity", "main method");
        }

        postBoxDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPostDetails();
            }
        });

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToViewPictureActivity(pProductName, pProductImage);
            }
        });

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToViewPictureActivity(pProductName, pTransactionPicture);
            }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalDialogShow();
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

    private void personalDialogShow() {
        try{
            final Dialog personalDialog = new Dialog(ViewBuyForMeActivity.this, R.style.Theme_CustomDialog);
            Button makePaymentNow, makePaymentOnDelivery, viewAllCharges;
            personalDialog.setContentView(R.layout.dialog_choose_payment);

            makePaymentNow = personalDialog.findViewById(R.id.orderProductDialog_makePaymentNow);
            makePaymentOnDelivery = personalDialog.findViewById(R.id.orderProductDialog_makePaymentOnDelivery);
            viewAllCharges = personalDialog.findViewById(R.id.orderProductDialog_viewCharges);

            viewAllCharges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAllChargesDialog();
                }
            });

            makePaymentOnDelivery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    personalDialog.dismiss();
                    databaseReference.child(postKey).child("paymentType").setValue("Payment On Delivery").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Buy for me");
                                databaseReference.child(postKey).child("message").setValue("Payment on Delivery");
                                //DatabaseReference dRef2 = FirebaseDatabase.getInstance().getReference().child("Buy for me");
                                databaseReference.child(postKey).child("paymentConfirm").setValue("Payment on Delivery");
                                sendNotificationToServer("Payment On Delivery", "");
                                tempAmountPaid = "";
                                tempSenderName = "";
                                tempTransId = "";
                                tempTransPicture = "";
                                progressDialog.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(ViewBuyForMeActivity.this);
                                builder.setTitle("Payment");
                                builder.setMessage("Payment on delivery processed successfully");
                                builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                                AlertDialog dialog = builder.create();
                                dialog.show();
                                Toast.makeText(ViewBuyForMeActivity.this, "Payment process successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });

            makePaymentNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    personalDialog.dismiss();
                    showMakeInstantPayment();
                }
            });

            personalDialog.show();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ViewBuyForMeActivity", "personalDialogShow method");
        }
    }

    //showing instant payment details
    private void showMakeInstantPayment() {
        instantPaymentDialog = new Dialog(ViewBuyForMeActivity.this, R.style.Theme_CustomDialog);
        final TextView note, mobileMoneyNumber, close, totalMoney, totalMoneyDetails;
        final EditText senderName, amountPaid, transactionId;
        ImageButton upload;
        final ImageView transPicture;
        Button paymentButton;
        final Spinner senderNetworks;

        instantPaymentDialog.setContentView(R.layout.dialog_payment_layout);
        close = instantPaymentDialog.findViewById(R.id.payment_close);
        note = instantPaymentDialog.findViewById(R.id.payment_note);
        mobileMoneyNumber = instantPaymentDialog.findViewById(R.id.payment_mobileMoneyNumber);
        amountPaid = instantPaymentDialog.findViewById(R.id.payment_amountPaid);
        transactionId = instantPaymentDialog.findViewById(R.id.payment_transactionId);
        senderName = instantPaymentDialog.findViewById(R.id.payment_senderName);
        transPicture = instantPaymentDialog.findViewById(R.id.payment_transactionPicture);
        paymentButton = instantPaymentDialog.findViewById(R.id.payment_button);
        upload = instantPaymentDialog.findViewById(R.id.payment_uploadPicture);
        totalMoney = instantPaymentDialog.findViewById(R.id.payment_totalAmount);
        totalMoneyDetails = instantPaymentDialog.findViewById(R.id.payment_totalAmountDetails);
        senderNetworks = instantPaymentDialog.findViewById(R.id.payment_networks);

        totalMoneyDetails.setPaintFlags(totalMoneyDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        try{
            //loading temporary value
            senderName.setText(tempSenderName);
            amountPaid.setText(tempAmountPaid);
            transactionId.setText(tempTransId);
            Picasso
                    .get()
                    .load(tempTransPicture)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image).into(transPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(tempTransPicture)
                            .fit()
                            .placeholder(R.drawable.no_image).into(transPicture);
                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ViewBuyForMeActivity", "showMakeInstantPayment method");
        }

        DatabaseReference paymentReference = FirebaseDatabase.getInstance().getReference().child("Payment Details");

        paymentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("WarningNote")){
                        String nt = dataSnapshot.child("WarningNote").getValue().toString();
                        note.setText(nt);
                    }
                    if (dataSnapshot.hasChild("MobileMoneyDetails")){
                        String mm = dataSnapshot.child("MobileMoneyDetails").getValue().toString();
                        mobileMoneyNumber.setText(mm);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        totalMoney.setText("GHC" + pTotalAmount);

        totalMoneyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllChargesDialog();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                instantPaymentDialog.dismiss();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallaryIntent = new Intent();
                gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gallaryIntent.setType("image/*");
                startActivityForResult(gallaryIntent, gallaryPick);
            }
        });

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sName = senderName.getText().toString();
                String sPic = pTransactionPicture;

                if (TextUtils.isEmpty(sPic) && TextUtils.isEmpty(sName))
                    Toast.makeText(ViewBuyForMeActivity.this, "Please upload your transaction picture", Toast.LENGTH_SHORT).show();
                else
                    sendUserToViewPictureActivity(sName, sPic);
            }
        });

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getAmountPaid = amountPaid.getText().toString();
                String getSenderName = senderName.getText().toString();
                String getTransactionId = transactionId.getText().toString();
                String getSenderNetwork = senderNetworks.getSelectedItem().toString();

                if (TextUtils.isEmpty(getAmountPaid))
                    Toast.makeText(ViewBuyForMeActivity.this, "Enter amount paid", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getSenderName))
                    Toast.makeText(ViewBuyForMeActivity.this, "Enter sender name", Toast.LENGTH_SHORT).show();
                else if  (TextUtils.isEmpty(getTransactionId))
                    Toast.makeText(ViewBuyForMeActivity.this, "Enter transaction ID", Toast.LENGTH_SHORT).show();
                else if (getSenderNetwork.equals("Select Network"))
                    Toast.makeText(ViewBuyForMeActivity.this, "Select network", Toast.LENGTH_SHORT).show();
                else if  (TextUtils.isEmpty(pTransactionPicture))
                    Toast.makeText(ViewBuyForMeActivity.this, "Upload transaction", Toast.LENGTH_SHORT).show();
                else {
                    savePaymentDetailsActivity("Instant Payment", "GHC" + getAmountPaid, getSenderName, getTransactionId, getSenderNetwork);
                }
            }
        });

        instantPaymentDialog.show();
    }

    private void showAllChargesDialog() {
        final Dialog msgDialog;
        TextView msgClose, message;
        Button msgButton;

        msgDialog = new Dialog(ViewBuyForMeActivity.this, R.style.Theme_CustomDialog);
        msgDialog.setContentView(R.layout.dialog_order_message);
        msgClose = msgDialog.findViewById(R.id.dialogOrderMsg_close);
        message = msgDialog.findViewById(R.id.dialogOrderMsg_message);
        msgButton = msgDialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);

        String msg = "Product Price : \tGHC" + pProductFee + "\nQuantity Ordered : \t" + pProductQuantity + " unit(s)" +
                "\nDelivery Fee : \tGHC" + pDeliveryFee + "\nTotal Amount : \tGHC" + pTotalAmount;

        message.setText(msg);

        msgButton.setVisibility(View.GONE);

        msgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                msgDialog.dismiss();
            }
        });

        msgDialog.show();
    }

    private void savePaymentDetailsActivity(String paymentType, final String amountPaid, final String getSenderName,
                                            final String getTransactionId, String getSenderNetwork) {
        try{
            progressDialog.setMessage("Processing payment....");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            Map paymentMap = new HashMap();
            paymentMap.put("paymentType", paymentType);
            paymentMap.put("paymentSenderName", getSenderName);
            paymentMap.put("paymentAmountPaid", amountPaid);
            paymentMap.put("paymentTransactionId", getTransactionId);
            paymentMap.put("paymentSenderNetwork", getSenderNetwork);
            paymentMap.put("paymentTransactionPicture", pTransactionPicture);
            paymentMap.put("paymentConfirm", "Waiting for confirmation");
            paymentMap.put("message", "Payment: Waiting for confirmation");

            databaseReference.child(postKey).updateChildren(paymentMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
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
                                "Payment Process",
                                "Buy for me order",
                                notifySenderList,
                                postKey
                        );
                        boolean deter = innerNotification.onSaveAll();
                        if (deter){
                            sendNotificationToServer("Instant Payment", amountPaid);
                            tempAmountPaid = amountPaid;
                            tempSenderName = getSenderName;
                            tempTransId = getTransactionId;
                            tempTransPicture = pTransactionPicture;
                            Toast.makeText(ViewBuyForMeActivity.this, "Payment process successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            instantPaymentDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewBuyForMeActivity.this);
                            builder.setTitle("Payment");
                            builder.setMessage("Payment processed successfully");
                            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            sendNotificationToServer("Instant Payment", amountPaid);
                            tempAmountPaid = amountPaid;
                            tempSenderName = getSenderName;
                            tempTransId = getTransactionId;
                            tempTransPicture = pTransactionPicture;
                            Toast.makeText(ViewBuyForMeActivity.this, "Payment process successfully", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            instantPaymentDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ViewBuyForMeActivity.this);
                            builder.setTitle("Payment");
                            builder.setMessage("Payment processed successfully");
                            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }

                    } else {
                        String err = task.getException().getMessage();
                        Toast.makeText(ViewBuyForMeActivity.this, err + " Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ViewBuyForMeActivity", "savePaymentDetailsActivity method");
        }
    }

    private void sendNotificationToServer(String paymentType, String amountPaid) {
       try{
           //sending notification
           final HashMap<String, String> notificationData = new HashMap<>();
           notificationData.put("from", userId);
           notificationData.put("title", "Buy for me");
           if (paymentType.equals("Payment On Delivery"))
               notificationData.put("message", paymentType + " made on " + pProductName);
           else
               notificationData.put("message", paymentType + " made of " + amountPaid);

           notificationReference.child(notifySender1).push().setValue(notificationData)
                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                               DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken");
                               dRef.child(notifySender1).child("order").setValue("no");
                               if (!TextUtils.isEmpty(notifySender2)){
                                   notificationReference.child(notifySender2).push().setValue(notificationData)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()){
                                                       DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken");
                                                       dRef.child(notifySender2).child("order").setValue("no");
                                                   } else {
                                                       String err = task.getException().getMessage();
                                                       Toast.makeText(ViewBuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });
                               }

                               if (!TextUtils.isEmpty(notifySender3)){
                                   notificationReference.child(notifySender3).push().setValue(notificationData)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()){
                                                       DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken");
                                                       dRef.child(notifySender3).child("order").setValue("no");
                                                   } else {
                                                       String err = task.getException().getMessage();
                                                       Toast.makeText(ViewBuyForMeActivity.this, err, Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });
                               }
                           } else {
                               String er = task.getException().getMessage();
                               Toast.makeText(ViewBuyForMeActivity.this, er, Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
       } catch (Exception e){
           Log.i("error", e.getMessage());
           catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                   "ViewBuyForMeActivity", "sendNotificationToServer method");
       }
    }

    //saving transaction picture into firebase storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == gallaryPick && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            try{
                saveTransactionPictureToFirebaseStorage();
            } catch (Exception ex){
                Toast.makeText(this, "Error occurred, please try again...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveTransactionPictureToFirebaseStorage() {
        progressDialog.setTitle("Uploading");
        progressDialog.setMessage("Please wait patiently while your picture is uploaded");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        try{
            if (!TextUtils.isEmpty(pTransactionPicture)){
                try{
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pTransactionPicture);
                    picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            saveTransPic();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            saveTransPic();
                        }
                    });
                } catch (Exception ex){
                    saveTransPic();
                }
            } else
                saveTransPic();
        } catch (Exception ex){
            Toast.makeText(this, "Error occurred, please try again...", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTransPic(){
        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTimeFormat.format(postTime.getTime());

        String saveRandomName = saveCurrentDate + userId + saveCurrentTime;

        StorageReference filePath = storageReference.child(userId)
                .child(imageUri.getLastPathSegment() + saveRandomName + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pTransactionPicture = uri.toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pTransactionPicture).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.no_image)
                                .into(transactionPicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pTransactionPicture).fit()
                                                .placeholder(R.drawable.no_image)
                                                .into(transactionPicture);
                                    }
                                });
                        Toast.makeText(ViewBuyForMeActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ViewBuyForMeActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    private void sendUserToViewPictureActivity(String sName, String sPic) {
        Intent intent = new Intent(ViewBuyForMeActivity.this, ViewPictureActivity.class);
        intent.putExtra("image", sPic);
        intent.putExtra("imageText", sName);
        startActivity(intent);
    }


    //Show Post Details
    private void  showPostDetails(){
        TextView pRegion, pTown, pDistrict, pPostNum;
        Button cls;

        dialog.setContentView(R.layout.dialog_show_post_delivery_details);
        pRegion = dialog.findViewById(R.id.showPostDelivery_region);
        pTown = dialog.findViewById(R.id.showPostDelivery_town);
        pDistrict = dialog.findViewById(R.id.showPostDelivery_district);
        pPostNum = dialog.findViewById(R.id.showPostDelivery_boxNumber);
        cls = dialog.findViewById(R.id.showPostDelivery_close);

        pRegion.setText(zPostRegion);
        pTown.setText(zPostTown);
        pDistrict.setText(zPostDistrict);
        pPostNum.setText(zPostNumber);

        cls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getAllInfo(){
        databaseReference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("orderDate")){
                        String pOrderDate = dataSnapshot.child("orderDate").getValue().toString();
                        orderDate.setText(pOrderDate);

                        if (dataSnapshot.hasChild("orderTime")){
                            String pOrderTime = dataSnapshot.child("orderTime").getValue().toString();
                            orderDate.setText(pOrderDate + " - " + pOrderTime);
                        }
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        pOrderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                        productConfirmation.setText("Product Confirmation: " + pOrderConfirm);

                        if (pOrderConfirm.equals("Order Confirmed")){
                            payment.setVisibility(View.VISIBLE);
                        }
                    }

                    if (dataSnapshot.hasChild("productName")){
                        pProductName = dataSnapshot.child("productName").getValue().toString();
                        productName.setText(pProductName);
                    }

                    if (dataSnapshot.hasChild("productDetails")){
                        String pProductDetails = dataSnapshot.child("productDetails").getValue().toString();
                        productDescription.setText(pProductDetails);
                    }
                    if (dataSnapshot.hasChild("productUrgent")){
                        String pProductUrgent = dataSnapshot.child("productUrgent").getValue().toString();
                        urgency.setText(pProductUrgent);
                    }
                    if (dataSnapshot.hasChild("productQuantity")){
                        pProductQuantity = dataSnapshot.child("productQuantity").getValue().toString();
                        quantityOrdered.setText(pProductQuantity);
                    }

                    if (dataSnapshot.hasChild("productImage")){
                        pProductImage = dataSnapshot.child("productImage").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductImage)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productImage, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductImage)
                                                .placeholder(R.drawable.warning)
                                                .into(productImage);
                                    }

                                });
                    }

                    if (dataSnapshot.hasChild("productFee")){
                        pProductFee = dataSnapshot.child("productFee").getValue().toString();
                        productFee.setText(pProductFee);
                    }

                    //delivery info
                    if (dataSnapshot.hasChild("userLocation")){
                        String pUserLocation = dataSnapshot.child("userLocation").getValue().toString();
                        userLocation.setText(pUserLocation);
                    }

                    if (dataSnapshot.hasChild("userTown")){
                        String pUserTown = dataSnapshot.child("userTown").getValue().toString();
                        userTown.setText(pUserTown);
                    }

                    if (dataSnapshot.hasChild("deliveryType")){
                        String pDeliveryType = dataSnapshot.child("deliveryType").getValue().toString();
                        deliveryType.setText(pDeliveryType);

                        if (pDeliveryType.equals("Post Box Delivery"))
                            postBoxDetails.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("postRegion")){
                        zPostRegion = dataSnapshot.child("postRegion").getValue().toString();
                    }

                    if ((dataSnapshot.hasChild("postTown"))){
                        zPostTown = dataSnapshot.child("postTown").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("postDistrict")){
                        zPostDistrict = dataSnapshot.child("postDistrict").getValue().toString();
                    }

                    if ((dataSnapshot.hasChild("postBoxNumber"))){
                        zPostNumber = dataSnapshot.child("postBoxNumber").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("deliveryDate")){
                        pDeliveryDate = dataSnapshot.child("deliveryDate").getValue().toString();
                        deliveryDate.setText(pDeliveryDate);
                    }

                    if (dataSnapshot.hasChild("deliveryFee")){
                        pDeliveryFee = dataSnapshot.child("deliveryFee").getValue().toString();
                        deliveryFee.setText(pDeliveryFee);
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        pDeliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                        confirmDelivery.setText(pDeliverySuccess);
                    }

                    //payment
                    if (dataSnapshot.hasChild("paymentType")){
                        String pPaymentType = dataSnapshot.child("paymentType").getValue().toString();
                        paymentType.setText(pPaymentType);

                        if (pPaymentType.equalsIgnoreCase("Instant Payment")){
                            payment.setVisibility(View.GONE);
                            deliveryDetails.setVisibility(View.VISIBLE);
                            paymentDetails.setVisibility(View.VISIBLE);
                            deliveryDetails.setVisibility(View.VISIBLE);
                            senderNameLinear.setVisibility(View.VISIBLE);
                            amountPaidLinear.setVisibility(View.VISIBLE);
                            transactionLinear.setVisibility(View.VISIBLE);
                            senderNetworkLinear.setVisibility(View.VISIBLE);
                        }

                        if (pPaymentType.equals("Payment On Delivery")){
                            deliveryDetails.setVisibility(View.VISIBLE);
                            paymentDetails.setVisibility(View.VISIBLE);
                            payment.setText("Edit Payment");
                            senderNameLinear.setVisibility(View.GONE);
                            amountPaidLinear.setVisibility(View.GONE);
                            transactionLinear.setVisibility(View.GONE);
                            senderNetworkLinear.setVisibility(View.GONE);
                        }
                    }

                    if (dataSnapshot.hasChild("paymentSenderName")){
                        String pSenderFullName = dataSnapshot.child("paymentSenderName").getValue().toString();
                        sendFullName.setText(pSenderFullName);
                    }

                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                        String pAmountPaid = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                        amountSend.setText(pAmountPaid);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionId")){
                        String pTransactionId = dataSnapshot.child("paymentTransactionId").getValue().toString();
                        transactionId.setText(pTransactionId);
                    }

                    if (dataSnapshot.hasChild("paymentSenderNetwork")){
                        String pSenderNetwork = dataSnapshot.child("paymentSenderNetwork").getValue().toString();
                        senderNetwork.setText(pSenderNetwork);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionPicture")){
                        pTransactionPicture = dataSnapshot.child("paymentTransactionPicture").getValue().toString();

                        if (TextUtils.isEmpty(pTransactionPicture))
                            transactionPicture.setVisibility(View.GONE);
                        else
                            transactionPicture.setVisibility(View.VISIBLE);

                        //loading picture offline
                        try{
                            Picasso.get()
                                    .load(pTransactionPicture).fit()
                                    .networkPolicy(NetworkPolicy.OFFLINE).into(transactionPicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(pTransactionPicture).fit()
                                            .into(transactionPicture);
                                }
                            });
                            transactionPicture.setVisibility(View.VISIBLE);
                        } catch (Exception ex){
                            transactionPicture.setVisibility(View.GONE);
                            System.out.println(ex.getMessage());
                        }
                    } else
                        transactionPicture.setVisibility(View.GONE);

                    if (dataSnapshot.hasChild("paymentConfirm")){
                        String pPaymentConfirm = dataSnapshot.child("paymentConfirm").getValue().toString();
                        paymentConfirmation.setText("Payment Confirmation : " + pPaymentConfirm);

                        if (pPaymentConfirm.equals("Payment Received")){
                            payment.setVisibility(View.GONE);
                            paymentDetails.setVisibility(View.VISIBLE);
                            deliveryDetails.setVisibility(View.VISIBLE);
                        }
                    }

                    if (!TextUtils.isEmpty(pDeliveryFee) && !TextUtils.isEmpty(pProductFee)){
                        pTotalAmount = Double.parseDouble(pDeliveryFee) + Double.parseDouble(pProductFee);
                        totalAmount.setText("Total Amount : GHC" + pTotalAmount);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserPrivateData(){
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("fullName")){
                        String pUserName = dataSnapshot.child("fullName").getValue().toString();
                        userName.setText(pUserName);
                    }

                    if (dataSnapshot.hasChild("email")){
                        String pUserEmail = dataSnapshot.child("email").getValue().toString();
                        userEmailAddress.setText(pUserEmail);
                    }

                    if (dataSnapshot.hasChild("phoneNumber")){
                        String number = dataSnapshot.child("phoneNumber").getValue().toString();
                        userPhoneNumber.setText(number);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
