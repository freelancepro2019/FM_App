package com.creative.share.apps.homecare.activities_fragments.activity_home.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.HomeActivity;
import com.creative.share.apps.homecare.adapters.Services_Adapter;
import com.creative.share.apps.homecare.adapters.SubAdapter;
import com.creative.share.apps.homecare.databinding.FragmentMainBinding;
import com.creative.share.apps.homecare.models.ServicesDataModel;
import com.creative.share.apps.homecare.models.SubServicesModel;
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

public class Fragment_Main extends Fragment  {
    private HomeActivity activity;
    private Preferences preferences;
    private String curent_language;
    private RecyclerView recView;
    private LinearLayoutManager manager;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progBar;
    private Services_Adapter services_adapter;
    private SubAdapter subAdapter;
    private List<ServicesDataModel.ServiceModel> serviceModelList;
    public static Dialog subdialog,detaildialog;
    private List<SubServicesModel.SubServiceModel> subServiceModelList;



    public static Fragment_Main newInstance() {
        return new Fragment_Main();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initView(view);
        getData();
        return view;
    }

    private void initView(View view) {
        serviceModelList = new ArrayList<>();
        subServiceModelList=new ArrayList<>();
        activity = (HomeActivity) getActivity();
        preferences = Preferences.getInstance();
        Paper.init(activity);
        curent_language = Paper.book().read("lang", Locale.getDefault().getLanguage());
        recView = view.findViewById(R.id.recView);

        progBar = view.findViewById(R.id.progBar);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        recView.setItemViewCacheSize(25);
        recView.setDrawingCacheEnabled(true);
        recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recView.setLayoutManager(new GridLayoutManager(activity, 2));

        services_adapter = new Services_Adapter(serviceModelList, activity,this);
        recView.setAdapter(services_adapter);

    }
    public void setItemData(ServicesDataModel.ServiceModel model) {

    }


    private void getData() {
        Api.getService(Tags.base_url).get_services(curent_language).enqueue(new Callback<ServicesDataModel>() {
            @Override
            public void onResponse(Call<ServicesDataModel> call, Response<ServicesDataModel> response) {
                progBar.setVisibility(View.GONE);
                if (response.isSuccessful()&&response.body()!=null) {
                    if (response.body().getServices().size() > 0) {
                        serviceModelList.clear();
                        serviceModelList.addAll(response.body().getServices());
                        services_adapter.notifyDataSetChanged();

                    }
                } else {
                    try {
                        Log.e("code_error",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServicesDataModel> call, Throwable t) {
                try {
                    swipeRefresh.setRefreshing(false);

                    progBar.setVisibility(View.GONE);
                    Log.e("error message", t.getMessage());
                }catch (Exception e){}


            }
        });
    }

    public void showSubDialog(int main_service_id){
        getSubServices(main_service_id);
        subdialog = new Dialog(activity);
        subdialog.setCancelable(false);
        subdialog.setContentView(R.layout.dialog_recview);

        Button btncancel =  subdialog.findViewById(R.id.btn_cancel);
        btncancel.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {

                subdialog.dismiss();
            }
        });

        RecyclerView recyclerView = subdialog.findViewById(R.id.recView);
        subAdapter = new SubAdapter(activity,subServiceModelList,this);
        recyclerView.setAdapter(subAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });

        subdialog.show();

    }


    public void getSubServices( int main_service_id) {
        Api.getService(Tags.base_url).get_sub_services(curent_language,main_service_id).enqueue(new Callback<SubServicesModel>() {
            @Override
            public void onResponse(Call<SubServicesModel> call, Response<SubServicesModel> response) {
                progBar.setVisibility(View.GONE);
                if (response.isSuccessful()&&response.body()!=null) {
                    if (response.body().getServices().size() > 0) {
                        subServiceModelList.clear();
                        subServiceModelList.addAll(response.body().getServices());
                        subAdapter.notifyDataSetChanged();
                        Log.e("title",subServiceModelList+"");

                    }
                } else {
                    try {
                        Log.e("code_error",response.code()+"_"+response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(activity, getString(R.string.failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SubServicesModel> call, Throwable t) {
                try {
                    swipeRefresh.setRefreshing(false);

                    progBar.setVisibility(View.GONE);
                    Log.e("error message", t.getMessage());
                }catch (Exception e){}


            }
        });
    }

    public void showDetailDialog(String content){
        detaildialog = new Dialog(activity);
        detaildialog.setCancelable(false);
        detaildialog.setContentView(R.layout.dialog_subdetail);
        TextView tvContent=detaildialog.findViewById(R.id.tvContent);
        tvContent.setText(content);
        Button btn_make_order =  detaildialog.findViewById(R.id.btn_make_order);
        Button btn_cancel =  detaildialog.findViewById(R.id.btn_cancel);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detaildialog.dismiss();


            }
        });
        btn_make_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detaildialog.dismiss();
                activity.DisplayFragmentMakeOrder();

            }
        });


        detaildialog.show();

    }




}
