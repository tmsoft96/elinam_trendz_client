package com.tmsoft.tm.elinamclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.Holders.advertiseClass;
import com.tmsoft.tm.elinamclient.R;

public class ViewAdvertisementActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;

    private DatabaseReference advertiseReference;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_advertisement);

        myToolbar = (Toolbar) findViewById(R.id.viewAdvertisement_toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.viewAdvertisement_recycler);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.viewAdvertisement_refresh);

        advertiseReference = FirebaseDatabase.getInstance().getReference().child("Product Advertisement");
        advertiseReference.keepSynced(true);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Promotion");

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);

        try{
            showAllProductAdvertise();
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    showAllProductAdvertise();
                    refreshLayout.setRefreshing(false);
                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ViewAdvertisementActivity", "main method");
        }
    }

    private void showAllProductAdvertise() {
        Query query = advertiseReference.orderByChild("advertisementSlot");
        FirebaseRecyclerAdapter<advertiseClass, advertiseViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<advertiseClass, advertiseViewHolder>(
                        advertiseClass.class,
                        R.layout.layout_view_advertise,
                        advertiseViewHolder.class,
                        query
                ) {
                    @Override
                    protected void populateViewHolder(final advertiseViewHolder viewHolder, advertiseClass model, int position) {
                        viewHolder.setAdvertisementEvent(model.getAdvertisementEvent());
                        viewHolder.setAdvertisementSlot(model.getAdvertisementSlot());
                        viewHolder.setAdvertisementDateAndTime(model.getAdvertisementDateAndTime());

                        final String advertisementId = model.getAdvertiseId();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(advertisementId);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("ProductName")){
                                        String productName = dataSnapshot.child("ProductName").getValue().toString();
                                        viewHolder.setProductName(productName);
                                    }

                                    if (dataSnapshot.hasChild("ProductPrice")){
                                        String productPrice = dataSnapshot.child("ProductPrice").getValue().toString();
                                        viewHolder.setProductPrice(productPrice);
                                    }

                                    if (dataSnapshot.hasChild("ProductPicture1")){
                                        String productPicture = dataSnapshot.child("ProductPicture1").getValue().toString();
                                        viewHolder.setProductPicture(productPicture);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        viewHolder.setIsProductDiscount(model.getIsProductDiscount());
                        viewHolder.setProductDiscountPrice(model.getProductDiscountPrice());
                        viewHolder.setProductDiscountPercent(model.getProductDiscountPercent());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ViewAdvertisementActivity.this, AdvertisementProductActivity.class);
                                intent.putExtra("postKey", advertisementId);
                                startActivity(intent);
                            }
                        });
                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class advertiseViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView pDiscountPrice, pPrice, pPrice2, pDiscountPercent;

        public advertiseViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;

            pPrice2 = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_productPrice2);
            pPrice  = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_productPrice);
            pDiscountPrice = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_productDiscountPrice);
            pDiscountPercent = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_discountPercent);
        }

        public void setAdvertisementEvent(String advertisementEvent) {
            ImageView eventPicture = (ImageView) mView.findViewById(R.id.layoutViewAdvertisement_eventPicture);
            if (advertisementEvent.equalsIgnoreCase("Christmas Packages"))
                eventPicture.setImageResource(R.drawable.packages_christmas);
            else if (advertisementEvent.equalsIgnoreCase("Easter Packages"))
                eventPicture.setImageResource(R.drawable.packages_easter);
            else if (advertisementEvent.equalsIgnoreCase("Black Market Packages"))
                eventPicture.setImageResource(R.drawable.packages_black_market);
            else if (advertisementEvent.equalsIgnoreCase("Price Reduction Packages"))
                eventPicture.setImageResource(R.drawable.packages_price_reduction);
            else if (advertisementEvent.equalsIgnoreCase("Hot Packages"))
                eventPicture.setImageResource(R.drawable.packages_hot);
            else if (advertisementEvent.equalsIgnoreCase("Valentine Packages"))
                eventPicture.setImageResource(R.drawable.packages_valentine);
            else if (advertisementEvent.equalsIgnoreCase("Mother's Day Packages"))
                eventPicture.setImageResource(R.drawable.packages_mothers_day);
            else if (advertisementEvent.equalsIgnoreCase("Father's Day Packages"))
                eventPicture.setImageResource(R.drawable.packages_fathers_day);
            else
                eventPicture.setImageResource(R.drawable.packages_customise);
        }

        public void setAdvertisementSlot(String advertisementSlot) {
            TextView slotNumber = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_slotNumber);
            slotNumber.setText(advertisementSlot);
        }

        public void setAdvertisementDateAndTime(String advertisementDateAndTime) {
            TextView endDate = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_endDate);
            endDate.setText("end on " + advertisementDateAndTime);
        }

        public void setProductPicture(final String productPicture) {
            final ImageView pPicture = (ImageView) mView.findViewById(R.id.layoutViewAdvertisement_productPicture);
            //loading picture offline
            Picasso.
                    get()
                    .load(productPicture)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning)
                    .into(pPicture, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso
                                    .get()
                                    .load(productPicture)
                                    .fit()
                                    .placeholder(R.drawable.warning)
                                    .into(pPicture);
                        }
                    });
        }

        public void setProductName(String productName) {
            TextView pName = (TextView) mView.findViewById(R.id.layoutViewAdvertisement_productName);
            pName.setText(productName);
        }

        public void setProductPrice(String productPrice) {
            pPrice2.setText(productPrice);
            pPrice.setText(productPrice);
        }

        public void setIsProductDiscount(String isProductDiscount) {
            try{
                if (isProductDiscount.equalsIgnoreCase("false")){
                    pDiscountPercent.setVisibility(View.GONE);
                    pDiscountPrice.setVisibility(View.GONE);
                    pPrice2.setVisibility(View.GONE);
                    pPrice.setVisibility(View.VISIBLE);
                } else if (isProductDiscount.equalsIgnoreCase("true")){
                    pDiscountPercent.setVisibility(View.VISIBLE);
                    pDiscountPrice.setVisibility(View.VISIBLE);
                    pPrice2.setVisibility(View.VISIBLE);
                    pPrice.setVisibility(View.GONE);
                }
            } catch (Exception e){
                Log.i("error", e.getMessage());
            }
        }

        public void setProductDiscountPrice(String productDiscountPrice) {
            pDiscountPrice.setText("GHC" + productDiscountPrice);
        }

        public void setProductDiscountPercent(String productDiscountPercent) {
            pDiscountPercent.setText(productDiscountPercent);
        }
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
