package com.tmsoft.tm.elinamclient.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Holders.LastMessageSendTimeClass;
import com.tmsoft.tm.elinamclient.Holders.MessagesClass;
import com.tmsoft.tm.elinamclient.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private List<MessagesClass> userMessageList;
    private Context context;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    public MessageAdapter(List<MessagesClass> userMessageList, Context context){
        this.userMessageList = userMessageList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_single_chat, parent, false);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.keepSynced(true);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        MessagesClass message = userMessageList.get(position);

        String getMessageSenderId = mAuth.getCurrentUser().getUid();
        String fromUserId = message.getFrom();
        String actualSenderId = message.getActualSenderId();
        final String type = message.getType();
        final long time = message.getTime();

        if (getMessageSenderId.equals(fromUserId)){
            holder.messageTextRight.setVisibility(View.VISIBLE);
            holder.messageDateRight.setVisibility(View.VISIBLE);
            holder.messageDate.setVisibility(View.GONE);
            holder.messageText.setVisibility(View.GONE);
            holder.messageProfilePicture.setVisibility(View.INVISIBLE);
        } else {
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageDate.setVisibility(View.VISIBLE);
            holder.messageDateRight.setVisibility(View.GONE);
            holder.messageTextRight.setVisibility(View.GONE);
            holder.messageProfilePicture.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(actualSenderId)){
            databaseReference.child("Users").child(actualSenderId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("profilePicture")){
                        final String pp = dataSnapshot.child("profilePicture").getValue().toString();
                        Picasso
                                .get()
                                .load(pp)
                                .fit()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.profile_image)
                                .into(holder.messageProfilePicture, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso
                                                .get()
                                                .load(pp)
                                                .fit()
                                                .placeholder(R.drawable.profile_image)
                                                .into(holder.messageProfilePicture);
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        //setting the InnerMessage
        holder.messageText.setText(message.getMessage());
        holder.messageTextRight.setText(message.getMessage());

        //setting date
        try{
            LastMessageSendTimeClass lastSeenTimeClass = new LastMessageSendTimeClass();

            String lastSeenDisplayTime = LastMessageSendTimeClass.getLastTimeAgo(time, context);

            holder.messageDateRight.setText(lastSeenDisplayTime);
            holder.messageDate.setText(lastSeenDisplayTime);
        } catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return userMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        public TextView messageText, messageTextRight, messageDate, messageDateRight;
        public CircleImageView messageProfilePicture;

        public MessageViewHolder(View mView) {
            super(mView);

            messageText = mView.findViewById(R.id.messageLayout_displayMessage);
            messageTextRight = mView.findViewById(R.id.messageLayout_displayMessageRight);
            messageProfilePicture = mView.findViewById(R.id.messagesLayout_profilePicture);
            messageDate = mView.findViewById(R.id.messageLayout_date);
            messageDateRight = mView.findViewById(R.id.messageLayout_dateRight);
        }


    }
}
