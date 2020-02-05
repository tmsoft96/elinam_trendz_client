package com.tmsoft.tm.elinamclient.fragment.OrderFragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Activity.DifferentCartOrdersViewActivity;
import com.tmsoft.tm.elinamclient.Adapters.MainPage.NewOrderCartAdapter;
import com.tmsoft.tm.elinamclient.Holders.cartViewOrder;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartOrderFragment extends Fragment {

    private View view;
    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private ImageView threeDots;
    private Dialog dialog;
    private RelativeLayout noOrder;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference;

    private final List<cartViewOrder> pOrderList = new ArrayList<>();
    private NewOrderCartAdapter newOrderAdapter;

    private String day;

    public CartOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_k_cart_order, container, false);

        myToolBar = view.findViewById(R.id.fragmentCartOrder_toolbar);
        myToolBar.setTitle("Cart Product Order");

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();

        checkItems();

        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("All Order Details")
                .child("cartOrder").child(getCurrentUserId);
        dRef.keepSynced(true);
        //orderRef.child(getCurrentUserId).child("currentDate").setValue(day);
        dRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("currentDate")){
                        day = dataSnapshot.child("currentDate").getValue().toString();
                        showAllProductOrder(day);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        threeDots = view.findViewById(R.id.fragmentCartOrder_threeDots);
        dialog = new Dialog(view.getContext(), R.style.Theme_CustomDialog);
        noOrder = view.findViewById(R.id.fragmentCartOrder_noOrder);

        newOrderAdapter = new NewOrderCartAdapter(pOrderList, view.getContext());

        recyclerView = view.findViewById(R.id.fragmentCartOrder_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(newOrderAdapter);

        threeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewOrderOptions();
            }
        });

        return view;
    }

    private void checkItems() {
        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("Cart Product Orders");
        dReference.keepSynced(true);
        Query orderQuery = dReference.orderByChild("userId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");

        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int number = (int) dataSnapshot.getChildrenCount();

                    if (number > 0)
                        noOrder.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAllProductOrder(String day) {
        pOrderList.clear();
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


    private void viewOrderOptions() {
        TextView close;
        RelativeLayout pending, concealed, confirm;

        dialog.setContentView(R.layout.dialog_view_order_option);
        close = dialog.findViewById(R.id.dialog_viewOrderCloses);
        pending = dialog.findViewById(R.id.dialog_viewOrderPending);
        concealed = dialog.findViewById(R.id.dialog_viewOrderConcealed);
        confirm = dialog.findViewById(R.id.dialog_viewOrderConfirmed);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToDifferentCartOrder("cartOrder", "pendingOrder");
            }
        });

        concealed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToDifferentCartOrder("cartOrder", "canceledOrder");
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToDifferentCartOrder("cartOrder", "confirmOrder");
            }
        });

        dialog.show();
    }


    private void sendUserToDifferentCartOrder(String orderType, String orderStatus){
        Intent intent = new Intent(view.getContext(), DifferentCartOrdersViewActivity.class);
        intent.putExtra("orderType", orderType);
        intent.putExtra("orderStatus", orderStatus);
        startActivity(intent);
    }
}
