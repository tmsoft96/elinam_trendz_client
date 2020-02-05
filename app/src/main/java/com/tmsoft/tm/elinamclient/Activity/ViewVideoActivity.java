package com.tmsoft.tm.elinamclient.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.tmsoft.tm.elinamclient.R;

public class ViewVideoActivity extends AppCompatActivity {

    private Toolbar myToolBar;
    private WebView videoView;
    private ProgressDialog progressDialog;

    private String getLink, link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);

        myToolBar = findViewById(R.id.viewProductVideo_toolbar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Video");

        getLink = getIntent().getExtras().get("link").toString();

        link = "<iframe width=\"100%\" height\"100%\" src=\"https://www.youtube.com/embed/" + getLink + "\" frameborder=\"0\" allowfullscreen></iframe>";

        videoView = findViewById(R.id.viewProductVideo_video);
        progressDialog = new ProgressDialog(this);
        videoView.getSettings().setJavaScriptEnabled(true);
        videoView.setFocusable(true);
        videoView.setFocusableInTouchMode(true);
        videoView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        videoView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        videoView.getSettings().setDomStorageEnabled(true);
        videoView.getSettings().setDatabaseEnabled(true);
        videoView.getSettings().setAppCacheEnabled(true);
        videoView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        checkConnection();

        videoView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            public void onReceivedError(WebView view, int errorCode, String description, String
                    failingUrl) {
                progressDialog.dismiss();
                Toast.makeText(ViewVideoActivity.this, "Error loading video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                checkConnection();
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void checkConnection(){
        if (isOnline()){
            autoplay();
        } else {
            Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
        }

    }

    private void autoplay() {
        progressDialog.setMessage("Loading video...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        videoView.loadData(link, "text/html", "utf-8");
    }

    private void stopPlay(){
        videoView.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            stopPlay();
            finish();
        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            stopPlay();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
