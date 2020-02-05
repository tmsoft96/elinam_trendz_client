package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ViewProductDetailsActivity extends AppCompatActivity {

    private ImageView productPicture1, productPicture2, productPicture3, videoView, buttonFav;
    private TextView productName, productPrice, productDescription, productQuantityAvailable, limitedQty, category;
    private Button order, addToCart, comment, showCart;
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private Toolbar myToolBar;
    private SwipeRefreshLayout refresh;
    private ImageView speaker;

    private RelativeLayout discountRelative;
    private TextView discountPercent, discountPrice;
    private ImageView priceConceal;

    private TextToSpeech phoneSpeak;

    private double pitch = 0.0f, speechRate = 1.0f;

    private FirebaseAuth mAuth;
    private String getUserCurrentId;
    private DatabaseReference databaseReference, favoriteDatabaseReference, cartReference,
            cartOrderCounterReference, databaseReference2, cartCounterReference;

    private String postKey, pLimitedQty, isLimitedQty;
    private String pProductName, pProductPrice, pProductDescription, pProductPicture1, pProductPicture2,
            pProductPicture3, pProductQuantityAvailable, pProductCategory = "", pDiscountPercent;
    private String saveCurrentDate, saveCurrentTime, pViewVideo, cartOrderCounter, pDiscount, pDiscountPrice;

    private long counter = 0;
    private double totalAmount = 0;
    private int itemAddedCount = 0;

    private int speakCount = 0;

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
        setContentView(R.layout.activity_view_product_details);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        productPicture1 = findViewById(R.id.viewProduct_productPicture1);
        productPicture2 = findViewById(R.id.viewProduct_productPicture2);
        productPicture3 = findViewById(R.id.viewProduct_productPicture3);
        productName = findViewById(R.id.viewProduct_productName);
        productPrice = findViewById(R.id.viewProduct_productPrice);
        productQuantityAvailable = findViewById(R.id.viewProduct_productQuantityAvailable);
        productDescription = findViewById(R.id.viewProduct_productDescription);
        order = findViewById(R.id.viewProduct_order);
        addToCart = findViewById(R.id.viewProduct_addToList);
        showCart = findViewById(R.id.viewProduct_showCart);
        progressDialog = new ProgressDialog(this);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        comment = findViewById(R.id.viewProduct_comment);
        videoView = findViewById(R.id.viewProduct_video);
        buttonFav = findViewById(R.id.viewProduct_favorite);
        discountRelative = findViewById(R.id.viewProduct_discountRelative);
        discountPercent = findViewById(R.id.viewProduct_discountPercent);
        discountPrice = findViewById(R.id.viewProduct_discountPrice);
        priceConceal = findViewById(R.id.viewProduct_priceConceal);
        limitedQty = findViewById(R.id.viewProduct_limitedQty);
        refresh = findViewById(R.id.viewProduct_refresh);
        speaker = findViewById(R.id.viewProduct_speaker);
        category = findViewById(R.id.viewProduct_productCategory);

        myToolBar = findViewById(R.id.viewProduct_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Details");

       //Phone speak out
       phoneSpeak = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
           @Override
           public void onInit(int i) {
               if(i != TextToSpeech.ERROR){
                   phoneSpeak.setLanguage(Locale.ENGLISH);
                   phoneSpeak.setPitch((float) pitch);
                   phoneSpeak.setSpeechRate((float) speechRate);
               }
           }
       });

        try{
            postKey = getIntent().getExtras().get("postKey").toString();

            mAuth = FirebaseAuth.getInstance();
            getUserCurrentId = mAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
            databaseReference.keepSynced(true);
            favoriteDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Favorites");
            favoriteDatabaseReference.keepSynced(true);
            cartReference = FirebaseDatabase.getInstance().getReference().child("Cart Details");
            cartReference.keepSynced(true);
            cartOrderCounterReference = FirebaseDatabase.getInstance().getReference().child("Cart Order");
            cartOrderCounterReference.keepSynced(true);
            databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Cart Order Confirm");
            databaseReference2.keepSynced(true);
            cartCounterReference = FirebaseDatabase.getInstance().getReference().child("Cart Order Counter");
            cartCounterReference.keepSynced(true);

            productPicture2.setVisibility(View.GONE);
            productPicture3.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
            showCart.setVisibility(View.GONE);
            discountRelative.setVisibility(View.GONE);
            priceConceal.setVisibility(View.GONE);
            discountPrice.setVisibility(View.GONE);
            limitedQty.setVisibility(View.GONE);

            displaySelectedProduct();
            getAllCartDetails();
            showCartCounter();
            showCartOrderCounter();

            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    displaySelectedProduct();
                    getAllCartDetails();
                    showCartCounter();
                    showCartOrderCounter();
                    refresh.setRefreshing(false);
                }
            });



            showCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendUserToShowCartActivity();
                }
            });

            addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnline()){
                        showCartQuantityDialog();
                    } else {
                        Toast.makeText(ViewProductDetailsActivity.this, "No Internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

            buttonFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveFavorite();
                }
            });

            order.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnline()){
                        try{
                            if (pDiscount.equals("no")){
                                sendUserToOrderProductActivity(pProductName, pProductPrice, pProductPicture1, pProductQuantityAvailable,
                                        pLimitedQty, pProductCategory);
                            }
                            else if (pDiscount.equals("yes"))
                                sendUserToOrderProductActivity(pProductName, pDiscountPrice, pProductPicture1, pProductQuantityAvailable,
                                        pLimitedQty, pProductCategory);
                        } catch (Exception ex){
                            sendUserToOrderProductActivity(pProductName, pProductPrice, pProductPicture1, pProductQuantityAvailable,
                                    pLimitedQty, pProductCategory);
                        }
                    } else {
                        Toast.makeText(ViewProductDetailsActivity.this, "No Internet connection",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

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

            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendUserToCommentActivity();
                }
            });

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewProductDetailsActivity.this, ViewVideoActivity.class);
                    intent.putExtra("link", pViewVideo);
                    startActivity(intent);
                }
            });
        } catch (Exception ex){
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (speakCount == 0)
                    speakOut();
                else if (speakCount == 1)
                    speakStop();
            }
        });
    }

    private void speakStop() {
        speakCount = 0;
        speaker.setImageResource(R.drawable.speaker);
        if (phoneSpeak.isSpeaking()){
            phoneSpeak.stop();
        }
    }

    private void speakOut() {
        speakCount = 1;
        speaker.setImageResource(R.drawable.speaker_not);
        String speak;
        if (pDiscount.equals("yes")){
            if (pProductCategory.equals("Bulk Purchase")){
                if (TextUtils.isEmpty(pLimitedQty)){
                    speak = "Product Name " + pProductName + " Product Description " + pProductDescription +
                            ". This product original cost was " + pProductPrice + " with a discount of " +
                            pDiscountPercent + ", new price is now " + pDiscountPrice + " Thank you.";
                    phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    speak = "Product Name " + pProductName + " Product Description " + pProductDescription +
                            ". This product original cost was " + pProductPrice + " with a discount of " +
                            pDiscountPercent + ", new price is now " + pDiscountPrice + " Thank you. Minimum order for this product is "
                            + pLimitedQty;
                    phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                }
            } else {
                speak = "Product Name " + pProductName + " Product Description " + pProductDescription +
                        ". This product original cost was " + pProductPrice + " with a discount of " +
                        pDiscountPercent + ", new price is now " + pDiscountPrice + " Thank you. ";
                phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            if (pProductCategory.equals("Bulk Purchase")){
                if (TextUtils.isEmpty(pLimitedQty)){
                    speak = "Product Name " + pProductName + " Product Description " + pProductDescription +
                            ". This product cost " + pProductPrice + "Thank you.";
                    phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    speak = "Product Name " + pProductName + " Product Description " + pProductDescription +
                            ". This product cost " + pProductPrice + "Thank you.  Minimum order for this product is " + pLimitedQty;
                    phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
                }
            } else {
                speak = "Product Name " + pProductName + " Product Description " + pProductDescription +
                        ". This product cost " + pProductPrice + "Thank you. ";
                phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        if (!phoneSpeak.isSpeaking()){
            speakCount = 0;
            speaker.setImageResource(R.drawable.speaker);
        }
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void showCartQuantityDialog() {
        TextView close;
        final EditText qty;
        final ImageView qtyAdd, qtyMinus;
        Button add;

        dialog.setContentView(R.layout.dialog_cart_quantity);
        close = dialog.findViewById(R.id.dialogCart_close);
        qty = dialog.findViewById(R.id.dialogCart_quantity);
        qtyAdd = dialog.findViewById(R.id.dialogCart_qtyAdd);
        qtyMinus = dialog.findViewById(R.id.dialogCart_qtySub);
        add = dialog.findViewById(R.id.dialogCart_add);

        if (TextUtils.isEmpty(pLimitedQty)){
            isLimitedQty = "no";
            qtyAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qq = qty.getText().toString();
                    int quantity = Integer.parseInt(qq);
                    ++quantity;
                    qty.setText(quantity + "");
                }
            });

            qtyMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String qq = qty.getText().toString();
                    int quantity = Integer.parseInt(qq);
                    --quantity;
                    if (quantity > 0)
                        qty.setText(quantity + "");
                }
            });
        } else {
            int ll = Integer.parseInt(pLimitedQty);
            qtyMinus.setEnabled(false);
            if (ll > 0){
                isLimitedQty = "yes";
                qtyAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String qq = qty.getText().toString();
                        int quantity = Integer.parseInt(qq);
                        ++quantity;
                        qty.setText(quantity + "");
                        qtyMinus.setEnabled(true);
                    }
                });

                qtyMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String qq = qty.getText().toString();
                        int quantity = Integer.parseInt(qq);
                        --quantity;
                        if (quantity > 0)
                            qty.setText(quantity + "");
                    }
                });
            } else{
                qtyMinus.setEnabled(false);
            }
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qq = qty.getText().toString();

                if (TextUtils.isEmpty(qq))
                    Toast.makeText(ViewProductDetailsActivity.this, "Enter quantity you want", Toast.LENGTH_SHORT).show();
                else {
                    int cqty = Integer.parseInt(qq);
                    int cproductQty = Integer.parseInt(pProductQuantityAvailable);
                    if (cqty > cproductQty)
                        Toast.makeText(ViewProductDetailsActivity.this, "Your preferred quantity is more than available quantity", Toast.LENGTH_LONG).show();
                    else
                        saveToCart(qq, isLimitedQty);
                }
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

    private void showCartOrderCounter(){
        cartCounterReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild(getUserCurrentId)){
                        cartOrderCounter = dataSnapshot.child(getUserCurrentId).getValue().toString();
                    } else{
                        cartCounterReference.child(getUserCurrentId).setValue("0");
                        cartOrderCounter = "0";
                    }
                } else{
                    cartCounterReference.child(getUserCurrentId).setValue("0");
                    cartOrderCounter = "0";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showCartCounter(){
        cartOrderCounterReference.child(getUserCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    counter = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void saveToCart(String quantity, String limSaveQty){
        progressDialog.setMessage("Adding cart...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        String actualPrice;

        try{
            if (pDiscount.equals("yes"))
                actualPrice = pDiscountPrice;
            else
                actualPrice = pProductPrice;
        } catch (Exception ex){
            actualPrice = pProductPrice;
        }

        int aa = actualPrice.length();
        String value = actualPrice.substring(3,aa);
        double dValue = Double.valueOf(value);
        double dQty = Double.valueOf(quantity);
        double tAmount = dValue * dQty;
        totalAmount = totalAmount + tAmount;

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        String saveCurrentTime = currentTimeFormat.format(postTime.getTime());

        final String tt = System.currentTimeMillis() + "";

        final String randomName = counter + getUserCurrentId + saveCurrentTime;
        final String randomName2 = counter + tt;

        final Map saveMap = new HashMap();
        saveMap.put("postKey", postKey);
        saveMap.put("userId", getUserCurrentId);
        saveMap.put("calAmount", tAmount);
        saveMap.put("quantity", quantity);
        saveMap.put("limitedQuantity", limSaveQty);
        saveMap.put("counter", counter);
        saveMap.put("cartOrderCounter", cartOrderCounter);
        saveMap.put("cartPostKey", randomName2);

        //cartOrderCounterReference = FirebaseDatabase.getInstance().getReference().child("Cart Order");
        cartOrderCounterReference.child(getUserCurrentId).child(randomName).updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    //databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Cart Order Confirm");
                    databaseReference2.child(getUserCurrentId).child(cartOrderCounter).child(randomName2).updateChildren(saveMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()){
                                        itemAddedCount = itemAddedCount + 1;

                                        Map cartDetails = new HashMap();
                                        cartDetails.put("userId", getUserCurrentId);
                                        cartDetails.put("totalAmount", totalAmount);
                                        cartDetails.put("countItem", itemAddedCount);
                                        cartDetails.put("cartPostKey", randomName2);

                                        cartReference.child(getUserCurrentId).updateChildren(cartDetails).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()){
                                                    String msg = "You added " + itemAddedCount + " product to your cart";
                                                    Toast.makeText(ViewProductDetailsActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                    dialog.dismiss();
                                                }
                                            }
                                        });
                                    }  else{
                                        String err = task.getException().getMessage();
                                        Toast.makeText(ViewProductDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                } else{
                    String err = task.getException().getMessage();
                    Toast.makeText(ViewProductDetailsActivity.this, err, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void getAllCartDetails() {
        cartReference.child(getUserCurrentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("totalAmount")) {
                        String tA = dataSnapshot.child("totalAmount").getValue().toString();
                        totalAmount = Double.valueOf(tA);
                    }

                    if (dataSnapshot.hasChild("countItem")){
                        String cI = dataSnapshot.child("countItem").getValue().toString();
                        itemAddedCount = Integer.parseInt(cI);

                        if (itemAddedCount > 0)
                            showCart.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void saveFavorite() {
        progressDialog.setTitle("Adding to my List");
        progressDialog.setMessage("Please wait patiently while your product is added");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        Calendar postDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDateFormat.format(postDate.getTime());

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTimeFormat.format(postTime.getTime());

        String randomKey = favoriteDatabaseReference.push().getKey();

        HashMap favoriteMap = new HashMap();
        favoriteMap.put("UserId", getUserCurrentId);
        favoriteMap.put("productKey", postKey);
        favoriteMap.put("favoriteKey", randomKey);

        favoriteDatabaseReference.child(randomKey).setValue(favoriteMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    buttonFav.setEnabled(false);
                    Toast.makeText(ViewProductDetailsActivity.this, "Product added to my list successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(ViewProductDetailsActivity.this, "Failed to add product \n" + errorMessage + "\n Please try again .....", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
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
                    if (dataSnapshot.hasChild("ProductPicture1")){
                        pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture1)
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
                                .load(pProductPicture2)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture2, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(pProductPicture2)
                                                .placeholder(R.drawable.warning)
                                                .into(productPicture2);
                                    }
                                });
                    }

                    if (dataSnapshot.hasChild("ProductCategory")){
                        pProductCategory = dataSnapshot.child("ProductCategory").getValue().toString();
                        category.setText(pProductCategory);
                    }

                    if (dataSnapshot.hasChild("ProductPicture3")){
                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                        productPicture3.setVisibility(View.VISIBLE);
                        //loading picture offline
                        Picasso.get()
                                .load(pProductPicture3)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(productPicture3, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture3)
                                        .placeholder(R.drawable.warning)
                                        .into(productPicture3);
                            }
                        });
                    }
                    if (dataSnapshot.hasChild("ProductQuantity")){
                        pProductQuantityAvailable = dataSnapshot.child("ProductQuantity").getValue().toString();
                        productQuantityAvailable.setText(pProductQuantityAvailable);
                    }

                    if (dataSnapshot.hasChild("ProductVideo")){
                        pViewVideo = dataSnapshot.child("ProductVideo").getValue().toString();
                        if (!pViewVideo.isEmpty())
                            videoView.setVisibility(View.VISIBLE);
                    }

                    if (dataSnapshot.hasChild("ProductDiscount")){
                        pDiscount = dataSnapshot.child("ProductDiscount").getValue().toString();
                        if (pDiscount.equals("yes")){
                            if (dataSnapshot.hasChild("ProductDiscountPercentage")){
                                pDiscountPercent = dataSnapshot.child("ProductDiscountPercentage").getValue().toString();
                                if(!TextUtils.isEmpty(pDiscountPercent)){
                                    discountPercent.setText(pDiscountPercent);
                                    discountRelative.setVisibility(View.VISIBLE);
                                    discountPercent.setVisibility(View.VISIBLE);
                                }
                            }

                            if (dataSnapshot.hasChild("ProductDiscountPrice")){
                                pDiscountPrice = dataSnapshot.child("ProductDiscountPrice").getValue().toString();
                                if(!TextUtils.isEmpty(pDiscountPrice)){
                                    discountPrice.setText(pDiscountPrice);
                                    discountPrice.setVisibility(View.VISIBLE);
                                    productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                }
                            }
                        }
                    }

                    if (dataSnapshot.hasChild("ProductLimitedQty")){
                        pLimitedQty = dataSnapshot.child("ProductLimitedQty").getValue().toString();
                        if (!TextUtils.isEmpty(pLimitedQty)){
                            String qq = "Minimum quantity order is " + pLimitedQty;
                            limitedQty.setText(qq);
                            limitedQty.setVisibility(View.VISIBLE);
                        }
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
                Intent intent = new Intent(ViewProductDetailsActivity.this, ViewPictureActivity.class);
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

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToCommentActivity(){
        Intent intent = new Intent(ViewProductDetailsActivity.this, CommentActivity.class);
        intent.putExtra("postKey", postKey);
        intent.putExtra("productName", pProductName);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToOrderProductActivity(String pProductName, String pProductPrice, String pProductPicture1,
                                                String pProductQuantityAvailable, String ppLimitedQty, String pProductCategory) {
        Intent intent = new Intent(ViewProductDetailsActivity.this, OrderProductActivity.class);
        intent.putExtra("ProductName", pProductName);
        intent.putExtra("ProductPrice", pProductPrice);
        intent.putExtra("ProductPicture1", pProductPicture1);
        intent.putExtra("ProductQuantityAvailable", pProductQuantityAvailable);
        intent.putExtra("LimitedQty", ppLimitedQty);
        intent.putExtra("ProductCategory", pProductCategory);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToShowCartActivity(){
        Intent intent = new Intent(ViewProductDetailsActivity.this, ShowCartActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
