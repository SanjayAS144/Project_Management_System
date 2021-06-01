package com.example.projrctmanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.projrctmanagement.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class PhotoViewerActivity extends AppCompatActivity {

    PhotoView mPhotoView;
    String photoUrl,project_id,group_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#424242"));
        setContentView(R.layout.activity_photo_viewer);

        ImageView back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhotoViewerActivity.super.onBackPressed();
            }
        });
        Intent intent = getIntent();
        photoUrl = intent.getStringExtra("msg_image");
        project_id = intent.getStringExtra("msg_image");
        group_name = intent.getStringExtra("group_name");

        mPhotoView =findViewById(R.id.photoviewer);
        Picasso.get().load(photoUrl).into(mPhotoView);
    }


}