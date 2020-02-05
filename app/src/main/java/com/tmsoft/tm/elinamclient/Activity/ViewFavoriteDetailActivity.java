package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class ViewFavoriteDetailActivity extends AppCompatActivity {

    private ImageView productPicture1, productPicture2, productPicture3;
    private TextView productName, productPrice, productDescription;
    private Dialog dialog;
    private ImageButton delete;
    private Toolbar myToolBar;
    private Button order;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private String postKey;
    private String pProductName, pProductPrice, pProductDescription, pProductPicture1, pProductPicture2,
            pProductPicture3, pProductQuantityAvailable, pLimitedQty = "", pProductCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorite_detail);

        myToolBar = findViewById(R.id.viewFavorite_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        productPicture1 = findViewById(R.id.viewFavorite_productPicture1);
        productPicture2 = findViewById(R.id.viewFavorite_productPicture2);
        productPicture3 = findViewById(R.id.viewFavorite_productPicture3);
        productName = findViewById(R.id.viewFavorite_productName);
        productPrice = findViewById(R.id.viewFavorite_productPrice);
        productDescription = findViewById(R.id.viewFavorite_productDescription);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        delete = findViewById(R.id.viewFavorite_delete);
        order = findViewById(R.id.viewFavorite_order);

        postKey = getIntent().getExtras().get("postKey").toString();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
        databaseReference.keepSynced(true);

        productPicture2.setVisibility(View.GONE);
        productPicture3.setVisibility(View.GONE);

        displaySelectedProduct();

        productPicture1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture1);
            }
        });

        productPicture2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture2);
            }
        });

        productPicture3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPictureDialog(pProductPicture3);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCurrentProduct();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToOrderActivity();
            }
        });
    }

    private void sendUserToOrderActivity() {
        Intent intent = new Intent(ViewFavoriteDetailActivity.this, OrderProductActivity.class);
        intent.putExtra("ProductName", pProductName);
        intent.putExtra("ProductPrice", pProductPrice);
        intent.putExtra("ProductPicture1", pProductPicture1);
        intent.putExtra("ProductQuantityAvailable", pProductQuantityAvailable);
        intent.putExtra("LimitedQty", pLimitedQty);
        intent.putExtra("ProductCategory", pProductCategory);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void displaySelectedProduct() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("ProductName")){
                        pProductName = dataSnapshot.child("ProductName").getValue().toString();
                        productName.setText(pProductName);
                    }
                    if (dataSnapshot.hasChild("ProductPrice")){
                        pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                        productPrice.setText(pProductPrice);
                    }
                    if (dataSnapshot.hasChild("ProductDescription")){
                        pProductDescription = dataSnapshot.child("ProductDescription").getValue().toString();
                        productDescription.setText(pProductDescription);
                    }
                    if (dataSnapshot.hasChild("ProductQuantity")){
                        pProductQuantityAvailable = dataSnapshot.child("ProductQuantity").getValue().toString();
                    }
                    if (dataSnapshot.hasChild("ProductPicture1")){
                        pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture1)
                                .fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture1, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductPicture1)
                                                .fit()
                                                .placeholder(R.drawable.warning)
                                                .into(productPicture1);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture2")){
                        pProductPicture2 = dataSnapshot.child("ProductPicture2").getValue().toString();
                        productPicture2.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture2).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture2, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductPicture2).fit()
                                                .placeholder(R.drawable.warning)
                                                .into(productPicture2);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture3")){
                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                        productPicture3.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture3).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture3, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture3).fit()
                                        .placeholder(R.drawable.warning)
                                        .into(productPicture3);
                            }
                        });
                    }

                    if (dataSnapshot.hasChild("ProductLimitedQty")){
                        pLimitedQty = dataSnapshot.child("ProductLimitedQty").getValue().toString();
                        if (!TextUtils.isEmpty(pLimitedQty)){
                            String qq = "Minimum quantity order is " + pLimitedQty;
                            Toast.makeText(ViewFavoriteDetailActivity.this, qq, Toast.LENGTH_LONG).show();
                        }
                    } else
                        pLimitedQty = "0";

                    if (dataSnapshot.hasChild("ProductCategory")){
                        pProductCategory = dataSnapshot.child("ProductCategory").getValue().toString();
                    } else
                        pProductCategory = "My Favorites";
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
        Picasso.get().load(pictureUrl).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(picture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e){
                Picasso.get().load(pictureUrl)
                        .placeholder(R.drawable.warning).into(picture);
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFavoriteDetailActivity.this, ViewPictureActivity.class);
                intent.putExtra("imageText", pProductName);
                intent.putExtra("image", pictureUrl);
                startActivity(intent);
            }
        });

        dialog.show();
    }

    private void deleteCurrentProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewFavoriteDetailActivity.this);
        builder.setTitle("Confirm Product");
        builder.setMessage("Are you sure you want to remove this product from your list");

        //two button
        //Update button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.removeValue();
                finish();
                Toast.makeText(ViewFavoriteDetailActivity.this, "Product Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        //cancel button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
