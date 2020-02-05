package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

public class ViewProductOrderDetailsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private TextView orderTime, orderDate, productOrderId, productName, productPrice, qtyOrder,
            orderConfirm, userFullName, deliveryLocation, userPhoneNumber, userEmailAddress, userTown,
            senderFullName, amountPaid, transactionId, senderNetwork, deliveryDate, deliveryFee,
            deliveryConfirm, paymentType, deliveryType, totalAmount, postDeliveryButton, paymentConfirm;
    private ImageView productImage, transactionPicture;
    private Dialog dialog;
    private LinearLayout linearSenderName, linearAmountPaid, linearTransactionId, linearSenderNetwork;

    private DatabaseReference databaseReference;

    private String orderProductKey, pProductName, pProductPicture, pTransactionPicture, pTransactionId;
    private String zPostRegion, zPostTown, zPostDistrict, zPostNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_order_details);

        myToolBar = findViewById(R.id.viewProductOrderDetails_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Ordered");

        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        orderDate = findViewById(R.id.viewProductOrderDetails_orderDate);
        orderTime = findViewById(R.id.viewProductOrderDetails_orderTime);
        productOrderId = findViewById(R.id.viewProductOrderDetails_productId);
        productName = findViewById(R.id.viewProductOrderDetails_productName);
        productPrice = findViewById(R.id.viewProductOrderDetails_productPrice);
        qtyOrder = findViewById(R.id.viewProductOrderDetails_quantity);
        orderConfirm = findViewById(R.id.viewProductOrderDetails_orderConfirm);
        userFullName = findViewById(R.id.viewProductOrderDetails_userFullName);
        deliveryLocation = findViewById(R.id.viewProductOrderDetails_userLocation);
        userPhoneNumber = findViewById(R.id.viewProductOrderDetails_userPhoneNumber);
        userEmailAddress = findViewById(R.id.viewProductOrderDetails_userEmail);
        senderFullName = findViewById(R.id.viewProductOrderDetails_senderFullName);
        amountPaid = findViewById(R.id.viewProductOrderDetails_amountPaid);
        transactionId = findViewById(R.id.viewProductOrderDetails_transactionId);
        senderNetwork = findViewById(R.id.viewProductOrderDetails_senderNetwork);
        deliveryDate = findViewById(R.id.viewProductOrderDetails_deliveryDate);
        deliveryFee = findViewById(R.id.viewProductOrderDetails_deliveryFee);
        deliveryConfirm = findViewById(R.id.viewProductOrderDetails_deliveryConfirm);
        productImage = findViewById(R.id.viewProductOrderDetails_productPicture);
        userTown = findViewById(R.id.viewProductOrderDetails_userTown);
        paymentType = findViewById(R.id.viewProductOrderDetails_paymentType);
        deliveryType = findViewById(R.id.viewProductOrderDetails_deliveryType);
        totalAmount = findViewById(R.id.viewProductOrderDetails_totalAmount);
        transactionPicture = findViewById(R.id.viewProductOrderDetails_transactionPicture);
        postDeliveryButton = findViewById(R.id.viewProductOrderDetails_postDelivery);
        linearAmountPaid = findViewById(R.id.viewProductOrderDetails_linearAmountPaid);
        linearSenderName = findViewById(R.id.viewProductOrderDetails_linearSenderFullName);
        linearSenderNetwork = findViewById(R.id.viewProductOrderDetails_linearSenderNetwork);
        linearTransactionId = findViewById(R.id.viewProductOrderDetails_linearTransactionId);
        paymentConfirm = findViewById(R.id.viewProductOrderDetails_paymentConfirm);

        orderProductKey = getIntent().getExtras().get("productOrderKey").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        databaseReference.keepSynced(true);

        transactionPicture.setVisibility(View.GONE);

        getAllInformation(orderProductKey);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog();
            }
        });

        transactionPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToViewPicActivity(pTransactionId, pTransactionPicture);
            }
        });

        if (deliveryType.equals("Post Box Delivery"))
            postDeliveryButton.setVisibility(View.VISIBLE);
        else
            postDeliveryButton.setVisibility(View.GONE);

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

                    if (dataSnapshot.hasChild("productName")){
                        pProductName = dataSnapshot.child("productName").getValue().toString();
                        productName.setText(pProductName);
                    }

                    if (dataSnapshot.hasChild("productPrice")){
                        String pProductPrice = dataSnapshot.child("productPrice").getValue().toString();
                        productPrice.setText(pProductPrice);
                    }

                    if (dataSnapshot.hasChild("productQuantity")){
                        String pProductQty = dataSnapshot.child("productQuantity").getValue().toString();
                        qtyOrder.setText(pProductQty);
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
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        String pDeliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                        deliveryConfirm.setText(pDeliverySuccess);
                    }

                    if (dataSnapshot.hasChild("totalAmount")){
                        String pTotalAmount = dataSnapshot.child("totalAmount").getValue().toString();
                        totalAmount.setText(pTotalAmount);
                    }

                    if (dataSnapshot.hasChild("productPicture1")){
                        pProductPicture = dataSnapshot.child("productPicture1").getValue().toString();
                        //loading picture offline
                        try{
                            Picasso.get().load(pProductPicture).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.warning).into(productImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(pProductPicture)
                                            .placeholder(R.drawable.warning).into(productImage);
                                }
                            });
                        } catch (Exception ex){
                            System.out.println(ex.getMessage());
                        }
                    }

                    if (dataSnapshot.hasChild("paymentTransactionPicture")){
                        pTransactionPicture = dataSnapshot.child("paymentTransactionPicture").getValue().toString();
                        //loading picture offline
                        try{
                            Picasso.get().load(pTransactionPicture).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.no_image).into(transactionPicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(pTransactionPicture)
                                            .placeholder(R.drawable.no_image).into(transactionPicture);
                                }
                            });
                            transactionPicture.setVisibility(View.VISIBLE);
                        } catch (Exception ex){
                            transactionPicture.setVisibility(View.GONE);
                            System.out.println(ex.getMessage());
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

                    if ((dataSnapshot.hasChild("postBoxNumber"))){
                        zPostNumber = dataSnapshot.child("postBoxNumber").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void viewPictureDialog() {
        TextView close, title;
        ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pProductName);

        Picasso.get().load(pProductPicture).into(picture);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
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
        Intent intent = new Intent(ViewProductOrderDetailsActivity.this, ViewPictureActivity.class);
        intent.putExtra("imageText", picName);
        intent.putExtra("image", picUrl);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
