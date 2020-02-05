package com.tmsoft.tm.elinamclient.Adapters.MainPage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Activity.ShowCartViewOrderDetailsActivity;
import com.tmsoft.tm.elinamclient.Holders.cartViewOrder;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

public class NewOrderCartAdapter extends RecyclerView.Adapter<NewOrderCartAdapter.OrderCartViewHolder> {
    private List<cartViewOrder> cartOrderClassList;
    private Context context;


    public NewOrderCartAdapter(List<cartViewOrder> cartOrderClassList, Context context){
        this.cartOrderClassList = cartOrderClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_show_cart_view_order, parent, false);

        return new OrderCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderCartViewHolder holder, int position) {
        cartViewOrder orderClass = cartOrderClassList.get(position);

        final String key = orderClass.getKey();

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference()
                .child("Cart Product Orders").child(key);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("orderDate")){
                        String ss = dataSnapshot.child("orderDate").getValue().toString();
                        holder.date.setText(ss);
                    }

                    if (dataSnapshot.hasChild("orderTime")){
                        String ss = dataSnapshot.child("orderTime").getValue().toString();
                        holder.time.setText(ss);
                    }

                    if (dataSnapshot.hasChild("orderConfirm")){
                        String ss = dataSnapshot.child("orderConfirm").getValue().toString();
                        holder.orderStatus.setText(ss);
                    }

                    if (dataSnapshot.hasChild("paymentAmountPaid")){
                        String ss = dataSnapshot.child("paymentAmountPaid").getValue().toString();
                        if (TextUtils.isEmpty(ss))
                            holder.amountPaid.setText("-");
                        else
                            holder.amountPaid.setText(ss);
                    }

                    if (dataSnapshot.hasChild("paymentConfirm")){
                        String ss = dataSnapshot.child("paymentConfirm").getValue().toString();
                        holder.paymentStatus.setText(ss);
                    }

                    if (dataSnapshot.hasChild("deliverySuccess")){
                        String ss = dataSnapshot.child("deliverySuccess").getValue().toString();
                        holder.deliveryStatus.setText(ss);
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
                Intent intent = new Intent(context, ShowCartViewOrderDetailsActivity.class);
                intent.putExtra("cartPostKey", key);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartOrderClassList.size();
    }

    public class OrderCartViewHolder extends RecyclerView.ViewHolder{
        public TextView date, time, orderStatus, amountPaid, paymentStatus, deliveryStatus;
        public View view;


        public OrderCartViewHolder(View mView){
            super(mView);
            view = mView;

            date = mView.findViewById(R.id.layoutShowCartViewOrder_date);
            time = mView.findViewById(R.id.layoutShowCartViewOrder_time);
            orderStatus = mView.findViewById(R.id.layoutShowCartViewOrder_orderStatus);
            amountPaid = mView.findViewById(R.id.layoutShowCartViewOrder_amountPaid);
            paymentStatus = mView.findViewById(R.id.layoutShowCartViewOrder_paymentStatus);
            deliveryStatus = mView.findViewById(R.id.layoutShowCartViewOrder_deliveryStatus);

        }
    }
}
