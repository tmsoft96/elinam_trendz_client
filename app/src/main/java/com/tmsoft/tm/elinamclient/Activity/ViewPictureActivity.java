package com.tmsoft.tm.elinamclient.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.tmsoft.tm.elinamclient.R;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ViewPictureActivity extends AppCompatActivity {

    private TextView text;
    private ImageView image;
    private PhotoViewAttacher attacher;

    private String getText, getImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);

        text = findViewById(R.id.view_text);
        image = findViewById(R.id.view_image);

        try{
            getText = getIntent().getExtras().get("imageText").toString();
            getImage = getIntent().getExtras().get("image").toString();
        } catch (Exception ex){

        }

        if (TextUtils.isEmpty(getImage))
            image.setImageResource(R.drawable.no_image);
        else {
            //loading picture offline
            Picasso.get()
                    .load(getImage)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.no_image)
                    .into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    Picasso.get().load(getImage)
                            .placeholder(R.drawable.no_image).into(image);
                }
            });
        }

        if (getText.equalsIgnoreCase("")){
            text.setVisibility(View.INVISIBLE);
        } else{
            text.setText(getText);
        }

        //zooming photo
        attacher = new PhotoViewAttacher(image);
        attacher.update();
    }
}
