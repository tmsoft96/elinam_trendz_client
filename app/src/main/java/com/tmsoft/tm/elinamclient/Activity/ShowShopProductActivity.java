package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Adapters.AllProductsAdapter;
import com.tmsoft.tm.elinamclient.Holders.allProductsClass;
import com.tmsoft.tm.elinamclient.Holders.autofit;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShowShopProductActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private ImageView shopDetails;
    private Dialog dialog;
    private SwipeRefreshLayout refresh;

    private final List<allProductsClass> allProductsClassList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private AllProductsAdapter allProductsAdapter;

    private DatabaseReference databaseReference;

    private String key;
    private int ref = 0;
    private autofit noColums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_shop_product);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        myToolBar = (Toolbar) findViewById(R.id.shopProduct_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Products");

        key = getIntent().getExtras().get("key").toString();

        shopDetails = (ImageView) findViewById(R.id.shopProduct_info);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        refresh = (SwipeRefreshLayout) findViewById(R.id.shopProduct_refresh);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        noColums = new autofit();
        noColums.autofit(this);
        allProductsAdapter = new AllProductsAdapter(allProductsClassList, this, noColums.getLayoutWidth(), noColums.getTestLength());

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_shopProduct);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, mNoofColums);
        gridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(allProductsAdapter);

        showItems();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showItems();
                refresh.setRefreshing(false);
            }
        });

        shopDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showShopDetails();
            }
        });
    }

    private void showShopDetails() {
        final TextView close, name, detail;
        final ImageView icon;

        dialog.setContentView(R.layout.dialog_shop_details);
        close = dialog.findViewById(R.id.dialogShop_close);
        name = dialog.findViewById(R.id.dialogShop_name);
        detail = dialog.findViewById(R.id.dialogShop_details);
        icon = dialog.findViewById(R.id.dialogShop_icon);

        DatabaseReference shopRef = FirebaseDatabase.getInstance().getReference().child("Shop Details").child(key);
        shopRef.keepSynced(true);
        shopRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("shopLogo")){
                        final String logo = dataSnapshot.child("shopLogo").getValue().toString();
                        try{
                            Picasso.get()
                                    .load(logo).fit()
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.warning).into(icon, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(logo).fit().placeholder(R.drawable.warning)
                                            .into(icon);
                                }
                            });
                        } catch (Exception ex){

                        }
                    }

                    if (dataSnapshot.hasChild("shopName")){
                        String sName = dataSnapshot.child("shopName").getValue().toString();
                        name.setText(sName);
                    }

                    if (dataSnapshot.hasChild("shopOther")){
                        String sOther = dataSnapshot.child("shopOther").getValue().toString();
                        detail.setText(sOther);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    private void showItems() {
        allProductsClassList.clear();

        Query query = databaseReference.child("Products").orderByChild("ProductShopId").startAt(key).endAt(key + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                        allProductsClassList.add(snapShot.getValue(allProductsClass.class));
                    }

                    if (ref == 0){
                        Collections.reverse(allProductsClassList);
                        ref = 1;
                    } else
                        ref = 0;

                    allProductsAdapter = new AllProductsAdapter(allProductsClassList, ShowShopProductActivity.this, noColums.getLayoutWidth(),
                            noColums.getTestLength());
                    recyclerView.setAdapter(allProductsAdapter);
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
