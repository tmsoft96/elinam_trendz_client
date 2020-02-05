package com.tmsoft.tm.elinamclient.Holders;

import android.content.Context;

import com.tmsoft.tm.elinamclient.R;

public class MainPageMarginClass {
    private int hide, notHide;

    public MainPageMarginClass(Context context) {
        hide = context.getResources().getDimensionPixelSize(R.dimen.mainPageRelativeMarginDownHide);
        notHide = context.getResources().getDimensionPixelSize(R.dimen.mainPageRelativeMarginDownNotHide);
    }

    public int getHide() {
        return hide;
    }

    public int getNotHide() {
        return notHide;
    }
}
