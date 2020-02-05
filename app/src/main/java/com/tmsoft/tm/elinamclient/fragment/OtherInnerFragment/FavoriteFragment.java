package com.tmsoft.tm.elinamclient.fragment.OtherInnerFragment;


import android.content.Intent;
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
import com.tmsoft.tm.elinamclient.Activity.ViewFavoriteDetailActivity;
import com.tmsoft.tm.elinamclient.Holders.autofit;
import com.tmsoft.tm.elinamclient.Holders.favoriteProducts;
import com.tmsoft.tm.elinamclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private RelativeLayout noProduct;
    private SwipeRefreshLayout refresh;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String getCurrentUserId;
    private int num;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Favorites");
        databaseReference.keepSynced(true);

        autofit noColums = new autofit();
        noColums.autofit(view.getContext());

        int mNoofColums = noColums.getNoOfColumn();
        Log.i("Number", mNoofColums + "");

        noProduct = view.findViewById(R.id.fragmentCart_noProduct);
        noProduct.setVisibility(View.VISIBLE);

        refresh = view.findViewById(R.id.fragmentCart_refresh);

        recyclerView = view.findViewById(R.id.fragmentCart_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mNoofColums));

        checkFavoriteProduct();
        displayAllFavoriteProduct();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkFavoriteProduct();
                displayAllFavoriteProduct();
                refresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void checkFavoriteProduct() {
        Query favQuery = databaseReference.orderByChild("UserId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");

        favQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noProduct.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayAllFavoriteProduct() {
        Query favQuery = databaseReference.orderByChild("UserId")
                .startAt(getCurrentUserId).endAt(getCurrentUserId + "\uf8ff");

        FirebaseRecyclerAdapter<favoriteProducts, favoriteProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<favoriteProducts, favoriteProductViewHolder>(

                        favoriteProducts.class,
                        R.layout.layout_all_post_display,
                        FavoriteFragment.favoriteProductViewHolder.class,
                        favQuery

                ) {
                    @Override
                    protected void populateViewHolder(final FavoriteFragment.favoriteProductViewHolder viewHolder, favoriteProducts model, int position) {
                        final String postKey = model.getProductKey();
                        final String thisKey = getRef(position).getKey();

                        DatabaseReference dReference = FirebaseDatabase.getInstance().getReference().child("Products");
                        dReference.keepSynced(true);

                        try{
                            dReference.child(postKey).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        if (dataSnapshot.hasChild("ProductName")){
                                            String pProductName = dataSnapshot.child("ProductName").getValue().toString();
                                            viewHolder.setpProductName(pProductName);
                                        }
                                        if (dataSnapshot.hasChild("ProductPrice")){
                                            String pProductPrice = dataSnapshot.child("ProductPrice").getValue().toString();

                                            if (dataSnapshot.hasChild("ProductDiscount")){
                                                String pDiscount = dataSnapshot.child("ProductDiscount").getValue().toString();
                                                if (pDiscount.equals("no"))
                                                    viewHolder.setpProductPrice(pProductPrice);
                                                else if (pDiscount.equals("yes")){
                                                    if (dataSnapshot.hasChild("ProductDiscountPrice")){
                                                        String pDiscountPx = dataSnapshot.child("ProductDiscountPrice").getValue().toString();
                                                        viewHolder.setpProductPrice(pDiscountPx);
                                                    }
                                                }
                                            } else
                                                viewHolder.setpProductPrice(pProductPrice);
                                        }
                                        if (dataSnapshot.hasChild("ProductPicture1")) {
                                            String pProductPicture1 = dataSnapshot.child("ProductPicture1").getValue().toString();
                                            viewHolder.setpProductPicture1(pProductPicture1);
                                        }
                                    } else {
                                        // if product is deleted or does not exit delete from favorite database
                                        databaseReference.child(thisKey).removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        } catch (Exception ex){

                        }

                        //setting onClick Listener to the recycle view
                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                num = 0;
                                Intent clickPostIntent = new Intent(getContext(), ViewFavoriteDetailActivity.class);
                                clickPostIntent.putExtra("postKey", postKey);
                                startActivity(clickPostIntent);
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class favoriteProductViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView discountPercent, discountPrice, productName;
        ImageView priceConceal;
        RelativeLayout relativeLayout;

        public favoriteProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            discountPercent = mView.findViewById(R.id.allPost_productDiscountPercent);
            discountPrice = mView.findViewById(R.id.allPost_productDiscountPrice);
            priceConceal = mView.findViewById(R.id.allPost_priceConceal);
            relativeLayout = mView.findViewById(R.id.allPost_relative);
            productName = mView.findViewById(R.id.allPost_productName);

            int width = relativeLayout.getLayoutParams().width;
            int height = relativeLayout.getLayoutParams().height;
            Log.i("width", width+ "");

            autofit noColums = new autofit();
            noColums.autofit(mView.getContext());

            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(noColums.getLayoutWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT));
            productName.setHeight(noColums.getTextHeight());

            discountPercent.setVisibility(View.GONE);
            discountPrice.setVisibility(View.GONE);
            priceConceal.setVisibility(View.GONE);
        }

        public void setpProductName(String pProductName) {
            productName.setText(pProductName);
        }

        public void setpProductPrice(String pProductPrice) {
            TextView productPrice = mView.findViewById(R.id.allPost_productPrice);
            productPrice.setText(pProductPrice);
        }

        public void setpProductPicture1(final String pProductPicture1) {
            final ImageView pProductPicture = mView.findViewById(R.id.allPost_productImage);
            //loading picture offline
            Picasso.get()
                    .load(pProductPicture1).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning).into(pProductPicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(pProductPicture1).fit()
                            .placeholder(R.drawable.warning).into(pProductPicture);
                }
            });
        }
    }
}
