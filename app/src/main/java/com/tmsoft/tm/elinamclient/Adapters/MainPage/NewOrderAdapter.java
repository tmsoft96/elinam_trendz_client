package com.tmsoft.tm.elinamclient.Adapters.MainPage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Activity.ViewProductOrderDetailsActivity;
import com.tmsoft.tm.elinamclient.Holders.productOrderList;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

public class NewOrderAdapter extends RecyclerView.Adapter<NewOrderAdapter.OldOrderViewHolder> {
    private List<productOrderList> productOrderClassList;
    private Context context;


    public NewOrderAdapter(List<productOrderList> productOrderClassList, Context context){
        this.productOrderClassList = productOrderClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public OldOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_product_order, parent, false);

        return new OldOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OldOrderViewHolder holder, int position) {
        productOrderList orderClass = productOrderClassList.get(position);

        final String key = orderClass.getKey();

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Product Orders").child(key);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("orderConfirm")){
                        String ss = dataSnapshot.child("orderConfirm").getValue().toString();
                        holder.pOrderConfirm.setText(ss);
                    }

                    if (dataSnapshot.hasChild("productPrice")){
                        String ss = dataSnapshot.child("productPrice").getValue().toString();
                        holder.pPrice.setText(ss);
                    }

                    if (dataSnapshot.hasChild("productName")){
                        String ss = dataSnapshot.child("productName").getValue().toString();
                        holder.pName.setText(ss);
                    }

                    if (dataSnapshot.hasChild("productPicture1")){
                        final String ss = dataSnapshot.child("productPicture1").getValue().toString();
                        //loading picture offline
                        Picasso
                                .get()
                                .load(ss).fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.warning)
                                .into(holder.pPicture, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso
                                        .get()
                                        .load(ss).fit()
                                        .placeholder(R.drawable.warning)
                                        .into(holder.pPicture);
                            }
                        });
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        String ss = dataSnapshot.child("orderConfirm").getValue().toString();
                        holder.pOrderConfirm.setText(ss);
                    }

                    if (dataSnapshot.hasChild("orderDate")){
                        String ss = dataSnapshot.child("orderDate").getValue().toString();
                        holder.pDate.setText(ss);
                    }

                    if (dataSnapshot.hasChild("orderTime")){
                        String ss = dataSnapshot.child("orderTime").getValue().toString();
                        holder.pTime.setText(ss);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewProductOrderDetailsActivity.class);
                intent.putExtra("productOrderKey", key);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productOrderClassList.size();
    }

    public class OldOrderViewHolder extends RecyclerView.ViewHolder{
        public ImageView pPicture;
        public TextView pName, pPrice, pOrderConfirm, pTime, pDate;
        public View view;


        public OldOrderViewHolder(View mView){
            super(mView);
            view = mView;

            pPicture = mView.findViewById(R.id.viewProductOrder_productPicture);
            pName = mView.findViewById(R.id.viewProductOrder_productName);
            pPrice = mView.findViewById(R.id.viewProductOrder_productPrice);
            pOrderConfirm = mView.findViewById(R.id.viewProductOrder_confirmMessage);
            pDate = mView.findViewById(R.id.viewProductOrder_date);
            pTime = mView.findViewById(R.id.viewProductOrder_time);
        }
    }
}
