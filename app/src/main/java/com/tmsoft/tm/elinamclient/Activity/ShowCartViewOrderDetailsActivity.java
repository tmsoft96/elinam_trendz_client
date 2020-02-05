package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.R;

public class ShowCartViewOrderDetailsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private TextView orderTime, orderDate, productOrderId, orderConfirm, userFullName, deliveryLocation,
            userPhoneNumber, userEmailAddress, userTown, senderFullName, amountPaid, transactionId, senderNetwork,
            deliveryDate, deliveryFee, deliveryConfirm, paymentType, deliveryType, totalAmount, postDeliveryButton, paymentConfirm;
    private ImageView transactionPicture;
    private LinearLayout linearLayout, linearSenderName, linearAmountPaid, linearTransactionId, linearSenderNetwork;
    private Dialog dialog;

    private DatabaseReference databaseReference;

    private String orderProductKey, pTransactionPicture, pTransactionId, cartPostKey;
    private String zPostRegion, zPostTown, zPostDistrict, zPostNumber, cartOrderCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart_view_order_details);

        myToolBar = findViewById(R.id.showCartViewOrderDetails_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Ordered");

        orderDate = findViewById(R.id.showCartViewOrderDetails_date);
        orderTime = findViewById(R.id.showCartViewOrderDetails_time);
        productOrderId = findViewById(R.id.showCartViewOrderDetails_productId);
        orderConfirm = findViewById(R.id.showCartViewOrderDetails_orderConfirm);
        userFullName = findViewById(R.id.showCartViewOrderDetails_userFullName);
        deliveryLocation = findViewById(R.id.showCartViewOrderDetails_userLocation);
        userPhoneNumber = findViewById(R.id.showCartViewOrderDetails_userPhoneNumber);
        userEmailAddress = findViewById(R.id.showCartViewOrderDetails_userEmail);
        senderFullName = findViewById(R.id.showCartViewOrderDetails_senderFullName);
        amountPaid = findViewById(R.id.showCartViewOrderDetails_amountPaid);
        transactionId = findViewById(R.id.showCartViewOrderDetails_transactionId);
        senderNetwork = findViewById(R.id.showCartViewOrderDetails_senderNetwork);
        deliveryDate = findViewById(R.id.showCartViewOrderDetails_deliveryDate);
        deliveryFee = findViewById(R.id.showCartViewOrderDetails_deliveryFee);
        deliveryConfirm = findViewById(R.id.showCartViewOrderDetails_deliveryConfirm);
        userTown = findViewById(R.id.showCartViewOrderDetails_userTown);
        paymentType = findViewById(R.id.showCartViewOrderDetails_paymentType);
        deliveryType = findViewById(R.id.showCartViewOrderDetails_deliveryType);
        totalAmount = findViewById(R.id.showCartViewOrderDetails_totalAmount);
        transactionPicture = findViewById(R.id.showCartViewOrderDetails_transactionPicture);
        linearLayout = findViewById(R.id.showCartViewOrderDetails_showAllProduct);
        postDeliveryButton = findViewById(R.id.showCartViewOrderDetails_postDelivery);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        linearAmountPaid = findViewById(R.id.showCartViewOrderDetails_linearSenderAmountPaid);
        linearSenderName = findViewById(R.id.showCartViewOrderDetails_linearSenderFullName);
        linearSenderNetwork = findViewById(R.id.showCartViewOrderDetails_linearSenderNetwork);
        linearTransactionId = findViewById(R.id.showCartViewOrderDetails_linearTransactionId);
        paymentConfirm = findViewById(R.id.showCartViewOrderDetails_paymentConfirm);

        orderProductKey = getIntent().getExtras().get("cartPostKey").toString();

        postDeliveryButton.setVisibility(View.GONE);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
        databaseReference.keepSynced(true);

        transactionPicture.setVisibility(View.GONE);

        getAllInformation(orderProductKey);

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewPicActivity(pTransactionId, pTransactionPicture);
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewCartProductActivity();
            }
        });

        postDeliveryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPostDetails();
            }
        });
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

    private void getAllInformation(String orderProductKey) {
        databaseReference.child(orderProductKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("allProductDetails")){
                        cartPostKey = dataSnapshot.child("allProductDetails").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("orderDate")){
                        String pOrderDate = dataSnapshot.child("orderDate").getValue().toString();
                        orderDate.setText(pOrderDate);
                    }

                    if (dataSnapshot.hasChild("orderTime")){
                        String pOrderTime = dataSnapshot.child("orderTime").getValue().toString();
                        orderTime.setText(pOrderTime);
                    }

                    if (dataSnapshot.hasChild("productId")){
                        String pProductId = dataSnapshot.child("productId").getValue().toString();
                        productOrderId.setText(pProductId);
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        String pOrderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                        orderConfirm.setText(pOrderConfirm);
                    }

                    if (dataSnapshot.hasChild("userFullName")){
                        String pUserFullName = dataSnapshot.child("userFullName").getValue().toString();
                        userFullName.setText(pUserFullName);
                    }

                    if (dataSnapshot.hasChild("userTown")){
                        String pUserTown = dataSnapshot.child("userTown").getValue().toString();
                        userTown.setText(pUserTown);
                    }

                    if (dataSnapshot.hasChild("userLocation")){
                        String pUserLocation = dataSnapshot.child("userLocation").getValue().toString();
                        deliveryLocation.setText(pUserLocation);
                    }

                    if (dataSnapshot.hasChild("userPhoneNumber")){
                        String pUserPhoneNumber = dataSnapshot.child("userPhoneNumber").getValue().toString();
                        userPhoneNumber.setText(pUserPhoneNumber);
                    }

                    if (dataSnapshot.hasChild("userEmail")){
                        String pUserEmail = dataSnapshot.child("userEmail").getValue().toString();
                        userEmailAddress.setText(pUserEmail);
                    }
                    String pPaymentType = "";
                    if (dataSnapshot.hasChild("paymentType")){
                        pPaymentType = dataSnapshot.child("paymentType").getValue().toString();
                        paymentType.setText(pPaymentType);

                        if (pPaymentType.equalsIgnoreCase("Payment On Delivery")){
                            linearTransactionId.setVisibility(View.GONE);
                            linearSenderNetwork.setVisibility(View.GONE);
                            linearSenderName.setVisibility(View.GONE);
                            linearAmountPaid.setVisibility(View.GONE);
                        }
                    }

                    if (dataSnapshot.hasChild("paymentSenderName")){
                        String pSenderFullName = dataSnapshot.child("paymentSenderName").getValue().toString();
                        senderFullName.setText(pSenderFullName);
                    }

                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                        String pAmountPaid = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                        amountPaid.setText(pAmountPaid);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionId")){
                        pTransactionId = dataSnapshot.child("paymentTransactionId").getValue().toString();
                        transactionId.setText(pTransactionId);
                    }

                    if (dataSnapshot.hasChild("paymentSenderNetwork")){
                        String pSenderNetwork = dataSnapshot.child("paymentSenderNetwork").getValue().toString();
                        senderNetwork.setText(pSenderNetwork);
                    }

                    if (dataSnapshot.hasChild("paymentConfirm")){
                        String pPaymentConfirm = dataSnapshot.child("paymentConfirm").getValue().toString();
                        if (pPaymentType.equalsIgnoreCase("Payment On Delivery")){
                            paymentConfirm.setText("Payment On Delivery");
                        } else
                            paymentConfirm.setText(pPaymentConfirm);
                    }

                    if (dataSnapshot.hasChild("deliveryDate")){
                        String pDeliveryDate = dataSnapshot.child("deliveryDate").getValue().toString();
                        deliveryDate.setText(pDeliveryDate);
                    }

                    if (dataSnapshot.hasChild("deliveryFee")){
                        String pDeliveryFee = dataSnapshot.child("deliveryFee").getValue().toString();
                        deliveryFee.setText(pDeliveryFee);
                    }

                    if (dataSnapshot.hasChild("deliveryType")){
                        String pDeliveryType = dataSnapshot.child("deliveryType").getValue().toString();
                        deliveryType.setText(pDeliveryType);

                        if (pDeliveryType.equals("Post Box Delivery"))
                            postDeliveryButton.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        String pDeliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                        deliveryConfirm.setText(pDeliverySuccess);
                    }

                    if (dataSnapshot.hasChild("totalAmount")){
                        String pTotalAmount = dataSnapshot.child("totalAmount").getValue().toString();
                        totalAmount.setText(pTotalAmount);
                    }

                    if (dataSnapshot.hasChild("paymentTransactionPicture")){
                        pTransactionPicture = dataSnapshot.child("paymentTransactionPicture").getValue().toString();
                        //loading picture offline
                        if (!TextUtils.isEmpty(pTransactionPicture)){
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
                            transactionPicture.setVisibility(View.VISIBLE);
                        }
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

                    if (dataSnapshot.hasChild("postBoxNumber")){
                        zPostNumber = dataSnapshot.child("postBoxNumber").getValue().toString();
                    }

                    if (dataSnapshot.hasChild("cartOrderCounter")){
                        cartOrderCounter = dataSnapshot.child("cartOrderCounter").getValue().toString();
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
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToViewPicActivity(String picName, String picUrl){
        Intent intent = new Intent(ShowCartViewOrderDetailsActivity.this, ViewPictureActivity.class);
        intent.putExtra("imageText", picName);
        intent.putExtra("image", picUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToViewCartProductActivity(){
        Intent intent = new Intent(ShowCartViewOrderDetailsActivity.this, ShowCartViewOrderDetailsProductActivity.class);
        intent.putExtra("cartPostKey", cartPostKey);
        intent.putExtra("cartOrderCounter", cartOrderCounter);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
