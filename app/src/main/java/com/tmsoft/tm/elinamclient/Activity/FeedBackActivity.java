package com.tmsoft.tm.elinamclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Holders.LastMessageSendTimeClass;
import com.tmsoft.tm.elinamclient.Holders.feedbackClass;
import com.tmsoft.tm.elinamclient.R;
import com.tmsoft.tm.elinamclient.Urls.InnerNotification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedBackActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView recyclerView;
    private EditText feedbackText;
    private ImageView feedbackSend;
    private ProgressDialog progressDialog;
    private RelativeLayout noFeedback;
    private SwipeRefreshLayout refresh;

    private InnerNotification innerNotification;
    private ArrayList<String> notifySenderList;

    private String notifySender1, notifySender2, notifySender3;

    private String userId;
    private long numOfPost = 0;
    private int num;

    private DatabaseReference databaseReference, userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        userId = getIntent().getExtras().get("userId").toString();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Feedback");
        databaseReference.keepSynced(true);
        userReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userReference.keepSynced(true);

        myToolBar = findViewById(R.id.feedback_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Feedback");

        feedbackSend = findViewById(R.id.feedback_sendText);
        feedbackText = findViewById(R.id.feedback_enterFeedback);
        progressDialog = new ProgressDialog(this);
        noFeedback = findViewById(R.id.feedback_noFeedback);
        refresh = findViewById(R.id.feedback_refresh);
        notifySenderList = new ArrayList<>();

        DatabaseReference notifySender = FirebaseDatabase.getInstance().getReference().child("ServerId");
        notifySender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("0")){
                        notifySender1 = dataSnapshot.child("0").getValue().toString();
                        notifySenderList.add(notifySender1);
                    }

                    if (dataSnapshot.hasChild("1")){
                        notifySender2 = dataSnapshot.child("1").getValue().toString();
                        notifySenderList.add(notifySender2);
                    }

                    if (dataSnapshot.hasChild("2")){
                        notifySender3 = dataSnapshot.child("2").getValue().toString();
                        notifySenderList.add(notifySender3);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        feedbackSend.setVisibility(View.INVISIBLE);
        feedbackText.setVisibility(View.INVISIBLE);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (currentUserId.equals(userId)) {
            feedbackSend.setVisibility(View.VISIBLE);
            feedbackText.setVisibility(View.VISIBLE);
        }

        recyclerView = findViewById(R.id.feedback_recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount() - 1);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    numOfPost = dataSnapshot.getChildrenCount();
                } else {
                    numOfPost = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        checkFeedback();
        showAllFeedback();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkFeedback();
                showAllFeedback();
                refresh.setRefreshing(false);
            }
        });

        feedbackSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = feedbackText.getText().toString();

                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(FeedBackActivity.this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
                } else {
                    saveFeedBack(text);
                }
            }
        });
    }

    private void checkFeedback() {
        Query query = databaseReference.orderByChild("userId")
                .startAt(userId).endAt(userId + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noFeedback.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showAllFeedback() {
        Query query = databaseReference.orderByChild("userId")
                .startAt(userId).endAt(userId + "\uf8ff");

        FirebaseRecyclerAdapter<feedbackClass, feedbackViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<feedbackClass, feedbackViewHolder>(
                feedbackClass.class,
                R.layout.layout_feedback,
                feedbackViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final feedbackViewHolder viewHolder, feedbackClass model, int position) {
                viewHolder.setTime(model.getTime(), getApplicationContext());
                viewHolder.setFeedback(model.getFeedback());

                userReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("profilePicture")) {
                                String pP = dataSnapshot.child("profilePicture").getValue().toString();
                                viewHolder.setProfilePicture(pP, getApplicationContext());
                            }

                            if (dataSnapshot.hasChild("fullName")) {
                                String fN = dataSnapshot.child("fullName").getValue().toString();
                                viewHolder.setUserName(fN);
                            }

                            if (dataSnapshot.hasChild("phoneNumber")) {
                                String pN = dataSnapshot.child("phoneNumber").getValue().toString();
                                viewHolder.setPhoneNumber(pN);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class feedbackViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public feedbackViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setFeedback(String feedback) {
            TextView mFeedbackMessage = mView.findViewById(R.id.feedback_message);
            mFeedbackMessage.setText(feedback);
        }

        public void setTime(long time, Context context) {
            LastMessageSendTimeClass lastMessageSendTimeClass = new LastMessageSendTimeClass();
            String getTime = LastMessageSendTimeClass.getLastTimeAgo(time, context);

            TextView mTime = mView.findViewById(R.id.feedbackDate);
            mTime.setText(getTime);
        }

        public void setProfilePicture(final String profilePicture, final Context context) {
            final CircleImageView mProfilePicture = mView.findViewById(R.id.feedback_profilePicture);
            //loading picture offline
            Picasso.
                    get()
                    .load(profilePicture)
                    .fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_image)
                    .into(mProfilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso
                            .get()
                            .load(profilePicture)
                            .fit()
                            .placeholder(R.drawable.profile_image)
                            .into(mProfilePicture);
                }
            });
        }

        public void setUserName(String userName) {
            TextView mUsername = mView.findViewById(R.id.feedback_userName);
            mUsername.setText(userName);
        }

        public void setPhoneNumber(String phoneNumber) {
            TextView mPhoneNumber = mView.findViewById(R.id.feedback_phoneNumber);
            mPhoneNumber.setText(phoneNumber);
        }
    }

    private void saveFeedBack(final String text) {
        progressDialog.setMessage("Saving...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        final Map feedBackMap = new HashMap();
        feedBackMap.put("userId", userId);
        feedBackMap.put("feedback", text);
        feedBackMap.put("time", ServerValue.TIMESTAMP);
        feedBackMap.put("number", numOfPost);

        Calendar postTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
        String time = currentTimeFormat.format(postTime.getTime());

        final String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(feedBackMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String[] noti = new String[notifySenderList.size()];
                    for(int x = 0; x < noti.length; ++x){
                        noti[x] = notifySenderList.get(x);
                    }

                    innerNotification = new InnerNotification(
                            "feedback",
                            "no",
                            userId,
                            "FeedbackActivity",
                            ServerValue.TIMESTAMP.toString(),
                            text,
                            "Feedback from a user",
                            notifySenderList,
                            key
                    );
                    boolean deter = innerNotification.onSaveAll();
                    if (deter){
                        feedbackText.setText("");
                        progressDialog.dismiss();
                    } else {
                        feedbackText.setText("");
                        progressDialog.dismiss();
                    }
                } else {
                    String err = task.getException().toString();
                    Toast.makeText(FeedBackActivity.this, err + "\nTry Again ...", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
