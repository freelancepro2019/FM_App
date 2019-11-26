package com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.fragment_orders;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.homecare.databinding.FragmentPendingCurrentPreviousOrderBinding;
import com.creative.share.apps.homecare.preferences.Preferences;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Provider_Previous_Orders extends Fragment  {
    private FragmentPendingCurrentPreviousOrderBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;

    public static Fragment_Provider_Previous_Orders newInstance() {
        return new Fragment_Provider_Previous_Orders();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_pending_current_previous_order, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity,R.color.colorPrimary), PorterDuff.Mode.SRC_IN);




    }









}
