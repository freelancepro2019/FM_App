package com.taibah.fm_app.activities_fragments.activity_home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_diet.DietActivity;
import com.taibah.fm_app.activities_fragments.activity_health_food.HealthFoodActivity;
import com.taibah.fm_app.activities_fragments.activity_join_now.JoinNowActivity;
import com.taibah.fm_app.activities_fragments.activity_login.LoginActivity;
import com.taibah.fm_app.activities_fragments.activity_sell_participation.SellParticipationActivity;
import com.taibah.fm_app.activities_fragments.activity_terms.TermsActivity;
import com.taibah.fm_app.activities_fragments.activity_home_sessions.HomeSessionsActivity;
import com.taibah.fm_app.adapters.SliderAdapter;
import com.taibah.fm_app.databinding.ActivityHomeBinding;
import com.taibah.fm_app.language.LanguageHelper;
import com.taibah.fm_app.models.UserModel;
import com.taibah.fm_app.preferences.Preferences;
import com.taibah.fm_app.share.Common;
import com.taibah.fm_app.tags.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;
    private ActionBarDrawerToggle toggle;
    private SliderAdapter sliderAdapter;
    private List<String> images;
    private Timer timer;
    private TimerTask timerTask;
    private DatabaseReference dRef;


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
        dRef = FirebaseDatabase.getInstance().getReference(Tags.DATABASE_NAME).child(Tags.TABLE_SETTINGS).child(Tags.TABLE_SLIDER);
        images = new ArrayList<>();
        preferences = Preferences.newInstance();
        userModel = preferences.getUserData(this);
        Paper.init(this);
        lang = Paper.book().read("lang", "ar");
        binding.setLang(lang);
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_IN);

        toggle = new ActionBarDrawerToggle(this, binding.drawer, binding.toolBar, R.string.open, R.string.close);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(ContextCompat.getColor(this, R.color.white));

        binding.tab.addTab(binding.tab.newTab().setText("عربي"));
        binding.tab.addTab(binding.tab.newTab().setText("English"));

        binding.tab2.setupWithViewPager(binding.pager);

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


        binding.flSell.setOnClickListener(view -> {

           /* if (userModel == null) {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            } else {
            }*/

            navigateToSellActivity();
        });


        binding.flJoin.setOnClickListener(view -> {

           /* if (userModel == null) {
                Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
            } else {
            }*/

           navigateToJoinActivity();
        });

        binding.flSession.setOnClickListener(view -> {
            navigateToHomeSessioActivity();
        });
        binding.fldiet.setOnClickListener(view -> {
            navigateToDietActivity();
        });

        binding.flHealthFood.setOnClickListener(view -> {
            navigateToHealthFoodActivity();
        });
        binding.consAbout.setOnClickListener(view -> {
            navigateToTermsActivity(2);
        });


        binding.consTerms.setOnClickListener(view -> {
            navigateToTermsActivity(1);
        });

        getSliderImages();
    }

    private void getSliderImages() {

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    binding.sliderContainer.setVisibility(View.VISIBLE);

                    for (DataSnapshot ds :dataSnapshot.getChildren())
                    {
                        images.add(ds.getValue().toString());
                    }

                    updateSliderUI();

                }else
                    {
                        binding.sliderContainer.setVisibility(View.INVISIBLE);
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateSliderUI() {

        binding.progBar.setVisibility(View.GONE);
        sliderAdapter = new SliderAdapter(images,this);
        binding.pager.setAdapter(sliderAdapter);

        if (images.size()>1)
        {
            startTimer();
        }
    }


    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToJoinActivity() {
        Intent intent = new Intent(this, JoinNowActivity.class);
        startActivity(intent);
    }
    private void navigateToHomeSessioActivity() {
        Intent intent = new Intent(this, HomeSessionsActivity.class);
        startActivity(intent);
    }
    private void navigateToDietActivity() {
        Intent intent = new Intent(this, DietActivity.class);
        startActivity(intent);
    }
    private void navigateToSellActivity() {
        Intent intent = new Intent(this, SellParticipationActivity.class);
        startActivity(intent);
    }
    private void navigateToHealthFoodActivity() {
        Intent intent = new Intent(this, HealthFoodActivity.class);
        startActivity(intent);
    }

    private void navigateToTermsActivity(int type) {
        Intent intent = new Intent(this, TermsActivity.class);
        intent.putExtra("type",type);
        startActivity(intent);
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

    private void startTimer() {
        timer = new Timer();
        timerTask = new MyTask();
        timer.scheduleAtFixedRate(timerTask,6000,6000);


    }
    private class MyTask extends TimerTask{
        @Override
        public void run() {
            runOnUiThread(() -> {
                if (binding.pager.getCurrentItem()<sliderAdapter.getCount()-1)
                {
                    binding.pager.setCurrentItem(binding.pager.getCurrentItem()+1);
                }else
                {
                    binding.pager.setCurrentItem(0);
                }
            });
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null)
        {
            timer.purge();
            timer.cancel();
        }

        if (timerTask!=null)
        {
            timerTask.cancel();
        }
    }
}
