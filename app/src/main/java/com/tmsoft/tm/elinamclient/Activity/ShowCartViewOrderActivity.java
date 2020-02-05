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
import com.tmsoft.tm.elinamclient.Adapters.MainPage.NewOrderCartAdapter;
import com.tmsoft.tm.elinamclient.Holders.cartViewOrder;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.List;

public class ShowCartViewOrderActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference;

    private final List<cartViewOrder> pOrderList = new ArrayList<>();
    private NewOrderCartAdapter newOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cart_view_order);

        myToolBar = findViewById(R.id.showCartViewOrder_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Order");

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Details")
                .child("cartOrder").child(getCurrentUserId);
        dRef.keepSynced(true);
        //orderRef.child(getCurrentUserId).child("currentDate").setValue(day);
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

        newOrderAdapter = new NewOrderCartAdapter(pOrderList, this);

        recyclerView = findViewById(R.id.showCartViewOrder_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(newOrderAdapter);
    }

    private void showAllProductOrder(String day) {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("All Order Details")
                .child("cartOrder").child(getCurrentUserId).child("orders").child(day);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                cartViewOrder cartOrderNew = dataSnapshot.getValue(cartViewOrder.class);
                pOrderList.add(cartOrderNew);
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
        Intent intent = new Intent(ShowCartViewOrderActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
