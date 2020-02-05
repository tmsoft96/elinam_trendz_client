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

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Activity.ViewProductDetailsActivity;
import com.tmsoft.tm.elinamclient.Holders.allProductsClass;
import com.tmsoft.tm.elinamclient.R;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
    private Context context;
    private ArrayList<allProductsClass> searchList;
    private ArrayList<String> key;
    private int layoutWidth, textWidth;


    public SearchAdapter(Context context, ArrayList<allProductsClass> searchList, ArrayList<String> key, int layoutWidth, int textWidth) {
        this.context = context;
        this.searchList = searchList;
        this.key = key;
        this.layoutWidth = layoutWidth;
        this.textWidth = textWidth;
    }
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_all_post_display, parent, false);

        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchViewHolder holder, final int position) {

        String discount = searchList.get(position).getProductDiscount();
        String pProductCategory = searchList.get(position).getProductCategory();
        //final String postKey = priceSearchClass.getProductKey();


        String realName = searchList.get(position).getProductName();
        Log.i("SearchValue", "Name: " + realName);

        int len = realName.length();
        if (len >= (textWidth - 5))
            holder.productName.setText(realName.substring(0, (len - 3)) + "...");
        else
            holder.productName.setText(realName);

        try {
            if (pProductCategory.equals("Global Trending"))
                holder.plane.setVisibility(View.VISIBLE);
            else
                holder.plane.setVisibility(View.GONE);
        } catch (Exception e){
            holder.plane.setVisibility(View.GONE);
        }

        if (discount.equals("no")){
            holder.productPrice2.setVisibility(View.VISIBLE);
            holder.priceRelative.setVisibility(View.GONE);
            holder.priceConceal.setVisibility(View.GONE);
            holder.discountPrice.setVisibility(View.GONE);
            holder.discountPercent.setVisibility(View.GONE);
            holder.productPrice.setText(searchList.get(position).getProductPrice());
            holder.productPrice2.setText(searchList.get(position).getProductPrice());
        } else if (discount.equals("yes")){
            holder.productPrice2.setVisibility(View.GONE);
            holder.priceRelative.setVisibility(View.VISIBLE);
            holder.priceConceal.setVisibility(View.VISIBLE);
            holder.discountPrice.setVisibility(View.VISIBLE);
            holder.discountPercent.setVisibility(View.VISIBLE);
            holder.productPrice.setText(searchList.get(position).getProductPrice());
            holder.discountPrice.setText(searchList.get(position).getProductDiscountPrice());
            holder.discountPercent.setText(searchList.get(position).getProductDiscountPercentage());
        }

        //loading picture offline
        Picasso
                .get()
                .load(searchList.get(position).getProductPicture1()).fit()
                .networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.warning)
                .into(holder.productPicture, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso
                        .get()
                        .load(searchList.get(position).getProductPicture1())
                        .fit()
                        .placeholder(R.drawable.warning)
                        .into(holder.productPicture);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clickPostIntent = new Intent(view.getContext(), ViewProductDetailsActivity.class);
                clickPostIntent.putExtra("category", searchList.get(position).getProductCategory());
                clickPostIntent.putExtra("postKey", key.get(position));
                context.startActivity(clickPostIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{
        public TextView productName, productPrice, productPrice2, discountPercent, discountPrice;
        public ImageView productPicture, priceConceal, plane;
        public View view;
        public RelativeLayout relativeLayout, priceRelative;


        public SearchViewHolder(View mView){
            super(mView);
            view = mView;

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
