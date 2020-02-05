package com.tmsoft.tm.elinamclient.Adapters.TabAdapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tmsoft.tm.elinamclient.fragment.HomeFragment.CategoriesFragment;
import com.tmsoft.tm.elinamclient.fragment.InnerFragment.NowAvailableFragment;

public class TabsAdapter extends FragmentPagerAdapter {
    private String[] tabTitile = new String[]{"Now Available", "Categories"};
    private Context context;

    public TabsAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                NowAvailableFragment tFragment;
                tFragment = new NowAvailableFragment();
                return tFragment;

            case 1:
                CategoriesFragment mFragment = new CategoriesFragment();
                return mFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitile[position];
    }
}
