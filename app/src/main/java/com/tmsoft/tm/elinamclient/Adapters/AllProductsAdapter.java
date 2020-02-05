package com.tmsoft.tm.elinamclient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Activity.ViewProductDetailsActivity;
import com.tmsoft.tm.elinamclient.Holders.allProductsClass;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

public class AllProductsAdapter extends RecyclerView.Adapter<AllProductsAdapter.NowAvailableViewHolder> {
    private List<allProductsClass> productsClassList;
    private Context context;
    private int layoutWidth, textWidth;

    private DatabaseReference databaseReference;

    public AllProductsAdapter(List<allProductsClass> productsClassList, Context context, int layoutWidth, int textWidth){
        this.productsClassList = productsClassList;
        this.context = context;
        this.layoutWidth = layoutWidth;
        this.textWidth = textWidth;
    }

    @NonNull
    @Override
    public NowAvailableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_all_post_display, parent, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        return new NowAvailableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NowAvailableViewHolder holder, int position) {
        allProductsClass productsClass = productsClassList.get(position);

        String pProductName = productsClass.getProductName();
        String pProductPrice = productsClass.getProductPrice();
        final String pProductPicture = productsClass.getProductPicture1();
        String discount = productsClass.getProductDiscount();
        String discountPrice = productsClass.getProductDiscountPrice();
        String discountPercentage = productsClass.getProductDiscountPercentage();
        String pProductCategory = productsClass.getProductCategory();

        final String postKey = productsClass.getProductKey();

        int len = pProductName.length();
        if (len >= (textWidth - 5))
            holder.productName.setText(pProductName.substring(0, (len - 3)) + "...");
        else
            holder.productName.setText(pProductName);

        try {
            if (pProductCategory.equals("Global Trending"))
                holder.plane.setVisibility(View.VISIBLE);
            else
                holder.plane.setVisibility(View.GONE);
        } catch (Exception e){
            holder.plane.setVisibility(View.GONE);
        }

        try{
            if (discount.equals("no")){
                holder.productPrice2.setVisibility(View.VISIBLE);
                holder.priceRelative.setVisibility(View.GONE);
                holder.priceConceal.setVisibility(View.GONE);
                holder.discountPrice.setVisibility(View.GONE);
                holder.discountPercent.setVisibility(View.GONE);
                holder.productPrice.setText(pProductPrice);
                holder.productPrice2.setText(pProductPrice);
            } else if (discount.equals("yes")){
                holder.productPrice2.setVisibility(View.GONE);
                holder.priceRelative.setVisibility(View.VISIBLE);
                holder.priceConceal.setVisibility(View.VISIBLE);
                holder.discountPrice.setVisibility(View.VISIBLE);
                holder.discountPercent.setVisibility(View.VISIBLE);
                holder.productPrice.setText(pProductPrice);
                holder.discountPrice.setText(discountPrice);
                holder.discountPercent.setText(discountPercentage);
            }
        } catch (Exception ex){
            Log.i("error", ex.getMessage());
            holder.priceConceal.setVisibility(View.GONE);
            holder.discountPrice.setVisibility(View.GONE);
            holder.discountPercent.setVisibility(View.GONE);
            holder.productPrice.setText(pProductPrice);
        }


        Log.i("textName", holder.productName.getTextSize() + "");
        Log.i("textDiscount", holder.discountPrice.getTextSize() + "");
        Log.i("textPercent", holder.discountPercent.getTextSize() + "");


        //loading picture offline
        Picasso.get()
                .load(pProductPicture).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning).into(holder.productPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get()
                        .load(pProductPicture).fit()
                        .placeholder(R.drawable.warning)
                        .into(holder.productPicture);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clickPostIntent = new Intent(view.getContext(), ViewProductDetailsActivity.class);
                clickPostIntent.putExtra("postKey", postKey);
                context.startActivity(clickPostIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsClassList.size();
    }


    public class NowAvailableViewHolder extends RecyclerView.ViewHolder{
        public TextView productName, productPrice, productPrice2, discountPercent, discountPrice;
        public ImageView productPicture, priceConceal, plane;
        public RelativeLayout relativeLayout, priceRelative;

        public NowAvailableViewHolder(View mView){
            super(mView);

            productName = mView.findViewById(R.id.allPost_productName);
            productPrice = mView.findViewById(R.id.allPost_productPrice);
            productPrice2 = mView.findViewById(R.id.allPost_productPrice2);
            productPicture = mView.findViewById(R.id.allPost_productImage);
            discountPercent = mView.findViewById(R.id.allPost_productDiscountPercent);
            discountPrice = mView.findViewById(R.id.allPost_productDiscountPrice);
            priceConceal = mView.findViewById(R.id.allPost_priceConceal);
            relativeLayout = mView.findViewById(R.id.allPost_relative);
            priceRelative = mView.findViewById(R.id.allPost_relativePrice);
            plane = mView.findViewById(R.id.allPost_plane);

            int width = relativeLayout.getLayoutParams().width;
            int height = relativeLayout.getLayoutParams().height;
            Log.i("width", width+ "");

            productPrice2.setVisibility(View.GONE);

            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(layoutWidth, height));
        }
    }
}
