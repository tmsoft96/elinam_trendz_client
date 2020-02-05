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

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Adapters.ShopAdapter;
import com.tmsoft.tm.elinamclient.Holders.ShopProductClass;
import com.tmsoft.tm.elinamclient.Holders.autofit;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MadeInGhanaFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private TextView details;
    private ImageView showDetails, hideDetails;
    private SwipeRefreshLayout refresh;

    private final List<ShopProductClass> shopProductClassList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private ShopAdapter shopAdapter;

    private DatabaseReference databaseReference;

    public MadeInGhanaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_s_made_in_ghana, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        autofit noColums = new autofit();
        noColums.autofit(view.getContext());

        shopAdapter = new ShopAdapter(shopProductClassList, view.getContext(), noColums.getLayoutWidth(), "Made In Ghana");

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        recyclerView = view.findViewById(R.id.recycler_madeInGhana);
        recyclerView.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(view.getContext(), mNoofColums);
        gridLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(shopAdapter);

        details = view.findViewById(R.id.fragmentMadeInGhana_details);
        showDetails = view.findViewById(R.id.fragmentMadeInGhana_showDetail);
        hideDetails = view.findViewById(R.id.fragmentMadeInGhana_hideDetail);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.fragmentMadeInGhana_refreshs);

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
                    if (dataSnapshot.hasChild("Made In Ghana")){
                        String gg = dataSnapshot.child("Made In Ghana").getValue().toString();
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
        shopProductClassList.clear();
        Query query = databaseReference.child("Shop Details").orderByChild("shopItemsMIG").startAt("1");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ShopProductClass shopProductClass = dataSnapshot.getValue(ShopProductClass.class);
                shopProductClassList.add(shopProductClass);
                shopAdapter.notifyDataSetChanged();
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
