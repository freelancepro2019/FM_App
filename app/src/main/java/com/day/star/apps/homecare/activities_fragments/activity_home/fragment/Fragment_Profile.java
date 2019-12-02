package com.day.star.apps.homecare.activities_fragments.activity_home.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.day.star.apps.homecare.R;
import com.day.star.apps.homecare.activities_fragments.activity_home.HomeActivity;
import com.day.star.apps.homecare.databinding.FragmentProfileBinding;
import com.day.star.apps.homecare.models.UserModel;
import com.day.star.apps.homecare.preferences.Preferences;
import com.day.star.apps.homecare.tags.Tags;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import io.paperdb.Paper;

public class Fragment_Profile extends Fragment  {
    private FragmentProfileBinding binding;
    private HomeActivity activity;
    private String lang;
    private Preferences preferences;
    private UserModel userModel;

    public static Fragment_Profile newInstance() {
        return new Fragment_Profile();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        View view = binding.getRoot();
        initView();
        return view;
    }

    private void initView() {
        preferences = Preferences.newInstance();
        activity = (HomeActivity) getActivity();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        userModel = preferences.getUserData(activity);
        binding.setLang(lang);
        binding.setUserModel(userModel);

        Picasso.with(activity).load(Uri.parse(Tags.IMAGE_AVATAR+userModel.getLogo())).placeholder(R.drawable.splash).fit().into(binding.image);

        Picasso.with(activity).load(R.drawable.beauty).fit().into(binding.imageBg);




    }









}
