package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
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
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ConfirmOrderActivity extends AppCompatActivity {

    private String getCurrentUserId,  uFullName,  uPhoneNumber,uTownName,  uEmailAddress,  uDeliveryLocation,
            deliveryType, deliveryFee,  productName,  productPrice, productPicture,  productQuantity,  actualAmount,
            paymentType,  getAmountPaid,  getSenderName, getTransactionId, getSenderNetwork,  getTransactionPictureUri, uProfilePicture,
            productQuantityAvaliable;

    private String zPostRegion, zPostDistrict,zPostTown, zPostNumber;

    private String saveRandomName;

    //private CircleImageView profilePicture;
    private TextView pName, pPrice, pQuantity;
    private ImageView pPicture, transactionPicture;
    private TextView uName, uPhone, uTown, uLocation, uEmail, delType, delFee, postDeliveryButton;
    private TextView totalAmount, senderName, amountPaid, transactionID, senderNetwork, payType;
    private Button completeOrder;
    private Toolbar myToolBar;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private LinearLayout linearSenderName, linearAmountPaid, linearTransactionId, linearSenderNetwork;

    private TextToSpeech phoneSpeak;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;
    private InnerNotification innerNotification;
    private ArrayList<String> notifySenderList;

    private String notifySender1, notifySender2, notifySender3;
    private long counter = 0;

    private int determine = 0;

    private DatabaseReference databaseReference, orderReference, notificationReference;
    private FirebaseAuth mAuth;

    @Override
    public void onPause() {
        if(phoneSpeak != null){
            phoneSpeak.stop();
            phoneSpeak.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        pName = findViewById(R.id.confirmOrder_productName);
        pPrice = findViewById(R.id.confirmOrder_productPrice);
        pQuantity = findViewById(R.id.confirmOrder_quantity);
        pPicture = findViewById(R.id.confirmOrder_productPicture);
        transactionPicture = findViewById(R.id.confirmOrder_transactionPicture);
        uName = findViewById(R.id.confirmOrder_userFullName);
        uPhone = findViewById(R.id.confirmOrder_userPhoneNumber);
        uTown = findViewById(R.id.confirmOrder_userTown);
        uLocation = findViewById(R.id.confirmOrder_userLocation);
        uEmail = findViewById(R.id.confirmOrder_userEmail);
        delType = findViewById(R.id.confirmOrder_deliveryType);
        delFee = findViewById(R.id.confirmOrder_deliveryFee);
        totalAmount = findViewById(R.id.confirmOrder_totalAmount);
        payType = findViewById(R.id.confirmOrder_paymentType);
        senderName = findViewById(R.id.confirmOrder_senderFullName);
        amountPaid = findViewById(R.id.confirmOrder_amountPaid);
        transactionID = findViewById(R.id.confirmOrder_transactionId);
        senderNetwork = findViewById(R.id.confirmOrder_senderNetwork);
        completeOrder = findViewById(R.id.confirmOrder_button);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        postDeliveryButton = findViewById(R.id.confirmOrder_postDelivery);
        //profilePicture = (CircleImageView) findViewById(R.id.confirmOrder_profilePic);
        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();
        linearAmountPaid = findViewById(R.id.confirmOrder_linearAmountPaid);
        linearSenderName = findViewById(R.id.confirmOrder_linearSenderFullName);
        linearSenderNetwork = findViewById(R.id.confirmOrder_linearSenderNetwork);
        linearTransactionId = findViewById(R.id.confirmOrder_linearTransactionId);
        notifySenderList = new ArrayList<>();

        //Phone speak out
        phoneSpeak = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    phoneSpeak.setLanguage(Locale.UK);
                }
            }
        });

        myToolBar = findViewById(R.id.confirmOrder_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Complete Order");

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        orderReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        orderReference.keepSynced(true);
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        try{
            getOtherIntentValue();
            getAllIntentValues();
            showProfilePicture();
            displayAllItem();
            getCounterValue();
            getNotifySenderDetails();

            postDeliveryButton.setVisibility(View.GONE);

            if (deliveryType.equals("Post Box Delivery"))
                postDeliveryButton.setVisibility(View.VISIBLE);

            completeOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (determine == 1)
                        Toast.makeText(ConfirmOrderActivity.this, "You already ordered product", Toast.LENGTH_LONG).show();
                    else
                        saveAllOrder();
                }
            });

            pPicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendUserToViewPictureActivity();
                }
            });

            postDeliveryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPostDetails();
                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ConfirmOrderActivity", "main method");
        }
    }

    private void getOtherIntentValue() {
        zPostDistrict = getIntent().getExtras().get("pDistrict").toString();
        zPostNumber = getIntent().getExtras().get("pBoxNumber").toString();
        zPostRegion = getIntent().getExtras().get("pRegion").toString();
        zPostTown = getIntent().getExtras().get("pTown").toString();
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

    private void getCounterValue() {
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    counter = dataSnapshot.getChildrenCount();
                } else {
                    counter = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllIntentValues(){
        try{
            getCurrentUserId = getIntent().getExtras().get("userId").toString();
            uFullName = getIntent().getExtras().get("userFullName").toString();
            uPhoneNumber = getIntent().getExtras().get("userPhoneNumber").toString();
            uTownName = getIntent().getExtras().get("userTownName").toString();
            uEmailAddress = getIntent().getExtras().get("userEmail").toString();
            uDeliveryLocation = getIntent().getExtras().get("userDeliveryLocation").toString();
            deliveryType = getIntent().getExtras().get("deliveryType").toString();
            deliveryFee = getIntent().getExtras().get("deliveryFee").toString();
            productName = getIntent().getExtras().get("productName").toString();
            productPrice = getIntent().getExtras().get("productPrice").toString();
            productPicture = getIntent().getExtras().get("productPicture").toString();
            productQuantity = getIntent().getExtras().get("productQuantity").toString();
            actualAmount = getIntent().getExtras().get("totalAmount").toString();
            paymentType = getIntent().getExtras().get("paymentType").toString();
            getAmountPaid = getIntent().getExtras().get("paymentAmountPaid").toString();
            getSenderName = getIntent().getExtras().get("paymentSenderName").toString();
            getTransactionId = getIntent().getExtras().get("paymentTransactionId").toString();
            getSenderNetwork = getIntent().getExtras().get("paymentSenderNetwork").toString();
            getTransactionPictureUri = getIntent().getExtras().get("paymentTransactionPicture").toString();
            productQuantityAvaliable = getIntent().getExtras().get("ProductQuantityAvailable").toString();

        } catch (Exception ex){

        }
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

    private void saveAllOrder(){
       try{
           progressDialog.setMessage("Product Ordering");
           progressDialog.setCanceledOnTouchOutside(true);
           progressDialog.show();

           Calendar postTime = Calendar.getInstance();
           SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
           String saveCurrentTime = currentTimeFormat.format(postTime.getTime());

           Calendar postDate = Calendar.getInstance();
           SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
           String saveCurrentDate = currentDateFormat.format(postDate.getTime());

           saveRandomName = orderReference.push().getKey();

           Map orderMap = new HashMap();
           orderMap.put("productId", saveRandomName);
           orderMap.put("productPicture1", productPicture);
           orderMap.put("productName", productName);
           orderMap.put("productPrice", productPrice);
           orderMap.put("productQuantity", productQuantity);
           orderMap.put("userId", getCurrentUserId);
           orderMap.put("userFullName", uFullName);
           orderMap.put("userProfilePicture", uProfilePicture);
           orderMap.put("userPhoneNumber", uPhoneNumber);
           orderMap.put("userTown", uTownName);
           orderMap.put("userLocation", uDeliveryLocation);
           orderMap.put("userEmail", uEmailAddress);
           orderMap.put("paymentSenderName", getSenderName);
           orderMap.put("paymentAmountPaid", getAmountPaid);
           orderMap.put("paymentTransactionId", getTransactionId);
           orderMap.put("paymentSenderNetwork", getSenderNetwork);
           orderMap.put("paymentTransactionPicture", getTransactionPictureUri);
           orderMap.put("paymentType", paymentType);
           orderMap.put("deliveryDate", "Waiting for delivery");
           orderMap.put("deliveryFee", deliveryFee);
           orderMap.put("deliveryType", deliveryType);
           orderMap.put("deliverySuccess", "Order Not Delivered Yet");
           orderMap.put("orderConfirm", "Waiting for confirmation");
           orderMap.put("paymentConfirm", "Waiting for confirmation");
           orderMap.put("orderDate", saveCurrentDate);
           orderMap.put("orderTime", saveCurrentTime);
           orderMap.put("totalAmount", actualAmount);
           orderMap.put("postRegion", zPostRegion);
           orderMap.put("postTown", zPostTown);
           orderMap.put("postDistrict", zPostDistrict);
           orderMap.put("postBoxNumber", zPostNumber);
           orderMap.put("number", counter);

           final Map delMap = new HashMap();
           delMap.put("userTown", uTownName);
           delMap.put("userLocation", uDeliveryLocation);
           delMap.put("postRegion", zPostRegion);
           delMap.put("postTown", zPostTown);
           delMap.put("postDistrict", zPostDistrict);
           delMap.put("postBoxNumber", zPostNumber);


           orderReference.child(saveRandomName).setValue(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful()){
                       final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(getCurrentUserId);
                       dRef.child("singleOrder").child("pendingOrder").child(saveRandomName).child("item").setValue(saveRandomName)
                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if (task.isSuccessful()){
                                           dRef.child("deliveryDetails").updateChildren(delMap).addOnCompleteListener(new OnCompleteListener() {
                                               @Override
                                               public void onComplete(@NonNull Task task) {
                                                   if (!task.isSuccessful()){
                                                       String err = task.getException().getMessage();
                                                       Toast.makeText(ConfirmOrderActivity.this, err, Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });
                                       } else{
                                           String err = task.getException().getMessage();
                                           Toast.makeText(ConfirmOrderActivity.this, err, Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                       final String msg;
                       if (TextUtils.isEmpty(getSenderName) && TextUtils.isEmpty(getAmountPaid)){
                           msg = productName + " | Cash on delivery";
                       } else {
                           msg = productName + " | Instant payment made";
                       }

                       //get today's date
                       GregorianCalendar calendar = new GregorianCalendar();
                       int year = calendar.get(GregorianCalendar.YEAR);
                       int month = calendar.get(GregorianCalendar.MONTH) + 1;
                       int date = calendar.get(GregorianCalendar.DATE);
                       final String day = year + "-" + month + "-" + date;

                       final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("All Order Details").child("singleOrder");
                       orderRef.keepSynced(true);

                       //getting last date
                       orderRef.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(DataSnapshot dataSnapshot) {
                               if (dataSnapshot.exists()){
                                   if (dataSnapshot.hasChild("currentDate")){
                                       String lastDate = dataSnapshot.child("currentDate").getValue().toString();
                                       if (!day.equals(lastDate)){
                                           //Deleting last date
                                           orderRef.child(getCurrentUserId).child(lastDate).removeValue();
                                       }
                                   }
                               }
                           }

                           @Override
                           public void onCancelled(DatabaseError databaseError) {

                           }
                       });

                       //save new date to only this user
                       orderRef.child(getCurrentUserId).child("currentDate").setValue(day);

                       //save order key
                       orderRef.child(getCurrentUserId).child("orders").child(day).child(saveRandomName).child("key").setValue(saveRandomName);


                       //sending notification
                       final HashMap<String, String> notificationData = new HashMap<>();
                       notificationData.put("from", getCurrentUserId);
                       notificationData.put("title", "Product Order");
                       notificationData.put("message", msg);

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
                                                                   Toast.makeText(ConfirmOrderActivity.this, err, Toast.LENGTH_SHORT).show();
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
                                                                   Toast.makeText(ConfirmOrderActivity.this, err, Toast.LENGTH_SHORT).show();
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
                                                   getCurrentUserId,
                                                   "ViewProductOrderDetailsActivity",
                                                   ServerValue.TIMESTAMP.toString(),
                                                   "Product Order",
                                                   msg,
                                                   notifySenderList,
                                                   saveRandomName
                                           );
                                           boolean deter = innerNotification.onSaveAll();
                                           if (deter){
                                               try{
                                                   /*String speak = "Product Order Successfully. Thank you";
                                                   phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);*/
                                                   progressDialog.dismiss();
                                                   determine = 1;
                                                   showSuccessDialog(View.INVISIBLE, R.drawable.ok, "Product Order Successfully", View.VISIBLE);
                                               } catch (Exception e){
                                                   Log.i("error", e.getMessage());
                                               }
                                           } else {
                                               /*progressDialog.dismiss();
                                               AllMessage msg = new AllMessage();
                                               Toast.makeText(ConfirmOrderActivity.this, msg.getErrorMessage(), Toast.LENGTH_SHORT).show();*/
                                               progressDialog.dismiss();
                                               determine = 1;
                                               showSuccessDialog(View.INVISIBLE, R.drawable.ok, "Product Order Successfully", View.VISIBLE);
                                           }

                                       } else {
                                           String er = task.getException().getMessage();
                                           Toast.makeText(ConfirmOrderActivity.this, er, Toast.LENGTH_SHORT).show();
                                       }
                                   }
                               });
                   } else{
                       String err = task.getException().getMessage();
                       String speak = "Failed to order product, please try again";
                       phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                       progressDialog.dismiss();
                       Toast.makeText(ConfirmOrderActivity.this, "Error Occurred... Failed to order product \n" +
                               "Please try again\n" + err, Toast.LENGTH_SHORT).show();
                       determine = 0;
                       showSuccessDialog(View.VISIBLE, R.drawable.error, "Product Order Failed", View.INVISIBLE);
                   }
               }
           });
       } catch (Exception e){
           Log.i("error", e.getMessage());
           catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                   "ConfirmOrderActivity", "saveAllOrder method");
       }
    }

    private void backEvent(){
        if (determine == 1){
            Toast.makeText(this, "Your product have been ordered successfully", Toast.LENGTH_LONG).show();
            showSuccessDialog(View.INVISIBLE, R.drawable.ok, "Product Order Successfully", View.VISIBLE);
        } else {
            sendUserToOrderActivity();
        }
    }

    //show confirm dialog about the transaction
    private void showSuccessDialog(int cl, int img, String msg, int vv) {
        Button button;
        TextView text, close;
        ImageView image;

        dialog.setContentView(R.layout.dialog_order_successful);
        button = dialog.findViewById(R.id.orderSuccess_button);
        text = dialog.findViewById(R.id.orderSuccess_text);
        close = dialog.findViewById(R.id.orderSuccess_close);
        image = dialog.findViewById(R.id.orderSuccess_mark);

        close.setVisibility(cl);
        image.setImageResource(img);
        text.setText(msg);
        button.setVisibility(vv);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserViewProductOrderActivity();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            backEvent();
        }

        return false;
    }

    private void displayAllItem(){
        pName.setText(productName);
        pPrice.setText(productPrice);
        pQuantity.setText(productQuantity);
        uName.setText(uFullName);
        uPhone.setText(uPhoneNumber);
        uTown.setText(uTownName);
        uLocation.setText(uDeliveryLocation);
        uEmail.setText(uEmailAddress);
        delType.setText(deliveryType);
        delFee.setText(deliveryFee);
        totalAmount.setText(actualAmount);
        payType.setText(paymentType);
        senderName.setText(getSenderName);
        amountPaid.setText(getAmountPaid);
        transactionID.setText(getTransactionId);
        senderNetwork.setText(getSenderNetwork);

        //loading picture offline
        Picasso
                .get()
                .load(productPicture)
                .fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image)
                .into(pPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso
                        .get()
                        .load(productPicture)
                        .fit()
                        .placeholder(R.drawable.no_image).into(pPicture);
            }
        });

        //loading picture offline
        try{
            Picasso
                    .get()
                    .load(getTransactionPictureUri).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image)
                    .into(transactionPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso
                            .get()
                            .load(getTransactionPictureUri).fit()
                            .placeholder(R.drawable.no_image)
                            .into(transactionPicture);
                }
            });
        } catch (Exception ex){
            catchErrors.setErrors(ex.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(), "ConfirmOrderActivity", "displayAllItem method");
        }

        if (TextUtils.isEmpty(getTransactionPictureUri))
            transactionPicture.setVisibility(View.GONE);
        else
            transactionPicture.setVisibility(View.VISIBLE);

        if (paymentType.equalsIgnoreCase("Payment On Delivery")){
            linearTransactionId.setVisibility(View.GONE);
            linearSenderNetwork.setVisibility(View.GONE);
            linearSenderName.setVisibility(View.GONE);
            linearAmountPaid.setVisibility(View.GONE);
        }
    }

    private void showProfilePicture() {
        databaseReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("profilePicture")){
                        uProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                        //code not functioning
                    }
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
            backEvent();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToOrderActivity() {
        Intent intent = new Intent(ConfirmOrderActivity.this, OrderProductActivity.class);
        intent.putExtra("tempTownName", uTownName);
        intent.putExtra("tempLocation", uDeliveryLocation);
        intent.putExtra("tempSenderName", getSenderName);
        intent.putExtra("tempAmountPaid", getAmountPaid);
        intent.putExtra("tempTransId", getTransactionId);
        intent.putExtra("tempTransPicture", getTransactionPictureUri);
        intent.putExtra("tempRegion", zPostRegion);
        intent.putExtra("tempDistrict", zPostDistrict);
        intent.putExtra("tempTownNamePost", zPostTown);
        intent.putExtra("tempBoxNumber", zPostNumber);
        intent.putExtra("tempQtyOrder", productQuantity);
        intent.putExtra("ProductName", productName);
        intent.putExtra("ProductPrice", productPrice);
        intent.putExtra("ProductPicture1", productPicture);
        intent.putExtra("ProductQuantityAvailable", productQuantityAvaliable);
        intent.putExtra("defaultValue", "temp");
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserViewProductOrderActivity(){
        Intent intent = new Intent(ConfirmOrderActivity.this, ViewProductOrderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void sendUserToViewPictureActivity(){
        Intent intent = new Intent(ConfirmOrderActivity.this, ViewPictureActivity.class);
        intent.putExtra("imageText", productName);
        intent.putExtra("image", productPicture);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
