package com.taibah.fm_app.activities_fragments.activity_session_reservations;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_session_reservations.fragments.Fragment_Session_Current_Order;
import com.taibah.fm_app.activities_fragments.activity_session_reservations.fragments.Fragment_Session_Pending_Order;
import com.taibah.fm_app.adapters.ViewPagerAdapter;
import com.taibah.fm_app.databinding.ActivitySessionReservationBinding;
import com.taibah.fm_app.interfaces.Listeners;
import com.taibah.fm_app.language.LanguageHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;

public class SessionReservationActivity extends AppCompatActivity implements Listeners.BackListener{
    private ActivitySessionReservationBinding binding;
    private String lang;
    private ViewPagerAdapter adapter;
    private List<Fragment> fragmentList;
    private List<String> titles;





    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(LanguageHelper.updateResources(newBase, Paper.book().read("lang","ar")));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_session_reservation);
        initView();
    }




    private void initView()
    {
        titles = new ArrayList<>();
        fragmentList = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.setBackListener(this);
        binding.setLang(lang);

        fragmentList.add(Fragment_Session_Pending_Order.newInstance());
        fragmentList.add(Fragment_Session_Current_Order.newInstance());
        
        titles.add(getString(R.string.pending));
        titles.add(getString(R.string.current));


        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragments(fragmentList);
        adapter.AddTitle(titles);
        binding.pager.setAdapter(adapter);
        binding.tab.setupWithViewPager(binding.pager);
        binding.pager.setOffscreenPageLimit(fragmentList.size());

    }



    @Override
    public void back() {
        finish();
    }


}
