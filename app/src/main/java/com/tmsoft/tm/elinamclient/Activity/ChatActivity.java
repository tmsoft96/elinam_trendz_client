package com.tmsoft.tm.elinamclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.Adapters.MessageAdapter;
import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.Holders.MessagesClass;
import com.tmsoft.tm.elinamclient.R;
import com.tmsoft.tm.elinamclient.Urls.InnerNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText writeMessage;
    private ImageButton button;
    private RecyclerView recyclerView;
    private Toolbar myToolBar;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private String senderId, receiverId;
    private DatabaseReference rootReference, notificationReference, databaseReference, serverNotiReference;
    private DatabaseReference onlineReference;
    private String fullName, key;
    private long counter = 0;

    private final List<MessagesClass> messagesClassList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private String notifySender1, notifySender2, notifySender3;
    private String serverStatus = "offline";

    private CatchErrors catchErrors;
    private DateAndTime dateAndTime;
    private InnerNotification innerNotification;
    private ArrayList<String> notifySenderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        writeMessage = findViewById(R.id.chat_writeMessage);
        button = findViewById(R.id.chat_sendMessageButton);

        myToolBar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Chat");

        mAuth = FirebaseAuth.getInstance();
        senderId = mAuth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(senderId);

        rootReference = FirebaseDatabase.getInstance().getReference();
        rootReference.keepSynced(true);

        notificationReference = FirebaseDatabase.getInstance().getReference().child("Notifications");
        notificationReference.keepSynced(true);

        serverNotiReference = FirebaseDatabase.getInstance().getReference().child("ServerChat");
        serverNotiReference.keepSynced(true);

        onlineReference = FirebaseDatabase.getInstance().getReference().child("OnlineUser");
        onlineReference.keepSynced(true);

        //String getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        /*DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken").child(getCurrentUserId);
        tokenRef.keepSynced(true);
        tokenRef.child("chat").setValue("yes");*/

        catchErrors = new CatchErrors();
        dateAndTime = new DateAndTime();
        notifySenderList = new ArrayList<>();

        //receivers id = tmsoft
        receiverId = "tmsoft";

        messageAdapter = new MessageAdapter(messagesClassList, getApplicationContext());

        progressDialog = new ProgressDialog(this);

        recyclerView = findViewById(R.id.chat_recycler);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        recyclerView.setAdapter(messageAdapter);

        try{
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

            saveUserOnlineStatus("online");
            collectOnlineStatus();
            ShowMessages();
            getUserName();
            countServerNoti();
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "ChatActivity", "main method");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = writeMessage.getText().toString();

                if (TextUtils.isEmpty(msg))
                    Toast.makeText(ChatActivity.this, "Message box empty", Toast.LENGTH_SHORT).show();
                else {
                    try{
                        sendUserMessage(msg);
                        writeMessage.setText("");
                    } catch (Exception e){
                        Log.i("error", e.getMessage());
                        catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                                "ChatActivity", "main method button");
                    }
                }
            }
        });
    }

    private void saveUserOnlineStatus(String status) {
        onlineReference.child("clientStatus").child(senderId).setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    String err = task.getException().getMessage();
                    Log.i("error", err);
                }
            }
        });
    }

    private void collectOnlineStatus() {
        onlineReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("serverStatus")){
                        serverStatus = dataSnapshot.child("serverStatus").getValue().toString();
                    }
                } else {
                    serverStatus = "offline";
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void countServerNoti() {
        serverNotiReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    counter = dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getUserName(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("fullName")) {
                        fullName = dataSnapshot.child("fullName").getValue().toString();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ShowMessages(){
        rootReference.child("Messages").child(senderId).child(receiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        MessagesClass messages = dataSnapshot.getValue(MessagesClass.class);
                        messagesClassList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void sendUserMessage(final String msg) {
        progressDialog.setMessage("sending...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        String msgSenderRef = "Messages/" + senderId + "/" + receiverId;
        String msgReceiverRef = "Messages/" + receiverId + "/" + senderId;

        DatabaseReference msgKeyRef = rootReference.child("Messages").child(senderId).child(receiverId).push();
        String msgKey = msgKeyRef.getKey();

        Map messageMap = new HashMap();
        messageMap.put("message", msg);
        messageMap.put("type", "text");
        messageMap.put("time", ServerValue.TIMESTAMP);
        messageMap.put("from", senderId);
        messageMap.put("actualSenderId", notifySender1);

        Map messageDetailsMap = new HashMap();
        messageDetailsMap.put(msgSenderRef + "/" + msgKey, messageMap);
        messageDetailsMap.put(msgReceiverRef + "/" + msgKey, messageMap);

        rootReference.updateChildren(messageDetailsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){

                }

                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);

                final String tt = fullName + " send you a message";

                //sending notification
                final HashMap<String, String> notificationData = new HashMap<>();
                notificationData.put("from", senderId);
                notificationData.put("title", tt);
                notificationData.put("message", msg);

                if (serverStatus.equals("online")){
                    Log.i("chat", "online");
                    progressDialog.dismiss();
                    writeMessage.setText("");
                } else if(serverStatus.equals("offline")){
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
                                                            if (!task.isSuccessful()){
                                                                String err = task.getException().getMessage();
                                                                Log.i("error", err);
                                                            }
                                                        }
                                                    });
                                        }

                                        if (!TextUtils.isEmpty(notifySender3)){
                                            notificationReference.child(notifySender3).push().setValue(notificationData)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (!task.isSuccessful()){
                                                                String err = task.getException().getMessage();
                                                                Log.i("error", err);
                                                            }
                                                        }
                                                    });
                                        }
                                        Map serverNotiMap = new HashMap();
                                        serverNotiMap.put("time", ServerValue.TIMESTAMP);
                                        serverNotiMap.put("from", senderId);
                                        serverNotiMap.put("read", "no");
                                        serverNotiMap.put("message", msg);
                                        serverNotiMap.put("counter", counter);

                                        String[] noti = new String[notifySenderList.size()];
                                        for(int x = 0; x < noti.length; ++x){
                                            noti[x] = notifySenderList.get(x);
                                        }

                                        innerNotification = new InnerNotification(
                                                "chat",
                                                "no",
                                                senderId,
                                                "ChatActivity",
                                                ServerValue.TIMESTAMP.toString(),
                                                msg,
                                                tt,
                                                notifySenderList,
                                                receiverId
                                        );

                                        boolean deter = innerNotification.onSaveAll();
                                        if (deter){
                                            Query query = serverNotiReference.orderByChild("from").startAt(senderId).endAt(senderId + "\uf8ff");
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                                                            String key = snapShot.getKey();
                                                            serverNotiReference.child(key).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            serverNotiReference.push().setValue(serverNotiMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    writeMessage.setText("");
                                                }
                                            });
                                        } else {
                                            Query query = serverNotiReference.orderByChild("from").startAt(senderId).endAt(senderId + "\uf8ff");
                                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()){
                                                        for (DataSnapshot snapShot : dataSnapshot.getChildren()){
                                                            String key = snapShot.getKey();
                                                            serverNotiReference.child(key).removeValue();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            serverNotiReference.push().setValue(serverNotiMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    writeMessage.setText("");
                                                }
                                            });
                                        }
                                    } else {
                                        String er = task.getException().getMessage();
                                        Log.i("error", er);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            closeKeyBoard();
            saveUserOnlineStatus("offline");
            finish();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            closeKeyBoard();
            saveUserOnlineStatus("offline");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
