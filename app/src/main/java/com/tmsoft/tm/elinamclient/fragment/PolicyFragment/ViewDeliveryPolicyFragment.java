package com.tmsoft.tm.elinamclient.fragment.PolicyFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tmsoft.tm.elinamclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewDeliveryPolicyFragment extends Fragment {

    private View view;
    private TextView textView;

    public ViewDeliveryPolicyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_p_view_delivery_policy, container, false);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Policy Details");
        databaseReference.keepSynced(true);

        textView = view.findViewById(R.id.deliveryPolicy_text);

        databaseReference.child("policy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("deliveryPolicy")){
                        String del = dataSnapshot.child("deliveryPolicy").getValue().toString();
                        textView.setText(del);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

}
