package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;

import java.util.Locale;
import java.util.Objects;

public class AdvertisementProductActivity extends AppCompatActivity {

    private ImageView productPicture1, productPicture2, productPicture3, productVideo, eventPicture;
    private TextView productName, productPrice, discountPercent, discountPrice,
            productAvaliableQty, dateAndTime, productDescription;
    private RelativeLayout discountLayout;
    private Dialog dialog;
    private SwipeRefreshLayout refreshLayout;
    private ImageView speaker;

    private TextToSpeech phoneSpeak;
    private CatchErrors catchErrors;
    private DateAndTime dateAndTimeError;

    private double pitch = 0.0f, speechRate = 1.0f;

    private String AD_DISCOUNT_PERCENT, AD_DISCOUNT_PRIZE, AD_IS_DISCOUNT = "false", AD_TIME_AND_DATE, AD_PRODUCT_QUANTITY_AVAILABLE,
            AD_EVENT, AD_PRODUCT_DESCRIPTION, pProductPicture1, pProductPicture2, pProductPicture3, pProductName, pProductVideo,
            pProductPrice;

    private String postKey;
    private DatabaseReference databaseReference, advertiseReference;

    private int speakCount = 0;

    @Override
    public void onPause() {
        if (phoneSpeak != null) {
            phoneSpeak.stop();
            phoneSpeak.shutdown();
        }
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advertisement_product);

        Toolbar myToolbar = findViewById(R.id.advertiseProduct_toolbar);
        productPicture1 = findViewById(R.id.advertiseProduct_productPicture1);
        productPicture2 = findViewById(R.id.advertiseProduct_productPicture2);
        productPicture3 = findViewById(R.id.advertiseProduct_productPicture3);
        productVideo = findViewById(R.id.advertiseProduct_video);
        productName = findViewById(R.id.advertiseProduct_productName);
        productPrice = findViewById(R.id.advertiseProduct_productPrice);
        productAvaliableQty = findViewById(R.id.advertiseProduct_productQuantityAvailable);
        productDescription = findViewById(R.id.advertiseProduct_description);
        Button orderProduct = findViewById(R.id.advertiseProduct_order);
        eventPicture = findViewById(R.id.advertiseProduct_event);
        discountPercent = findViewById(R.id.advertiseProduct_discountPercent);
        discountPrice = findViewById(R.id.advertiseProduct_discountPrice);
        dateAndTime = findViewById(R.id.advertiseProduct_dateAndTime);
        discountLayout = findViewById(R.id.advertiseProduct_discountRelative);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        refreshLayout = findViewById(R.id.advertiseProduct_refresh);
        speaker = findViewById(R.id.advertiseProduct_speaker);

        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Promotion! Promotion!! Promotion!!!");

        postKey = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("postKey")).toString();
        if (TextUtils.isEmpty(postKey)) {
            Toast.makeText(this, "Error occurred, please report this to us", Toast.LENGTH_LONG).show();
            finish();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(postKey);
        databaseReference.keepSynced(true);
        advertiseReference = FirebaseDatabase.getInstance().getReference().child("Product Advertisement");
        advertiseReference.keepSynced(true);


        discountPrice.setVisibility(View.GONE);
        discountLayout.setVisibility(View.GONE);
        productPicture2.setVisibility(View.GONE);
        productPicture3.setVisibility(View.GONE);
        productVideo.setVisibility(View.GONE);

        catchErrors = new CatchErrors();
        dateAndTimeError = new DateAndTime();

        try {
            showProductAdvertise();
            displaySelectedProduct();
        } catch (Exception e) {
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTimeError.getDate(), dateAndTimeError.getTime(),
                    "AdvertisementProductActivity", "main method");
        }

        //Phone speak out
        phoneSpeak = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    phoneSpeak.setLanguage(Locale.ENGLISH);
                    phoneSpeak.setPitch((float) pitch);
                    phoneSpeak.setSpeechRate((float) speechRate);
                }
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    showProductAdvertise();
                    displaySelectedProduct();
                    refreshLayout.setRefreshing(false);
                } catch (Exception e) {
                    Log.i("error", e.getMessage());
                    catchErrors.setErrors(e.getMessage(), dateAndTimeError.getDate(), dateAndTimeError.getTime(),
                            "AdvertisementProductActivity", "main method refreshLayout");
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

        productVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(AdvertisementProductActivity.this, ViewVideoActivity.class);
                    intent.putExtra("link", pProductVideo);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.i("error", e.getMessage());
                    catchErrors.setErrors(e.getMessage(), dateAndTimeError.getDate(), dateAndTimeError.getTime(),
                            "AdvertisementProductActivity", "main method productVideo");
                }
            }
        });

        orderProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOnline()) {
                    try {
                        if (AD_IS_DISCOUNT.equalsIgnoreCase("false")) {
                            sendUserToOrderProductActivity(pProductName, pProductPrice, pProductPicture1,
                                    AD_PRODUCT_QUANTITY_AVAILABLE);
                        } else if (AD_IS_DISCOUNT.equals("true"))
                            sendUserToOrderProductActivity(pProductName, "GHC" + AD_DISCOUNT_PRIZE, pProductPicture1,
                                    AD_PRODUCT_QUANTITY_AVAILABLE);
                    } catch (Exception ex) {
                        Log.i("error", ex.getMessage());
                        sendUserToOrderProductActivity(pProductName, pProductPrice, pProductPicture1, AD_PRODUCT_QUANTITY_AVAILABLE);
                        catchErrors.setErrors(ex.getMessage(), dateAndTimeError.getDate(), dateAndTimeError.getTime(),
                                "AdvertisementProductActivity", "main method orderProduct");
                    }
                } else {
                    Toast.makeText(AdvertisementProductActivity.this, "No Internet connection",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

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
        if (phoneSpeak.isSpeaking()) {
            phoneSpeak.stop();
        }
    }

    private void speakOut() {
        speakCount = 1;
        speaker.setImageResource(R.drawable.speaker_not);
        String speak;
        if (AD_IS_DISCOUNT.equals("true")) {
            speak = "Product Name " + pProductName + " Product Description " + AD_PRODUCT_DESCRIPTION +
                    ". This product original cost was " + pProductPrice + " with a discount of " +
                    AD_DISCOUNT_PERCENT + ", new price is now " + AD_DISCOUNT_PRIZE + " Thank you. Promotion end date is " +
                    AD_TIME_AND_DATE;
            phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            speak = "Product Name " + pProductName + " Product Description " + AD_PRODUCT_DESCRIPTION +
                    ". This product cost " + pProductPrice + "Thank you. Promotion end date is " +
                    AD_TIME_AND_DATE;
            phoneSpeak.speak(speak, TextToSpeech.QUEUE_FLUSH, null);
        }

        if (!phoneSpeak.isSpeaking()) {
            speakCount = 0;
            speaker.setImageResource(R.drawable.speaker);
        }
    }

    private void sendUserToOrderProductActivity(String pProductName, String pProductPrice, String pProductPicture1,
                                                String pProductQuantityAvailable) {
        try{
            Intent intent = new Intent(AdvertisementProductActivity.this, OrderProductActivity.class);
            intent.putExtra("ProductName", pProductName);
            intent.putExtra("ProductPrice", pProductPrice);
            intent.putExtra("ProductPicture1", pProductPicture1);
            intent.putExtra("ProductQuantityAvailable", pProductQuantityAvailable);
            intent.putExtra("LimitedQty", "0");
            intent.putExtra("ProductCategory", "Advertisement");
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTimeError.getDate(), dateAndTimeError.getTime(),
                    "AdvertisementProductActivity", "sendUserToOrderProductActivity productVideo");
        }
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void displaySelectedProduct() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("ProductName")) {
                        pProductName = dataSnapshot.child("ProductName").getValue().toString();
                        productName.setText(pProductName);
                    }

                    if (dataSnapshot.hasChild("ProductPrice")) {
                        pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                        productPrice.setText(pProductPrice);
                    }

                    if (dataSnapshot.hasChild("ProductVideo")) {
                        pProductVideo = dataSnapshot.child("ProductVideo").getValue().toString();
                        if (!pProductVideo.isEmpty())
                            productVideo.setVisibility(View.VISIBLE);
                    }
                    if (dataSnapshot.hasChild("ProductPicture1")) {
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
                                                .placeholder(R.drawable.warning).into(productPicture1);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture2")) {
                        pProductPicture2 = dataSnapshot.child("ProductPicture2").getValue().toString();
                        productPicture2.setVisibility(View.VISIBLE);
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
                                                .placeholder(R.drawable.warning).into(productPicture2);
                                    }
                                });
                    }
                    if (dataSnapshot.hasChild("ProductPicture3")) {
                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                        productPicture3.setVisibility(View.VISIBLE);
                        Picasso.get()
                                .load(pProductPicture3)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning).into(productPicture3, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get()
                                        .load(pProductPicture3)
                                        .placeholder(R.drawable.warning).into(productPicture3);
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

    private void showProductAdvertise() {
        advertiseReference.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("productDescription")) {
                        AD_PRODUCT_DESCRIPTION = dataSnapshot.child("productDescription").getValue().toString();
                        productDescription.setText(AD_PRODUCT_DESCRIPTION);
                    }

                    if (dataSnapshot.hasChild("productDiscountPercent")) {
                        AD_DISCOUNT_PERCENT = dataSnapshot.child("productDiscountPercent").getValue().toString();
                        discountPercent.setText(AD_DISCOUNT_PERCENT);
                    }

                    if (dataSnapshot.hasChild("isProductDiscount")) {
                        AD_IS_DISCOUNT = dataSnapshot.child("isProductDiscount").getValue().toString();

                        if (AD_IS_DISCOUNT.equalsIgnoreCase("true")) {
                            if (dataSnapshot.hasChild("productDiscountPrice")) {
                                AD_DISCOUNT_PRIZE = dataSnapshot.child("productDiscountPrice").getValue().toString();
                                discountPrice.setText("GHC" + AD_DISCOUNT_PRIZE);
                                discountPrice.setVisibility(View.VISIBLE);
                                discountLayout.setVisibility(View.VISIBLE);
                                productPrice.setPaintFlags(productPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                        }


                    }

                    if (dataSnapshot.hasChild("productQuantityAvailable")) {
                        AD_PRODUCT_QUANTITY_AVAILABLE = dataSnapshot.child("productQuantityAvailable").getValue().toString();
                        productAvaliableQty.setText(AD_PRODUCT_QUANTITY_AVAILABLE);
                    }

                    if (dataSnapshot.hasChild("advertisementDateAndTime")) {
                        AD_TIME_AND_DATE = dataSnapshot.child("advertisementDateAndTime").getValue().toString();
                        dateAndTime.setText("end on " + AD_TIME_AND_DATE);
                    }

                    if (dataSnapshot.hasChild("advertisementEvent")) {
                        AD_EVENT = dataSnapshot.child("advertisementEvent").getValue().toString();

                        if (AD_EVENT.equalsIgnoreCase("Christmas Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_christmas);
                        else if (AD_EVENT.equalsIgnoreCase("Easter Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_easter);
                        else if (AD_EVENT.equalsIgnoreCase("Black Market Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_black_market);
                        else if (AD_EVENT.equalsIgnoreCase("Price Reduction Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_price_reduction);
                        else if (AD_EVENT.equalsIgnoreCase("Hot Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_hot);
                        else if (AD_EVENT.equalsIgnoreCase("Valentine Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_valentine);
                        else if (AD_EVENT.equalsIgnoreCase("Mother's Day Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_mothers_day);
                        else if (AD_EVENT.equalsIgnoreCase("Father's Day Packages"))
                            eventPicture.setBackgroundResource(R.drawable.packages_fathers_day);
                        else
                            eventPicture.setBackgroundResource(R.drawable.packages_customise);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void viewPictureDialog(final String pictureUrl) {
        try{
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
                    Intent intent = new Intent(AdvertisementProductActivity.this, ViewPictureActivity.class);
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
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTimeError.getDate(), dateAndTimeError.getTime(),
                    "AdvertisementProductActivity", "viewPictureDialog method");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
