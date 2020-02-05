package com.tmsoft.tm.elinamclient.Activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Holders.orderSummaryClass;
import com.tmsoft.tm.elinamclient.R;

public class DifferentCartOrdersViewActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private RelativeLayout noOrder;
    private SwipeRefreshLayout refresh;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference;

    private String orderType, orderStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_different_cart_orders_view);

        myToolBar = findViewById(R.id.differentCart_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Order");

        orderType = getIntent().getExtras().get("orderType").toString();
        orderStatus = getIntent().getExtras().get("orderStatus").toString();

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Order Summary").child(getCurrentUserId)
                .child(orderType).child(orderStatus);
        databaseReference.keepSynced(true);

        noOrder = findViewById(R.id.differentCart_noOrder);

        refresh = findViewById(R.id.differentCart_refresh);

        recyclerView = findViewById(R.id.differentCart_recyclerView);
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
        FirebaseRecyclerAdapter<orderSummaryClass, showCartViewOrderViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<orderSummaryClass, showCartViewOrderViewHolder>(

                        orderSummaryClass.class,
                        R.layout.layout_show_cart_view_order,
                        showCartViewOrderViewHolder.class,
                        databaseReference

                ) {
                    @Override
                    protected void populateViewHolder(final showCartViewOrderViewHolder viewHolder, orderSummaryClass model, int position) {
                        final String value = model.getItem();

                        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
                        dReference.keepSynced(true);

                        dReference.child(value).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("orderDate")){
                                        String orderDate = dataSnapshot.child("orderDate").getValue().toString();
                                        viewHolder.setOrderDate(orderDate);
                                    }

                                    if (dataSnapshot.hasChild("orderTime")){
                                        String orderTime = dataSnapshot.child("orderTime").getValue().toString();
                                        viewHolder.setOrderTime(orderTime);
                                    }

                                    if (dataSnapshot.hasChild("orderConfirm")){
                                        String orderConfirm = dataSnapshot.child("orderConfirm").getValue().toString();
                                        viewHolder.setOrderConfirm(orderConfirm);
                                    }

                                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                                        String paymentAmountPaid = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                                        viewHolder.setPaymentAmountPaid(paymentAmountPaid);
                                    }

                                    if (dataSnapshot.hasChild("paymentConfirm")){
                                        String paymentConfirm = dataSnapshot.child("paymentConfirm").getValue().toString();
                                        viewHolder.setPaymentConfirm(paymentConfirm);
                                    }

                                    if (dataSnapshot.hasChild("deliverySuccess")){
                                        String deliverySuccess = dataSnapshot.child("deliverySuccess").getValue().toString();
                                        viewHolder.setDeliverySuccess(deliverySuccess);
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
                                Intent intent = new Intent(DifferentCartOrdersViewActivity.this, ShowCartViewOrderDetailsActivity.class);
                                intent.putExtra("cartPostKey", value);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class showCartViewOrderViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public showCartViewOrderViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setOrderDate(String orderDate) {
            TextView date = mView.findViewById(R.id.layoutShowCartViewOrder_date);
            date.setText(orderDate);
        }

        public void setOrderTime(String orderTime) {
            TextView time = mView.findViewById(R.id.layoutShowCartViewOrder_time);
            time.setText(orderTime);
        }

        public void setOrderConfirm(String orderConfirm) {
            TextView orderStatus = mView.findViewById(R.id.layoutShowCartViewOrder_orderStatus);
            orderStatus.setText(orderConfirm);
        }

        public void setPaymentAmountPaid(String paymentAmountPaid) {
            TextView amountPaid = mView.findViewById(R.id.layoutShowCartViewOrder_amountPaid);
            if (TextUtils.isEmpty(paymentAmountPaid))
                amountPaid.setText("-");
            else
                amountPaid.setText(paymentAmountPaid);
        }

        public void setPaymentConfirm(String paymentConfirm) {
            TextView paymentStatus = mView.findViewById(R.id.layoutShowCartViewOrder_paymentStatus);
            paymentStatus.setText(paymentConfirm);
        }

        public void setDeliverySuccess(String deliverySuccess) {
            TextView deliveryStatus = mView.findViewById(R.id.layoutShowCartViewOrder_deliveryStatus);
            deliveryStatus.setText(deliverySuccess);
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
