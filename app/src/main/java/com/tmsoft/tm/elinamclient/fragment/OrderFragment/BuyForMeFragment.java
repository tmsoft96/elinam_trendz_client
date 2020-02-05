package com.tmsoft.tm.elinamclient.fragment.OrderFragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Activity.ViewBuyForMeActivity;
import com.tmsoft.tm.elinamclient.Holders.buyForMeClass;
import com.tmsoft.tm.elinamclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyForMeFragment extends Fragment {

    private View view;
    private Toolbar myToolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private RelativeLayout relativeLayout;

    private DatabaseReference databaseReference;
    private String getCurrentUserId;

    public BuyForMeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_buy_for_me, container, false);

        myToolbar = (Toolbar) view.findViewById(R.id.fragmentBuyForMe_toolbar);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragmentBuyForMe_recycler);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragmentBuyForMe_refresh);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.fragmentBuyForMe_noOrder);

        myToolbar.setTitle("Buy for me");

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Buy for me");
        databaseReference.keepSynced(true);
        getCurrentUserId = FirebaseAuth.getInstance().getUid();

        showOrders();
        checkOrders();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showOrders();
                checkOrders();
                refreshLayout.setRefreshing(false);
            }
        });

        return view;
    }

    private void checkOrders() {
        Query orderQuery = databaseReference.orderByChild("userId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");
        orderQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        relativeLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showOrders() {
        Query orderQuery = databaseReference.orderByChild("userId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");

        FirebaseRecyclerAdapter<buyForMeClass, buyForMeViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<buyForMeClass, buyForMeViewHolder>(
                        buyForMeClass.class,
                        R.layout.layout_buy_for_me,
                        buyForMeViewHolder.class,
                        orderQuery
                ) {
                    @Override
                    protected void populateViewHolder(final buyForMeViewHolder viewHolder, final buyForMeClass model, int position) {
                        final String postKey = getRef(position).getKey();

                        viewHolder.setProductName(model.getProductName());
                        viewHolder.setProductQuantity(model.getProductQuantity());
                        viewHolder.setProductUrgent(model.getProductUrgent());
                        viewHolder.setProductImage(model.getProductImage());
                        viewHolder.setMessage(model.getMessage());
                        viewHolder.setOrderDate(model.getOrderDate());
                        viewHolder.setOrderTime(model.getOrderTime());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(view.getContext(), ViewBuyForMeActivity.class);
                                intent.putExtra("postKey", postKey);
                                startActivity(intent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class buyForMeViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public buyForMeViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProductImage(final String productImage) {
            final ImageView pProductPicture = (ImageView) mView.findViewById(R.id.layoutBuyForMe_productPicture);
            //loading picture offline
            Picasso
                    .get()
                    .load(productImage).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image).into(pProductPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso
                            .get()
                            .load(productImage).fit()
                            .placeholder(R.drawable.no_image)
                            .into(pProductPicture);
                }
            });
        }

        public void setProductName(String productName) {
            TextView pProductName = (TextView) mView.findViewById(R.id.layoutBuyForMe_productName);
            pProductName.setText(productName);
        }

        public void setProductUrgent(String productUrgent) {
            TextView pProductUrgent = (TextView) mView.findViewById(R.id.layoutBuyForMe_productUrgent);
            pProductUrgent.setText(productUrgent);
        }

        public void setProductQuantity(String productQuantity) {
            TextView qty = (TextView) mView.findViewById(R.id.layoutBuyForMe_productQuantity);
            qty.setText(productQuantity);
        }

        public void setMessage(String message) {
            TextView msg = (TextView) mView.findViewById(R.id.layoutBuyForMe_message);
            msg.setText(message);
        }

        public void setOrderDate(String orderDate) {
            TextView date = (TextView) mView.findViewById(R.id.layoutBuyForMe_date);
            date.setText(orderDate);
        }

        public void setOrderTime(String orderTime) {
            TextView time = (TextView) mView.findViewById(R.id.layoutBuyForMe_time);
            time.setText(orderTime);
        }
    }

}
