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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Activity.ShowShopProductActivity;
import com.tmsoft.tm.elinamclient.Holders.ShopProductClass;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    private List<ShopProductClass> shopProductClassList;
    private Context context;
    private int layoutWidth;
    private String shopType;

    private DatabaseReference databaseReference;

    public ShopAdapter(List<ShopProductClass> shopProductClassList, Context context, int layoutWidth, String shopType){
        this.shopProductClassList = shopProductClassList;
        this.context = context;
        this.layoutWidth = layoutWidth;
        this.shopType = shopType;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_all_shop_display, parent, false);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShopViewHolder holder, int position) {
        ShopProductClass shopProductClass = shopProductClassList.get(position);

        final String shopNumber = shopProductClass.getShopNumber();
        String shopName = shopProductClass.getShopName();
        final String shopLogo = shopProductClass.getShopLogo();

        String shopItems = null;
        if (shopType.equalsIgnoreCase("Made In Ghana"))
            shopItems = shopProductClass.getShopItemsMIG();
        else if (shopType.equalsIgnoreCase("Manufactures"))
            shopItems = shopProductClass.getShopItemsManu();

        //loading picture offline
        try{
            Picasso.get()
                    .load(shopLogo).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.warning).into(holder.shopLogo, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(shopLogo).fit()
                            .placeholder(R.drawable.warning)
                            .into(holder.shopLogo);
                }
            });
        } catch (Exception ex){

        }

        holder.shopName.setText(shopName + "(" + shopItems + ")");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent clickPostIntent = new Intent(view.getContext(), ShowShopProductActivity.class);
                clickPostIntent.putExtra("category", "Made In Ghana");
                clickPostIntent.putExtra("key", shopNumber);
                context.startActivity(clickPostIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shopProductClassList.size();
    }


    public class ShopViewHolder extends RecyclerView.ViewHolder{
        public TextView shopName;
        public ImageView shopLogo;
        public LinearLayout linearLayout;

        public ShopViewHolder(View mView){
            super(mView);

            shopName = mView.findViewById(R.id.layoutAllShop_name);
            shopLogo = mView.findViewById(R.id.layoutAllShop_image);
            linearLayout = mView.findViewById(R.id.layoutAllShop_relative);

            int width = linearLayout.getLayoutParams().width;
            int height = linearLayout.getLayoutParams().height;
            Log.i("width", width+ "");

            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(layoutWidth, height));
        }
    }
}
