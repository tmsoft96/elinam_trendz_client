package com.tmsoft.tm.elinamclient.Urls;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InnerNotification {
    private String title, read, userId, activityToGo, time, message,
            messageTitle, postKey;
    private List<String> messageFrom;

    public InnerNotification(String title, String read, String userId, String activityToGo, String time,
                             String message, String messageTitle,List<String> messageFrom, String postKey) {
        this.title = title;
        this.read = read;
        this.userId = userId;
        this.activityToGo = activityToGo;
        this.time = time;
        this.message = message;
        this.messageTitle = messageTitle;
        this.postKey = postKey;
        this.messageFrom = messageFrom;
    }

    /**
     * title means the activity summary functionality
     * read must always be in a 'yes or no' form which mean that when a click and read the InnerMessage it changes automatically to yes
     * userId is the current user online id
     * activityToGo is when the user click it should send the user to a particular activity
     * time, date, InnerMessage, messageTitle all talks about the info you wish to pass through
     * messageFrom this means the recipient of the InnerMessage and it requires id
     * postKey this can be used when u want the user to see an exiting post*/

    private boolean determine;
    public boolean onSaveAll(){
        for (String from : messageFrom){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("InnerNotification").child(from);
            Map notiMap = new HashMap();
            notiMap.put("title", title);
            notiMap.put("read", read);
            notiMap.put("userId", userId);
            notiMap.put("activityIoGo", activityToGo);
            notiMap.put("time", ServerValue.TIMESTAMP);
            notiMap.put("message", message);
            notiMap.put("messageTitle", messageTitle);
            notiMap.put("messageFrom", from);
            notiMap.put("postKey", postKey);

            databaseReference.push().setValue(notiMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    determine = task.isSuccessful();
                }
            });
        }
        return determine;
    }
}
