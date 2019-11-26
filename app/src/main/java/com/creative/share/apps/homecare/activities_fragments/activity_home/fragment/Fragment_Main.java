package com.creative.share.apps.homecare.activities_fragments.activity_home.fragment;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.homecare.adapters.Services_Adapter;
import com.creative.share.apps.homecare.databinding.FragmentMainBinding;
import com.creative.share.apps.homecare.models.ServicesDataModel;
import com.creative.share.apps.homecare.preferences.Preferences;
import com.creative.share.apps.homecare.remote.Api;
import com.creative.share.apps.homecare.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Main extends Fragment {
    private FragmentMainBinding binding;
    private HomeActivity activity;
    private Preferences preferences;
    private String lang;
    private List<ServicesDataModel.ServiceModel> serviceModelList;
    private Services_Adapter adapter;


    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        initView();
        return binding.getRoot();
    }

    private void initView() {
        serviceModelList = new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        binding.progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        binding.recView.setItemViewCacheSize(25);
        binding.recView.setDrawingCacheEnabled(true);
        binding.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recView.setLayoutManager(new GridLayoutManager(activity, 2));
        adapter = new Services_Adapter(serviceModelList, activity, this);
        binding.recView.setAdapter(adapter);

        getData();

    }




    private void getData() {
        Api.getService(Tags.base_url).get_services(lang).enqueue(new Callback<ServicesDataModel>() {
            @Override
            public void onResponse(Call<ServicesDataModel> call, Response<ServicesDataModel> response) {
                binding.progBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getServices().size() > 0) {

                        binding.tvNoService.setVisibility(View.GONE);
                        serviceModelList.clear();
                        serviceModelList.addAll(response.body().getServices());
                        adapter.notifyDataSetChanged();

                    }else
                        {
                            binding.tvNoService.setVisibility(View.VISIBLE);

                        }
                } else {
                    try {

                        Log.e("error", response.code() + "_" + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (response.code() == 500) {
                        Toast.makeText(activity, "Server Error", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();


                    }
                }
            }

            @Override
            public void onFailure(Call<ServicesDataModel> call, Throwable t) {

                try {
                    binding.progBar.setVisibility(View.GONE);
                    if (t.getMessage() != null) {
                        Log.e("error", t.getMessage());
                        if (t.getMessage().toLowerCase().contains("failed to connect") || t.getMessage().toLowerCase().contains("unable to resolve host")) {
                            Toast.makeText(activity, R.string.something, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                }



            }
        });
    }


    public void setItemData(ServicesDataModel.ServiceModel model, int color) {
        activity.openSheet(color,model);
    }




}
