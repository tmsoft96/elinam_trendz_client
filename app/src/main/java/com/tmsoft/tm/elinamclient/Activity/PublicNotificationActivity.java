package com.tmsoft.tm.elinamclient.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.Holders.LastMessageSendTimeClass;
import com.tmsoft.tm.elinamclient.Holders.publicNotificationClass;
import com.tmsoft.tm.elinamclient.R;

public class PublicNotificationActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private RelativeLayout noNotification;
    private SwipeRefreshLayout refresh;
    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference;
    private String getCurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_notification);

        getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId);
        databaseReference.keepSynced(true);

        myToolBar = (Toolbar) findViewById(R.id.publicNotification_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Notification");

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();

        recyclerView = (RecyclerView) findViewById(R.id.publicNotification_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        noNotification = findViewById(R.id.publicNotification_noNotification);
        progressDialog = new ProgressDialog(this);

        refresh = findViewById(R.id.publicNotification_refresh);

        try {
            checkNotification();
            displayAllNotification();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "PublicNotificationActivity", "main method");
        }

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    checkNotification();
                    displayAllNotification();
                    refresh.setRefreshing(false);
                } catch (Exception e){
                    Log.i("error", e.getMessage());
                    catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                            "MainActivity", "main method");
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void checkNotification() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noNotification.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void displayAllNotification() {
        FirebaseRecyclerAdapter<publicNotificationClass, PublicNotificationViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<publicNotificationClass, PublicNotificationViewHolder>(
                        publicNotificationClass.class,
                        R.layout.layout_public_notification,
                        PublicNotificationViewHolder.class,
                        databaseReference
                ) {
                    @Override
                    protected void populateViewHolder(final PublicNotificationViewHolder viewHolder, final publicNotificationClass model, int position) {
                        final String title = model.getTitle();
                        final String userId = model.getUserId();
                        final String activityToGo = model.getActivityIoGo();
                        final String postKey = model.getPostKey();
                        final String key = getRef(position).getKey();
                        viewHolder.setRead(model.getRead());
                        viewHolder.setMessage(model.getMessage(), false);
                        viewHolder.setTime(model.getTime(), getApplicationContext());
                        viewHolder.setMessageTitle(model.getMessageTitle());

                       try{
                           {
                               new Thread(){
                                   @Override
                                   public void run() {
                                       if (TextUtils.isEmpty(model.getMessage()) && TextUtils.isEmpty(model.getMessageTitle())) {
                                           DatabaseReference datReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId);
                                           datReference.child(key).removeValue();
                                       }
                                   }
                               }.start();

                               if (TextUtils.isEmpty(title) || title.equalsIgnoreCase("delete")){
                                   viewHolder.setProfilePicture("");
                                   viewHolder.setMessage(model.getMessage(), true);
                               }
                               else if (title.equalsIgnoreCase("order") && !TextUtils.isEmpty(postKey)){
                                   if (activityToGo.equalsIgnoreCase("BuyForMeActivity")){
                                       DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child("Buy for me").child(postKey);
                                       dd.keepSynced(true);
                                       dd.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               if (dataSnapshot.exists()){
                                                   if (dataSnapshot.hasChild("productImage")){
                                                       viewHolder.setProfilePicture(dataSnapshot.child("productImage").getValue().toString());
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });
                                   }
                                   else if (activityToGo.equalsIgnoreCase("ViewProductOrderActivity")){
                                       DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child("Product Orders").child(postKey);
                                       dd.keepSynced(true);
                                       dd.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                               if (dataSnapshot.exists()){
                                                   if (dataSnapshot.hasChild("productPicture1")){
                                                       viewHolder.setProfilePicture(dataSnapshot.child("productPicture1").getValue().toString());
                                                   }
                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError databaseError) {

                                           }
                                       });
                                   }
                                   else
                                       viewHolder.setProfilePicture("cart");
                               } else if (title.equalsIgnoreCase("chat")){
                                   DatabaseReference dd = FirebaseDatabase.getInstance().getReference().child("Users");
                                   dd.keepSynced(true);
                                   dd.child(userId).addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.exists()){
                                               if (dataSnapshot.hasChild("profilePicture")){
                                                   viewHolder.setProfilePicture(dataSnapshot.child("profilePicture").getValue().toString());
                                               }
                                           }
                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError databaseError) {

                                       }
                                   });
                               }
                           }
                       } catch (Exception e){
                           Log.i("error", e.getMessage());
                       }

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.i("key", key);
                                if (isOnline()){
                                    progressDialog.setMessage("Loading page...");
                                    progressDialog.setCanceledOnTouchOutside(true);
                                    progressDialog.show();
                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(getCurrentUserId).child(key);
                                    ref.child("read").setValue("yes").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                if (title.equalsIgnoreCase("delete")){
                                                    showAlertDialog(model.getMessage(), model.getMessageTitle());
                                                    progressDialog.dismiss();
                                                } else {
                                                    userClickNotification(title, userId, activityToGo, postKey);
                                                    progressDialog.dismiss();
                                                }
                                               /* userClickNotification(title, userId, activityToGo, postKey);
                                                progressDialog.dismiss();*/
                                            } else {
                                                String err = task.getException().getMessage();
                                                if (title.equalsIgnoreCase("delete"))
                                                    showAlertDialog(model.getMessage(), model.getMessageTitle());
                                                else
                                                    userClickNotification(title, userId, activityToGo, postKey);
                                                Toast.makeText(PublicNotificationActivity.this, err + "\nPlease report this", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        }
                                    });
                                } else {
                                    Snackbar snackbar = Snackbar.make(view, "No internet connection and may delay performance", Snackbar.LENGTH_LONG)
                                            .setAction("Continue", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    userClickNotification(title, userId, activityToGo, postKey);
                                                }
                                            });
                                    snackbar.getView().setBackgroundColor(getApplicationContext().getResources().getColor(R.color.colorPrimaryDark));
                                    snackbar.show();
                                }
                            }
                        });
                    }
                };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showAlertDialog(String message, String title) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PublicNotificationActivity.this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Home Page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                sendUserToActivity("postKey", "mainNotification", MainActivity.class);
            }
        }).setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void userClickNotification(String title, String userId, String activityToGo, String postKey) {
        if (title.equalsIgnoreCase("order")){
            if (!TextUtils.isEmpty(activityToGo) && !TextUtils.isEmpty(postKey)){
                if (activityToGo.equalsIgnoreCase("BuyForMeActivity"))
                    sendUserToActivity("postKey", postKey, ViewBuyForMeActivity.class);
                else if (activityToGo.equalsIgnoreCase("ViewProductOrderActivity"))
                    sendUserToActivity("productOrderKey", postKey, ViewProductOrderDetailsActivity.class);
                else if (activityToGo.equalsIgnoreCase("ShowCartOrderedDetailsActivity"))
                    sendUserToActivity("cartPostKey", postKey, ShowCartViewOrderDetailsActivity.class);
            } else if (!TextUtils.isEmpty(activityToGo) && TextUtils.isEmpty(postKey)){
                if (activityToGo.equalsIgnoreCase("BuyForMeActivity"))
                    sendUserToActivity("postKey", "order", MainActivity.class);
                else if (activityToGo.equalsIgnoreCase("ViewProductOrderDetailsActivity"))
                    sendUserToActivity("postKey", "order", MainActivity.class);
                else if (activityToGo.equalsIgnoreCase("ShowCartViewOrderDetailsActivity"))
                    sendUserToActivity("postKey", "order", MainActivity.class);
            } else
                Toast.makeText(PublicNotificationActivity.this, "Please report this", Toast.LENGTH_LONG).show();
        } else if (title.equalsIgnoreCase("chat")){
            if (!TextUtils.isEmpty(userId))
                sendUserToActivity("userId", userId, ChatActivity.class);
            else
                Toast.makeText(PublicNotificationActivity.this, "Please report this", Toast.LENGTH_LONG).show();
        } else
            sendUserToActivity("postKey", "mainNotification", MainActivity.class);
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void sendUserToActivity(String name, String postKey, Class classToGo){
        Intent intent = new Intent(PublicNotificationActivity.this, classToGo);
        intent.putExtra(name, postKey);
        startActivity(intent);
    }

    public static class PublicNotificationViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public PublicNotificationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setMessageTitle(String messageTitle) {
            TextView tt = mView.findViewById(R.id.layoutNotification_messageTitle);
            tt.setText(messageTitle);
        }

        public void setTime(long time, Context context) {
            TextView tt = mView.findViewById(R.id.layoutNotification_time);
            String lastSeenDisplayTime = LastMessageSendTimeClass.getLastTimeAgo(time, context);
            tt.setText(lastSeenDisplayTime);
        }

        public void setMessage(String message, boolean publicNotification) {
            TextView msg = mView.findViewById(R.id.layoutNotification_message);
            if (publicNotification){
                int len = message.length();
                if (len >= 47){
                    String newMsg = message.substring(0,42) + "...";
                    msg.setText(newMsg);
                } else
                    msg.setText(message);
            } else
                msg.setText(message);
        }

        public void setRead(String read) {
            LinearLayout linearLayout = mView.findViewById(R.id.layoutNotification_linear);
            if (read.equalsIgnoreCase("no"))
                linearLayout.setBackgroundResource(R.color.unread);
            else
                linearLayout.setBackgroundResource(R.color.white);
        }

        public void setProfilePicture(final String profilePic){
            final ImageView pp = mView.findViewById(R.id.layoutNotification_picture);
            try{
                if (profilePic.equalsIgnoreCase("")){
                    pp.setImageResource(R.drawable.notify_public_notification);
                } else if (profilePic.equalsIgnoreCase("cart"))
                    pp.setImageResource(R.drawable.cart_noti);
                else {
                    //loading picture offline
                    Picasso.get()
                            .load(profilePic)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.warning)
                            .into(pp, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(profilePic)
                                            .placeholder(R.drawable.warning)
                                            .into(pp);
                                }
                            });
                }
            } catch (Exception e){
                pp.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            sendUserToMainActivity();
        }

        return false;
    }

    private void sendUserToMainActivity() {
        Intent intent = new Intent(PublicNotificationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            sendUserToMainActivity();
        }

        return super.onOptionsItemSelected(item);
    }

}
