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
import com.taibah.fm_app.models.JoinNowModel;

import java.util.List;

public class DurationAdapter  extends BaseAdapter {
    private Context context;
    private List<JoinNowModel.Duration> durationList;
    private LayoutInflater inflate;

    public DurationAdapter(Context context, List<JoinNowModel.Duration> durationList) {
        this.context = context;
        this.durationList = durationList;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return durationList.size();
    }

    @Override
    public Object getItem(int i) {
        return durationList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder") SpinnerDurationRowBinding binding = DataBindingUtil.inflate(inflate, R.layout.spinner_duration_row,viewGroup,false);
        binding.setModel(durationList.get(i));
        return binding.getRoot();
    }
}
