package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.tmsoft.tm.elinamclient.Holders.autofit;
import com.tmsoft.tm.elinamclient.Holders.cartProducts;
import com.tmsoft.tm.elinamclient.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ShowCartActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private LinearLayout orderAll, noProduct;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout refresh;

    private ImageView transactionPicture;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference, cartReference, userReference, paymentReference,
            cartCounterReference;
    private StorageReference storageReference;

    private double totalAmount = 0;
    private String deliveryType;
    private String profilePicture, name, num, emailAddress, saveCurrentTime, saveCurrentDate, saveRandomName;
    private int gallaryPick = 1;
    private Uri imageUri;
    private String getTransactionPictureUri, cartOrderCounter;
    private String postBoxDeliveryFee, postBoxDeliveryMessage, personnelDeliveryFee;

    private String tempTownName = null, tempLocation = null, tempSenderName = null, tempAmountPaid = null, tempTransId = null,
            tempTransPicture = null, tempRegion = null, tempDistrict = null, tempTownNamePost = null, tempBoxNumber = null;

    private String defaultValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart);

        myToolBar = findViewById(R.id.showCart_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Cart");

        orderAll = findViewById(R.id.showCart_orderAll);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        progressDialog = new ProgressDialog(this);
        noProduct = findViewById(R.id.showCart_noProduct);
        refresh = findViewById(R.id.showCart_refresh);

        noProduct.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        cartCounterReference = FirebaseDatabase.getInstance().getReference().child("Cart Order Counter");
        cartCounterReference.keepSynced(true);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Order").child(getCurrentUserId);
        databaseReference.keepSynced(true);
        cartReference = FirebaseDatabase.getInstance().getReference().child("Cart Details");
        cartReference.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference().child("Transaction Picture");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getCurrentUserId);
        userReference.keepSynced(true);

        autofit noColums = new autofit();
        noColums.autofit(getApplicationContext());

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        recyclerView = findViewById(R.id.showCart_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, mNoofColums));

        displayAllProducts();
        getAllCartDetails();
        getUserInformation();
        allDeliveryInformation();
        showCartOrderCounter();
        determinePreviousDetail();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                displayAllProducts();
                getAllCartDetails();
                getUserInformation();
                allDeliveryInformation();
                showCartOrderCounter();
                determinePreviousDetail();
                refresh.setRefreshing(false);
            }
        });

        try{
            tempTownName = getIntent().getExtras().get("tempTownName").toString();
            tempLocation = getIntent().getExtras().get("tempLocation").toString();
            tempSenderName = getIntent().getExtras().get("tempSenderName").toString();
            tempAmountPaid = getIntent().getExtras().get("tempAmountPaid").toString();
            tempTransId = getIntent().getExtras().get("tempTransId").toString();
            tempTransPicture = getIntent().getExtras().get("tempTransPicture").toString();
            tempRegion = getIntent().getExtras().get("tempRegion").toString();
            tempDistrict = getIntent().getExtras().get("tempDistrict").toString();
            tempTownNamePost = getIntent().getExtras().get("tempTownNamePost").toString();
            tempBoxNumber = getIntent().getExtras().get("tempBoxNumber").toString();
            defaultValue = getIntent().getExtras().get("defaultValue").toString();
        } catch(Exception ex){
            //System.out.println(ex.getMessage());
        }

        orderAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMessageDialog();
            }
        });
    }

    private void showCartOrderCounter(){
        cartCounterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    if (dataSnapshot.hasChild(getCurrentUserId))
                        cartOrderCounter = dataSnapshot.child(getCurrentUserId).getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayMessageDialog() {
        TextView close, message;
        Button deliveryDetails;

        dialog.setContentView(R.layout.dialog_order_message);
        close = dialog.findViewById(R.id.dialogOrderMsg_close);
        message = dialog.findViewById(R.id.dialogOrderMsg_message);
        deliveryDetails = dialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        String mm = "Your total amount of all product in your cart is GHC" + totalAmount +
                " You can proceed to fill the delivery form";

        message.setText(mm);

        deliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                viewDeliveryDialog();
            }
        });

        dialog.show();
    }

    private void determinePreviousDetail(){
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(getCurrentUserId);
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

    //loading previous data of the user
    private void getUserPreviousDetails() {
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(getCurrentUserId);
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

    //Showing delivery dialog
    private void viewDeliveryDialog() {
        final RelativeLayout layoutPersonal, layoutBusTerminal, layoutPoxBox;
        Button dialogClose;

        dialog.setContentView(R.layout.dialog_delivery);
        layoutPersonal = dialog.findViewById(R.id.dialog_delivery_relative_personal);
        layoutBusTerminal = dialog.findViewById(R.id.dialog_delivery_relative_bus_terminal);
        layoutPoxBox = dialog.findViewById(R.id.dialog_delivery_relative_post_box);
        dialogClose = dialog.findViewById(R.id.dialog_delivery_close);

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
                Toast.makeText(ShowCartActivity.this, "Still working on this delivery option", Toast.LENGTH_LONG).show();
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
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //showing InnerMessage to the customer dialog
    private void showMessageDialog() {
        TextView message, closeMsg;
        Button deliveryDetails;

        dialog.setContentView(R.layout.dialog_order_message);
        message = dialog.findViewById(R.id.dialogOrderMsg_message);
        deliveryDetails = dialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);
        closeMsg = dialog.findViewById(R.id.dialogOrderMsg_close);

        String msg = "Hello " + name + ", customer service will contact you in some few minutes to confirm your order.\n"
                + "You can proceed to fill the delivery form";

        message.setText(msg);

        deliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (defaultValue.equals("temp"))
                    showFillDeliveryDetailsForPersonnelDelivery();
                else
                    showPreviousDataDialog();
            }
        });

        closeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showPreviousDataDialog() {
        TextView msg;
        Button yes, no;

        dialog.setContentView(R.layout.dialog_question);
        msg = dialog.findViewById(R.id.dialogQuestion_message);
        yes = dialog.findViewById(R.id.dialogQuestion_yes);
        no = dialog.findViewById(R.id.dialogQuestion_no);

        String message = "Do you want to load your previous details ?";

        msg.setText(message);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserPreviousDetails();
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                showFillDeliveryDetailsForPersonnelDelivery();
            }
        });

        dialog.show();
    }

    Dialog sDialog;
    //show Fill Delivery Details for personnel delivery
    private void showFillDeliveryDetailsForPersonnelDelivery(){
        sDialog = new Dialog(this, R.style.Theme_CustomDialog);
        final EditText fullName, phoneNumber, email, townName, deliveryLocation;
        Button makePayment;
        TextView deliveryClose;

        sDialog.setContentView(R.layout.dialog_delivery_form_personnel_delivery);
        fullName = sDialog.findViewById(R.id.dialogDeliveryForm_profileFullName);
        phoneNumber = sDialog.findViewById(R.id.dialogDeliveryForm_profilePhoneNumber);
        email = sDialog.findViewById(R.id.dialogDeliveryForm_profileEmailAddress);
        townName = sDialog.findViewById(R.id.dialogDeliveryForm_townName);
        deliveryLocation = sDialog.findViewById(R.id.dialogDeliveryForm_profileLocation);
        deliveryClose = sDialog.findViewById(R.id.dialogDeliveryForm_close);
        makePayment = sDialog.findViewById(R.id.dialogDeliveryForm_order);

        fullName.setText(name);
        phoneNumber.setText(num);
        email.setText(emailAddress);

        townName.setText(tempTownName);
        deliveryLocation.setText(tempLocation);

        if (deliveryType.equals("Post Box Delivery"))
            makePayment.setText("Post Box Details");

        fullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ShowCartActivity.this, "Edit your name in your profile", Toast.LENGTH_LONG).show();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ShowCartActivity.this, "You cannot change your default email address", Toast.LENGTH_LONG).show();
            }
        });


        makePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String uFull, uPhone, uTown, uEmail, uDelivery;
                uFull = fullName.getText().toString();
                uPhone = phoneNumber.getText().toString();
                uTown = townName.getText().toString();
                uEmail = email.getText().toString();
                uDelivery = deliveryLocation.getText().toString();

                if (TextUtils.isEmpty(uFull))
                    Toast.makeText(ShowCartActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(uTown))
                    Toast.makeText(ShowCartActivity.this, "Enter your town name", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(uDelivery))
                    Toast.makeText(ShowCartActivity.this, "Enter your delivery location", Toast.LENGTH_SHORT).show();
                else {
                    if (deliveryType.equals("Personnel Delivery")){
                        sDialog.dismiss();

                        Button makePaymentNow, makePaymentOnDelivery, viewAllCharges;
                        dialog.setContentView(R.layout.dialog_choose_payment);

                        makePaymentNow = dialog.findViewById(R.id.orderProductDialog_makePaymentNow);
                        makePaymentOnDelivery = dialog.findViewById(R.id.orderProductDialog_makePaymentOnDelivery);
                        viewAllCharges = dialog.findViewById(R.id.orderProductDialog_viewCharges);

                        viewAllCharges.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int aa = personnelDeliveryFee.length();
                                String amountT = personnelDeliveryFee.substring(3,aa);
                                final double perDeliveryFee = Double.valueOf(amountT);

                                final double actualAmount = totalAmount + perDeliveryFee;
                                showAllChargesDialog(perDeliveryFee, actualAmount);
                            }
                        });

                        makePaymentOnDelivery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                showPaymentOnDelivery(uFull, uPhone, uTown, uEmail, uDelivery, "Payment On Delivery", deliveryType,
                                        personnelDeliveryFee);
                            }
                        });

                        makePaymentNow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                showMakeInstantPayment(uFull, uPhone, uTown, uEmail, uDelivery, "", "", "",
                                        "", "Instant Payment", personnelDeliveryFee);
                            }
                        });

                        dialog.show();

                    } else if (deliveryType.equals("Post Box Delivery")){
                        TextView close;
                        final EditText district, townName, bozNumber;
                        Button payment;
                        final Spinner region;

                        dialog.setContentView(R.layout.dialog_delivery_form_post_box_delivery);
                        close = dialog.findViewById(R.id.dialogPostDeliveryForm_close);
                        region = dialog.findViewById(R.id.dialogPostDeliveryForm_region);
                        district = dialog.findViewById(R.id.dialogPostDeliveryForm_district);
                        townName = dialog.findViewById(R.id.dialogPostDeliveryForm_townName);
                        bozNumber = dialog.findViewById(R.id.dialogPostDeliveryForm_boxNumber);
                        payment = dialog.findViewById(R.id.dialogPostDeliveryForm_payment);

                        region.setSelection(((ArrayAdapter) region.getAdapter()).getPosition(tempRegion));
                        district.setText(tempDistrict);
                        townName.setText(tempTownNamePost);
                        bozNumber.setText(tempBoxNumber);

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
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
                                    Toast.makeText(ShowCartActivity.this, "Enter your region", Toast.LENGTH_SHORT).show();
                                else if (TextUtils.isEmpty(getTown))
                                    Toast.makeText(ShowCartActivity.this, "Enter your town", Toast.LENGTH_SHORT).show();
                                else if (getRegion.equalsIgnoreCase("Select your region"))
                                    Toast.makeText(ShowCartActivity.this, "Select your current region", Toast.LENGTH_SHORT).show();
                                else if (TextUtils.isEmpty(getBoxNum))
                                    Toast.makeText(ShowCartActivity.this, "Enter your post box number", Toast.LENGTH_SHORT).show();
                                else {
                                    dialog.dismiss();

                                    TextView message;
                                    Button contButton;

                                    dialog.setContentView(R.layout.dialog_delivery_message);
                                    message = dialog.findViewById(R.id.dialogDeliveryMessage_message);
                                    contButton = dialog.findViewById(R.id.dialogDeliveryMessage_continue);

                                    if (TextUtils.isEmpty(postBoxDeliveryMessage)){
                                        dialog.dismiss();
                                        showMakeInstantPayment(uFull, uPhone, uTown, uEmail, uDelivery, getRegion, getDistrict, getTown, getBoxNum,
                                                "Instant Payment", postBoxDeliveryFee);
                                    }

                                    message.setText(postBoxDeliveryMessage);

                                    contButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialog.dismiss();
                                            showMakeInstantPayment(uFull, uPhone, uTown, uEmail, uDelivery, getRegion, getDistrict, getTown,
                                                    getBoxNum, "Instant Payment", postBoxDeliveryFee);
                                        }
                                    });

                                    dialog.show();

                                }
                            }
                        });

                        dialog.show();
                    }
                }
            }
        });

        deliveryClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        sDialog.show();
    }

    //showing payment on delivery
    private void showPaymentOnDelivery(final String uFullName, final String uPhoneNumber, final String uTownName,
                                       final String uEmailAddress, final String uDeliveryLocation, String paymentType,
                                       String deliveryType, String deliveryFee) {

        int aa = deliveryFee.length();
        String amountT = deliveryFee.substring(3,aa);
        final double perDeliveryFee = Double.valueOf(amountT);

        final double actualAmount = totalAmount + perDeliveryFee;

        String calAmount = "GHC" + actualAmount;
        String dFee = "GHC" + perDeliveryFee;

        sendUserToConfirmOrderDetailsActivity(getCurrentUserId, uFullName, uPhoneNumber, uTownName, uEmailAddress, uDeliveryLocation,
                deliveryType, dFee, calAmount, paymentType, "", "", "", "",
                "", "", "", "", "");
    }

    //showing instant payment details
    private void showMakeInstantPayment(final String uFullName, final String uPhoneNumber, final String uTownName,
                                        final String uEmailAddress, final String uDeliveryLocation, final String postRegion,
                                        final String postDistrict, final String postTown, final String postBoxNumber, final String paymentType,
                                        final String deliveryFee) {
        final TextView note, mobileMoneyNumber, close, totalMoney, totalMoneyDetails;
        final EditText senderName, amountPaid, transactionId;
        final Spinner senderNetworks;
        ImageButton upload;
        Button paymentButton;

        dialog.setContentView(R.layout.dialog_payment_layout);
        close = dialog.findViewById(R.id.payment_close);
        note = dialog.findViewById(R.id.payment_note);
        mobileMoneyNumber = dialog.findViewById(R.id.payment_mobileMoneyNumber);
        amountPaid = dialog.findViewById(R.id.payment_amountPaid);
        transactionId = dialog.findViewById(R.id.payment_transactionId);
        senderName = dialog.findViewById(R.id.payment_senderName);
        transactionPicture = dialog.findViewById(R.id.payment_transactionPicture);
        paymentButton = dialog.findViewById(R.id.payment_button);
        upload = dialog.findViewById(R.id.payment_uploadPicture);
        totalMoney = dialog.findViewById(R.id.payment_totalAmount);
        totalMoneyDetails = dialog.findViewById(R.id.payment_totalAmountDetails);
        senderNetworks = dialog.findViewById(R.id.payment_networks);

        totalMoneyDetails.setPaintFlags(totalMoneyDetails.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        try{
            //loading temporary value
            senderName.setText(tempSenderName);
            amountPaid.setText(tempAmountPaid);
            transactionId.setText(tempTransId);
            Picasso.get().load(tempTransPicture).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image).into(transactionPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(tempTransPicture)
                            .placeholder(R.drawable.no_image).into(transactionPicture);
                }
            });
        } catch (Exception ex){
            //System.out.println(ex.getMessage());
        }

        paymentReference = FirebaseDatabase.getInstance().getReference().child("Payment Details");

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

        int aa = deliveryFee.length();
        String amountT = deliveryFee.substring(3,aa);
        final double perDeliveryFee = Double.valueOf(amountT);

        final double actualAmount = totalAmount + perDeliveryFee;

        totalMoney.setText("GHC" + actualAmount);

        totalMoneyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllChargesDialog(perDeliveryFee, actualAmount);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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
                String sPic = getTransactionPictureUri;

                if (TextUtils.isEmpty(sPic) && TextUtils.isEmpty(sName))
                    Toast.makeText(ShowCartActivity.this, "Please upload your transaction picture", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ShowCartActivity.this, "Enter amount paid", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getSenderName))
                    Toast.makeText(ShowCartActivity.this, "Enter sender name", Toast.LENGTH_SHORT).show();
                else if  (TextUtils.isEmpty(getTransactionId))
                    Toast.makeText(ShowCartActivity.this, "Enter transaction ID", Toast.LENGTH_SHORT).show();
                else if (getSenderNetwork.equals("Select Network"))
                    Toast.makeText(ShowCartActivity.this, "Select network", Toast.LENGTH_SHORT).show();
                else if  (TextUtils.isEmpty(getTransactionPictureUri))
                    Toast.makeText(ShowCartActivity.this, "Upload transaction", Toast.LENGTH_SHORT).show();
                else {
                    dialog.dismiss();
                    String calAmount = "GHC" + actualAmount;
                    String dFee = "GHC" + perDeliveryFee;
                    sendUserToConfirmOrderDetailsActivity(getCurrentUserId, uFullName, uPhoneNumber, uTownName, uEmailAddress, uDeliveryLocation,
                            deliveryType, dFee, calAmount, paymentType, "GHC" + getAmountPaid, getSenderName, getTransactionId, getSenderNetwork,
                            getTransactionPictureUri, postRegion, postDistrict, postTown, postBoxNumber);
                }
            }
        });

        dialog.show();
    }

    private void showAllChargesDialog(double perDeliveryFee, double actualAmount) {
        final Dialog msgDialog;
        TextView msgClose, message;
        Button msgButton;

        msgDialog = new Dialog(ShowCartActivity.this, R.style.Theme_CustomDialog);
        msgDialog.setContentView(R.layout.dialog_order_message);
        msgClose = msgDialog.findViewById(R.id.dialogOrderMsg_close);
        message = msgDialog.findViewById(R.id.dialogOrderMsg_message);
        msgButton = msgDialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);

        String msg = "All Product Price : \tGHC" + totalAmount + "\nDelivery Fee : \tGHC" + perDeliveryFee +
                "\nTotal Amount : \tGHC" + actualAmount;

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

    //sending user to confirm activity
    private void sendUserToConfirmOrderDetailsActivity(String getCurrentUserId, String uFullName, String uPhoneNumber,
                                                       String uTownName, String uEmailAddress, String uDeliveryLocation,
                                                       String deliveryType, String deliveryFee, String actualAmount,
                                                       String paymentType, String getAmountPaid, String getSenderName,
                                                       String getTransactionId, String getSenderNetwork,
                                                       String getTransactionPictureUri, String postRegion,
                                                       String postDistrict, String postTown, String postBoxNumber) {

        Intent intent = new Intent(ShowCartActivity.this, ShowCartConfirmOrderActivity.class);
        intent.putExtra("userId", getCurrentUserId);
        intent.putExtra("userFullName", uFullName);
        intent.putExtra("userPhoneNumber", uPhoneNumber);
        intent.putExtra("userTownName", uTownName);
        intent.putExtra("userEmail", uEmailAddress);
        intent.putExtra("userDeliveryLocation", uDeliveryLocation);
        intent.putExtra("deliveryType", deliveryType);
        intent.putExtra("deliveryFee", deliveryFee);
        intent.putExtra("totalAmount", actualAmount);
        intent.putExtra("paymentType", paymentType);
        intent.putExtra("paymentAmountPaid", getAmountPaid);
        intent.putExtra("paymentSenderName", getSenderName);
        intent.putExtra("paymentTransactionId", getTransactionId);
        intent.putExtra("paymentSenderNetwork", getSenderNetwork);
        intent.putExtra("paymentTransactionPicture", getTransactionPictureUri);
        intent.putExtra("pRegion", postRegion);
        intent.putExtra("pDistrict", postDistrict);
        intent.putExtra("pTown", postTown);
        intent.putExtra("pBoxNumber", postBoxNumber);
        intent.putExtra("cartOrderCounter", cartOrderCounter);
        startActivity(intent);
    }

    //Displaying delivery fee and InnerMessage
    private void allDeliveryInformation() {
        DatabaseReference deliveryFeeRef = FirebaseDatabase.getInstance().getReference().child("Delivery Details");
        deliveryFeeRef.child("personnelDelivery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("amount")){
                        personnelDeliveryFee = dataSnapshot.child("amount").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        deliveryFeeRef.child("postBoxDelivery").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("amount")){
                        postBoxDeliveryFee = dataSnapshot.child("amount").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("InnerMessage")){
                        postBoxDeliveryMessage = dataSnapshot.child("InnerMessage").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAllCartDetails() {
        cartReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("totalAmount")) {
                        String tA = dataSnapshot.child("totalAmount").getValue().toString();
                        totalAmount = Double.valueOf(tA);
                        noProduct.setVisibility(View.GONE);
                    }
                } else {
                    noProduct.setVisibility(View.VISIBLE);
                    orderAll.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayAllProducts() {
        FirebaseRecyclerAdapter<cartProducts,productViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<cartProducts, productViewHolder>(

                        cartProducts.class,
                        R.layout.layout_all_post_display,
                        productViewHolder.class,
                        databaseReference

                ) {
                    @Override
                    protected void populateViewHolder(final productViewHolder viewHolder, cartProducts model, int position) {
                        final String cartPostKey = getRef(position).getKey();
                        String limitedQuantity = model.getLimitedQuantity();

                        if (TextUtils.isEmpty(limitedQuantity))
                            viewHolder.setLimitedQuantity("", View.GONE);
                        else{
                            if (limitedQuantity.equalsIgnoreCase("yes"))
                                viewHolder.setLimitedQuantity("Bulk Order", View.VISIBLE);
                            else
                                viewHolder.setLimitedQuantity("", View.GONE);
                        }

                        viewHolder.setQuantity(model.getQuantity() + "unit(s)");

                        String postKey = model.getPostKey();
                        final String randomName2 = model.getCartPostKey();

                        if (TextUtils.isEmpty(postKey))
                            Toast.makeText(ShowCartActivity.this, "No item in cart", Toast.LENGTH_LONG).show();

                        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
                        productReference.keepSynced(true);

                        productReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("ProductName")){
                                        String pName = dataSnapshot.child("ProductName").getValue().toString();
                                        viewHolder.setProductName(pName);
                                    }

                                    if (dataSnapshot.hasChild("ProductPicture1")){
                                        String pPicture = dataSnapshot.child("ProductPicture1").getValue().toString();
                                        viewHolder.setProductPicture(pPicture, getApplicationContext());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ShowCartActivity.this, ShowCartDetailsActivity.class);
                                intent.putExtra("cartPostKey", cartPostKey);
                                intent.putExtra("randomName2", randomName2);
                                intent.putExtra("cartOrderCounter", cartOrderCounter);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class productViewHolder extends RecyclerView.ViewHolder{

        View mView;
        TextView discountPercent, discountPrice, pName;
        ImageView priceConceal;
        RelativeLayout relativeLayout;

        public productViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            discountPercent = mView.findViewById(R.id.allPost_productDiscountPercent);
            discountPrice = mView.findViewById(R.id.allPost_productDiscountPrice);
            priceConceal = mView.findViewById(R.id.allPost_priceConceal);
            relativeLayout = mView.findViewById(R.id.allPost_relative);
            pName = mView.findViewById(R.id.allPost_productName);

            int width = relativeLayout.getLayoutParams().width;
            int height = relativeLayout.getLayoutParams().height;
            Log.i("width", width+ "");

            autofit noColums = new autofit();
            noColums.autofit(mView.getContext());

            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(noColums.getLayoutWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT));
            pName.setHeight(noColums.getTextHeight());

            discountPercent.setVisibility(View.GONE);
            discountPrice.setVisibility(View.GONE);
            priceConceal.setVisibility(View.GONE);
        }

        public void setLimitedQuantity(String limitedQuantity, int visibility) {
            discountPercent.setText(limitedQuantity);
            discountPercent.setVisibility(visibility);
        }

        public void setQuantity(String quantity) {
            TextView qty = mView.findViewById(R.id.allPost_productPrice);
            qty.setText(quantity);
        }

        public void setProductPicture(final String pic, final Context context){
            final ImageView pImage = mView.findViewById(R.id.allPost_productImage);
            //loading picture offline
            Picasso.get().load(pic).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning).into(pImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(pic).placeholder(R.drawable.warning).into(pImage);
                }
            });
        }

        public void setProductName(String name){
            pName.setText(name);
        }
    }

    //Show all user information and save it
    private void getUserInformation() {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profilePicture")){
                        profilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("fullName")){
                        name = dataSnapshot.child("fullName").getValue().toString();
                    }
                    if(dataSnapshot.hasChild("phoneNumber")){
                        num = dataSnapshot.child("phoneNumber").getValue().toString();
                    }

                    emailAddress = mAuth.getCurrentUser().getEmail();

                } else {
                    Toast.makeText(ShowCartActivity.this, "Profile details not completed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
            if (!TextUtils.isEmpty(getTransactionPictureUri)){
                try{
                    StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(getTransactionPictureUri);
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
        saveCurrentDate = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTimeFormat.format(postTime.getTime());

        saveRandomName = saveCurrentDate + getCurrentUserId + saveCurrentTime;

        StorageReference filePath = storageReference.child(getCurrentUserId)
                .child(imageUri.getLastPathSegment() + saveRandomName + ".jpg");
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        getTransactionPictureUri = uri.toString();
                        //loading picture offline
                        Picasso.get().
                                load(getTransactionPictureUri).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.no_image)
                                .into(transactionPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(getTransactionPictureUri).fit()
                                        .placeholder(R.drawable.no_image)
                                        .into(transactionPicture);
                            }
                        });
                        Toast.makeText(ShowCartActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(ShowCartActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Toast.makeText(this, "Select back or close button", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToViewPictureActivity(String pName, String pPicture) {
        Intent intent = new Intent(ShowCartActivity.this, ViewPictureActivity.class);
        intent.putExtra("imageText", pName);
        intent.putExtra("image", pPicture);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
