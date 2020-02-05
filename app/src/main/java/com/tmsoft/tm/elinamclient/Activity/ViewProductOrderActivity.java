package com.tmsoft.tm.elinamclient.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Adapters.MainPage.NewOrderAdapter;
import com.tmsoft.tm.elinamclient.Holders.productOrderList;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.List;

public class ViewProductOrderActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;

    private DatabaseReference databaseReference;
    private String getCurrentUserId;
    private FirebaseAuth mAuth;

    private final List<productOrderList> pOrderList = new ArrayList<>();
    private NewOrderAdapter newOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product_order);

        myToolBar = findViewById(R.id.viewProductOrder_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Order");

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Details")
                .child("singleOrder").child(getCurrentUserId);
        dRef.keepSynced(true);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("currentDate")){
                        String day = dataSnapshot.child("currentDate").getValue().toString();
                        showAllProductOrder(day);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        newOrderAdapter = new NewOrderAdapter(pOrderList, this);

        recyclerView = findViewById(R.id.viewProductOrder_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(newOrderAdapter);
    }

    private void showAllProductOrder(String day) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Order Details")
                .child("singleOrder").child(getCurrentUserId).child("orders").child(day);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                productOrderList productOrderNew = dataSnapshot.getValue(productOrderList.class);
                pOrderList.add(productOrderNew);
                newOrderAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            sendUserToMainActivity();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(ViewProductOrderActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
