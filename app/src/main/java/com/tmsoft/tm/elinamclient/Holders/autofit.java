package com.tmsoft.tm.elinamclient.Holders;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.tmsoft.tm.elinamclient.R;

public class autofit {

    private int noOfColumn, layoutWidth, testLength, textHeight;

    public void autofit(Context context) {
        textHeight = context.getResources().getDimensionPixelSize(R.dimen.oneLineOnly);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.i("width", dpWidth + "");
        if (dpWidth <= 190){
            noOfColumn = 1;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display1);
            testLength = 35;
        } else if (dpWidth >= 191 && dpWidth <= 330){
            noOfColumn = 2;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display2);
            testLength = 30;
        } else if (dpWidth >= 331 && dpWidth <= 368){
            noOfColumn = 2;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display3);
            testLength = 30;
        }
        else if (dpWidth >= 369 && dpWidth <= 398){
            noOfColumn = 2;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display4);
            testLength = 32;
        }
        else if (dpWidth >= 399 && dpWidth <= 417){
            noOfColumn = 2;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display5);
            testLength = 35;
        }
        else if (dpWidth >= 418 && dpWidth <= 435){
            noOfColumn = 2;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display6);
            testLength = 40;
        }
        else if (dpWidth >= 436 && dpWidth <= 495){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display7);
            testLength = 55;
        }
        else if (dpWidth >= 496 && dpWidth <= 541){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display8);
            testLength = 46;
        }
        else if (dpWidth >= 542 && dpWidth <= 610){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display9);
            testLength = 50;
        }
        else if (dpWidth >= 585 && dpWidth <= 610){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display10);
            testLength = 55;
        }
        else if (dpWidth >= 611 && dpWidth <= 690){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display11);
            testLength = 52;
        }
        else if (dpWidth >= 691 && dpWidth <= 740){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display12);
            testLength = 55;
        }
        else if (dpWidth >= 741 && dpWidth <= 788){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display13);
            testLength = 60;
        }
        else if (dpWidth >= 789 && dpWidth <= 798){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display14);
            testLength = 55;
        }
        else if (dpWidth >= 799 && dpWidth <= 810){
            noOfColumn = 3;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display15);
            testLength = 50;
        }
        else if (dpWidth >= 811 && dpWidth <= 830){
            noOfColumn = 4;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display16);
            testLength = 50;
        }
        else if (dpWidth >= 831 && dpWidth <= 850){
            noOfColumn = 4;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display17);
            testLength = 46;
        }
        else if (dpWidth >= 851 && dpWidth <= 890){
            noOfColumn = 4;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display18);
            testLength = 47;
        }
        else if (dpWidth >= 891 && dpWidth <= 920){
            noOfColumn = 4;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display19);
            testLength = 48;
        }
        else if (dpWidth >= 921 && dpWidth <= 980){
            noOfColumn = 4;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display20);
            testLength = 55;
        }
        else if (dpWidth >= 981 && dpWidth <= 1121){
            noOfColumn = 4;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display21);
            testLength = 57;
        }
        else if (dpWidth >= 1122 && dpWidth <= 1280){
            noOfColumn = 5;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display22);
            testLength = 65;
        }
        else{
            noOfColumn = 5;
            testLength = 65;
            layoutWidth = context.getResources().getDimensionPixelSize(R.dimen.display22);
        }
    }

    public int getNoOfColumn() {
        return noOfColumn;
    }

    public int getLayoutWidth() {
        return layoutWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public int getTestLength() {
        return testLength;
    }
}
