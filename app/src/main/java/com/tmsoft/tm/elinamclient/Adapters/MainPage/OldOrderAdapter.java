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

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Activity.ViewProductOrderDetailsActivity;
import com.tmsoft.tm.elinamclient.Holders.productOrderOldList;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

public class OldOrderAdapter extends RecyclerView.Adapter<OldOrderAdapter.OldOrderViewHolder> {
    private List<productOrderOldList> productOrderClassList;
    private Context context;


    public OldOrderAdapter(List<productOrderOldList> productOrderClassList, Context context){
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
        productOrderOldList orderClass = productOrderClassList.get(position);

        final String productId = orderClass.getProductId();
        final String picture = orderClass.getProductPicture1();
        String name = orderClass.getProductName();
        String price = orderClass.getProductPrice();
        String orderConfirm = orderClass.getOrderConfirm();
        String orderDate = orderClass.getOrderDate();
        String orderTime = orderClass.getOrderTime();

        //loading picture offline
        Picasso
                .get()
                .load(picture).fit()
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
                        .load(picture).fit()
                        .placeholder(R.drawable.warning)
                        .into(holder.pPicture);
            }
        });

        holder.pName.setText(name);
        holder.pOrderConfirm.setText(orderConfirm);
        holder.pPrice.setText(price);
        holder.pOrderTime.setText(orderTime);
        holder.pOrderDate.setText(orderDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewProductOrderDetailsActivity.class);
                intent.putExtra("productOrderKey", productId);
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
        public TextView pName, pPrice, pOrderConfirm, pOrderTime, pOrderDate;
        public View view;


        public OldOrderViewHolder(View mView){
            super(mView);
            view = mView;

            pPicture = mView.findViewById(R.id.viewProductOrder_productPicture);
            pName = mView.findViewById(R.id.viewProductOrder_productName);
            pPrice = mView.findViewById(R.id.viewProductOrder_productPrice);
            pOrderConfirm = mView.findViewById(R.id.viewProductOrder_confirmMessage);
            pOrderDate = mView.findViewById(R.id.viewProductOrder_date);
            pOrderTime = mView.findViewById(R.id.viewProductOrder_time);
        }
    }
}
