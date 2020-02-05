package com.tmsoft.tm.elinamclient.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Holders.orderSummaryClass;
import com.tmsoft.tm.elinamclient.R;

public class DifferentOrdersViewActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private RelativeLayout noOrder;

    private DatabaseReference databaseReference;
    private String getCurrentUserId;
    private FirebaseAuth mAuth;

    private String orderType, orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_different_orders_view);

        myToolBar = findViewById(R.id.different_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Order");

        orderType = getIntent().getExtras().get("orderType").toString();
        orderStatus = getIntent().getExtras().get("orderStatus").toString();

        refresh = findViewById(R.id.different_refresh);
        noOrder = findViewById(R.id.different_noOrder);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(getCurrentUserId)
                .child(orderType).child(orderStatus);
        databaseReference.keepSynced(true);

        recyclerView = findViewById(R.id.different_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        checkOrder();
        showAllProductOrder();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkOrder();
                showAllProductOrder();
                refresh.setRefreshing(false);
            }
        });
    }

    private void checkOrder() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noOrder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAllProductOrder() {
        FirebaseRecyclerAdapter<orderSummaryClass, productOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<orderSummaryClass, productOrderViewHolder>(
                        orderSummaryClass.class,
                        R.layout.layout_product_order,
                        productOrderViewHolder.class,
                        databaseReference
                ){
                    @Override
                    protected void populateViewHolder(final productOrderViewHolder viewHolder, final orderSummaryClass model, int position) {
                        final String value = model.getItem();
                        //final String productOrderKey = getRef(position).getKey();

                        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Product Orders");
                        dRef.keepSynced(true);

                        dRef.child(value).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("orderConfirm")){
                                        String orderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                                        viewHolder.setOrderConfirm(orderConfirm);
                                    }

                                    if (dataSnapshot.hasChild("productName")){
                                        String productName = dataSnapshot.child("productName").getValue().toString();
                                        viewHolder.setProductName(productName);
                                    }

                                    if (dataSnapshot.hasChild("productPicture1")){
                                        String productPicture1 = dataSnapshot.child("productPicture1").getValue().toString();
                                        viewHolder.setProductPicture1(productPicture1, getApplicationContext());
                                    }

                                    if (dataSnapshot.hasChild("productPrice")){
                                        String productPrice = dataSnapshot.child("productPrice").getValue().toString();
                                        viewHolder.setProductPrice(productPrice);
                                    }

                                    if (dataSnapshot.hasChild("orderDate")){
                                        String ss = dataSnapshot.child("orderDate").getValue().toString();
                                        viewHolder.setOrderDate(ss);
                                    }

                                    if (dataSnapshot.hasChild("orderTime")){
                                        String ss = dataSnapshot.child("orderTime").getValue().toString();
                                        viewHolder.setOrderTime(ss);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(DifferentOrdersViewActivity.this, ViewProductOrderDetailsActivity.class);
                                intent.putExtra("productOrderKey", value);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class productOrderViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public productOrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProductPicture1(final String productPicture1, final Context context) {
            final ImageView pPicture = mView.findViewById(R.id.viewProductOrder_productPicture);
            //loading picture offline
            Picasso
                    .get()
                    .load(productPicture1)
                    .resize(50,50)
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning).into(pPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso
                            .get()
                            .load(productPicture1)
                            .resize(50,50)
                            .centerCrop()
                            .placeholder(R.drawable.warning).into(pPicture);
                }
            });
        }

        public void setProductName(String productName) {
            TextView pName = mView.findViewById(R.id.viewProductOrder_productName);
            pName.setText(productName);
        }

        public void setProductPrice(String productPrice) {
            TextView pPrice = mView.findViewById(R.id.viewProductOrder_productPrice);
            pPrice.setText(productPrice);
        }

        public void setOrderConfirm(String orderConfirm) {
            TextView pOrderConfirm = mView.findViewById(R.id.viewProductOrder_confirmMessage);
            pOrderConfirm.setText(orderConfirm);
        }

        public void setOrderDate(String orderDate){
            TextView pOrderDate = mView.findViewById(R.id.viewProductOrder_date);
            pOrderDate.setText(orderDate);
        }

        public void setOrderTime(String orderTime){
            TextView pOrderTime = mView.findViewById(R.id.viewProductOrder_time);
            pOrderTime.setText(orderTime);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }

        return false;
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
