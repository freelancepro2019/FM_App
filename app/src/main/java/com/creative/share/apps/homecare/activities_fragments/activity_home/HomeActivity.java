package com.creative.share.apps.homecare.activities_fragments.activity_home;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Main;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_MakeOrder;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Profile;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_orders.Fragment_Client_Orders;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_orders.Fragment_Provider_Orders;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_settings.Fragment_Settings;
import com.creative.share.apps.homecare.activities_fragments.activity_login.LoginActivity;
import com.creative.share.apps.homecare.activities_fragments.activity_notification.NotificationActivity;
import com.creative.share.apps.homecare.activities_fragments.activity_sub_service_details.SubServiceDetailsActivity;
import com.creative.share.apps.homecare.adapters.SubServiceAdapter;
import com.creative.share.apps.homecare.databinding.ActivityHomeBinding;
import com.creative.share.apps.homecare.language.LanguageHelper;
import com.creative.share.apps.homecare.models.ServicesDataModel;
import com.creative.share.apps.homecare.models.SubServicesModel;
import com.creative.share.apps.homecare.models.UserModel;
import com.creative.share.apps.homecare.preferences.Preferences;
import com.creative.share.apps.homecare.remote.Api;
import com.creative.share.apps.homecare.share.Common;
import com.creative.share.apps.homecare.tags.Tags;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private Preferences preferences;
    private UserModel userModel;
    private Fragment_Main fragment_main;
    private Fragment_Client_Orders fragment_client_orders;
    private Fragment_Provider_Orders fragment_provider_orders;
    private Fragment_Profile fragment_profile;
    private Fragment_Settings fragment_settings;
    private Fragment_MakeOrder fragment_makeOrder;
    private FragmentManager fragmentManager;
    ///////////////////////////////////////
    private LinearLayout root,llTitle;
    private ImageView arrow;
    private BottomSheetBehavior behavior;
    private TextView tvTitle,tvNoService;
    private ProgressBar progBar;
    private RecyclerView recView;
    private String lang;
    private List<SubServicesModel.SubServiceModel> subServiceModelList;
    private String main_service_id;
    private int color;


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

    private void initView()
    {
        Paper.init(this);
        lang = Paper.book().read("lang",Locale.getDefault().getLanguage());
        preferences = Preferences.getInstance();

        fragmentManager = getSupportFragmentManager();
        setUpBottomNavigation();
        root = findViewById(R.id.root);
        llTitle = findViewById(R.id.llTitle);
        tvTitle = findViewById(R.id.tvSheetTitle);
        progBar = findViewById(R.id.progBar);
        tvNoService = findViewById(R.id.tvNoService);
        recView = findViewById(R.id.recView);
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setNestedScrollingEnabled(false);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        arrow = findViewById(R.id.arrow);
        if (lang.equals("ar"))
        {
            arrow.setRotation(180.0f);
        }

        behavior = BottomSheetBehavior.from(root);

        llTitle.setOnClickListener(view -> closeSheet());
        behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState==BottomSheetBehavior.STATE_DRAGGING)
                {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        binding.flNotification.setOnClickListener(view ->
        {
            if(userModel!=null)
            {
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);

            }else
                {
                    Common.CreateDialogAlert(this,getString(R.string.please_sign_in_or_sign_up),R.color.colorPrimary);

                }

        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeStatusBarColor(int color)
    {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,color));
    }

    public void openSheet(int color, ServicesDataModel.ServiceModel serviceModel)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeStatusBarColor(color);
        }
        this.color = color;
        main_service_id = serviceModel.getService_id();
        tvTitle.setText(serviceModel.getWords().getTitle());
        llTitle.setBackgroundResource(color);

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        progBar.setVisibility(View.VISIBLE);
        subServiceModelList = new ArrayList<>();
        SubServiceAdapter adapter = new SubServiceAdapter(this,subServiceModelList);
        recView.setAdapter(adapter);

        Api.getService(Tags.base_url).
                get_sub_services(lang,serviceModel.getService_id()).
                enqueue(new Callback<SubServicesModel>() {
            @Override
            public void onResponse(Call<SubServicesModel> call, Response<SubServicesModel> response) {
                progBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getServices().size() > 0) {

                        tvNoService.setVisibility(View.GONE);
                        subServiceModelList.clear();
                        subServiceModelList.addAll(response.body().getServices());
                        adapter.notifyDataSetChanged();

                    }else
                    {
                        tvNoService.setVisibility(View.VISIBLE);

                    }
                } else {
                    try {

                        Log.e("error", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 500) {
                        Toast.makeText(HomeActivity.this, "Server Error", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(HomeActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                    }
                }
            }

            @Override
            public void onFailure(Call<SubServicesModel> call, Throwable t) {

                try {
                    progBar.setVisibility(View.GONE);
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            Toast.makeText(HomeActivity.this, R.string.something, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                }



            }
        });

    }
    private void closeSheet()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeStatusBarColor(R.color.colorPrimaryDark);
        }
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void setSubServiceItemData(SubServicesModel.SubServiceModel subServiceModel) {

        Intent intent = new Intent(this, SubServiceDetailsActivity.class);
        intent.putExtra("main_service_id",main_service_id);
        intent.putExtra("color",color);
        intent.putExtra("data",subServiceModel);
        startActivity(intent);
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
                        if (userModel.getUser_type().equals(Tags.USER_CLIENT))
                        {
                            DisplayFragmentClientOrders();

                        }else
                            {
                                DisplayFragmentProviderOrders();
                            }

                    } else {
                        Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up),R.color.colorPrimary);
                    }
                    break;
                case 2:
                    if (userModel != null) {
                        DisplayFragmentProfile();

                    } else {
                        Common.CreateDialogAlert(this, getString(R.string.please_sign_in_or_sign_up),R.color.colorPrimary);
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

        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }

        if (fragment_provider_orders != null && fragment_provider_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_provider_orders).commit();
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

    private void DisplayFragmentClientOrders() {

        fragment_client_orders = Fragment_Client_Orders.newInstance();

        if (fragment_main != null && fragment_main.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_main).commit();
        }
        if (fragment_profile != null && fragment_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_profile).commit();
        }
        if (fragment_settings != null && fragment_settings.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_settings).commit();
        }


        if (fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_client_orders).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_client_orders, "fragment_client_orders").addToBackStack("fragment_client_orders").commit();

        }
        binding.ahBottomNav.setCurrentItem(1, false);
        binding.tvTitle.setText(R.string.orders);
    }

    private void DisplayFragmentProviderOrders()
    {

        fragment_provider_orders = Fragment_Provider_Orders.newInstance();

        if (fragment_main != null && fragment_main.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_main).commit();
        }
        if (fragment_profile != null && fragment_profile.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_profile).commit();
        }
        if (fragment_settings != null && fragment_settings.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_settings).commit();
        }


        if (fragment_provider_orders.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_provider_orders).commit();

        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_provider_orders, "fragment_provider_orders").addToBackStack("fragment_provider_orders").commit();

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
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_provider_orders != null && fragment_provider_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_provider_orders).commit();
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
        if (fragment_client_orders != null && fragment_client_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_client_orders).commit();
        }
        if (fragment_provider_orders != null && fragment_provider_orders.isAdded()) {
            fragmentManager.beginTransaction().hide(fragment_provider_orders).commit();
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

    public void DisplayFragmentMakeOrder() {

        fragment_makeOrder = Fragment_MakeOrder.newInstance();

        if (fragment_makeOrder.isAdded()) {
            fragmentManager.beginTransaction().show(fragment_makeOrder).commit();
        } else {
            fragmentManager.beginTransaction().add(R.id.fragment_home_container, fragment_makeOrder, "fragment_makeOrder").addToBackStack("fragment_makeOrder").commit();

        }


    }
    @Override
    public void onBackPressed() {
       back();

    }

    public void back() {
        if (behavior.getState()==BottomSheetBehavior.STATE_EXPANDED)
        {
            closeSheet();
        }else
        {
            if (fragment_main != null && fragment_main.isAdded() && fragment_main.isVisible()) {
                if (userModel==null)
                {
                    navigateToSignInActivity();
                }else
                {
                    finish();
                }
            } else {
                DisplayFragmentMain();
            }
        }
    }

    public void navigateToSignInActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void logout() {
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

