package com.creative.share.apps.homecare.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.creative.share.apps.homecare.R;
import com.creative.share.apps.homecare.activities_fragments.activity_home.fragment.Fragment_Main;
import com.creative.share.apps.homecare.databinding.ServiceRowBinding;
import com.creative.share.apps.homecare.models.ServicesDataModel;

import java.util.List;

public class Services_Adapter extends RecyclerView.Adapter<Services_Adapter.Service_Holder> {
    private List<ServicesDataModel.ServiceModel> serviceModelList;
    private Context context;
    private Fragment_Main fragment_main;
    private int[] colors = {R.color.color1, R.color.color2, R.color.color3, R.color.color4, R.color.color5, R.color.color6, R.color.color7, R.color.color8, R.color.color9, R.color.color10};
    private int pos_color = 0;

    public Services_Adapter(List<ServicesDataModel.ServiceModel> serviceModelList, Context context, Fragment_Main fragment_main) {
        this.serviceModelList = serviceModelList;
        this.context = context;
        this.fragment_main = fragment_main;
    }

    @Override
    public Service_Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        ServiceRowBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.service_row, viewGroup, false);
        return new Service_Holder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull final Service_Holder holder, final int i) {
        ServicesDataModel.ServiceModel serviceModel = serviceModelList.get(i);
        holder.binding.setServiceModel(serviceModel);
        holder.binding.image.setColorFilter(ContextCompat.getColor(context, R.color.white));

        if (i < colors.length) {
            pos_color = i;
            holder.binding.image.setBackgroundResource(colors[pos_color]);
            holder.binding.tvTitle.setTextColor(ContextCompat.getColor(context, colors[pos_color]));
            pos_color++;
        } else {
            pos_color = 0;
        }
        holder.itemView.setOnClickListener(v -> {
            ServicesDataModel.ServiceModel model1 = serviceModelList.get(holder.getAdapterPosition());

            int pos = ((holder.getAdapterPosition()+1)%colors.length)-1;
            fragment_main.setItemData(model1,colors[pos]);


        });


    }

    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class Service_Holder extends RecyclerView.ViewHolder {
        private ServiceRowBinding binding;

        public Service_Holder(@NonNull ServiceRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }

    }

}
