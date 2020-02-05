package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.Map;

public class ShowCartDetailsActivity extends AppCompatActivity {
    private Toolbar myToolBar;
    private ImageView delete, productPic1, productPic2, productPic3;
    private TextView productName, productPrice, productQuantity, productDescription, totalAmount;
    private Dialog dialog;
    private ProgressDialog progressDialog;
    private Button order;

    private String cartPostKey, pProductName, pProductPrice, pProductDescription, pProductPicture1,
            pProductPicture2, pProductPicture3, randomName2, cartCounter, pLimitedQty = "", pProductCategory = "";
    private String postKey, pQuantity, pTotalAmount;
    private double allCartTotalAmount = 0;
    private int itemAddedCount = 0;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference, productReference, cartReference;

    private String determineDelete = "yes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart_details);

        cartPostKey = getIntent().getExtras().get("cartPostKey").toString();
        try {
            randomName2 = getIntent().getExtras().get("randomName2").toString();
            determineDelete = getIntent().getExtras().get("delete").toString();
        } catch (Exception ex) {

        }

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Cart Order").child(getCurrentUserId).child(cartPostKey);
        databaseReference.keepSynced(true);
        cartReference = FirebaseDatabase.getInstance().getReference().child("Cart Details");
        cartReference.keepSynced(true);

        myToolBar = findViewById(R.id.showCartDetails_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Cart Details");

        delete = findViewById(R.id.showCartDetails_delete);
        productPic1 = findViewById(R.id.showCartDetails_productPicture1);
        productPic2 = findViewById(R.id.showCartDetails_productPicture2);
        productPic3 = findViewById(R.id.showCartDetails_productPicture3);
        productName = findViewById(R.id.showCartDetails_productName);
        productPrice = findViewById(R.id.showCartDetails_productPrice);
        productQuantity = findViewById(R.id.showCartDetails_yourQuantity);
        productDescription = findViewById(R.id.showCartDetails_productDescription);
        totalAmount = findViewById(R.id.showCartDetails_totalAmount);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        order = findViewById(R.id.showCartDetails_orderOnlyProduct);
        progressDialog = new ProgressDialog(this);

        if (determineDelete.equals("no")) {
            delete.setVisibility(View.GONE);
            order.setVisibility(View.GONE);
        } else {
            delete.setVisibility(View.VISIBLE);
            order.setVisibility(View.VISIBLE);
        }

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

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog();
            }
        });

        showProductDetails();
        getAllCartDetails();

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToOrderActivty();
            }
        });
    }

    private void showProductDetails() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("postKey")) {
                        postKey = dataSnapshot.child("postKey").getValue().toString();
                        displaySelectedProduct();
                    }

                    if (dataSnapshot.hasChild("calAmount")) {
                        pTotalAmount = dataSnapshot.child("calAmount").getValue().toString();
                        totalAmount.setText("GHC" + pTotalAmount);
                    }

                    if (dataSnapshot.hasChild("quantity")) {
                        pQuantity = dataSnapshot.child("quantity").getValue().toString();
                        productQuantity.setText(pQuantity + " unit(s)");
                    } else
                        pQuantity = "0";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showDeleteDialog() {
        TextView message;
        Button yes, no;

        dialog.setContentView(R.layout.dialog_question);
        message = dialog.findViewById(R.id.dialogQuestion_message);
        yes = dialog.findViewById(R.id.dialogQuestion_yes);
        no = dialog.findViewById(R.id.dialogQuestion_no);

        message.setText("Do you want to delete this product");

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCartProduct();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getAllCartDetails() {
        cartReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("totalAmount")) {
                        String tA = dataSnapshot.child("totalAmount").getValue().toString();
                        allCartTotalAmount = Double.valueOf(tA);
                    }

                    if (dataSnapshot.hasChild("countItem")) {
                        String cI = dataSnapshot.child("countItem").getValue().toString();
                        itemAddedCount = Integer.parseInt(cI);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void deleteCartProduct() {
        progressDialog.setMessage("Removing product from your cart...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        double tt = Double.valueOf(pTotalAmount);
        if (allCartTotalAmount > 0 && itemAddedCount > 0) {
            allCartTotalAmount = allCartTotalAmount - tt;
            itemAddedCount = itemAddedCount - 1;
        } else {
            allCartTotalAmount = 0;
            itemAddedCount = 0;
        }

        Map saveMap = new HashMap();
        saveMap.put("totalAmount", allCartTotalAmount);
        saveMap.put("countItem", itemAddedCount);
        cartReference.child(getCurrentUserId).updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    databaseReference.removeValue();
                    DatabaseReference rReference = FirebaseDatabase.getInstance().getReference().child("Cart Order Confirm")
                            .child(getCurrentUserId);
                    try {
                        rReference.child(cartCounter).child(randomName2).removeValue();
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    Toast.makeText(ShowCartDetailsActivity.this, "Product removed successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                } else {
                    String err = task.getException().getMessage();
                    Toast.makeText(ShowCartDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displaySelectedProduct() {
        productReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
        productReference.keepSynced(true);
        productReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("ProductName")) {
                        pProductName = dataSnapshot.child("ProductName").getValue().toString();
                        productName.setText(pProductName);
                    }
                    if (dataSnapshot.hasChild("ProductPrice")) {
                        pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                        if (dataSnapshot.hasChild("ProductDiscount")) {
                            String pDiscount = dataSnapshot.child("ProductDiscount").getValue().toString();
                            if (pDiscount.equals("yes")) {
                                if (dataSnapshot.hasChild("ProductDiscountPrice")) {
                                    String pDiscountPrice = dataSnapshot.child("ProductDiscountPrice").getValue().toString();
                                    if (!TextUtils.isEmpty(pDiscountPrice)) {
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
                    if (dataSnapshot.hasChild("ProductDescription")) {
                        pProductDescription = dataSnapshot.child("ProductDescription").getValue().toString();
                        productDescription.setText(pProductDescription);
                    }
                    if (dataSnapshot.hasChild("ProductPicture1")) {
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
                                                .placeholder(R.drawable.warning)
                                                .into(productPic1);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture2")) {
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
                    if (dataSnapshot.hasChild("ProductPicture3")) {
                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                        productPic3.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture3).fit()
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

                    if (dataSnapshot.hasChild("ProductLimitedQty")) {
                        pLimitedQty = dataSnapshot.child("ProductLimitedQty").getValue().toString();
                    } else
                        pLimitedQty = "0";

                    if (dataSnapshot.hasChild("ProductCategory")) {
                        pProductCategory = dataSnapshot.child("ProductCategory").getValue().toString();
                    } else
                        pProductCategory = "Cart Single Order";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void viewPictureDialog(final String pictureUrl) {
        TextView close, title;
        final ImageView picture;
        dialog.setContentView(R.layout.dialog_view_picture);

        close = dialog.findViewById(R.id.viewPicture_close);
        title = dialog.findViewById(R.id.viewPicture_title);
        picture = dialog.findViewById(R.id.viewPicture_picture);

        title.setText(pProductName);

        //loading picture offline
        Picasso.get().load(pictureUrl).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.no_image).into(picture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(pictureUrl).placeholder(R.drawable.no_image).into(picture);
            }
        });

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowCartDetailsActivity.this, ViewPictureActivity.class);
                intent.putExtra("imageText", pProductName);
                intent.putExtra("image", pictureUrl);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

        if (id == android.R.id.home) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToOrderActivty() {
        Intent intent = new Intent(ShowCartDetailsActivity.this, OrderProductActivity.class);
        intent.putExtra("ProductName", pProductName);
        intent.putExtra("ProductPrice", pProductPrice);
        intent.putExtra("ProductPicture1", pProductPicture1);
        intent.putExtra("ProductQuantityAvailable", pQuantity);
        intent.putExtra("LimitedQty", pLimitedQty);
        intent.putExtra("ProductCategory", pProductCategory);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
