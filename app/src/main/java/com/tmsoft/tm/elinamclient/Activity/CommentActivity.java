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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.Holders.commentElement;
import com.tmsoft.tm.elinamclient.R;
import com.tmsoft.tm.elinamclient.Urls.InnerNotification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private RecyclerView commentRecyclerView;
    private EditText commentText;
    private ImageButton commentButton;
    private ProgressDialog progressDialog;
    private RelativeLayout noComment;
    private SwipeRefreshLayout refresh;

    private DatabaseReference commentReference, commentView, userPostReference, notificationReference;
    private String getCurrentUserId;
    private FirebaseAuth mAuth;

    private String postKey, commentFullName, commentProfilePicture, commentDate, commentTime, productName = "";
    private String notifySender1, notifySender2, notifySender3;

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;
    private InnerNotification innerNotification;
    private ArrayList<String> notifySenderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = FirebaseAuth.getInstance();
        getCurrentUserId = mAuth.getCurrentUser().getUid();
        commentReference = FirebaseDatabase.getInstance().getReference().child("Comments");
        commentReference.keepSynced(true);
        userPostReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userPostReference.keepSynced(true);
        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();

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

        postKey = getIntent().getExtras().get("postKey").toString();

        try{
            productName = getIntent().getExtras().get("productName").toString();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "CommentActivity", "main method");
        }

        commentButton = findViewById(R.id.comment_commentButton);
        commentText = findViewById(R.id.comment_commentTextBox);
        progressDialog = new ProgressDialog(this);
        noComment = findViewById(R.id.comment_noComment);
        refresh = findViewById(R.id.comment_refresh);

        notifySenderList = new ArrayList<>();

        myToolBar = findViewById(R.id.comment_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Comments");

        commentRecyclerView = findViewById(R.id.comment_recyclerView);
        commentRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentRecyclerView.setLayoutManager(linearLayoutManager);

        try{
            //displaying all post comment
            displayAllPostComment(postKey);
            checkComments(postKey);

            refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    displayAllPostComment(postKey);
                    checkComments(postKey);
                    refresh.setRefreshing(false);
                }
            });
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "CommentActivity", "main method");
        }

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comments = commentText.getText().toString();

                if(TextUtils.isEmpty(comments)) {
                    Toast.makeText(CommentActivity.this, "Please enter your comment", Toast.LENGTH_SHORT).show();
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
                else {
                    addUserComment(postKey, comments);
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        });
    }

    private void checkComments(String postKey) {
        commentView = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        commentView.keepSynced(true);
        commentView.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    int num = (int) dataSnapshot.getChildrenCount();

                    if (num > 0)
                        noComment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    //comment adding
    private void addUserComment(final String postKey, final String comments) {
       try{
           progressDialog.setTitle("Uploading Comment");
           progressDialog.setMessage("Please wait patiently while your comment is uploaded");
           progressDialog.setCanceledOnTouchOutside(true);
           progressDialog.show();

           userPostReference.child(getCurrentUserId).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   if (dataSnapshot.exists()){
                       if(dataSnapshot.hasChild("fullName")){
                           commentFullName = dataSnapshot.child("fullName").getValue().toString();
                       }
                       if(dataSnapshot.hasChild("profilePicture")){
                           commentProfilePicture = dataSnapshot.child("profilePicture").getValue().toString();
                       }

                       Calendar date = Calendar.getInstance();
                       SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
                       commentDate = currentDateFormat.format(date.getTime());

                       Calendar time = Calendar.getInstance();
                       SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm");
                       commentTime = currentTimeFormat.format(time.getTime());

                       HashMap<String, String> commentMap = new HashMap<>();
                       commentMap.put("commentUserId", getCurrentUserId);
                       commentMap.put("commentUsername", commentFullName);
                       commentMap.put("commentProfilePicture", commentProfilePicture);
                       commentMap.put("commentDate", commentDate);
                       commentMap.put("commentTime", commentTime);
                       commentMap.put("comment", comments);
                       commentMap.put("postKet", postKey);

                       commentReference.child(postKey).push().setValue(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if(task.isSuccessful()){
                                   Toast.makeText(CommentActivity.this, "Comment uploaded successfully", Toast.LENGTH_SHORT).show();

                                   String tt = commentFullName + " comment on a product";

                                   //sending notification
                                   final HashMap<String, String> notificationData = new HashMap<>();
                                   notificationData.put("from", getCurrentUserId);
                                   notificationData.put("title", tt);
                                   notificationData.put("message", comments);

                                   notificationReference.child(notifySender1).push().setValue(notificationData)
                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if (task.isSuccessful()){
                                                       if (!TextUtils.isEmpty(notifySender2)){
                                                           notificationReference.child(notifySender2).push().setValue(notificationData)
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {

                                                                       }
                                                                   });
                                                       }

                                                       if (!TextUtils.isEmpty(notifySender3)){
                                                           notificationReference.child(notifySender3).push().setValue(notificationData)
                                                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                       @Override
                                                                       public void onComplete(@NonNull Task<Void> task) {

                                                                       }
                                                                   });
                                                       }

                                                       String[] noti = new String[notifySenderList.size()];
                                                       for(int x = 0; x < noti.length; ++x){
                                                           noti[x] = notifySenderList.get(x);
                                                       }

                                                        innerNotification = new InnerNotification(
                                                                "comment",
                                                                "no",
                                                                getCurrentUserId,
                                                                "ViewProductDetailsActivity",
                                                                ServerValue.TIMESTAMP.toString(),
                                                                comments,
                                                                commentFullName + " comment on " + productName,
                                                                notifySenderList,
                                                                postKey
                                                        );
                                                       boolean deter = innerNotification.onSaveAll();
                                                       if (deter){
                                                           commentText.setText("");
                                                           progressDialog.dismiss();
                                                       } else {
                                                           commentText.setText("");
                                                           progressDialog.dismiss();
                                                       }
                                                   } else {
                                                       String er = task.getException().getMessage();
                                                       Toast.makeText(CommentActivity.this, er, Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });

                               } else {
                                   String errorMessage = task.getException().getMessage();
                                   Toast.makeText(CommentActivity.this, "Error Occurred... " + errorMessage
                                           + "\nPlease try again", Toast.LENGTH_SHORT).show();
                                   progressDialog.dismiss();
                               }
                           }
                       });
                   }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
           });
       } catch (Exception e){
           Log.i("error", e.getMessage());
           catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                   "CommentActivity", "addUserComment method");
       }

    }

    //displaying all post comment
    private void displayAllPostComment(String postKey) {
        commentView = FirebaseDatabase.getInstance().getReference().child("Comments").child(postKey);
        commentView.keepSynced(true);

        FirebaseRecyclerAdapter<commentElement, commentViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<commentElement, commentViewHolder>(
                        commentElement.class,
                        R.layout.layout_comment_view,
                        commentViewHolder.class,
                        commentView

                ) {
                    @Override
                    protected void populateViewHolder(commentViewHolder viewHolder, commentElement model, int position) {
                        viewHolder.setComment(model.getComment());
                        viewHolder.setCommentDate(model.getCommentDate());
                        viewHolder.setCommentTime(model.getCommentTime());
                        viewHolder.setCommentProfilePicture(getApplicationContext(), model.getCommentProfilePicture());
                        viewHolder.setCommentUsername(model.getCommentUsername());
                    }
                };
        commentRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class commentViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public commentViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setCommentUsername(String commentUsername) {
            TextView userName = mView.findViewById(R.id.commentUserName);
            int aa = commentUsername.length();
            if (aa > 2){
                String name = commentUsername.substring(0, 2);
                userName.setText(name + "***");
            } else
                userName.setText(commentUsername);
        }

        public void setCommentProfilePicture(final Context context, final String commentProfilePicture) {
            final CircleImageView profilePicture = mView.findViewById(R.id.commentProfilePicture);
            //loading picture offline
            Picasso.get()
                    .load(commentProfilePicture).fit()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.profile_image)
                    .into(profilePicture, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get()
                            .load(commentProfilePicture).fit()
                            .placeholder(R.drawable.profile_image)
                            .into(profilePicture);
                }
            });
        }

        public void setCommentDate(String commentDate) {
            TextView commentDateSet = mView.findViewById(R.id.commentDateDate);
            commentDateSet.setText(commentDate);
        }

        public void setCommentTime(String commentTime) {
            TextView commentTimeSet = mView.findViewById(R.id.commentTimeTime);
            commentTimeSet.setText(commentTime);
        }

        public void setComment(String comment) {
            TextView commentComent = mView.findViewById(R.id.realComment);
            commentComent.setText(comment);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
