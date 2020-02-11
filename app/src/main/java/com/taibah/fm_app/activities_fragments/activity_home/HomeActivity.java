package com.taibah.fm_app.activities_fragments.activity_home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.tabs.TabLayout;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_login.LoginActivity;
import com.taibah.fm_app.databinding.ActivityHomeBinding;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private ActionBarDrawerToggle toggle;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();
    }

    private void initView() {
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolBar, R.string.open, R.string.close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));


        binding.tab.addTab(binding.tab.newTab().setText("عربي"));
        binding.tab.addTab(binding.tab.newTab().setText("English"));


        if (lang.equals("ar")) {

            binding.tab.getTabAt(0).select();

        } else {
            binding.tab.getTabAt(1).select();


        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            binding.drawer.setElevation(0.0f);
        }

        binding.drawer.setScrimColor(Color.TRANSPARENT);

        binding.drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

                float slideX = drawerView.getWidth() * slideOffset;
                if (lang.equals("ar")) {
                    slideX = slideX * -1;
                }
                binding.llHomeContent.setTranslationX(slideX);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                binding.drawer.closeDrawer(GravityCompat.START);


                int pos = tab.getPosition();

                if (pos == 0) {
                    RefreshActivity("ar");
                } else {
                    RefreshActivity("en");

                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.cardViewLogout.setOnClickListener(view -> logout());

        binding.consProfile.setOnClickListener(view -> {
            if (userModel == null) {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            } else {
            }
        });





    }


    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    private void logout() {
        binding.drawer.closeDrawer(GravityCompat.START);
        preferences.clear(this);
        navigateToSignInActivity();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            navigateToSignInActivity();
        }
    }


    public void RefreshActivity(String lang) {
        Paper.book().write("lang", lang);
        LanguageHelper.setNewLocale(this, lang);

        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (userModel == null) {
            navigateToSignInActivity();
        } else {
            finish();
        }
    }
}
