package com.tmsoft.tm.elinamclient.fragment.OrderFragment.OrderView;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.tmsoft.tm.elinamclient.Adapters.MainPage.OldOrderAdapter;
import com.tmsoft.tm.elinamclient.Holders.productOrderOldList;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewSingleOrderFragment extends Fragment {

    private View view;
    private Toolbar myToolBar;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private String getCurrentUserId;
    private DatabaseReference databaseReference;

    private final List<productOrderOldList> productOrderLists = new ArrayList<>();
    private OldOrderAdapter oldOrderAdapter;

    public ViewSingleOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_w_view_single_order, container, false);

        myToolBar = view.findViewById(R.id.singleOrderViewOld_toolbar);
        myToolBar.setTitle("Previous Orders");

        oldOrderAdapter = new OldOrderAdapter(productOrderLists, view.getContext());

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Product Orders");
        databaseReference.keepSynced(true);

        recyclerView = view.findViewById(R.id.singleOrderViewOld_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(oldOrderAdapter);

        showItems();

        return view;
    }

    private void showItems() {
        productOrderLists.clear();
        Query productOrderQuery = databaseReference.orderByChild("userId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");

        productOrderQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                productOrderOldList productOrderOld = dataSnapshot.getValue(productOrderOldList.class);
                productOrderLists.add(productOrderOld);
                oldOrderAdapter.notifyDataSetChanged();
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

}
