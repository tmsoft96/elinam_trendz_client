package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class OrderProductActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ImageView productImage, qtyAdd, qtyMinus;
    private TextView productName, productPrize;
    private EditText quantity;
    private Button selectDeliveryButton;
    private Dialog deliveryDialog, messageDialog, previousDataDialog, deliveryFormDialog, personalDialog,
            postBoxDialog, paymentDialog, instantPaymentDialog, dialog;
    private ProgressDialog progressDialog;

    private ImageView transactionPicture;

    private String pName, pPrice, pImage, pQtyAva = "0", pLimitedQty = "0", pCategory = "";
    private String name, num, emailAddress, saveCurrentTime, saveCurrentDate, saveRandomName;
    private int gallaryPick = 1;
    private Uri imageUri;
    private String getTransactionPictureUri;
    private String deliveryType;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference userReference, paymentReference, temReference;
    private StorageReference storageReference;

    private String postBoxDeliveryFee, postBoxDeliveryMessage, personnelDeliveryFee;

    private String tempTownName = null, tempLocation = null, tempSenderName = null, tempAmountPaid = null, tempTransId = null,
            tempTransPicture = null, tempRegion = null, tempDistrict = null, tempTownNamePost = null, tempBoxNumber = null,
            tempQtyOrder = null;

    private String defaultValue;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_product);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();

        try{
            pName = getIntent().getExtras().get("ProductName").toString();
            pPrice = getIntent().getExtras().get("ProductPrice").toString();
            pImage = getIntent().getExtras().get("ProductPicture1").toString();
            pQtyAva = getIntent().getExtras().get("ProductQuantityAvailable").toString();
            pLimitedQty = getIntent().getExtras().get("LimitedQty").toString();
            pCategory = getIntent().getExtras().get("ProductCategory").toString();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "OrderProductActivity", "main method");
        }

        myToolBar = findViewById(R.id.orderProduct_toolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ordering Product");

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("Transaction Picture");
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(getCurrentUserId);
        userReference.keepSynced(true);
        temReference = FirebaseDatabase.getInstance().getReference().child("Temp").child(getCurrentUserId);
        temReference.keepSynced(true);

        productImage = findViewById(R.id.orderProduct_productPicture1);
        productName = findViewById(R.id.orderProduct_productName);
        productPrize = findViewById(R.id.orderProduct_productPrice);
        quantity = findViewById(R.id.orderProduct_quantityNUmber);
        selectDeliveryButton = findViewById(R.id.orderProduct_selectDelivery);
        deliveryDialog = new Dialog(this, R.style.Theme_CustomDialog);
        messageDialog = new Dialog(this, R.style.Theme_CustomDialog);
        previousDataDialog = new Dialog(this, R.style.Theme_CustomDialog);
        deliveryFormDialog = new Dialog(this, R.style.Theme_CustomDialog);
        personalDialog = new Dialog(this, R.style.Theme_CustomDialog);
        postBoxDialog = new Dialog(this, R.style.Theme_CustomDialog);
        paymentDialog = new Dialog(this, R.style.Theme_CustomDialog);
        instantPaymentDialog = new Dialog(this, R.style.Theme_CustomDialog);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        progressDialog = new ProgressDialog(this);
        qtyAdd = findViewById(R.id.orderProduct_quantityNumberAdd);
        qtyMinus = findViewById(R.id.orderProduct_quantityNumberMinus);



        try{
            displayProductDetails();
            getUserInformation();
            allDeliveryInformation();
            determinePreviousDetail();
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
            tempQtyOrder = getIntent().getExtras().get("tempQtyOrder").toString();
            defaultValue = getIntent().getExtras().get("defaultValue").toString();

            quantity.setText(tempQtyOrder);
        } catch(Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "OrderProductActivity", "main method");
        }

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog();
            }
        });

        if (pCategory.equals("Bulk Purchase")){
            String pQuantity = quantity.getText().toString();
            int qty = Integer.parseInt(pQuantity);
            int ltdQty = Integer.parseInt(pLimitedQty);

            if (ltdQty == qty){
                qtyMinus.setEnabled(false);
                Toast.makeText(OrderProductActivity.this, "Minimum quantity order is " + pLimitedQty, Toast.LENGTH_SHORT).show();
            }
        }

        selectDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pQuantity = quantity.getText().toString();

                if (TextUtils.isEmpty(pQuantity))
                    Toast.makeText(OrderProductActivity.this, "Enter quantity you want to order", Toast.LENGTH_SHORT).show();
                else{
                    int userQuantity = 0;
                    userQuantity = Integer.parseInt(pQuantity);
                    int availableQuantity = Integer.parseInt(pQtyAva);
                    if (userQuantity > availableQuantity)
                        Toast.makeText(OrderProductActivity.this, "Your preferred quantity is more than the available quantity",
                                Toast.LENGTH_LONG).show();
                    else if (userQuantity < Integer.parseInt(pLimitedQty) && pCategory.equalsIgnoreCase("Bulk Purchase"))
                        Toast.makeText(OrderProductActivity.this, "Your preferred quantity is less than the minimum quantity " +
                                "for bulk purchase", Toast.LENGTH_LONG).show();
                    else
                        viewDeliveryDialog(pQuantity);
                }
            }
        });

        qtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qq = quantity.getText().toString();
                int qty = Integer.parseInt(qq);

                if (pCategory.equalsIgnoreCase("Bulk Purchase")){
                    int ltdQty = Integer.parseInt(pLimitedQty);

                    if (qty > ltdQty)
                        qtyMinus.setEnabled(true);

                    ++qty;
                    quantity.setText(qty + "");
                } else {
                    ++qty;
                    quantity.setText(qty + "");
                }
            }
        });

        qtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qq = quantity.getText().toString();
                int qty = Integer.parseInt(qq);
                if (pCategory.equalsIgnoreCase("Bulk Purchase")){
                    int ltdQty = Integer.parseInt(pLimitedQty);
                    if (qty == ltdQty){
                        qtyMinus.setEnabled(false);
                        Toast.makeText(OrderProductActivity.this, "Minimum quantity order is " + pLimitedQty, Toast.LENGTH_SHORT).show();
                    } else
                        --qty;
                    quantity.setText(qty + "");
                } else {
                    --qty;
                    if (qty > 0)
                        quantity.setText(qty + "");
                }

            }
        });
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
    private void getUserPreviousDetails(final String productQuantity) {
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
                    showFillDeliveryDetailsForPersonnelDelivery(productQuantity);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Showing delivery dialog
    private void viewDeliveryDialog(final String productQuantity) {
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
                showMessageDialog(productQuantity);
                deliveryType = "Personnel Delivery";
            }
        });

        layoutBusTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OrderProductActivity.this, "Still working on this delivery option", Toast.LENGTH_LONG).show();
            }
        });

        layoutPoxBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageDialog(productQuantity);
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

    //showing InnerMessage to the customer dialog
    private void showMessageDialog(final String productQuantity) {
        TextView message, closeMsg;
        Button deliveryDetails;

        messageDialog.setContentView(R.layout.dialog_order_message);
        message = messageDialog.findViewById(R.id.dialogOrderMsg_message);
        deliveryDetails = messageDialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);
        closeMsg = messageDialog.findViewById(R.id.dialogOrderMsg_close);

        String msg = "Hello " + name + ", customer service will contact you in some few minutes to confirm your order.\n"
                + "You can proceed to fill the delivery form";

        message.setText(msg);

        deliveryDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageDialog.dismiss();
                if (defaultValue.equals("temp"))
                    showFillDeliveryDetailsForPersonnelDelivery(productQuantity);
                else
                    showPreviousDataDialog(productQuantity);
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

    private void showPreviousDataDialog(final String productQuantity) {
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
                getUserPreviousDetails(productQuantity);
                previousDataDialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousDataDialog.dismiss();
                showFillDeliveryDetailsForPersonnelDelivery(productQuantity);
            }
        });

        previousDataDialog.show();
    }

    //show Fill Delivery Details for personnel delivery
    private void showFillDeliveryDetailsForPersonnelDelivery(final String productQuantity){
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

            fullName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(OrderProductActivity.this, "Edit your name in your profile", Toast.LENGTH_LONG).show();
                }
            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(OrderProductActivity.this, "You cannot change your default email address", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(OrderProductActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(uTown))
                        Toast.makeText(OrderProductActivity.this, "Enter your town name", Toast.LENGTH_SHORT).show();
                    else if (TextUtils.isEmpty(uDelivery))
                        Toast.makeText(OrderProductActivity.this, "Enter your delivery location", Toast.LENGTH_SHORT).show();
                    else {
                        if (deliveryType.equals("Personnel Delivery")){
                            deliveryFormDialog.dismiss();
                            personalDialogShow(uFull, uPhone, uTown, uEmail, uDelivery, productQuantity);
                        } else if (deliveryType.equals("Post Box Delivery")){
                            postBoxDialogShow(uFull, uPhone, uTown, uEmail, uDelivery, productQuantity);
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
            catchErrors.setErrors(ex.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "OrderProductActivity", "showFillDeliveryDetailsForPersonnelDelivery");
        }
    }

    private void personalDialogShow(final String uFull, final String uPhone, final String uTown, final String uEmail,
                                    final String uDelivery, final String productQuantity) {
        Button makePaymentNow, makePaymentOnDelivery, viewAllCharges;
        personalDialog.setContentView(R.layout.dialog_choose_payment);

        makePaymentNow = personalDialog.findViewById(R.id.orderProductDialog_makePaymentNow);
        makePaymentOnDelivery = personalDialog.findViewById(R.id.orderProductDialog_makePaymentOnDelivery);
        viewAllCharges = personalDialog.findViewById(R.id.orderProductDialog_viewCharges);

        viewAllCharges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int aa = personnelDeliveryFee.length();
                String amountT = personnelDeliveryFee.substring(3,aa);
                final double perDeliveryFee = Double.valueOf(amountT);

                int mm = pPrice.length();
                final String value = pPrice.substring(3,mm);
                double tMoney = Double.valueOf(value);
                double tQuantity = Double.valueOf(productQuantity);
                double aAmount = tMoney * tQuantity;
                final double actualAmount = aAmount + perDeliveryFee;
                showAllChargesDialog(value, productQuantity, perDeliveryFee, actualAmount);
            }
        });

        makePaymentOnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalDialog.dismiss();
                showPaymentOnDelivery(uFull, uPhone, uTown, uEmail, uDelivery, pName, pPrice, pImage, productQuantity,
                        "Payment On Delivery", personnelDeliveryFee);
            }
        });

        makePaymentNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personalDialog.dismiss();
                showMakeInstantPayment(uFull, uPhone, uTown, uEmail, uDelivery, pName, pPrice, pImage, productQuantity,
                        "", "", "", "", "Instant Payment",
                        personnelDeliveryFee);
            }
        });

        personalDialog.show();
    }

    private void postBoxDialogShow(final String uFull, final String uPhone, final String uTown, final String uEmail,
                                   final String uDelivery, final String productQuantity) {
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
                    Toast.makeText(OrderProductActivity.this, "Enter your region", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getTown))
                    Toast.makeText(OrderProductActivity.this, "Enter your town", Toast.LENGTH_SHORT).show();
                else if (getRegion.equalsIgnoreCase("Select your region"))
                    Toast.makeText(OrderProductActivity.this, "Select your current region", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getBoxNum))
                    Toast.makeText(OrderProductActivity.this, "Enter your post box number", Toast.LENGTH_SHORT).show();
                else {
                    postBoxDialog.dismiss();
                    paymentDialogShow(uFull, uPhone, uTown, uEmail, uDelivery, productQuantity, getRegion, getDistrict, getTown, getBoxNum);
                }
            }
        });

        postBoxDialog.show();
    }

    private void paymentDialogShow(final String uFull, final String uPhone, final String uTown, final String uEmail, final String uDelivery, final String productQuantity,
                                   final String getRegion, final String getDistrict, final String getTown, final String getBoxNum) {
        TextView message;
        Button contButton;

        paymentDialog.setContentView(R.layout.dialog_delivery_message);
        message = paymentDialog.findViewById(R.id.dialogDeliveryMessage_message);
        contButton = paymentDialog.findViewById(R.id.dialogDeliveryMessage_continue);

        if (TextUtils.isEmpty(postBoxDeliveryMessage)){
            paymentDialog.dismiss();
            showMakeInstantPayment(uFull, uPhone, uTown, uEmail, uDelivery, pName, pPrice, pImage, productQuantity,
                    getRegion, getDistrict, getTown, getBoxNum, "Instant Payment", postBoxDeliveryFee);
        }

        message.setText(postBoxDeliveryMessage);

        contButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentDialog.dismiss();
                showMakeInstantPayment(uFull, uPhone, uTown, uEmail, uDelivery, pName, pPrice, pImage, productQuantity,
                        getRegion, getDistrict, getTown, getBoxNum, "Instant Payment", postBoxDeliveryFee);
            }
        });

        paymentDialog.show();
    }


    //showing payment on delivery
    private void showPaymentOnDelivery(final String uFullName, final String uPhoneNumber, final String uTownName,
                                       final String uEmailAddress, final String uDeliveryLocation, final String productName,
                                       final String productPrice, final String productPicture, final String productQuantity,
                                       String paymentType, String deliveryFee) {

        int aa = deliveryFee.length();
        String amountT = deliveryFee.substring(3,aa);
        final double perDeliveryFee = Double.valueOf(amountT);

        int mm = productPrice.length();
        final String value = productPrice.substring(3,mm);
        double tMoney = Double.valueOf(value);
        double tQuantity = Double.valueOf(productQuantity);
        double aAmount = tMoney * tQuantity;
        final double actualAmount = aAmount + perDeliveryFee;

        String calAmount = "GHC" + actualAmount;
        String dFee = "GHC" + perDeliveryFee;

        sendUserToConfirmOrderDetailsActivity(getCurrentUserId, uFullName, uPhoneNumber, uTownName, uEmailAddress, uDeliveryLocation,
                deliveryType, dFee, productName, productPrice, productPicture, productQuantity, calAmount, paymentType, "",
                "", "", "", "", "", "",
                "", "");
    }


    //showing instant payment details
    private void showMakeInstantPayment(final String uFullName, final String uPhoneNumber, final String uTownName,
                                        final String uEmailAddress, final String uDeliveryLocation, final String productName,
                                        final String productPrice, final String productPicture, final String productQuantity,
                                        final String postRegion, final String postDistrict, final String postTown, final String postBoxNumber,
                                        final String paymentType, final String deliveryFee) {
        final TextView note, mobileMoneyNumber, close, totalMoney, totalMoneyDetails;
        final EditText senderName, amountPaid, transactionId;
        ImageButton upload;
        Button paymentButton;
        final Spinner senderNetworks;

        instantPaymentDialog.setContentView(R.layout.dialog_payment_layout);
        close = instantPaymentDialog.findViewById(R.id.payment_close);
        note = instantPaymentDialog.findViewById(R.id.payment_note);
        mobileMoneyNumber = instantPaymentDialog.findViewById(R.id.payment_mobileMoneyNumber);
        amountPaid = instantPaymentDialog.findViewById(R.id.payment_amountPaid);
        transactionId = instantPaymentDialog.findViewById(R.id.payment_transactionId);
        senderName = instantPaymentDialog.findViewById(R.id.payment_senderName);
        transactionPicture = instantPaymentDialog.findViewById(R.id.payment_transactionPicture);
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
                    .placeholder(R.drawable.no_image).into(transactionPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(tempTransPicture)
                            .placeholder(R.drawable.no_image).into(transactionPicture);
                }
            });
        } catch (Exception ex){
            Log.i("error", ex.getMessage());
            catchErrors.setErrors(ex.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "OrderProductActivity", "showMakeInstantPayment");
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

        int mm = productPrice.length();
        final String value = productPrice.substring(3,mm);
        double tMoney = Double.valueOf(value);
        double tQuantity = Double.valueOf(productQuantity);
        double aAmount = tMoney * tQuantity;
        final double actualAmount = aAmount + perDeliveryFee;

        totalMoney.setText("GHC" + actualAmount);

        totalMoneyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllChargesDialog(value, productQuantity, perDeliveryFee, actualAmount);
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
                String sPic = getTransactionPictureUri;

                if (TextUtils.isEmpty(sPic) && TextUtils.isEmpty(sName))
                    Toast.makeText(OrderProductActivity.this, "Please upload your transaction picture", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(OrderProductActivity.this, "Enter amount paid", Toast.LENGTH_SHORT).show();
                else if (TextUtils.isEmpty(getSenderName))
                    Toast.makeText(OrderProductActivity.this, "Enter sender name", Toast.LENGTH_SHORT).show();
                else if  (TextUtils.isEmpty(getTransactionId))
                    Toast.makeText(OrderProductActivity.this, "Enter transaction ID", Toast.LENGTH_SHORT).show();
                else if (getSenderNetwork.equals("Select Network"))
                    Toast.makeText(OrderProductActivity.this, "Select network", Toast.LENGTH_SHORT).show();
                else if  (TextUtils.isEmpty(getTransactionPictureUri))
                    Toast.makeText(OrderProductActivity.this, "Upload transaction", Toast.LENGTH_SHORT).show();
                else {
                    instantPaymentDialog.dismiss();
                    String calAmount = "GHC" + actualAmount;
                    String dFee = "GHC" + perDeliveryFee;
                    sendUserToConfirmOrderDetailsActivity(getCurrentUserId, uFullName, uPhoneNumber, uTownName, uEmailAddress, uDeliveryLocation,
                            deliveryType, dFee, productName, productPrice, productPicture, productQuantity, calAmount, paymentType, "GHC" + getAmountPaid,
                            getSenderName, getTransactionId, getSenderNetwork, getTransactionPictureUri, postRegion, postDistrict, postTown, postBoxNumber);
                }
            }
        });

        instantPaymentDialog.show();
    }

    private void showAllChargesDialog(String value, String productQuantity, double perDeliveryFee, double actualAmount) {
        final Dialog msgDialog;
        TextView msgClose, message;
        Button msgButton;

        msgDialog = new Dialog(OrderProductActivity.this, R.style.Theme_CustomDialog);
        msgDialog.setContentView(R.layout.dialog_order_message);
        msgClose = msgDialog.findViewById(R.id.dialogOrderMsg_close);
        message = msgDialog.findViewById(R.id.dialogOrderMsg_message);
        msgButton = msgDialog.findViewById(R.id.dialogOrderMsg_deliveryDetails);

        String msg = "Product Price : \tGHC" + value + "\nQuantity Ordered : \t" + productQuantity + " unit(s)" +
                "\nDelivery Fee : \tGHC" + perDeliveryFee + "\nTotal Amount : \tGHC" + actualAmount;

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

    //sending user to confirm activity
    private void sendUserToConfirmOrderDetailsActivity(String getCurrentUserId, String uFullName, String uPhoneNumber,
                                                       String uTownName, String uEmailAddress, String uDeliveryLocation,
                                                       String deliveryType, String deliveryFee, String productName, String productPrice,
                                                       String productPicture, String productQuantity, String actualAmount,
                                                       String paymentType, String getAmountPaid, String getSenderName,
                                                       String getTransactionId, String getSenderNetwork, String getTransactionPictureUri, String postRegion,
                                                       String postDistrict, String postTown, String postBoxNumber) {

        Intent intent = new Intent(OrderProductActivity.this, ConfirmOrderActivity.class);
        intent.putExtra("userId", getCurrentUserId);
        intent.putExtra("userFullName", uFullName);
        intent.putExtra("userPhoneNumber", uPhoneNumber);
        intent.putExtra("userTownName", uTownName);
        intent.putExtra("userEmail", uEmailAddress);
        intent.putExtra("userDeliveryLocation", uDeliveryLocation);
        intent.putExtra("deliveryType", deliveryType);
        intent.putExtra("deliveryFee", deliveryFee);
        intent.putExtra("productName", productName);
        intent.putExtra("productPrice", productPrice);
        intent.putExtra("productPicture", productPicture);
        intent.putExtra("productQuantity", productQuantity);
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
        intent.putExtra("ProductQuantityAvailable",pQtyAva);
        startActivity(intent);
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
                        Picasso.get()
                                .load(getTransactionPictureUri).fit()
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
                        Toast.makeText(OrderProductActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = e.getMessage();
                        Toast.makeText(OrderProductActivity.this, "Failed to upload picture\n" + errorMessage + "\ntry again....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    //displaying product details when user click on this product
    private void displayProductDetails() {
        //loading picture offline
        Picasso.get().load(pImage).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(productImage, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(pImage)
                        .placeholder(R.drawable.warning).into(productImage);
            }
        });
        productName.setText(pName);
        productPrize.setText(pPrice);

        try{
            if (pCategory.equalsIgnoreCase("Bulk Purchase")){
                quantity.setText(pLimitedQty);
            }
        } catch (Exception ex){
            Log.i("error", ex.getMessage());
            catchErrors.setErrors(ex.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "OrderProductActivity", "displayProductDetails");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            sendUserToMainActivity();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
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

                    emailAddress = mAuth.getCurrentUser().getEmail();

                } else {
                    Toast.makeText(OrderProductActivity.this, "Profile details not completed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void viewPictureDialog() {
        TextView close, title;
        final ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pName);

        //loading picture offline
        Picasso
                .get()
                .load(pImage)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image)
                .into(picture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pImage)
                        .placeholder(R.drawable.no_image)
                        .into(picture);
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewPictureActivity(pName, pImage);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendUserToViewPictureActivity(String pName, String pPicture) {
        Intent intent = new Intent(OrderProductActivity.this, ViewPictureActivity.class);
        intent.putExtra("imageText", pName);
        intent.putExtra("image", pPicture);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToMainActivity(){
        Intent intent = new Intent(OrderProductActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
