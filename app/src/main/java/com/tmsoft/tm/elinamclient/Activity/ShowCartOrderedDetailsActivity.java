package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
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
import com.tmsoft.tm.elinamclient.R;

public class ShowCartOrderedDetailsActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private ImageView productPic1, productPic2, productPic3;
    private TextView productName, productPrice, productQuantity, productDescription, totalAmount;
    private Dialog dialog;

    private String cartPostKey, pProductName, pProductPrice, pProductDescription, pProductPicture1, pProductPicture2, pProductPicture3;
    private String postKey, category, pQuantity, pTotalAmount, userId, cartOrderCounter;

    private DatabaseReference databaseReference, productReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart_ordered_details);
        cartPostKey = getIntent().getExtras().get("cartPostKey").toString();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        cartOrderCounter = getIntent().getExtras().get("cartOrderCounter").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Order Confirm").child(userId)
                .child(cartOrderCounter).child(cartPostKey);
        databaseReference.keepSynced(true);

        myToolBar = (Toolbar) findViewById(R.id.showCartOrderedDetailss_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Cart Details");

        productPic1 = (ImageView) findViewById(R.id.showCartOrderedDetails_productPicture1);
        productPic2 = (ImageView) findViewById(R.id.showCartOrderedDetails_productPicture2);
        productPic3 = (ImageView) findViewById(R.id.showCartOrderedDetails_productPicture3);
        productName = (TextView) findViewById(R.id.showCartOrderedDetails_productName);
        productPrice = (TextView) findViewById(R.id.showCartOrderedDetails_productPrice);
        productQuantity = (TextView) findViewById(R.id.showCartOrderedDetails_yourQuantity);
        productDescription = (TextView) findViewById(R.id.showCartOrderedDetails_productDescription);
        totalAmount = (TextView) findViewById(R.id.showCartOrderedDetails_totalAmount);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);

        productPic2.setVisibility(View.GONE);
        productPic3.setVisibility(View.GONE);

        productPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture1);
            }
        });

        productPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture2);
            }
        });

        productPic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture3);
            }
        });

        showProductDetails();

    }


    private void showProductDetails() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("postKey")){
                        postKey = dataSnapshot.child("postKey").getValue().toString();

                        if (dataSnapshot.hasChild("category")){
                            category = dataSnapshot.child("category").getValue().toString();
                        }
                        displaySelectedProduct();
                    }


                    if (dataSnapshot.hasChild("calAmount")){
                        pTotalAmount = dataSnapshot.child("calAmount").getValue().toString();
                        totalAmount.setText("GHC" + pTotalAmount);
                    }

                    if (dataSnapshot.hasChild("quantity")){
                        pQuantity = dataSnapshot.child("quantity").getValue().toString();
                        productQuantity.setText(pQuantity + " unit(s)");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displaySelectedProduct() {
        productReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
        productReference.keepSynced(true);

        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("ProductName")){
                        pProductName = dataSnapshot.child("ProductName").getValue().toString();
                        productName.setText(pProductName);
                    }
                    if (dataSnapshot.hasChild("ProductPrice")){
                        pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                        if (dataSnapshot.hasChild("ProductDiscount")){
                            String pDiscount = dataSnapshot.child("ProductDiscount").getValue().toString();
                            if (pDiscount.equals("yes")){
                                if (dataSnapshot.hasChild("ProductDiscountPrice")){
                                    String pDiscountPrice = dataSnapshot.child("ProductDiscountPrice").getValue().toString();
                                    if(!TextUtils.isEmpty(pDiscountPrice)){
                                        productPrice.setText(pDiscountPrice);
                                    }
                                }
                            } else {
                                productPrice.setText(pProductPrice);
                            }
                        } else {
                            productPrice.setText(pProductPrice);
                        }
                    }
                    if (dataSnapshot.hasChild("ProductDescription")){
                        pProductDescription = dataSnapshot.child("ProductDescription").getValue().toString();
                        productDescription.setText(pProductDescription);
                    }
                    if (dataSnapshot.hasChild("ProductPicture1")){
                        pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture1).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPic1, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductPicture1).fit()
                                                .placeholder(R.drawable.warning).into(productPic1);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture2")){
                        pProductPicture2 = dataSnapshot.child("ProductPicture2").getValue().toString();
                        productPic2.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture2).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPic2, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductPicture2).fit()
                                                .placeholder(R.drawable.warning)
                                                .into(productPic2);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture3")){
                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                        productPic3.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture3)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPic3, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductPicture3).fit()
                                                .placeholder(R.drawable.warning)
                                                .into(productPic3);
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void viewPictureDialog(final String pictureUrl){
        TextView close, title;
        final ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pProductName);

        //loading picture offline
        Picasso.get()
                .load(pictureUrl).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image)
                .into(picture, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(pictureUrl).fit()
                                .placeholder(R.drawable.no_image)
                                .into(picture);
                    }
                });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowCartOrderedDetailsActivity.this, ViewPictureActivity.class);
                intent.putExtra("imageText", pProductName);
                intent.putExtra("image", pictureUrl);
                startActivity(intent);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
