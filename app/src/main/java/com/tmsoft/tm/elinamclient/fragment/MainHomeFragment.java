package com.tmsoft.tm.elinamclient.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tmsoft.tm.elinamclient.Adapters.TabAdapter.TabsAdapter;
import com.tmsoft.tm.elinamclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainHomeFragment extends Fragment {

    private View view;



    private ViewPager myViewPager;
    private TabLayout myTabLayout;



    public MainHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_b_main_home, container, false);

        //Tabs in main activity
        myViewPager = view.findViewById(R.id.main_viewPager);
        myViewPager.setAdapter(new TabsAdapter(getChildFragmentManager(),view.getContext()));
        myTabLayout = view.findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);



        return view;
    }

}
