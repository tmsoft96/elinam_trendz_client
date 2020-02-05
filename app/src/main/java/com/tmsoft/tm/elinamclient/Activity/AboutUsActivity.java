package com.tmsoft.tm.elinamclient.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tmsoft.tm.elinamclient.Handles.CatchErrors;
import com.tmsoft.tm.elinamclient.Handles.DateAndTime;
import com.tmsoft.tm.elinamclient.R;
import com.tmsoft.tm.elinamclient.Urls.ServerInputDetails;
import com.tmsoft.tm.elinamclient.Urls.SocialMediaUrl;

import java.util.Objects;

public class AboutUsActivity extends AppCompatActivity {

    private Dialog dialog;

    private SocialMediaUrl socialMediaUrl;
    private ServerInputDetails serverInputDetails;
    private DateAndTime dateAndTime;
    private CatchErrors catchErrors;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        TextView year = findViewById(R.id.aboutUs_year);
        TextView appInfo = findViewById(R.id.aboutUs_appInfo);
        TextView contactUs = findViewById(R.id.aboutUs_contactUs);
        TextView credits = findViewById(R.id.aboutUs_credits);
        ImageView facebook = findViewById(R.id.aboutUs_facebook);
        ImageView twitter = findViewById(R.id.aboutUs_twitter);
        ImageView telegram = findViewById(R.id.aboutUs_telegram);
        ImageView whatsApp = findViewById(R.id.aboutUs_whatsApp);
        ImageView gmail = findViewById(R.id.aboutUs_gmail);
        dialog = new Dialog(this, R.style.Theme_CustomDialog);
        socialMediaUrl = new SocialMediaUrl();
        serverInputDetails = new ServerInputDetails();
        dateAndTime = new DateAndTime();
        catchErrors = new CatchErrors();

        Toolbar myToolBar = findViewById(R.id.aboutUs_toolbar);
        setSupportActionBar(myToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_aboutUs);

        year.setText(R.string.app_year);

        appInfo.setPaintFlags(appInfo.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        contactUs.setPaintFlags(contactUs.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        credits.setPaintFlags(credits.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        appInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendUserToAppInfo();
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreditDialog();
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showContactUsDialog();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getFacebook());
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getTwitter());
            }
        });

        telegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUri(socialMediaUrl.getTelegram());
            }
        });

        whatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUri(socialMediaUrl.getWhatsapp());
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerInputDetails serverInputDetails = new ServerInputDetails();
                getUri(serverInputDetails.getEmailAddress());
            }
        });
    }

    private void getUri(String s) {
        try{
            Uri uri = Uri.parse(s);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "AboutUsActivity", "getUri method");
        }
    }

    private void showContactUsDialog() {
        final TextView close, phoneNumber, emailAddress, location;

        dialog.setContentView(R.layout.dialog_contact_us);
        close = dialog.findViewById(R.id.contactUs_close);
        phoneNumber = dialog.findViewById(R.id.contactUs_phoneNumber);
        emailAddress = dialog.findViewById(R.id.contactUs_email);
        location = dialog.findViewById(R.id.contactUs_location);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        phoneNumber.setText(serverInputDetails.getPhoneNumber());
        emailAddress.setText(serverInputDetails.getEmailAddress());
        location.setText(serverInputDetails.getLocation());

        dialog.show();
    }

    private void showCreditDialog() {
        TextView close, year;

        dialog.setContentView(R.layout.dialog_credits);
        close = dialog.findViewById(R.id.credits_close);
        year = dialog.findViewById(R.id.credits_year);

        year.setText(R.string.app_year);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendUserToAppInfo() {
        try{
            Intent intent = new Intent(AboutUsActivity.this, AppInfoActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } catch (Exception e){
            Log.i("error", e.getMessage());
            catchErrors.setErrors(e.getMessage(), dateAndTime.getDate(), dateAndTime.getTime(),
                    "AboutUsActivity", "sendUserToAppInfo method");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
