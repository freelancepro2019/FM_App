package com.taibah.fm_app.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.databinding.DataBindingUtil;

import com.taibah.fm_app.R;
import com.taibah.fm_app.databinding.SpinnerDurationRowBinding;
import com.taibah.fm_app.databinding.SpinnerServiceRowBinding;
import com.taibah.fm_app.models.HomeSessionModel;

import java.util.List;

public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private List<HomeSessionModel.Service> serviceList;
    private LayoutInflater inflate;

    public ServiceAdapter(Context context, List<HomeSessionModel.Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int i) {
        return serviceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder") SpinnerServiceRowBinding binding = DataBindingUtil.inflate(inflate, R.layout.spinner_service_row,viewGroup,false);
        binding.setModel(serviceList.get(i));
        return binding.getRoot();
    }
}
