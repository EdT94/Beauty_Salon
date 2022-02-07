package com.example.zinabeautysalon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView splash_IMG_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        hideSystemUI();
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
        startSplash(splash_IMG_logo);
    }

    private void startSplash(final ImageView v) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_out);

        v.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }


    public void hideSystemUI() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.myPurple));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.myPink));

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY       // Set the content to appear under the system bars so that the
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }
}