package com.taibah.fm_app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.taibah.fm_app.R;
import com.taibah.fm_app.activities_fragments.activity_my_join.MyJoinsActivity;
import com.taibah.fm_app.databinding.JoinRowBinding;
import com.taibah.fm_app.models.MyJoinModel;

import java.util.List;

public class MyJoinsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyJoinModel> list;
    private Context context;
    private LayoutInflater inflater;
    private int [] resource = {R.drawable.circle_color1,R.drawable.circle_color2,R.drawable.circle_color3,R.drawable.circle_color4,R.drawable.circle_color5,R.drawable.circle_color6};
    private MyJoinsActivity activity;
    public MyJoinsAdapter(List<MyJoinModel> list, Context context) {
        this.list = list;
        this.context = context;
        inflater = LayoutInflater.from(context);
        activity = (MyJoinsActivity) context;


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        JoinRowBinding binding = DataBindingUtil.inflate(inflater, R.layout.join_row, parent, false);
        return new MyHolder(binding);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyHolder myHolder = (MyHolder) holder;
        int pos = position%list.size();
        myHolder.binding.fl.setBackgroundResource(resource[pos]);
        myHolder.binding.setModel(list.get(position));
        myHolder.binding.setId(position+1);
        myHolder.binding.tvSell.setOnClickListener(view -> {
            MyJoinModel myJoinModel = list.get(myHolder.getAdapterPosition());
            activity.setItemData(myJoinModel,myHolder.getAdapterPosition());
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        public JoinRowBinding binding;

        public MyHolder(@NonNull JoinRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }




}
