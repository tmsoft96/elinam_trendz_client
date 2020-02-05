package com.tmsoft.tm.elinamclient.Adapters.TabAdapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tmsoft.tm.elinamclient.fragment.OrderFragment.BuyForMeFragment;
import com.tmsoft.tm.elinamclient.fragment.OrderFragment.CartOrderFragment;
import com.tmsoft.tm.elinamclient.fragment.OrderFragment.OrderFragment;

public class OrderAdapter extends FragmentPagerAdapter {
    private String[] tabTitle = new String[]{"Order Product", "Cart Order", "Buy For Me"};
    Context context;

    public OrderAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                OrderFragment order = new OrderFragment();
                return order;

            case 1 :
                CartOrderFragment cart = new CartOrderFragment();
                return cart;

            case 2 :
                BuyForMeFragment buy = new BuyForMeFragment();
                return buy;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
