package com.example.reme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.reme.R;

public class StartActivity extends AppCompatActivity {

    ImageView logo, appname, splashImg;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        logo = findViewById(R.id.logo);
        appname = findViewById(R.id.appname);
        splashImg = findViewById(R.id.background);
        lottieAnimationView = findViewById(R.id.notepadLoading);

        splashImg.animate().translationY(-4000).setDuration(1000).setStartDelay(3000);
        logo.animate().translationY(4000).setDuration(1000).setStartDelay(3000);
        appname.animate().translationY(4000).setDuration(1000).setStartDelay(3000);
        lottieAnimationView.animate().translationY(2000).setDuration(1000).setStartDelay(3000);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent intent = new Intent(StartActivity.this, Login.class);
                //Intent intent = new Intent(StartActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, 5000);


    }
}
