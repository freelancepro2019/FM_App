package com.creative.share.apps.homecare.activities_fragments.activity_home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Main;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Profile;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_settings.Fragment_Settings;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_orders.Fragment_Orders;
import com.creative.share.apps.homecare.activities_fragments.activity_login.LoginActivity;
import com.creative.share.apps.homecare.databinding.ActivityHomeBinding;
import com.creative.share.apps.homecare.language.LanguageHelper;
import com.creative.share.apps.homecare.models.UserModel;
import com.creative.share.apps.homecare.preferences.Preferences;
import com.creative.share.apps.homecare.share.Common;

import java.util.Locale;

import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private Fragment_Main fragment_main;
    private Fragment_Orders fragment_orders;
    private Fragment_Profile fragment_profile;
    private Fragment_Settings fragment_settings;
    private FragmentManager fragmentManager;


    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang", Locale.getDefault().getLanguage())));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initView();

    }

    private void initView() {
        preferences = Preferences.getInstance();

        fragmentManager = getSupportFragmentManager();
        setUpBottomNavigation();
        binding.imageLogout.setOnClickListener((view -> {
            if (userModel==null)
            {
                navigateToSignInActivity();
            }else
            {
                logout();
            }
        }));
    }




    private void setUpBottomNavigation() {

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(getString(R.string.home), R.drawable.ic_nav_home);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(getString(R.string.orders), R.drawable.ic_checklist);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(getString(R.string.profile), R.drawable.ic_user);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem(getString(R.string.more), R.drawable.ic_more);

        binding.ahBottomNav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        binding.ahBottomNav.setDefaultBackgroundColor(ContextCompat.getColor(this, R.color.nav_bg));
        binding.ahBottomNav.setTitleTextSizeInSp(14, 12);
        binding.ahBottomNav.setForceTint(true);
        binding.ahBottomNav.setAccentColor(ContextCompat.getColor(this, R.color.colorPrimary));
        binding.ahBottomNav.setInactiveColor(ContextCompat.getColor(this, R.color.gray4));
        binding.ahBottomNav.addItem(item1);
        binding.ahBottomNav.addItem(item2);
        binding.ahBottomNav.addItem(item3);
        binding.ahBottomNav.addItem(item4);

        binding.ahBottomNav.setOnTabSelectedListener((position, wasSelected) -> {
            userModel = preferences.getUserData(this);
            switch (position) {
                case 0:

                    DisplayFragmentMain();
                    break;
                case 1:
                    if (userModel != null) {
                        DisplayFragmentOrders();

                    } else {
                        Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
                    }
                    break;
                case 2:
                    if (userModel != null) {
                        DisplayFragmentProfile();

                    } else {
                        Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up));
                    }
                    break;
                case 3:

                    DisplayFragmentMore();
                    break;


            }
            return false;
        });

        binding.ahBottomNav.setCurrentItem(0, false);
        DisplayFragmentMain();


    }

    private void DisplayFragmentMain() {

        if (fragment_main == null) {
            fragment_main = Fragment_Main.newInstance();
        }
        if (fragment_orders != null && fragment_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_orders).commit();
        }
        if (fragment_profile != null && fragment_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_profile).commit();
        }
        if (fragment_settings != null && fragment_settings.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_settings).commit();
        }


        if (fragment_main.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_main).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_main, "fragment_main").addToBackStack("fragment_main").commit();

        }
        binding.ahBottomNav.setCurrentItem(0, false);
        binding.tvTitle.setText(getString(R.string.home));

    }

    private void DisplayFragmentOrders() {

        if (fragment_orders == null) {
            fragment_orders = Fragment_Orders.newInstance();
        }
        if (fragment_main != null && fragment_main.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_main).commit();
        }
        if (fragment_profile != null && fragment_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_profile).commit();
        }
        if (fragment_settings != null && fragment_settings.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_settings).commit();
        }


        if (fragment_orders.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_orders).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_orders, "fragment_orders").addToBackStack("fragment_orders").commit();

        }
        binding.ahBottomNav.setCurrentItem(1, false);
        binding.tvTitle.setText(R.string.orders);
    }

    private void DisplayFragmentProfile() {

        if (fragment_profile == null) {
            fragment_profile = Fragment_Profile.newInstance();
        }
        if (fragment_main != null && fragment_main.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_main).commit();
        }
        if (fragment_orders != null && fragment_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_orders).commit();
        }
        if (fragment_settings != null && fragment_settings.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_settings).commit();
        }


        if (fragment_profile.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_profile).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_profile, "fragment_profile").addToBackStack("fragment_profile").commit();

        }
        binding.ahBottomNav.setCurrentItem(2, false);
        binding.tvTitle.setText(R.string.profile);
    }

    private void DisplayFragmentMore() {

        if (fragment_settings == null) {
            fragment_settings = Fragment_Settings.newInstance();
        }
        if (fragment_main != null && fragment_main.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_main).commit();
        }
        if (fragment_orders != null && fragment_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_orders).commit();
        }
        if (fragment_profile != null && fragment_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_profile).commit();
        }


        if (fragment_settings.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_settings).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_settings, "fragment_settings").addToBackStack("fragment_settings").commit();

        }
        binding.ahBottomNav.setCurrentItem(3, false);
        binding.tvTitle.setText(R.string.more);
    }

    @Override
    public void onBackPressed() {
        if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
            if (userModel==null)
            {
                navigateToSignInActivity();
            }else
                {
                    logout();
                }
        } else {
            DisplayFragmentMain();
        }
    }

    private void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void logout() {
        preferences.clear(this);
        finish();

    }

    public void RefreshActivity(String lang) {
        //Log.e("lang",selected_language);
        Paper.book().write("lang", lang);
        preferences.create_update_language(this, lang);
        preferences.setIsLanguageSelected(this);
        LanguageHelper.setNewLocale(this, lang);

        new Handler()
                .postDelayed(() -> {

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }, 1050);


    }
}

