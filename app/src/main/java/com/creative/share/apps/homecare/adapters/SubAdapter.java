package com.creative.share.apps.homecare.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Main;
import com.creative.share.apps.homecare.models.SubServicesModel;
import java.util.List;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private List<SubServicesModel.SubServiceModel> subServiceModels;
    private Fragment_Main fragment_main;
    public String content;


    public SubAdapter(Context ctx, List<SubServicesModel.SubServiceModel> subServiceModels, Fragment_Main fragment_main){

        this.fragment_main=fragment_main;
        inflater = LayoutInflater.from(ctx);
        this.subServiceModels = subServiceModels;
    }

    @Override
    public SubAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.sub_layout_row, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(SubAdapter.MyViewHolder holder, int position) {
        SubServicesModel.SubServiceModel model = subServiceModels.get(position);
       // holder.tvSubTitle.setText(myImageNameList[position]);
        holder.BindData(model);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content=model.getWords().getContent();

                fragment_main.showDetailDialog(content);





            }
        });
    }

    @Override
    public int getItemCount() {
        return subServiceModels.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvSubTitle;


        public MyViewHolder(View itemView) {
            super(itemView);

            tvSubTitle = itemView.findViewById(R.id.tvSubTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
            public void BindData(SubServicesModel.SubServiceModel subservices)
            {
                tvSubTitle.setText(subservices.getWords().getTitle());
            }

        }



}