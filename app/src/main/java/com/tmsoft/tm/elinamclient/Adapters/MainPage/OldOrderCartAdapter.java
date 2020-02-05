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

import com.tmsoft.tm.elinamclient.Activity.ShowCartViewOrderDetailsActivity;
import com.tmsoft.tm.elinamclient.Holders.cartViewOldOrder;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

public class OldOrderCartAdapter extends RecyclerView.Adapter<OldOrderCartAdapter.OldOrderCartViewHolder> {
    private List<cartViewOldOrder> cartOrderOldClassList;
    private Context context;


    public OldOrderCartAdapter(List<cartViewOldOrder> cartOrderOldClassList, Context context){
        this.cartOrderOldClassList = cartOrderOldClassList;
        this.context = context;
    }

    @NonNull
    @Override
    public OldOrderCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_show_cart_view_order, parent, false);

        return new OldOrderCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OldOrderCartViewHolder holder, int position) {
        cartViewOldOrder orderOldClass = cartOrderOldClassList.get(position);

        final String cartPostKey = orderOldClass.getProductId();
        String pDate = orderOldClass.getOrderDate();
        String pTime = orderOldClass.getOrderTime();
        String pOrderStatus = orderOldClass.getOrderConfirm();
        String pAmountPaid = orderOldClass.getPaymentAmountPaid();
        String pPaymentStatus = orderOldClass.getPaymentAmountPaid();
        String pDeliveryStatus = orderOldClass.getDeliverySuccess();

        holder.date.setText(pDate);
        holder.time.setText(pTime);
        holder.orderStatus.setText(pOrderStatus);

        if (TextUtils.isEmpty(pAmountPaid))
            holder.amountPaid.setText("-");
        else
            holder.amountPaid.setText(pAmountPaid);

        holder.paymentStatus.setText(pPaymentStatus);
        holder.deliveryStatus.setText(pDeliveryStatus);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ShowCartViewOrderDetailsActivity.class);
                intent.putExtra("cartPostKey", cartPostKey);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartOrderOldClassList.size();
    }

    public class OldOrderCartViewHolder extends RecyclerView.ViewHolder{
        public TextView date, time, orderStatus, amountPaid, paymentStatus, deliveryStatus;
        public View view;


        public OldOrderCartViewHolder(View mView){
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
