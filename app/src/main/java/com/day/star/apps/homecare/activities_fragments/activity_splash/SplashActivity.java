package com.day.star.apps.homecare.activities_fragments.activity_splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.day.star.apps.homecare.R;
import com.day.star.apps.homecare.activities_fragments.activity_home.HomeActivity;
import com.day.star.apps.homecare.activities_fragments.activity_login.LoginActivity;
import com.day.star.apps.homecare.databinding.ActivitySplashBinding;
import com.day.star.apps.homecare.language.LanguageHelper;
import com.day.star.apps.homecare.preferences.Preferences;
import com.day.star.apps.homecare.tags.Tags;

import java.util.Locale;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private Animation animation1,animation2;
    private Preferences preferences;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        preferences = Preferences.newInstance();

        animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.alpha);
        binding.imageSplash.startAnimation(animation1);
        animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.translate);

        animation1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.imgDayStar.setVisibility(View.VISIBLE);
                binding.imgDayStar.startAnimation(animation2);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animation2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                String session = preferences.getSession(SplashActivity.this);
                if (session.equals(Tags.session_login)) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });




    }
}