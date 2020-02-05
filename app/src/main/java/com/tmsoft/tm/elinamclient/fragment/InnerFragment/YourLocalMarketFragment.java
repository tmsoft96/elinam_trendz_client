package com.tmsoft.tm.elinamclient.fragment.InnerFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Adapters.AllProductsAdapter;
import com.tmsoft.tm.elinamclient.Holders.allProductsClass;
import com.tmsoft.tm.elinamclient.Holders.autofit;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourLocalMarketFragment extends Fragment {
    private View view;

    private RecyclerView recyclerView;
    private TextView details;
    private ImageView showDetails, hideDetails;
    private SwipeRefreshLayout refresh;

    private final List<allProductsClass> allProductsClassList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private AllProductsAdapter allProductsAdapter;

    private DatabaseReference databaseReference;
    private autofit noColums;
    private int ref = 0;


    public YourLocalMarketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_s_live_stock, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.keepSynced(true);

        noColums = new autofit();
        noColums.autofit(view.getContext());

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        recyclerView = view.findViewById(R.id.recycler_livestock);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(view.getContext(), mNoofColums);
        gridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(gridLayoutManager);

        details = view.findViewById(R.id.fragmentLivestock_details);
        showDetails = view.findViewById(R.id.fragmentLivestock_showDetails);
        hideDetails = view.findViewById(R.id.fragmentLivestock_hideDetails);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.fragmentLivestock_refreshs);

        showItems();

        showDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details.setVisibility(View.VISIBLE);
                showDetails.setVisibility(View.GONE);
                hideDetails.setVisibility(View.VISIBLE);
            }
        });

        hideDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                details.setVisibility(View.GONE);
                showDetails.setVisibility(View.VISIBLE);
                hideDetails.setVisibility(View.GONE);
            }
        });

        showCategoryDetail();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showItems();
                showCategoryDetail();
                refresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void showCategoryDetail(){
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category Details");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("Livestock")){
                        String gg = dataSnapshot.child("Livestock").getValue().toString();
                        if (gg.isEmpty())
                            showDetails.setVisibility(View.GONE);
                        else
                            showDetails.setVisibility(View.VISIBLE);
                        details.setText(gg);
                    }
                } else
                    showDetails.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showItems() {
        allProductsClassList.clear();
        Query query = databaseReference.orderByChild("ProductCategory").startAt("Your Local Market").endAt("Your Local Market + \uf8ff");
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

                    allProductsAdapter = new AllProductsAdapter(allProductsClassList, view.getContext(), noColums.getLayoutWidth(),
                            noColums.getTestLength());
                    recyclerView.setAdapter(allProductsAdapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
