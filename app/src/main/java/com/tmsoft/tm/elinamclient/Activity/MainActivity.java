package com.tmsoft.tm.elinamclient.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.rerlanggas.lib.ExceptionHandler;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.Holders.MainPageMarginClass;
import com.tmsoft.tm.elinamclient.R;
import com.tmsoft.tm.elinamclient.Urls.ServerInputDetails;
import com.tmsoft.tm.elinamclient.Urls.SocialMediaUrl;
import com.tmsoft.tm.elinamclient.fragment.HomeProfileFragment;
import com.tmsoft.tm.elinamclient.fragment.MainHomeFragment;
import com.tmsoft.tm.elinamclient.fragment.MainOrderFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST = 0;
    private DatabaseReference databaseReference, onlineReference, tokenRef, agreementReference, advertisementReference;
    private FirebaseAuth mAuth;
    private String getCurrentUserId;

    private RelativeLayout relativeLayout;
    private Toolbar myToolBar;
    private FragmentManager fragmentManager;
    private Dialog dialog;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView profilePicture;
    private ImageView facebook, twitter, telegram,whatsApp, gmail, search, productPic1, productPic2, productPic3;
    private FloatingActionButton hideIcon, hideIcon2;
    private TextView details;
    private BottomNavigationView bottomNavigationView;
    private ViewFlipper productPictureFlipper;
    private TextView notificationCounter;

    private String uProfilePicture;

    private long totalAd = 0;
    private String adSlotnumber, adAdvertiseId, adProductDescription, adProductDiscountPrice, adProductDiscountPercent,
            adIsProductDiscount = "false", adProductQuantityAvailable, adAdvertiseDateAndTime, adAdvertisementEvent;

    private String pProductName, pProductPrice, pProductPicture1, pProductPicture2, pProductPicture3, key;
    private boolean determine = true, hide = true, hide2 = false;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;
    private SocialMediaUrl socialMediaUrl;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //catching error
        ExceptionHandler.init(this, MainActivity.class);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        fragmentManager = getSupportFragmentManager();

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        DatabaseReference pricedatabaseRef = FirebaseDatabase.getInstance().getReference().child("Products");
        pricedatabaseRef.keepSynced(true);
        DatabaseReference contactReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        contactReference.keepSynced(true);
        onlineReference = FirebaseDatabase.getInstance().getReference().child("OnlineUser");
        onlineReference.keepSynced(true);
        tokenRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken").child(getCurrentUserId);
        tokenRef.keepSynced(true);
        agreementReference = FirebaseDatabase.getInstance().getReference().child("AgreementTerms").child(getCurrentUserId);
        agreementReference.keepSynced(true);
        advertisementReference = FirebaseDatabase.getInstance().getReference().child("Product Advertisement");
        advertisementReference.keepSynced(true);

        relativeLayout = findViewById(R.id.main_relativeLayout);
        myToolBar = findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();
        socialMediaUrl = new SocialMediaUrl();

        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        navigationView = (NavigationView) findViewById(R.id.main_slideNavigation);

        View navView = navigationView.inflateHeaderView(R.layout.navigation_slide_header);
        profilePicture = (CircleImageView) navView.findViewById(R.id.nav_profilePicture);
        facebook = (ImageView) navView.findViewById(R.id.nav_facebook);
        twitter = (ImageView) navView.findViewById(R.id.nav_twitter);
        telegram = (ImageView) navView.findViewById(R.id.nav_telegram);
        whatsApp = (ImageView) navView.findViewById(R.id.nav_whatsApp);
        gmail = (ImageView) navView.findViewById(R.id.nav_gmail);
        details = (TextView) navView.findViewById(R.id.nav_details);
        search = (ImageView) findViewById(R.id.main_search);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawerLayout);
        hideIcon = (FloatingActionButton) findViewById(R.id.main_hideIcon);
        hideIcon2 = (FloatingActionButton) findViewById(R.id.main_hideIcon2);
        notificationCounter = (TextView) findViewById(R.id.notification_count);

        gmail.setVisibility(View.GONE);

        search.setVisibility(View.GONE);

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        try{
            String order = getIntent().getExtras().get("postKey").toString();
            if (order.equalsIgnoreCase("order"))
                bottomNavigationView.setSelectedItemId(R.id.navigation_orders);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }

        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final MainPageMarginClass mainPageMarginClass = new MainPageMarginClass(this);

        int visible = bottomNavigationView.getVisibility();
        if (visible == 0){
            setMargins(relativeLayout,mainPageMarginClass.getNotHide());
        } else {
            setMargins(relativeLayout,mainPageMarginClass.getHide());
        }

        try{
            closeKeyBoard();
            checkForManifestPermission();
            checkTermsAndAgreement();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "MainActivity", "main method");
        }

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                MenuSelected(menuItem);
                return false;
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        notificationCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToPublicNoti();
            }
        });

        hideIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hide){
                    myToolBar.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    notificationCounter.setVisibility(View.GONE);
                    setMargins(relativeLayout, mainPageMarginClass.getHide());
                    hide = false;
                } else {
                    myToolBar.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    notificationCounter.setVisibility(View.VISIBLE);
                    setMargins(relativeLayout,mainPageMarginClass.getNotHide());
                    hide = true;
                }
            }
        });

        hideIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hide2){
                    myToolBar.setVisibility(View.GONE);
                    bottomNavigationView.setVisibility(View.GONE);
                    setMargins(relativeLayout, mainPageMarginClass.getHide());
                    hide2 = false;
                } else {
                    myToolBar.setVisibility(View.VISIBLE);
                    bottomNavigationView.setVisibility(View.VISIBLE);
                    setMargins(relativeLayout,mainPageMarginClass.getNotHide());
                    hide2 = true;
                }
            }
        });
    }

    private void checkForManifestPermission() {
        ArrayList<String> arrPerm = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED){
            arrPerm.add(Manifest.permission.READ_PHONE_STATE);
        }

        if(!arrPerm.isEmpty()){
            String[] permissions = new String[arrPerm.size()];
            permissions = arrPerm.toArray(permissions);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, MY_PERMISSIONS_REQUEST);
        }
    }



    private void setMargins (View view, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(0, 0, 0, bottom);
            view.requestLayout();
        }
    }

    private void getAdvertisement() {
        advertisementReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    totalAd = dataSnapshot.getChildrenCount();
                    if (totalAd > 0){
                        adSlotnumber = "1";
                        showAllAdvertisementInfo( "1");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAllAdvertisementInfo(String slot){
        Query query = advertisementReference.orderByChild("advertisementSlot")
                .startAt(slot).endAt(slot + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        key = child.getKey();
                        Log.i("advertise", key);
                        break;
                    }

                    new Thread(){
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(key))
                                getAd(key);
                        }
                    }.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAd(String key){
        advertisementReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.i("advertise", "yes");
                    if (dataSnapshot.hasChild("advertisementSlot")){
                        adSlotnumber = dataSnapshot.child("advertisementSlot").getValue().toString();
                        Log.i("advertise", adSlotnumber);
                    }

                    if (dataSnapshot.hasChild("advertiseId")){
                        adAdvertiseId = dataSnapshot.child("advertiseId").getValue().toString();
                        Log.i("advertise", adAdvertiseId);

                        DatabaseReference productReference = FirebaseDatabase.getInstance().getReference()
                                .child("Products").child(adAdvertiseId);
                        databaseReference.keepSynced(true);
                        productReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("ProductName")){
                                        pProductName = dataSnapshot.child("ProductName").getValue().toString();
                                        //Log.i("advertise", pProductName);
                                    }
                                    if (dataSnapshot.hasChild("ProductPrice")){
                                        pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                                        //Log.i("advertise", pProductPrice);
                                    }
                                    if (dataSnapshot.hasChild("ProductPicture1")){
                                        pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                                        //Log.i("advertise", pProductPicture1);
                                    }
                                    if (dataSnapshot.hasChild("ProductPicture2")){
                                        pProductPicture2 = dataSnapshot.child("ProductPicture2").getValue().toString();
                                        //Log.i("advertise", pProductPicture2);
                                    }
                                    if (dataSnapshot.hasChild("ProductPicture3")){
                                        pProductPicture3 = dataSnapshot.child("ProductPicture3").getValue().toString();
                                        //Log.i("advertise", pProductPicture3);
                                    }

                                    if (determine){
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAdvertisementDialog();
                                            }
                                        }, 4000);
                                    } else{
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showAdvertisementDialog();
                                            }
                                        }, 700);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    if (dataSnapshot.hasChild("productDescription")){
                        adProductDescription = dataSnapshot.child("productDescription").getValue().toString();
                        //Log.i("advertise", adProductDescription);
                    }

                    if (dataSnapshot.hasChild("productDiscountPrice")){
                        adProductDiscountPrice = dataSnapshot.child("productDiscountPrice").getValue().toString();
                        //Log.i("advertise", adProductDiscountPrice);
                    }

                    if (dataSnapshot.hasChild("productDiscountPercent")){
                        adProductDiscountPercent = dataSnapshot.child("productDiscountPercent").getValue().toString();
                        //Log.i("advertise", adSlotnumber);
                    }

                    if (dataSnapshot.hasChild("isProductDiscount")){
                        adIsProductDiscount = dataSnapshot.child("isProductDiscount").getValue().toString();
                        if (!adIsProductDiscount.equalsIgnoreCase("true"))
                            adIsProductDiscount = "false";
                        //Log.i("advertise", adIsProductDiscount);
                    } else
                        adIsProductDiscount = "false";

                    if (dataSnapshot.hasChild("productQuantityAvailable")){
                        adProductQuantityAvailable = dataSnapshot.child("productQuantityAvailable").getValue().toString();
                        //Log.i("advertise", adProductQuantityAvailable);
                    }

                    if (dataSnapshot.hasChild("advertisementDateAndTime")){
                        adAdvertiseDateAndTime = dataSnapshot.child("advertisementDateAndTime").getValue().toString();
                        //Log.i("advertise", adAdvertiseDateAndTime);
                    }

                    if (dataSnapshot.hasChild("advertisementEvent")){
                        adAdvertisementEvent = dataSnapshot.child("advertisementEvent").getValue().toString();
                        //Log.i("advertise", adAdvertisementEvent);
                    }

                    //showAdvertisementDialog();
                } else
                    Log.i("advertise", "no");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private int first;
    private int last;
    private void showAdvertisementDialog() {
        TextView slotNumber, discountPercent, initialPrice, discountPrice, productName, close, dateAndTime;
        final ImageView eventPicture, productPicture, previous, next, priceStroke;
        RelativeLayout main;

        dialog.setContentView(R.layout.dialog_advertise_product);
        dialog.setCanceledOnTouchOutside(true);
        slotNumber = (TextView) dialog.findViewById(R.id.dialogAdvertise_advertisementNumber);
        discountPercent = (TextView) dialog.findViewById(R.id.dialogAdvertise_discountPercent);
        initialPrice = (TextView) dialog.findViewById(R.id.dialogAdvertise_initialPrice);
        discountPrice = (TextView) dialog.findViewById(R.id.dialogAdvertise_discountPrice);
        productName = (TextView) dialog.findViewById(R.id.dialogAdvertise_productName);
        close = (TextView) dialog.findViewById(R.id.dialogAdvertise_close);
        dateAndTime = (TextView) dialog.findViewById(R.id.dialogAdvertise_advertisementDateAndTime);
        eventPicture = (ImageView) dialog.findViewById(R.id.dialogAdvertise_eventPicture);
        productPicture = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture);
        previous = (ImageView) dialog.findViewById(R.id.dialogAdvertise_previous);
        next = (ImageView) dialog.findViewById(R.id.dialogAdvertise_next);
        productPic1 = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture1);
        productPic2 = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture2);
        productPic3 = (ImageView) dialog.findViewById(R.id.dialogAdvertise_productPicture3);
        priceStroke = (ImageView) dialog.findViewById(R.id.dialogAdvertise_priceStroke);
        productPictureFlipper = (ViewFlipper) dialog.findViewById(R.id.dialogAdvertise_productPictureFlipper);
        main = (RelativeLayout) dialog.findViewById(R.id.dialogAdvertise_main);

        previous.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        priceStroke.setVisibility(View.GONE);
        discountPrice.setVisibility(View.GONE);
        discountPercent.setVisibility(View.GONE);
        productPictureFlipper.setVisibility(View.GONE);
        productPic1.setVisibility(View.VISIBLE);
        productPic2.setVisibility(View.GONE);
        productPic3.setVisibility(View.GONE);

        first = Integer.parseInt(adSlotnumber);
        last = (int) totalAd;

        if (first > 0 && first <= last)
            next.setVisibility(View.VISIBLE);
        else
            next.setVisibility(View.GONE);

        if (first > 1 && first <= last)
            previous.setVisibility(View.VISIBLE);
        else
            previous.setVisibility(View.GONE);

        if (first == last)
            next.setVisibility(View.GONE);


        adSlotnumber = adSlotnumber + " - " + totalAd;

        slotNumber.setText(adSlotnumber);
        discountPercent.setText(adProductDiscountPercent);
        initialPrice.setText(pProductPrice);
        discountPrice.setText("GHC" + adProductDiscountPrice);
        productName.setText(pProductName);
        dateAndTime.setText("end on : " + adAdvertiseDateAndTime);
        //loading picture offline
        Picasso.get()
                .load(pProductPicture1)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(productPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture1)
                        .placeholder(R.drawable.warning).into(productPicture);
            }
        });

        if (adIsProductDiscount.equals("true")){
            discountPercent.setVisibility(View.VISIBLE);
            priceStroke.setVisibility(View.VISIBLE);
            discountPrice.setVisibility(View.VISIBLE);
        } else {
            priceStroke.setVisibility(View.GONE);
            discountPrice.setVisibility(View.GONE);
            discountPercent.setVisibility(View.GONE);
        }


        productPic1.setBackgroundResource(R.drawable.check);
        productPic2.setBackgroundResource(R.drawable.uncheck);
        productPic3.setBackgroundResource(R.drawable.uncheck);

        if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && TextUtils.isEmpty(pProductPicture3)){
            productPictureFlipper.setVisibility(View.VISIBLE);
            productPic1.setVisibility(View.VISIBLE);
            productPic2.setVisibility(View.VISIBLE);
            productPicture.setVisibility(View.GONE);

            final String[] images = {pProductPicture1, pProductPicture2};
            for (String image : images){
                flipImages(image);
            }
        } else if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && !TextUtils.isEmpty(pProductPicture3)){
            productPictureFlipper.setVisibility(View.VISIBLE);
            productPic1.setVisibility(View.VISIBLE);
            productPic2.setVisibility(View.VISIBLE);
            productPic3.setVisibility(View.VISIBLE);
            productPicture.setVisibility(View.GONE);

            final String[] images = {pProductPicture1, pProductPicture2, pProductPicture3};
            for (String image : images){
                flipImages(image);
            }
        }

        setSmallImages();

        if (adAdvertisementEvent.equalsIgnoreCase("Christmas Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_christmas);
        else if (adAdvertisementEvent.equalsIgnoreCase("Easter Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_easter);
        else if (adAdvertisementEvent.equalsIgnoreCase("Black Market Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_black_market);
        else if (adAdvertisementEvent.equalsIgnoreCase("Price Reduction Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_price_reduction);
        else if (adAdvertisementEvent.equalsIgnoreCase("Hot Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_hot);
        else if (adAdvertisementEvent.equalsIgnoreCase("Valentine Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_valentine);
        else if (adAdvertisementEvent.equalsIgnoreCase("Mother's Day Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_mothers_day);
        else if (adAdvertisementEvent.equalsIgnoreCase("Father's Day Packages"))
            eventPicture.setBackgroundResource(R.drawable.packages_fathers_day);
        else
            eventPicture.setBackgroundResource(R.drawable.packages_customise);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        productPic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipOneImage(pProductPicture1);
                productPic1.setBackgroundResource(R.drawable.check);
                productPic2.setBackgroundResource(R.drawable.uncheck);
                productPic3.setBackgroundResource(R.drawable.uncheck);
            }
        });

        productPic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipOneImage(pProductPicture2);
                productPic1.setBackgroundResource(R.drawable.uncheck);
                productPic2.setBackgroundResource(R.drawable.check);
                productPic3.setBackgroundResource(R.drawable.uncheck);
            }
        });

        productPic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flipOneImage(pProductPicture3);
                productPic1.setBackgroundResource(R.drawable.uncheck);
                productPic2.setBackgroundResource(R.drawable.uncheck);
                productPic3.setBackgroundResource(R.drawable.check);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (first < last){
                    ++first;
                    determine = false;
                    showAllAdvertisementInfo(first + "");
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                --first;
                determine = false;
                showAllAdvertisementInfo(first + "");
            }
        });

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Intent intent = new Intent(MainActivity.this, AdvertisementProductActivity.class);
                    intent.putExtra("postKey", adAdvertiseId);
                    startActivity(intent);
                } catch (Exception e){
                    Log.i("error", e.getMessage());
                }
            }
        });

        dialog.show();
    }

    private void setSmallImages() {
        new Thread(){
            @Override
            public void run() {
                int num = 1;
                while (true){
                    if (!TextUtils.isEmpty(pProductPicture1) && num == 1){
                        try {
                            productPic1.setBackgroundResource(R.drawable.check);
                            productPic2.setBackgroundResource(R.drawable.uncheck);
                            productPic3.setBackgroundResource(R.drawable.uncheck);
                            Thread.sleep(5000);
                            if (!TextUtils.isEmpty(pProductPicture2))
                                num = 2;
                            else
                                num = 1;
                        } catch (Exception e) {
                            Log.i("error" , e.getMessage());
                        }
                    } else if (!TextUtils.isEmpty(pProductPicture2) && num == 2){
                        try {
                            productPic1.setBackgroundResource(R.drawable.uncheck);
                            productPic2.setBackgroundResource(R.drawable.check);
                            productPic3.setBackgroundResource(R.drawable.uncheck);
                            Thread.sleep(5000);
                            if (!TextUtils.isEmpty(pProductPicture3))
                                num = 3;
                            else
                                num = 2;
                        } catch (Exception e) {
                            Log.i("error" , e.getMessage());
                        }
                    } else if (!TextUtils.isEmpty(pProductPicture3) && num == 3){
                        try {
                            productPic1.setBackgroundResource(R.drawable.uncheck);
                            productPic2.setBackgroundResource(R.drawable.uncheck);
                            productPic3.setBackgroundResource(R.drawable.check);
                            Thread.sleep(5000);
                            num = 1;
                        } catch (Exception e) {
                            Log.i("error" , e.getMessage());
                        }
                    } else {
                        productPic1.setBackgroundResource(R.drawable.check);
                        productPic2.setBackgroundResource(R.drawable.uncheck);
                        productPic3.setBackgroundResource(R.drawable.uncheck);
                    }
                }
            }
        }.start();
    }

    private void flipImages(final String image){
        final ImageView imageView = new ImageView(MainActivity.this);
        try{
            //loading picture offline
            Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(image).fit()
                                    .placeholder(R.drawable.warning).into(imageView);
                        }
                    });

            productPictureFlipper.addView(imageView);
            productPictureFlipper.setFlipInterval(5000);//5 sec
            productPictureFlipper.setAutoStart(true);

            //animation
            productPictureFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            productPictureFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        } catch (Exception e){
            Log.i("error", e.getMessage());
        }
    }

    private void flipOneImage(final String image){
        productPictureFlipper.removeAllViews();
        final ImageView imageView = new ImageView(MainActivity.this);
        try{
            //loading picture offline
            Picasso.get()
                    .load(image)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(image).fit()
                                    .placeholder(R.drawable.warning).into(imageView);
                        }
                    });

            productPictureFlipper.addView(imageView);
            productPictureFlipper.setFlipInterval(5000);//5 sec


            if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && TextUtils.isEmpty(pProductPicture3)){
                productPictureFlipper.removeAllViews();
                final String[] images = {pProductPicture1, pProductPicture2};
                for (String img : images){
                    flipImages(img);
                }
            } else if (!TextUtils.isEmpty(pProductPicture1) && !TextUtils.isEmpty(pProductPicture2) && !TextUtils.isEmpty(pProductPicture3)){
                productPictureFlipper.removeAllViews();
                final String[] images = {pProductPicture1, pProductPicture2, pProductPicture3};
                for (String img : images){
                    flipImages(img);
                }
            }

            setSmallImages();

            //animation
            productPictureFlipper.setInAnimation(this, android.R.anim.slide_in_left);
            productPictureFlipper.setOutAnimation(this, android.R.anim.slide_out_right);
        } catch (Exception e){
            Log.i("error", e.getMessage());
        }
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void checkTermsAndAgreement() {
        try{
            agreementReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        if (dataSnapshot.hasChild("terms")){
                            String terms = dataSnapshot.child("terms").getValue().toString();
                            if (terms.equalsIgnoreCase("yes")){
                                Log.i("termsAgreement", "yes");
                                showDetails();
                                initializeSocialMedia();
                                saveUserOnlineStatus();
                                getAdvertisement();
                                getNotificationCounter();

                                String DeviceToken = FirebaseInstanceId.getInstance().getToken();
                                tokenRef.child("device_token").setValue(DeviceToken);
                            } else{
                                Log.i("termsAgreement", "no");
                                showTermsAndAgreementDialog();
                            }
                        }
                    } else {
                        showTermsAndAgreementDialog();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "MainActivity", "checkTermsAndAgreement method");
        }
    }

    private void getNotificationCounter() {
        notificationCounter.setVisibility(View.GONE);
        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId);
        dReference.keepSynced(true);
        Query query = dReference.orderByChild("read").startAt("no").endAt("no" + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    long count = dataSnapshot.getChildrenCount();
                    if (count < 1)
                        notificationCounter.setVisibility(View.GONE);
                    else{
                        if (count >= 1000 && count <= 999999){
                            long orgCount = count / 1000;
                            notificationCounter.setText(orgCount + "K");
                            notificationCounter.setVisibility(View.VISIBLE);
                        } else if (count >= 1000000){
                            long orgCount = count / 1000000;
                            notificationCounter.setText(orgCount + "M");
                            notificationCounter.setVisibility(View.VISIBLE);
                        } else {
                            notificationCounter.setText(count + "");
                            notificationCounter.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showTermsAndAgreementDialog() {
        Button accept;
        TextView text;

        dialog.setContentView(R.layout.dialog_term_and_agreement);
        accept = dialog.findViewById(R.id.dialogTerms_accept);
        text = dialog.findViewById(R.id.dialogTerms_text);

        try {
            InputStream stream = getAssets().open("terms.txt");

            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            String tContents = new String(buffer);
            text.setText(tContents);
        } catch (IOException e) {
            Log.i("Error", e.getMessage());
        }

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agreementReference.child("terms").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                        } else {
                            String err = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    private void saveUserOnlineStatus() {
        onlineReference.child("clientStatus").child(getCurrentUserId).setValue("offline").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    String err = task.getException().getMessage();
                    Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeSocialMedia() {
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getFacebook());
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getTwitter());
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getTelegram());
            }
        });

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getWhatsapp());
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerInputDetails serverInputDetails = new ServerInputDetails();
                getUri(serverInputDetails.getEmailAddress());
            }
        });
    }

    private void getUri(String s) {
        Uri uri = Uri.parse(s);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void MenuSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_cart :
                sendUserToCartActivity();
                break;

            case R.id.nav_chat :
                sendUserToChatActivity();
                break;

            case R.id.nav_policy :
                sendUserToOurPolicy();
                break;
                
            case R.id.nav_rate :
                Toast.makeText(this, "rate this app", Toast.LENGTH_SHORT).show();
                break;
                
            case R.id.nave_share :
                shareApp();
                break;
        }
    }

    private void shareApp() {
        DatabaseReference deReference = FirebaseDatabase.getInstance().getReference().child("Contact Details");
        deReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("AppLink")){
                        String link = dataSnapshot.child("AppLink").getValue().toString();
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        String shareBody = link;
                        String shareSub = "Elinam Trendz";
                        intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                        intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                        startActivity(Intent.createChooser(intent, "Share using ..."));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            showCloseDialog();
        }

        return false;
    }

    private void showDetails() {
        databaseReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("profilePicture")){
                        uProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                        //loading picture offline
                        Picasso.get().load(uProfilePicture).fit().networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image).into(profilePicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(uProfilePicture).fit().placeholder(R.drawable.profile_image)
                                        .into(profilePicture);
                            }
                        });
                    }

                    if (dataSnapshot.hasChild("fullName")){
                        String name = dataSnapshot.child("fullName").getValue().toString();

                        if(dataSnapshot.hasChild("phoneNumber")){
                            String pPhoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                            details.setText(name + "\n" + pPhoneNumber);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showCloseDialog() {
        TextView msg;
        Button yes, no;

        dialog.setContentView(R.layout.dialog_question);
        msg = dialog.findViewById(R.id.dialogQuestion_message);
        yes = dialog.findViewById(R.id.dialogQuestion_yes);
        no = dialog.findViewById(R.id.dialogQuestion_no);

        String message = "Do you want to exit ?";

        msg.setText(message);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                finish();
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


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currrentUser = mAuth.getCurrentUser();
        if (currrentUser == null){
            sendUserToLogInActivity();
        } else {
            checkUserExistence();
        }
    }

    private void checkUserExistence() {
        final String currentUserID = mAuth.getCurrentUser().getUid();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserID)){
                    sendUserToProfileActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_icon, menu);
        MenuItem menuItem = menu.findItem(R.id.mainToolbar_search);
        MenuItem adMenuItem = menu.findItem(R.id.mainToolbar_advertise);
        MenuItem menuNotification = menu.findItem(R.id.mainToolbar_notification);

        menuNotification.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendUserToPublicNoti();
                return true;
            }
        });

        adMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendUserToViewAdvertisementActivity();
                return true;
            }
        });

        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                sendUserToSearchActivity();
                return true;
            }
        });
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    MainHomeFragment mainHomeFragment = new MainHomeFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_relativeLayout, mainHomeFragment,
                            mainHomeFragment.getTag()).commit();
                    myToolBar.setVisibility(View.VISIBLE);
                    notificationCounter.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_orders:
                    MainOrderFragment mainOrderFragment = new MainOrderFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_relativeLayout, mainOrderFragment,
                            mainOrderFragment.getTag()).commit();
                    myToolBar.setVisibility(View.GONE);
                    notificationCounter.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_profile:
                    HomeProfileFragment homeProfileFragment = new HomeProfileFragment();
                    fragmentManager.beginTransaction().replace(R.id.main_relativeLayout, homeProfileFragment,
                            homeProfileFragment.getTag()).commit();
                    myToolBar.setVisibility(View.GONE);
                    notificationCounter.setVisibility(View.GONE);
                    return true;
            }
            return false;
        }
    };

    private void sendUserToProfileActivity() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void sendUserToLogInActivity() {
        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void sendUserToCartActivity(){
        Intent intent = new Intent(MainActivity.this, ShowCartActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToChatActivity(){
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToPublicNoti(){
        Intent intents = new Intent(MainActivity.this, PublicNotificationActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void sendUserToSearchActivity(){
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(intent);
    }

    private void sendUserToViewAdvertisementActivity(){
        Intent intent = new Intent(MainActivity.this, ViewAdvertisementActivity.class);
        startActivity(intent);
    }

    private void sendUserToOurPolicy(){
        Intent intent = new Intent(MainActivity.this, ViewPolicyActivity.class);
        startActivity(intent);
    }

}
