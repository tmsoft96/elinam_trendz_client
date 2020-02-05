package com.tmsoft.tm.elinamclient.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tmsoft.tm.elinamclient.Adapters.TabAdapter.OrderAdapter;
import com.tmsoft.tm.elinamclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainOrderFragment extends Fragment {

    private View view;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;

    public MainOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_b_main_order, container, false);

        //Tabs in main activity
        myViewPager = view.findViewById(R.id.mainOrder_viewPager);
        myViewPager.setAdapter(new OrderAdapter(getChildFragmentManager(),view.getContext()));
        myTabLayout = view.findViewById(R.id.mainOrder_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        String getCurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("AllDeviceToken").child(getCurrentUserId);
        tokenRef.keepSynced(true);
        tokenRef.child("order").setValue("yes");

        return view;
    }
}
